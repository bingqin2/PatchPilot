package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Order(420)
public class PythonPytestLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (PythonPytestSignals.hasPytestConfiguration(repositoryRoot)) {
            return supported("Detected pytest configuration");
        }
        if (PythonPytestSignals.hasPytestDependencyInRequirements(repositoryRoot)) {
            return supported("Detected pytest dependency");
        }
        if (!Files.isRegularFile(repositoryRoot.resolve("requirements.txt"))
                && !Files.isRegularFile(repositoryRoot.resolve("pyproject.toml"))
                && !Files.isRegularFile(repositoryRoot.resolve("pytest.ini"))) {
            return unsupported();
        }
        if (PythonPytestSignals.hasPytestDependencyInPyproject(repositoryRoot)) {
            return supported("Detected pytest dependency");
        }
        return unsupported();
    }

    private static LanguageDetectionResult unsupported() {
        return LanguageDetectionResult.unsupported(
                "python",
                "pytest",
                "Unsupported repository: no pytest configuration or dependency found"
        );
    }

    private static LanguageDetectionResult supported(String reason) {
        return LanguageDetectionResult.supported(
                "python",
                "pytest",
                List.of("python3", "-m", "pytest"),
                reason
        );
    }
}
