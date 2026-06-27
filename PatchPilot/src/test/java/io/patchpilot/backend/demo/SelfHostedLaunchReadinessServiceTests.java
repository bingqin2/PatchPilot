package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoAdapterFixtureEvidenceVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleSummaryVo;
import io.patchpilot.backend.demo.domain.DemoEvidenceBundleVo;
import io.patchpilot.backend.demo.domain.DemoReadinessCheckVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStatus;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistStepVo;
import io.patchpilot.backend.demo.domain.DemoSmokeChecklistVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelfHostedLaunchReadinessServiceTests {

    @Test
    void should_mark_launch_package_ready_when_readiness_evidence_and_finalization_are_ready() {
        SelfHostedLaunchReadinessService service = new SelfHostedLaunchReadinessService(
                () -> readiness(DemoReadinessStatus.READY, List.of()),
                () -> evidenceBundle(DemoReadinessStatus.READY, DemoReadinessStatus.READY, true, List.of())
        );

        DemoSelfHostedLaunchReadinessVo readiness = service.getReadinessPackage();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(readiness.readyToLaunch()).isTrue();
        assertThat(readiness.summary()).isEqualTo("Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.");
        assertThat(readiness.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Demo readiness:READY",
                        "Evidence bundle:READY",
                        "Handoff finalization:READY",
                        "Credentials and secrets:READY",
                        "Webhook setup:READY",
                        "Queue and worker:READY"
                );
        assertThat(readiness.nextActions()).containsExactly(
                "Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review."
        );
        assertThat(readiness.markdownReport())
                .contains("# PatchPilot Self-Hosted Launch Readiness")
                .contains("- Status: `READY`")
                .contains("- Ready to launch: `true`")
                .contains("Post the tested /agent fix comment");
    }

    @Test
    void should_need_attention_when_final_handoff_delivery_evidence_is_missing() {
        SelfHostedLaunchReadinessService service = new SelfHostedLaunchReadinessService(
                () -> readiness(DemoReadinessStatus.READY, List.of()),
                () -> evidenceBundle(
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        DemoReadinessStatus.NEEDS_ATTENTION,
                        false,
                        List.of("Send the current handoff package, record a delivery receipt, then download the finalization report.")
                )
        );

        DemoSelfHostedLaunchReadinessVo readiness = service.getReadinessPackage();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(readiness.readyToLaunch()).isFalse();
        assertThat(readiness.summary()).isEqualTo("Self-hosted PatchPilot needs attention before launch.");
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Handoff finalization"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
                    assertThat(check.message()).contains("final delivery evidence is not current");
                    assertThat(check.action()).contains("record a delivery receipt");
                });
        assertThat(readiness.nextActions()).containsExactly(
                "Send the current handoff package, record a delivery receipt, then download the finalization report.",
                "Resolve launch package warnings, then rerun this readiness package."
        );
    }

    @Test
    void should_block_launch_when_demo_readiness_is_blocked() {
        SelfHostedLaunchReadinessService service = new SelfHostedLaunchReadinessService(
                () -> readiness(
                        DemoReadinessStatus.BLOCKED,
                        List.of("Configure missing credentials in .env and restart the backend.")
                ),
                () -> evidenceBundle(DemoReadinessStatus.READY, DemoReadinessStatus.READY, true, List.of())
        );

        DemoSelfHostedLaunchReadinessVo readiness = service.getReadinessPackage();

        assertThat(readiness.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(readiness.readyToLaunch()).isFalse();
        assertThat(readiness.summary()).isEqualTo("Self-hosted PatchPilot is blocked before launch.");
        assertThat(readiness.checks())
                .filteredOn(check -> check.name().equals("Demo readiness"))
                .singleElement()
                .satisfies(check -> {
                    assertThat(check.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
                    assertThat(check.action()).isEqualTo("Configure missing credentials in .env and restart the backend.");
                });
        assertThat(readiness.nextActions()).containsExactly(
                "Configure missing credentials in .env and restart the backend.",
                "Resolve blocked launch checks before posting a live /agent fix trigger."
        );
    }

    private static DemoReadinessVo readiness(DemoReadinessStatus status, List<String> nextActions) {
        return new DemoReadinessVo(
                status,
                status == DemoReadinessStatus.READY
                        ? "PatchPilot is ready for a controlled demo."
                        : "PatchPilot is not ready for a controlled demo.",
                List.of(
                        new DemoReadinessCheckVo("Credentials", status, "Credential gate mirrors demo readiness.", firstAction(nextActions)),
                        new DemoReadinessCheckVo("Webhook setup", DemoReadinessStatus.READY, "Webhook setup is ready.", "No action needed."),
                        new DemoReadinessCheckVo("Queue", DemoReadinessStatus.READY, "Queue is healthy.", "No action needed.")
                ),
                nextActions
        );
    }

    private static DemoEvidenceBundleVo evidenceBundle(
            DemoReadinessStatus status,
            DemoReadinessStatus finalizationStatus,
            boolean finalized,
            List<String> nextActions
    ) {
        DemoReadinessVo readiness = readiness(DemoReadinessStatus.READY, List.of());
        DemoSmokeChecklistVo smokeChecklist = new DemoSmokeChecklistVo(
                DemoSmokeChecklistStatus.READY,
                "Live demo smoke checklist is ready.",
                List.of(new DemoSmokeChecklistStepVo(1, "Readiness gate", DemoSmokeChecklistStatus.READY, "Ready.", "Evidence", "No action needed.")),
                List.of()
        );
        return new DemoEvidenceBundleVo(
                status,
                status == DemoReadinessStatus.READY ? "Demo evidence bundle is ready." : "Demo evidence bundle needs attention.",
                new DemoEvidenceBundleSummaryVo(1, 0, 1, 0, true),
                readiness,
                smokeChecklist,
                null,
                new DemoAdapterFixtureEvidenceVo(1, 0),
                FixTaskQueueSummaryVo.empty(),
                null,
                "https://github.com/bingqin2/PatchPilot/pull/42",
                null,
                null,
                List.of(),
                null,
                0,
                DemoReadinessStatus.READY,
                "Latest handoff archive is ready to share.",
                "Share the latest handoff package summary and archived package with the reviewer.",
                DemoReadinessStatus.READY,
                "Post-demo handoff package is ready to share.",
                "Download the package, send the prepared handoff message, then record a delivery receipt.",
                List.of("Download handoff package archive."),
                finalized,
                finalized ? "receipt-1" : null,
                finalized ? "Demo reviewer" : null,
                finalized ? "email" : null,
                finalized ? "2026-06-27T01:00:00Z" : null,
                finalized ? "FRESH" : "MISSING",
                finalized,
                finalized
                        ? "Latest delivery receipt matches the current handoff archive and session."
                        : "No delivery receipt has been recorded for the current handoff package.",
                finalizationStatus,
                finalized,
                finalized
                        ? "Demo handoff is finalized with a fresh delivery receipt for the current archive."
                        : "Demo handoff package is send-ready but final delivery evidence is not current.",
                finalized
                        ? "Use the finalization report as the post-demo delivery acceptance record."
                        : "Send the current handoff package, record a delivery receipt, then download the finalization report.",
                finalized ? "FRESH" : "MISSING",
                finalized,
                finalized ? "receipt-1" : null,
                Instant.parse("2026-06-27T01:00:00Z"),
                nextActions
        );
    }

    private static String firstAction(List<String> nextActions) {
        return nextActions.isEmpty() ? "No action needed." : nextActions.get(0);
    }
}
