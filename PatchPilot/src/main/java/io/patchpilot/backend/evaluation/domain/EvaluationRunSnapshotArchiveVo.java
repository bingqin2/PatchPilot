package io.patchpilot.backend.evaluation.domain;

import java.time.Instant;
import java.util.List;

public record EvaluationRunSnapshotArchiveVo(
        String id,
        String previewRunId,
        String title,
        String status,
        int caseCount,
        int supportedFixCaseCount,
        int safetyRejectionCaseCount,
        List<String> coveredLanguages,
        List<String> coveredBuildSystems,
        List<String> expectedVerificationCommands,
        List<String> safetyRejectionCategories,
        Instant createdAt,
        String sideEffectContract,
        String report
) {
}
