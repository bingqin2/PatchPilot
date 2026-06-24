package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoSessionArchiveVo(
        String id,
        String sessionId,
        DemoReadinessStatus status,
        String summary,
        String shareSummary,
        String recentPullRequestUrl,
        Instant createdAt,
        String report
) {
}
