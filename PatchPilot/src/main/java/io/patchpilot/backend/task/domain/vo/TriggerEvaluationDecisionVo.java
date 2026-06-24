package io.patchpilot.backend.task.domain.vo;

public record TriggerEvaluationDecisionVo(
        boolean allowed,
        String reason,
        String category
) {
}
