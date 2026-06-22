package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.PythonNoxLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PythonNoxLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_noxfile_project() throws Exception {
        Files.writeString(tempDir.resolve("noxfile.py"), """
                import nox

                @nox.session
                def tests(session):
                    session.run("pytest")
                """);
        PythonNoxLanguageAdapter adapter = new PythonNoxLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("nox");
        assertThat(result.verificationCommand()).containsExactly("nox");
        assertThat(result.reason()).isEqualTo("Detected noxfile.py");
    }

    @Test
    void should_reject_repository_without_noxfile() throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.pytest.ini_options]
                testpaths = ["tests"]
                """);
        PythonNoxLanguageAdapter adapter = new PythonNoxLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("python");
        assertThat(result.buildSystem()).isEqualTo("nox");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no noxfile.py found");
    }
}
