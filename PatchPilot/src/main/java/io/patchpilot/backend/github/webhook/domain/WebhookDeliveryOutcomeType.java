package io.patchpilot.backend.github.webhook.domain;

public enum WebhookDeliveryOutcomeType {
    TASK,
    REJECTED_TRIGGER,
    IGNORED,
    DUPLICATE,
    ERROR
}
