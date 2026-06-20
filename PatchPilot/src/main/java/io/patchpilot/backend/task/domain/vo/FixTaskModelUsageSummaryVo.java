package io.patchpilot.backend.task.domain.vo;

public record FixTaskModelUsageSummaryVo(
        long totalPromptTokens,
        long totalCompletionTokens,
        long totalTokens,
        long successfulCalls,
        long failedCalls,
        double estimatedCostUsd
) {

    public static FixTaskModelUsageSummaryVo empty() {
        return new FixTaskModelUsageSummaryVo(0, 0, 0, 0, 0, 0.0);
    }
}
