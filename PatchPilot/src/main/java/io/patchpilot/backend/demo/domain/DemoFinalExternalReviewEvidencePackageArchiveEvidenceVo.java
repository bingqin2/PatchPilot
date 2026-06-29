package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean readyForExternalReview,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestCloseoutArchiveId,
        String latestCompletionArchiveId,
        String latestCompletionEvidenceDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
    public DemoFinalExternalReviewEvidencePackageArchiveEvidenceVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
