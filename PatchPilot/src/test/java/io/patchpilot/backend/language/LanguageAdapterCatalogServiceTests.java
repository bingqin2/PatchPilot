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
                        "node/pnpm",
                        "node/yarn",
                        "node/npm",
                        "python/poetry",
                        "python/uv",
                        "python/pytest"
                );
        assertThat(adapters).allSatisfy(adapter -> assertThat(adapter.status()).isEqualTo("SUPPORTED"));
        assertThat(find(adapters, "java", "maven").verificationCommand()).containsExactly("mvn", "test");
        assertThat(find(adapters, "node", "pnpm").detectionSignals()).contains("package.json", "pnpm-lock.yaml", "scripts.test");
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
