package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveDemoHandoffPackageServiceTests {

    @Test
    void should_create_ready_handoff_package_from_latest_ready_evidence_bundle_archive() {
        InMemoryDemoLiveDemoEvidenceBundleArchiveRepository repository =
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository();
        repository.save(readyEvidenceBundleArchive("live-demo-evidence-bundle-archive-1"));
        DemoLiveDemoHandoffPackageService service = new DemoLiveDemoHandoffPackageService(
                repository,
                () -> Instant.parse("2026-07-02T04:00:00Z")
        );

        DemoLiveDemoHandoffPackageVo handoffPackage = service.createPackage();

        assertThat(handoffPackage.status()).isEqualTo("READY");
        assertThat(handoffPackage.readyForReview()).isTrue();
        assertThat(handoffPackage.evidenceBundleArchiveId()).isEqualTo("live-demo-evidence-bundle-archive-1");
        assertThat(handoffPackage.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(handoffPackage.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(handoffPackage.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(handoffPackage.taskId()).isEqualTo("task-1");
        assertThat(handoffPackage.summary()).contains("ready for reviewer handoff");
        assertThat(handoffPackage.reviewChecklist()).containsExactly(
                "Open the Pull Request and review the files changed.",
                "Confirm the evidence bundle archive live-demo-evidence-bundle-archive-1 matches the issue and task.",
                "Merge or close the Pull Request according to repository policy."
        );
        assertThat(handoffPackage.deliveryInstructions()).contains(
                "Share this handoff package and archived evidence report with the reviewer."
        );
        assertThat(handoffPackage.sideEffectContract()).contains("read-only live demo handoff package");
        assertThat(handoffPackage.generatedAt()).isEqualTo(Instant.parse("2026-07-02T04:00:00Z"));
        assertThat(handoffPackage.markdownReport()).contains("# PatchPilot Live Demo Handoff Package");
        assertThat(handoffPackage.markdownReport()).contains("live-demo-evidence-bundle-archive-1");
        assertThat(handoffPackage.markdownReport()).contains("https://github.com/bingqin2/PatchPilot/pull/42");
    }

    @Test
    void should_block_without_evidence_bundle_archive() {
        DemoLiveDemoHandoffPackageService service = new DemoLiveDemoHandoffPackageService(
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository(),
                () -> Instant.parse("2026-07-02T04:00:00Z")
        );

        DemoLiveDemoHandoffPackageVo handoffPackage = service.createPackage();

        assertThat(handoffPackage.status()).isEqualTo("BLOCKED");
        assertThat(handoffPackage.readyForReview()).isFalse();
        assertThat(handoffPackage.summary()).contains("missing a live demo evidence bundle archive");
        assertThat(handoffPackage.reviewChecklist()).isEmpty();
        assertThat(handoffPackage.deliveryInstructions()).contains(
                "Archive a live demo evidence bundle before preparing the final reviewer handoff package."
        );
        assertThat(handoffPackage.markdownReport()).contains("PatchPilot Live Demo Handoff Package");
    }

    @Test
    void should_need_attention_when_latest_archive_is_not_ready() {
        InMemoryDemoLiveDemoEvidenceBundleArchiveRepository repository =
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository();
        repository.save(new DemoLiveDemoEvidenceBundleArchiveVo(
                "live-demo-evidence-bundle-archive-1",
                "NEEDS_ATTENTION",
                false,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                "launch-package-archive-1",
                Instant.parse("2026-07-02T00:00:05Z"),
                "outcome-closeout-archive-1",
                Instant.parse("2026-07-02T01:05:00Z"),
                "task-1",
                "FAILED",
                null,
                "delivery-1",
                "Live demo evidence bundle needs attention.",
                List.of("Outcome closeout archive outcome-closeout-archive-1 is not successful."),
                List.of("Resolve the outcome closeout failure."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T02:00:00Z"),
                Instant.parse("2026-07-02T03:00:00Z"),
                "# PatchPilot Live Demo Evidence Bundle Archive"
        ));
        DemoLiveDemoHandoffPackageService service = new DemoLiveDemoHandoffPackageService(
                repository,
                () -> Instant.parse("2026-07-02T04:00:00Z")
        );

        DemoLiveDemoHandoffPackageVo handoffPackage = service.createPackage();

        assertThat(handoffPackage.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(handoffPackage.readyForReview()).isFalse();
        assertThat(handoffPackage.summary()).contains("latest evidence bundle archive is not ready");
        assertThat(handoffPackage.deliveryInstructions()).contains(
                "Resolve the evidence bundle archive gaps before sending this handoff package."
        );
    }

    static DemoLiveDemoEvidenceBundleArchiveVo readyEvidenceBundleArchive(String id) {
        return new DemoLiveDemoEvidenceBundleArchiveVo(
                id,
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                "launch-package-archive-1",
                Instant.parse("2026-07-02T00:00:05Z"),
                "outcome-closeout-archive-1",
                Instant.parse("2026-07-02T01:05:00Z"),
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "Live demo evidence bundle is ready for handoff.",
                List.of("Launch package archive launch-package-archive-1 is ready."),
                List.of("Review and merge https://github.com/bingqin2/PatchPilot/pull/42."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T02:00:00Z"),
                Instant.parse("2026-07-02T03:00:00Z"),
                "# PatchPilot Live Demo Evidence Bundle Archive"
        );
    }
}
