package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;
import java.util.List;

public record FixTaskEvidencePackageAcceptanceCertificateVo(
        String status,
        boolean certified,
        String summary,
        String nextAction,
        int archiveCount,
        String latestCloseoutArchiveId,
        String latestEvidenceArchiveId,
        String latestDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String deliveryReceiptFreshness,
        Instant latestArchivedAt,
        Instant generatedAt,
        List<String> downloadActions,
        String markdownReport
) {
}
