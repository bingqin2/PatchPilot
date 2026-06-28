package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalAcceptanceShareFinalizationVo(
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
        List<Check> checks,
        List<String> evidenceNotes,
        String markdownReport,
        Instant generatedAt
) {

    public record Check(
            String name,
            DemoReadinessStatus status,
            String summary,
            String nextAction
    ) {
    }
}
