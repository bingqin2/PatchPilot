package io.patchpilot.backend.security.exposure.service;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;

import java.util.List;
import java.util.Optional;

public interface ExternalExposureSessionRepository {

    ExternalExposureSessionVo save(ExternalExposureSessionVo session);

    List<ExternalExposureSessionVo> listRecentSessions(int limit);

    Optional<ExternalExposureSessionVo> findById(String sessionId);
}
