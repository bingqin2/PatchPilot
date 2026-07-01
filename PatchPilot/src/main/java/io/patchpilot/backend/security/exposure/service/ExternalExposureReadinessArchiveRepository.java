package io.patchpilot.backend.security.exposure.service;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;

import java.util.List;
import java.util.Optional;

public interface ExternalExposureReadinessArchiveRepository {

    ExternalExposureReadinessArchiveVo save(ExternalExposureReadinessArchiveVo archive);

    List<ExternalExposureReadinessArchiveVo> listRecentArchives(int limit);

    Optional<ExternalExposureReadinessArchiveVo> findById(String archiveId);
}
