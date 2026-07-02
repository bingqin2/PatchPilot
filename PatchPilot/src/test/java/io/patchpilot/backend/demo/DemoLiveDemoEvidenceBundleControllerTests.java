package io.patchpilot.backend.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DemoLiveDemoEvidenceBundleControllerTests {

    @Test
    void should_return_admin_protected_live_demo_evidence_bundle_without_exposing_token() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-evidence-bundle")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyForHandoff").value(true))
                .andExpect(jsonPath("$.data.launchPackageArchiveId").value("launch-package-archive-1"))
                .andExpect(jsonPath("$.data.outcomeCloseoutArchiveId").value("outcome-closeout-archive-1"))
                .andExpect(jsonPath("$.data.pullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("PatchPilot Live Demo Evidence Bundle")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("test-admin-token"))));
    }

    @Test
    void should_download_live_demo_evidence_bundle_report() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-evidence-bundle/report/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("patchpilot-live-demo-evidence-bundle.md")))
                .andExpect(content().string(containsString("# PatchPilot Live Demo Evidence Bundle")))
                .andExpect(content().string(containsString("https://github.com/bingqin2/PatchPilot/pull/42")));
    }

    @Test
    void should_create_list_and_download_live_demo_evidence_bundle_archives() throws Exception {
        TestControllerFixture fixture = controllerFixture();
        MockMvc mockMvc = fixture.mockMvc();

        mockMvc.perform(post("/api/demo/live-demo-evidence-bundle/archives")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("live-demo-evidence-bundle-archive-1"))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyForHandoff").value(true))
                .andExpect(jsonPath("$.data.outcomeCloseoutArchiveId").value("outcome-closeout-archive-1"))
                .andExpect(jsonPath("$.data.pullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.report").value(containsString("PatchPilot Live Demo Evidence Bundle Archive")));

        mockMvc.perform(get("/api/demo/live-demo-evidence-bundle/archives")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("live-demo-evidence-bundle-archive-1"))
                .andExpect(jsonPath("$.data[0].archivedAt").value(notNullValue()));

        DemoLiveDemoEvidenceBundleArchiveVo archive = fixture.archiveService().listRecentArchives().get(0);
        mockMvc.perform(get("/api/demo/live-demo-evidence-bundle/archives/{archiveId}/report/download", archive.id())
                        .header("X-PatchPilot-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CONTENT_DISPOSITION,
                        containsString("patchpilot-live-demo-evidence-bundle-archive-live-demo-evidence-bundle-archive-1.md")
                ))
                .andExpect(content().string(containsString("# PatchPilot Live Demo Evidence Bundle Archive")))
                .andExpect(content().string(containsString("https://github.com/bingqin2/PatchPilot/pull/42")));
    }

    @Test
    void should_require_admin_token_for_live_demo_evidence_bundle() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(get("/api/demo/live-demo-evidence-bundle"))
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
        InMemoryDemoLiveTriggerLaunchPackageArchiveRepository launchRepository =
                new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository();
        InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository closeoutRepository =
                new InMemoryDemoLiveTriggerOutcomeCloseoutArchiveRepository();
        launchRepository.save(readyLaunchArchive());
        closeoutRepository.save(successfulCloseoutArchive());
        DemoLiveDemoEvidenceBundleService service = new DemoLiveDemoEvidenceBundleService(
                launchRepository,
                closeoutRepository,
                () -> Instant.parse("2026-07-02T02:00:00Z")
        );
        DemoLiveDemoEvidenceBundleArchiveService archiveService = new DemoLiveDemoEvidenceBundleArchiveService(
                service,
                new InMemoryDemoLiveDemoEvidenceBundleArchiveRepository(),
                () -> "live-demo-evidence-bundle-archive-1",
                () -> Instant.parse("2026-07-02T03:00:00Z")
        );
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new DemoLiveDemoEvidenceBundleController(service, archiveService))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
        return new TestControllerFixture(mockMvc, archiveService);
    }

    private static DemoLiveTriggerLaunchPackageArchiveVo readyLaunchArchive() {
        return new DemoLiveTriggerLaunchPackageArchiveVo(
                "launch-package-archive-1",
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                "PatchPilot is ready for the operator to post the live trigger.",
                "operator-archive-1",
                true,
                Instant.parse("2026-07-02T00:00:00Z"),
                "READY",
                true,
                List.of("Launch package archive was ready."),
                List.of("Post the exact comment."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T00:00:01Z"),
                Instant.parse("2026-07-02T00:00:05Z"),
                "# PatchPilot Live Trigger Launch Package"
        );
    }

    private static DemoLiveTriggerOutcomeCloseoutArchiveVo successfulCloseoutArchive() {
        return new DemoLiveTriggerOutcomeCloseoutArchiveVo(
                "outcome-closeout-archive-1",
                "READY",
                true,
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                "launch-package-archive-1",
                "READY",
                Instant.parse("2026-07-02T00:00:05Z"),
                "task-1",
                "COMPLETED",
                null,
                Instant.parse("2026-07-02T00:10:00Z"),
                Instant.parse("2026-07-02T00:11:00Z"),
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "TASK_CREATED",
                "Live trigger created a Pull Request.",
                List.of("Task task-1 completed."),
                List.of("Review and merge https://github.com/bingqin2/PatchPilot/pull/42."),
                "Archive creation writes only PatchPilot local archive records.",
                Instant.parse("2026-07-02T01:00:00Z"),
                Instant.parse("2026-07-02T01:05:00Z"),
                "# PatchPilot Live Trigger Outcome Closeout"
        );
    }

    private record TestControllerFixture(
            MockMvc mockMvc,
            DemoLiveDemoEvidenceBundleArchiveService archiveService
    ) {
    }
}
