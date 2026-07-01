package io.patchpilot.backend.demo;

public record DemoLiveTriggerLaunchPackageRequestDto(
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
