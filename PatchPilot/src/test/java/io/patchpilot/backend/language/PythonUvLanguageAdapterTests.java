package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.PythonUvLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PythonUvLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_uv_project_with_pytest_options() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [project]
                name = "demo"

                [tool.pytest.ini_options]
                testpaths = ["tests"]
                """);
        Files.writeString(tempDir.resolve("uv.lock"), "version = 1\n");
        PythonUvLanguageAdapter adapter = new PythonUvLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("uv");
        assertThat(result.verificationCommand()).containsExactly("uv", "run", "pytest");
        assertThat(result.reason()).isEqualTo("Detected uv project with pytest configuration");
    }

    @Test
    void should_detect_uv_project_with_pytest_dependency() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [project]
                name = "demo"
                dependencies = [
                    "pytest>=8.2.0"
                ]
                """);
        Files.writeString(tempDir.resolve("uv.lock"), "version = 1\n");
        PythonUvLanguageAdapter adapter = new PythonUvLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("uv");
        assertThat(result.verificationCommand()).containsExactly("uv", "run", "pytest");
        assertThat(result.reason()).isEqualTo("Detected uv project with pytest dependency");
    }

    @Test
    void should_reject_pyproject_without_uv_lockfile() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.pytest.ini_options]
                testpaths = ["tests"]
                """);
        PythonUvLanguageAdapter adapter = new PythonUvLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("uv");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no uv.lock found");
    }

    @Test
    void should_reject_uv_project_without_pytest_signal() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [project]
                name = "demo"
                """);
        Files.writeString(tempDir.resolve("uv.lock"), "version = 1\n");
        PythonUvLanguageAdapter adapter = new PythonUvLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("uv");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: uv project has no pytest configuration or dependency");
    }
}
