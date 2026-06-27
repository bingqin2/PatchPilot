package io.patchpilot.backend.evaluation.domain;

import java.time.Instant;

public record EvaluationRunArchiveDigestVo(
        String id,
        String status,
        int totalCaseCount,
        int supportedFixCaseCount,
        int safetyRejectionCaseCount,
        int executedFixCaseCount,
        int passedFixCaseCount,
        int failedFixCaseCount,
        int skippedCaseCount,
        Instant createdAt
) {
}
