package io.patchpilot.backend.task.domain.vo;

import io.patchpilot.backend.task.domain.enums.FixTaskStatus;

import java.time.Instant;

public record FixTaskVo(
        String id,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        long installationId,
        String triggerUser,
        String triggerComment,
        String deliveryId,
        long commentId,
        FixTaskStatus status,
        String failureReason,
        Instant createdAt,
        String pullRequestUrl,
        Instant completedAt,
        Instant updatedAt,
        Long statusCommentId,
        String statusCommentUrl
) {

    public FixTaskVo(
            String id,
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            long installationId,
            String triggerUser,
            String triggerComment,
            String deliveryId,
            long commentId,
            FixTaskStatus status,
            String failureReason,
            Instant createdAt
    ) {
        this(id, repositoryOwner, repositoryName, issueNumber, installationId, triggerUser, triggerComment,
                deliveryId, commentId, status, failureReason, createdAt, null, null, createdAt, null, null);
    }
}
