package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;
import java.util.List;

public record FixTaskEvidencePackageShareCenterVo(
        String status,
        boolean shareReady,
        String summary,
        String nextAction,
        int archiveCount,
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
        String shareableArchiveId,
        String shareableTaskId,
        String shareableRepositoryOwner,
        String shareableRepositoryName,
        Long shareableIssueNumber,
        String shareablePullRequestUrl,
        List<String> downloadActions,
        List<String> evidenceNotes,
        String sideEffectContract,
        String markdownReport,
        Instant generatedAt
) {
}
