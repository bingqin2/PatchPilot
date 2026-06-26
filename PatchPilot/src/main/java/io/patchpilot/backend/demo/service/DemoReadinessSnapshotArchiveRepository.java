package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoReadinessSnapshotArchiveRepository {

    DemoReadinessSnapshotArchiveVo save(DemoReadinessSnapshotArchiveVo archive);

    List<DemoReadinessSnapshotArchiveVo> listRecentArchives(int limit);

    Optional<DemoReadinessSnapshotArchiveVo> findById(String archiveId);
}
