package io.patchpilot.backend.github.credential.domain;

public record GitHubRepositoryPermissionProbeResult(
        String defaultBranch,
        boolean pull,
        boolean push,
        boolean admin,
        boolean maintain
) {
}
