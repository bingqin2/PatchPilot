package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoLaunchEvidenceShareDeliveryReceiptVo(
        String id,
        String status,
        String launchEvidenceArchiveId,
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
