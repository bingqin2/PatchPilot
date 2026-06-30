package io.patchpilot.backend.github.credential.domain;

import java.util.List;

public record GitHubLivePublishPreflightProbeResult(
        String defaultBranch,
        List<String> branchNames,
        List<String> openPatchpilotPullRequestUrls
) {
}
