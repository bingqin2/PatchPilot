package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.entity.FixTaskQueueItemEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.mapper.FixTaskQueueItemMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskQueueQueryService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MyBatisFixTaskQueueQueryServiceTests {

    private final FixTaskQueueItemMapper queueItemMapper = mock(FixTaskQueueItemMapper.class);
    private final MyBatisFixTaskQueueQueryService queryService = new MyBatisFixTaskQueueQueryService(queueItemMapper);

    @Test
    void should_list_queue_items() {
        when(queueItemMapper.selectList(any())).thenReturn(List.of(
                entity("queue-1", "task-1", FixTaskQueueItemStatus.PENDING, 0, Instant.parse("2026-06-19T10:00:00Z")),
                entity("queue-2", "task-2", FixTaskQueueItemStatus.RUNNING, 1, Instant.parse("2026-06-19T10:01:00Z"))
        ));

        List<FixTaskQueueItemVo> queueItems = queryService.listItems(null);

        assertThat(queueItems).extracting(FixTaskQueueItemVo::id).containsExactly("queue-1", "queue-2");
        assertThat(queueItems).extracting(FixTaskQueueItemVo::taskId).containsExactly("task-1", "task-2");
        assertThat(queueItems).extracting(FixTaskQueueItemVo::status)
                .containsExactly(FixTaskQueueItemStatus.PENDING, FixTaskQueueItemStatus.RUNNING);
    }

    @Test
    void should_list_queue_items_by_status() {
        when(queueItemMapper.selectList(any())).thenReturn(List.of(
                entity("queue-failed", "task-failed", FixTaskQueueItemStatus.FAILED, 3, Instant.parse("2026-06-19T10:00:00Z"))
        ));

        List<FixTaskQueueItemVo> queueItems = queryService.listItems(FixTaskQueueItemStatus.FAILED);

        assertThat(queueItems).hasSize(1);
        assertThat(queueItems.get(0).id()).isEqualTo("queue-failed");
        assertThat(queueItems.get(0).status()).isEqualTo(FixTaskQueueItemStatus.FAILED);
    }

    @Test
    void should_find_latest_queue_item_by_task_id() {
        when(queueItemMapper.selectList(any())).thenReturn(List.of(
                entity("queue-latest", "task-123", FixTaskQueueItemStatus.FAILED, 3, Instant.parse("2026-06-19T10:05:00Z")),
                entity("queue-older", "task-123", FixTaskQueueItemStatus.PENDING, 1, Instant.parse("2026-06-19T10:00:00Z"))
        ));

        assertThat(queryService.findByTaskId("task-123"))
                .isPresent()
                .get()
                .extracting(FixTaskQueueItemVo::id, FixTaskQueueItemVo::status, FixTaskQueueItemVo::attemptCount)
                .containsExactly("queue-latest", FixTaskQueueItemStatus.FAILED, 3);
    }

    @Test
    void should_list_queue_items_by_task_id() {
        when(queueItemMapper.selectList(any())).thenReturn(List.of(
                entity("queue-latest", "task-123", FixTaskQueueItemStatus.FAILED, 3, Instant.parse("2026-06-19T10:05:00Z")),
                entity("queue-older", "task-123", FixTaskQueueItemStatus.PENDING, 1, Instant.parse("2026-06-19T10:00:00Z"))
        ));

        List<FixTaskQueueItemVo> queueItems = queryService.listByTaskId("task-123");

        assertThat(queueItems).extracting(FixTaskQueueItemVo::id)
                .containsExactly("queue-latest", "queue-older");
        assertThat(queueItems).extracting(FixTaskQueueItemVo::status)
                .containsExactly(FixTaskQueueItemStatus.FAILED, FixTaskQueueItemStatus.PENDING);
    }

    @Test
    void should_summarize_queue_status_counts_and_pending_availability() {
        when(queueItemMapper.selectList(any())).thenReturn(List.of(
                entity("queue-available", "task-available", FixTaskQueueItemStatus.PENDING, 0, Instant.now().minusSeconds(5)),
                entity("queue-delayed", "task-delayed", FixTaskQueueItemStatus.PENDING, 1, Instant.now().plusSeconds(60)),
                entity("queue-running", "task-running", FixTaskQueueItemStatus.RUNNING, 1, Instant.now().minusSeconds(30)),
                entity("queue-completed", "task-completed", FixTaskQueueItemStatus.COMPLETED, 1, Instant.now().minusSeconds(120)),
                entity("queue-failed", "task-failed", FixTaskQueueItemStatus.FAILED, 3, Instant.now().minusSeconds(180)),
                entity("queue-cancelled", "task-cancelled", FixTaskQueueItemStatus.CANCELLED, 0, Instant.now().minusSeconds(240))
        ));

        FixTaskQueueSummaryVo summary = queryService.summary();

        assertThat(summary.totalCount()).isEqualTo(6);
        assertThat(summary.pendingCount()).isEqualTo(2);
        assertThat(summary.availablePendingCount()).isEqualTo(1);
        assertThat(summary.delayedPendingCount()).isEqualTo(1);
        assertThat(summary.runningCount()).isEqualTo(1);
        assertThat(summary.completedCount()).isEqualTo(1);
        assertThat(summary.failedCount()).isEqualTo(1);
        assertThat(summary.cancelledCount()).isEqualTo(1);
    }

    private static FixTaskQueueItemEntity entity(
            String id,
            String taskId,
            FixTaskQueueItemStatus status,
            int attemptCount,
            Instant availableAt
    ) {
        FixTaskQueueItemEntity entity = new FixTaskQueueItemEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setStatus(status.name());
        entity.setAttemptCount(attemptCount);
        entity.setLastError(status == FixTaskQueueItemStatus.FAILED ? "worker failed" : null);
        entity.setAvailableAt(availableAt);
        entity.setLockedAt(status == FixTaskQueueItemStatus.RUNNING ? availableAt.plusSeconds(1) : null);
        entity.setCreatedAt(availableAt.minusSeconds(10));
        entity.setUpdatedAt(availableAt);
        return entity;
    }
}
