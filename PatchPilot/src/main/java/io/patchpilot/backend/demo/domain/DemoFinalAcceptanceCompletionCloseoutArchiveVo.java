package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalAcceptanceCompletionCloseoutArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean closed,
        String summary,
        String nextAction,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestSharePackageArchiveId,
        String latestCompletionArchiveId,
        String latestCompletionEvidenceDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String deliveryReceiptFreshness,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String report,
        Instant generatedAt,
        Instant archivedAt
) {
    public DemoFinalAcceptanceCompletionCloseoutArchiveVo {
        evidenceNotes = List.copyOf(evidenceNotes);
        downloadActions = List.copyOf(downloadActions);
    }
}
