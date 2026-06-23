package io.patchpilot.backend.github.client.domain;

public record GetIssueContextCommand(
        String owner,
        String repository,
        long issueNumber,
        int commentLimit
) {
}
