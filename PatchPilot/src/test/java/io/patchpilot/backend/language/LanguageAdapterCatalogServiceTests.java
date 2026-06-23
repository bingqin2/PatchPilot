package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageAdapterCatalogServiceTests {

    @Test
    void should_list_supported_language_adapters_with_commands_and_demo_fixtures() {
        LanguageAdapterCatalogService service = new LanguageAdapterCatalogService();

        List<SupportedLanguageAdapterVo> adapters = service.listSupportedAdapters();

        assertThat(adapters)
                .extracting(adapter -> adapter.language() + "/" + adapter.buildSystem())
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
        assertThat(adapters).allSatisfy(adapter -> assertThat(adapter.status()).isEqualTo("SUPPORTED"));
        assertThat(find(adapters, "java", "maven").verificationCommand()).containsExactly("mvn", "test");
        assertThat(find(adapters, "go", "go").verificationCommand()).containsExactly("go", "test", "./...");
        assertThat(find(adapters, "go", "go").demoFixturePath()).isEqualTo("docs/demo-repositories/go-module");
        assertThat(find(adapters, "go", "go").detectionSignals()).contains("go.mod", "*_test.go");
        assertThat(find(adapters, "node", "bun").demoFixturePath()).isEqualTo("docs/demo-repositories/node-bun");
        assertThat(find(adapters, "node", "pnpm").detectionSignals()).contains("package.json", "pnpm-lock.yaml", "scripts.test");
        assertThat(find(adapters, "python", "tox").verificationCommand()).containsExactly("tox");
        assertThat(find(adapters, "python", "nox").demoFixturePath()).isEqualTo("docs/demo-repositories/python-nox");
        assertThat(find(adapters, "python", "hatch").detectionSignals()).contains("pyproject.toml", "Hatch test script");
        assertThat(find(adapters, "python", "uv").demoFixturePath()).isEqualTo("docs/demo-repositories/python-uv");
    }

    private static SupportedLanguageAdapterVo find(
            List<SupportedLanguageAdapterVo> adapters,
            String language,
            String buildSystem
    ) {
        return adapters.stream()
                .filter(adapter -> adapter.language().equals(language) && adapter.buildSystem().equals(buildSystem))
                .findFirst()
                .orElseThrow();
    }
}
