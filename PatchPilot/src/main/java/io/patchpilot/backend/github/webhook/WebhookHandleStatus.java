package io.patchpilot.backend.github.webhook;

public enum WebhookHandleStatus {
    IGNORED,
    REJECTED,
    TASK_CREATED,
    DUPLICATE_DELIVERY,
    ACTIVE_TASK_EXISTS
}
