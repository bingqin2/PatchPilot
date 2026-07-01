package io.patchpilot.backend.demo.domain;

public record DemoLiveTriggerOutcomeCloseoutCommand(
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment,
        String launchPackageArchiveId
) {
}
