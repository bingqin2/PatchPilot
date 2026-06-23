package io.patchpilot.backend.safety.domain;

public record ManualTriggerQuarantineRequest(
        TriggerQuarantineScope scope,
        String scopeKey,
        String reason,
        Long durationMs,
        String operator
) {
}
