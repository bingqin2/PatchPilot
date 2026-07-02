package io.patchpilot.backend.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DemoLiveDemoHandoffDeliveryReceiptControllerTests {

    @Test
    void should_record_list_and_download_admin_protected_delivery_receipts() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/demo/live-demo-handoff-package/delivery-receipts")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveryChannel": "github-comment",
                                  "deliveryTarget": "https://github.com/bingqin2/PatchPilot/pull/42",
                                  "operator": "local-operator",
                                  "notes": "Sent the live demo handoff package to the reviewer.",
                                  "deliveredAt": "2026-07-02T04:55:00Z"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("live-demo-handoff-delivery-receipt-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.evidenceBundleArchiveId").value("live-demo-evidence-bundle-archive-1"))
                .andExpect(jsonPath("$.data.deliveryTarget").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("test-admin-token"))));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/delivery-receipts")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("live-demo-handoff-delivery-receipt-1"))
                .andExpect(jsonPath("$.data[0].pullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"));

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/delivery-receipts/live-demo-handoff-delivery-receipt-1/report/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("patchpilot-live-demo-handoff-delivery-receipt-live-demo-handoff-delivery-receipt-1.md")))
                .andExpect(content().string(containsString("# PatchPilot Live Demo Handoff Delivery Receipt")))
                .andExpect(content().string(containsString("live-demo-evidence-bundle-archive-1")));
    }

    @Test
    void should_require_admin_token_for_delivery_receipts() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/delivery-receipts"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    private static MockMvc mockMvc() {
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
        DemoLiveDemoHandoffDeliveryReceiptService receiptService = new DemoLiveDemoHandoffDeliveryReceiptService(
                packageService::createPackage,
                new InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository(),
                () -> "live-demo-handoff-delivery-receipt-1",
                () -> Instant.parse("2026-07-02T05:00:00Z")
        );
        DemoLiveDemoHandoffDeliveryFinalizationService finalizationService =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        packageService::createPackage,
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
                        archiveRepository,
                        new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                        new InMemoryDemoLiveDemoCompletionCertificateArchiveRepository()
                );
        return MockMvcBuilders
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
    }
}
