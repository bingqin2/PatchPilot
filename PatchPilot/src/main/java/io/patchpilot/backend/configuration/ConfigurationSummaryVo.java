package io.patchpilot.backend.configuration;

import java.util.List;

public record ConfigurationSummaryVo(
        String agentProvider,
        String agentModel,
        String agentBaseUrl,
        boolean agentApiKeyConfigured,
        boolean githubTokenConfigured,
        boolean githubWebhookSecretConfigured,
        boolean adminTokenConfigured,
        boolean dashboardBaseUrlConfigured,
        String workspaceRootDir,
        int queueMaxAttempts,
        long queueRetryDelayMs,
        long queueVisibilityTimeoutMs,
        long queueWorkerHeartbeatStaleMs,
        boolean modelCostConfigured,
        boolean modelTriggerClassificationEnabled,
        boolean triggerRateLimitEnabled,
        long triggerRateLimitWindowMs,
        int triggerRateLimitMaxPerTriggerUser,
        int triggerRateLimitMaxPerRepository,
        int triggerRateLimitMaxPerIssue,
        boolean rejectedTriggerQuarantineEnabled,
        long rejectedTriggerQuarantineWindowMs,
        int rejectedTriggerQuarantineThreshold,
        long rejectedTriggerQuarantineCooldownMs,
        boolean triggerUserAllowlistConfigured,
        boolean repositoryAllowlistConfigured,
        boolean reviewApprovalAllowlistConfigured,
        boolean generatedDiffRiskGateEnabled,
        int generatedDiffProtectedPathCount,
        List<String> allowedTriggerUsers,
        List<String> allowedRepositories,
        List<String> reviewApprovalAllowedOperators,
        List<String> repositoryPreflightAllowedRootDirs
) {
}
