package io.patchpilot.backend.safety.domain;

import java.time.Instant;

public record RejectedTriggerAuditVo(
        String id,
        String source,
        String deliveryId,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment,
        String reason,
        Instant createdAt
) {
}
