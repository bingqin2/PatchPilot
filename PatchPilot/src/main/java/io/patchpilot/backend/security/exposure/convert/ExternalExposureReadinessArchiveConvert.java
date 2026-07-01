package io.patchpilot.backend.security.exposure.convert;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureReadinessArchiveEntity;

public final class ExternalExposureReadinessArchiveConvert {

    private ExternalExposureReadinessArchiveConvert() {
    }

    public static ExternalExposureReadinessArchiveEntity toEntity(ExternalExposureReadinessArchiveVo archive) {
        ExternalExposureReadinessArchiveEntity entity = new ExternalExposureReadinessArchiveEntity();
        entity.setId(archive.id());
        entity.setStatus(archive.status());
        entity.setSafeToExpose(archive.safeToExpose());
        entity.setSummary(archive.summary());
        entity.setReadyCount(archive.readyCount());
        entity.setNeedsAttentionCount(archive.needsAttentionCount());
        entity.setBlockedCount(archive.blockedCount());
        entity.setTotalCount(archive.totalCount());
        entity.setCreatedAt(archive.createdAt());
        entity.setReport(archive.report());
        return entity;
    }

    public static ExternalExposureReadinessArchiveVo toVo(ExternalExposureReadinessArchiveEntity entity) {
        return new ExternalExposureReadinessArchiveVo(
                entity.getId(),
                entity.getStatus(),
                Boolean.TRUE.equals(entity.getSafeToExpose()),
                entity.getSummary(),
                countOrZero(entity.getReadyCount()),
                countOrZero(entity.getNeedsAttentionCount()),
                countOrZero(entity.getBlockedCount()),
                countOrZero(entity.getTotalCount()),
                entity.getCreatedAt(),
                entity.getReport()
        );
    }

    private static int countOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
