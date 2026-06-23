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
        Long commentId,
        String commentUrl,
        String retriedTaskId,
        Instant retriedAt,
        Instant createdAt
) {

    public RejectedTriggerAuditVo(
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
        this(id, source, deliveryId, repositoryOwner, repositoryName, issueNumber, triggerUser, triggerComment,
                reason, null, null, null, null, createdAt);
    }

    public RejectedTriggerAuditVo(
            String id,
            String source,
            String deliveryId,
            String repositoryOwner,
            String repositoryName,
            Long issueNumber,
            String triggerUser,
            String triggerComment,
            String reason,
            Long commentId,
            String commentUrl,
            Instant createdAt
    ) {
        this(id, source, deliveryId, repositoryOwner, repositoryName, issueNumber, triggerUser, triggerComment,
                reason, commentId, commentUrl, null, null, createdAt);
    }
}
