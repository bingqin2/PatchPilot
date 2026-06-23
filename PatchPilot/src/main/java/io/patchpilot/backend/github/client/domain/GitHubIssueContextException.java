package io.patchpilot.backend.github.client.domain;

public class GitHubIssueContextException extends RuntimeException {

    public GitHubIssueContextException(String message) {
        super(message);
    }

    public GitHubIssueContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
