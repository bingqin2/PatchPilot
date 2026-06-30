package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo(
        DemoReadinessStatus status,
        boolean certified,
        String summary,
        String nextAction,
        String latestDeliveryFinalizationArchiveId,
        String latestReleaseBundleArchiveId,
        String latestDeliveryReceiptId,
        String latestCertificateArchiveId,
        String latestPackageArchiveId,
        String latestPackageDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        Instant latestArchivedAt,
        String releaseBundleDeliveryReceiptFreshness,
        boolean releaseBundleDeliveryReceiptFresh,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport,
        Instant generatedAt
) {
    public DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo {
        checks = List.copyOf(checks);
        evidenceNotes = List.copyOf(evidenceNotes);
        downloadActions = List.copyOf(downloadActions);
    }

    public record Check(
            String name,
            DemoReadinessStatus status,
            String summary,
            String nextAction
    ) {
    }
}
