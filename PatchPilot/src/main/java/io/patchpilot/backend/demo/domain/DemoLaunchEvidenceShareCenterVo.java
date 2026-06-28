package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLaunchEvidenceShareCenterVo(
        String status,
        boolean shareReady,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestSessionId,
        String latestCreatedAt,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestWebhookDeliveryId,
        String evaluationRunId,
        DemoReadinessStatus finalHandoffReportPackageArchiveStatus,
        boolean finalHandoffReportPackageArchiveReady,
        String finalHandoffReportPackageArchiveId,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        boolean deliveryReceiptRecorded,
        String deliveryReceiptFreshness,
        boolean deliveryReceiptFresh,
        String deliveryReceiptFreshnessSummary,
        List<String> downloadActions,
        List<String> evidenceNotes,
        String markdownReport,
        Instant generatedAt
) {
}
