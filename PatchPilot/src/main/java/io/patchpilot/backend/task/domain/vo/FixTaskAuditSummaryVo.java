package io.patchpilot.backend.task.domain.vo;

public record FixTaskAuditSummaryVo(
        FixTaskVo task,
        int timelineEventCount,
        int testRunCount,
        int toolCallCount,
        int modelCallCount,
        long totalModelTokens,
        FixTaskTimelineEventVo latestTimelineEvent,
        Integer latestTestRunExitCode,
        Long latestTestRunDurationMs
) {
}
