package io.patchpilot.backend.security.exposure.domain;

import java.time.Instant;

public record ExternalExposureSessionCreateRequestDto(
        String publicUrl,
        String webhookUrl,
        String purpose,
        String operator,
        Instant expectedShutdownAt,
        String notes
) {
}
