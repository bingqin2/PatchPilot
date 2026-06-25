package io.patchpilot.backend.demo;

public record DemoPreparedLaunchCommandRequestDto(
        String triggerComment,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String operation,
        String targetPath,
        String replacementText,
        String savedAt
) {
}
