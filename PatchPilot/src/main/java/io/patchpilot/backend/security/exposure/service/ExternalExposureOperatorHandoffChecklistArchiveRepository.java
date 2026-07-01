package io.patchpilot.backend.security.exposure.service;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistArchiveVo;

import java.util.List;
import java.util.Optional;

public interface ExternalExposureOperatorHandoffChecklistArchiveRepository {

    ExternalExposureOperatorHandoffChecklistArchiveVo save(ExternalExposureOperatorHandoffChecklistArchiveVo archive);

    List<ExternalExposureOperatorHandoffChecklistArchiveVo> listRecentArchives(int limit);

    Optional<ExternalExposureOperatorHandoffChecklistArchiveVo> findById(String archiveId);
}
