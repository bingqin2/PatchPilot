package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.GoLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GoLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_go_module_project() throws Exception {
        Files.writeString(tempDir.resolve("go.mod"), """
                module example.com/patchpilot/demo

                go 1.22
                """);
        GoLanguageAdapter adapter = new GoLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("go");
        assertThat(result.buildSystem()).isEqualTo("go");
        assertThat(result.verificationCommand()).containsExactly("go", "test", "./...");
        assertThat(result.reason()).isEqualTo("Detected Go module");
    }

    @Test
    void should_reject_repository_without_go_module() throws Exception {
        Files.writeString(tempDir.resolve("main.go"), "package main\n");
        GoLanguageAdapter adapter = new GoLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("go");
        assertThat(result.buildSystem()).isEqualTo("go");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no go.mod found");
    }
}
