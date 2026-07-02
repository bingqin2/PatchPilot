package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoHandoffDeliveryFinalizationVo(
        String status,
        boolean finalized,
        String summary,
        String nextAction,
        String latestDeliveryReceiptId,
        String evidenceBundleArchiveId,
        String repository,
        long issueNumber,
        String issueUrl,
        String taskId,
        String taskStatus,
        String pullRequestUrl,
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
    public DemoLiveDemoHandoffDeliveryFinalizationVo {
        checks = List.copyOf(checks);
        evidenceNotes = List.copyOf(evidenceNotes);
        downloadActions = List.copyOf(downloadActions);
    }

    public record Check(
            String name,
            String status,
            String summary,
            String nextAction
    ) {
    }
}
