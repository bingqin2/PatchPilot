package io.patchpilot.backend.github.webhook.convert;

import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticEntity;
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
                entity.getCreatedAt()
        );
    }
}
