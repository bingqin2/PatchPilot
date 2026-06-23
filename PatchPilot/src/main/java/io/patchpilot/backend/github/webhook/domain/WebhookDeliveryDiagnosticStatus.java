package io.patchpilot.backend.github.webhook.domain;

public enum WebhookDeliveryDiagnosticStatus {
    IGNORED,
    INVALID_SIGNATURE,
    BAD_REQUEST,
    REJECTED,
    DUPLICATE_DELIVERY,
    ACTIVE_TASK_EXISTS,
    TASK_CREATED,
    FAILED
}
