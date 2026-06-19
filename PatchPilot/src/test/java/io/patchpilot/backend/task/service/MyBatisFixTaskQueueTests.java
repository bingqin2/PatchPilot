package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.entity.FixTaskQueueItemEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.mapper.FixTaskQueueItemMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskQueue;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskQueueTests {

    private final FixTaskQueueItemMapper queueItemMapper = mock(FixTaskQueueItemMapper.class);
    private final MyBatisFixTaskQueue queue = new MyBatisFixTaskQueue(queueItemMapper);

    @Test
    void should_enqueue_pending_queue_item() {
        when(queueItemMapper.insert(any(FixTaskQueueItemEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskQueueItemEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskQueueItemEntity.class);

        queue.enqueue("task-123");

        verify(queueItemMapper).insert(entityCaptor.capture());
        FixTaskQueueItemEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isNotBlank();
        assertThat(insertedEntity.getTaskId()).isEqualTo("task-123");
        assertThat(insertedEntity.getStatus()).isEqualTo(FixTaskQueueItemStatus.PENDING.name());
        assertThat(insertedEntity.getAttemptCount()).isZero();
        assertThat(insertedEntity.getLastError()).isNull();
        assertThat(insertedEntity.getAvailableAt()).isNotNull();
        assertThat(insertedEntity.getLockedAt()).isNull();
        assertThat(insertedEntity.getCreatedAt()).isEqualTo(insertedEntity.getAvailableAt());
        assertThat(insertedEntity.getUpdatedAt()).isEqualTo(insertedEntity.getAvailableAt());
    }

    @Test
    void should_claim_oldest_available_pending_item() {
        FixTaskQueueItemEntity newer = entity(
                "queue-newer",
                "task-newer",
                FixTaskQueueItemStatus.PENDING,
                0,
                Instant.parse("2026-06-19T10:05:00Z")
        );
        FixTaskQueueItemEntity older = entity(
                "queue-older",
                "task-older",
                FixTaskQueueItemStatus.PENDING,
                0,
                Instant.parse("2026-06-19T10:00:00Z")
        );
        when(queueItemMapper.selectList(any())).thenReturn(List.of(newer, older));
        when(queueItemMapper.updateById(any(FixTaskQueueItemEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskQueueItemEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskQueueItemEntity.class);

        Optional<FixTaskQueueItemVo> claimed = queue.claimNext();

        assertThat(claimed).isPresent();
        assertThat(claimed.get().id()).isEqualTo("queue-older");
        assertThat(claimed.get().taskId()).isEqualTo("task-older");
        assertThat(claimed.get().status()).isEqualTo(FixTaskQueueItemStatus.RUNNING);
        assertThat(claimed.get().attemptCount()).isEqualTo(1);
        verify(queueItemMapper).updateById(entityCaptor.capture());
        FixTaskQueueItemEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskQueueItemStatus.RUNNING.name());
        assertThat(updatedEntity.getAttemptCount()).isEqualTo(1);
        assertThat(updatedEntity.getLockedAt()).isNotNull();
    }

    @Test
    void should_return_empty_when_no_pending_item_can_be_claimed() {
        when(queueItemMapper.selectList(any())).thenReturn(List.of());

        assertThat(queue.claimNext()).isEmpty();

        verify(queueItemMapper, never()).updateById(any(FixTaskQueueItemEntity.class));
    }

    @Test
    void should_mark_queue_item_completed() {
        FixTaskQueueItemEntity current = entity(
                "queue-123",
                "task-123",
                FixTaskQueueItemStatus.RUNNING,
                1,
                Instant.parse("2026-06-19T10:00:00Z")
        );
        when(queueItemMapper.selectById("queue-123")).thenReturn(current);
        when(queueItemMapper.updateById(any(FixTaskQueueItemEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskQueueItemEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskQueueItemEntity.class);

        queue.markCompleted("queue-123");

        verify(queueItemMapper).updateById(entityCaptor.capture());
        FixTaskQueueItemEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskQueueItemStatus.COMPLETED.name());
        assertThat(updatedEntity.getLastError()).isNull();
        assertThat(updatedEntity.getUpdatedAt()).isAfter(current.getUpdatedAt());
    }

    @Test
    void should_mark_queue_item_failed_with_failure_reason() {
        FixTaskQueueItemEntity current = entity(
                "queue-123",
                "task-123",
                FixTaskQueueItemStatus.RUNNING,
                1,
                Instant.parse("2026-06-19T10:00:00Z")
        );
        when(queueItemMapper.selectById("queue-123")).thenReturn(current);
        when(queueItemMapper.updateById(any(FixTaskQueueItemEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskQueueItemEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskQueueItemEntity.class);

        queue.markFailed("queue-123", "worker failed");

        verify(queueItemMapper).updateById(entityCaptor.capture());
        FixTaskQueueItemEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskQueueItemStatus.FAILED.name());
        assertThat(updatedEntity.getLastError()).isEqualTo("worker failed");
        assertThat(updatedEntity.getUpdatedAt()).isAfter(current.getUpdatedAt());
    }

    private static FixTaskQueueItemEntity entity(
            String id,
            String taskId,
            FixTaskQueueItemStatus status,
            int attemptCount,
            Instant createdAt
    ) {
        FixTaskQueueItemEntity entity = new FixTaskQueueItemEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setStatus(status.name());
        entity.setAttemptCount(attemptCount);
        entity.setLastError(null);
        entity.setAvailableAt(createdAt);
        entity.setLockedAt(status == FixTaskQueueItemStatus.RUNNING ? createdAt.plusSeconds(5) : null);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(createdAt);
        return entity;
    }
}
