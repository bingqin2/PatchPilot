package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.PythonHatchLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PythonHatchLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_default_hatch_test_script() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.hatch.envs.default.scripts]
                test = "pytest"
                """);
        PythonHatchLanguageAdapter adapter = new PythonHatchLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("hatch");
        assertThat(result.verificationCommand()).containsExactly("hatch", "test");
        assertThat(result.reason()).isEqualTo("Detected Hatch test script");
    }

    @Test
    void should_detect_test_environment_hatch_test_script() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.hatch.envs.test.scripts]
                test = "pytest"
                """);
        PythonHatchLanguageAdapter adapter = new PythonHatchLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("hatch");
        assertThat(result.verificationCommand()).containsExactly("hatch", "test");
        assertThat(result.reason()).isEqualTo("Detected Hatch test script");
    }

    @Test
    void should_reject_hatch_project_without_test_script() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.hatch.envs.default.scripts]
                lint = "ruff check ."
                """);
        PythonHatchLanguageAdapter adapter = new PythonHatchLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("hatch");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no Hatch test script found");
    }
}
