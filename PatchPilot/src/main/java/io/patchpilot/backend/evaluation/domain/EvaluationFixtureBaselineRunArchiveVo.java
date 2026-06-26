package io.patchpilot.backend.evaluation.domain;

import java.time.Instant;

public record EvaluationFixtureBaselineRunArchiveVo(
        String id,
        String status,
        int totalCaseCount,
        int executedCaseCount,
        int passedCaseCount,
        int failedCaseCount,
        int skippedCaseCount,
        Instant createdAt,
        String sideEffectContract,
        String nextAction,
        String report
) {
}
