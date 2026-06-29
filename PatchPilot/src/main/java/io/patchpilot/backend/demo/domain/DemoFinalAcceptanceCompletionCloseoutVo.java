package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalAcceptanceCompletionCloseoutVo(
        DemoReadinessStatus status,
        boolean closed,
        String summary,
        String nextAction,
        String latestTaskId,
        String latestPullRequestUrl,
        String latestSharePackageArchiveId,
        String latestCompletionArchiveId,
        String latestCompletionEvidenceDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String deliveryReceiptFreshness,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport,
        Instant generatedAt
) {

    public DemoFinalAcceptanceCompletionCloseoutVo {
        checks = List.copyOf(checks);
        evidenceNotes = List.copyOf(evidenceNotes);
        downloadActions = List.copyOf(downloadActions);
    }

    public record Check(
            String name,
            DemoReadinessStatus status,
            String summary,
            String nextAction
    ) {
    }
}
