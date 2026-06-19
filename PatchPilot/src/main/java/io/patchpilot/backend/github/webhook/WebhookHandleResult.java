package io.patchpilot.backend.github.webhook;

public record WebhookHandleResult(WebhookHandleStatus status, String taskId) {

    public static WebhookHandleResult ignored() {
        return new WebhookHandleResult(WebhookHandleStatus.IGNORED, null);
    }

    public static WebhookHandleResult taskCreated(String taskId) {
        return new WebhookHandleResult(WebhookHandleStatus.TASK_CREATED, taskId);
    }

    public static WebhookHandleResult duplicate(String taskId) {
        return new WebhookHandleResult(WebhookHandleStatus.DUPLICATE_DELIVERY, taskId);
    }
}
