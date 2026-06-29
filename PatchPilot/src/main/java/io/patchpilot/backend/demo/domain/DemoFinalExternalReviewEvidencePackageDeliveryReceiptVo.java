package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo(
        String id,
        DemoReadinessStatus status,
        DemoReadinessStatus finalExternalReviewPackageArchiveStatus,
        String finalExternalReviewPackageArchiveId,
        String closeoutArchiveId,
        String completionArchiveId,
        String completionEvidenceDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        String summary,
        String nextAction,
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        Instant deliveredAt,
        Instant createdAt,
        String markdownReport
) {
}
