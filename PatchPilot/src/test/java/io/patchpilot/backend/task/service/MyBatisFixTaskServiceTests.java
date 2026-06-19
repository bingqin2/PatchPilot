package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.entity.FixTaskEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.mapper.FixTaskMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskServiceTests {

    private final FixTaskMapper fixTaskMapper = mock(FixTaskMapper.class);
    private final FixTaskService fixTaskService = new MyBatisFixTaskService(fixTaskMapper);

    @Test
    void should_create_pending_task() {
        when(fixTaskMapper.selectOne(any())).thenReturn(null);
        when(fixTaskMapper.insert(any(FixTaskEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskEntity.class);

        FixTaskCreationResult creationResult = fixTaskService.createFixTaskIfAbsent(command("delivery-123"));
        FixTaskVo task = creationResult.task();

        verify(fixTaskMapper).insert(entityCaptor.capture());
        FixTaskEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isNotBlank();
        assertThat(insertedEntity.getRepositoryOwner()).isEqualTo("octocat");
        assertThat(insertedEntity.getRepositoryName()).isEqualTo("hello-world");
        assertThat(insertedEntity.getIssueNumber()).isEqualTo(42);
        assertThat(insertedEntity.getInstallationId()).isZero();
        assertThat(insertedEntity.getTriggerUser()).isEqualTo("alice");
        assertThat(insertedEntity.getTriggerComment()).isEqualTo("/agent fix");
        assertThat(insertedEntity.getDeliveryId()).isEqualTo("delivery-123");
        assertThat(insertedEntity.getCommentId()).isEqualTo(98765);
        assertThat(insertedEntity.getStatus()).isEqualTo(FixTaskStatus.PENDING.name());
        assertThat(insertedEntity.getFailureReason()).isNull();
        assertThat(insertedEntity.getCreatedAt()).isNotNull();
        assertThat(insertedEntity.getPullRequestUrl()).isNull();
        assertThat(insertedEntity.getCompletedAt()).isNull();
        assertThat(insertedEntity.getUpdatedAt()).isEqualTo(insertedEntity.getCreatedAt());

        assertThat(task.id()).isEqualTo(insertedEntity.getId());
        assertThat(creationResult.created()).isTrue();
        assertThat(task.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(task.deliveryId()).isEqualTo("delivery-123");
    }

    @Test
    void should_return_existing_task_when_delivery_id_already_exists() {
        FixTaskEntity existingTask = entity("task-existing", "delivery-123", FixTaskStatus.COMPLETED,
                null, Instant.parse("2026-06-19T01:02:03Z"));
        when(fixTaskMapper.selectOne(any())).thenReturn(existingTask);

        FixTaskCreationResult creationResult = fixTaskService.createFixTaskIfAbsent(command("delivery-123"));
        FixTaskVo task = creationResult.task();

        verify(fixTaskMapper, never()).insert(any(FixTaskEntity.class));
        assertThat(creationResult.created()).isFalse();
        assertThat(task.id()).isEqualTo("task-existing");
        assertThat(task.deliveryId()).isEqualTo("delivery-123");
        assertThat(task.status()).isEqualTo(FixTaskStatus.COMPLETED);
    }

    @Test
    void should_update_status_and_failure_reason() {
        FixTaskEntity current = entity("task-123", "delivery-123", FixTaskStatus.RUNNING_TESTS,
                null, Instant.parse("2026-06-19T01:02:03Z"));
        when(fixTaskMapper.selectById("task-123")).thenReturn(current);
        when(fixTaskMapper.updateById(any(FixTaskEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskEntity.class);

        FixTaskVo failedTask = fixTaskService.markFailed("task-123", "maven test failed");

        verify(fixTaskMapper).updateById(entityCaptor.capture());
        FixTaskEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getId()).isEqualTo("task-123");
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskStatus.FAILED.name());
        assertThat(updatedEntity.getFailureReason()).isEqualTo("maven test failed");
        assertThat(updatedEntity.getUpdatedAt()).isAfter(current.getUpdatedAt());
        assertThat(failedTask.status()).isEqualTo(FixTaskStatus.FAILED);
        assertThat(failedTask.failureReason()).isEqualTo("maven test failed");
    }

    @Test
    void should_mark_completed_with_pull_request_url_and_timestamps() {
        FixTaskEntity current = entity("task-123", "delivery-123", FixTaskStatus.RUNNING_TESTS,
                null, Instant.parse("2026-06-19T01:02:03Z"));
        when(fixTaskMapper.selectById("task-123")).thenReturn(current);
        when(fixTaskMapper.updateById(any(FixTaskEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskEntity.class);

        FixTaskVo completedTask = fixTaskService.markCompleted(
                "task-123",
                "https://github.com/octocat/hello-world/pull/7"
        );

        verify(fixTaskMapper).updateById(entityCaptor.capture());
        FixTaskEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskStatus.COMPLETED.name());
        assertThat(updatedEntity.getFailureReason()).isNull();
        assertThat(updatedEntity.getPullRequestUrl()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(updatedEntity.getCompletedAt()).isNotNull();
        assertThat(updatedEntity.getUpdatedAt()).isEqualTo(updatedEntity.getCompletedAt());
        assertThat(completedTask.pullRequestUrl()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(completedTask.completedAt()).isEqualTo(updatedEntity.getCompletedAt());
    }

    @Test
    void should_list_tasks_newest_first() {
        FixTaskEntity olderTask = entity("task-older", "delivery-older", FixTaskStatus.PENDING,
                null, Instant.parse("2026-06-19T01:00:00Z"));
        FixTaskEntity newerTask = entity("task-newer", "delivery-newer", FixTaskStatus.COMPLETED,
                null, Instant.parse("2026-06-19T02:00:00Z"));
        when(fixTaskMapper.selectList(any())).thenReturn(List.of(olderTask, newerTask));

        List<FixTaskVo> tasks = fixTaskService.listTasks();

        assertThat(tasks)
                .extracting(FixTaskVo::id)
                .containsExactly("task-newer", "task-older");
    }

    @Test
    void should_find_task_by_id() {
        FixTaskEntity existingTask = entity("task-123", "delivery-123", FixTaskStatus.PENDING,
                null, Instant.parse("2026-06-19T01:00:00Z"));
        when(fixTaskMapper.selectById("task-123")).thenReturn(existingTask);

        assertThat(fixTaskService.findTask("task-123"))
                .get()
                .extracting(FixTaskVo::id)
                .isEqualTo("task-123");
    }

    @Test
    void should_reject_status_transition_for_missing_task() {
        when(fixTaskMapper.selectById("missing-task")).thenReturn(null);

        assertThatThrownBy(() -> fixTaskService.markRunning("missing-task"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task not found: missing-task");
    }

    private CreateFixTaskCommand command(String deliveryId) {
        return new CreateFixTaskCommand(
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                deliveryId,
                98765
        );
    }

    private FixTaskEntity entity(String id, String deliveryId, FixTaskStatus status,
                                 String failureReason, Instant createdAt) {
        FixTaskEntity entity = new FixTaskEntity();
        entity.setId(id);
        entity.setRepositoryOwner("octocat");
        entity.setRepositoryName("hello-world");
        entity.setIssueNumber(42);
        entity.setInstallationId(0);
        entity.setTriggerUser("alice");
        entity.setTriggerComment("/agent fix");
        entity.setDeliveryId(deliveryId);
        entity.setCommentId(98765);
        entity.setStatus(status.name());
        entity.setFailureReason(failureReason);
        entity.setCreatedAt(createdAt);
        entity.setPullRequestUrl(null);
        entity.setCompletedAt(null);
        entity.setUpdatedAt(createdAt);
        return entity;
    }
}
