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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void should_require_admin_token_for_live_demo_handoff_delivery_finalization() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-handoff-package/delivery-finalization"))
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
        return MockMvcBuilders
                .standaloneSetup(new DemoLiveDemoHandoffPackageController(
                        packageService,
                        receiptService,
                        finalizationService
                ))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
    }
}
