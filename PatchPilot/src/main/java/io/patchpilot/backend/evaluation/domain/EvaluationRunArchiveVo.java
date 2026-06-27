package io.patchpilot.backend.evaluation.domain;

import java.time.Instant;
import java.util.List;

public record EvaluationRunArchiveVo(
        String id,
        String status,
        int totalCaseCount,
        int supportedFixCaseCount,
        int safetyRejectionCaseCount,
        int executedFixCaseCount,
        int passedFixCaseCount,
        int failedFixCaseCount,
        int skippedCaseCount,
        List<String> coveredLanguages,
        List<String> coveredBuildSystems,
        List<String> safetyRejectionCategories,
        Instant createdAt,
        String sideEffectContract,
        String nextAction,
        String report
) {

    public EvaluationRunArchiveVo {
        coveredLanguages = List.copyOf(coveredLanguages);
        coveredBuildSystems = List.copyOf(coveredBuildSystems);
        safetyRejectionCategories = List.copyOf(safetyRejectionCategories);
    }
}
