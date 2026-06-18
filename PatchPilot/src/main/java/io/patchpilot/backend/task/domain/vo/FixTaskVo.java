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
        Instant createdAt
) {
}
