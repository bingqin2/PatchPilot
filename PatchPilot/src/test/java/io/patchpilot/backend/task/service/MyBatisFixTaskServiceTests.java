package io.patchpilot.backend.task.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.entity.FixTaskEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskSort;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.mapper.FixTaskMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskService;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    static void initializeMyBatisPlusMetadata() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                FixTaskEntity.class
        );
    }

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
        assertThat(insertedEntity.getStatusCommentId()).isNull();
        assertThat(insertedEntity.getStatusCommentUrl()).isNull();

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
    void should_mark_task_cancelled() {
        FixTaskEntity current = entity("task-123", "delivery-123", FixTaskStatus.PENDING,
                null, Instant.parse("2026-06-19T01:02:03Z"));
        when(fixTaskMapper.selectById("task-123")).thenReturn(current);
        when(fixTaskMapper.updateById(any(FixTaskEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskEntity.class);

        FixTaskVo cancelledTask = fixTaskService.markCancelled("task-123", "cancelled by user");

        verify(fixTaskMapper).updateById(entityCaptor.capture());
        FixTaskEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskStatus.CANCELLED.name());
        assertThat(updatedEntity.getFailureReason()).isEqualTo("cancelled by user");
        assertThat(updatedEntity.getUpdatedAt()).isAfter(current.getUpdatedAt());
        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
    }

    @Test
    void should_mark_terminal_task_pending_for_retry() {
        FixTaskEntity current = entity("task-123", "delivery-123", FixTaskStatus.FAILED,
                "executor failed", Instant.parse("2026-06-19T01:02:03Z"));
        when(fixTaskMapper.selectById("task-123")).thenReturn(current);
        when(fixTaskMapper.updateById(any(FixTaskEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskEntity.class);

        FixTaskVo retriedTask = fixTaskService.markPendingForRetry("task-123");

        verify(fixTaskMapper).updateById(entityCaptor.capture());
        FixTaskEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskStatus.PENDING.name());
        assertThat(updatedEntity.getFailureReason()).isNull();
        assertThat(updatedEntity.getPullRequestUrl()).isNull();
        assertThat(updatedEntity.getCompletedAt()).isNull();
        assertThat(updatedEntity.getUpdatedAt()).isAfter(current.getUpdatedAt());
        assertThat(retriedTask.status()).isEqualTo(FixTaskStatus.PENDING);
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
    void should_attach_status_comment_metadata() {
        FixTaskEntity current = entity("task-123", "delivery-123", FixTaskStatus.PENDING,
                null, Instant.parse("2026-06-19T01:02:03Z"));
        when(fixTaskMapper.selectById("task-123")).thenReturn(current);
        when(fixTaskMapper.updateById(any(FixTaskEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskEntity.class);

        FixTaskVo updatedTask = fixTaskService.attachStatusComment(
                "task-123",
                123,
                "https://github.com/octocat/hello-world/issues/42#issuecomment-123"
        );

        verify(fixTaskMapper).updateById(entityCaptor.capture());
        FixTaskEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getId()).isEqualTo("task-123");
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskStatus.PENDING.name());
        assertThat(updatedEntity.getStatusCommentId()).isEqualTo(123L);
        assertThat(updatedEntity.getStatusCommentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(updatedEntity.getUpdatedAt()).isAfter(current.getUpdatedAt());
        assertThat(updatedTask.statusCommentId()).isEqualTo(123L);
        assertThat(updatedTask.statusCommentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
    }

    @Test
    void should_record_adapter_metadata() {
        FixTaskEntity current = entity("task-123", "delivery-123", FixTaskStatus.RUNNING,
                null, Instant.parse("2026-06-19T01:02:03Z"));
        when(fixTaskMapper.selectById("task-123")).thenReturn(current);
        when(fixTaskMapper.updateById(any(FixTaskEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskEntity.class);

        FixTaskVo updatedTask = fixTaskService.recordAdapterMetadata(
                "task-123",
                "java",
                "gradle",
                "./gradlew test"
        );

        verify(fixTaskMapper).updateById(entityCaptor.capture());
        FixTaskEntity updatedEntity = entityCaptor.getValue();
        assertThat(updatedEntity.getId()).isEqualTo("task-123");
        assertThat(updatedEntity.getStatus()).isEqualTo(FixTaskStatus.RUNNING.name());
        assertThat(updatedEntity.getLanguage()).isEqualTo("java");
        assertThat(updatedEntity.getBuildSystem()).isEqualTo("gradle");
        assertThat(updatedEntity.getVerificationCommand()).isEqualTo("./gradlew test");
        assertThat(updatedEntity.getUpdatedAt()).isAfter(current.getUpdatedAt());
        assertThat(updatedTask.language()).isEqualTo("java");
        assertThat(updatedTask.buildSystem()).isEqualTo("gradle");
        assertThat(updatedTask.verificationCommand()).isEqualTo("./gradlew test");
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
    void should_list_tasks_with_query_status_limit_and_offset() {
        FixTaskEntity olderMatchingTask = entity("task-older", "delivery-query-older", FixTaskStatus.FAILED,
                "maven failed because search target", Instant.parse("2026-06-19T01:00:00Z"));
        FixTaskEntity newerMatchingTask = entity("task-newer", "delivery-query-newer", FixTaskStatus.FAILED,
                "maven failed because search target", Instant.parse("2026-06-19T02:00:00Z"));
        when(fixTaskMapper.selectList(any())).thenReturn(List.of(olderMatchingTask, newerMatchingTask));

        List<FixTaskVo> tasks = fixTaskService.listTasks(new FixTaskListQuery(
                "search target",
                FixTaskStatus.FAILED,
                "octocat",
                "hello-world",
                1,
                1
        ));

        assertThat(tasks)
                .extracting(FixTaskVo::id)
                .containsExactly("task-newer", "task-older");
        ArgumentCaptor<Wrapper<FixTaskEntity>> wrapperCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(fixTaskMapper).selectList(wrapperCaptor.capture());
        assertThat(wrapperCaptor.getValue().getSqlSegment())
                .contains("LIMIT 1, 1");
    }

    @Test
    void should_list_tasks_oldest_first_when_requested() {
        FixTaskEntity olderTask = entity("task-older", "delivery-sort-older", FixTaskStatus.PENDING,
                null, Instant.parse("2026-06-19T01:00:00Z"));
        FixTaskEntity newerTask = entity("task-newer", "delivery-sort-newer", FixTaskStatus.COMPLETED,
                null, Instant.parse("2026-06-19T02:00:00Z"));
        when(fixTaskMapper.selectList(any())).thenReturn(List.of(newerTask, olderTask));

        List<FixTaskVo> tasks = fixTaskService.listTasks(new FixTaskListQuery(
                "delivery-sort",
                null,
                "octocat",
                "hello-world",
                10,
                0,
                FixTaskSort.CREATED_AT_ASC
        ));

        assertThat(tasks)
                .extracting(FixTaskVo::id)
                .containsExactly("task-older", "task-newer");
        ArgumentCaptor<Wrapper<FixTaskEntity>> wrapperCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(fixTaskMapper).selectList(wrapperCaptor.capture());
        assertThat(wrapperCaptor.getValue().getSqlSegment())
                .contains("ORDER BY created_at ASC,id ASC")
                .contains("LIMIT 0, 10");
    }

    @Test
    void should_list_tasks_with_created_time_range() {
        FixTaskEntity newerTask = entity("task-newer", "delivery-created-range-newer", FixTaskStatus.COMPLETED,
                null, Instant.parse("2026-06-19T02:00:00Z"));
        when(fixTaskMapper.selectList(any())).thenReturn(List.of(newerTask));

        List<FixTaskVo> tasks = fixTaskService.listTasks(new FixTaskListQuery(
                null,
                null,
                "octocat",
                "hello-world",
                Instant.parse("2026-06-19T01:00:00Z"),
                Instant.parse("2026-06-19T03:00:00Z"),
                10,
                0,
                FixTaskSort.CREATED_AT_DESC
        ));

        assertThat(tasks)
                .extracting(FixTaskVo::id)
                .containsExactly("task-newer");
        ArgumentCaptor<Wrapper<FixTaskEntity>> wrapperCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(fixTaskMapper).selectList(wrapperCaptor.capture());
        assertThat(wrapperCaptor.getValue().getSqlSegment())
                .contains("created_at")
                .contains("LIMIT 0, 10");
    }

    @Test
    void should_list_tasks_with_adapter_metadata_filters() {
        FixTaskEntity npmTask = entity("task-npm", "delivery-adapter-npm", FixTaskStatus.COMPLETED,
                null, Instant.parse("2026-06-19T02:00:00Z"));
        when(fixTaskMapper.selectList(any())).thenReturn(List.of(npmTask));

        List<FixTaskVo> tasks = fixTaskService.listTasks(new FixTaskListQuery(
                null,
                null,
                "octocat",
                "hello-world",
                "node",
                "npm",
                10,
                0
        ));

        assertThat(tasks)
                .extracting(FixTaskVo::id)
                .containsExactly("task-npm");
        ArgumentCaptor<Wrapper<FixTaskEntity>> wrapperCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(fixTaskMapper).selectList(wrapperCaptor.capture());
        assertThat(wrapperCaptor.getValue().getSqlSegment())
                .contains("language")
                .contains("build_system")
                .contains("LIMIT 0, 10");
    }

    @Test
    void should_count_tasks_with_query_filters_without_limit_or_offset() {
        when(fixTaskMapper.selectCount(any())).thenReturn(2L);

        long total = fixTaskService.countTasks(new FixTaskListQuery(
                "search target",
                FixTaskStatus.FAILED,
                "octocat",
                "hello-world",
                1,
                1
        ));

        assertThat(total).isEqualTo(2);
        ArgumentCaptor<Wrapper<FixTaskEntity>> wrapperCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(fixTaskMapper).selectCount(wrapperCaptor.capture());
        assertThat(wrapperCaptor.getValue().getSqlSegment())
                .doesNotContain("LIMIT")
                .contains("status")
                .contains("repository_owner")
                .contains("repository_name");
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
    void should_find_task_by_delivery_id() {
        FixTaskEntity existingTask = entity("task-123", "delivery-123", FixTaskStatus.PENDING,
                null, Instant.parse("2026-06-19T01:00:00Z"));
        when(fixTaskMapper.selectOne(any())).thenReturn(existingTask);

        assertThat(fixTaskService.findTaskByDeliveryId("delivery-123"))
                .get()
                .extracting(FixTaskVo::id)
                .isEqualTo("task-123");
    }

    @Test
    void should_find_active_task_for_issue_newest_first() {
        FixTaskEntity olderTask = entity("task-older", "delivery-older", FixTaskStatus.PENDING,
                null, Instant.parse("2026-06-19T01:00:00Z"));
        FixTaskEntity newerTask = entity("task-newer", "delivery-newer", FixTaskStatus.RUNNING_TESTS,
                null, Instant.parse("2026-06-19T02:00:00Z"));
        when(fixTaskMapper.selectList(any())).thenReturn(List.of(olderTask, newerTask));

        assertThat(fixTaskService.findActiveTaskForIssue("octocat", "hello-world", 42))
                .get()
                .extracting(FixTaskVo::id)
                .isEqualTo("task-newer");
    }

    @Test
    void should_return_empty_when_issue_has_no_active_task() {
        when(fixTaskMapper.selectList(any())).thenReturn(List.of());

        assertThat(fixTaskService.findActiveTaskForIssue("octocat", "hello-world", 42))
                .isEmpty();
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
        entity.setLanguage(null);
        entity.setBuildSystem(null);
        entity.setVerificationCommand(null);
        entity.setStatusCommentId(null);
        entity.setStatusCommentUrl(null);
        return entity;
    }
}
