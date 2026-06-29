package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity;

public final class DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvert {

    private DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvert() {
    }

    public static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity toEntity(
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt
    ) {
        DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity entity =
                new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity();
        entity.setId(receipt.id());
        entity.setStatus(receipt.status().name());
        entity.setReadyToShare(receipt.readyToShare());
        entity.setCompletionEvidenceBundleStatus(receipt.completionEvidenceBundleStatus().name());
        entity.setSummary(receipt.summary());
        entity.setNextAction(receipt.nextAction());
        entity.setLatestCompletionArchiveId(receipt.latestCompletionArchiveId());
        entity.setLatestSharePackageArchiveId(receipt.latestSharePackageArchiveId());
        entity.setLatestDeliveryReceiptId(receipt.latestDeliveryReceiptId());
        entity.setLatestTaskId(receipt.latestTaskId());
        entity.setDeliveryChannel(receipt.deliveryChannel());
        entity.setDeliveryTarget(receipt.deliveryTarget());
        entity.setOperator(receipt.operator());
        entity.setNotes(receipt.notes());
        entity.setDeliveredAt(receipt.deliveredAt());
        entity.setCreatedAt(receipt.createdAt());
        entity.setMarkdownReport(receipt.markdownReport());
        return entity;
    }

    public static DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo toVo(
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity entity
    ) {
        return new DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getReadyToShare()),
                DemoReadinessStatus.valueOf(entity.getCompletionEvidenceBundleStatus()),
                entity.getSummary(),
                entity.getNextAction(),
                entity.getLatestCompletionArchiveId(),
                entity.getLatestSharePackageArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestTaskId(),
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
