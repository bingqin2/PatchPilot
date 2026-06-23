package io.patchpilot.backend.github.webhook.domain;

public record RecordWebhookDeliveryDiagnosticCommand(
        String deliveryId,
        String event,
        WebhookDeliveryDiagnosticStatus status,
        String taskId,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment,
        String message
) {
}
