package io.patchpilot.backend.github.credential.domain;

import java.time.Instant;
import java.util.List;

public record GitHubPublishPermissionReadinessVo(
        String status,
        boolean publishPermissionReady,
        boolean tokenConfigured,
        boolean repositoryConfigured,
        String repository,
        String defaultBranch,
        boolean canReadRepository,
        boolean canPushBranches,
        boolean canCreatePullRequests,
        boolean issueFeedbackPermissionLikely,
        String summary,
        String nextAction,
        String sideEffectContract,
        List<GitHubPublishPermissionReadinessCheckVo> permissionChecks,
        List<String> evidenceNotes,
        long latencyMs,
        Instant checkedAt
) {
}
