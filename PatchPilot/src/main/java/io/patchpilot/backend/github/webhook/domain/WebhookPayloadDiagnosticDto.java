package io.patchpilot.backend.github.webhook.domain;

public record WebhookPayloadDiagnosticDto(
        String event,
        String deliveryId,
        String signature,
        String payload
) {
}
