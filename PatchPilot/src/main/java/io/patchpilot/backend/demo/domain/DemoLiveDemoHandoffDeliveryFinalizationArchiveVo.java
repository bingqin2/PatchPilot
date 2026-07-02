package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoHandoffDeliveryFinalizationArchiveVo(
        String id,
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
        List<DemoLiveDemoHandoffDeliveryFinalizationVo.Check> checks,
        List<String> evidenceNotes,
        List<String> downloadActions,
        String sideEffectContract,
        Instant finalizationGeneratedAt,
        Instant archivedAt,
        String report
) {
    public DemoLiveDemoHandoffDeliveryFinalizationArchiveVo {
        checks = List.copyOf(checks);
        evidenceNotes = List.copyOf(evidenceNotes);
        downloadActions = List.copyOf(downloadActions);
    }
}
