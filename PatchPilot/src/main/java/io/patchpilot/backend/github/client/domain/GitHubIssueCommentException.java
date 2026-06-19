package io.patchpilot.backend.github.client.domain;

public class GitHubIssueCommentException extends RuntimeException {

    public GitHubIssueCommentException(String message) {
        super(message);
    }

    public GitHubIssueCommentException(String message, Throwable cause) {
        super(message, cause);
    }
}
