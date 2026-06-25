package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskPreExecutionSafetySnapshotVo(
        String eventId,
        String source,
        String finalDecision,
        String safetyDecision,
        String quarantineDecision,
        String rateLimitDecision,
        String issueContextStatus,
        String modelDecision,
        Instant createdAt
) {
}
