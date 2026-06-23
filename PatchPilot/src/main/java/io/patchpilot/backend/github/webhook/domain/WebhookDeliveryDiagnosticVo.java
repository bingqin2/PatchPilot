package io.patchpilot.backend.github.webhook.domain;

import java.time.Instant;

public record WebhookDeliveryDiagnosticVo(
        String id,
        String deliveryId,
        String event,
        WebhookDeliveryDiagnosticStatus status,
        String taskId,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment,
        String message,
        Instant createdAt
) {
}
