package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;

import java.util.List;
import java.util.Optional;

public interface FixTaskEvidencePackageAcceptanceCertificateArchiveRepository {

    FixTaskEvidencePackageAcceptanceCertificateArchiveVo save(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive
    );

    List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> listRecentArchives(int limit);

    Optional<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> findById(String archiveId);
}
