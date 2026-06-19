package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskToolCallVo(
        String id,
        String taskId,
        String toolName,
        String inputSummary,
        String outputSummary,
        boolean success,
        Instant startedAt,
        Instant finishedAt,
        long durationMs
) {
}
