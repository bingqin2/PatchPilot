package io.patchpilot.backend.github.credential;

@FunctionalInterface
public interface GitHubRepositoryAccessProbe {

    String check(String token, String owner, String repository);
}
