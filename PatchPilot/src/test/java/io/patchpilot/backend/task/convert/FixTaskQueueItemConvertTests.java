package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskQueueItemEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskQueueItemConvertTests {

    @Test
    void should_convert_between_entity_and_vo() {
        Instant createdAt = Instant.parse("2026-06-19T10:00:00Z");

        FixTaskQueueItemEntity entity = FixTaskQueueItemConvert.newPendingEntity(
                "queue-item-123",
                "task-123",
                createdAt
        );
        entity.setStatus(FixTaskQueueItemStatus.RUNNING.name());
        entity.setAttemptCount(1);
        entity.setLastError("previous failure");
        entity.setLockedAt(createdAt.plusSeconds(5));
        entity.setUpdatedAt(createdAt.plusSeconds(5));
        FixTaskQueueItemVo vo = FixTaskQueueItemConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("queue-item-123");
        assertThat(entity.getTaskId()).isEqualTo("task-123");
        assertThat(entity.getStatus()).isEqualTo(FixTaskQueueItemStatus.RUNNING.name());
        assertThat(entity.getAttemptCount()).isEqualTo(1);
        assertThat(entity.getLastError()).isEqualTo("previous failure");
        assertThat(entity.getAvailableAt()).isEqualTo(createdAt);
        assertThat(entity.getLockedAt()).isEqualTo(createdAt.plusSeconds(5));
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.getUpdatedAt()).isEqualTo(createdAt.plusSeconds(5));
        assertThat(vo.id()).isEqualTo("queue-item-123");
        assertThat(vo.taskId()).isEqualTo("task-123");
        assertThat(vo.status()).isEqualTo(FixTaskQueueItemStatus.RUNNING);
        assertThat(vo.attemptCount()).isEqualTo(1);
        assertThat(vo.lastError()).isEqualTo("previous failure");
        assertThat(vo.availableAt()).isEqualTo(createdAt);
        assertThat(vo.lockedAt()).isEqualTo(createdAt.plusSeconds(5));
        assertThat(vo.createdAt()).isEqualTo(createdAt);
        assertThat(vo.updatedAt()).isEqualTo(createdAt.plusSeconds(5));
    }
}
