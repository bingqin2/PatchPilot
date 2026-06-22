package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class LanguageAdapterRegistry {

    private static final LanguageDetectionResult UNSUPPORTED = LanguageDetectionResult.unsupported(
            "unknown",
            "unknown",
            "Unsupported repository: no supported language adapter detected"
    );

    private final List<LanguageAdapter> languageAdapters;

    public LanguageAdapterRegistry(List<LanguageAdapter> languageAdapters) {
        this.languageAdapters = languageAdapters == null ? List.of() : List.copyOf(languageAdapters);
    }

    public LanguageDetectionResult detect(Path repositoryDir) {
        for (LanguageAdapter languageAdapter : languageAdapters) {
            LanguageDetectionResult detectionResult = languageAdapter.detect(repositoryDir);
            if (detectionResult.supported()) {
                return detectionResult;
            }
        }
        return UNSUPPORTED;
    }
}
