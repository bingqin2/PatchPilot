package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLaunchEvidencePackageArchiveRepository {

    DemoLaunchEvidencePackageArchiveVo save(DemoLaunchEvidencePackageArchiveVo archive);

    List<DemoLaunchEvidencePackageArchiveVo> listRecentArchives(int limit);

    Optional<DemoLaunchEvidencePackageArchiveVo> findById(String archiveId);
}
