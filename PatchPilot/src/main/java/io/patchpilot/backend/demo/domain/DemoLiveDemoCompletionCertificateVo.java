package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoLiveDemoCompletionCertificateVo(
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
        List<String> downloadActions,
        String sideEffectContract,
        String markdownReport
) {
    public DemoLiveDemoCompletionCertificateVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
