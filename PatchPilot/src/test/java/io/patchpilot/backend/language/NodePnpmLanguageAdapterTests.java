package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.NodePnpmLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NodePnpmLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_pnpm_project_with_lockfile_and_test_script() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "test": "vitest run"
                  }
                }
                """);
        Files.writeString(tempDir.resolve("pnpm-lock.yaml"), "lockfileVersion: '9.0'\n");
        NodePnpmLanguageAdapter adapter = new NodePnpmLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("pnpm");
        assertThat(result.verificationCommand()).containsExactly("pnpm", "test");
        assertThat(result.reason()).isEqualTo("Detected pnpm project with test script");
    }

    @Test
    void should_reject_package_json_without_pnpm_lockfile() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "test": "vitest run"
                  }
                }
                """);
        NodePnpmLanguageAdapter adapter = new NodePnpmLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("pnpm");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no pnpm-lock.yaml found");
    }

    @Test
    void should_reject_package_json_without_test_script() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "build": "vite build"
                  }
                }
                """);
        Files.writeString(tempDir.resolve("pnpm-lock.yaml"), "lockfileVersion: '9.0'\n");
        NodePnpmLanguageAdapter adapter = new NodePnpmLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("pnpm");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: package.json has no scripts.test command");
    }

    @Test
    void should_reject_invalid_package_json() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), "{");
        Files.writeString(tempDir.resolve("pnpm-lock.yaml"), "lockfileVersion: '9.0'\n");
        NodePnpmLanguageAdapter adapter = new NodePnpmLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("pnpm");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: package.json could not be parsed");
    }
}
