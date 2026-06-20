package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryFixTaskServiceTests {

    private final FixTaskService fixTaskService = new InMemoryFixTaskService();

    @Test
    void should_mark_task_running_and_completed() {
        FixTaskVo task = createTask("delivery-completed");

        FixTaskVo runningTask = fixTaskService.markRunning(task.id());
        FixTaskVo completedTask = fixTaskService.markCompleted(
                task.id(),
                "https://github.com/octocat/hello-world/pull/7"
        );

        assertThat(task.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(runningTask.status()).isEqualTo(FixTaskStatus.RUNNING);
        assertThat(completedTask.status()).isEqualTo(FixTaskStatus.COMPLETED);
        assertThat(completedTask.failureReason()).isNull();
        assertThat(completedTask.pullRequestUrl()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(completedTask.completedAt()).isNotNull();
        assertThat(completedTask.updatedAt()).isEqualTo(completedTask.completedAt());
        assertThat(fixTaskService.findTask(task.id()))
                .get()
                .extracting(FixTaskVo::status)
                .isEqualTo(FixTaskStatus.COMPLETED);
    }

    @Test
    void should_mark_task_running_tests() {
        FixTaskVo task = createTask("delivery-running-tests");

        FixTaskVo runningTestsTask = fixTaskService.markRunningTests(task.id());

        assertThat(runningTestsTask.status()).isEqualTo(FixTaskStatus.RUNNING_TESTS);
        assertThat(runningTestsTask.failureReason()).isNull();
        assertThat(runningTestsTask.updatedAt()).isAfterOrEqualTo(task.updatedAt());
        assertThat(fixTaskService.findTask(task.id()))
                .get()
                .extracting(FixTaskVo::status)
                .isEqualTo(FixTaskStatus.RUNNING_TESTS);
    }

    @Test
    void should_mark_task_failed_with_reason() {
        FixTaskVo task = createTask("delivery-failed");

        FixTaskVo failedTask = fixTaskService.markFailed(task.id(), "executor failed");

        assertThat(failedTask.status()).isEqualTo(FixTaskStatus.FAILED);
        assertThat(failedTask.failureReason()).isEqualTo("executor failed");
        assertThat(failedTask.updatedAt()).isAfterOrEqualTo(task.updatedAt());
        assertThat(fixTaskService.findTask(task.id()))
                .get()
                .extracting(FixTaskVo::failureReason)
                .isEqualTo("executor failed");
    }

    @Test
    void should_mark_pending_task_cancelled() {
        FixTaskVo task = createTask("delivery-cancelled");

        FixTaskVo cancelledTask = fixTaskService.markCancelled(task.id(), "cancelled by user");

        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(cancelledTask.failureReason()).isEqualTo("cancelled by user");
        assertThat(cancelledTask.updatedAt()).isAfterOrEqualTo(task.updatedAt());
        assertThat(fixTaskService.findTask(task.id()))
                .get()
                .extracting(FixTaskVo::status)
                .isEqualTo(FixTaskStatus.CANCELLED);
    }

    @Test
    void should_mark_terminal_task_pending_for_retry() {
        FixTaskVo task = createTask("delivery-retry");
        fixTaskService.markFailed(task.id(), "executor failed");

        FixTaskVo retriedTask = fixTaskService.markPendingForRetry(task.id());

        assertThat(retriedTask.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(retriedTask.failureReason()).isNull();
        assertThat(retriedTask.pullRequestUrl()).isNull();
        assertThat(retriedTask.completedAt()).isNull();
        assertThat(fixTaskService.findTask(task.id()))
                .get()
                .extracting(FixTaskVo::status)
                .isEqualTo(FixTaskStatus.PENDING);
    }

    @Test
    void should_attach_status_comment_metadata() {
        FixTaskVo task = createTask("delivery-status-comment");

        FixTaskVo updatedTask = fixTaskService.attachStatusComment(
                task.id(),
                123,
                "https://github.com/octocat/hello-world/issues/42#issuecomment-123"
        );

        assertThat(updatedTask.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(updatedTask.statusCommentId()).isEqualTo(123L);
        assertThat(updatedTask.statusCommentUrl()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(updatedTask.updatedAt()).isAfterOrEqualTo(task.updatedAt());
        assertThat(fixTaskService.findTask(task.id()))
                .get()
                .extracting(FixTaskVo::statusCommentId)
                .isEqualTo(123L);
    }

    @Test
    void should_find_active_task_for_issue() {
        FixTaskVo task = createTask("delivery-active");

        assertThat(fixTaskService.findActiveTaskForIssue("octocat", "hello-world", 42))
                .get()
                .extracting(FixTaskVo::id)
                .isEqualTo(task.id());
    }

    @Test
    void should_find_task_by_delivery_id() {
        FixTaskVo task = createTask("delivery-lookup");

        assertThat(fixTaskService.findTaskByDeliveryId("delivery-lookup"))
                .get()
                .extracting(FixTaskVo::id)
                .isEqualTo(task.id());
    }

    @Test
    void should_list_tasks_with_query_status_limit_and_offset() {
        FixTaskVo olderMatchingTask = createTask("delivery-query-older");
        FixTaskVo newerMatchingTask = createTask("delivery-query-newer");
        FixTaskVo skippedTask = createTask("delivery-query-skipped");
        fixTaskService.markFailed(olderMatchingTask.id(), "maven failed because search target");
        fixTaskService.markFailed(newerMatchingTask.id(), "maven failed because search target");
        fixTaskService.markFailed(skippedTask.id(), "different failure");

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
                .containsExactly(olderMatchingTask.id());
    }

    @Test
    void should_count_tasks_before_limit_and_offset() {
        createTask("delivery-count-older");
        createTask("delivery-count-newer");
        FixTaskVo skippedTask = createTask("delivery-count-skipped");
        fixTaskService.markFailed(skippedTask.id(), "different failure");

        long total = fixTaskService.countTasks(new FixTaskListQuery(
                "delivery-count",
                FixTaskStatus.PENDING,
                "octocat",
                "hello-world",
                1,
                1
        ));

        assertThat(total).isEqualTo(2);
    }

    @Test
    void should_ignore_completed_task_when_finding_active_task_for_issue() {
        FixTaskVo task = createTask("delivery-completed-active-lookup");
        fixTaskService.markCompleted(task.id(), "https://github.com/octocat/hello-world/pull/7");

        assertThat(fixTaskService.findActiveTaskForIssue("octocat", "hello-world", 42))
                .isEmpty();
    }

    @Test
    void should_reject_status_transition_for_missing_task() {
        assertThatThrownBy(() -> fixTaskService.markRunning("missing-task"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task not found: missing-task");
    }

    private FixTaskVo createTask(String deliveryId) {
        return fixTaskService.createFixTask(new CreateFixTaskCommand(
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                deliveryId,
                98765
        ));
    }
}
