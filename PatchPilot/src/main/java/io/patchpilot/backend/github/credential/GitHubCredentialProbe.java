package io.patchpilot.backend.github.credential;

@FunctionalInterface
public interface GitHubCredentialProbe {

    void check(String token);
}
