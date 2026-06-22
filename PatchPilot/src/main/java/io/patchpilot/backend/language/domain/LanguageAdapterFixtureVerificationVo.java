package io.patchpilot.backend.language.domain;

import java.util.List;

public record LanguageAdapterFixtureVerificationVo(
        String fixtureName,
        String fixturePath,
        String expectedLanguage,
        String expectedBuildSystem,
        List<String> expectedVerificationCommand,
        String actualLanguage,
        String actualBuildSystem,
        List<String> actualVerificationCommand,
        String reason,
        String status
) {

    public LanguageAdapterFixtureVerificationVo {
        expectedVerificationCommand = List.copyOf(expectedVerificationCommand);
        actualVerificationCommand = List.copyOf(actualVerificationCommand);
    }
}
