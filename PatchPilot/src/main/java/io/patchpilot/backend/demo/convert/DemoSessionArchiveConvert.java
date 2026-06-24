package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoSessionArchiveEntity;

public final class DemoSessionArchiveConvert {

    private DemoSessionArchiveConvert() {
    }

    public static DemoSessionArchiveEntity toEntity(DemoSessionArchiveVo archive) {
        DemoSessionArchiveEntity entity = new DemoSessionArchiveEntity();
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

    public static DemoSessionArchiveVo toVo(DemoSessionArchiveEntity entity) {
        return new DemoSessionArchiveVo(
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
