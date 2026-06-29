package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalAcceptanceCompletionCloseoutArchiveRepository {

    DemoFinalAcceptanceCompletionCloseoutArchiveVo save(DemoFinalAcceptanceCompletionCloseoutArchiveVo archive);

    List<DemoFinalAcceptanceCompletionCloseoutArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalAcceptanceCompletionCloseoutArchiveVo> findById(String archiveId);
}
