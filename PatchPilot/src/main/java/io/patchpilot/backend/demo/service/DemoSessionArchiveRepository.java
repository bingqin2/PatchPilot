package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoSessionArchiveRepository {

    DemoSessionArchiveVo save(DemoSessionArchiveVo archive);

    List<DemoSessionArchiveVo> listRecentArchives(int limit);

    Optional<DemoSessionArchiveVo> findById(String archiveId);
}
