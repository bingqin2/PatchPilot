package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoFinalReviewerHandoffDeliveryReceiptVo(
        String id,
        DemoReadinessStatus status,
        DemoReadinessStatus handoffPackageStatus,
        String latestCertificateArchiveId,
        String latestDeliveryFinalizationArchiveId,
        String latestReleaseBundleArchiveId,
        String latestDeliveryReceiptId,
        String latestPackageCertificateArchiveId,
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
