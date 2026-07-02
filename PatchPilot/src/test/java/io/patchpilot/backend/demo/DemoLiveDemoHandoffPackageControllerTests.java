package io.patchpilot.backend.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DemoLiveDemoHandoffPackageControllerTests {

    @Test
    void should_return_admin_protected_live_demo_handoff_package_without_exposing_token() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyForReview").value(true))
                .andExpect(jsonPath("$.data.evidenceBundleArchiveId").value("live-demo-evidence-bundle-archive-1"))
                .andExpect(jsonPath("$.data.pullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.reviewChecklist[0]").value("Open the Pull Request and review the files changed."))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("PatchPilot Live Demo Handoff Package")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("test-admin-token"))));
    }

    @Test
    void should_download_live_demo_handoff_package_report() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/report/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("patchpilot-live-demo-handoff-package.md")))
                .andExpect(content().string(containsString("# PatchPilot Live Demo Handoff Package")))
                .andExpect(content().string(containsString("live-demo-evidence-bundle-archive-1")))
                .andExpect(content().string(containsString("https://github.com/bingqin2/PatchPilot/pull/42")));
    }

    @Test
    void should_return_and_download_live_demo_artifact_chain_report() throws Exception {
        MockMvc mockMvc = mockMvcWithArtifactChain();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/artifact-chain-report")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.complete").value(true))
                .andExpect(jsonPath("$.data.launchPackageArchiveId").value("launch-package-archive-1"))
                .andExpect(jsonPath("$.data.outcomeCloseoutArchiveId").value("outcome-closeout-archive-1"))
                .andExpect(jsonPath("$.data.evidenceBundleArchiveId").value("live-demo-evidence-bundle-archive-1"))
                .andExpect(jsonPath("$.data.handoffFinalizationArchiveId")
                        .value("live-demo-handoff-delivery-finalization-archive-1"))
                .andExpect(jsonPath("$.data.completionCertificateArchiveId")
                        .value("live-demo-completion-certificate-archive-1"))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("PatchPilot Live Demo Artifact Chain Report")));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/artifact-chain-report/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CONTENT_DISPOSITION,
                        containsString("patchpilot-live-demo-artifact-chain-report.md")
                ))
                .andExpect(content().string(containsString("# PatchPilot Live Demo Artifact Chain Report")))
                .andExpect(content().string(containsString("live-demo-completion-certificate-archive-1")));
    }

    @Test
    void should_return_and_download_live_demo_replay_package() throws Exception {
        MockMvc mockMvc = mockMvcWithArtifactChain();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/replay-package")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.replayReady").value(true))
                .andExpect(jsonPath("$.data.artifactChainStatus").value("READY"))
                .andExpect(jsonPath("$.data.completionCertificateArchiveId")
                        .value("live-demo-completion-certificate-archive-1"))
                .andExpect(jsonPath("$.data.sections[0].name").value("Open the live demo issue"))
                .andExpect(jsonPath("$.data.evidenceLinks[1].url")
                        .value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("PatchPilot Live Demo Replay Package")));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/replay-package/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CONTENT_DISPOSITION,
                        containsString("patchpilot-live-demo-replay-package.md")
                ))
                .andExpect(content().string(containsString("# PatchPilot Live Demo Replay Package")))
                .andExpect(content().string(containsString("live-demo-completion-certificate-archive-1")))
                .andExpect(content().string(containsString("https://github.com/bingqin2/PatchPilot/pull/42")));
    }

    @Test
    void should_return_and_download_live_demo_reviewer_delivery_center() throws Exception {
        MockMvc mockMvc = mockMvcWithArtifactChain();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/reviewer-delivery-center")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.deliverable").value(true))
                .andExpect(jsonPath("$.data.readinessCards[0].name").value("Reviewer handoff package"))
                .andExpect(jsonPath("$.data.readinessCards[3].name").value("Replay package"))
                .andExpect(jsonPath("$.data.evidenceLinks[1].url")
                        .value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.markdownReport")
                        .value(containsString("PatchPilot Live Demo Reviewer Delivery Center")));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/reviewer-delivery-center/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CONTENT_DISPOSITION,
                        containsString("patchpilot-live-demo-reviewer-delivery-center.md")
                ))
                .andExpect(content().string(containsString("# PatchPilot Live Demo Reviewer Delivery Center")))
                .andExpect(content().string(containsString("live-demo-completion-certificate-archive-1")))
                .andExpect(content().string(containsString("https://github.com/bingqin2/PatchPilot/pull/42")));
    }

    @Test
    void should_require_admin_token_for_live_demo_handoff_package() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    private static MockMvc mockMvc() {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        InMemoryDemoLiveDemoEvidenceBundleArchiveRepository repository =
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository();
        repository.save(DemoLiveDemoHandoffPackageServiceTests.readyEvidenceBundleArchive(
                "live-demo-evidence-bundle-archive-1"
        ));
        DemoLiveDemoHandoffPackageService service = new DemoLiveDemoHandoffPackageService(
                repository,
                () -> Instant.parse("2026-07-02T04:00:00Z")
        );
        DemoLiveDemoHandoffDeliveryReceiptService receiptService = new DemoLiveDemoHandoffDeliveryReceiptService(
                service::createPackage,
                new InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository(),
                () -> "live-demo-handoff-delivery-receipt-1",
                () -> Instant.parse("2026-07-02T05:00:00Z")
        );
        DemoLiveDemoHandoffDeliveryFinalizationService finalizationService =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        service::createPackage,
                        List::of,
                        java.time.Clock.systemUTC()
                );
        DemoLiveDemoHandoffDeliveryFinalizationArchiveService archiveService =
                new DemoLiveDemoHandoffDeliveryFinalizationArchiveService(
                        finalizationService,
                        new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository()
                );
        DemoLiveDemoCompletionCertificateService completionCertificateService =
                new DemoLiveDemoCompletionCertificateService(
                        archiveService::listRecentArchives,
                        java.time.Clock.systemUTC()
                );
        DemoLiveDemoCompletionCertificateArchiveService completionCertificateArchiveService =
                new DemoLiveDemoCompletionCertificateArchiveService(
                        completionCertificateService,
                        new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository()
                );
        DemoLiveDemoArtifactChainReportService artifactChainReportService =
                new DemoLiveDemoArtifactChainReportService(
                        new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository(),
                        new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository(),
                        repository,
                        new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                        new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository()
                );
        DemoLiveDemoReplayPackageService replayPackageService =
                new DemoLiveDemoReplayPackageService(artifactChainReportService);
        DemoLiveDemoReviewerDeliveryCenterService reviewerDeliveryCenterService =
                new DemoLiveDemoReviewerDeliveryCenterService(
                        service::createPackage,
                        artifactChainReportService::getReport,
                        completionCertificateService::getCertificate,
                        replayPackageService::getPackage,
                        java.time.Clock.fixed(Instant.parse("2026-07-02T12:00:00Z"), java.time.ZoneOffset.UTC)
                );
        return MockMvcBuilders
                .standaloneSetup(new DemoLiveDemoHandoffPackageController(
                        service,
                        receiptService,
                        finalizationService,
                        archiveService,
                        completionCertificateService,
                        completionCertificateArchiveService,
                        artifactChainReportService,
                        replayPackageService,
                        reviewerDeliveryCenterService
                ))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
    }

    private static MockMvc mockMvcWithArtifactChain() {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");

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

        DemoLiveDemoArtifactChainReportService artifactChainReportService =
                new DemoLiveDemoArtifactChainReportService(
                        launchRepository,
                        closeoutRepository,
                        evidenceRepository,
                        finalizationRepository,
                        completionRepository,
                        java.time.Clock.fixed(Instant.parse("2026-07-02T10:00:00Z"), java.time.ZoneOffset.UTC)
                );
        DemoLiveDemoReplayPackageService replayPackageService =
                new DemoLiveDemoReplayPackageService(
                        artifactChainReportService,
                        java.time.Clock.fixed(Instant.parse("2026-07-02T11:00:00Z"), java.time.ZoneOffset.UTC)
                );

        DemoLiveDemoHandoffPackageService service = new DemoLiveDemoHandoffPackageService(
                evidenceRepository,
                () -> Instant.parse("2026-07-02T04:00:00Z")
        );
        DemoLiveDemoHandoffDeliveryReceiptService receiptService = new DemoLiveDemoHandoffDeliveryReceiptService(
                service::createPackage,
                new InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository(),
                () -> "live-demo-handoff-delivery-receipt-1",
                () -> Instant.parse("2026-07-02T05:00:00Z")
        );
        DemoLiveDemoHandoffDeliveryFinalizationService finalizationService =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        service::createPackage,
                        List::of,
                        java.time.Clock.systemUTC()
                );
        DemoLiveDemoHandoffDeliveryFinalizationArchiveService archiveService =
                new DemoLiveDemoHandoffDeliveryFinalizationArchiveService(
                        finalizationService,
                        finalizationRepository
                );
        DemoLiveDemoCompletionCertificateService completionCertificateService =
                new DemoLiveDemoCompletionCertificateService(
                        archiveService::listRecentArchives,
                        java.time.Clock.systemUTC()
                );
        DemoLiveDemoCompletionCertificateArchiveService completionCertificateArchiveService =
                new DemoLiveDemoCompletionCertificateArchiveService(
                        completionCertificateService,
                        completionRepository
                );
        DemoLiveDemoReviewerDeliveryCenterService reviewerDeliveryCenterService =
                new DemoLiveDemoReviewerDeliveryCenterService(
                        service::createPackage,
                        artifactChainReportService::getReport,
                        completionCertificateService::getCertificate,
                        replayPackageService::getPackage,
                        java.time.Clock.fixed(Instant.parse("2026-07-02T12:00:00Z"), java.time.ZoneOffset.UTC)
                );
        return MockMvcBuilders
                .standaloneSetup(new DemoLiveDemoHandoffPackageController(
                        service,
                        receiptService,
                        finalizationService,
                        archiveService,
                        completionCertificateService,
                        completionCertificateArchiveService,
                        artifactChainReportService,
                        replayPackageService,
                        reviewerDeliveryCenterService
                ))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
    }
}
