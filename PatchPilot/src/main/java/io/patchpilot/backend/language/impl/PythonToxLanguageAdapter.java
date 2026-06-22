package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Order(370)
public class PythonToxLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (Files.isRegularFile(repositoryRoot.resolve("tox.ini"))
                || PythonPytestSignals.containsText(repositoryRoot.resolve("pyproject.toml"), "[tool.tox]")) {
            return LanguageDetectionResult.supported(
                    "python",
                    "tox",
                    List.of("tox"),
                    "Detected tox configuration"
            );
        }
        return LanguageDetectionResult.unsupported(
                "python",
                "tox",
                "Unsupported repository: no tox configuration found"
        );
    }
}
