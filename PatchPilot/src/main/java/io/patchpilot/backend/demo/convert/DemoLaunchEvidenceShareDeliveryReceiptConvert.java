package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchEvidenceShareDeliveryReceiptEntity;

public final class DemoLaunchEvidenceShareDeliveryReceiptConvert {

    private DemoLaunchEvidenceShareDeliveryReceiptConvert() {
    }

    public static DemoLaunchEvidenceShareDeliveryReceiptEntity toEntity(
            DemoLaunchEvidenceShareDeliveryReceiptVo receipt
    ) {
        DemoLaunchEvidenceShareDeliveryReceiptEntity entity = new DemoLaunchEvidenceShareDeliveryReceiptEntity();
        entity.setId(receipt.id());
        entity.setStatus(receipt.status());
        entity.setLaunchEvidenceArchiveId(receipt.launchEvidenceArchiveId());
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

    public static DemoLaunchEvidenceShareDeliveryReceiptVo toVo(
            DemoLaunchEvidenceShareDeliveryReceiptEntity entity
    ) {
        return new DemoLaunchEvidenceShareDeliveryReceiptVo(
                entity.getId(),
                entity.getStatus(),
                entity.getLaunchEvidenceArchiveId(),
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
