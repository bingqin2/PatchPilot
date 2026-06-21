package io.patchpilot.backend.github.webhook;

public record WebhookHandleResult(WebhookHandleStatus status, String taskId) {

    public static WebhookHandleResult ignored() {
        return new WebhookHandleResult(WebhookHandleStatus.IGNORED, null);
    }

    public static WebhookHandleResult rejected() {
        return new WebhookHandleResult(WebhookHandleStatus.REJECTED, null);
    }

    public static WebhookHandleResult taskCreated(String taskId) {
        return new WebhookHandleResult(WebhookHandleStatus.TASK_CREATED, taskId);
    }

    public static WebhookHandleResult duplicate(String taskId) {
        return new WebhookHandleResult(WebhookHandleStatus.DUPLICATE_DELIVERY, taskId);
    }

    public static WebhookHandleResult activeTaskExists(String taskId) {
        return new WebhookHandleResult(WebhookHandleStatus.ACTIVE_TASK_EXISTS, taskId);
    }
}
