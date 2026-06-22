package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.ApproveReviewCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.config.ReviewApprovalProperties;
import io.patchpilot.backend.task.process.TaskProcessRegistry;
import io.patchpilot.backend.task.service.impl.DefaultFixTaskControlService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultFixTaskControlServiceTests {

    private final InMemoryFixTaskService fixTaskService = new InMemoryFixTaskService();
    private final RecordingFixTaskQueue fixTaskQueue = new RecordingFixTaskQueue();
    private final RecordingTimelineService fixTaskTimelineService = new RecordingTimelineService();
    private final RecordingTaskProcessRegistry taskProcessRegistry = new RecordingTaskProcessRegistry();
    private final FixTaskControlService controlService = new DefaultFixTaskControlService(
            fixTaskService,
            fixTaskQueue,
            fixTaskTimelineService,
            taskProcessRegistry,
            reviewApprovalProperties(List.of("release-captain"))
    );

    @Test
    void should_cancel_pending_task_and_pending_queue_items() {
        FixTaskVo task = createTask("delivery-control-cancel");

        FixTaskVo cancelledTask = controlService.cancelTask(task.id());

        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(cancelledTask.failureReason()).isEqualTo("Task cancelled by user request");
        assertThat(fixTaskQueue.cancelledTaskIds()).containsExactly(task.id());
        assertThat(taskProcessRegistry.cancelledTaskIds()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.CANCELLED);
        assertThat(fixTaskTimelineService.messages()).containsExactly("Task cancelled by user request");
    }

    @Test
    void should_cancel_running_task_without_cancelling_queue_item() {
        FixTaskVo task = createTask("delivery-control-cancel-running");
        fixTaskService.markRunning(task.id());

        FixTaskVo cancelledTask = controlService.cancelTask(task.id());

        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(fixTaskQueue.cancelledTaskIds()).isEmpty();
        assertThat(taskProcessRegistry.cancelledTaskIds()).containsExactly(task.id());
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.CANCELLED);
    }

    @Test
    void should_cancel_running_tests_task_without_cancelling_queue_item() {
        FixTaskVo task = createTask("delivery-control-cancel-running-tests");
        fixTaskService.markRunningTests(task.id());

        FixTaskVo cancelledTask = controlService.cancelTask(task.id());

        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(fixTaskQueue.cancelledTaskIds()).isEmpty();
        assertThat(taskProcessRegistry.cancelledTaskIds()).containsExactly(task.id());
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.CANCELLED);
    }

    @Test
    void should_cancel_pending_review_task_without_cancelling_queue_item_or_process() {
        FixTaskVo task = createTask("delivery-control-cancel-pending-review");
        fixTaskService.markPendingReview(task.id(), "Generated diff rejected: sensitive path .env");

        FixTaskVo cancelledTask = controlService.cancelTask(task.id());

        assertThat(cancelledTask.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(fixTaskQueue.cancelledTaskIds()).isEmpty();
        assertThat(taskProcessRegistry.cancelledTaskIds()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.CANCELLED);
    }

    @Test
    void should_reject_cancelling_terminal_task() {
        FixTaskVo task = createTask("delivery-control-cancel-terminal");
        fixTaskService.markCompleted(task.id(), "https://github.com/octocat/hello-world/pull/7");

        assertThatThrownBy(() -> controlService.cancelTask(task.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only active tasks can be cancelled");
    }

    @Test
    void should_retry_failed_task_and_enqueue_it() {
        FixTaskVo task = createTask("delivery-control-retry");
        fixTaskService.markFailed(task.id(), "executor failed");

        FixTaskVo retriedTask = controlService.retryTask(task.id());

        assertThat(retriedTask.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(retriedTask.failureReason()).isNull();
        assertThat(fixTaskQueue.enqueuedTaskIds()).containsExactly(task.id());
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.REQUEUED);
        assertThat(fixTaskTimelineService.messages()).containsExactly("Task requeued by user request");
    }

    @Test
    void should_reject_retrying_active_task() {
        FixTaskVo task = createTask("delivery-control-retry-active");

        assertThatThrownBy(() -> controlService.retryTask(task.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only failed or cancelled tasks can be retried");

        assertThat(fixTaskQueue.enqueuedTaskIds()).isEmpty();
    }

    @Test
    void should_reject_retrying_pending_review_task_until_human_decision_exists() {
        FixTaskVo task = createTask("delivery-control-retry-pending-review");
        fixTaskService.markPendingReview(task.id(), "Generated diff rejected: binary file change");

        assertThatThrownBy(() -> controlService.retryTask(task.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Pending review tasks must be cancelled or approved before retry");

        assertThat(fixTaskQueue.enqueuedTaskIds()).isEmpty();
    }

    @Test
    void should_approve_pending_review_task_and_enqueue_existing_workspace_resume() {
        FixTaskVo task = createTask("delivery-control-approve-review");
        fixTaskService.markPendingReview(task.id(), "Generated diff rejected: sensitive path .env");

        FixTaskVo approvedTask = controlService.approveReviewTask(
                task.id(),
                new ApproveReviewCommand("release-captain", "Reviewed generated diff and accepted docs-only change")
        );

        assertThat(approvedTask.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(approvedTask.failureReason()).isNull();
        assertThat(approvedTask.riskReviewApprovedAt()).isNotNull();
        assertThat(approvedTask.riskReviewApprovedBy()).isEqualTo("release-captain");
        assertThat(approvedTask.riskReviewApprovalReason())
                .isEqualTo("Reviewed generated diff and accepted docs-only change");
        assertThat(fixTaskQueue.enqueuedTaskIds()).containsExactly(task.id());
        assertThat(fixTaskQueue.cancelledTaskIds()).isEmpty();
        assertThat(taskProcessRegistry.cancelledTaskIds()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.REVIEW_APPROVED);
        assertThat(fixTaskTimelineService.messages()).containsExactly(
                "Pending review approved by release-captain: Reviewed generated diff and accepted docs-only change"
        );
    }

    @Test
    void should_reject_pending_review_approval_from_operator_outside_allowlist() {
        FixTaskVo task = createTask("delivery-control-approve-review-denied");
        fixTaskService.markPendingReview(task.id(), "Generated diff rejected: sensitive path .env");

        assertThatThrownBy(() -> controlService.approveReviewTask(
                task.id(),
                new ApproveReviewCommand("unknown-operator", "Reviewed generated diff")
        ))
                .isInstanceOf(SecurityException.class)
                .hasMessage("operator is not allowed to approve risk reviews");

        assertThat(fixTaskService.findTask(task.id())).hasValueSatisfying(storedTask -> {
            assertThat(storedTask.status()).isEqualTo(FixTaskStatus.PENDING_REVIEW);
            assertThat(storedTask.riskReviewApprovedBy()).isNull();
            assertThat(storedTask.riskReviewApprovalReason()).isNull();
        });
        assertThat(fixTaskQueue.enqueuedTaskIds()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).isEmpty();
    }

    @Test
    void should_reject_approving_non_pending_review_task() {
        FixTaskVo task = createTask("delivery-control-approve-active");

        assertThatThrownBy(() -> controlService.approveReviewTask(
                task.id(),
                new ApproveReviewCommand("release-captain", "Reviewed generated diff")
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only pending review tasks can be approved");

        assertThat(fixTaskQueue.enqueuedTaskIds()).isEmpty();
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

    private static ReviewApprovalProperties reviewApprovalProperties(List<String> allowedOperators) {
        ReviewApprovalProperties properties = new ReviewApprovalProperties();
        properties.setAllowedOperators(allowedOperators);
        return properties;
    }

    private static final class RecordingFixTaskQueue implements FixTaskQueue {

        private final List<String> enqueuedTaskIds = new CopyOnWriteArrayList<>();
        private final List<String> cancelledTaskIds = new CopyOnWriteArrayList<>();

        @Override
        public void enqueue(String taskId) {
            enqueuedTaskIds.add(taskId);
        }

        @Override
        public int cancelPendingForTask(String taskId) {
            cancelledTaskIds.add(taskId);
            return 1;
        }

        private List<String> enqueuedTaskIds() {
            return enqueuedTaskIds;
        }

        private List<String> cancelledTaskIds() {
            return cancelledTaskIds;
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
                    Instant.parse("2026-06-20T00:00:00Z").plusSeconds(eventTypes.size())
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

    private static final class RecordingTaskProcessRegistry extends TaskProcessRegistry {

        private final List<String> cancelledTaskIds = new CopyOnWriteArrayList<>();

        @Override
        public boolean cancel(String taskId) {
            cancelledTaskIds.add(taskId);
            return true;
        }

        private List<String> cancelledTaskIds() {
            return cancelledTaskIds;
        }
    }
}
