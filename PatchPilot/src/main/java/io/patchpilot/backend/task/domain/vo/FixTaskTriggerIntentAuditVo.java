package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskTriggerIntentAuditVo(
        String eventId,
        String summary,
        String safetyDecision,
        String issueContextStatus,
        String modelDecision,
        Instant createdAt
) {
}
