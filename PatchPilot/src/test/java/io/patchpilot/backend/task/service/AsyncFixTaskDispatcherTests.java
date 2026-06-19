package io.patchpilot.backend.task.service;

import io.patchpilot.backend.agent.tool.IssueCommentTool;
import io.patchpilot.backend.github.client.GitHubIssueCommentClient;
import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.client.domain.UpdateIssueCommentCommand;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncFixTaskDispatcherTests {

    @Test
    void should_execute_task_and_mark_completed() throws Exception {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-completed");

        dispatcher.dispatch(task.id());

        assertThat(issueCommentTool.await()).isTrue();
        FixTaskVo completedTask = awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.COMPLETED);
        assertThat(executor.taskId()).isEqualTo(task.id());
        assertThat(completedTask.status()).isEqualTo(FixTaskStatus.COMPLETED);
        assertThat(completedTask.pullRequestUrl()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(completedTask.completedAt()).isNotNull();
        assertThat(issueCommentTool.updatedStatuses())
                .containsSequence(FixTaskStatus.RUNNING, FixTaskStatus.RUNNING_TESTS, FixTaskStatus.COMPLETED);
        assertThat(issueCommentTool.updatedTaskIds()).contains(task.id());
        assertThat(timelineService.eventTypes())
                .containsSequence(
                        FixTaskTimelineEventType.RUNNING,
                        FixTaskTimelineEventType.RUNNING_TESTS,
                        FixTaskTimelineEventType.PR_CREATED,
                        FixTaskTimelineEventType.COMPLETED
                );
    }

    @Test
    void should_mark_running_tests_before_executor_runs() throws Exception {
        RecordingFixTaskService fixTaskService = new RecordingFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor, issueCommentTool, timelineService);
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
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-failed");

        dispatcher.dispatch(task.id());

        assertThat(issueCommentTool.await()).isTrue();
        FixTaskVo failedTask = awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.FAILED);
        assertThat(failedTask.status()).isEqualTo(FixTaskStatus.FAILED);
        assertThat(failedTask.failureReason()).isEqualTo("executor failed");
        assertThat(issueCommentTool.updatedStatuses())
                .containsSequence(FixTaskStatus.RUNNING, FixTaskStatus.RUNNING_TESTS, FixTaskStatus.FAILED);
        assertThat(issueCommentTool.failureReasons()).contains("executor failed");
        assertThat(timelineService.eventTypes())
                .containsSequence(
                        FixTaskTimelineEventType.RUNNING,
                        FixTaskTimelineEventType.RUNNING_TESTS,
                        FixTaskTimelineEventType.FAILED
                );
        assertThat(timelineService.messages()).contains("executor failed");
    }

    @Test
    void should_keep_completed_status_when_status_comment_update_fails() throws Exception {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        FailingUpdateIssueCommentTool issueCommentTool = new FailingUpdateIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskDispatcher dispatcher = new AsyncFixTaskDispatcher(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-dispatch-comment-failed");

        dispatcher.dispatch(task.id());

        assertThat(issueCommentTool.awaitUpdate()).isTrue();
        FixTaskVo completedTask = awaitTaskStatus(fixTaskService, task.id(), FixTaskStatus.COMPLETED);
        assertThat(completedTask.status()).isEqualTo(FixTaskStatus.COMPLETED);
        assertThat(completedTask.failureReason()).isNull();
    }

    private static FixTaskVo createTask(FixTaskService fixTaskService, String deliveryId) {
        FixTaskVo task = fixTaskService.createFixTask(new CreateFixTaskCommand(
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                deliveryId,
                98765
        ));
        return fixTaskService.attachStatusComment(
                task.id(),
                123,
                "https://github.com/octocat/hello-world/issues/42#issuecomment-123"
        );
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
        private final List<FixTaskStatus> updatedStatuses = new CopyOnWriteArrayList<>();
        private final List<String> updatedTaskIds = new CopyOnWriteArrayList<>();
        private final List<String> failureReasons = new CopyOnWriteArrayList<>();

        private RecordingIssueCommentTool() {
            super(new GitHubIssueCommentClient(new GitHubProperties()) {
                @Override
                public IssueCommentResult createIssueComment(CreateIssueCommentCommand command) {
                    return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
                }

                @Override
                public IssueCommentResult updateIssueComment(UpdateIssueCommentCommand command) {
                    return new IssueCommentResult(command.commentId(), "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
                }
            });
        }

        @Override
        public Optional<IssueCommentResult> updateRunning(FixTaskVo task) {
            record(task);
            return Optional.of(new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123"));
        }

        @Override
        public Optional<IssueCommentResult> updateRunningTests(FixTaskVo task) {
            record(task);
            return Optional.of(new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123"));
        }

        @Override
        public Optional<IssueCommentResult> updateCompleted(FixTaskVo task) {
            record(task);
            latch.countDown();
            return Optional.of(new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123"));
        }

        @Override
        public Optional<IssueCommentResult> updateFailed(FixTaskVo task) {
            record(task);
            failureReasons.add(task.failureReason());
            latch.countDown();
            return Optional.of(new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123"));
        }

        private boolean await() throws InterruptedException {
            return latch.await(3, TimeUnit.SECONDS);
        }

        private List<FixTaskStatus> updatedStatuses() {
            return updatedStatuses;
        }

        private List<String> updatedTaskIds() {
            return updatedTaskIds;
        }

        private List<String> failureReasons() {
            return failureReasons;
        }

        private void record(FixTaskVo task) {
            updatedStatuses.add(task.status());
            updatedTaskIds.add(task.id());
        }
    }

    private static final class FailingUpdateIssueCommentTool extends IssueCommentTool {

        private final CountDownLatch updateLatch = new CountDownLatch(1);

        private FailingUpdateIssueCommentTool() {
            super(new GitHubIssueCommentClient(new GitHubProperties()) {
                @Override
                public IssueCommentResult createIssueComment(CreateIssueCommentCommand command) {
                    return new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123");
                }
            });
        }

        @Override
        public Optional<IssueCommentResult> updateRunning(FixTaskVo task) {
            return Optional.of(new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123"));
        }

        @Override
        public Optional<IssueCommentResult> updateRunningTests(FixTaskVo task) {
            return Optional.of(new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123"));
        }

        @Override
        public Optional<IssueCommentResult> updateCompleted(FixTaskVo task) {
            updateLatch.countDown();
            throw new IllegalStateException("comment update failed");
        }

        private boolean awaitUpdate() throws InterruptedException {
            return updateLatch.await(3, TimeUnit.SECONDS);
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
        public FixTaskVo markCompleted(String id, String pullRequestUrl) {
            FixTaskVo task = super.markCompleted(id, pullRequestUrl);
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

    private static final class RecordingTimelineService implements FixTaskTimelineService {

        private final List<FixTaskTimelineEventType> eventTypes = new CopyOnWriteArrayList<>();
        private final List<String> messages = new CopyOnWriteArrayList<>();

        @Override
        public FixTaskTimelineEventVo recordEvent(String taskId, FixTaskTimelineEventType eventType, String message) {
            eventTypes.add(eventType);
            messages.add(message);
            return new FixTaskTimelineEventVo(
                    "event-" + eventTypes.size(),
                    taskId,
                    eventType,
                    message,
                    java.time.Instant.parse("2026-06-19T08:00:00Z").plusSeconds(eventTypes.size())
            );
        }

        @Override
        public List<FixTaskTimelineEventVo> listEvents(String taskId) {
            return List.of();
        }

        private List<FixTaskTimelineEventType> eventTypes() {
            return eventTypes;
        }

        private List<String> messages() {
            return messages;
        }
    }
}
