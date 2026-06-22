package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

@Component
@Order(400)
public class PythonPytestLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (Files.isRegularFile(repositoryRoot.resolve("pytest.ini"))) {
            return supported("Detected pytest configuration");
        }
        if (containsText(repositoryRoot.resolve("pyproject.toml"), "[tool.pytest.ini_options]")) {
            return supported("Detected pytest configuration");
        }
        if (hasPytestDependency(repositoryRoot.resolve("requirements.txt"))) {
            return supported("Detected pytest dependency");
        }
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

    private static boolean containsText(Path path, String expectedText) {
        if (!Files.isRegularFile(path)) {
            return false;
        }
        try {
            return Files.readString(path).contains(expectedText);
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean hasPytestDependency(Path requirementsFile) {
        if (!Files.isRegularFile(requirementsFile)) {
            return false;
        }
        try {
            return Files.readAllLines(requirementsFile).stream()
                    .map(line -> line.strip().toLowerCase(Locale.ROOT))
                    .anyMatch(PythonPytestLanguageAdapter::isPytestRequirement);
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isPytestRequirement(String line) {
        return line.equals("pytest")
                || line.startsWith("pytest==")
                || line.startsWith("pytest>=")
                || line.startsWith("pytest<=")
                || line.startsWith("pytest~=")
                || line.startsWith("pytest[");
    }
}
