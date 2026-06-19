package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskTestRunEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;

import java.time.Instant;

public final class FixTaskTestRunConvert {

    private FixTaskTestRunConvert() {
    }

    public static FixTaskTestRunEntity newEntity(
            String id,
            String taskId,
            String command,
            int exitCode,
            String output,
            Instant startedAt,
            Instant finishedAt,
            long durationMs
    ) {
        FixTaskTestRunEntity entity = new FixTaskTestRunEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setCommand(command);
        entity.setExitCode(exitCode);
        entity.setOutput(output);
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(finishedAt);
        entity.setDurationMs(durationMs);
        return entity;
    }

    public static FixTaskTestRunVo toVo(FixTaskTestRunEntity entity) {
        return new FixTaskTestRunVo(
                entity.getId(),
                entity.getTaskId(),
                entity.getCommand(),
                entity.getExitCode(),
                entity.getOutput(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getDurationMs()
        );
    }
}
