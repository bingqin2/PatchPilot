package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLiveTriggerOutcomeCloseoutArchiveRepository {

    DemoLiveTriggerOutcomeCloseoutArchiveVo save(DemoLiveTriggerOutcomeCloseoutArchiveVo archive);

    List<DemoLiveTriggerOutcomeCloseoutArchiveVo> listRecentArchives(int limit);

    Optional<DemoLiveTriggerOutcomeCloseoutArchiveVo> findById(String archiveId);
}
