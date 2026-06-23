package io.patchpilot.backend.safety.domain;

import java.time.Instant;

public record OperatorSafetyAuditVo(
        String id,
        String action,
        String resourceType,
        String resourceId,
        TriggerQuarantineScope scope,
        String scopeKey,
        String operator,
        String reason,
        Instant createdAt
) {
}
