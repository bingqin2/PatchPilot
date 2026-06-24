package io.patchpilot.backend.task.domain.dto;

public record EvaluateTriggerDto(
        String source,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
