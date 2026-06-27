package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoHandoffShareDeliveryReceiptVo(
        String id,
        DemoReadinessStatus status,
        String handoffArchiveId,
        String sessionId,
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
