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
                entity.getShareSummary(),
                entity.getRecentPullRequestUrl(),
                entity.getCreatedAt(),
                entity.getReport()
        );
    }
}
