package io.patchpilot.backend.agent.provider.domain;

import java.time.Instant;

public record ModelProviderHealthVo(
        String provider,
        String model,
        boolean baseUrlConfigured,
        boolean apiKeyConfigured,
        String status,
        String message,
        long latencyMs,
        Instant checkedAt,
        String operatorAction
) {
}
