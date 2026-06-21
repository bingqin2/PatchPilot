package io.patchpilot.backend.task.domain.vo;

public record FixTaskStatusCountsVo(
        long totalCount,
        long pendingCount,
        long runningCount,
        long runningTestsCount,
        long completedCount,
        long failedCount,
        long cancelledCount
) {
}
