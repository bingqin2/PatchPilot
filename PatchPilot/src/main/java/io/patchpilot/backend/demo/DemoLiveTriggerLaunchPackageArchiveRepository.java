package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLiveTriggerLaunchPackageArchiveRepository {

    DemoLiveTriggerLaunchPackageArchiveVo save(DemoLiveTriggerLaunchPackageArchiveVo archive);

    List<DemoLiveTriggerLaunchPackageArchiveVo> listRecentArchives(int limit);

    Optional<DemoLiveTriggerLaunchPackageArchiveVo> findById(String archiveId);
}
