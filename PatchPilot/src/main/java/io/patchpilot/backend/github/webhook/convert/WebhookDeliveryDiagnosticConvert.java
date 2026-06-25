package io.patchpilot.backend.github.webhook.convert;

import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticEntity;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryOutcomeType;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;

import java.time.Instant;

public final class WebhookDeliveryDiagnosticConvert {

    private WebhookDeliveryDiagnosticConvert() {
    }

    public static WebhookDeliveryDiagnosticEntity newEntity(
            String id,
            RecordWebhookDeliveryDiagnosticCommand command,
            Instant createdAt
    ) {
        WebhookDeliveryDiagnosticEntity entity = new WebhookDeliveryDiagnosticEntity();
        entity.setId(id);
        entity.setDeliveryId(command.deliveryId());
        entity.setEvent(command.event());
        entity.setStatus(command.status());
        entity.setTaskId(command.taskId());
        entity.setRepositoryOwner(command.repositoryOwner());
        entity.setRepositoryName(command.repositoryName());
        entity.setIssueNumber(command.issueNumber());
        entity.setTriggerUser(command.triggerUser());
        entity.setTriggerComment(command.triggerComment());
        entity.setMessage(command.message());
        entity.setOutcomeType(command.outcomeType());
        entity.setOutcomeId(command.outcomeId());
        entity.setOutcomeUrl(command.outcomeUrl());
        entity.setCreatedAt(createdAt);
        return entity;
    }

    public static WebhookDeliveryDiagnosticVo toVo(WebhookDeliveryDiagnosticEntity entity) {
        return new WebhookDeliveryDiagnosticVo(
                entity.getId(),
                entity.getDeliveryId(),
                entity.getEvent(),
                entity.getStatus(),
                entity.getTaskId(),
                entity.getRepositoryOwner(),
                entity.getRepositoryName(),
                entity.getIssueNumber(),
                entity.getTriggerUser(),
                entity.getTriggerComment(),
                entity.getMessage(),
                outcomeType(entity),
                outcomeId(entity),
                outcomeUrl(entity),
                entity.getCreatedAt()
        );
    }

    private static WebhookDeliveryOutcomeType outcomeType(WebhookDeliveryDiagnosticEntity entity) {
        if (entity.getOutcomeType() != null) {
            return entity.getOutcomeType();
        }
        return switch (entity.getStatus()) {
            case TASK_CREATED, ACTIVE_TASK_EXISTS -> WebhookDeliveryOutcomeType.TASK;
            case DUPLICATE_DELIVERY -> WebhookDeliveryOutcomeType.DUPLICATE;
            case REJECTED -> WebhookDeliveryOutcomeType.REJECTED_TRIGGER;
            case IGNORED -> WebhookDeliveryOutcomeType.IGNORED;
            case INVALID_SIGNATURE, BAD_REQUEST, FAILED -> WebhookDeliveryOutcomeType.ERROR;
        };
    }

    private static String outcomeId(WebhookDeliveryDiagnosticEntity entity) {
        if (entity.getOutcomeId() != null) {
            return entity.getOutcomeId();
        }
        return taskBackedStatus(entity.getStatus()) ? entity.getTaskId() : null;
    }

    private static String outcomeUrl(WebhookDeliveryDiagnosticEntity entity) {
        if (entity.getOutcomeUrl() != null) {
            return entity.getOutcomeUrl();
        }
        String fallbackOutcomeId = outcomeId(entity);
        return taskBackedStatus(entity.getStatus()) && fallbackOutcomeId != null ? "/tasks/" + fallbackOutcomeId : null;
    }

    private static boolean taskBackedStatus(WebhookDeliveryDiagnosticStatus status) {
        return status == WebhookDeliveryDiagnosticStatus.TASK_CREATED
                || status == WebhookDeliveryDiagnosticStatus.ACTIVE_TASK_EXISTS
                || status == WebhookDeliveryDiagnosticStatus.DUPLICATE_DELIVERY;
    }
}
