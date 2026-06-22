package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Order(200)
public class JavaGradleLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (Files.isRegularFile(repositoryRoot.resolve("gradlew"))) {
            return LanguageDetectionResult.supported(
                    "java",
                    "gradle",
                    List.of("./gradlew", "test"),
                    "Detected Gradle wrapper"
            );
        }
        if (hasGradleBuildFile(repositoryRoot)) {
            return LanguageDetectionResult.supported(
                    "java",
                    "gradle",
                    List.of("gradle", "test"),
                    "Detected Gradle project"
            );
        }
        return LanguageDetectionResult.unsupported(
                "java",
                "gradle",
                "Unsupported repository: no gradlew, build.gradle, or build.gradle.kts found"
        );
    }

    private static boolean hasGradleBuildFile(Path repositoryRoot) {
        return Files.isRegularFile(repositoryRoot.resolve("build.gradle"))
                || Files.isRegularFile(repositoryRoot.resolve("build.gradle.kts"));
    }
}
