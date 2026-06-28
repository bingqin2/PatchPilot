package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalHandoffReportPackageArchiveRepository {

    DemoFinalHandoffReportPackageArchiveVo save(DemoFinalHandoffReportPackageArchiveVo archive);

    List<DemoFinalHandoffReportPackageArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalHandoffReportPackageArchiveVo> findById(String archiveId);
}
