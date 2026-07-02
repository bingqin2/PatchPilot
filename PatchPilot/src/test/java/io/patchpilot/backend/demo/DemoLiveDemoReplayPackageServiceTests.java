package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReplayPackageVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveDemoReplayPackageServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-02T11:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_create_ready_replay_package_from_complete_artifact_chain() {
        Repositories repositories = readyRepositories();
        DemoLiveDemoReplayPackageService service = service(repositories);

        DemoLiveDemoReplayPackageVo replayPackage = service.getPackage();

        assertThat(replayPackage.status()).isEqualTo("READY");
        assertThat(replayPackage.replayReady()).isTrue();
        assertThat(replayPackage.summary()).isEqualTo("PatchPilot live demo replay package is ready for reviewer walkthrough.");
        assertThat(replayPackage.artifactChainStatus()).isEqualTo("READY");
        assertThat(replayPackage.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(replayPackage.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(replayPackage.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(replayPackage.sections())
                .extracting(DemoLiveDemoReplayPackageVo.Section::name)
                .containsExactly(
                        "Open the live demo issue",
                        "Inspect the generated Pull Request",
                        "Review the artifact chain",
                        "Download final evidence",
                        "Confirm read-only replay boundary"
                );
        assertThat(replayPackage.sections())
                .extracting(DemoLiveDemoReplayPackageVo.Section::status)
                .containsOnly("READY");
        assertThat(replayPackage.evidenceLinks())
                .extracting(DemoLiveDemoReplayPackageVo.EvidenceLink::label)
                .contains(
                        "GitHub issue",
                        "Generated Pull Request",
                        "Artifact chain report",
                        "Completion certificate archive"
                );
        assertThat(replayPackage.replaySteps()).containsExactly(
                "Open the GitHub issue and confirm the original /agent trigger comment.",
                "Open the generated Pull Request and inspect the changed files.",
                "Download the artifact chain report and verify all archive ids are consistent.",
                "Download the completion certificate archive as the final demo certificate.",
                "Use the read-only replay package as reviewer-facing evidence without rerunning the agent."
        );
        assertThat(replayPackage.downloadActions()).contains(
                "Download live demo replay package.",
                "Download live demo artifact chain report.",
                "Download live demo completion certificate archive live-demo-completion-certificate-archive-1."
        );
        assertThat(replayPackage.sideEffectContract())
                .contains("GET /api/demo/live-demo-handoff-package/replay-package is read-only");
        assertThat(replayPackage.generatedAt()).isEqualTo(Instant.parse("2026-07-02T11:00:00Z"));
        assertThat(replayPackage.markdownReport())
                .contains("# PatchPilot Live Demo Replay Package")
                .contains("live-demo-completion-certificate-archive-1")
                .contains("https://github.com/bingqin2/PatchPilot/pull/42");
    }

    @Test
    void should_block_replay_package_when_artifact_chain_is_incomplete() {
        Repositories repositories = new Repositories();
        DemoLiveDemoArtifactChainReportServiceTests.RepositoriesAccessor.seedLaunchArchive(
                repositories.launchRepository
        );
        DemoLiveDemoReplayPackageService service = service(repositories);

        DemoLiveDemoReplayPackageVo replayPackage = service.getPackage();

        assertThat(replayPackage.status()).isEqualTo("BLOCKED");
        assertThat(replayPackage.replayReady()).isFalse();
        assertThat(replayPackage.summary()).isEqualTo("PatchPilot live demo replay package is blocked by an incomplete artifact chain.");
        assertThat(replayPackage.nextAction())
                .isEqualTo("Create the missing live demo archives before sharing the replay package.");
        assertThat(replayPackage.sections())
                .extracting(DemoLiveDemoReplayPackageVo.Section::status)
                .contains("BLOCKED");
    }

    private static DemoLiveDemoReplayPackageService service(Repositories repositories) {
        DemoLiveDemoArtifactChainReportService artifactChainReportService = new DemoLiveDemoArtifactChainReportService(
                repositories.launchRepository,
                repositories.closeoutRepository,
                repositories.evidenceRepository,
                repositories.finalizationRepository,
                repositories.completionRepository,
                Clock.fixed(Instant.parse("2026-07-02T10:00:00Z"), ZoneOffset.UTC)
        );
        return new DemoLiveDemoReplayPackageService(artifactChainReportService, CLOCK);
    }

    private static Repositories readyRepositories() {
        Repositories repositories = new Repositories();
        DemoLiveDemoArtifactChainReportServiceTests.RepositoriesAccessor.seedReadyArchives(
                repositories.launchRepository,
                repositories.closeoutRepository,
                repositories.evidenceRepository,
                repositories.finalizationRepository,
                repositories.completionRepository
        );
        return repositories;
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
}
