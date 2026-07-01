package io.patchpilot.backend.security.exposure.domain;

public record ExternalExposureOperatorHandoffChecklistCheckVo(
        String name,
        String status,
        String summary,
        String nextAction
) {
}
