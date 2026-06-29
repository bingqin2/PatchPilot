package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean finalized,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestPackageArchiveId,
        String latestDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
    public DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidenceVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
