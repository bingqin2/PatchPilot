package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveDemoReviewerDeliveryCenterServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-02T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_create_ready_reviewer_delivery_center_from_complete_live_demo_evidence() {
        ReadyFixture fixture = readyFixture();
        DemoLiveDemoReviewerDeliveryCenterService service = new DemoLiveDemoReviewerDeliveryCenterService(
                fixture.handoffPackageService()::createPackage,
                fixture.artifactChainReportService()::getReport,
                fixture.completionCertificateService()::getCertificate,
                fixture.replayPackageService()::getPackage,
                CLOCK
        );

        DemoLiveDemoReviewerDeliveryCenterVo center = service.getCenter();

        assertThat(center.status()).isEqualTo("READY");
        assertThat(center.deliverable()).isTrue();
        assertThat(center.summary()).isEqualTo("PatchPilot live demo reviewer delivery center is ready.");
        assertThat(center.nextAction()).isEqualTo("Send the replay package and final evidence links to reviewers.");
        assertThat(center.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(center.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(center.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(center.readinessCards())
                .extracting(DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard::name)
                .containsExactly(
                        "Reviewer handoff package",
                        "Artifact chain",
                        "Completion certificate",
                        "Replay package"
                );
        assertThat(center.readinessCards())
                .extracting(DemoLiveDemoReviewerDeliveryCenterVo.ReadinessCard::status)
                .containsOnly("READY");
        assertThat(center.blockers()).isEmpty();
        assertThat(center.evidenceLinks())
                .extracting(DemoLiveDemoReviewerDeliveryCenterVo.EvidenceLink::label)
                .contains(
                        "GitHub issue",
                        "Generated Pull Request",
                        "Reviewer handoff package",
                        "Artifact chain report",
                        "Completion certificate",
                        "Replay package"
                );
        assertThat(center.downloadActions()).contains(
                "Download live demo reviewer delivery center.",
                "Download live demo replay package.",
                "Download live demo artifact chain report.",
                "Download live demo completion certificate."
        );
        assertThat(center.sideEffectContract())
                .contains("GET /api/demo/live-demo-handoff-package/reviewer-delivery-center is read-only");
        assertThat(center.generatedAt()).isEqualTo(Instant.parse("2026-07-02T12:00:00Z"));
        assertThat(center.markdownReport())
                .contains("# PatchPilot Live Demo Reviewer Delivery Center")
                .contains("PatchPilot live demo reviewer delivery center is ready.")
                .contains("https://github.com/bingqin2/PatchPilot/pull/42");
    }

    @Test
    void should_block_reviewer_delivery_center_when_required_evidence_is_missing() {
        InMemoryDemoLiveDemoEvidenceBundleArchiveRepository evidenceRepository =
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository();
        DemoLiveDemoHandoffPackageService handoffPackageService =
                new DemoLiveDemoHandoffPackageService(
                        evidenceRepository,
                        () -> Instant.parse("2026-07-02T04:00:00Z")
                );
        DemoLiveDemoArtifactChainReportService artifactChainReportService =
                new DemoLiveDemoArtifactChainReportService(
                        new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository(),
                        new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository(),
                        evidenceRepository,
                        new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                        new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-07-02T10:00:00Z"), ZoneOffset.UTC)
                );
        DemoLiveDemoCompletionCertificateService completionCertificateService =
                new DemoLiveDemoCompletionCertificateService(
                        new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                        Clock.fixed(Instant.parse("2026-07-02T08:00:00Z"), ZoneOffset.UTC)
                );
        DemoLiveDemoReplayPackageService replayPackageService =
                new DemoLiveDemoReplayPackageService(
                        artifactChainReportService,
                        Clock.fixed(Instant.parse("2026-07-02T11:00:00Z"), ZoneOffset.UTC)
                );
        DemoLiveDemoReviewerDeliveryCenterService service = new DemoLiveDemoReviewerDeliveryCenterService(
                handoffPackageService::createPackage,
                artifactChainReportService::getReport,
                completionCertificateService::getCertificate,
                replayPackageService::getPackage,
                CLOCK
        );

        DemoLiveDemoReviewerDeliveryCenterVo center = service.getCenter();

        assertThat(center.status()).isEqualTo("BLOCKED");
        assertThat(center.deliverable()).isFalse();
        assertThat(center.summary()).isEqualTo("PatchPilot live demo reviewer delivery center is blocked.");
        assertThat(center.blockers())
                .contains(
                        "Reviewer handoff package is BLOCKED: PatchPilot is missing a live demo evidence bundle archive for reviewer handoff.",
                        "Artifact chain is BLOCKED: PatchPilot live demo artifact chain is missing required archives."
                );
        assertThat(center.nextAction()).isEqualTo(center.blockers().get(0));
        assertThat(center.markdownReport()).contains("## Blockers");
    }

    private static ReadyFixture readyFixture() {
        InMemoryDemoLiveTriggerLaunchPackageArchiveRepository launchRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository =
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository();
        InMemoryDemoLiveDemoEvidenceBundleArchiveRepository evidenceRepository =
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository();
        InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository finalizationRepository =
                new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository();
        InMemoryDemoLiveDemoCompletionCertificateArchiveRepository completionRepository =
                new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository();
        DemoLiveDemoArtifactChainReportServiceTests.RepositoriesAccessor.seedReadyArchives(
                launchRepository,
                closeoutRepository,
                evidenceRepository,
                finalizationRepository,
                completionRepository
        );
        DemoLiveDemoHandoffPackageService handoffPackageService =
                new DemoLiveDemoHandoffPackageService(
                        evidenceRepository,
                        () -> Instant.parse("2026-07-02T04:00:00Z")
                );
        DemoLiveDemoArtifactChainReportService artifactChainReportService =
                new DemoLiveDemoArtifactChainReportService(
                        launchRepository,
                        closeoutRepository,
                        evidenceRepository,
                        finalizationRepository,
                        completionRepository,
                        Clock.fixed(Instant.parse("2026-07-02T10:00:00Z"), ZoneOffset.UTC)
                );
        DemoLiveDemoCompletionCertificateService completionCertificateService =
                new DemoLiveDemoCompletionCertificateService(
                        finalizationRepository,
                        Clock.fixed(Instant.parse("2026-07-02T08:00:00Z"), ZoneOffset.UTC)
                );
        DemoLiveDemoReplayPackageService replayPackageService =
                new DemoLiveDemoReplayPackageService(
                        artifactChainReportService,
                        Clock.fixed(Instant.parse("2026-07-02T11:00:00Z"), ZoneOffset.UTC)
                );
        return new ReadyFixture(
                handoffPackageService,
                artifactChainReportService,
                completionCertificateService,
                replayPackageService
        );
    }

    private record ReadyFixture(
            DemoLiveDemoHandoffPackageService handoffPackageService,
            DemoLiveDemoArtifactChainReportService artifactChainReportService,
            DemoLiveDemoCompletionCertificateService completionCertificateService,
            DemoLiveDemoReplayPackageService replayPackageService
    ) {
    }
}
