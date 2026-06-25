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
        String message,
        WebhookDeliveryOutcomeType outcomeType,
        String outcomeId,
        String outcomeUrl
) {

    public RecordWebhookDeliveryDiagnosticCommand(
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
        this(
                deliveryId,
                event,
                status,
                taskId,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                message,
                defaultOutcomeType(status),
                taskId,
                defaultOutcomeUrl(status, taskId)
        );
    }

    public RecordWebhookDeliveryDiagnosticCommand withOutcome(
            WebhookDeliveryOutcomeType outcomeType,
            String outcomeId,
            String outcomeUrl
    ) {
        return new RecordWebhookDeliveryDiagnosticCommand(
                deliveryId,
                event,
                status,
                taskId,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                message,
                outcomeType,
                outcomeId,
                outcomeUrl
        );
    }

    private static WebhookDeliveryOutcomeType defaultOutcomeType(WebhookDeliveryDiagnosticStatus status) {
        return switch (status) {
            case TASK_CREATED, ACTIVE_TASK_EXISTS -> WebhookDeliveryOutcomeType.TASK;
            case DUPLICATE_DELIVERY -> WebhookDeliveryOutcomeType.DUPLICATE;
            case REJECTED -> WebhookDeliveryOutcomeType.REJECTED_TRIGGER;
            case IGNORED -> WebhookDeliveryOutcomeType.IGNORED;
            case INVALID_SIGNATURE, BAD_REQUEST, FAILED -> WebhookDeliveryOutcomeType.ERROR;
        };
    }

    private static String defaultOutcomeUrl(WebhookDeliveryDiagnosticStatus status, String taskId) {
        return (status == WebhookDeliveryDiagnosticStatus.TASK_CREATED
                || status == WebhookDeliveryDiagnosticStatus.ACTIVE_TASK_EXISTS
                || status == WebhookDeliveryDiagnosticStatus.DUPLICATE_DELIVERY)
                && taskId != null
                ? "/tasks/" + taskId
                : null;
    }
}
