package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewEvidencePackageVo(
        DemoReadinessStatus status,
        boolean readyForExternalReview,
        String summary,
        String nextAction,
        DemoReadinessStatus finalAcceptanceSummaryStatus,
        DemoReadinessStatus finalAcceptanceShareFinalizationStatus,
        DemoReadinessStatus completionEvidenceBundleStatus,
        DemoReadinessStatus completionDeliveryFinalizationStatus,
        DemoReadinessStatus completionCloseoutStatus,
        DemoReadinessStatus closeoutArchiveStatus,
        String latestTaskId,
        String latestPullRequestUrl,
        String finalAcceptanceSharePackageArchiveId,
        String completionArchiveId,
        String completionEvidenceDeliveryReceiptId,
        String closeoutArchiveId,
        String deliveryTarget,
        String deliveryChannel,
        String deliveredAt,
        String deliveryReceiptFreshness,
        Instant closeoutArchivedAt,
        Instant generatedAt,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport
) {
    public DemoFinalExternalReviewEvidencePackageVo {
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
