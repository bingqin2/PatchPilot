package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.NodeBunLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NodeBunLanguageAdapterTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_detect_bun_project_with_lockfile_and_test_script() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "test": "bun test"
                  }
                }
                """);
        Files.writeString(tempDir.resolve("bun.lockb"), "bun binary lock placeholder\n");
        NodeBunLanguageAdapter adapter = new NodeBunLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("bun");
        assertThat(result.verificationCommand()).containsExactly("bun", "test");
        assertThat(result.reason()).isEqualTo("Detected Bun project with test script");
    }

    @Test
    void should_detect_text_bun_lockfile_and_test_script() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "test": "bun test"
                  }
                }
                """);
        Files.writeString(tempDir.resolve("bun.lock"), "lockfileVersion = 0\n");
        NodeBunLanguageAdapter adapter = new NodeBunLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.buildSystem()).isEqualTo("bun");
        assertThat(result.verificationCommand()).containsExactly("bun", "test");
    }

    @Test
    void should_reject_package_json_without_bun_lockfile() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "test": "bun test"
                  }
                }
                """);
        NodeBunLanguageAdapter adapter = new NodeBunLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("bun");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: no bun.lockb or bun.lock found");
    }

    @Test
    void should_reject_package_json_without_test_script() throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "build": "bun build src/index.ts"
                  }
                }
                """);
        Files.writeString(tempDir.resolve("bun.lockb"), "bun binary lock placeholder\n");
        NodeBunLanguageAdapter adapter = new NodeBunLanguageAdapter();

        LanguageDetectionResult result = adapter.detect(tempDir);

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("bun");
        assertThat(result.verificationCommand()).isEqualTo(List.of());
        assertThat(result.reason()).isEqualTo("Unsupported repository: package.json has no scripts.test command");
    }
}
