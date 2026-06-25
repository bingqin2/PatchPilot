package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskPreExecutionDecisionSummaryVo(
        String id,
        String taskId,
        String repositoryOwner,
        String repositoryName,
        long issueNumber,
        String triggerUser,
        String triggerComment,
        String taskStatus,
        String source,
        String finalDecision,
        TriggerEvaluationDecisionVo safetyDecision,
        TriggerEvaluationDecisionVo activeTaskDecision,
        TriggerEvaluationDecisionVo quarantineDecision,
        TriggerEvaluationDecisionVo rateLimitDecision,
        TriggerEvaluationDecisionVo triggerIntentDecision,
        boolean issueContextLoaded,
        Instant createdAt
) {
}
