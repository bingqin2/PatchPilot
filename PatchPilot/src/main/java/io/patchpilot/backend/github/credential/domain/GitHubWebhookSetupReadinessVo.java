package io.patchpilot.backend.github.credential.domain;

import java.time.Instant;
import java.util.List;

public record GitHubWebhookSetupReadinessVo(
        String status,
        boolean secretConfigured,
        boolean publicUrlReady,
        String publicBaseUrl,
        String payloadUrl,
        String healthUrl,
        String latestDeliveryStatus,
        String latestDeliveryId,
        boolean redeliveryRecommended,
        String summary,
        List<String> nextActions,
        Instant checkedAt,
        String markdownReport
) {
    public GitHubWebhookSetupReadinessVo {
        nextActions = nextActions == null ? List.of() : List.copyOf(nextActions);
    }
}
