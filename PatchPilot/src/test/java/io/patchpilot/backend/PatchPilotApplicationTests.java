package io.patchpilot.backend;

import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.agent.workflow.PlanDrivenPatchWorkflow;
import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.impl.JavaGradleLanguageAdapter;
import io.patchpilot.backend.language.impl.JavaMavenLanguageAdapter;
import io.patchpilot.backend.language.impl.NodeNpmLanguageAdapter;
import io.patchpilot.backend.language.impl.PythonPytestLanguageAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

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
                .hasAtLeastOneElementOfType(NodeNpmLanguageAdapter.class)
                .hasAtLeastOneElementOfType(PythonPytestLanguageAdapter.class);
    }
}
