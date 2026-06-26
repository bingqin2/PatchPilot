package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationCaseVo(
        String id,
        String title,
        String category,
        String language,
        String buildSystem,
        String repositoryFixturePath,
        String issueText,
        List<String> expectedVerificationCommand,
        List<String> expectedChangedFiles,
        List<String> successCriteria,
        String expectedDecision,
        String expectedRejectionCategory,
        String safetyExpectation
) {
}
