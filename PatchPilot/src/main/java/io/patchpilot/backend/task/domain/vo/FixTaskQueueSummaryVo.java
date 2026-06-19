package io.patchpilot.backend.task.domain.vo;

public record FixTaskQueueSummaryVo(
        long totalCount,
        long pendingCount,
        long availablePendingCount,
        long delayedPendingCount,
        long runningCount,
        long completedCount,
        long failedCount,
        long cancelledCount
) {

    public static FixTaskQueueSummaryVo empty() {
        return new FixTaskQueueSummaryVo(0, 0, 0, 0, 0, 0, 0, 0);
    }
}
