package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.credential.domain.GitHubRepositoryPermissionProbeResult;

@FunctionalInterface
public interface GitHubRepositoryPermissionProbe {

    GitHubRepositoryPermissionProbeResult check(String token, String owner, String repository);
}
