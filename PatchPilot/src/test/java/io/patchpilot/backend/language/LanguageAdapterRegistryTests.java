package io.patchpilot.backend.language;

import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.language.impl.JavaGradleLanguageAdapter;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeBunLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeNpmLanguageAdapter;
import io.patchpilot.backend.language.impl.NodePnpmLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeYarnLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPoetryLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPytestLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonUvLanguageAdapter;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

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

    @Test
    void should_detect_adapter_demo_fixtures() {
        LanguageAdapterRegistry registry = new LanguageAdapterRegistry(List.of(
                new JavaMavenLanguageAdapter(),
                new JavaGradleLanguageAdapter(),
                new NodeBunLanguageAdapter(),
                new NodePnpmLanguageAdapter(),
                new NodeYarnLanguageAdapter(),
                new NodeNpmLanguageAdapter(),
                new PythonPoetryLanguageAdapter(),
                new PythonUvLanguageAdapter(),
                new PythonPytestLanguageAdapter()
        ));

        Stream.of(
                fixture("java-maven", "java", "maven", List.of("mvn", "test")),
                fixture("java-gradle", "java", "gradle", List.of("gradle", "test")),
                fixture("node-bun", "node", "bun", List.of("bun", "test")),
                fixture("node-npm", "node", "npm", List.of("npm", "test")),
                fixture("node-pnpm", "node", "pnpm", List.of("pnpm", "test")),
                fixture("node-yarn", "node", "yarn", List.of("yarn", "test")),
                fixture("python-pytest", "python", "pytest", List.of("python3", "-m", "pytest")),
                fixture("python-poetry", "python", "poetry", List.of("poetry", "run", "pytest")),
                fixture("python-uv", "python", "uv", List.of("uv", "run", "pytest"))
        ).forEach(expectation -> {
            LanguageDetectionResult result = registry.detect(expectation.path());

            assertThat(result.supported()).as(expectation.name()).isTrue();
            assertThat(result.language()).as(expectation.name()).isEqualTo(expectation.language());
            assertThat(result.buildSystem()).as(expectation.name()).isEqualTo(expectation.buildSystem());
            assertThat(result.verificationCommand()).as(expectation.name()).isEqualTo(expectation.command());
        });
    }

    private static FixtureExpectation fixture(
            String name,
            String language,
            String buildSystem,
            List<String> command
    ) {
        return new FixtureExpectation(
                name,
                demoRepositoriesRoot().resolve(name),
                language,
                buildSystem,
                command
        );
    }

    private static Path demoRepositoriesRoot() {
        Path rootRelativePath = Path.of("docs", "demo-repositories");
        if (Files.isDirectory(rootRelativePath)) {
            return rootRelativePath;
        }
        return Path.of("..", "docs", "demo-repositories");
    }

    private record FixtureExpectation(
            String name,
            Path path,
            String language,
            String buildSystem,
            List<String> command
    ) {
    }
}
