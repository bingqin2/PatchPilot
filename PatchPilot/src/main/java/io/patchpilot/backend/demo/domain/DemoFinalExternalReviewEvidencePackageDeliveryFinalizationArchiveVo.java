package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean finalized,
        String summary,
        String nextAction,
        String latestArchiveId,
        String latestDeliveryReceiptId,
        String latestCloseoutArchiveId,
        String latestCompletionArchiveId,
        String latestCompletionEvidenceDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String deliveryReceiptFreshness,
        boolean deliveryReceiptFresh,
        String deliveryReceiptFreshnessSummary,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String report,
        Instant generatedAt,
        Instant archivedAt
) {
    public DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo {
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
