package io.patchpilot.backend.github.webhook;

public record GitHubTriggerDryRunRequestDto(
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
