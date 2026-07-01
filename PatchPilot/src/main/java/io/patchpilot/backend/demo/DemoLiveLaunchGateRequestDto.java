package io.patchpilot.backend.demo;

public record DemoLiveLaunchGateRequestDto(
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
