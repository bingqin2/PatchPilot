package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLaunchAcceptanceCloseoutArchiveRepository {

    DemoLaunchAcceptanceCloseoutArchiveVo save(DemoLaunchAcceptanceCloseoutArchiveVo archive);

    List<DemoLaunchAcceptanceCloseoutArchiveVo> listRecentArchives(int limit);

    Optional<DemoLaunchAcceptanceCloseoutArchiveVo> findById(String archiveId);
}
