package io.patchpilot.backend.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchCheckVo;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightCheckVo;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.webhook.GitHubTriggerDryRunService;
import io.patchpilot.backend.security.AdminApiSecurityFilter;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DemoLiveLaunchGateControllerTests {

    private static final Instant NOW = Instant.parse("2026-07-01T10:00:00Z");

    @Test
    void should_return_live_launch_gate_readiness_without_exposing_admin_token() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/demo/live-launch-gate")
                        .header("X-PatchPilot-Admin-Token", "test-admin-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 1,
                                  "triggerUser": "bingqin2",
                                  "triggerComment": "/agent fix touch docs/live-gate.md"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andExpect(jsonPath("$.data.readyToPost").value(true))
                .andExpect(jsonPath("$.data.repository").value("bingqin2/PatchPilot"))
                .andExpect(jsonPath("$.data.issueUrl").value("https://github.com/bingqin2/PatchPilot/issues/1"))
                .andExpect(jsonPath("$.data.triggerDryRun.status").value("WOULD_CREATE_TASK"))
                .andExpect(jsonPath("$.data.webhookSetup.payloadUrl")
                        .value("https://example.trycloudflare.com/api/github/webhook"))
                .andExpect(jsonPath("$.data.livePublishPreflight.repository").value("bingqin2/PatchPilot"))
                .andExpect(jsonPath("$.data.checks[0].name").value("Self-hosted launch readiness"))
                .andExpect(jsonPath("$.data.markdownReport").value(containsString("PatchPilot Live Launch Gate")))
                .andExpect(content().string(not(containsString("test-admin-token"))));
    }

    @Test
    void should_reject_invalid_live_launch_gate_request() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/demo/live-launch-gate")
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
    void should_require_admin_token_for_live_launch_gate() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/demo/live-launch-gate")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "repositoryOwner": "bingqin2",
                                  "repositoryName": "PatchPilot",
                                  "issueNumber": 1,
                                  "triggerUser": "bingqin2",
                                  "triggerComment": "/agent fix touch docs/live-gate.md"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Admin token is required"));
    }

    private static MockMvc mockMvc() {
        DemoLiveLaunchGateService service = new DemoLiveLaunchGateService(
                DemoLiveLaunchGateControllerTests::launchReadiness,
                DemoLiveLaunchGateControllerTests::webhookReadiness,
                (owner, repository) -> publishPreflight(),
                new GitHubTriggerDryRunService(command -> readyTriggerEvaluation()),
                () -> NOW
        );
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        return MockMvcBuilders
                .standaloneSetup(new DemoLiveLaunchGateController(service))
                .addFilters(new AdminApiSecurityFilter(properties, new ObjectMapper()))
                .build();
    }

    private static DemoSelfHostedLaunchReadinessVo launchReadiness() {
        return new DemoSelfHostedLaunchReadinessVo(
                DemoReadinessStatus.READY,
                true,
                "ready",
                List.of(new DemoSelfHostedLaunchCheckVo("runtime", DemoReadinessStatus.READY, "ready", "none")),
                List.of(),
                NOW,
                "launch report"
        );
    }

    private static GitHubWebhookSetupReadinessVo webhookReadiness() {
        return new GitHubWebhookSetupReadinessVo(
                "READY",
                true,
                true,
                "https://example.trycloudflare.com",
                "https://example.trycloudflare.com/api/github/webhook",
                "https://example.trycloudflare.com/health",
                "OK",
                "delivery-1",
                false,
                "ready",
                List.of(),
                NOW,
                "webhook report"
        );
    }

    private static GitHubLivePublishPreflightVo publishPreflight() {
        return new GitHubLivePublishPreflightVo(
                "READY",
                true,
                true,
                true,
                "bingqin2/PatchPilot",
                "main",
                List.of(),
                List.of(),
                "ready",
                "ready",
                "publish preflight side effect contract",
                List.of(new GitHubLivePublishPreflightCheckVo("publish", "READY", "ready", "none")),
                List.of("publish evidence"),
                10,
                NOW
        );
    }

    private static TriggerEvaluationResultVo readyTriggerEvaluation() {
        return new TriggerEvaluationResultVo(
                "WOULD_CREATE_TASK",
                "ISSUE_COMMENT",
                true,
                null,
                null,
                new TriggerEvaluationDecisionVo(true, "safe", null),
                new TriggerEvaluationDecisionVo(true, "no active task", null),
                new TriggerEvaluationDecisionVo(true, "not quarantined", null),
                new TriggerEvaluationDecisionVo(true, "rate limit ok", null),
                new TriggerEvaluationDecisionVo(true, "intent accepted", null),
                true,
                "Create a task."
        );
    }
}
