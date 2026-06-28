package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;

public final class FixTaskEvidencePackageAcceptanceCloseoutArchiveConvert {

    private FixTaskEvidencePackageAcceptanceCloseoutArchiveConvert() {
    }

    public static FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity toEntity(
            FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive
    ) {
        FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity entity =
                new FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status());
        entity.setAccepted(archive.accepted());
        entity.setSummary(archive.summary());
        entity.setLatestArchiveId(archive.latestArchiveId());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestDeliveryReceiptId(archive.latestDeliveryReceiptId());
        entity.setLatestDeliveryTarget(archive.latestDeliveryTarget());
        entity.setLatestDeliveryChannel(archive.latestDeliveryChannel());
        entity.setDeliveryReceiptFreshness(archive.deliveryReceiptFreshness());
        entity.setCreatedAt(archive.createdAt());
        entity.setReport(archive.report());
        return entity;
    }

    public static FixTaskEvidencePackageAcceptanceCloseoutArchiveVo toVo(
            FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity entity
    ) {
        return new FixTaskEvidencePackageAcceptanceCloseoutArchiveVo(
                entity.getId(),
                entity.getStatus(),
                Boolean.TRUE.equals(entity.getAccepted()),
                entity.getSummary(),
                entity.getLatestArchiveId(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestDeliveryReceiptId(),
                entity.getLatestDeliveryTarget(),
                entity.getLatestDeliveryChannel(),
                entity.getDeliveryReceiptFreshness(),
                entity.getCreatedAt(),
                entity.getReport()
        );
    }
}
