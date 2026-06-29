package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewReleaseBundleArchiveEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean releaseReady,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestCertificateArchiveId,
        String latestDeliveryFinalizationArchiveId,
        String latestPackageArchiveId,
        String latestDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
    public DemoFinalExternalReviewReleaseBundleArchiveEvidenceVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
