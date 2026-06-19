package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface FixTaskModelCallService {

    FixTaskModelCallVo recordModelCall(
            String taskId,
            String provider,
            String model,
            String promptSummary,
            String responseSummary,
            int promptTokens,
            int completionTokens,
            boolean success,
            String errorMessage,
            Instant startedAt,
            Instant finishedAt
    );

    List<FixTaskModelCallVo> listModelCalls(String taskId);

    static long durationMs(Instant startedAt, Instant finishedAt) {
        return Duration.between(startedAt, finishedAt).toMillis();
    }
}
