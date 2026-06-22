package io.patchpilot.backend;

import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.agent.workflow.PlanDrivenPatchWorkflow;
import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
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
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("default")
class PatchPilotApplicationTests {

    private final ApplicationContext applicationContext;

    PatchPilotApplicationTests(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void should_register_single_plan_driven_patch_workflow() {
        Map<String, PatchWorkflow> workflows = applicationContext.getBeansOfType(PatchWorkflow.class);

        assertThat(workflows).hasSize(1);
        assertThat(workflows.values().iterator().next()).isInstanceOf(PlanDrivenPatchWorkflow.class);
    }

    @Test
    void should_register_supported_language_adapters() {
        Map<String, LanguageAdapter> adapters = applicationContext.getBeansOfType(LanguageAdapter.class);

        assertThat(adapters.values())
                .hasAtLeastOneElementOfType(JavaMavenLanguageAdapter.class)
                .hasAtLeastOneElementOfType(JavaGradleLanguageAdapter.class)
                .hasAtLeastOneElementOfType(NodeBunLanguageAdapter.class)
                .hasAtLeastOneElementOfType(NodeNpmLanguageAdapter.class)
                .hasAtLeastOneElementOfType(NodePnpmLanguageAdapter.class)
                .hasAtLeastOneElementOfType(NodeYarnLanguageAdapter.class)
                .hasAtLeastOneElementOfType(PythonPoetryLanguageAdapter.class)
                .hasAtLeastOneElementOfType(PythonUvLanguageAdapter.class)
                .hasAtLeastOneElementOfType(PythonPytestLanguageAdapter.class);
    }

    @Test
    void should_prefer_specific_node_package_manager_adapters_before_npm(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "test": "vitest run"
                  }
                }
                """);
        Files.writeString(tempDir.resolve("pnpm-lock.yaml"), "lockfileVersion: '9.0'\n");
        LanguageAdapterRegistry registry = applicationContext.getBean(LanguageAdapterRegistry.class);

        LanguageDetectionResult result = registry.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.buildSystem()).isEqualTo("pnpm");
        assertThat(result.verificationCommand()).containsExactly("pnpm", "test");
    }

    @Test
    void should_prefer_bun_adapter_before_broad_npm(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("package.json"), """
                {
                  "scripts": {
                    "test": "bun test"
                  }
                }
                """);
        Files.writeString(tempDir.resolve("bun.lockb"), "bun binary lock placeholder\n");
        LanguageAdapterRegistry registry = applicationContext.getBean(LanguageAdapterRegistry.class);

        LanguageDetectionResult result = registry.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.buildSystem()).isEqualTo("bun");
        assertThat(result.verificationCommand()).containsExactly("bun", "test");
    }

    @Test
    void should_prefer_specific_python_project_runner_adapters_before_plain_pytest(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("pyproject.toml"), """
                [tool.poetry]
                name = "demo"

                [tool.pytest.ini_options]
                testpaths = ["tests"]
                """);
        LanguageAdapterRegistry registry = applicationContext.getBean(LanguageAdapterRegistry.class);

        LanguageDetectionResult result = registry.detect(tempDir);

        assertThat(result.supported()).isTrue();
        assertThat(result.buildSystem()).isEqualTo("poetry");
        assertThat(result.verificationCommand()).containsExactly("poetry", "run", "pytest");
    }
}
