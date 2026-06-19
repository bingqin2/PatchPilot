package io.patchpilot.backend.github.client.domain;

public record UpdateIssueCommentCommand(
        String owner,
        String repository,
        long commentId,
        String body
) {
}
