package io.patchpilot.backend.security.exposure.domain;

import java.time.Instant;

public record ExternalExposureReadinessArchiveVo(
        String id,
        String status,
        boolean safeToExpose,
        String summary,
        int readyCount,
        int needsAttentionCount,
        int blockedCount,
        int totalCount,
        Instant createdAt,
        String report
) {
}
