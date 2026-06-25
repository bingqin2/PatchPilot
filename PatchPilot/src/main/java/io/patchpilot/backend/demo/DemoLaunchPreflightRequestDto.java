package io.patchpilot.backend.demo;

public record DemoLaunchPreflightRequestDto(
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
