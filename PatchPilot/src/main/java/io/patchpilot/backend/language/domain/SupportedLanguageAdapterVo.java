package io.patchpilot.backend.language.domain;

import java.util.List;

public record SupportedLanguageAdapterVo(
        String language,
        String buildSystem,
        List<String> verificationCommand,
        List<String> detectionSignals,
        String demoFixturePath,
        String status
) {

    public SupportedLanguageAdapterVo {
        verificationCommand = List.copyOf(verificationCommand);
        detectionSignals = List.copyOf(detectionSignals);
    }
}
