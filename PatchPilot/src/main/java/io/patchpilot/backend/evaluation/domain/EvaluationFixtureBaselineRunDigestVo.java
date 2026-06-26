package io.patchpilot.backend.evaluation.domain;

import java.time.Instant;

public record EvaluationFixtureBaselineRunDigestVo(
        String id,
        String status,
        int totalCaseCount,
        int executedCaseCount,
        int passedCaseCount,
        int failedCaseCount,
        int skippedCaseCount,
        Instant createdAt
) {
}
