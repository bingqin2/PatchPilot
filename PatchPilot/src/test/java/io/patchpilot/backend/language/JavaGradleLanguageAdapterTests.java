package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.JavaGradleLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JavaGradleLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_gradle_wrapper_project() throws Exception {
        Files.writeString(tempDir.resolve("gradlew"), "#!/bin/sh\n");
        Files.writeString(tempDir.resolve("build.gradle"), "plugins { id 'java' }\n");
        JavaGradleLanguageAdapter adapter = new JavaGradleLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.buildSystem()).isEqualTo("gradle");
        assertThat(result.verificationCommand()).containsExactly("./gradlew", "test");
        assertThat(result.reason()).isEqualTo("Detected Gradle wrapper");
    }

    @Test
    void should_detect_gradle_project_without_wrapper() throws Exception {
        Files.writeString(tempDir.resolve("build.gradle.kts"), "plugins { java }\n");
        JavaGradleLanguageAdapter adapter = new JavaGradleLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.buildSystem()).isEqualTo("gradle");
        assertThat(result.verificationCommand()).containsExactly("gradle", "test");
        assertThat(result.reason()).isEqualTo("Detected Gradle project");
    }

    @Test
    void should_reject_non_gradle_project() throws Exception {
        Files.writeString(tempDir.resolve("pom.xml"), "<project />");
        JavaGradleLanguageAdapter adapter = new JavaGradleLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.buildSystem()).isEqualTo("gradle");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no gradlew, build.gradle, or build.gradle.kts found");
    }
}
