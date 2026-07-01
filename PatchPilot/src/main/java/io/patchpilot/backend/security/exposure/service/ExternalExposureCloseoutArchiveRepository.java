package io.patchpilot.backend.security.exposure.service;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;

import java.util.List;
import java.util.Optional;

public interface ExternalExposureCloseoutArchiveRepository {

    ExternalExposureCloseoutArchiveVo save(ExternalExposureCloseoutArchiveVo archive);

    List<ExternalExposureCloseoutArchiveVo> listRecentArchives(int limit);

    Optional<ExternalExposureCloseoutArchiveVo> findById(String archiveId);
}
