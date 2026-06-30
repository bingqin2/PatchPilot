package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository {

    DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo save(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo archive
    );

    List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo> findById(String archiveId);
}
