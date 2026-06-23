package io.patchpilot.backend.github.webhook.domain;

import java.time.Instant;

public record WebhookDeliveryDiagnosticVo(
        String id,
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
        boolean redeliveryRecommended,
        String operatorAction,
        Instant createdAt
) {
    public WebhookDeliveryDiagnosticVo(
            String id,
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
            Instant createdAt
    ) {
        this(
                id,
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
                redeliveryRecommended(status),
                operatorAction(status),
                createdAt
        );
    }

    private static boolean redeliveryRecommended(WebhookDeliveryDiagnosticStatus status) {
        return status == WebhookDeliveryDiagnosticStatus.INVALID_SIGNATURE
                || status == WebhookDeliveryDiagnosticStatus.BAD_REQUEST
                || status == WebhookDeliveryDiagnosticStatus.FAILED;
    }

    private static String operatorAction(WebhookDeliveryDiagnosticStatus status) {
        return switch (status) {
            case INVALID_SIGNATURE ->
                    "Fix the webhook secret or payload URL first, then use GitHub's Redeliver action for this delivery.";
            case BAD_REQUEST ->
                    "Fix the malformed webhook request or GitHub event configuration first, then use GitHub's Redeliver action for this delivery.";
            case FAILED ->
                    "Fix the backend processing error first, then use GitHub's Redeliver action for this delivery.";
            case TASK_CREATED ->
                    "Task was created. Do not redeliver this webhook unless you intentionally want GitHub to report a duplicate delivery.";
            case DUPLICATE_DELIVERY ->
                    "This delivery id was already handled. Do not redeliver unless you are validating idempotency.";
            case ACTIVE_TASK_EXISTS ->
                    "A task is already active for this issue. Wait for it to finish, cancel it, or retry from PatchPilot instead of redelivering.";
            case REJECTED ->
                    "PatchPilot rejected this trigger by policy. Change the issue comment, allowlist, or safety configuration before trying again.";
            case IGNORED ->
                    "PatchPilot intentionally ignored this delivery. Redelivery will not create a task unless the event or comment changes.";
        };
    }
}
