package io.patchpilot.backend.task.domain.bo;

public record EvaluateTriggerCommand(
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
