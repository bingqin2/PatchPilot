package io.patchpilot.backend.language.domain;

import java.util.List;

public record LanguageDetectionResult(
        boolean supported,
        String language,
        String buildSystem,
        List<String> verificationCommand,
        String reason
) {

    public static LanguageDetectionResult supported(
            String language,
            String buildSystem,
            List<String> verificationCommand,
            String reason
    ) {
        return new LanguageDetectionResult(true, language, buildSystem, List.copyOf(verificationCommand), reason);
    }

    public static LanguageDetectionResult unsupported(String language, String buildSystem, String reason) {
        return new LanguageDetectionResult(false, language, buildSystem, List.of(), reason);
    }
}
