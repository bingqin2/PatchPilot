package io.patchpilot.backend.safety.domain;

public record TriggerQuarantineRequest(
        String source,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser
) {
}
