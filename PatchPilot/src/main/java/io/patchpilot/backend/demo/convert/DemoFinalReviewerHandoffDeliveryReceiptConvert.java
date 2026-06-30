package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalReviewerHandoffDeliveryReceiptEntity;

public final class DemoFinalReviewerHandoffDeliveryReceiptConvert {

    private DemoFinalReviewerHandoffDeliveryReceiptConvert() {
    }

    public static DemoFinalReviewerHandoffDeliveryReceiptEntity toEntity(
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt
    ) {
        DemoFinalReviewerHandoffDeliveryReceiptEntity entity =
                new DemoFinalReviewerHandoffDeliveryReceiptEntity();
        entity.setId(receipt.id());
        entity.setStatus(receipt.status().name());
        entity.setHandoffPackageStatus(receipt.handoffPackageStatus().name());
        entity.setLatestCertificateArchiveId(receipt.latestCertificateArchiveId());
        entity.setLatestDeliveryFinalizationArchiveId(receipt.latestDeliveryFinalizationArchiveId());
        entity.setLatestReleaseBundleArchiveId(receipt.latestReleaseBundleArchiveId());
        entity.setLatestDeliveryReceiptId(receipt.latestDeliveryReceiptId());
        entity.setLatestPackageCertificateArchiveId(receipt.latestPackageCertificateArchiveId());
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

    public static DemoFinalReviewerHandoffDeliveryReceiptVo toVo(
            DemoFinalReviewerHandoffDeliveryReceiptEntity entity
    ) {
        return new DemoFinalReviewerHandoffDeliveryReceiptVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                DemoReadinessStatus.valueOf(entity.getHandoffPackageStatus()),
                entity.getLatestCertificateArchiveId(),
                entity.getLatestDeliveryFinalizationArchiveId(),
                entity.getLatestReleaseBundleArchiveId(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestPackageCertificateArchiveId(),
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
