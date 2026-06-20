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
import io.patchpilot.backend.task.executor.TaskCancellationException;
import io.patchpilot.backend.task.executor.domain.FixTaskExecutionResult;
import io.patchpilot.backend.task.service.impl.FixTaskWorker;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskWorkerTests {

    @Test
    void should_execute_task_and_mark_completed() {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskWorker worker = new FixTaskWorker(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-worker-completed");

        worker.execute(task.id());

        FixTaskVo completedTask = fixTaskService.findTask(task.id()).orElseThrow();
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
    void should_mark_running_tests_before_executor_runs() {
        RecordingFixTaskService fixTaskService = new RecordingFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskWorker worker = new FixTaskWorker(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-worker-running-tests");

        worker.execute(task.id());

        assertThat(fixTaskService.statuses())
                .containsSequence(FixTaskStatus.RUNNING, FixTaskStatus.RUNNING_TESTS, FixTaskStatus.COMPLETED);
        assertThat(executor.statusWhenExecuted()).isEqualTo(FixTaskStatus.RUNNING_TESTS);
    }

    @Test
    void should_mark_failed_when_executor_throws() {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        FailingExecutor executor = new FailingExecutor();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskWorker worker = new FixTaskWorker(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-worker-failed");

        worker.execute(task.id());

        FixTaskVo failedTask = fixTaskService.findTask(task.id()).orElseThrow();
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
    void should_truncate_long_failure_reason() {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        FailingExecutor executor = new FailingExecutor("x".repeat(10_000));
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskWorker worker = new FixTaskWorker(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-worker-long-failure");

        worker.execute(task.id());

        FixTaskVo failedTask = fixTaskService.findTask(task.id()).orElseThrow();
        assertThat(failedTask.status()).isEqualTo(FixTaskStatus.FAILED);
        assertThat(failedTask.failureReason()).hasSizeLessThanOrEqualTo(2_000);
        assertThat(failedTask.failureReason()).contains("[truncated ");
        assertThat(issueCommentTool.failureReasons()).contains(failedTask.failureReason());
        assertThat(timelineService.messages()).contains(failedTask.failureReason());
    }

    @Test
    void should_keep_cancelled_status_when_executor_observes_cancellation() {
        CancellingFixTaskService fixTaskService = new CancellingFixTaskService();
        CancellingExecutor executor = new CancellingExecutor();
        RecordingIssueCommentTool issueCommentTool = new RecordingIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskWorker worker = new FixTaskWorker(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-worker-cancelled");

        worker.execute(task.id());

        FixTaskVo cancelledTask = fixTaskService.findTask(task.id()).orElseThrow();
        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(cancelledTask.failureReason()).isEqualTo("Task cancelled by user request");
        assertThat(fixTaskService.statuses()).doesNotContain(FixTaskStatus.FAILED, FixTaskStatus.COMPLETED);
        assertThat(timelineService.eventTypes()).contains(FixTaskTimelineEventType.CANCELLED);
        assertThat(timelineService.eventTypes()).doesNotContain(FixTaskTimelineEventType.FAILED);
    }

    @Test
    void should_keep_completed_status_when_status_comment_update_fails() {
        FixTaskService fixTaskService = new InMemoryFixTaskService();
        RecordingExecutor executor = new RecordingExecutor();
        FailingUpdateIssueCommentTool issueCommentTool = new FailingUpdateIssueCommentTool();
        RecordingTimelineService timelineService = new RecordingTimelineService();
        FixTaskWorker worker = new FixTaskWorker(fixTaskService, executor, issueCommentTool, timelineService);
        FixTaskVo task = createTask(fixTaskService, "delivery-worker-comment-failed");

        worker.execute(task.id());

        assertThat(issueCommentTool.updated()).isTrue();
        FixTaskVo completedTask = fixTaskService.findTask(task.id()).orElseThrow();
        assertThat(completedTask.status()).isEqualTo(FixTaskStatus.COMPLETED);
        assertThat(completedTask.failureReason()).isNull();
        assertThat(timelineService.eventTypes()).contains(FixTaskTimelineEventType.STATUS_COMMENT_FAILED);
        assertThat(timelineService.messages()).contains("Status comment failed: comment update failed");
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

    private static final class RecordingExecutor implements FixTaskExecutor {

        private final AtomicReference<String> taskId = new AtomicReference<>();
        private final AtomicReference<FixTaskStatus> statusWhenExecuted = new AtomicReference<>();

        @Override
        public FixTaskExecutionResult execute(FixTaskVo task) {
            taskId.set(task.id());
            statusWhenExecuted.set(task.status());
            return new FixTaskExecutionResult("https://github.com/octocat/hello-world/pull/7");
        }

        private String taskId() {
            return taskId.get();
        }

        private FixTaskStatus statusWhenExecuted() {
            return statusWhenExecuted.get();
        }
    }

    private static final class FailingExecutor implements FixTaskExecutor {

        private final String message;

        private FailingExecutor() {
            this("executor failed");
        }

        private FailingExecutor(String message) {
            this.message = message;
        }

        @Override
        public FixTaskExecutionResult execute(FixTaskVo task) {
            throw new IllegalStateException(message);
        }
    }

    private static final class CancellingExecutor implements FixTaskExecutor {

        @Override
        public FixTaskExecutionResult execute(FixTaskVo task) {
            throw new TaskCancellationException(task.id());
        }
    }

    private static final class RecordingIssueCommentTool extends IssueCommentTool {

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
            return Optional.of(new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123"));
        }

        @Override
        public Optional<IssueCommentResult> updateFailed(FixTaskVo task) {
            record(task);
            failureReasons.add(task.failureReason());
            return Optional.of(new IssueCommentResult(123, "https://github.com/octocat/hello-world/issues/42#issuecomment-123"));
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

        private boolean updated() {
            try {
                return updateLatch.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    private static class RecordingFixTaskService extends InMemoryFixTaskService {

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

        protected List<FixTaskStatus> statuses() {
            return statuses;
        }
    }

    private static final class CancellingFixTaskService extends RecordingFixTaskService {

        @Override
        public FixTaskVo markRunningTests(String id) {
            FixTaskVo task = super.markRunningTests(id);
            FixTaskVo cancelledTask = super.markCancelled(id, "Task cancelled by user request");
            statuses().add(cancelledTask.status());
            return task;
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
