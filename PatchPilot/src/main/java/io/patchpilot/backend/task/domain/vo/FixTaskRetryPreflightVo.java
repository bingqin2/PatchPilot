package io.patchpilot.backend.task.domain.vo;

import io.patchpilot.backend.task.domain.enums.FixTaskStatus;

public record FixTaskRetryPreflightVo(
        String taskId,
        FixTaskStatus status,
        boolean retryable,
        String category,
        String reason,
        String operatorAction
) {
}
