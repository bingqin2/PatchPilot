package io.patchpilot.backend.demo.domain;

public record DemoLiveLaunchGateCommand(
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
