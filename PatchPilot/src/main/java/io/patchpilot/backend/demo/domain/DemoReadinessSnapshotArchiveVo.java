package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoReadinessSnapshotArchiveVo(
        String id,
        DemoReadinessStatus status,
        String summary,
        int readyCheckCount,
        int needsAttentionCheckCount,
        int blockedCheckCount,
        Instant createdAt,
        String report
) {
}
