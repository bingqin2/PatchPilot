package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoHandoffPackageArchiveEntity;

public final class DemoHandoffPackageArchiveConvert {

    private DemoHandoffPackageArchiveConvert() {
    }

    public static DemoHandoffPackageArchiveEntity toEntity(DemoHandoffPackageArchiveVo archive) {
        DemoHandoffPackageArchiveEntity entity = new DemoHandoffPackageArchiveEntity();
        entity.setId(archive.id());
        entity.setSessionId(archive.sessionId());
        entity.setStatus(archive.status().name());
        entity.setSummary(archive.summary());
        entity.setHandoffReadinessStatus(archive.handoffReadinessStatus().name());
        entity.setHandoffReadinessSummary(archive.handoffReadinessSummary());
        entity.setHandoffReadinessNextAction(archive.handoffReadinessNextAction());
        entity.setHandoffReadyCheckCount(archive.handoffReadyCheckCount());
        entity.setHandoffNeedsAttentionCheckCount(archive.handoffNeedsAttentionCheckCount());
        entity.setHandoffBlockedCheckCount(archive.handoffBlockedCheckCount());
        entity.setShareSummary(archive.shareSummary());
        entity.setRecentPullRequestUrl(archive.recentPullRequestUrl());
        entity.setCreatedAt(archive.createdAt());
        entity.setReport(archive.report());
        return entity;
    }

    public static DemoHandoffPackageArchiveVo toVo(DemoHandoffPackageArchiveEntity entity) {
        return new DemoHandoffPackageArchiveVo(
                entity.getId(),
                entity.getSessionId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                entity.getSummary(),
                readinessStatus(entity),
                valueOrDefault(entity.getHandoffReadinessSummary(), entity.getSummary()),
                valueOrDefault(entity.getHandoffReadinessNextAction(), "No handoff readiness metadata recorded."),
                countOrZero(entity.getHandoffReadyCheckCount()),
                countOrZero(entity.getHandoffNeedsAttentionCheckCount()),
                countOrZero(entity.getHandoffBlockedCheckCount()),
                entity.getShareSummary(),
                entity.getRecentPullRequestUrl(),
                entity.getCreatedAt(),
                entity.getReport()
        );
    }

    private static DemoReadinessStatus readinessStatus(DemoHandoffPackageArchiveEntity entity) {
        String status = entity.getHandoffReadinessStatus();
        if (status == null || status.isBlank()) {
            status = entity.getStatus();
        }
        return DemoReadinessStatus.valueOf(status);
    }

    private static String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static int countOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
