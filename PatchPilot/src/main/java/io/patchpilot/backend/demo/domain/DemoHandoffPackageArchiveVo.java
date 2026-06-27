package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoHandoffPackageArchiveVo(
        String id,
        String sessionId,
        DemoReadinessStatus status,
        String summary,
        DemoReadinessStatus handoffReadinessStatus,
        String handoffReadinessSummary,
        String handoffReadinessNextAction,
        int handoffReadyCheckCount,
        int handoffNeedsAttentionCheckCount,
        int handoffBlockedCheckCount,
        String shareSummary,
        String recentPullRequestUrl,
        Instant createdAt,
        String report
) {

    public DemoHandoffPackageArchiveVo(
            String id,
            String sessionId,
            DemoReadinessStatus status,
            String summary,
            String shareSummary,
            String recentPullRequestUrl,
            Instant createdAt,
            String report
    ) {
        this(
                id,
                sessionId,
                status,
                summary,
                status,
                summary,
                "No handoff readiness metadata recorded.",
                0,
                0,
                0,
                shareSummary,
                recentPullRequestUrl,
                createdAt,
                report
        );
    }
}
