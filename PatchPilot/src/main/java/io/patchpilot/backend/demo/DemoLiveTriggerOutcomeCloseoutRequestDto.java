package io.patchpilot.backend.demo;

public record DemoLiveTriggerOutcomeCloseoutRequestDto(
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment,
        String launchPackageArchiveId
) {
}
