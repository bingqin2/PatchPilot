package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JavaMavenLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_maven_wrapper_project() throws Exception {
        Files.writeString(tempDir.resolve("mvnw"), "#!/bin/sh\n");
        Files.writeString(tempDir.resolve("pom.xml"), "<project />");
        JavaMavenLanguageAdapter adapter = new JavaMavenLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.buildSystem()).isEqualTo("maven");
        assertThat(result.verificationCommand()).containsExactly("./mvnw", "test");
        assertThat(result.reason()).isEqualTo("Detected Maven wrapper");
    }

    @Test
    void should_detect_maven_project_without_wrapper() throws Exception {
        Files.writeString(tempDir.resolve("pom.xml"), "<project />");
        JavaMavenLanguageAdapter adapter = new JavaMavenLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.buildSystem()).isEqualTo("maven");
        assertThat(result.verificationCommand()).containsExactly("mvn", "test");
        assertThat(result.reason()).isEqualTo("Detected Maven project");
    }

    @Test
    void should_reject_non_maven_project() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), "{}");
        JavaMavenLanguageAdapter adapter = new JavaMavenLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.buildSystem()).isEqualTo("maven");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no mvnw or pom.xml found");
    }
}
