package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskToolCallEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;

import java.time.Instant;

public final class FixTaskToolCallConvert {

    private FixTaskToolCallConvert() {
    }

    public static FixTaskToolCallEntity newEntity(
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
        FixTaskToolCallEntity entity = new FixTaskToolCallEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setToolName(toolName);
        entity.setInputSummary(inputSummary);
        entity.setOutputSummary(outputSummary);
        entity.setSuccess(success);
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(finishedAt);
        entity.setDurationMs(durationMs);
        return entity;
    }

    public static FixTaskToolCallVo toVo(FixTaskToolCallEntity entity) {
        return new FixTaskToolCallVo(
                entity.getId(),
                entity.getTaskId(),
                entity.getToolName(),
                entity.getInputSummary(),
                entity.getOutputSummary(),
                entity.isSuccess(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getDurationMs()
        );
    }
}
