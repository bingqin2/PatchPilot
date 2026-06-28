package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageShareDeliveryReceiptEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;

public final class FixTaskEvidencePackageShareDeliveryReceiptConvert {

    private FixTaskEvidencePackageShareDeliveryReceiptConvert() {
    }

    public static FixTaskEvidencePackageShareDeliveryReceiptEntity toEntity(
            FixTaskEvidencePackageShareDeliveryReceiptVo receipt
    ) {
        FixTaskEvidencePackageShareDeliveryReceiptEntity entity =
                new FixTaskEvidencePackageShareDeliveryReceiptEntity();
        entity.setId(receipt.id());
        entity.setStatus(receipt.status());
        entity.setTaskEvidenceArchiveId(receipt.taskEvidenceArchiveId());
        entity.setTaskId(receipt.taskId());
        entity.setRepositoryOwner(receipt.repositoryOwner());
        entity.setRepositoryName(receipt.repositoryName());
        entity.setIssueNumber(receipt.issueNumber());
        entity.setPullRequestUrl(receipt.pullRequestUrl());
        entity.setDeliveryChannel(receipt.deliveryChannel());
        entity.setDeliveryTarget(receipt.deliveryTarget());
        entity.setOperator(receipt.operator());
        entity.setNotes(receipt.notes());
        entity.setMessageSubject(receipt.messageSubject());
        entity.setDeliveredAt(receipt.deliveredAt());
        entity.setCreatedAt(receipt.createdAt());
        entity.setMarkdownReport(receipt.markdownReport());
        return entity;
    }

    public static FixTaskEvidencePackageShareDeliveryReceiptVo toVo(
            FixTaskEvidencePackageShareDeliveryReceiptEntity entity
    ) {
        return new FixTaskEvidencePackageShareDeliveryReceiptVo(
                entity.getId(),
                entity.getStatus(),
                entity.getTaskEvidenceArchiveId(),
                entity.getTaskId(),
                entity.getRepositoryOwner(),
                entity.getRepositoryName(),
                entity.getIssueNumber(),
                entity.getPullRequestUrl(),
                entity.getDeliveryChannel(),
                entity.getDeliveryTarget(),
                entity.getOperator(),
                entity.getNotes(),
                entity.getMessageSubject(),
                entity.getDeliveredAt(),
                entity.getCreatedAt(),
                entity.getMarkdownReport()
        );
    }
}
