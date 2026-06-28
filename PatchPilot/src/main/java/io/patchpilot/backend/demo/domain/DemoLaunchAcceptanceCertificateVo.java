package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLaunchAcceptanceCertificateVo(
        DemoReadinessStatus status,
        boolean certified,
        String summary,
        String nextAction,
        int archiveCount,
        String latestCloseoutArchiveId,
        String latestLaunchEvidenceArchiveId,
        DemoReadinessStatus finalHandoffReportPackageArchiveStatus,
        boolean finalHandoffReportPackageArchiveReady,
        String finalHandoffReportPackageArchiveId,
        String finalHandoffReportPackageArchiveSummary,
        String latestDeliveryReceiptId,
        String latestSessionId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestWebhookDeliveryId,
        String evaluationRunId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String deliveryReceiptFreshness,
        Instant latestArchivedAt,
        Instant generatedAt,
        List<String> downloadActions,
        String markdownReport
) {
}
