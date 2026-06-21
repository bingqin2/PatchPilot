package io.patchpilot.backend.safety.domain;

public record SafetyGateRequest(
        String repositoryOwner,
        String repositoryName,
        String triggerUser,
        String triggerComment
) {
}
