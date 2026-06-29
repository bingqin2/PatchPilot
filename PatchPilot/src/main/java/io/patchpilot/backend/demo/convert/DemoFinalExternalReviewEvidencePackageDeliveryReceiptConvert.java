package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity;

public final class DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvert {

    private DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvert() {
    }

    public static DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity toEntity(
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt
    ) {
        DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity entity =
                new DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity();
        entity.setId(receipt.id());
        entity.setStatus(receipt.status().name());
        entity.setFinalExternalReviewPackageArchiveStatus(
                receipt.finalExternalReviewPackageArchiveStatus().name()
        );
        entity.setFinalExternalReviewPackageArchiveId(receipt.finalExternalReviewPackageArchiveId());
        entity.setCloseoutArchiveId(receipt.closeoutArchiveId());
        entity.setCompletionArchiveId(receipt.completionArchiveId());
        entity.setCompletionEvidenceDeliveryReceiptId(receipt.completionEvidenceDeliveryReceiptId());
        entity.setLatestTaskId(receipt.latestTaskId());
        entity.setLatestPullRequestUrl(receipt.latestPullRequestUrl());
        entity.setSummary(receipt.summary());
        entity.setNextAction(receipt.nextAction());
        entity.setDeliveryChannel(receipt.deliveryChannel());
        entity.setDeliveryTarget(receipt.deliveryTarget());
        entity.setOperator(receipt.operator());
        entity.setNotes(receipt.notes());
        entity.setDeliveredAt(receipt.deliveredAt());
        entity.setCreatedAt(receipt.createdAt());
        entity.setMarkdownReport(receipt.markdownReport());
        return entity;
    }

    public static DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo toVo(
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity entity
    ) {
        return new DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                DemoReadinessStatus.valueOf(entity.getFinalExternalReviewPackageArchiveStatus()),
                entity.getFinalExternalReviewPackageArchiveId(),
                entity.getCloseoutArchiveId(),
                entity.getCompletionArchiveId(),
                entity.getCompletionEvidenceDeliveryReceiptId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getDeliveryChannel(),
                entity.getDeliveryTarget(),
                entity.getOperator(),
                entity.getNotes(),
                entity.getDeliveredAt(),
                entity.getCreatedAt(),
                entity.getMarkdownReport()
        );
    }
}
