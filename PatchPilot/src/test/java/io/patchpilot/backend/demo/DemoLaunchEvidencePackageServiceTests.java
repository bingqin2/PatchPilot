package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchEvidencePackageServiceTests {

    @Test
    void should_build_ready_launch_evidence_package_from_launch_readiness_and_session_snapshot() {
        DemoLaunchEvidencePackageService service = new DemoLaunchEvidencePackageService(
                () -> DemoLaunchEvidenceFixtures.launchReadiness(DemoReadinessStatus.READY),
                () -> DemoLaunchEvidenceFixtures.sessionSnapshot(DemoReadinessStatus.READY)
        );

        DemoLaunchEvidencePackageVo evidencePackage = service.getPackage();

        assertThat(evidencePackage.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.readyToShare()).isTrue();
        assertThat(evidencePackage.summary()).isEqualTo("PatchPilot launch evidence package is ready to share.");
        assertThat(evidencePackage.sessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(evidencePackage.launchReadinessStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.evidenceBundleStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.handoffFinalizationStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.finalHandoffReportPackageArchiveStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(evidencePackage.finalHandoffReportPackageArchiveReady()).isTrue();
        assertThat(evidencePackage.finalHandoffReportPackageArchiveId())
                .isEqualTo("final-handoff-report-package-archive-1");
        assertThat(evidencePackage.finalHandoffReportPackageArchiveSummary())
                .isEqualTo("Latest final handoff report package archive is download-ready and ready.");
        assertThat(evidencePackage.latestTaskId()).isEqualTo("task-1");
        assertThat(evidencePackage.latestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(evidencePackage.latestWebhookDeliveryId()).isEqualTo("delivery-1");
        assertThat(evidencePackage.evaluationRunId()).isEqualTo("evaluation-run-2");
        assertThat(evidencePackage.evaluationCoverage()).containsExactly("java", "python", "maven", "pytest");
        assertThat(evidencePackage.preLaunchChecks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly("Demo readiness:READY", "Evidence bundle:READY");
        assertThat(evidencePackage.liveRunProof()).contains(
                "Recent task task-1 reached COMPLETED.",
                "Recent Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is available.",
                "Latest webhook delivery delivery-1 created task task-1."
        );
        assertThat(evidencePackage.postDemoProof()).contains(
                "Handoff finalization is READY.",
                "Latest delivery receipt delivery-receipt-1 is fresh.",
                "Final handoff report package archive final-handoff-report-package-archive-1 is download-ready."
        );
        assertThat(evidencePackage.nextActions()).containsExactly(
                "Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review.",
                "Follow the script from step 1 through Pull Request review.",
                "Use this evidence bundle as the live demo baseline."
        );
        assertThat(evidencePackage.healthContract()).contains(
                "GET /api/demo/launch-evidence-package is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub."
        );
        assertThat(evidencePackage.markdownReport())
                .contains("# PatchPilot Demo Launch Evidence Package")
                .contains("- Status: `READY`")
                .contains("## Pre-Launch Checks")
                .contains("## Live Run Proof")
                .contains("Recent Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is available.")
                .contains("## Evaluation Proof")
                .contains("evaluation-run-2")
                .contains("- Final handoff report package archive: `final-handoff-report-package-archive-1`")
                .contains("- Final handoff report package archive status: `READY`")
                .contains("## Side Effect Contract");
    }

    @Test
    void should_need_attention_when_launch_readiness_or_handoff_finalization_is_not_ready() {
        DemoLaunchEvidencePackageService service = new DemoLaunchEvidencePackageService(
                () -> DemoLaunchEvidenceFixtures.launchReadiness(DemoReadinessStatus.NEEDS_ATTENTION),
                () -> DemoLaunchEvidenceFixtures.sessionSnapshot(DemoReadinessStatus.NEEDS_ATTENTION)
        );

        DemoLaunchEvidencePackageVo evidencePackage = service.getPackage();

        assertThat(evidencePackage.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(evidencePackage.readyToShare()).isFalse();
        assertThat(evidencePackage.summary()).isEqualTo("PatchPilot launch evidence package needs attention before sharing.");
        assertThat(evidencePackage.handoffFinalizationStatus()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(evidencePackage.finalHandoffReportPackageArchiveStatus()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(evidencePackage.finalHandoffReportPackageArchiveReady()).isFalse();
        assertThat(evidencePackage.finalHandoffReportPackageArchiveId()).isNull();
        assertThat(evidencePackage.nextActions()).contains(
                "Resolve launch package warnings, then rerun this readiness package.",
                "Fix demo evidence bundle before launch."
        );
        assertThat(evidencePackage.markdownReport())
                .contains("- Status: `NEEDS_ATTENTION`")
                .contains("Fix demo evidence bundle before launch.");
    }
}
