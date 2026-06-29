package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo(
        DemoReadinessStatus status,
        boolean recorded,
        boolean fresh,
        String freshness,
        String summary,
        String nextAction,
        int receiptCount,
        String latestReceiptId,
        String latestPackageArchiveId,
        String latestCloseoutArchiveId,
        String latestCompletionArchiveId,
        String latestCompletionEvidenceDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        Instant latestDeliveredAt,
        List<String> downloadActions
) {
    public DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidenceVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
