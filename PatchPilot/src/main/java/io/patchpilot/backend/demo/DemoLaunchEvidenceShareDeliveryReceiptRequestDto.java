package io.patchpilot.backend.demo;

import java.time.Instant;

public record DemoLaunchEvidenceShareDeliveryReceiptRequestDto(
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        Instant deliveredAt
) {
}
