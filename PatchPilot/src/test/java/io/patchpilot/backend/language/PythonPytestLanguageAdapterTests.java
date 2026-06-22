package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.PythonPytestLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PythonPytestLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_pytest_project_from_pytest_ini() throws Exception {
        Files.writeString(tempDir.resolve("pytest.ini"), "[pytest]\n");
        PythonPytestLanguageAdapter adapter = new PythonPytestLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("pytest");
        assertThat(result.verificationCommand()).containsExactly("python3", "-m", "pytest");
        assertThat(result.reason()).isEqualTo("Detected pytest configuration");
    }

    @Test
    void should_detect_pytest_project_from_pyproject_pytest_options() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.pytest.ini_options]
                testpaths = ["tests"]
                """);
        PythonPytestLanguageAdapter adapter = new PythonPytestLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("pytest");
        assertThat(result.verificationCommand()).containsExactly("python3", "-m", "pytest");
        assertThat(result.reason()).isEqualTo("Detected pytest configuration");
    }

    @Test
    void should_detect_pytest_project_from_requirements() throws Exception {
        Files.writeString(tempDir.resolve("requirements.txt"), "pytest==8.2.0\n");
        PythonPytestLanguageAdapter adapter = new PythonPytestLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("pytest");
        assertThat(result.verificationCommand()).containsExactly("python3", "-m", "pytest");
        assertThat(result.reason()).isEqualTo("Detected pytest dependency");
    }

    @Test
    void should_reject_python_project_without_pytest_signal() throws Exception {
        Files.writeString(tempDir.resolve("requirements.txt"), "flask==3.0.0\n");
        Files.writeString(tempDir.resolve("app.py"), "print('hello')\n");
        PythonPytestLanguageAdapter adapter = new PythonPytestLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("pytest");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no pytest configuration or dependency found");
    }
}
