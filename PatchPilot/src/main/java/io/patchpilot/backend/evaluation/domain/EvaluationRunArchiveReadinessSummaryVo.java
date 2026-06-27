package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationRunArchiveReadinessSummaryVo(
        String status,
        EvaluationRunArchiveDigestVo latestRun,
        EvaluationRunArchiveDigestVo previousRun,
        int passedDelta,
        int failedDelta,
        int skippedDelta,
        List<String> coveredLanguages,
        List<String> coveredBuildSystems,
        List<String> safetyRejectionCategories,
        String sideEffectContract,
        String nextAction,
        String markdownReport
) {

    public EvaluationRunArchiveReadinessSummaryVo {
        coveredLanguages = List.copyOf(coveredLanguages);
        coveredBuildSystems = List.copyOf(coveredBuildSystems);
        safetyRejectionCategories = List.copyOf(safetyRejectionCategories);
    }
}
