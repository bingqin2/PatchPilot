package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationCaseFixtureReadinessVo(
        String caseId,
        String title,
        String category,
        String status,
        boolean fixtureRequired,
        String fixturePath,
        boolean fixtureExists,
        String expectedLanguage,
        String actualLanguage,
        String expectedBuildSystem,
        String actualBuildSystem,
        List<String> expectedVerificationCommand,
        List<String> actualVerificationCommand,
        boolean adapterMatches,
        List<String> expectedChangedFiles,
        List<String> missingExpectedFiles,
        boolean expectedFilesExist,
        String reason,
        String nextAction
) {

    public EvaluationCaseFixtureReadinessVo {
        expectedVerificationCommand = List.copyOf(expectedVerificationCommand);
        actualVerificationCommand = List.copyOf(actualVerificationCommand);
        expectedChangedFiles = List.copyOf(expectedChangedFiles);
        missingExpectedFiles = List.copyOf(missingExpectedFiles);
    }
}
