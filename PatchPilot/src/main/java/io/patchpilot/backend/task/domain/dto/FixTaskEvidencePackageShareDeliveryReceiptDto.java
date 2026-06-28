package io.patchpilot.backend.task.domain.dto;

import java.time.Instant;

public record FixTaskEvidencePackageShareDeliveryReceiptDto(
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        Instant deliveredAt
) {
}
