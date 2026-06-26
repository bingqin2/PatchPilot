package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoReadinessSnapshotArchiveEntity;

public final class DemoReadinessSnapshotArchiveConvert {

    private DemoReadinessSnapshotArchiveConvert() {
    }

    public static DemoReadinessSnapshotArchiveEntity toEntity(DemoReadinessSnapshotArchiveVo archive) {
        DemoReadinessSnapshotArchiveEntity entity = new DemoReadinessSnapshotArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setSummary(archive.summary());
        entity.setReadyCheckCount(archive.readyCheckCount());
        entity.setNeedsAttentionCheckCount(archive.needsAttentionCheckCount());
        entity.setBlockedCheckCount(archive.blockedCheckCount());
        entity.setCreatedAt(archive.createdAt());
        entity.setReport(archive.report());
        return entity;
    }

    public static DemoReadinessSnapshotArchiveVo toVo(DemoReadinessSnapshotArchiveEntity entity) {
        return new DemoReadinessSnapshotArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                entity.getSummary(),
                entity.getReadyCheckCount(),
                entity.getNeedsAttentionCheckCount(),
                entity.getBlockedCheckCount(),
                entity.getCreatedAt(),
                entity.getReport()
        );
    }
}
