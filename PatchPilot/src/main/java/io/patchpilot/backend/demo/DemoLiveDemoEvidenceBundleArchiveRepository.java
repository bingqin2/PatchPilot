package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoLiveDemoEvidenceBundleArchiveRepository {

    DemoLiveDemoEvidenceBundleArchiveVo save(DemoLiveDemoEvidenceBundleArchiveVo archive);

    List<DemoLiveDemoEvidenceBundleArchiveVo> listRecentArchives(int limit);

    Optional<DemoLiveDemoEvidenceBundleArchiveVo> findById(String archiveId);
}
