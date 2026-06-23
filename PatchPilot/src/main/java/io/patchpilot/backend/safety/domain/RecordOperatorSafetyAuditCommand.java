package io.patchpilot.backend.safety.domain;

public record RecordOperatorSafetyAuditCommand(
        String action,
        String resourceType,
        String resourceId,
        TriggerQuarantineScope scope,
        String scopeKey,
        String operator,
        String reason
) {
}
