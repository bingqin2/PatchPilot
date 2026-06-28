package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalAcceptanceSharePackageArchiveRepository {

    DemoFinalAcceptanceSharePackageArchiveVo save(DemoFinalAcceptanceSharePackageArchiveVo archive);

    List<DemoFinalAcceptanceSharePackageArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalAcceptanceSharePackageArchiveVo> findById(String archiveId);
}
