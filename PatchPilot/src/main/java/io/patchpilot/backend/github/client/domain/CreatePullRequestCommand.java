package io.patchpilot.backend.github.client.domain;

public record CreatePullRequestCommand(
        String owner,
        String repository,
        String head,
        String base,
        String title,
        String body
) {
}
