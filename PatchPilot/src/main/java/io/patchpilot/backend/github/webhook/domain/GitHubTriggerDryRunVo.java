package io.patchpilot.backend.github.webhook.domain;

import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;

public record GitHubTriggerDryRunVo(
        String status,
        boolean wouldCreateTask,
        String repository,
        long issueNumber,
        String issueUrl,
        String triggerUser,
        String triggerComment,
        String summary,
        String nextAction,
        String sideEffectContract,
        TriggerEvaluationResultVo evaluation
) {
}
