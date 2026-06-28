package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoLaunchAcceptanceCloseoutServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-28T07:15:00Z"), ZoneOffset.UTC);

    private final SelfHostedLaunchReadinessService launchReadinessService = mock(SelfHostedLaunchReadinessService.class);
    private final DemoLaunchEvidencePackageService launchEvidencePackageService = mock(DemoLaunchEvidencePackageService.class);
    private final DemoLaunchEvidenceShareCenterService shareCenterService = mock(DemoLaunchEvidenceShareCenterService.class);
    private final DemoLaunchEvidenceFinalizationService finalizationService = mock(DemoLaunchEvidenceFinalizationService.class);
    private final DemoLaunchAcceptanceCloseoutService service = new DemoLaunchAcceptanceCloseoutService(
            launchReadinessService,
            launchEvidencePackageService,
            shareCenterService,
            finalizationService,
            CLOCK
    );

    @Test
    void should_accept_launch_closeout_when_readiness_share_center_and_finalization_are_ready() {
        when(launchReadinessService.getReadinessPackage()).thenReturn(launchReadiness(DemoReadinessStatus.READY));
        when(launchEvidencePackageService.getPackage()).thenReturn(launchEvidencePackage(DemoReadinessStatus.READY));
        when(shareCenterService.getShareCenter()).thenReturn(launchShareCenter(true, true));
        when(finalizationService.getFinalizationGate()).thenReturn(launchFinalization(DemoReadinessStatus.READY, true));

        DemoLaunchAcceptanceCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(closeout.accepted()).isTrue();
        assertThat(closeout.summary()).isEqualTo("PatchPilot launch acceptance closeout is complete.");
        assertThat(closeout.nextAction()).isEqualTo("Use this closeout report as the final self-hosted launch acceptance record.");
        assertThat(closeout.sessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(closeout.latestTaskId()).isEqualTo("task-1");
        assertThat(closeout.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(closeout.latestWebhookDeliveryId()).isEqualTo("delivery-1");
        assertThat(closeout.evaluationRunId()).isEqualTo("evaluation-run-2");
        assertThat(closeout.latestArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(closeout.latestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(closeout.latestDeliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(closeout.latestDeliveryChannel()).isEqualTo("email");
        assertThat(closeout.latestDeliveredAt()).isEqualTo("2026-06-28T06:05:00Z");
        assertThat(closeout.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(closeout.generatedAt()).isEqualTo(Instant.parse("2026-06-28T07:15:00Z"));
        assertThat(closeout.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Self-hosted launch readiness:READY",
                        "Launch evidence package:READY",
                        "Launch evidence share center:READY",
                        "Launch evidence finalization:READY"
                );
        assertThat(closeout.evidenceNotes()).contains(
                "Launch readiness status is READY.",
                "Launch evidence archive launch-evidence-archive-1 is share-ready.",
                "Delivery receipt launch-delivery-receipt-1 is fresh for demo-session-20260624T003000Z."
        );
        assertThat(closeout.downloadActions()).containsExactly(
                "Download self-hosted launch readiness report.",
                "Download launch evidence package report.",
                "Download launch evidence share center report.",
                "Download launch evidence finalization report.",
                "Download launch acceptance closeout report."
        );
        assertThat(closeout.markdownReport())
                .contains("# PatchPilot Launch Acceptance Closeout")
                .contains("- Status: `READY`")
                .contains("- Accepted: `true`")
                .contains("- Pull Request: `https://github.com/bingqin2/PatchPilot/pull/42`")
                .contains("GET /api/demo/launch-acceptance-closeout is read-only");
    }

    @Test
    void should_need_attention_when_final_delivery_receipt_is_missing() {
        when(launchReadinessService.getReadinessPackage()).thenReturn(launchReadiness(DemoReadinessStatus.READY));
        when(launchEvidencePackageService.getPackage()).thenReturn(launchEvidencePackage(DemoReadinessStatus.READY));
        when(shareCenterService.getShareCenter()).thenReturn(launchShareCenter(true, false));
        when(finalizationService.getFinalizationGate()).thenReturn(launchFinalization(DemoReadinessStatus.NEEDS_ATTENTION, false));

        DemoLaunchAcceptanceCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(closeout.accepted()).isFalse();
        assertThat(closeout.summary()).isEqualTo("PatchPilot launch acceptance closeout needs attention.");
        assertThat(closeout.nextAction())
                .isEqualTo("Share the current launch evidence package, record a delivery receipt, then download the finalization report.");
        assertThat(closeout.latestDeliveryReceiptId()).isNull();
        assertThat(closeout.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(closeout.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Self-hosted launch readiness:READY",
                        "Launch evidence package:READY",
                        "Launch evidence share center:NEEDS_ATTENTION",
                        "Launch evidence finalization:NEEDS_ATTENTION"
                );
    }

    @Test
    void should_block_when_pre_launch_readiness_is_blocked() {
        when(launchReadinessService.getReadinessPackage()).thenReturn(launchReadiness(DemoReadinessStatus.BLOCKED));
        when(launchEvidencePackageService.getPackage()).thenReturn(launchEvidencePackage(DemoReadinessStatus.READY));
        when(shareCenterService.getShareCenter()).thenReturn(launchShareCenter(true, true));
        when(finalizationService.getFinalizationGate()).thenReturn(launchFinalization(DemoReadinessStatus.READY, true));

        DemoLaunchAcceptanceCloseoutVo closeout = service.getCloseout();

        assertThat(closeout.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(closeout.accepted()).isFalse();
        assertThat(closeout.nextAction()).isEqualTo("Resolve blocked launch closeout checks before treating the demo as accepted.");
        assertThat(closeout.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Self-hosted launch readiness:BLOCKED",
                        "Launch evidence package:READY",
                        "Launch evidence share center:READY",
                        "Launch evidence finalization:READY"
                );
    }

    private static DemoSelfHostedLaunchReadinessVo launchReadiness(DemoReadinessStatus status) {
        return new DemoSelfHostedLaunchReadinessVo(
                status,
                status == DemoReadinessStatus.READY,
                status == DemoReadinessStatus.READY
                        ? "Self-hosted PatchPilot is ready for a controlled issue-to-PR launch."
                        : "Self-hosted PatchPilot is blocked before launch.",
                List.of(),
                status == DemoReadinessStatus.READY
                        ? List.of("Post the tested /agent fix comment.")
                        : List.of("Resolve blocked launch checks before posting."),
                Instant.parse("2026-06-28T01:00:00Z"),
                "# PatchPilot Self-Hosted Launch Readiness"
        );
    }

    private static DemoLaunchEvidencePackageVo launchEvidencePackage(DemoReadinessStatus status) {
        return new DemoLaunchEvidencePackageVo(
                status,
                status == DemoReadinessStatus.READY,
                status == DemoReadinessStatus.READY
                        ? "PatchPilot launch evidence package is ready to share."
                        : "PatchPilot launch evidence package needs attention before sharing.",
                "demo-session-20260624T003000Z",
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                DemoReadinessStatus.READY,
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                List.of("java", "maven"),
                List.of(),
                List.of("Recent task task-1 reached COMPLETED."),
                List.of("Handoff finalization is READY."),
                List.of("Download final launch evidence."),
                List.of("GET /api/demo/launch-evidence-package is read-only."),
                "# PatchPilot Demo Launch Evidence Package",
                Instant.parse("2026-06-28T02:00:00Z")
        );
    }

    private static DemoLaunchEvidenceShareCenterVo launchShareCenter(boolean shareReady, boolean receiptFresh) {
        return new DemoLaunchEvidenceShareCenterVo(
                shareReady ? "READY" : "NEEDS_ATTENTION",
                shareReady,
                shareReady
                        ? "Latest archived launch evidence package is READY and can be shared."
                        : "Latest archived launch evidence package needs attention before sharing.",
                shareReady
                        ? "Download the archived launch evidence package and share it with reviewers."
                        : "Archive a ready launch evidence package before sharing.",
                1,
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-28T02:30:00Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                receiptFresh ? "launch-delivery-receipt-1" : null,
                receiptFresh ? "reviewer@example.com" : null,
                receiptFresh ? "email" : null,
                receiptFresh ? "2026-06-28T06:05:00Z" : null,
                receiptFresh,
                receiptFresh ? "FRESH" : "MISSING",
                receiptFresh,
                receiptFresh
                        ? "Latest delivery receipt matches the current launch evidence archive and session."
                        : "No delivery receipt has been recorded for the current launch evidence package.",
                List.of("Download launch evidence package archive launch-evidence-archive-1."),
                List.of("Latest launch evidence archive status is READY."),
                "# PatchPilot Demo Launch Evidence Share Center",
                Instant.parse("2026-06-28T02:45:00Z")
        );
    }

    private static DemoLaunchEvidenceFinalizationVo launchFinalization(DemoReadinessStatus status, boolean finalized) {
        return new DemoLaunchEvidenceFinalizationVo(
                status,
                finalized,
                finalized
                        ? "Demo launch evidence is finalized with a fresh delivery receipt for the current archive."
                        : "Demo launch evidence package is share-ready but final delivery evidence is not current.",
                finalized
                        ? "Use the finalization report as the launch evidence delivery acceptance record."
                        : "Share the current launch evidence package, record a delivery receipt, then download the finalization report.",
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                finalized ? "launch-delivery-receipt-1" : null,
                finalized ? "reviewer@example.com" : null,
                finalized ? "email" : null,
                finalized ? "2026-06-28T06:05:00Z" : null,
                finalized ? "FRESH" : "MISSING",
                finalized,
                finalized
                        ? "Latest delivery receipt matches the current launch evidence archive and session."
                        : "No delivery receipt has been recorded for the current launch evidence package.",
                List.of(),
                List.of("Launch evidence share center status is READY."),
                "# PatchPilot Demo Launch Evidence Finalization Gate",
                Instant.parse("2026-06-28T06:30:00Z")
        );
    }
}
