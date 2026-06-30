package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity;

public final class DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvert {

    private DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvert() {
    }

    public static DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity toEntity(
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt
    ) {
        DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity entity =
                new DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity();
        entity.setId(receipt.id());
        entity.setStatus(receipt.status().name());
        entity.setReleaseBundleArchiveStatus(receipt.releaseBundleArchiveStatus().name());
        entity.setReleaseBundleArchiveId(receipt.releaseBundleArchiveId());
        entity.setLatestCertificateArchiveId(receipt.latestCertificateArchiveId());
        entity.setLatestDeliveryFinalizationArchiveId(receipt.latestDeliveryFinalizationArchiveId());
        entity.setLatestPackageArchiveId(receipt.latestPackageArchiveId());
        entity.setLatestPackageDeliveryReceiptId(receipt.latestPackageDeliveryReceiptId());
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

    public static DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo toVo(
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity entity
    ) {
        return new DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                DemoReadinessStatus.valueOf(entity.getReleaseBundleArchiveStatus()),
                entity.getReleaseBundleArchiveId(),
                entity.getLatestCertificateArchiveId(),
                entity.getLatestDeliveryFinalizationArchiveId(),
                entity.getLatestPackageArchiveId(),
                entity.getLatestPackageDeliveryReceiptId(),
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
