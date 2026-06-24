package io.patchpilot.backend.task.domain.vo;

public record TriggerEvaluationResultVo(
        String status,
        boolean wouldCreateTask,
        String blockedReason,
        String blockedCategory,
        TriggerEvaluationDecisionVo safetyDecision,
        TriggerEvaluationDecisionVo activeTaskDecision,
        TriggerEvaluationDecisionVo quarantineDecision,
        TriggerEvaluationDecisionVo rateLimitDecision,
        TriggerEvaluationDecisionVo triggerIntentDecision,
        boolean issueContextLoaded,
        String nextAction
) {
}
