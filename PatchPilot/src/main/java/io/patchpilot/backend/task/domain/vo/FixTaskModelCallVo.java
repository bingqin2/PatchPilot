package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskModelCallVo(
        String id,
        String taskId,
        String provider,
        String model,
        String promptSummary,
        String responseSummary,
        int promptTokens,
        int completionTokens,
        int totalTokens,
        boolean success,
        String errorMessage,
        Instant startedAt,
        Instant finishedAt,
        long durationMs
) {
}
