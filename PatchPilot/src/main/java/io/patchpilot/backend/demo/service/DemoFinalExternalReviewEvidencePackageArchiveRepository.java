package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalExternalReviewEvidencePackageArchiveRepository {

    DemoFinalExternalReviewEvidencePackageArchiveVo save(DemoFinalExternalReviewEvidencePackageArchiveVo archive);

    List<DemoFinalExternalReviewEvidencePackageArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalExternalReviewEvidencePackageArchiveVo> findById(String archiveId);
}
