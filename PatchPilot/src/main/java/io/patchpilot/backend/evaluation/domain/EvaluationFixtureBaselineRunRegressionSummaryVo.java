package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationFixtureBaselineRunRegressionSummaryVo(
        String status,
        EvaluationFixtureBaselineRunDigestVo latestRun,
        EvaluationFixtureBaselineRunDigestVo previousRun,
        int passedDelta,
        int failedDelta,
        int skippedDelta,
        List<String> latestFailedCaseIds,
        List<String> newlyFailedCaseIds,
        List<String> recoveredCaseIds,
        String sideEffectContract,
        String nextAction,
        String markdownReport
) {

    public EvaluationFixtureBaselineRunRegressionSummaryVo {
        latestFailedCaseIds = List.copyOf(latestFailedCaseIds);
        newlyFailedCaseIds = List.copyOf(newlyFailedCaseIds);
        recoveredCaseIds = List.copyOf(recoveredCaseIds);
    }
}
