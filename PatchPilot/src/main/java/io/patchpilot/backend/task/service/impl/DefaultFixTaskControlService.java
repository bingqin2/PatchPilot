package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.config.ReviewApprovalProperties;
import io.patchpilot.backend.task.domain.bo.ApproveReviewCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.process.TaskProcessRegistry;
import io.patchpilot.backend.task.service.FixTaskControlService;
import io.patchpilot.backend.task.service.FixTaskQueue;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import org.springframework.stereotype.Service;

@Service
public class DefaultFixTaskControlService implements FixTaskControlService {

    private static final String CANCELLATION_REASON = "Task cancelled by user request";

    private final FixTaskService fixTaskService;
    private final FixTaskQueue fixTaskQueue;
    private final FixTaskTimelineService fixTaskTimelineService;
    private final TaskProcessRegistry taskProcessRegistry;
    private final ReviewApprovalProperties reviewApprovalProperties;

    public DefaultFixTaskControlService(
            FixTaskService fixTaskService,
            FixTaskQueue fixTaskQueue,
            FixTaskTimelineService fixTaskTimelineService,
            TaskProcessRegistry taskProcessRegistry,
            ReviewApprovalProperties reviewApprovalProperties
    ) {
        this.fixTaskService = fixTaskService;
        this.fixTaskQueue = fixTaskQueue;
        this.fixTaskTimelineService = fixTaskTimelineService;
        this.taskProcessRegistry = taskProcessRegistry;
        this.reviewApprovalProperties = reviewApprovalProperties;
    }

    @Override
    public FixTaskVo cancelTask(String taskId) {
        FixTaskVo task = currentTask(taskId);
        if (!task.status().isActive()) {
            throw new IllegalStateException("Only active tasks can be cancelled");
        }
        if (task.status() == FixTaskStatus.PENDING) {
            fixTaskQueue.cancelPendingForTask(taskId);
        }
        FixTaskVo cancelledTask = fixTaskService.markCancelled(taskId, CANCELLATION_REASON);
        if (task.status() == FixTaskStatus.RUNNING || task.status() == FixTaskStatus.RUNNING_TESTS) {
            taskProcessRegistry.cancel(taskId);
        }
        fixTaskTimelineService.recordEvent(taskId, FixTaskTimelineEventType.CANCELLED, CANCELLATION_REASON);
        return cancelledTask;
    }

    @Override
    public FixTaskVo retryTask(String taskId) {
        FixTaskVo task = currentTask(taskId);
        if (task.status() == FixTaskStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Pending review tasks must be cancelled or approved before retry");
        }
        if (task.status() != FixTaskStatus.FAILED && task.status() != FixTaskStatus.CANCELLED) {
            throw new IllegalStateException("Only failed or cancelled tasks can be retried");
        }
        FixTaskVo retriedTask = fixTaskService.markPendingForRetry(taskId);
        fixTaskQueue.enqueue(taskId);
        fixTaskTimelineService.recordEvent(taskId, FixTaskTimelineEventType.REQUEUED, "Task requeued by user request");
        return retriedTask;
    }

    @Override
    public FixTaskVo approveReviewTask(String taskId, ApproveReviewCommand command) {
        FixTaskVo task = currentTask(taskId);
        if (task.status() != FixTaskStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Only pending review tasks can be approved");
        }
        requireAllowedReviewOperator(command.operator());
        FixTaskVo approvedTask = fixTaskService.markPendingForReviewApproval(
                taskId,
                command.operator(),
                command.reason()
        );
        fixTaskQueue.enqueue(taskId);
        fixTaskTimelineService.recordEvent(
                taskId,
                FixTaskTimelineEventType.REVIEW_APPROVED,
                "Pending review approved by " + command.operator() + ": " + command.reason()
        );
        return approvedTask;
    }

    private void requireAllowedReviewOperator(String operator) {
        boolean allowed = reviewApprovalProperties.normalizedAllowedOperators().stream()
                .anyMatch(allowedOperator -> allowedOperator.equalsIgnoreCase(operator));
        if (!allowed) {
            throw new SecurityException("operator is not allowed to approve risk reviews");
        }
    }

    private FixTaskVo currentTask(String taskId) {
        return fixTaskService.findTask(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
    }
}
