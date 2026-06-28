package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalAcceptanceCompletionArchiveRepository {

    DemoFinalAcceptanceCompletionArchiveVo save(DemoFinalAcceptanceCompletionArchiveVo archive);

    List<DemoFinalAcceptanceCompletionArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalAcceptanceCompletionArchiveVo> findById(String archiveId);
}
