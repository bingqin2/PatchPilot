package io.patchpilot.backend.github.credential.domain;

public class GitHubCredentialReadinessException extends RuntimeException {

    public GitHubCredentialReadinessException(String message) {
        super(message);
    }

    public GitHubCredentialReadinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
