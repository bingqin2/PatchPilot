package io.patchpilot.backend.task.domain.dto;

public record CreateFixTaskDto(
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
