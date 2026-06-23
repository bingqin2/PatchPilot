package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Order(300)
public class GoLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (Files.isRegularFile(repositoryRoot.resolve("go.mod"))) {
            return LanguageDetectionResult.supported(
                    "go",
                    "go",
                    List.of("go", "test", "./..."),
                    "Detected Go module"
            );
        }
        return LanguageDetectionResult.unsupported(
                "go",
                "go",
                "Unsupported repository: no go.mod found"
        );
    }
}
