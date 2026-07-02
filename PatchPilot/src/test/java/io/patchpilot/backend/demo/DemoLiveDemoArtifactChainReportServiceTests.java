package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoArtifactChainReportVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoCompletionCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveDemoArtifactChainReportServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-02T10:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_mark_live_demo_artifact_chain_ready_when_latest_archives_are_consistent() {
        Repositories repositories = readyRepositories();
        DemoLiveDemoArtifactChainReportService service = service(repositories);

        DemoLiveDemoArtifactChainReportVo report = service.getReport();

        assertThat(report.status()).isEqualTo("READY");
        assertThat(report.complete()).isTrue();
        assertThat(report.summary()).isEqualTo("PatchPilot live demo artifact chain is complete and consistent.");
        assertThat(report.launchPackageArchiveId()).isEqualTo("launch-package-archive-1");
        assertThat(report.outcomeCloseoutArchiveId()).isEqualTo("outcome-closeout-archive-1");
        assertThat(report.evidenceBundleArchiveId()).isEqualTo("live-demo-evidence-bundle-archive-1");
        assertThat(report.handoffFinalizationArchiveId())
                .isEqualTo("live-demo-handoff-delivery-finalization-archive-1");
        assertThat(report.completionCertificateArchiveId())
                .isEqualTo("live-demo-completion-certificate-archive-1");
        assertThat(report.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(report.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(report.taskId()).isEqualTo("task-1");
        assertThat(report.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(report.steps())
                .extracting(DemoLiveDemoArtifactChainReportVo.Step::name)
                .containsExactly(
                        "Live trigger launch package archive",
                        "Live trigger outcome closeout archive",
                        "Live demo evidence bundle archive",
                        "Live demo handoff delivery finalization archive",
                        "Live demo completion certificate archive"
                );
        assertThat(report.checks())
                .extracting(DemoLiveDemoArtifactChainReportVo.Check::status)
                .containsOnly("READY");
        assertThat(report.downloadActions()).containsExactly(
                "Download live demo artifact chain report.",
                "Download live trigger launch package archive launch-package-archive-1.",
                "Download live trigger outcome closeout archive outcome-closeout-archive-1.",
                "Download live demo evidence bundle archive live-demo-evidence-bundle-archive-1.",
                "Download live demo handoff delivery finalization archive live-demo-handoff-delivery-finalization-archive-1.",
                "Download live demo completion certificate archive live-demo-completion-certificate-archive-1.",
                "Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review."
        );
        assertThat(report.sideEffectContract())
                .contains("GET /api/demo/live-demo-handoff-package/artifact-chain-report is read-only");
        assertThat(report.markdownReport())
                .contains("# PatchPilot Live Demo Artifact Chain Report")
                .contains("launch-package-archive-1")
                .contains("live-demo-completion-certificate-archive-1");
    }

    @Test
    void should_block_when_required_archive_is_missing() {
        Repositories repositories = new Repositories();
        repositories.launchRepository.save(launchArchive());
        DemoLiveDemoArtifactChainReportService service = service(repositories);

        DemoLiveDemoArtifactChainReportVo report = service.getReport();

        assertThat(report.status()).isEqualTo("BLOCKED");
        assertThat(report.complete()).isFalse();
        assertThat(report.summary()).isEqualTo("PatchPilot live demo artifact chain is missing required archives.");
        assertThat(report.checks())
                .extracting(DemoLiveDemoArtifactChainReportVo.Check::name, DemoLiveDemoArtifactChainReportVo.Check::status)
                .contains(
                        org.assertj.core.groups.Tuple.tuple("Outcome closeout archive present", "BLOCKED"),
                        org.assertj.core.groups.Tuple.tuple("Evidence bundle archive present", "BLOCKED"),
                        org.assertj.core.groups.Tuple.tuple("Completion certificate archive present", "BLOCKED")
                );
        assertThat(report.nextAction())
                .isEqualTo("Create the missing live demo archives before sharing a final demo completion package.");
    }

    @Test
    void should_need_attention_when_archive_chain_is_inconsistent() {
        Repositories repositories = readyRepositories();
        repositories.evidenceRepository.save(evidenceArchive("different-launch-package-archive"));
        DemoLiveDemoArtifactChainReportService service = service(repositories);

        DemoLiveDemoArtifactChainReportVo report = service.getReport();

        assertThat(report.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(report.complete()).isFalse();
        assertThat(report.summary()).isEqualTo("PatchPilot live demo artifact chain has inconsistent archive references.");
        assertThat(report.checks())
                .extracting(DemoLiveDemoArtifactChainReportVo.Check::name, DemoLiveDemoArtifactChainReportVo.Check::status)
                .contains(org.assertj.core.groups.Tuple.tuple("Evidence bundle references launch package", "NEEDS_ATTENTION"));
        assertThat(report.nextAction())
                .isEqualTo("Regenerate and archive the affected live demo artifacts from the latest consistent launch package.");
    }

    private static DemoLiveDemoArtifactChainReportService service(Repositories repositories) {
        return new DemoLiveDemoArtifactChainReportService(
                repositories.launchRepository,
                repositories.closeoutRepository,
                repositories.evidenceRepository,
                repositories.finalizationRepository,
                repositories.completionRepository,
                CLOCK
        );
    }

    private static Repositories readyRepositories() {
        Repositories repositories = new Repositories();
        repositories.launchRepository.save(launchArchive());
        repositories.closeoutRepository.save(closeoutArchive());
        repositories.evidenceRepository.save(evidenceArchive("launch-package-archive-1"));
        repositories.finalizationRepository.save(finalizationArchive());
        repositories.completionRepository.save(completionArchive());
        return repositories;
    }

    private static DemoLiveTriggerLaunchPackageArchiveVo launchArchive() {
        return new DemoLiveTriggerLaunchPackageArchiveVo(
                "launch-package-archive-1",
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-gate.md",
                "Ready to post live trigger.",
                "operator-archive-1",
                true,
                Instant.parse("2026-07-02T00:00:00Z"),
                "READY",
                true,
                List.of("Launch package archive is ready."),
                List.of("Post the tested GitHub issue comment."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T00:00:01Z"),
                Instant.parse("2026-07-02T00:00:05Z"),
                "# Launch package archive"
        );
    }

    private static DemoLiveTriggerOutcomeCloseoutArchiveVo closeoutArchive() {
        return new DemoLiveTriggerOutcomeCloseoutArchiveVo(
                "outcome-closeout-archive-1",
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-gate.md",
                "launch-package-archive-1",
                "READY",
                Instant.parse("2026-07-02T00:00:05Z"),
                "task-1",
                "COMPLETED",
                null,
                Instant.parse("2026-07-02T00:10:00Z"),
                Instant.parse("2026-07-02T00:20:00Z"),
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "DELIVERED",
                "Live trigger completed.",
                List.of("Outcome closeout archive is successful."),
                List.of("Review the Pull Request."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T01:00:00Z"),
                Instant.parse("2026-07-02T01:05:00Z"),
                "# Outcome closeout archive"
        );
    }

    private static DemoLiveDemoEvidenceBundleArchiveVo evidenceArchive(String launchPackageArchiveId) {
        return new DemoLiveDemoEvidenceBundleArchiveVo(
                "live-demo-evidence-bundle-archive-1",
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-gate.md",
                launchPackageArchiveId,
                Instant.parse("2026-07-02T00:00:05Z"),
                "outcome-closeout-archive-1",
                Instant.parse("2026-07-02T01:05:00Z"),
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "Live demo evidence bundle is ready for handoff.",
                List.of("Evidence bundle archive is ready."),
                List.of("Share evidence with reviewer."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T02:00:00Z"),
                Instant.parse("2026-07-02T03:00:00Z"),
                "# Evidence bundle archive"
        );
    }

    private static DemoLiveDemoHandoffDeliveryFinalizationArchiveVo finalizationArchive() {
        return new DemoLiveDemoHandoffDeliveryFinalizationArchiveVo(
                "live-demo-handoff-delivery-finalization-archive-1",
                "READY",
                true,
                "Live demo handoff delivery is finalized.",
                "Use this finalization report as reviewer proof.",
                "live-demo-handoff-delivery-receipt-1",
                "live-demo-evidence-bundle-archive-1",
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "github-comment",
                "2026-07-02T04:55:00Z",
                "FRESH",
                true,
                "Latest live demo handoff delivery receipt matches the current handoff package.",
                List.of(),
                List.of("Delivery finalization archive is ready."),
                List.of("Download live demo handoff delivery finalization archive report."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T06:00:00Z"),
                Instant.parse("2026-07-02T07:00:00Z"),
                "# Finalization archive"
        );
    }

    private static DemoLiveDemoCompletionCertificateArchiveVo completionArchive() {
        return new DemoLiveDemoCompletionCertificateArchiveVo(
                "live-demo-completion-certificate-archive-1",
                "READY",
                true,
                "PatchPilot live demo is certified from the latest handoff finalization archive.",
                "Share the live demo completion certificate with reviewers.",
                "live-demo-handoff-delivery-finalization-archive-1",
                "live-demo-handoff-delivery-receipt-1",
                "live-demo-evidence-bundle-archive-1",
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "github-comment",
                "2026-07-02T04:55:00Z",
                "FRESH",
                Instant.parse("2026-07-02T06:00:00Z"),
                Instant.parse("2026-07-02T07:00:00Z"),
                Instant.parse("2026-07-02T08:00:00Z"),
                Instant.parse("2026-07-02T09:00:00Z"),
                List.of("Download live demo completion certificate."),
                "Archive creation writes only PatchPilot local completion certificate archive records.",
                "# Completion certificate archive"
        );
    }

    private static final class Repositories {
        private final InMemoryDemoLiveTriggerLaunchPackageArchiveRepository launchRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        private final InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository =
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository();
        private final InMemoryDemoLiveDemoEvidenceBundleArchiveRepository evidenceRepository =
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository();
        private final InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository finalizationRepository =
                new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository();
        private final InMemoryDemoLiveDemoCompletionCertificateArchiveRepository completionRepository =
                new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository();
    }

    static final class RepositoriesAccessor {
        private RepositoriesAccessor() {
        }

        static void seedReadyArchives(
                DemoLiveTriggerLaunchPackageArchiveRepository launchRepository,
                DemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository,
                DemoLiveDemoEvidenceBundleArchiveRepository evidenceRepository,
                DemoLiveDemoHandoffDeliveryFinalizationArchiveRepository finalizationRepository,
                DemoLiveDemoCompletionCertificateArchiveRepository completionRepository
        ) {
            launchRepository.save(launchArchive());
            closeoutRepository.save(closeoutArchive());
            evidenceRepository.save(evidenceArchive("launch-package-archive-1"));
            finalizationRepository.save(finalizationArchive());
            completionRepository.save(completionArchive());
        }
    }
}
