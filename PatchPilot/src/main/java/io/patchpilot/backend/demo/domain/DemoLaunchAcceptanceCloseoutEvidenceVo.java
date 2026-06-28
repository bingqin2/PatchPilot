package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLaunchAcceptanceCloseoutEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean accepted,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestEvidenceArchiveId,
        String latestDeliveryReceiptId,
        String latestPullRequestUrl,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
}
