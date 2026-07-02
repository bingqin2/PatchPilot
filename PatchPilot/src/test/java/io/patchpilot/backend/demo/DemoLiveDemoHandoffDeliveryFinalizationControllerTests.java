package io.patchpilot.backend.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DemoLiveDemoHandoffDeliveryFinalizationControllerTests {

    @Test
    void should_return_and_download_admin_protected_live_demo_handoff_delivery_finalization() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/delivery-finalization")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.finalized").value(true))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId")
                        .value("live-demo-handoff-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.evidenceBundleArchiveId")
                        .value("live-demo-evidence-bundle-archive-1"))
                .andExpect(jsonPath("$.data.deliveryReceiptFreshness").value("FRESH"))
                .andExpect(jsonPath("$.data.checks[0].name").value("Live demo handoff package"))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("test-admin-token"))));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/delivery-finalization/report/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CONTENT_DISPOSITION,
                        containsString("patchpilot-live-demo-handoff-delivery-finalization.md")
                ))
                .andExpect(content().string(containsString(
                        "# PatchPilot Live Demo Handoff Delivery Finalization"
                )))
                .andExpect(content().string(containsString("live-demo-handoff-delivery-receipt-1")));
    }

    @Test
    void should_create_list_and_download_live_demo_handoff_delivery_finalization_archives() throws Exception {
        TestControllerFixture fixture = controllerFixture();
        MockMvc mockMvc = fixture.mockMvc();

        mockMvc.perform(post("/api/demo/live-demo-handoff-package/delivery-finalization/archives")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("live-demo-handoff-delivery-finalization-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.finalized").value(true))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId")
                        .value("live-demo-handoff-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.evidenceBundleArchiveId")
                        .value("live-demo-evidence-bundle-archive-1"))
                .andExpect(jsonPath("$.data.report").value(containsString(
                        "PatchPilot Live Demo Handoff Delivery Finalization Archive"
                )));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/delivery-finalization/archives")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("live-demo-handoff-delivery-finalization-archive-1"))
                .andExpect(jsonPath("$.data[0].archivedAt").exists());

        mockMvc.perform(get(
                        "/api/demo/live-demo-handoff-package/delivery-finalization/archives/{archiveId}/report/download",
                        "live-demo-handoff-delivery-finalization-archive-1"
                ).header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CONTENT_DISPOSITION,
                        containsString(
                                "patchpilot-live-demo-handoff-delivery-finalization-archive-"
                                        + "live-demo-handoff-delivery-finalization-archive-1.md"
                        )
                ))
                .andExpect(content().string(containsString(
                        "# PatchPilot Live Demo Handoff Delivery Finalization Archive"
                )))
                .andExpect(content().string(containsString("live-demo-handoff-delivery-receipt-1")));

        mockMvc.perform(get(
                        "/api/demo/live-demo-handoff-package/delivery-finalization/archives/{archiveId}/report/download",
                        "missing-archive"
                ).header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isNotFound());

        assertThat(fixture.archiveService().listRecentArchives()).hasSize(1);
    }

    @Test
    void should_return_archive_list_and_download_live_demo_completion_certificates() throws Exception {
        TestControllerFixture fixture = controllerFixture();
        MockMvc mockMvc = fixture.mockMvc();
        fixture.archiveService().archiveFinalization();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/completion-certificate")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.certified").value(true))
                .andExpect(jsonPath("$.data.latestFinalizationArchiveId")
                        .value("live-demo-handoff-delivery-finalization-archive-1"))
                .andExpect(jsonPath("$.data.latestDeliveryReceiptId")
                        .value("live-demo-handoff-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.evidenceBundleArchiveId")
                        .value("live-demo-evidence-bundle-archive-1"));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/completion-certificate/report/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CONTENT_DISPOSITION,
                        containsString("patchpilot-live-demo-completion-certificate.md")
                ))
                .andExpect(content().string(containsString(
                        "# PatchPilot Live Demo Completion Certificate"
                )))
                .andExpect(content().string(containsString(
                        "live-demo-handoff-delivery-finalization-archive-1"
                )));

        mockMvc.perform(post("/api/demo/live-demo-handoff-package/completion-certificate/archives")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("live-demo-completion-certificate-archive-1"))
                .andExpect(jsonPath("$.data.certified").value(true))
                .andExpect(jsonPath("$.data.latestFinalizationArchiveId")
                        .value("live-demo-handoff-delivery-finalization-archive-1"));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/completion-certificate/archives")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("live-demo-completion-certificate-archive-1"));

        mockMvc.perform(get(
                        "/api/demo/live-demo-handoff-package/completion-certificate/archives/{archiveId}/report/download",
                        "live-demo-completion-certificate-archive-1"
                ).header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CONTENT_DISPOSITION,
                        containsString(
                                "patchpilot-live-demo-completion-certificate-archive-"
                                        + "live-demo-completion-certificate-archive-1.md"
                        )
                ))
                .andExpect(content().string(containsString(
                        "# PatchPilot Live Demo Completion Certificate"
                )));

        mockMvc.perform(get(
                        "/api/demo/live-demo-handoff-package/completion-certificate/archives/{archiveId}/report/download",
                        "missing-archive"
                ).header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_require_admin_token_for_live_demo_handoff_delivery_finalization() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/delivery-finalization"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    private static MockMvc mockMvc() {
        return controllerFixture().mockMvc();
    }

    private static TestControllerFixture controllerFixture() {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        InMemoryDemoLiveDemoEvidenceBundleArchiveRepository archiveRepository =
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository();
        archiveRepository.save(DemoLiveDemoHandoffPackageServiceTests.readyEvidenceBundleArchive(
                "live-demo-evidence-bundle-archive-1"
        ));
        DemoLiveDemoHandoffPackageService packageService = new DemoLiveDemoHandoffPackageService(
                archiveRepository,
                () -> Instant.parse("2026-07-02T04:00:00Z")
        );
        InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository receiptRepository =
                new InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository();
        DemoLiveDemoHandoffDeliveryReceiptService receiptService = new DemoLiveDemoHandoffDeliveryReceiptService(
                packageService::createPackage,
                receiptRepository,
                () -> "live-demo-handoff-delivery-receipt-1",
                () -> Instant.parse("2026-07-02T05:00:00Z")
        );
        receiptService.recordDeliveryReceipt(new DemoLiveDemoHandoffDeliveryReceiptRequestDto(
                "github-comment",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "local-operator",
                "Sent the live demo handoff package to the reviewer.",
                Instant.parse("2026-07-02T04:55:00Z")
        ));
        DemoLiveDemoHandoffDeliveryFinalizationService finalizationService =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        packageService::createPackage,
                        () -> receiptRepository.listRecentReceipts(1),
                        Clock.fixed(Instant.parse("2026-07-02T06:00:00Z"), ZoneOffset.UTC)
                );
        DemoLiveDemoHandoffDeliveryFinalizationArchiveService archiveService =
                new DemoLiveDemoHandoffDeliveryFinalizationArchiveService(
                        finalizationService,
                        new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                        () -> "live-demo-handoff-delivery-finalization-archive-1",
                        () -> Instant.parse("2026-07-02T07:00:00Z")
                );
        DemoLiveDemoCompletionCertificateService completionCertificateService =
                new DemoLiveDemoCompletionCertificateService(
                        archiveService::listRecentArchives,
                        Clock.fixed(Instant.parse("2026-07-02T08:00:00Z"), ZoneOffset.UTC)
                );
        DemoLiveDemoCompletionCertificateArchiveService completionCertificateArchiveService =
                new DemoLiveDemoCompletionCertificateArchiveService(
                        completionCertificateService::getCertificate,
                        new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository(),
                        () -> "live-demo-completion-certificate-archive-1",
                        () -> Instant.parse("2026-07-02T09:00:00Z")
                );
        DemoLiveDemoArtifactChainReportService artifactChainReportService =
                new DemoLiveDemoArtifactChainReportService(
                        new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository(),
                        new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository(),
                        archiveRepository,
                        new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                        new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository()
                );
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new DemoLiveDemoHandoffPackageController(
                        packageService,
                        receiptService,
                        finalizationService,
                        archiveService,
                        completionCertificateService,
                        completionCertificateArchiveService,
                        artifactChainReportService
                ))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
        return new TestControllerFixture(mockMvc, archiveService);
    }

    private record TestControllerFixture(
            MockMvc mockMvc,
            DemoLiveDemoHandoffDeliveryFinalizationArchiveService archiveService
    ) {
    }
}
