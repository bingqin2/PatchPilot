package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskModelCallEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;

import java.time.Instant;

public final class FixTaskModelCallConvert {

    private FixTaskModelCallConvert() {
    }

    public static FixTaskModelCallEntity newEntity(
            String id,
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
            Instant finishedAt,
            long durationMs
    ) {
        FixTaskModelCallEntity entity = new FixTaskModelCallEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setProvider(provider);
        entity.setModel(model);
        entity.setPromptSummary(promptSummary);
        entity.setResponseSummary(responseSummary);
        entity.setPromptTokens(promptTokens);
        entity.setCompletionTokens(completionTokens);
        entity.setTotalTokens(promptTokens + completionTokens);
        entity.setSuccess(success);
        entity.setErrorMessage(errorMessage);
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(finishedAt);
        entity.setDurationMs(durationMs);
        return entity;
    }

    public static FixTaskModelCallVo toVo(FixTaskModelCallEntity entity) {
        return new FixTaskModelCallVo(
                entity.getId(),
                entity.getTaskId(),
                entity.getProvider(),
                entity.getModel(),
                entity.getPromptSummary(),
                entity.getResponseSummary(),
                entity.getPromptTokens(),
                entity.getCompletionTokens(),
                entity.getTotalTokens(),
                entity.isSuccess(),
                entity.getErrorMessage(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getDurationMs()
        );
    }
}
