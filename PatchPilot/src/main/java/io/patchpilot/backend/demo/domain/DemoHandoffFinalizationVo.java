package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoHandoffFinalizationVo(
        DemoReadinessStatus status,
        boolean finalized,
        String summary,
        String nextAction,
        String latestArchiveId,
        String latestSessionId,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String deliveryReceiptFreshness,
        boolean deliveryReceiptFresh,
        String deliveryReceiptFreshnessSummary,
        List<DemoHandoffFinalizationCheckVo> checks,
        List<String> evidenceNotes,
        String markdownReport,
        Instant generatedAt
) {
}
