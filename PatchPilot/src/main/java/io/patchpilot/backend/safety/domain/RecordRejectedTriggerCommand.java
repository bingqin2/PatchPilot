package io.patchpilot.backend.safety.domain;

public record RecordRejectedTriggerCommand(
        String source,
        String deliveryId,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment,
        String reason
) {
}
