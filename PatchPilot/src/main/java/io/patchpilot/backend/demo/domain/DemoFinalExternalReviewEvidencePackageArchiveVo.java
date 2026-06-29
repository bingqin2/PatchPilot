package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewEvidencePackageArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean readyForExternalReview,
        String summary,
        String nextAction,
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
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String report,
        Instant generatedAt,
        Instant archivedAt
) {
    public DemoFinalExternalReviewEvidencePackageArchiveVo {
        evidenceNotes = List.copyOf(evidenceNotes);
        downloadActions = List.copyOf(downloadActions);
    }
}
