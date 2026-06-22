package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskGeneratedDiffVo(
        String toolCallId,
        String diff,
        Instant generatedAt
) {
}
