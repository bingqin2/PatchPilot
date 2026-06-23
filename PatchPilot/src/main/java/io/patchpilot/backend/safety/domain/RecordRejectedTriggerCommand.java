package io.patchpilot.backend.safety.domain;

public record RecordRejectedTriggerCommand(
        String source,
        String deliveryId,
        String repositoryOwner,
        String repositoryName,
        Long issueNumber,
        String triggerUser,
        String triggerComment,
        String reason,
        Long commentId,
        String commentUrl
) {

    public RecordRejectedTriggerCommand(
            String source,
            String deliveryId,
            String repositoryOwner,
            String repositoryName,
            Long issueNumber,
            String triggerUser,
            String triggerComment,
            String reason
    ) {
        this(source, deliveryId, repositoryOwner, repositoryName, issueNumber, triggerUser, triggerComment, reason,
                null, null);
    }
}
