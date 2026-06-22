package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
@Order(390)
public class PythonHatchLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (hasHatchTestScript(repositoryRoot)) {
            return LanguageDetectionResult.supported(
                    "python",
                    "hatch",
                    List.of("hatch", "test"),
                    "Detected Hatch test script"
            );
        }
        return LanguageDetectionResult.unsupported(
                "python",
                "hatch",
                "Unsupported repository: no Hatch test script found"
        );
    }

    private static boolean hasHatchTestScript(Path repositoryRoot) {
        Path pyproject = repositoryRoot.resolve("pyproject.toml");
        boolean hasHatchScripts = PythonPytestSignals.containsText(pyproject, "[tool.hatch.envs.default.scripts]")
                || PythonPytestSignals.containsText(pyproject, "[tool.hatch.envs.test.scripts]");
        return hasHatchScripts && PythonPytestSignals.containsText(pyproject, "test =");
    }
}
