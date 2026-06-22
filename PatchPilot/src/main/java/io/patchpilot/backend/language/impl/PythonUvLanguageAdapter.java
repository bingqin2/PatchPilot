package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Order(410)
public class PythonUvLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (!Files.isRegularFile(repositoryRoot.resolve("uv.lock"))) {
            return unsupported("Unsupported repository: no uv.lock found");
        }
        if (PythonPytestSignals.hasPytestConfiguration(repositoryRoot)) {
            return supported("Detected uv project with pytest configuration");
        }
        if (PythonPytestSignals.hasPytestDependencyInPyproject(repositoryRoot)) {
            return supported("Detected uv project with pytest dependency");
        }
        return unsupported("Unsupported repository: uv project has no pytest configuration or dependency");
    }

    private static LanguageDetectionResult supported(String reason) {
        return LanguageDetectionResult.supported(
                "python",
                "uv",
                List.of("uv", "run", "pytest"),
                reason
        );
    }

    private static LanguageDetectionResult unsupported(String reason) {
        return LanguageDetectionResult.unsupported("python", "uv", reason);
    }
}
