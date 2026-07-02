package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoLiveDemoHandoffDeliveryReceiptVo(
        String id,
        String status,
        String handoffPackageStatus,
        String evidenceBundleArchiveId,
        String repository,
        long issueNumber,
        String issueUrl,
        String triggerUser,
        String triggerComment,
        String taskId,
        String taskStatus,
        String pullRequestUrl,
        String webhookDeliveryId,
        String summary,
        String deliveryChannel,
        String deliveryTarget,
        String operator,
        String notes,
        Instant deliveredAt,
        Instant createdAt,
        String markdownReport
) {
}
