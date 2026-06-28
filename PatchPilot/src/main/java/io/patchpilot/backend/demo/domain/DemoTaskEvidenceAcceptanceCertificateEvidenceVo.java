package io.patchpilot.backend.demo.domain;

import java.time.Instant;
import java.util.List;

public record DemoTaskEvidenceAcceptanceCertificateEvidenceVo(
        DemoReadinessStatus status,
        boolean archived,
        boolean certified,
        String summary,
        String nextAction,
        int archiveCount,
        String latestArchiveId,
        String latestCloseoutArchiveId,
        String latestEvidenceArchiveId,
        String latestDeliveryReceiptId,
        String latestTaskId,
        String latestPullRequestUrl,
        Instant latestArchivedAt,
        List<String> downloadActions
) {
}
