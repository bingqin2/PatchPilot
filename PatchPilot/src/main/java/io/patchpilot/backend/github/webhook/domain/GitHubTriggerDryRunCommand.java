package io.patchpilot.backend.github.webhook.domain;

public record GitHubTriggerDryRunCommand(
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
