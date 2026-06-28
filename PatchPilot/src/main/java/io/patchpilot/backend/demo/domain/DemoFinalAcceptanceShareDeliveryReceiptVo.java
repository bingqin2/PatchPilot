package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoFinalAcceptanceShareDeliveryReceiptVo(
        String id,
        DemoReadinessStatus status,
        String finalAcceptanceSharePackageArchiveId,
        String latestTaskId,
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        String messageSubject,
        Instant deliveredAt,
        Instant createdAt,
        String markdownReport
) {
}
