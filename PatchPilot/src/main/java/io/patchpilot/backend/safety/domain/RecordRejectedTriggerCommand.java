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
        String category,
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
                RejectedTriggerCategory.UNKNOWN, null, null);
    }

    public RecordRejectedTriggerCommand(
            String source,
            String deliveryId,
            String repositoryOwner,
            String repositoryName,
            Long issueNumber,
            String triggerUser,
            String triggerComment,
            String reason,
            String category
    ) {
        this(source, deliveryId, repositoryOwner, repositoryName, issueNumber, triggerUser, triggerComment, reason,
                category, null, null);
    }

    public RecordRejectedTriggerCommand(
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
        this(source, deliveryId, repositoryOwner, repositoryName, issueNumber, triggerUser, triggerComment, reason,
                RejectedTriggerCategory.UNKNOWN, commentId, commentUrl);
    }
}
