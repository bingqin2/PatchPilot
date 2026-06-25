package io.patchpilot.backend.github.credential.domain;

import java.time.Instant;

public record GitHubRepositoryAccessReadinessVo(
        boolean tokenConfigured,
        boolean repositoryConfigured,
        String repository,
        String status,
        String message,
        String defaultBranch,
        long latencyMs,
        Instant checkedAt,
        String operatorAction
) {
}
