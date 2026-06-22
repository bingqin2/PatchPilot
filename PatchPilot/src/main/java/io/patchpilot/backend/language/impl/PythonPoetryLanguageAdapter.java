package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
@Order(400)
public class PythonPoetryLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (!PythonPytestSignals.containsText(repositoryRoot.resolve("pyproject.toml"), "[tool.poetry]")) {
            return unsupported("Unsupported repository: no [tool.poetry] section found");
        }
        if (PythonPytestSignals.hasPytestConfiguration(repositoryRoot)) {
            return supported("Detected Poetry project with pytest configuration");
        }
        if (PythonPytestSignals.hasPytestDependencyInPyproject(repositoryRoot)) {
            return supported("Detected Poetry project with pytest dependency");
        }
        return unsupported("Unsupported repository: Poetry project has no pytest configuration or dependency");
    }

    private static LanguageDetectionResult supported(String reason) {
        return LanguageDetectionResult.supported(
                "python",
                "poetry",
                List.of("poetry", "run", "pytest"),
                reason
        );
    }

    private static LanguageDetectionResult unsupported(String reason) {
        return LanguageDetectionResult.unsupported("python", "poetry", reason);
    }
}
