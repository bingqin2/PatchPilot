package io.patchpilot.backend.task.domain.bo;

import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;

import java.time.Instant;

public record RecordFixTaskPreExecutionDecisionCommand(
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
