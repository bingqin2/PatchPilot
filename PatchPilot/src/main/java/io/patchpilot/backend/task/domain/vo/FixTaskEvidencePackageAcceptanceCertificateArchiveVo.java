package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;
import java.util.List;

public record FixTaskEvidencePackageAcceptanceCertificateArchiveVo(
        String id,
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
        Instant archivedAt,
        List<String> downloadActions,
        String report
) {
}
