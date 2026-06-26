package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoHandoffPackageArchiveRepository {

    DemoHandoffPackageArchiveVo save(DemoHandoffPackageArchiveVo archive);

    List<DemoHandoffPackageArchiveVo> listRecentArchives(int limit);

    Optional<DemoHandoffPackageArchiveVo> findById(String archiveId);
}
