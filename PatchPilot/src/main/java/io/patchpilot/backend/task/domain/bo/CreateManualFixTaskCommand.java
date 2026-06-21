package io.patchpilot.backend.task.domain.bo;

public record CreateManualFixTaskCommand(
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
