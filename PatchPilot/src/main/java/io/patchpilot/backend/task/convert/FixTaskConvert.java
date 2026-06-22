package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.entity.FixTaskEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.time.Instant;

public final class FixTaskConvert {

    private FixTaskConvert() {
    }

    public static FixTaskVo toVo(FixTaskEntity entity) {
        return new FixTaskVo(
                entity.getId(),
                entity.getRepositoryOwner(),
                entity.getRepositoryName(),
                entity.getIssueNumber(),
                entity.getInstallationId(),
                entity.getTriggerUser(),
                entity.getTriggerComment(),
                entity.getDeliveryId(),
                entity.getCommentId(),
                FixTaskStatus.valueOf(entity.getStatus()),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getPullRequestUrl(),
                entity.getCompletedAt(),
                entity.getUpdatedAt(),
                entity.getLanguage(),
                entity.getBuildSystem(),
                entity.getVerificationCommand(),
                entity.getAdapterDetectionReason(),
                entity.getStatusCommentId(),
                entity.getStatusCommentUrl(),
                entity.getRiskReviewApprovedAt(),
                entity.getRiskReviewApprovedBy(),
                entity.getRiskReviewApprovalReason()
        );
    }

    public static FixTaskEntity newEntity(String id, CreateFixTaskCommand command, Instant createdAt) {
        FixTaskEntity entity = new FixTaskEntity();
        entity.setId(id);
        entity.setRepositoryOwner(command.repositoryOwner());
        entity.setRepositoryName(command.repositoryName());
        entity.setIssueNumber(command.issueNumber());
        entity.setInstallationId(command.installationId());
        entity.setTriggerUser(command.triggerUser());
        entity.setTriggerComment(command.triggerComment());
        entity.setDeliveryId(command.deliveryId());
        entity.setCommentId(command.commentId());
        entity.setStatus(FixTaskStatus.PENDING.name());
        entity.setFailureReason(null);
        entity.setCreatedAt(createdAt);
        entity.setPullRequestUrl(null);
        entity.setCompletedAt(null);
        entity.setUpdatedAt(createdAt);
        entity.setLanguage(null);
        entity.setBuildSystem(null);
        entity.setVerificationCommand(null);
        entity.setAdapterDetectionReason(null);
        entity.setStatusCommentId(null);
        entity.setStatusCommentUrl(null);
        entity.setRiskReviewApprovedAt(null);
        entity.setRiskReviewApprovedBy(null);
        entity.setRiskReviewApprovalReason(null);
        return entity;
    }

    public static FixTaskEntity replaceStatus(
            FixTaskEntity current,
            FixTaskStatus status,
            String failureReason,
            Instant updatedAt
    ) {
        FixTaskEntity entity = new FixTaskEntity();
        entity.setId(current.getId());
        entity.setRepositoryOwner(current.getRepositoryOwner());
        entity.setRepositoryName(current.getRepositoryName());
        entity.setIssueNumber(current.getIssueNumber());
        entity.setInstallationId(current.getInstallationId());
        entity.setTriggerUser(current.getTriggerUser());
        entity.setTriggerComment(current.getTriggerComment());
        entity.setDeliveryId(current.getDeliveryId());
        entity.setCommentId(current.getCommentId());
        entity.setStatus(status.name());
        entity.setFailureReason(failureReason);
        entity.setCreatedAt(current.getCreatedAt());
        entity.setPullRequestUrl(current.getPullRequestUrl());
        entity.setCompletedAt(current.getCompletedAt());
        entity.setUpdatedAt(updatedAt);
        entity.setLanguage(current.getLanguage());
        entity.setBuildSystem(current.getBuildSystem());
        entity.setVerificationCommand(current.getVerificationCommand());
        entity.setAdapterDetectionReason(current.getAdapterDetectionReason());
        entity.setStatusCommentId(current.getStatusCommentId());
        entity.setStatusCommentUrl(current.getStatusCommentUrl());
        entity.setRiskReviewApprovedAt(current.getRiskReviewApprovedAt());
        entity.setRiskReviewApprovedBy(current.getRiskReviewApprovedBy());
        entity.setRiskReviewApprovalReason(current.getRiskReviewApprovalReason());
        if (status == FixTaskStatus.PENDING_REVIEW) {
            clearRiskReviewApproval(entity);
        }
        return entity;
    }

    public static FixTaskEntity replaceCompleted(FixTaskEntity current, String pullRequestUrl, Instant completedAt) {
        FixTaskEntity entity = replaceStatus(current, FixTaskStatus.COMPLETED, null, completedAt);
        entity.setPullRequestUrl(pullRequestUrl);
        entity.setCompletedAt(completedAt);
        entity.setUpdatedAt(completedAt);
        return entity;
    }

    public static FixTaskEntity replacePendingForRetry(FixTaskEntity current, Instant updatedAt) {
        FixTaskEntity entity = replaceStatus(current, FixTaskStatus.PENDING, null, updatedAt);
        entity.setPullRequestUrl(null);
        entity.setCompletedAt(null);
        clearRiskReviewApproval(entity);
        return entity;
    }

    public static FixTaskEntity replacePendingForReviewApproval(
            FixTaskEntity current,
            Instant updatedAt,
            String approvedBy,
            String approvalReason
    ) {
        FixTaskEntity entity = replaceStatus(current, FixTaskStatus.PENDING, null, updatedAt);
        entity.setPullRequestUrl(null);
        entity.setCompletedAt(null);
        entity.setRiskReviewApprovedAt(updatedAt);
        entity.setRiskReviewApprovedBy(approvedBy);
        entity.setRiskReviewApprovalReason(approvalReason);
        return entity;
    }

    public static FixTaskEntity attachStatusComment(
            FixTaskEntity current,
            long statusCommentId,
            String statusCommentUrl,
            Instant updatedAt
    ) {
        FixTaskEntity entity = replaceStatus(
                current,
                FixTaskStatus.valueOf(current.getStatus()),
                current.getFailureReason(),
                updatedAt
        );
        entity.setStatusCommentId(statusCommentId);
        entity.setStatusCommentUrl(statusCommentUrl);
        return entity;
    }

    public static FixTaskEntity attachAdapterMetadata(
            FixTaskEntity current,
            String language,
            String buildSystem,
            String verificationCommand,
            String adapterDetectionReason,
            Instant updatedAt
    ) {
        FixTaskEntity entity = replaceStatus(
                current,
                FixTaskStatus.valueOf(current.getStatus()),
                current.getFailureReason(),
                updatedAt
        );
        entity.setLanguage(language);
        entity.setBuildSystem(buildSystem);
        entity.setVerificationCommand(verificationCommand);
        entity.setAdapterDetectionReason(adapterDetectionReason);
        return entity;
    }

    private static void clearRiskReviewApproval(FixTaskEntity entity) {
        entity.setRiskReviewApprovedAt(null);
        entity.setRiskReviewApprovedBy(null);
        entity.setRiskReviewApprovalReason(null);
    }
}
