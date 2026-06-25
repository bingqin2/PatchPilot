package io.patchpilot.backend.task.domain.vo;

import java.time.Instant;

public record FixTaskPreExecutionDecisionVo(
        String id,
        String taskId,
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
