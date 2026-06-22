package io.patchpilot.backend.configuration;

import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.task.config.TaskQueueProperties;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ConfigurationSummaryService {

    private final AgentProperties agentProperties;
    private final GitHubProperties gitHubProperties;
    private final WorkspaceProperties workspaceProperties;
    private final TaskQueueProperties taskQueueProperties;
    private final SafetyProperties safetyProperties;

    @Value("${patchpilot.github.webhook-secret:}")
    private String webhookSecret;

    public ConfigurationSummaryVo getConfigurationSummary() {
        return new ConfigurationSummaryVo(
                valueOrEmpty(agentProperties.getProvider()),
                valueOrEmpty(agentProperties.getModel()),
                valueOrEmpty(agentProperties.getBaseUrl()),
                hasText(agentProperties.getApiKey()),
                hasText(gitHubProperties.getToken()),
                hasText(webhookSecret),
                workspaceProperties.getRootDir().toString(),
                taskQueueProperties.getMaxAttempts(),
                taskQueueProperties.getRetryDelayMs(),
                taskQueueProperties.getVisibilityTimeoutMs(),
                modelCostConfigured(),
                safetyProperties.isModelTriggerClassificationEnabled(),
                safetyProperties.isTriggerRateLimitEnabled(),
                safetyProperties.getTriggerRateLimitWindowMs(),
                safetyProperties.getTriggerRateLimitMaxPerTriggerUser(),
                safetyProperties.getTriggerRateLimitMaxPerRepository(),
                safetyProperties.getTriggerRateLimitMaxPerIssue()
        );
    }

    private boolean modelCostConfigured() {
        AgentProperties.Cost cost = agentProperties.getCost();
        return cost != null && (cost.getPromptTokenUsd() > 0 || cost.getCompletionTokenUsd() > 0);
    }

    private static boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
