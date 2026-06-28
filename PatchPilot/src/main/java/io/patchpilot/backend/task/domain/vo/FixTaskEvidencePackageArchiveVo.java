package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskEvidencePackageArchiveVo(
        String id,
        String taskId,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String status,
        String pullRequestUrl,
        Instant archivedAt,
        String summary,
        String report
) {
}
