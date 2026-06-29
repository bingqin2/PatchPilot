package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository {

    DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo save(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive
    );

    List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> listRecentArchives(int limit);

    Optional<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> findById(String archiveId);
}
