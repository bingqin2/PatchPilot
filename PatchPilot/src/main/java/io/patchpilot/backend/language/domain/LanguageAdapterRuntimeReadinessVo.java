package io.patchpilot.backend.language.domain;

import java.util.List;

public record LanguageAdapterRuntimeReadinessVo(
        String language,
        String buildSystem,
        String executable,
        List<String> verificationCommand,
        String status,
        String reason
) {

    public LanguageAdapterRuntimeReadinessVo {
        verificationCommand = List.copyOf(verificationCommand);
    }
}
