package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalExternalReviewReleaseBundleArchiveRepository {

    DemoFinalExternalReviewReleaseBundleArchiveVo save(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive
    );

    List<DemoFinalExternalReviewReleaseBundleArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalExternalReviewReleaseBundleArchiveVo> findById(String archiveId);
}
