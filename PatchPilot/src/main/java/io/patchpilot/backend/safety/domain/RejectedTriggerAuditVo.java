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
        String category,
        Long commentId,
        String commentUrl,
        String retriedTaskId,
        Instant retriedAt,
        boolean retryable,
        String retryBlockedReason,
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
                reason, RejectedTriggerCategory.UNKNOWN, null, null, null, null, createdAt);
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
                reason, RejectedTriggerCategory.UNKNOWN, commentId, commentUrl, null, null, createdAt);
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
            String category,
            Long commentId,
            String commentUrl,
            Instant createdAt
    ) {
        this(id, source, deliveryId, repositoryOwner, repositoryName, issueNumber, triggerUser, triggerComment,
                reason, category, commentId, commentUrl, null, null, createdAt);
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
            String retriedTaskId,
            Instant retriedAt,
            Instant createdAt
    ) {
        this(id, source, deliveryId, repositoryOwner, repositoryName, issueNumber, triggerUser, triggerComment,
                reason, RejectedTriggerCategory.UNKNOWN, commentId, commentUrl, retriedTaskId, retriedAt, createdAt);
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
            String category,
            Long commentId,
            String commentUrl,
            String retriedTaskId,
            Instant retriedAt,
            Instant createdAt
    ) {
        this(
                id,
                source,
                deliveryId,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                reason,
                category,
                commentId,
                commentUrl,
                retriedTaskId,
                retriedAt,
                RejectedTriggerRetryPolicy.isRetryable(category, retriedTaskId),
                RejectedTriggerRetryPolicy.retryBlockedReason(category, retriedTaskId),
                createdAt
        );
    }
}
