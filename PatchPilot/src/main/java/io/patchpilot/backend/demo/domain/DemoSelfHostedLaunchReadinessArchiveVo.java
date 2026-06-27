package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoSelfHostedLaunchReadinessArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean readyToLaunch,
        String summary,
        int readyCheckCount,
        int needsAttentionCheckCount,
        int blockedCheckCount,
        Instant createdAt,
        String report
) {
}
