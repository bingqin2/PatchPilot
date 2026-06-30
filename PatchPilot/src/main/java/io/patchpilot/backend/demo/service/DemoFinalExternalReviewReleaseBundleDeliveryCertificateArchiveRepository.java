package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository {

    DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo save(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive
    );

    List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo> findById(String archiveId);
}
