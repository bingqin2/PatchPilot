package io.patchpilot.backend.task.domain.vo;

public record FixTaskLatencySummaryVo(
        long completedTaskCount,
        long averageTaskDurationMs,
        long maxTaskDurationMs,
        long modelCallCount,
        long averageModelCallDurationMs,
        long maxModelCallDurationMs,
        long toolCallCount,
        long averageToolCallDurationMs,
        long maxToolCallDurationMs,
        long testRunCount,
        long averageTestRunDurationMs,
        long maxTestRunDurationMs
) {

    public static FixTaskLatencySummaryVo empty() {
        return new FixTaskLatencySummaryVo(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}
