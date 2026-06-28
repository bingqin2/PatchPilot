package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoLaunchEvidenceFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-28T06:30:00Z"), ZoneOffset.UTC);

    private final DemoLaunchEvidenceShareCenterService shareCenterService = mock(DemoLaunchEvidenceShareCenterService.class);
    private final DemoLaunchEvidenceFinalizationService service =
            new DemoLaunchEvidenceFinalizationService(shareCenterService, CLOCK);

    @Test
    void should_report_ready_when_current_launch_evidence_archive_has_fresh_delivery_receipt() {
        when(shareCenterService.getShareCenter()).thenReturn(freshDeliveredShareCenter());

        DemoLaunchEvidenceFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(finalization.finalized()).isTrue();
        assertThat(finalization.summary())
                .isEqualTo("Demo launch evidence is finalized with a fresh delivery receipt for the current archive.");
        assertThat(finalization.nextAction())
                .isEqualTo("Use the finalization report as the launch evidence delivery acceptance record.");
        assertThat(finalization.latestArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(finalization.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-1");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(finalization.deliveryReceiptFresh()).isTrue();
        assertThat(finalization.generatedAt()).isEqualTo(Instant.parse("2026-06-28T06:30:00Z"));
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Launch evidence share readiness:READY",
                        "Delivery receipt freshness:READY",
                        "Launch acceptance evidence:READY"
                );
        assertThat(finalization.evidenceNotes()).contains(
                "Launch evidence share center status is READY.",
                "Latest delivery receipt launch-delivery-receipt-1 is fresh for launch-evidence-archive-1/demo-session-20260624T003000Z.",
                "Finalization report can be downloaded as the launch delivery acceptance record."
        );
        assertThat(finalization.markdownReport())
                .contains("# PatchPilot Demo Launch Evidence Finalization Gate")
                .contains("- Status: `READY`")
                .contains("- Finalized: `true`")
                .contains("- Latest delivery receipt: `launch-delivery-receipt-1`")
                .contains("GET /api/demo/launch-evidence-finalization is read-only");
    }

    @Test
    void should_need_attention_when_launch_evidence_is_share_ready_but_receipt_is_missing() {
        when(shareCenterService.getShareCenter()).thenReturn(missingReceiptShareCenter());

        DemoLaunchEvidenceFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.summary())
                .isEqualTo("Demo launch evidence package is share-ready but final delivery evidence is not current.");
        assertThat(finalization.nextAction())
                .isEqualTo("Share the current launch evidence package, record a delivery receipt, then download the finalization report.");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Launch evidence share readiness:READY",
                        "Delivery receipt freshness:NEEDS_ATTENTION",
                        "Launch acceptance evidence:NEEDS_ATTENTION"
                );
        assertThat(finalization.markdownReport())
                .contains("- Delivery receipt freshness: `MISSING`")
                .contains("Record a launch evidence delivery receipt after sharing the package.");
    }

    @Test
    void should_need_attention_when_latest_launch_delivery_receipt_is_stale() {
        when(shareCenterService.getShareCenter()).thenReturn(staleReceiptShareCenter());

        DemoLaunchEvidenceFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("launch-delivery-receipt-old");
        assertThat(finalization.nextAction())
                .isEqualTo("Record a new delivery receipt for launch evidence archive launch-evidence-archive-1, then download the finalization report.");
        assertThat(finalization.markdownReport())
                .contains("- Delivery receipt freshness: `STALE`")
                .contains("Latest delivery receipt launch-delivery-receipt-old belongs to old-launch-archive/old-session");
    }

    private static DemoLaunchEvidenceShareCenterVo freshDeliveredShareCenter() {
        return shareCenter(
                "launch-delivery-receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-28T06:05:00Z",
                true,
                "FRESH",
                true,
                "Latest delivery receipt matches the current launch evidence archive and session."
        );
    }

    private static DemoLaunchEvidenceShareCenterVo missingReceiptShareCenter() {
        return shareCenter(
                null,
                null,
                null,
                null,
                false,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current launch evidence package."
        );
    }

    private static DemoLaunchEvidenceShareCenterVo staleReceiptShareCenter() {
        return shareCenter(
                "launch-delivery-receipt-old",
                "Demo reviewer",
                "email",
                "2026-06-28T05:50:00Z",
                true,
                "STALE",
                false,
                "Latest delivery receipt launch-delivery-receipt-old belongs to old-launch-archive/old-session, not current launch-evidence-archive-1/demo-session-20260624T003000Z."
        );
    }

    private static DemoLaunchEvidenceShareCenterVo shareCenter(
            String receiptId,
            String target,
            String channel,
            String deliveredAt,
            boolean recorded,
            String freshness,
            boolean fresh,
            String freshnessSummary
    ) {
        return new DemoLaunchEvidenceShareCenterVo(
                "READY",
                true,
                "Latest archived launch evidence package is READY and can be shared.",
                fresh
                        ? "Download the archived launch evidence package and share it with reviewers."
                        : "Share the current launch evidence package and record a delivery receipt.",
                1,
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-28T02:30:00Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                receiptId,
                target,
                channel,
                deliveredAt,
                recorded,
                freshness,
                fresh,
                freshnessSummary,
                List.of("Download launch evidence package archive launch-evidence-archive-1."),
                List.of(freshnessSummary),
                "# PatchPilot Demo Launch Evidence Share Center",
                Instant.parse("2026-06-28T02:45:00Z")
        );
    }
}
