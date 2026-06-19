package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskTestRunVo(
        String id,
        String taskId,
        String command,
        int exitCode,
        String output,
        Instant startedAt,
        Instant finishedAt,
        long durationMs
) {
}
