package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoSelfHostedLaunchReadinessArchiveEntity;

public final class DemoSelfHostedLaunchReadinessArchiveConvert {

    private DemoSelfHostedLaunchReadinessArchiveConvert() {
    }

    public static DemoSelfHostedLaunchReadinessArchiveEntity toEntity(DemoSelfHostedLaunchReadinessArchiveVo archive) {
        DemoSelfHostedLaunchReadinessArchiveEntity entity = new DemoSelfHostedLaunchReadinessArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status().name());
        entity.setReadyToLaunch(archive.readyToLaunch());
        entity.setSummary(archive.summary());
        entity.setReadyCheckCount(archive.readyCheckCount());
        entity.setNeedsAttentionCheckCount(archive.needsAttentionCheckCount());
        entity.setBlockedCheckCount(archive.blockedCheckCount());
        entity.setCreatedAt(archive.createdAt());
        entity.setReport(archive.report());
        return entity;
    }

    public static DemoSelfHostedLaunchReadinessArchiveVo toVo(DemoSelfHostedLaunchReadinessArchiveEntity entity) {
        return new DemoSelfHostedLaunchReadinessArchiveVo(
                entity.getId(),
                DemoReadinessStatus.valueOf(entity.getStatus()),
                Boolean.TRUE.equals(entity.getReadyToLaunch()),
                entity.getSummary(),
                countOrZero(entity.getReadyCheckCount()),
                countOrZero(entity.getNeedsAttentionCheckCount()),
                countOrZero(entity.getBlockedCheckCount()),
                entity.getCreatedAt(),
                entity.getReport()
        );
    }

    private static int countOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
