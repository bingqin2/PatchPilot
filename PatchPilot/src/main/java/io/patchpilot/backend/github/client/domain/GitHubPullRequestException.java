package io.patchpilot.backend.github.client.domain;

public class GitHubPullRequestException extends RuntimeException {

    public GitHubPullRequestException(String message) {
        super(message);
    }

    public GitHubPullRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
