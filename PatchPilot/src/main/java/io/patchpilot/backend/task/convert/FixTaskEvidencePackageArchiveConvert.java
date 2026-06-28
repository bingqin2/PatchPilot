package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveVo;

public final class FixTaskEvidencePackageArchiveConvert {

    private FixTaskEvidencePackageArchiveConvert() {
    }

    public static FixTaskEvidencePackageArchiveEntity toEntity(FixTaskEvidencePackageArchiveVo archive) {
        FixTaskEvidencePackageArchiveEntity entity = new FixTaskEvidencePackageArchiveEntity();
        entity.setId(archive.id());
        entity.setTaskId(archive.taskId());
        entity.setRepositoryOwner(archive.repositoryOwner());
        entity.setRepositoryName(archive.repositoryName());
        entity.setIssueNumber(archive.issueNumber());
        entity.setStatus(archive.status());
        entity.setPullRequestUrl(archive.pullRequestUrl());
        entity.setArchivedAt(archive.archivedAt());
        entity.setSummary(archive.summary());
        entity.setReport(archive.report());
        return entity;
    }

    public static FixTaskEvidencePackageArchiveVo toVo(FixTaskEvidencePackageArchiveEntity entity) {
        return new FixTaskEvidencePackageArchiveVo(
                entity.getId(),
                entity.getTaskId(),
                entity.getRepositoryOwner(),
                entity.getRepositoryName(),
                entity.getIssueNumber(),
                entity.getStatus(),
                entity.getPullRequestUrl(),
                entity.getArchivedAt(),
                entity.getSummary(),
                entity.getReport()
        );
    }
}
