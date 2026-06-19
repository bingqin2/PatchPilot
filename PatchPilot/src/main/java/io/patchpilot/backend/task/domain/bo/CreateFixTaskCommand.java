package io.patchpilot.backend.task.domain.bo;

public record CreateFixTaskCommand(
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        long installationId,
        String triggerUser,
        String triggerComment,
        String deliveryId,
        long commentId
) {
}
