package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLaunchAcceptanceCloseoutVo(
        DemoReadinessStatus status,
        boolean accepted,
        String summary,
        String nextAction,
        String sessionId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestWebhookDeliveryId,
        String evaluationRunId,
        String latestArchiveId,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String deliveryReceiptFreshness,
        Instant generatedAt,
        List<DemoLaunchAcceptanceCloseoutCheckVo> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String markdownReport
) {
}
