package io.patchpilot.backend.safety.domain;

public record ReleaseTriggerQuarantineRequest(
        String operator,
        String reason
) {
}
