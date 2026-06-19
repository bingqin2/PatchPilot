package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.entity.FixTaskEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.time.Instant;

public final class FixTaskConvert {

    private FixTaskConvert() {
    }

    public static FixTaskVo toVo(FixTaskEntity entity) {
        return new FixTaskVo(
                entity.getId(),
                entity.getRepositoryOwner(),
                entity.getRepositoryName(),
                entity.getIssueNumber(),
                entity.getInstallationId(),
                entity.getTriggerUser(),
                entity.getTriggerComment(),
                entity.getDeliveryId(),
                entity.getCommentId(),
                FixTaskStatus.valueOf(entity.getStatus()),
                entity.getFailureReason(),
                entity.getCreatedAt()
        );
    }

    public static FixTaskEntity newEntity(String id, CreateFixTaskCommand command, Instant createdAt) {
        FixTaskEntity entity = new FixTaskEntity();
        entity.setId(id);
        entity.setRepositoryOwner(command.repositoryOwner());
        entity.setRepositoryName(command.repositoryName());
        entity.setIssueNumber(command.issueNumber());
        entity.setInstallationId(command.installationId());
        entity.setTriggerUser(command.triggerUser());
        entity.setTriggerComment(command.triggerComment());
        entity.setDeliveryId(command.deliveryId());
        entity.setCommentId(command.commentId());
        entity.setStatus(FixTaskStatus.PENDING.name());
        entity.setFailureReason(null);
        entity.setCreatedAt(createdAt);
        return entity;
    }

    public static FixTaskEntity replaceStatus(FixTaskEntity current, FixTaskStatus status, String failureReason) {
        FixTaskEntity entity = new FixTaskEntity();
        entity.setId(current.getId());
        entity.setRepositoryOwner(current.getRepositoryOwner());
        entity.setRepositoryName(current.getRepositoryName());
        entity.setIssueNumber(current.getIssueNumber());
        entity.setInstallationId(current.getInstallationId());
        entity.setTriggerUser(current.getTriggerUser());
        entity.setTriggerComment(current.getTriggerComment());
        entity.setDeliveryId(current.getDeliveryId());
        entity.setCommentId(current.getCommentId());
        entity.setStatus(status.name());
        entity.setFailureReason(failureReason);
        entity.setCreatedAt(current.getCreatedAt());
        return entity;
    }
}
