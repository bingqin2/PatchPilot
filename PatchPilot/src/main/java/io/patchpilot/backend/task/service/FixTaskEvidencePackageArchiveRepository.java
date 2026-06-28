package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveVo;

import java.util.List;
import java.util.Optional;

public interface FixTaskEvidencePackageArchiveRepository {

    FixTaskEvidencePackageArchiveVo save(FixTaskEvidencePackageArchiveVo archive);

    List<FixTaskEvidencePackageArchiveVo> listByTaskId(String taskId, int limit);

    Optional<FixTaskEvidencePackageArchiveVo> findById(String archiveId);
}
