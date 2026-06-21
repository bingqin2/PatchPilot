package io.patchpilot.backend.safety.convert;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditEntity;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;

import java.time.Instant;

public final class RejectedTriggerAuditConvert {

    private RejectedTriggerAuditConvert() {
    }

    public static RejectedTriggerAuditEntity newEntity(
            String id,
            RecordRejectedTriggerCommand command,
            Instant createdAt
    ) {
        RejectedTriggerAuditEntity entity = new RejectedTriggerAuditEntity();
        entity.setId(id);
        entity.setSource(command.source());
        entity.setDeliveryId(command.deliveryId());
        entity.setRepositoryOwner(command.repositoryOwner());
        entity.setRepositoryName(command.repositoryName());
        entity.setIssueNumber(command.issueNumber());
        entity.setTriggerUser(command.triggerUser());
        entity.setTriggerComment(command.triggerComment());
        entity.setReason(command.reason());
        entity.setCreatedAt(createdAt);
        return entity;
    }

    public static RejectedTriggerAuditVo toVo(RejectedTriggerAuditEntity entity) {
        return new RejectedTriggerAuditVo(
                entity.getId(),
                entity.getSource(),
                entity.getDeliveryId(),
                entity.getRepositoryOwner(),
                entity.getRepositoryName(),
                entity.getIssueNumber(),
                entity.getTriggerUser(),
                entity.getTriggerComment(),
                entity.getReason(),
                entity.getCreatedAt()
        );
    }
}
