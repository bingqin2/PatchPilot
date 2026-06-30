package io.patchpilot.backend.github.credential.domain;

public record GitHubLivePublishPreflightCheckVo(
        String name,
        String status,
        String summary,
        String nextAction
) {
}
