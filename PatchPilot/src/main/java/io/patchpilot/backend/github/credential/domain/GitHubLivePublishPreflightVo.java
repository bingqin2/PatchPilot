package io.patchpilot.backend.github.credential.domain;

import java.time.Instant;
import java.util.List;

public record GitHubLivePublishPreflightVo(
        String status,
        boolean livePublishReady,
        boolean tokenConfigured,
        boolean repositoryConfigured,
        String repository,
        String defaultBranch,
        List<String> patchpilotBranches,
        List<String> openPatchpilotPullRequests,
        String summary,
        String nextAction,
        String sideEffectContract,
        List<GitHubLivePublishPreflightCheckVo> checks,
        List<String> evidenceNotes,
        long latencyMs,
        Instant checkedAt
) {
}
