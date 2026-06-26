package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationRunPreviewVo(
        String status,
        String title,
        String previewRunId,
        int caseCount,
        int supportedFixCaseCount,
        int safetyRejectionCaseCount,
        List<String> coveredLanguages,
        List<String> coveredBuildSystems,
        List<String> expectedVerificationCommands,
        List<String> safetyRejectionCategories,
        List<String> gaps,
        String nextAction,
        boolean readOnly,
        String sideEffectContract,
        String markdownReport
) {
}
