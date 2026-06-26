package io.patchpilot.backend.demo;

public record DemoArchivedLaunchOutcomeRequestDto(
        String triggerComment,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String taskId,
        String taskStatus,
        String pullRequestUrl,
        String archivedAt,
        String report
) {
}
