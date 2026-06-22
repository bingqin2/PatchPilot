package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.PythonToxLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PythonToxLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_tox_ini_project() throws Exception {
        Files.writeString(tempDir.resolve("tox.ini"), """
                [tox]
                env_list = py311

                [testenv]
                commands = pytest
                """);
        PythonToxLanguageAdapter adapter = new PythonToxLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("tox");
        assertThat(result.verificationCommand()).containsExactly("tox");
        assertThat(result.reason()).isEqualTo("Detected tox configuration");
    }

    @Test
    void should_detect_pyproject_tox_project() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.tox]
                env_list = ["py311"]
                """);
        PythonToxLanguageAdapter adapter = new PythonToxLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("tox");
        assertThat(result.verificationCommand()).containsExactly("tox");
        assertThat(result.reason()).isEqualTo("Detected tox configuration");
    }

    @Test
    void should_reject_repository_without_tox_configuration() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.pytest.ini_options]
                testpaths = ["tests"]
                """);
        PythonToxLanguageAdapter adapter = new PythonToxLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("tox");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no tox configuration found");
    }
}
