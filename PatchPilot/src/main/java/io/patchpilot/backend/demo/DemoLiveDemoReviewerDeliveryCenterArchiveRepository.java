package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLiveDemoReviewerDeliveryCenterArchiveRepository {

    DemoLiveDemoReviewerDeliveryCenterArchiveVo save(DemoLiveDemoReviewerDeliveryCenterArchiveVo archive);

    List<DemoLiveDemoReviewerDeliveryCenterArchiveVo> listRecentArchives(int limit);

    Optional<DemoLiveDemoReviewerDeliveryCenterArchiveVo> findById(String archiveId);
}
