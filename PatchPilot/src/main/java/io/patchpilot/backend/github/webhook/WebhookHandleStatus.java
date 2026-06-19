package io.patchpilot.backend.github.webhook;

public enum WebhookHandleStatus {
    IGNORED,
    TASK_CREATED,
    DUPLICATE_DELIVERY,
    ACTIVE_TASK_EXISTS
}
