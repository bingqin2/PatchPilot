package io.patchpilot.backend.github.credential.domain;

import java.time.Instant;
import java.util.List;

public record GitHubPublishReadinessVo(
        String status,
        boolean publishReady,
        boolean tokenConfigured,
        boolean repositoryConfigured,
        String repository,
        String defaultBranch,
        String summary,
        String nextAction,
        String safePublishCommand,
        String sideEffectContract,
        List<GitHubPublishReadinessCheckVo> checks,
        List<String> evidenceNotes,
        Instant checkedAt
) {
}
