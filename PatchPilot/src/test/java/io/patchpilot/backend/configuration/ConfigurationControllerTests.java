package io.patchpilot.backend.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("default")
@TestPropertySource(properties = {
        "patchpilot.agent.provider=openai-compatible",
        "patchpilot.agent.model=gpt-5.5",
        "patchpilot.agent.base-url=https://api.example.test/v1",
        "patchpilot.agent.api-key=test-agent-key",
        "patchpilot.agent.cost.prompt-token-usd=0.000001",
        "patchpilot.agent.cost.completion-token-usd=0.000002",
        "patchpilot.github.token=test-github-token",
        "patchpilot.github.webhook-secret=test-webhook-secret",
        "patchpilot.safety.model-trigger-classification-enabled=true",
        "patchpilot.safety.trigger-rate-limit-enabled=true",
        "patchpilot.safety.trigger-rate-limit-window-ms=900000",
        "patchpilot.safety.trigger-rate-limit-max-per-trigger-user=9",
        "patchpilot.safety.trigger-rate-limit-max-per-repository=30",
        "patchpilot.safety.trigger-rate-limit-max-per-issue=4",
        "patchpilot.workspace.root-dir=/tmp/patchpilot/test-workspaces",
        "patchpilot.task.queue.max-attempts=5",
        "patchpilot.task.queue.retry-delay-ms=15000",
        "patchpilot.task.queue.visibility-timeout-ms=120000"
})
class ConfigurationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_non_sensitive_configuration_summary() throws Exception {
        mockMvc.perform(get("/api/configuration/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.agentProvider").value("openai-compatible"))
                .andExpect(jsonPath("$.data.agentModel").value("gpt-5.5"))
                .andExpect(jsonPath("$.data.agentBaseUrl").value("https://api.example.test/v1"))
                .andExpect(jsonPath("$.data.agentApiKeyConfigured").value(true))
                .andExpect(jsonPath("$.data.githubTokenConfigured").value(true))
                .andExpect(jsonPath("$.data.githubWebhookSecretConfigured").value(true))
                .andExpect(jsonPath("$.data.workspaceRootDir").value("/tmp/patchpilot/test-workspaces"))
                .andExpect(jsonPath("$.data.queueMaxAttempts").value(5))
                .andExpect(jsonPath("$.data.queueRetryDelayMs").value(15000))
                .andExpect(jsonPath("$.data.queueVisibilityTimeoutMs").value(120000))
                .andExpect(jsonPath("$.data.modelCostConfigured").value(true))
                .andExpect(jsonPath("$.data.modelTriggerClassificationEnabled").value(true))
                .andExpect(jsonPath("$.data.triggerRateLimitEnabled").value(true))
                .andExpect(jsonPath("$.data.triggerRateLimitWindowMs").value(900000))
                .andExpect(jsonPath("$.data.triggerRateLimitMaxPerTriggerUser").value(9))
                .andExpect(jsonPath("$.data.triggerRateLimitMaxPerRepository").value(30))
                .andExpect(jsonPath("$.data.triggerRateLimitMaxPerIssue").value(4))
                .andExpect(content().string(not(containsString("test-agent-key"))))
                .andExpect(content().string(not(containsString("test-github-token"))))
                .andExpect(content().string(not(containsString("test-webhook-secret"))));
    }
}
