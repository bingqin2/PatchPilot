package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageAdapterRegistryTests {

    @Test
    void should_return_first_supported_adapter_detection_result() {
        LanguageAdapterRegistry registry = new LanguageAdapterRegistry(List.of(
                repositoryDir -> LanguageDetectionResult.unsupported("java", "maven", "not maven"),
                repositoryDir -> LanguageDetectionResult.supported(
                        "node",
                        "npm",
                        List.of("npm", "test"),
                        "Detected npm project"
                )
        ));

        LanguageDetectionResult result = registry.detect(Path.of("/tmp/repo"));

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("node");
        assertThat(result.buildSystem()).isEqualTo("npm");
        assertThat(result.verificationCommand()).containsExactly("npm", "test");
        assertThat(result.reason()).isEqualTo("Detected npm project");
    }

    @Test
    void should_return_clear_unsupported_result_when_no_adapter_supports_repository() {
        LanguageAdapterRegistry registry = new LanguageAdapterRegistry(List.of(
                repositoryDir -> LanguageDetectionResult.unsupported("java", "maven", "not maven")
        ));

        LanguageDetectionResult result = registry.detect(Path.of("/tmp/repo"));

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("unknown");
        assertThat(result.buildSystem()).isEqualTo("unknown");
        assertThat(result.verificationCommand()).isEmpty();
        assertThat(result.reason()).isEqualTo("Unsupported repository: no supported language adapter detected");
    }
}
