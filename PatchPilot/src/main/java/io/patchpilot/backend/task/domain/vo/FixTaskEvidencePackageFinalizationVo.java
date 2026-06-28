package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;
import java.util.List;

public record FixTaskEvidencePackageFinalizationVo(
        String status,
        boolean finalized,
        String summary,
        String nextAction,
        String latestArchiveId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String deliveryReceiptFreshness,
        boolean deliveryReceiptFresh,
        String deliveryReceiptFreshnessSummary,
        List<FixTaskEvidencePackageFinalizationCheckVo> checks,
        List<String> evidenceNotes,
        String markdownReport,
        Instant generatedAt
) {
}
