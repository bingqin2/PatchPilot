package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.PythonPoetryLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PythonPoetryLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_poetry_project_with_pytest_options() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.poetry]
                name = "demo"

                [tool.pytest.ini_options]
                testpaths = ["tests"]
                """);
        PythonPoetryLanguageAdapter adapter = new PythonPoetryLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("poetry");
        assertThat(result.verificationCommand()).containsExactly("poetry", "run", "pytest");
        assertThat(result.reason()).isEqualTo("Detected Poetry project with pytest configuration");
    }

    @Test
    void should_detect_poetry_project_with_pytest_dependency() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.poetry]
                name = "demo"

                [tool.poetry.group.dev.dependencies]
                pytest = "^8.2.0"
                """);
        PythonPoetryLanguageAdapter adapter = new PythonPoetryLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("poetry");
        assertThat(result.verificationCommand()).containsExactly("poetry", "run", "pytest");
        assertThat(result.reason()).isEqualTo("Detected Poetry project with pytest dependency");
    }

    @Test
    void should_reject_pyproject_without_poetry_section() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [project]
                name = "demo"

                [tool.pytest.ini_options]
                testpaths = ["tests"]
                """);
        PythonPoetryLanguageAdapter adapter = new PythonPoetryLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("poetry");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no [tool.poetry] section found");
    }

    @Test
    void should_reject_poetry_project_without_pytest_signal() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.poetry]
                name = "demo"
                """);
        PythonPoetryLanguageAdapter adapter = new PythonPoetryLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("poetry");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: Poetry project has no pytest configuration or dependency");
    }
}
