package io.patchpilot.backend.demo;

public record DemoLaunchCommandRequestDto(
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String operation,
        String targetPath,
        String replacementText
) {
}
