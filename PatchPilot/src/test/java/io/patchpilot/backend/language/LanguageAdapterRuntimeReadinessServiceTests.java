package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageAdapterRuntimeReadinessVo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageAdapterRuntimeReadinessServiceTests {

    @Test
    void should_report_adapter_executable_readiness_without_running_verification_commands() {
        Set<String> availableExecutables = Set.of("mvn", "python3");
        LanguageAdapterRuntimeReadinessService service = new LanguageAdapterRuntimeReadinessService(
                new LanguageAdapterCatalogService(),
                availableExecutables::contains
        );

        List<LanguageAdapterRuntimeReadinessVo> readiness = service.listRuntimeReadiness();

        assertThat(readiness)
                .extracting(result -> result.language() + "/" + result.buildSystem())
                .containsExactly(
                        "java/maven",
                        "java/gradle",
                        "go/go",
                        "node/bun",
                        "node/pnpm",
                        "node/yarn",
                        "node/npm",
                        "python/tox",
                        "python/nox",
                        "python/hatch",
                        "python/poetry",
                        "python/uv",
                        "python/pytest"
                );
        assertThat(find(readiness, "java", "maven").executable()).isEqualTo("mvn");
        assertThat(find(readiness, "java", "maven").verificationCommand()).containsExactly("mvn", "test");
        assertThat(find(readiness, "java", "maven").status()).isEqualTo("READY");
        assertThat(find(readiness, "go", "go").executable()).isEqualTo("go");
        assertThat(find(readiness, "go", "go").status()).isEqualTo("MISSING");
        assertThat(find(readiness, "python", "pytest").executable()).isEqualTo("python3");
        assertThat(find(readiness, "python", "pytest").status()).isEqualTo("READY");
    }

    private static LanguageAdapterRuntimeReadinessVo find(
            List<LanguageAdapterRuntimeReadinessVo> readiness,
            String language,
            String buildSystem
    ) {
        return readiness.stream()
                .filter(result -> result.language().equals(language) && result.buildSystem().equals(buildSystem))
                .findFirst()
                .orElseThrow();
    }
}
