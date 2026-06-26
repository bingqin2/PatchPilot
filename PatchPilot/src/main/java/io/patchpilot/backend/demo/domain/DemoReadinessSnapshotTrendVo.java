package io.patchpilot.backend.demo.domain;

public record DemoReadinessSnapshotTrendVo(
        DemoReadinessSnapshotTrendStatus status,
        String summary,
        String latestSnapshotId,
        String previousSnapshotId,
        DemoReadinessStatus latestReadinessStatus,
        DemoReadinessStatus previousReadinessStatus,
        int readyCheckDelta,
        int needsAttentionCheckDelta,
        int blockedCheckDelta,
        String nextAction,
        String markdownReport
) {
}
