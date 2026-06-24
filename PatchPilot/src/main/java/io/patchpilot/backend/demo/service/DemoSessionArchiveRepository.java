package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;

import java.util.List;

public interface DemoSessionArchiveRepository {

    DemoSessionArchiveVo save(DemoSessionArchiveVo archive);

    List<DemoSessionArchiveVo> listRecentArchives(int limit);
}
