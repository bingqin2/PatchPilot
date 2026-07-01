package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;

@FunctionalInterface
public interface ExternalExposureReadinessProvider {

    ExternalExposureReadinessVo getReadiness();
}
