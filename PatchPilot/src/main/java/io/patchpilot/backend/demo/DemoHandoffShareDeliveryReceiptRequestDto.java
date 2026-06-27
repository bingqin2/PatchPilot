package io.patchpilot.backend.demo;

import java.time.Instant;

public record DemoHandoffShareDeliveryReceiptRequestDto(
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        Instant deliveredAt
) {
}
