package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskEvidencePackageArchiveSummaryVo(
        int totalArchiveCount,
        int completedArchiveCount,
        int failedArchiveCount,
        int pendingReviewArchiveCount,
        int cancelledArchiveCount,
        String latestArchiveId,
        String latestTaskId,
        String latestRepositoryOwner,
        String latestRepositoryName,
        Long latestIssueNumber,
        Instant latestArchivedAt,
        String sideEffectContract,
        String nextAction
) {
}
