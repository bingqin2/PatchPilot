package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Order(380)
public class PythonNoxLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (Files.isRegularFile(repositoryRoot.resolve("noxfile.py"))) {
            return LanguageDetectionResult.supported(
                    "python",
                    "nox",
                    List.of("nox"),
                    "Detected noxfile.py"
            );
        }
        return LanguageDetectionResult.unsupported(
                "python",
                "nox",
                "Unsupported repository: no noxfile.py found"
        );
    }
}
