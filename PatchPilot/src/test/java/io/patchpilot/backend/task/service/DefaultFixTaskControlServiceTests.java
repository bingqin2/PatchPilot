package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.ApproveReviewCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskRetryPreflightVo;
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
        assertThat(retriedTask.retrySourceTaskId()).isEqualTo(task.id());
        assertThat(retriedTask.retrySourceStatus()).isEqualTo(FixTaskStatus.FAILED.name());
        assertThat(retriedTask.retrySourceFailureReason()).isEqualTo("executor failed");
        assertThat(retriedTask.retriedAt()).isNotNull();
        assertThat(fixTaskQueue.enqueuedTaskIds()).containsExactly(task.id());
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.REQUEUED);
        assertThat(fixTaskTimelineService.messages()).containsExactly("Task requeued by user request");
    }

    @Test
    void should_retry_patch_review_rejected_task_with_recovery_lineage() {
        FixTaskVo task = createTask("delivery-control-retry-review-rejection");
        String failureReason = "Model patch review rejected generated edits: unrelated authentication change";
        fixTaskService.markFailed(task.id(), failureReason);

        FixTaskVo retriedTask = controlService.retryTask(task.id());

        assertThat(retriedTask.status()).isEqualTo(FixTaskStatus.PENDING);
        assertThat(retriedTask.failureReason()).isNull();
        assertThat(retriedTask.retrySourceTaskId()).isEqualTo(task.id());
        assertThat(retriedTask.retrySourceStatus()).isEqualTo(FixTaskStatus.FAILED.name());
        assertThat(retriedTask.retrySourceFailureReason()).isEqualTo(failureReason);
        assertThat(retriedTask.retriedAt()).isNotNull();
        assertThat(fixTaskQueue.enqueuedTaskIds()).containsExactly(task.id());
        assertThat(fixTaskTimelineService.eventTypes()).containsExactly(FixTaskTimelineEventType.REQUEUED);
    }

    @Test
    void should_return_retry_preflight_for_verification_failure() {
        FixTaskVo task = createTask("delivery-control-retry-preflight-verification");
        fixTaskService.markFailed(task.id(), "maven tests failed: token=ghp_123456789012345678901234567890123456");

        FixTaskRetryPreflightVo preflight = controlService.retryPreflight(task.id());

        assertThat(preflight.taskId()).isEqualTo(task.id());
        assertThat(preflight.status()).isEqualTo(FixTaskStatus.FAILED);
        assertThat(preflight.retryable()).isTrue();
        assertThat(preflight.category()).isEqualTo("VERIFICATION_FAILED");
        assertThat(preflight.reason()).isEqualTo("maven tests failed: token=[REDACTED]");
        assertThat(preflight.operatorAction())
                .isEqualTo("Inspect the verification output, fix the failing test or build error, then retry the task.");
    }

    @Test
    void should_block_retry_preflight_for_github_operation_failure() {
        FixTaskVo task = createTask("delivery-control-retry-preflight-github");
        fixTaskService.markFailed(
                task.id(),
                "GitHub token is required to create Pull Requests: token=ghp_123456789012345678901234567890123456"
        );

        FixTaskRetryPreflightVo preflight = controlService.retryPreflight(task.id());

        assertThat(preflight.taskId()).isEqualTo(task.id());
        assertThat(preflight.status()).isEqualTo(FixTaskStatus.FAILED);
        assertThat(preflight.retryable()).isFalse();
        assertThat(preflight.category()).isEqualTo("GITHUB_OPERATION_FAILED");
        assertThat(preflight.reason()).isEqualTo("GitHub token is required to create Pull Requests: token=[REDACTED]");
        assertThat(preflight.operatorAction())
                .isEqualTo("Check GitHub token or App permissions, then retry the task after access is fixed.");

        assertThatThrownBy(() -> controlService.retryTask(task.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Check GitHub token or App permissions, then retry the task after access is fixed.");
        assertThat(fixTaskQueue.enqueuedTaskIds()).isEmpty();
        assertThat(fixTaskTimelineService.eventTypes()).isEmpty();
    }

    @Test
    void should_return_retry_preflight_for_cancelled_task() {
        FixTaskVo task = createTask("delivery-control-retry-preflight-cancelled");
        fixTaskService.markCancelled(task.id(), "Task cancelled by user request");

        FixTaskRetryPreflightVo preflight = controlService.retryPreflight(task.id());

        assertThat(preflight.taskId()).isEqualTo(task.id());
        assertThat(preflight.status()).isEqualTo(FixTaskStatus.CANCELLED);
        assertThat(preflight.retryable()).isTrue();
        assertThat(preflight.category()).isEqualTo("CANCELLED");
        assertThat(preflight.reason()).isEqualTo("Task was cancelled before completion.");
        assertThat(preflight.operatorAction()).isEqualTo("Retry creates a fresh pending task from the same issue request.");
    }

    @Test
    void should_reject_retrying_active_task() {
        FixTaskVo task = createTask("delivery-control-retry-active");

        FixTaskRetryPreflightVo preflight = controlService.retryPreflight(task.id());
        assertThat(preflight.retryable()).isFalse();
        assertThat(preflight.category()).isEqualTo("PENDING");
        assertThat(preflight.operatorAction()).isEqualTo("Only failed or cancelled tasks can be retried");

        assertThatThrownBy(() -> controlService.retryTask(task.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only failed or cancelled tasks can be retried");

        assertThat(fixTaskQueue.enqueuedTaskIds()).isEmpty();
    }

    @Test
    void should_reject_retrying_pending_review_task_until_human_decision_exists() {
        FixTaskVo task = createTask("delivery-control-retry-pending-review");
        fixTaskService.markPendingReview(task.id(), "Generated diff rejected: binary file change");

        FixTaskRetryPreflightVo preflight = controlService.retryPreflight(task.id());
        assertThat(preflight.retryable()).isFalse();
        assertThat(preflight.category()).isEqualTo("PENDING_REVIEW");
        assertThat(preflight.operatorAction())
                .isEqualTo("Pending review tasks must be cancelled or approved before retry");

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
