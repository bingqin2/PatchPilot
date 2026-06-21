package io.patchpilot.backend.safety.domain;

public record TriggerIntentClassificationRequest(
        String classificationId,
        String source,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
