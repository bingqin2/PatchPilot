package io.patchpilot.backend.github.credential.domain;

import java.time.Instant;

public record GitHubCredentialReadinessVo(
        boolean tokenConfigured,
        String status,
        String message,
        long latencyMs,
        Instant checkedAt,
        String operatorAction
) {
}
