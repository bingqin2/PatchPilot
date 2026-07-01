package io.patchpilot.backend.security.exposure.domain;

import java.time.Instant;

public record ExternalExposureSessionCloseRequestDto(
        String closedBy,
        Instant closedAt,
        String closeNotes
) {
}
