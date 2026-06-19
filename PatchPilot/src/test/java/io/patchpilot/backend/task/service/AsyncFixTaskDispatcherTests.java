package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.FixTaskExecutor;
import io.patchpilot.backend.task.service.impl.AsyncFixTaskDispatcher;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncFixTaskDispatcherTests {

    @Test
    void should_execute_task_and_mark_completed() throws Exception {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-completed");

        dispatcher.dispatch(task.id());

        assertThat(executor.await()).isTrue();
        FixTaskVo completedTask = awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.COMPLETED);
        assertThat(executor.taskId()).isEqualTo(task.id());
        assertThat(completedTask.status()).isEqualTo(FixTaskStatus.COMPLETED);
    }

    @Test
    void should_mark_running_tests_before_executor_runs() throws Exception {
        RecordingFixTaskService fixTaskService = new RecordingFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-running-tests");

        dispatcher.dispatch(task.id());

        assertThat(executor.await()).isTrue();
        awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.COMPLETED);
        assertThat(fixTaskService.statuses())
                .containsSequence(FixTaskStatus.RUNNING, FixTaskStatus.RUNNING_TESTS, FixTaskStatus.COMPLETED);
        assertThat(executor.statusWhenExecuted()).isEqualTo(FixTaskStatus.RUNNING_TESTS);
    }

    @Test
    void should_mark_failed_when_executor_throws() throws Exception {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        FailingExecutor executor = new FailingExecutor();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-failed");

        dispatcher.dispatch(task.id());

        assertThat(executor.await()).isTrue();
        FixTaskVo failedTask = awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.FAILED);
        assertThat(failedTask.status()).isEqualTo(FixTaskStatus.FAILED);
        assertThat(failedTask.failureReason()).isEqualTo("executor failed");
    }

    private static FixTaskVo createTask(FixTaskService fixTaskService, String deliveryId) {
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

    private static FixTaskVo awaitTaskStatus(
            FixTaskService fixTaskService,
            String taskId,
            FixTaskStatus expectedStatus
    ) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
        while (System.nanoTime() < deadline) {
            FixTaskVo task = fixTaskService.findTask(taskId).orElseThrow();
            if (task.status() == expectedStatus) {
                return task;
            }
            Thread.sleep(25);
        }
        return fixTaskService.findTask(taskId).orElseThrow();
    }

    private static final class RecordingExecutor implements FixTaskExecutor {

        private final CountDownLatch latch = new CountDownLatch(1);
        private final AtomicReference<String> taskId = new AtomicReference<>();
        private final AtomicReference<FixTaskStatus> statusWhenExecuted = new AtomicReference<>();

        @Override
        public void execute(FixTaskVo task) {
            taskId.set(task.id());
            statusWhenExecuted.set(task.status());
            latch.countDown();
        }

        private boolean await() throws InterruptedException {
            return latch.await(3, TimeUnit.SECONDS);
        }

        private String taskId() {
            return taskId.get();
        }

        private FixTaskStatus statusWhenExecuted() {
            return statusWhenExecuted.get();
        }
    }

    private static final class FailingExecutor implements FixTaskExecutor {

        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void execute(FixTaskVo task) {
            latch.countDown();
            throw new IllegalStateException("executor failed");
        }

        private boolean await() throws InterruptedException {
            return latch.await(3, TimeUnit.SECONDS);
        }
    }

    private static final class RecordingFixTaskService extends InMemoryFixTaskService {

        private final List<FixTaskStatus> statuses = new CopyOnWriteArrayList<>();

        @Override
        public FixTaskVo markRunning(String id) {
            FixTaskVo task = super.markRunning(id);
            statuses.add(task.status());
            return task;
        }

        @Override
        public FixTaskVo markRunningTests(String id) {
            FixTaskVo task = super.markRunningTests(id);
            statuses.add(task.status());
            return task;
        }

        @Override
        public FixTaskVo markCompleted(String id) {
            FixTaskVo task = super.markCompleted(id);
            statuses.add(task.status());
            return task;
        }

        @Override
        public FixTaskVo markFailed(String id, String failureReason) {
            FixTaskVo task = super.markFailed(id, failureReason);
            statuses.add(task.status());
            return task;
        }

        private List<FixTaskStatus> statuses() {
            return statuses;
        }
    }
}
