package io.patchpilot.backend.github.client.domain;

public record GitHubIssueContextComment(
        long id,
        String author,
        String body,
        String createdAt,
        String url
) {
}
