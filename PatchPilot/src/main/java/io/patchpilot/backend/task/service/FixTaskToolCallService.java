package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface FixTaskToolCallService {

    FixTaskToolCallVo recordToolCall(
            String taskId,
            String toolName,
            String inputSummary,
            String outputSummary,
            boolean success,
            Instant startedAt,
            Instant finishedAt
    );

    List<FixTaskToolCallVo> listToolCalls(String taskId);

    static long durationMs(Instant startedAt, Instant finishedAt) {
        return Duration.between(startedAt, finishedAt).toMillis();
    }
}
