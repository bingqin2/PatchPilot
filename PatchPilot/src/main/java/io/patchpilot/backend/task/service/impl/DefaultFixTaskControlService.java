package io.patchpilot.backend.task.service.impl;

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

    public DefaultFixTaskControlService(
            FixTaskService fixTaskService,
            FixTaskQueue fixTaskQueue,
            FixTaskTimelineService fixTaskTimelineService,
            TaskProcessRegistry taskProcessRegistry
    ) {
        this.fixTaskService = fixTaskService;
        this.fixTaskQueue = fixTaskQueue;
        this.fixTaskTimelineService = fixTaskTimelineService;
        this.taskProcessRegistry = taskProcessRegistry;
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
        if (task.status() != FixTaskStatus.FAILED && task.status() != FixTaskStatus.CANCELLED) {
            throw new IllegalStateException("Only failed or cancelled tasks can be retried");
        }
        FixTaskVo retriedTask = fixTaskService.markPendingForRetry(taskId);
        fixTaskQueue.enqueue(taskId);
        fixTaskTimelineService.recordEvent(taskId, FixTaskTimelineEventType.REQUEUED, "Task requeued by user request");
        return retriedTask;
    }

    private FixTaskVo currentTask(String taskId) {
        return fixTaskService.findTask(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
    }
}
