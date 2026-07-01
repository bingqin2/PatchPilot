package io.patchpilot.backend.demo.domain;

public record DemoLiveTriggerLaunchPackageCommand(
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
