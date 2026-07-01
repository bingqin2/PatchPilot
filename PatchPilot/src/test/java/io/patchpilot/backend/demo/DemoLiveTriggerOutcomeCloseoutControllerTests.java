package io.patchpilot.backend.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutVo;
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

class DemoLiveTriggerOutcomeCloseoutControllerTests {

    @Test
    void should_create_admin_protected_live_trigger_outcome_closeout_without_exposing_token() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/demo/live-trigger-outcome-closeout")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.successful").value(true))
                .andExpect(jsonPath("$.data.taskId").value("task-1"))
                .andExpect(jsonPath("$.data.pullRequestUrl").value("https://github.com/bingqin2/PatchPilot/pull/42"))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("PatchPilot Live Trigger Outcome Closeout")))
                .andExpect(content().string(not(containsString("test-admin-token"))));
    }

    @Test
    void should_download_live_trigger_outcome_closeout_report() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/demo/live-trigger-outcome-closeout/report/download")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("patchpilot-live-trigger-outcome-closeout.md")))
                .andExpect(content().string(containsString("# PatchPilot Live Trigger Outcome Closeout")))
                .andExpect(content().string(containsString("https://github.com/bingqin2/PatchPilot/pull/42")));
    }

    @Test
    void should_reject_invalid_live_trigger_outcome_closeout_request() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/demo/live-trigger-outcome-closeout")
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
    void should_require_admin_token_for_live_trigger_outcome_closeout() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/demo/live-trigger-outcome-closeout")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    private static MockMvc mockMvc() {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        return MockMvcBuilders
                .standaloneSetup(new DemoLiveTriggerOutcomeCloseoutController(new StubOutcomeCloseoutService()))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
    }

    private static String requestJson() {
        return """
                {
                  "repositoryOwner": "bingqin2",
                  "repositoryName": "PatchPilot",
                  "issueNumber": 1,
                  "triggerUser": "bingqin2",
                  "triggerComment": "/agent fix touch docs/live-outcome.md",
                  "launchPackageArchiveId": "launch-package-archive-1"
                }
                """;
    }

    private static final class StubOutcomeCloseoutService extends DemoLiveTriggerOutcomeCloseoutService {

        private StubOutcomeCloseoutService() {
            super(
                    new InMemoryDemoLiveTriggerLaunchPackageArchiveRepository(),
                    new io.patchpilot.backend.task.service.impl.InMemoryFixTaskService(),
                    Instant::now
            );
        }

        @Override
        public DemoLiveTriggerOutcomeCloseoutVo createCloseout(DemoLiveTriggerOutcomeCloseoutCommand command) {
            return new DemoLiveTriggerOutcomeCloseoutVo(
                    "READY",
                    true,
                    "bingqin2/PatchPilot",
                    1,
                    "https://github.com/bingqin2/PatchPilot/issues/1",
                    "bingqin2",
                    "/agent fix touch docs/live-outcome.md",
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
                    "Live trigger completed and created Pull Request https://github.com/bingqin2/PatchPilot/pull/42.",
                    List.of("Task task-1 completed."),
                    List.of("Review and merge https://github.com/bingqin2/PatchPilot/pull/42."),
                    "Read-only live trigger outcome closeout: this endpoint does not mutate GitHub or task state.",
                    Instant.parse("2026-07-02T01:00:00Z"),
                    "# PatchPilot Live Trigger Outcome Closeout\n\n- Pull Request: https://github.com/bingqin2/PatchPilot/pull/42"
            );
        }
    }
}
