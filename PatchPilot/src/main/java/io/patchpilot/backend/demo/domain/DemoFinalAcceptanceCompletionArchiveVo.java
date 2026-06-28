package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalAcceptanceCompletionArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean finalized,
        String summary,
        String nextAction,
        String latestArchiveId,
        String latestTaskId,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String deliveryReceiptFreshness,
        boolean deliveryReceiptFresh,
        String deliveryReceiptFreshnessSummary,
        List<String> evidenceNotes,
        String report,
        Instant generatedAt,
        Instant archivedAt
) {
}
