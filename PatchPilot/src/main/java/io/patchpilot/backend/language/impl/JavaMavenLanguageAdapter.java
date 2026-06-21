package io.patchpilot.backend.language.impl;

import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class JavaMavenLanguageAdapter implements LanguageAdapter {

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        if (Files.isRegularFile(repositoryRoot.resolve("mvnw"))) {
            return LanguageDetectionResult.supported(
                    "java",
                    "maven",
                    List.of("./mvnw", "test"),
                    "Detected Maven wrapper"
            );
        }
        if (Files.isRegularFile(repositoryRoot.resolve("pom.xml"))) {
            return LanguageDetectionResult.supported(
                    "java",
                    "maven",
                    List.of("mvn", "test"),
                    "Detected Maven project"
            );
        }
        return LanguageDetectionResult.unsupported(
                "java",
                "maven",
                "Unsupported repository: no mvnw or pom.xml found"
        );
    }
}
