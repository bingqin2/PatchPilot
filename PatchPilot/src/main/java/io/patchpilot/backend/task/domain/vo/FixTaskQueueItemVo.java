package io.patchpilot.backend.task.domain.vo;

import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;

import java.time.Instant;

public record FixTaskQueueItemVo(
        String id,
        String taskId,
        FixTaskQueueItemStatus status,
        int attemptCount,
        String lastError,
        Instant availableAt,
        Instant lockedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
