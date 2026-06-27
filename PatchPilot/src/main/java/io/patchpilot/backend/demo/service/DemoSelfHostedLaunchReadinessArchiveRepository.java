package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoSelfHostedLaunchReadinessArchiveRepository {

    DemoSelfHostedLaunchReadinessArchiveVo save(DemoSelfHostedLaunchReadinessArchiveVo archive);

    List<DemoSelfHostedLaunchReadinessArchiveVo> listRecentArchives(int limit);

    Optional<DemoSelfHostedLaunchReadinessArchiveVo> findById(String archiveId);
}
