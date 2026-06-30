package io.patchpilot.backend.github.credential.domain;

public record GitHubPublishReadinessCheckVo(
        String name,
        String status,
        String summary,
        String nextAction
) {
}
