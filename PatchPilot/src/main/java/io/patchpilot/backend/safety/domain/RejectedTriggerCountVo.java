package io.patchpilot.backend.safety.domain;

public record RejectedTriggerCountVo(
        String value,
        long count
) {
}
