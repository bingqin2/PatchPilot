package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskEvidencePackageAcceptanceCloseoutArchiveVo(
        String id,
        String status,
        boolean accepted,
        String summary,
        String latestArchiveId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String deliveryReceiptFreshness,
        Instant createdAt,
        String report
) {
}
