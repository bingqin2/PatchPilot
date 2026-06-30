package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo(
        String id,
        DemoReadinessStatus status,
        DemoReadinessStatus releaseBundleArchiveStatus,
        String releaseBundleArchiveId,
        String latestCertificateArchiveId,
        String latestDeliveryFinalizationArchiveId,
        String latestPackageArchiveId,
        String latestPackageDeliveryReceiptId,
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
