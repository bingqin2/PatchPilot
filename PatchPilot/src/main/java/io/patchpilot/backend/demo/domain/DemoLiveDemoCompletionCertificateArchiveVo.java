package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoCompletionCertificateArchiveVo(
        String id,
        String status,
        boolean certified,
        String summary,
        String nextAction,
        String latestFinalizationArchiveId,
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
        Instant latestFinalizationGeneratedAt,
        Instant latestFinalizationArchivedAt,
        Instant generatedAt,
        Instant archivedAt,
        List<String> downloadActions,
        String sideEffectContract,
        String report
) {
    public DemoLiveDemoCompletionCertificateArchiveVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
