package io.patchpilot.backend.github.credential.domain;

import java.time.Instant;

public record GitHubWebhookUrlReadinessVo(
        boolean publicBaseUrlConfigured,
        String status,
        String publicBaseUrl,
        String payloadUrl,
        String healthUrl,
        String message,
        long latencyMs,
        Instant checkedAt,
        String operatorAction
) {
}
