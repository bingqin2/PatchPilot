package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchEvidencePackageArchiveEntity;

public final class DemoLaunchEvidencePackageArchiveConvert {

    private DemoLaunchEvidencePackageArchiveConvert() {
    }

    public static DemoLaunchEvidencePackageArchiveEntity toEntity(DemoLaunchEvidencePackageArchiveVo archive) {
        DemoLaunchEvidencePackageArchiveEntity entity = new DemoLaunchEvidencePackageArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setReadyToShare(archive.readyToShare());
        entity.setSummary(archive.summary());
        entity.setSessionId(archive.sessionId());
        entity.setLaunchReadinessStatus(archive.launchReadinessStatus().name());
        entity.setEvidenceBundleStatus(archive.evidenceBundleStatus().name());
        entity.setHandoffFinalizationStatus(archive.handoffFinalizationStatus().name());
        entity.setFinalHandoffReportPackageArchiveStatus(archive.finalHandoffReportPackageArchiveStatus().name());
        entity.setFinalHandoffReportPackageArchiveReady(archive.finalHandoffReportPackageArchiveReady());
        entity.setFinalHandoffReportPackageArchiveId(archive.finalHandoffReportPackageArchiveId());
        entity.setFinalHandoffReportPackageArchiveSummary(archive.finalHandoffReportPackageArchiveSummary());
        entity.setLatestTaskId(archive.latestTaskId());
        entity.setLatestPullRequestUrl(archive.latestPullRequestUrl());
        entity.setLatestWebhookDeliveryId(archive.latestWebhookDeliveryId());
        entity.setEvaluationRunId(archive.evaluationRunId());
        entity.setCreatedAt(archive.createdAt());
        entity.setReport(archive.report());
        return entity;
    }

    public static DemoLaunchEvidencePackageArchiveVo toVo(DemoLaunchEvidencePackageArchiveEntity entity) {
        return new DemoLaunchEvidencePackageArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getReadyToShare()),
                entity.getSummary(),
                entity.getSessionId(),
                DemoReadinessStatus.valueOf(entity.getLaunchReadinessStatus()),
                DemoReadinessStatus.valueOf(entity.getEvidenceBundleStatus()),
                DemoReadinessStatus.valueOf(entity.getHandoffFinalizationStatus()),
                DemoReadinessStatus.valueOf(entity.getFinalHandoffReportPackageArchiveStatus()),
                Boolean.TRUE.equals(entity.getFinalHandoffReportPackageArchiveReady()),
                entity.getFinalHandoffReportPackageArchiveId(),
                entity.getFinalHandoffReportPackageArchiveSummary(),
                entity.getLatestTaskId(),
                entity.getLatestPullRequestUrl(),
                entity.getLatestWebhookDeliveryId(),
                entity.getEvaluationRunId(),
                entity.getCreatedAt(),
                entity.getReport()
        );
    }
}
