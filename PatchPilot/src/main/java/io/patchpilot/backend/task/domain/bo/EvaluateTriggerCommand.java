package io.patchpilot.backend.task.domain.bo;

import io.patchpilot.backend.task.domain.enums.TriggerEvaluationSource;

public record EvaluateTriggerCommand(
        TriggerEvaluationSource source,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment
) {
}
