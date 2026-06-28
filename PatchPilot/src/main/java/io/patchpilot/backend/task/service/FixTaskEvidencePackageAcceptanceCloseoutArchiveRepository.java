package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;

import java.util.List;
import java.util.Optional;

public interface FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository {

    FixTaskEvidencePackageAcceptanceCloseoutArchiveVo save(
            FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive
    );

    List<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> listRecentArchives(int limit);

    Optional<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> findById(String archiveId);
}
