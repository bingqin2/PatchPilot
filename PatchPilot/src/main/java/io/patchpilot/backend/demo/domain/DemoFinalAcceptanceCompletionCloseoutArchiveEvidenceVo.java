package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean closed,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestCompletionArchiveId,
        String latestCompletionEvidenceDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
    public DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo {
        downloadActions = List.copyOf(downloadActions);
    }
}
