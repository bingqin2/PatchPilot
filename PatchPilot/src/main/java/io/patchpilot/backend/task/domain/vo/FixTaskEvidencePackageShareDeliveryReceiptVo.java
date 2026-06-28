package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskEvidencePackageShareDeliveryReceiptVo(
        String id,
        String status,
        String taskEvidenceArchiveId,
        String taskId,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String pullRequestUrl,
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        String messageSubject,
        Instant deliveredAt,
        Instant createdAt,
        String markdownReport
) {
}
