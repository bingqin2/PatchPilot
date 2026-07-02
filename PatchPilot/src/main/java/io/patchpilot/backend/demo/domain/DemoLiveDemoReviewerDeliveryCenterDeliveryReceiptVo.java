package io.patchpilot.backend.demo.domain;

import java.time.Instant;

public record DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo(
        String id,
        String status,
        String reviewerDeliveryCenterArchiveId,
        String reviewerDeliveryCenterStatus,
        String repository,
        long issueNumber,
        String issueUrl,
        String taskId,
        String taskStatus,
        String pullRequestUrl,
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
