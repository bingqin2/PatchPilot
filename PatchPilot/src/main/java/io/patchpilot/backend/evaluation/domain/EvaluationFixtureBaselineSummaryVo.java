package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationFixtureBaselineSummaryVo(
        String status,
        int totalCaseCount,
        int executedCaseCount,
        int passedCaseCount,
        int failedCaseCount,
        int skippedCaseCount,
        List<EvaluationFixtureBaselineCaseVo> cases,
        String sideEffectContract,
        String nextAction,
        String markdownReport
) {

    public EvaluationFixtureBaselineSummaryVo {
        cases = List.copyOf(cases);
    }
}
