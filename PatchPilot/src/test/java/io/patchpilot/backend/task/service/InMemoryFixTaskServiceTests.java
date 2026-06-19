package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryFixTaskServiceTests {

    private final FixTaskService fixTaskService = new InMemoryFixTaskService();

    @Test
    void should_mark_task_running_and_completed() {
        FixTaskVo task = createTask("delivery-completed");

        FixTaskVo runningTask = fixTaskService.markRunning(task.id());
        FixTaskVo completedTask = fixTaskService.markCompleted(task.id());

        assertThat(task.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(runningTask.status()).isEqualTo(FixTaskStatus.RUNNING);
        assertThat(completedTask.status()).isEqualTo(FixTaskStatus.COMPLETED);
        assertThat(completedTask.failureReason()).isNull();
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
        assertThat(fixTaskService.findTask(task.id()))
                .get()
                .extracting(FixTaskVo::failureReason)
                .isEqualTo("executor failed");
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
