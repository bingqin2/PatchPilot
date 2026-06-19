package io.patchpilot.backend.github.client.domain;

public record CreateIssueCommentCommand(
        String owner,
        String repository,
        long issueNumber,
        String body
) {
}
