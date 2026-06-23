package io.patchpilot.backend.safety.domain;

import java.time.Instant;

public record RecordTriggerQuarantineCommand(
        TriggerQuarantineScope scope,
        String scopeKey,
        String reason,
        String category,
        int evidenceCount,
        long windowMs,
        Instant expiresAt
) {
}
