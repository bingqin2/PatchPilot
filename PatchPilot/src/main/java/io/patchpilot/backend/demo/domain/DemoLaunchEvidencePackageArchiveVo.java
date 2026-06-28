package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoLaunchEvidencePackageArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean readyToShare,
        String summary,
        String sessionId,
        DemoReadinessStatus launchReadinessStatus,
        DemoReadinessStatus evidenceBundleStatus,
        DemoReadinessStatus handoffFinalizationStatus,
        DemoReadinessStatus finalHandoffReportPackageArchiveStatus,
        boolean finalHandoffReportPackageArchiveReady,
        String finalHandoffReportPackageArchiveId,
        String finalHandoffReportPackageArchiveSummary,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestWebhookDeliveryId,
        String evaluationRunId,
        Instant createdAt,
        String report
) {
}
