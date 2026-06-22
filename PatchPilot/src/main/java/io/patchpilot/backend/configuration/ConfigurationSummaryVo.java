package io.patchpilot.backend.configuration;

import java.util.List;

public record ConfigurationSummaryVo(
        String agentProvider,
        String agentModel,
        String agentBaseUrl,
        boolean agentApiKeyConfigured,
        boolean githubTokenConfigured,
        boolean githubWebhookSecretConfigured,
        String workspaceRootDir,
        int queueMaxAttempts,
        long queueRetryDelayMs,
        long queueVisibilityTimeoutMs,
        boolean modelCostConfigured,
        boolean modelTriggerClassificationEnabled,
        boolean triggerRateLimitEnabled,
        long triggerRateLimitWindowMs,
        int triggerRateLimitMaxPerTriggerUser,
        int triggerRateLimitMaxPerRepository,
        int triggerRateLimitMaxPerIssue,
        List<String> reviewApprovalAllowedOperators
) {
}
