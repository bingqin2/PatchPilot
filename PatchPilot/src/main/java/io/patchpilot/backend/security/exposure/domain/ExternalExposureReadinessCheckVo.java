package io.patchpilot.backend.security.exposure.domain;

public record ExternalExposureReadinessCheckVo(
        String name,
        String status,
        String summary,
        String nextAction
) {
}
