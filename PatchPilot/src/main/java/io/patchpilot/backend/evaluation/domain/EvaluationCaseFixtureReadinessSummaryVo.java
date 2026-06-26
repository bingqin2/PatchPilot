package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationCaseFixtureReadinessSummaryVo(
        String status,
        int totalCaseCount,
        int passingCaseCount,
        int noFixtureRequiredCaseCount,
        int failingCaseCount,
        List<EvaluationCaseFixtureReadinessVo> cases,
        String sideEffectContract,
        String nextAction,
        String markdownReport
) {

    public EvaluationCaseFixtureReadinessSummaryVo {
        cases = List.copyOf(cases);
    }
}
