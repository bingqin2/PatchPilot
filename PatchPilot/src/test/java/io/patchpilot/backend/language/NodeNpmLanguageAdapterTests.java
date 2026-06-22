package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.NodeNpmLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NodeNpmLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_npm_project_with_test_script() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "test": "vitest run"
                  }
                }
                """);
        NodeNpmLanguageAdapter adapter = new NodeNpmLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("npm");
        assertThat(result.verificationCommand()).containsExactly("npm", "test");
        assertThat(result.reason()).isEqualTo("Detected npm project with test script");
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
        NodeNpmLanguageAdapter adapter = new NodeNpmLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("npm");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: package.json has no scripts.test command");
    }

    @Test
    void should_reject_missing_package_json() {
        NodeNpmLanguageAdapter adapter = new NodeNpmLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("npm");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no package.json found");
    }

    @Test
    void should_reject_invalid_package_json() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), "{");
        NodeNpmLanguageAdapter adapter = new NodeNpmLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("npm");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: package.json could not be parsed");
    }
}
