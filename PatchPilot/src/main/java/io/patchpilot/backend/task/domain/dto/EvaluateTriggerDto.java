package io.patchpilot.backend.task.domain.dto;

public record EvaluateTriggerDto(
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
