package io.patchpilot.backend.task.service;

import io.patchpilot.backend.agent.tool.IssueCommentTool;
import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.FixTaskExecutor;
import io.patchpilot.backend.task.executor.domain.FixTaskExecutionResult;
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
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor, issueCommentTool);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-completed");

        dispatcher.dispatch(task.id());

        assertThat(issueCommentTool.await()).isTrue();
        FixTaskVo completedTask = awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.COMPLETED);
        assertThat(executor.taskId()).isEqualTo(task.id());
        assertThat(completedTask.status()).isEqualTo(FixTaskStatus.COMPLETED);
        assertThat(issueCommentTool.completedTaskId()).isEqualTo(task.id());
        assertThat(issueCommentTool.pullRequestUrl()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
    }

    @Test
    void should_mark_running_tests_before_executor_runs() throws Exception {
        RecordingFixTaskService fixTaskService = new RecordingFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor, issueCommentTool);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-running-tests");

        dispatcher.dispatch(task.id());

        assertThat(issueCommentTool.await()).isTrue();
        awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.COMPLETED);
        assertThat(fixTaskService.statuses())
                .containsSequence(FixTaskStatus.RUNNING, FixTaskStatus.RUNNING_TESTS, FixTaskStatus.COMPLETED);
        assertThat(executor.statusWhenExecuted()).isEqualTo(FixTaskStatus.RUNNING_TESTS);
    }

    @Test
    void should_mark_failed_when_executor_throws() throws Exception {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        FailingExecutor executor = new FailingExecutor();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor, issueCommentTool);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-failed");

        dispatcher.dispatch(task.id());

        assertThat(issueCommentTool.await()).isTrue();
        FixTaskVo failedTask = awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.FAILED);
        assertThat(failedTask.status()).isEqualTo(FixTaskStatus.FAILED);
        assertThat(failedTask.failureReason()).isEqualTo("executor failed");
        assertThat(issueCommentTool.failedTaskId()).isEqualTo(task.id());
        assertThat(issueCommentTool.failureReason()).isEqualTo("executor failed");
    }

    @Test
    void should_keep_completed_status_when_completion_comment_fails() throws Exception {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        FailingCompletionIssueCommentTool issueCommentTool = new FailingCompletionIssueCommentTool();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor, issueCommentTool);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-comment-failed");

        dispatcher.dispatch(task.id());

        assertThat(issueCommentTool.awaitCompletion()).isTrue();
        assertThat(issueCommentTool.awaitFailureComment()).isFalse();
        assertThat(fixTaskService.findTask(task.id()).orElseThrow().status()).isEqualTo(FixTaskStatus.COMPLETED);
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
        public FixTaskExecutionResult execute(FixTaskVo task) {
            taskId.set(task.id());
            statusWhenExecuted.set(task.status());
            latch.countDown();
            return new FixTaskExecutionResult("https://github.com/octocat/hello-world/pull/7");
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
        public FixTaskExecutionResult execute(FixTaskVo task) {
            latch.countDown();
            throw new IllegalStateException("executor failed");
        }

        private boolean await() throws InterruptedException {
            return latch.await(3, TimeUnit.SECONDS);
        }
    }

    private static final class RecordingIssueCommentTool extends IssueCommentTool {

        private final CountDownLatch latch = new CountDownLatch(1);
        private final AtomicReference<String> completedTaskId = new AtomicReference<>();
        private final AtomicReference<String> pullRequestUrl = new AtomicReference<>();
        private final AtomicReference<String> failedTaskId = new AtomicReference<>();
        private final AtomicReference<String> failureReason = new AtomicReference<>();

        private RecordingIssueCommentTool() {
            super(new GitHubIssueCommentClient(new GitHubProperties()) {
                @Override
                public IssueCommentResult createIssueComment(CreateIssueCommentCommand command) {
                    return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
                }
            });
        }

        @Override
        public IssueCommentResult commentCompleted(FixTaskVo task, String pullRequestUrl) {
            this.completedTaskId.set(task.id());
            this.pullRequestUrl.set(pullRequestUrl);
            latch.countDown();
            return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        }

        @Override
        public IssueCommentResult commentFailed(FixTaskVo task, String failureReason) {
            this.failedTaskId.set(task.id());
            this.failureReason.set(failureReason);
            latch.countDown();
            return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        }

        private boolean await() throws InterruptedException {
            return latch.await(3, TimeUnit.SECONDS);
        }

        private String completedTaskId() {
            return completedTaskId.get();
        }

        private String pullRequestUrl() {
            return pullRequestUrl.get();
        }

        private String failedTaskId() {
            return failedTaskId.get();
        }

        private String failureReason() {
            return failureReason.get();
        }
    }

    private static final class FailingCompletionIssueCommentTool extends IssueCommentTool {

        private final CountDownLatch completionLatch = new CountDownLatch(1);
        private final CountDownLatch failureCommentLatch = new CountDownLatch(1);

        private FailingCompletionIssueCommentTool() {
            super(new GitHubIssueCommentClient(new GitHubProperties()) {
                @Override
                public IssueCommentResult createIssueComment(CreateIssueCommentCommand command) {
                    return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
                }
            });
        }

        @Override
        public IssueCommentResult commentCompleted(FixTaskVo task, String pullRequestUrl) {
            completionLatch.countDown();
            throw new IllegalStateException("comment failed");
        }

        @Override
        public IssueCommentResult commentFailed(FixTaskVo task, String failureReason) {
            failureCommentLatch.countDown();
            return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        }

        private boolean awaitCompletion() throws InterruptedException {
            return completionLatch.await(3, TimeUnit.SECONDS);
        }

        private boolean awaitFailureComment() throws InterruptedException {
            return failureCommentLatch.await(250, TimeUnit.MILLISECONDS);
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
