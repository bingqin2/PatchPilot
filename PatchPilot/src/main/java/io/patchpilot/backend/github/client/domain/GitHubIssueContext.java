package io.patchpilot.backend.github.client.domain;

import java.util.List;

public record GitHubIssueContext(
        String title,
        String body,
        String url,
        List<GitHubIssueContextComment> comments
) {
}
