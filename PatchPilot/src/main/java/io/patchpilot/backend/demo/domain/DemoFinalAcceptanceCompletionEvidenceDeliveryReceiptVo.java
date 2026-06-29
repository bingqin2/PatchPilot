package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo(
        String id,
        DemoReadinessStatus status,
        boolean readyToShare,
        DemoReadinessStatus completionEvidenceBundleStatus,
        String summary,
        String nextAction,
        String latestCompletionArchiveId,
        String latestSharePackageArchiveId,
        String latestDeliveryReceiptId,
        String latestTaskId,
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        Instant deliveredAt,
        Instant createdAt,
        String markdownReport
) {
}
