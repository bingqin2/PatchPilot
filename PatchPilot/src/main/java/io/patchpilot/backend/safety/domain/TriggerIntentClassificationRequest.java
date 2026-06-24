package io.patchpilot.backend.safety.domain;

import java.util.List;

public record TriggerIntentClassificationRequest(
        String classificationId,
        String source,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment,
        String issueTitle,
        String issueBody,
        List<TriggerIntentIssueComment> recentIssueComments
) {

    public TriggerIntentClassificationRequest(
            String classificationId,
            String source,
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String triggerComment
    ) {
        this(
                classificationId,
                source,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                "",
                "",
                List.of()
        );
    }

    public TriggerIntentClassificationRequest {
        recentIssueComments = recentIssueComments == null ? List.of() : List.copyOf(recentIssueComments);
    }
}
