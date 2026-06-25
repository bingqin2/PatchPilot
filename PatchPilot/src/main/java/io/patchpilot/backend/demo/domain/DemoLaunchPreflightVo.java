package io.patchpilot.backend.demo.domain;

import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;

import java.util.List;

public record DemoLaunchPreflightVo(
        DemoReadinessStatus status,
        boolean readyToPost,
        String summary,
        DemoReadinessVo readiness,
        TriggerEvaluationResultVo triggerEvaluation,
        List<String> nextActions
) {

    public DemoLaunchPreflightVo {
        nextActions = List.copyOf(nextActions);
    }
}
