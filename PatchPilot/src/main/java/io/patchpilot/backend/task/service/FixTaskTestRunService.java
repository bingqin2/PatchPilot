package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface FixTaskTestRunService {

    FixTaskTestRunVo recordTestRun(
            String taskId,
            String command,
            int exitCode,
            String output,
            Instant startedAt,
            Instant finishedAt
    );

    List<FixTaskTestRunVo> listTestRuns(String taskId);

    static long durationMs(Instant startedAt, Instant finishedAt) {
        return Duration.between(startedAt, finishedAt).toMillis();
    }
}
