package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoHandoffShareDeliveryReceiptEntity;

public final class DemoHandoffShareDeliveryReceiptConvert {

    private DemoHandoffShareDeliveryReceiptConvert() {
    }

    public static DemoHandoffShareDeliveryReceiptEntity toEntity(DemoHandoffShareDeliveryReceiptVo receipt) {
        DemoHandoffShareDeliveryReceiptEntity entity = new DemoHandoffShareDeliveryReceiptEntity();
        entity.setId(receipt.id());
        entity.setStatus(receipt.status().name());
        entity.setHandoffArchiveId(receipt.handoffArchiveId());
        entity.setSessionId(receipt.sessionId());
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

    public static DemoHandoffShareDeliveryReceiptVo toVo(DemoHandoffShareDeliveryReceiptEntity entity) {
        return new DemoHandoffShareDeliveryReceiptVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                entity.getHandoffArchiveId(),
                entity.getSessionId(),
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
