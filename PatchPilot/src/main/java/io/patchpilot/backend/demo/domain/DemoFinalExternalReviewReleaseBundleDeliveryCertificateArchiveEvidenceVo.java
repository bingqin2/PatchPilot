package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean certified,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestDeliveryFinalizationArchiveId,
        String latestReleaseBundleArchiveId,
        String latestDeliveryReceiptId,
        String latestCertificateArchiveId,
        String latestPackageArchiveId,
        String latestTaskId,
        String latestPullRequestUrl,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
    public DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEvidenceVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
