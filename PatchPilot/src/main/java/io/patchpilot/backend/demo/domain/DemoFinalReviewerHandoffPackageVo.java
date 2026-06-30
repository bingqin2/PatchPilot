package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalReviewerHandoffPackageVo(
        DemoReadinessStatus status,
        boolean readyForReview,
        String summary,
        String nextAction,
        String latestCertificateArchiveId,
        String latestDeliveryFinalizationArchiveId,
        String latestReleaseBundleArchiveId,
        String latestDeliveryReceiptId,
        String latestPackageCertificateArchiveId,
        String latestPackageArchiveId,
        String latestPackageDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        Instant latestArchivedAt,
        List<String> requiredAttachments,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport,
        Instant generatedAt
) {
    public DemoFinalReviewerHandoffPackageVo {
        requiredAttachments = List.copyOf(requiredAttachments);
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
