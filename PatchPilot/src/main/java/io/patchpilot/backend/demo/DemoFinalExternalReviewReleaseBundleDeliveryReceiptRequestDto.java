package io.patchpilot.backend.demo;

import java.time.Instant;

public record DemoFinalExternalReviewReleaseBundleDeliveryReceiptRequestDto(
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        Instant deliveredAt
) {
}
