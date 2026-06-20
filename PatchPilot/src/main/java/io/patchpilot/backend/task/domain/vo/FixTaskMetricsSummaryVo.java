package io.patchpilot.backend.task.domain.vo;

public record FixTaskMetricsSummaryVo(
        long totalCount,
        long pendingCount,
        long runningCount,
        long runningTestsCount,
        long completedCount,
        long failedCount,
        long cancelledCount,
        double completionRate,
        double failureRate,
        long averageCompletionDurationMs,
        long totalModelTokens,
        long averageModelTokensPerCompletedTask,
        long testRunCount,
        long passedTestRunCount,
        long failedTestRunCount,
        double testPassRate
) {

    public static FixTaskMetricsSummaryVo empty() {
        return new FixTaskMetricsSummaryVo(0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0.0);
    }
}
