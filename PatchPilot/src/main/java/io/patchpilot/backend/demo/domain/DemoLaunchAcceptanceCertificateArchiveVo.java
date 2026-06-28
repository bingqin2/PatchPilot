package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLaunchAcceptanceCertificateArchiveVo(
        String id,
        DemoReadinessStatus status,
        boolean certified,
        String summary,
        String nextAction,
        int archiveCount,
        String latestCloseoutArchiveId,
        String latestLaunchEvidenceArchiveId,
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
        Instant archivedAt,
        List<String> downloadActions,
        String report
) {
}
