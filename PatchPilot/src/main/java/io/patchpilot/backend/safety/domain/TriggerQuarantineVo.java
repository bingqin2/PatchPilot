package io.patchpilot.backend.safety.domain;

import java.time.Instant;

public record TriggerQuarantineVo(
        String id,
        TriggerQuarantineScope scope,
        String scopeKey,
        String reason,
        String category,
        int evidenceCount,
        long windowMs,
        Instant startedAt,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        Instant releasedAt,
        String releasedBy,
        String releaseReason,
        boolean active
) {
}
