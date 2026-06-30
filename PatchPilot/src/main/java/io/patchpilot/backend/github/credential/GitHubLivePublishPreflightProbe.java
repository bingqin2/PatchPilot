package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightProbeResult;

@FunctionalInterface
public interface GitHubLivePublishPreflightProbe {

    GitHubLivePublishPreflightProbeResult check(String token, String owner, String repository);
}
