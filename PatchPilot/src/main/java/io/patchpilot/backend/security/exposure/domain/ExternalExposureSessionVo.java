package io.patchpilot.backend.security.exposure.domain;

import java.time.Instant;

public record ExternalExposureSessionVo(
        String id,
        String status,
        String publicUrl,
        String webhookUrl,
        String purpose,
        String operator,
        Instant expectedShutdownAt,
        String notes,
        String linkedHandoffStatus,
        String linkedReadinessArchiveId,
        Instant startedAt,
        String closedBy,
        Instant closedAt,
        String closeNotes,
        String markdownReport
) {
}
