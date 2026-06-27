package io.patchpilot.backend.demo.domain;

import java.util.List;

public record DemoEvaluationRunReadinessEvidenceVo(
        DemoReadinessStatus status,
        String latestRunId,
        String previousRunId,
        int passedDelta,
        int failedDelta,
        int skippedDelta,
        List<String> coveredLanguages,
        List<String> coveredBuildSystems,
        List<String> safetyRejectionCategories,
        String sideEffectContract,
        String nextAction
) {
    public DemoEvaluationRunReadinessEvidenceVo {
        coveredLanguages = List.copyOf(coveredLanguages);
        coveredBuildSystems = List.copyOf(coveredBuildSystems);
        safetyRejectionCategories = List.copyOf(safetyRejectionCategories);
    }
}
