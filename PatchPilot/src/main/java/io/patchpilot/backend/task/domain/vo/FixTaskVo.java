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
        String language,
        String buildSystem,
        String verificationCommand,
        String adapterDetectionReason,
        Long statusCommentId,
        String statusCommentUrl,
        Instant riskReviewApprovedAt,
        String riskReviewApprovedBy,
        String riskReviewApprovalReason,
        String retrySourceTaskId,
        String retrySourceStatus,
        String retrySourceFailureReason,
        String retryReason,
        Instant retriedAt
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
            Instant createdAt,
            String pullRequestUrl,
            Instant completedAt,
            Instant updatedAt,
            String language,
            String buildSystem,
            String verificationCommand,
            String adapterDetectionReason,
            Long statusCommentId,
            String statusCommentUrl
    ) {
        this(id, repositoryOwner, repositoryName, issueNumber, installationId, triggerUser, triggerComment,
                deliveryId, commentId, status, failureReason, createdAt, pullRequestUrl, completedAt, updatedAt,
                language, buildSystem, verificationCommand, adapterDetectionReason, statusCommentId, statusCommentUrl,
                null, null, null, null, null, null, null, null);
    }

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
            Instant createdAt,
            String pullRequestUrl,
            Instant completedAt,
            Instant updatedAt,
            String language,
            String buildSystem,
            String verificationCommand,
            String adapterDetectionReason,
            Long statusCommentId,
            String statusCommentUrl,
            Instant riskReviewApprovedAt
    ) {
        this(id, repositoryOwner, repositoryName, issueNumber, installationId, triggerUser, triggerComment,
                deliveryId, commentId, status, failureReason, createdAt, pullRequestUrl, completedAt, updatedAt,
                language, buildSystem, verificationCommand, adapterDetectionReason, statusCommentId, statusCommentUrl,
                riskReviewApprovedAt, null, null, null, null, null, null, null);
    }

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
            Instant createdAt,
            String pullRequestUrl,
            Instant completedAt,
            Instant updatedAt,
            String language,
            String buildSystem,
            String verificationCommand,
            String adapterDetectionReason,
            Long statusCommentId,
            String statusCommentUrl,
            Instant riskReviewApprovedAt,
            String riskReviewApprovedBy,
            String riskReviewApprovalReason
    ) {
        this(id, repositoryOwner, repositoryName, issueNumber, installationId, triggerUser, triggerComment,
                deliveryId, commentId, status, failureReason, createdAt, pullRequestUrl, completedAt, updatedAt,
                language, buildSystem, verificationCommand, adapterDetectionReason, statusCommentId, statusCommentUrl,
                riskReviewApprovedAt, riskReviewApprovedBy, riskReviewApprovalReason, null, null, null, null, null);
    }

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
            Instant createdAt,
            String pullRequestUrl,
            Instant completedAt,
            Instant updatedAt,
            String language,
            String buildSystem,
            String verificationCommand,
            Long statusCommentId,
            String statusCommentUrl
    ) {
        this(id, repositoryOwner, repositoryName, issueNumber, installationId, triggerUser, triggerComment,
                deliveryId, commentId, status, failureReason, createdAt, pullRequestUrl, completedAt, updatedAt,
                language, buildSystem, verificationCommand, null, statusCommentId, statusCommentUrl);
    }

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
            Instant createdAt,
            String pullRequestUrl,
            Instant completedAt,
            Instant updatedAt,
            Long statusCommentId,
            String statusCommentUrl
    ) {
        this(id, repositoryOwner, repositoryName, issueNumber, installationId, triggerUser, triggerComment,
                deliveryId, commentId, status, failureReason, createdAt, pullRequestUrl, completedAt, updatedAt,
                null, null, null, null, statusCommentId, statusCommentUrl);
    }

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
                deliveryId, commentId, status, failureReason, createdAt, null, null, createdAt, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
    }

    public FixTaskVo withAdapterMetadata(String language, String buildSystem, String verificationCommand) {
        return withAdapterMetadata(language, buildSystem, verificationCommand, null);
    }

    public FixTaskVo withAdapterMetadata(
            String language,
            String buildSystem,
            String verificationCommand,
            String adapterDetectionReason
    ) {
        return new FixTaskVo(
                id,
                repositoryOwner,
                repositoryName,
                issueNumber,
                installationId,
                triggerUser,
                triggerComment,
                deliveryId,
                commentId,
                status,
                failureReason,
                createdAt,
                pullRequestUrl,
                completedAt,
                updatedAt,
                language,
                buildSystem,
                verificationCommand,
                adapterDetectionReason,
                statusCommentId,
                statusCommentUrl,
                riskReviewApprovedAt,
                riskReviewApprovedBy,
                riskReviewApprovalReason,
                retrySourceTaskId,
                retrySourceStatus,
                retrySourceFailureReason,
                retryReason,
                retriedAt
        );
    }

    public FixTaskVo withRiskReviewApprovedAt(Instant riskReviewApprovedAt) {
        return withRiskReviewApproval(riskReviewApprovedAt, riskReviewApprovedBy, riskReviewApprovalReason);
    }

    public FixTaskVo withRiskReviewApproval(
            Instant riskReviewApprovedAt,
            String riskReviewApprovedBy,
            String riskReviewApprovalReason
    ) {
        return new FixTaskVo(
                id,
                repositoryOwner,
                repositoryName,
                issueNumber,
                installationId,
                triggerUser,
                triggerComment,
                deliveryId,
                commentId,
                status,
                failureReason,
                createdAt,
                pullRequestUrl,
                completedAt,
                updatedAt,
                language,
                buildSystem,
                verificationCommand,
                adapterDetectionReason,
                statusCommentId,
                statusCommentUrl,
                riskReviewApprovedAt,
                riskReviewApprovedBy,
                riskReviewApprovalReason,
                retrySourceTaskId,
                retrySourceStatus,
                retrySourceFailureReason,
                retryReason,
                retriedAt
        );
    }
}
