package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskQueueItemEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;

import java.time.Instant;

public final class FixTaskQueueItemConvert {

    private FixTaskQueueItemConvert() {
    }

    public static FixTaskQueueItemEntity newPendingEntity(String id, String taskId, Instant createdAt) {
        FixTaskQueueItemEntity entity = new FixTaskQueueItemEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setStatus(FixTaskQueueItemStatus.PENDING.name());
        entity.setAttemptCount(0);
        entity.setLastError(null);
        entity.setAvailableAt(createdAt);
        entity.setLockedAt(null);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(createdAt);
        return entity;
    }

    public static FixTaskQueueItemEntity replaceRunning(FixTaskQueueItemEntity current, Instant lockedAt) {
        FixTaskQueueItemEntity entity = copy(current);
        entity.setStatus(FixTaskQueueItemStatus.RUNNING.name());
        entity.setAttemptCount(current.getAttemptCount() + 1);
        entity.setLockedAt(lockedAt);
        entity.setUpdatedAt(lockedAt);
        return entity;
    }

    public static FixTaskQueueItemEntity replaceCompleted(FixTaskQueueItemEntity current, Instant updatedAt) {
        FixTaskQueueItemEntity entity = copy(current);
        entity.setStatus(FixTaskQueueItemStatus.COMPLETED.name());
        entity.setLastError(null);
        entity.setUpdatedAt(updatedAt);
        return entity;
    }

    public static FixTaskQueueItemEntity replaceFailed(
            FixTaskQueueItemEntity current,
            String failureReason,
            Instant updatedAt
    ) {
        FixTaskQueueItemEntity entity = copy(current);
        entity.setStatus(FixTaskQueueItemStatus.FAILED.name());
        entity.setLastError(failureReason);
        entity.setUpdatedAt(updatedAt);
        return entity;
    }

    public static FixTaskQueueItemVo toVo(FixTaskQueueItemEntity entity) {
        return new FixTaskQueueItemVo(
                entity.getId(),
                entity.getTaskId(),
                FixTaskQueueItemStatus.valueOf(entity.getStatus()),
                entity.getAttemptCount(),
                entity.getLastError(),
                entity.getAvailableAt(),
                entity.getLockedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private static FixTaskQueueItemEntity copy(FixTaskQueueItemEntity current) {
        FixTaskQueueItemEntity entity = new FixTaskQueueItemEntity();
        entity.setId(current.getId());
        entity.setTaskId(current.getTaskId());
        entity.setStatus(current.getStatus());
        entity.setAttemptCount(current.getAttemptCount());
        entity.setLastError(current.getLastError());
        entity.setAvailableAt(current.getAvailableAt());
        entity.setLockedAt(current.getLockedAt());
        entity.setCreatedAt(current.getCreatedAt());
        entity.setUpdatedAt(current.getUpdatedAt());
        return entity;
    }
}
