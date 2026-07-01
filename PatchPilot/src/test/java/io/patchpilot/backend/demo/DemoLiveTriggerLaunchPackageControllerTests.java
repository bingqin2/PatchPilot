package io.patchpilot.backend.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoLiveLaunchGateVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageVo;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DemoLiveTriggerLaunchPackageControllerTests {

    @Test
    void should_create_admin_protected_live_trigger_launch_package_without_exposing_token() throws Exception {
        MockMvc mockMvc = mockMvc(launchPackageService());

        mockMvc.perform(post("/api/demo/live-trigger-launch-package")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToPost").value(true))
                .andExpect(jsonPath("$.data.operatorHandoffArchiveId").value("operator-archive-1"))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("PatchPilot Live Trigger Launch Package")))
                .andExpect(content().string(not(containsString("test-admin-token"))));
    }

    @Test
    void should_download_live_trigger_launch_package_report() throws Exception {
        MockMvc mockMvc = mockMvc(launchPackageService());

        mockMvc.perform(post("/api/demo/live-trigger-launch-package/report/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("patchpilot-live-trigger-launch-package.md")))
                .andExpect(content().string(containsString("# PatchPilot Live Trigger Launch Package")));
    }

    @Test
    void should_reject_invalid_live_trigger_launch_package_request() throws Exception {
        MockMvc mockMvc = mockMvc(launchPackageService());

        mockMvc.perform(post("/api/demo/live-trigger-launch-package")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 1,
                                  "triggerUser": "bingqin2",
                                  "triggerComment": "hello"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("triggerComment must start with /agent fix"));
    }

    @Test
    void should_require_admin_token_for_live_trigger_launch_package() throws Exception {
        MockMvc mockMvc = mockMvc(launchPackageService());

        mockMvc.perform(post("/api/demo/live-trigger-launch-package")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    private static MockMvc mockMvc(DemoLiveTriggerLaunchPackageService service) {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        return MockMvcBuilders
                .standaloneSetup(new DemoLiveTriggerLaunchPackageController(service))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
    }

    private static DemoLiveTriggerLaunchPackageService launchPackageService() {
        return new DemoLiveTriggerLaunchPackageService(
                command -> null,
                () -> java.util.Optional.empty(),
                Instant::now
        ) {
            @Override
            public DemoLiveTriggerLaunchPackageVo createPackage(DemoLiveTriggerLaunchPackageCommand command) {
                return launchPackage(command);
            }
        };
    }

    private static DemoLiveTriggerLaunchPackageVo launchPackage(DemoLiveTriggerLaunchPackageCommand command) {
        return new DemoLiveTriggerLaunchPackageVo(
                "READY",
                true,
                command.repositoryOwner() + "/" + command.repositoryName(),
                command.issueNumber(),
                "https://github.com/" + command.repositoryOwner() + "/" + command.repositoryName() + "/issues/" + command.issueNumber(),
                command.triggerUser(),
                command.triggerComment(),
                "PatchPilot is ready for the operator to post the live trigger.",
                "operator-archive-1",
                true,
                Instant.parse("2026-07-02T00:00:00Z"),
                "READY",
                true,
                List.of("Latest external exposure operator handoff archive operator-archive-1 is ready."),
                List.of("Post the exact comment."),
                "Read-only live trigger launch package: this endpoint does not create tasks.",
                null,
                Instant.parse("2026-07-02T00:00:00Z"),
                "# PatchPilot Live Trigger Launch Package"
        );
    }

    private static String requestJson() {
        return """
                {
                  "repositoryOwner": "bingqin2",
                  "repositoryName": "PatchPilot",
                  "issueNumber": 1,
                  "triggerUser": "bingqin2",
                  "triggerComment": "/agent fix touch docs/live-package.md"
                }
                """;
    }
}
