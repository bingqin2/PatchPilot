package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceShareDeliveryReceiptEntity;

public final class DemoFinalAcceptanceShareDeliveryReceiptConvert {

    private DemoFinalAcceptanceShareDeliveryReceiptConvert() {
    }

    public static DemoFinalAcceptanceShareDeliveryReceiptEntity toEntity(
            DemoFinalAcceptanceShareDeliveryReceiptVo receipt
    ) {
        DemoFinalAcceptanceShareDeliveryReceiptEntity entity =
                new DemoFinalAcceptanceShareDeliveryReceiptEntity();
        entity.setId(receipt.id());
        entity.setStatus(receipt.status().name());
        entity.setFinalAcceptanceSharePackageArchiveId(receipt.finalAcceptanceSharePackageArchiveId());
        entity.setLatestTaskId(receipt.latestTaskId());
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

    public static DemoFinalAcceptanceShareDeliveryReceiptVo toVo(
            DemoFinalAcceptanceShareDeliveryReceiptEntity entity
    ) {
        return new DemoFinalAcceptanceShareDeliveryReceiptVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                entity.getFinalAcceptanceSharePackageArchiveId(),
                entity.getLatestTaskId(),
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
