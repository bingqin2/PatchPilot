package io.patchpilot.backend.safety.convert;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditEntity;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import org.springframework.util.StringUtils;

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
        entity.setCategory(categoryOrUnknown(command.category()));
        entity.setCommentId(command.commentId());
        entity.setCommentUrl(command.commentUrl());
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
                categoryOrUnknown(entity.getCategory()),
                entity.getCommentId(),
                entity.getCommentUrl(),
                entity.getRetriedTaskId(),
                entity.getRetriedAt(),
                entity.getCreatedAt()
        );
    }

    private static String categoryOrUnknown(String category) {
        return StringUtils.hasText(category) ? category : RejectedTriggerCategory.UNKNOWN;
    }
}
