package io.patchpilot.backend.safety.domain;

public record TriggerRateLimitRequest(
        String source,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser
) {
}
