package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageAdapterFixtureVerificationVo;
import io.patchpilot.backend.language.domain.SupportedLanguageAdapterVo;
import io.patchpilot.backend.language.impl.GoLanguageAdapter;
import io.patchpilot.backend.language.impl.JavaGradleLanguageAdapter;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeBunLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeNpmLanguageAdapter;
import io.patchpilot.backend.language.impl.NodePnpmLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeYarnLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonHatchLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonNoxLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPoetryLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPytestLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonToxLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonUvLanguageAdapter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageAdapterFixtureVerificationServiceTests {

    @Test
    void should_verify_supported_adapter_demo_fixtures_with_real_registry() {
        LanguageAdapterFixtureVerificationService service = fixtureService();

        List<LanguageAdapterFixtureVerificationVo> results = service.listFixtureVerifications();

        assertThat(results)
                .extracting(LanguageAdapterFixtureVerificationVo::fixtureName)
                .containsExactly(
                        "java-maven",
                        "java-gradle",
                        "go-module",
                        "node-bun",
                        "node-pnpm",
                        "node-yarn",
                        "node-npm",
                        "python-tox",
                        "python-nox",
                        "python-hatch",
                        "python-poetry",
                        "python-uv",
                        "python-pytest"
                );
        assertThat(results).allSatisfy(result -> {
            assertThat(result.status()).isEqualTo("PASS");
            assertThat(result.expectedLanguage()).isEqualTo(result.actualLanguage());
            assertThat(result.expectedBuildSystem()).isEqualTo(result.actualBuildSystem());
            assertThat(result.expectedVerificationCommand()).isEqualTo(result.actualVerificationCommand());
            assertThat(result.reason()).isNotBlank();
        });
        LanguageAdapterFixtureVerificationVo hatch = find(results, "python-hatch");
        assertThat(hatch.fixturePath()).isEqualTo("docs/demo-repositories/python-hatch");
        assertThat(hatch.expectedVerificationCommand()).containsExactly("hatch", "test");
        LanguageAdapterFixtureVerificationVo go = find(results, "go-module");
        assertThat(go.fixturePath()).isEqualTo("docs/demo-repositories/go-module");
        assertThat(go.expectedVerificationCommand()).containsExactly("go", "test", "./...");
    }

    @Test
    void should_return_failed_verification_for_missing_fixture_without_throwing() {
        LanguageAdapterFixtureVerificationService service = new LanguageAdapterFixtureVerificationService(
                () -> List.of(
                        adapter(
                                "python",
                                "missing",
                                List.of("missing-test"),
                                List.of("missing.fixture"),
                                "docs/demo-repositories/does-not-exist"
                        )
                ),
                new LanguageAdapterRegistry(List.of(new PythonPytestLanguageAdapter()))
        );

        List<LanguageAdapterFixtureVerificationVo> results = service.listFixtureVerifications();

        assertThat(results).hasSize(1);
        LanguageAdapterFixtureVerificationVo result = results.get(0);
        assertThat(result.fixtureName()).isEqualTo("does-not-exist");
        assertThat(result.status()).isEqualTo("FAIL");
        assertThat(result.expectedLanguage()).isEqualTo("python");
        assertThat(result.expectedBuildSystem()).isEqualTo("missing");
        assertThat(result.actualLanguage()).isEqualTo("unknown");
        assertThat(result.actualBuildSystem()).isEqualTo("unknown");
        assertThat(result.actualVerificationCommand()).isEmpty();
        assertThat(result.reason()).contains("missing fixture path");
    }

    static LanguageAdapterFixtureVerificationService fixtureService() {
        return new LanguageAdapterFixtureVerificationService(
                new LanguageAdapterCatalogService(),
                new LanguageAdapterRegistry(List.of(
                        new JavaMavenLanguageAdapter(),
                        new JavaGradleLanguageAdapter(),
                        new GoLanguageAdapter(),
                        new NodeBunLanguageAdapter(),
                        new NodePnpmLanguageAdapter(),
                        new NodeYarnLanguageAdapter(),
                        new NodeNpmLanguageAdapter(),
                        new PythonToxLanguageAdapter(),
                        new PythonNoxLanguageAdapter(),
                        new PythonHatchLanguageAdapter(),
                        new PythonPoetryLanguageAdapter(),
                        new PythonUvLanguageAdapter(),
                        new PythonPytestLanguageAdapter()
                ))
        );
    }

    private static SupportedLanguageAdapterVo adapter(
            String language,
            String buildSystem,
            List<String> verificationCommand,
            List<String> detectionSignals,
            String demoFixturePath
    ) {
        return new SupportedLanguageAdapterVo(
                language,
                buildSystem,
                verificationCommand,
                detectionSignals,
                demoFixturePath,
                "SUPPORTED"
        );
    }

    private static LanguageAdapterFixtureVerificationVo find(
            List<LanguageAdapterFixtureVerificationVo> results,
            String fixtureName
    ) {
        return results.stream()
                .filter(result -> result.fixtureName().equals(fixtureName))
                .findFirst()
                .orElseThrow();
    }

}
