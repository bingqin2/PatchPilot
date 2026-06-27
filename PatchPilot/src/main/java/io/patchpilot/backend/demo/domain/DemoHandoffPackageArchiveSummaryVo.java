package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoHandoffPackageArchiveSummaryVo(
        String status,
        boolean shareReady,
        int archiveCount,
        String latestArchiveId,
        String latestSessionId,
        DemoReadinessStatus latestHandoffReadinessStatus,
        Instant latestCreatedAt,
        String summary,
        String nextAction,
        String markdownReport
) {
}
