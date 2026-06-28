package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoLaunchAcceptanceCloseoutArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean accepted,
        String summary,
        String sessionId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestWebhookDeliveryId,
        String evaluationRunId,
        String latestArchiveId,
        DemoReadinessStatus finalHandoffReportPackageArchiveStatus,
        boolean finalHandoffReportPackageArchiveReady,
        String finalHandoffReportPackageArchiveId,
        String finalHandoffReportPackageArchiveSummary,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String deliveryReceiptFreshness,
        Instant createdAt,
        String report
) {
}
