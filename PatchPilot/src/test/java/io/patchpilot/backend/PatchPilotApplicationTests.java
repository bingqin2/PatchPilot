package io.patchpilot.backend;

import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.agent.workflow.PlanDrivenPatchWorkflow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
}
