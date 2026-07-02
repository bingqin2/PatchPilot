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
        return MockMvcBuilders
                .standaloneSetup(new DemoLiveDemoHandoffPackageController(service, receiptService, finalizationService))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
    }
}
