package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean finalized,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestReleaseBundleArchiveId,
        String latestDeliveryReceiptId,
        String latestCertificateArchiveId,
        String latestPackageArchiveId,
        String latestTaskId,
        String latestPullRequestUrl,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
    public DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEvidenceVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
