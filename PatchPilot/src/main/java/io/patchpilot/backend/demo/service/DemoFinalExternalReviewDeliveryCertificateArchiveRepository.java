package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalExternalReviewDeliveryCertificateArchiveRepository {

    DemoFinalExternalReviewDeliveryCertificateArchiveVo save(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo archive
    );

    List<DemoFinalExternalReviewDeliveryCertificateArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalExternalReviewDeliveryCertificateArchiveVo> findById(String archiveId);
}
