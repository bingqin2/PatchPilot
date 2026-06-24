package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.domain.RepositoryPreflightRequest;
import io.patchpilot.backend.language.domain.RepositoryPreflightVo;
import io.patchpilot.backend.language.config.RepositoryPreflightProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositoryPreflightServiceTests {

    @TempDir
    private Path repositoryDir;

    @Test
    void should_return_supported_preflight_when_registry_detects_allowed_repository() {
        RepositoryPreflightService service = new RepositoryPreflightService(
                new LanguageAdapterRegistry(List.of(path -> LanguageDetectionResult.supported(
                        "java",
                        "maven",
                        List.of("mvn", "test"),
                        "Detected Maven project"
                ))),
                new LanguageAdapterCatalogService(),
                preflightProperties(repositoryDir)
        );

        RepositoryPreflightVo result = service.preflight(new RepositoryPreflightRequest(repositoryDir.toString()));

        assertThat(result.supported()).isTrue();
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.buildSystem()).isEqualTo("maven");
        assertThat(result.verificationCommand()).containsExactly("mvn", "test");
        assertThat(result.reason()).isEqualTo("Detected Maven project");
        assertThat(result.operatorAction()).contains("Repository is supported");
        assertThat(result.repositoryPath()).isEqualTo(repositoryDir.toString());
        assertThat(result.supportedAdapters()).isEmpty();
    }

    @Test
    void should_return_unsupported_preflight_with_adapter_guidance() {
        RepositoryPreflightService service = new RepositoryPreflightService(
                new LanguageAdapterRegistry(List.of()),
                new LanguageAdapterCatalogService(),
                preflightProperties(repositoryDir)
        );

        RepositoryPreflightVo result = service.preflight(new RepositoryPreflightRequest(repositoryDir.toString()));

        assertThat(result.supported()).isFalse();
        assertThat(result.language()).isEqualTo("unknown");
        assertThat(result.buildSystem()).isEqualTo("unknown");
        assertThat(result.verificationCommand()).isEmpty();
        assertThat(result.reason()).contains("Unsupported repository");
        assertThat(result.operatorAction()).contains("Add a LanguageAdapter");
        assertThat(result.supportedAdapters()).hasSize(13);
    }

    @Test
    void should_return_unsupported_preflight_for_missing_repository_path() {
        RepositoryPreflightService service = new RepositoryPreflightService(
                new LanguageAdapterRegistry(List.of(path -> LanguageDetectionResult.supported(
                        "java",
                        "maven",
                        List.of("mvn", "test"),
                        "Detected Maven project"
                ))),
                new LanguageAdapterCatalogService(),
                preflightProperties(repositoryDir)
        );
        Path missingPath = repositoryDir.resolve("missing");

        RepositoryPreflightVo result = service.preflight(new RepositoryPreflightRequest(missingPath.toString()));

        assertThat(result.supported()).isFalse();
        assertThat(result.reason()).contains("Repository path is not a directory");
        assertThat(result.repositoryPath()).isEqualTo(missingPath.toString());
        assertThat(result.supportedAdapters()).hasSize(13);
    }

    @Test
    void should_reject_blank_repository_path() {
        RepositoryPreflightService service = new RepositoryPreflightService(
                new LanguageAdapterRegistry(List.of()),
                new LanguageAdapterCatalogService(),
                preflightProperties(repositoryDir)
        );

        assertThatThrownBy(() -> service.preflight(new RepositoryPreflightRequest(" ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("repositoryPath is required");
    }

    @Test
    void should_reject_repository_path_outside_allowed_preflight_roots(@TempDir Path outsideDir) throws IOException {
        Files.createFile(outsideDir.resolve("pom.xml"));
        RepositoryPreflightService service = new RepositoryPreflightService(
                new LanguageAdapterRegistry(List.of(path -> LanguageDetectionResult.supported(
                        "java",
                        "maven",
                        List.of("mvn", "test"),
                        "Detected Maven project"
                ))),
                new LanguageAdapterCatalogService(),
                preflightProperties(repositoryDir)
        );

        assertThatThrownBy(() -> service.preflight(new RepositoryPreflightRequest(outsideDir.toString())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Repository preflight path is outside allowed roots");
    }

    private static RepositoryPreflightProperties preflightProperties(Path allowedRoot) {
        RepositoryPreflightProperties properties = new RepositoryPreflightProperties();
        properties.setAllowedRootDirs(List.of(allowedRoot));
        return properties;
    }
}
