package io.patchpilot.backend.github.webhook.domain;

import io.patchpilot.backend.github.webhook.WebhookSignatureDiagnosticStatus;

public record WebhookPayloadDiagnosticVo(
        WebhookPayloadDiagnosticStatus status,
        WebhookSignatureDiagnosticStatus signatureStatus,
        boolean validJson,
        boolean supportedEvent,
        boolean supportedAction,
        boolean agentFixCommand,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment,
        String message,
        String nextAction
) {
}
