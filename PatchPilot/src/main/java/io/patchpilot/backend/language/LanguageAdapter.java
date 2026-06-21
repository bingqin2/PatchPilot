package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;

import java.nio.file.Path;

public interface LanguageAdapter {

    LanguageDetectionResult detect(Path repositoryDir);
}
