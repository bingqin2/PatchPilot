package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo(
        DemoReadinessStatus status,
        boolean finalized,
        String summary,
        String nextAction,
        String latestCompletionArchiveId,
        String latestSharePackageArchiveId,
        String latestDeliveryReceiptId,
        String latestTaskId,
        String latestCompletionEvidenceDeliveryReceiptId,
        String latestDeliveryTarget,
        String latestDeliveryChannel,
        String latestDeliveredAt,
        String deliveryReceiptFreshness,
        boolean deliveryReceiptFresh,
        String deliveryReceiptFreshnessSummary,
        List<Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport,
        Instant generatedAt
) {

    public DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo {
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
