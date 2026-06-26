package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationSummaryVo(
        String status,
        int totalCaseCount,
        int supportedFixCaseCount,
        int safetyRejectionCaseCount,
        List<String> coveredLanguages,
        List<String> coveredBuildSystems,
        List<String> rejectionCategories,
        String nextAction,
        boolean readOnly,
        String healthContract
) {
}
