package io.patchpilot.backend.evaluation.domain;

import java.util.List;

public record EvaluationFixtureBaselineCaseVo(
        String caseId,
        String title,
        String category,
        String status,
        boolean executed,
        String fixturePath,
        String language,
        String buildSystem,
        List<String> verificationCommand,
        Integer exitCode,
        String outputSnippet,
        String reason,
        String nextAction
) {

    public EvaluationFixtureBaselineCaseVo {
        verificationCommand = List.copyOf(verificationCommand);
    }
}
