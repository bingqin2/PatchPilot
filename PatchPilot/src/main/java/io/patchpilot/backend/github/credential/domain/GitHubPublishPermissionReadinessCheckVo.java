package io.patchpilot.backend.github.credential.domain;

public record GitHubPublishPermissionReadinessCheckVo(
        String name,
        String status,
        String summary,
        String nextAction
) {
}
