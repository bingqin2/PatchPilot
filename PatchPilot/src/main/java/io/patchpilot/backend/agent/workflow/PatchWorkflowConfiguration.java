package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileWriteTool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatchWorkflowConfiguration {

    @Bean
    PlannedPatchWorkflow plannedPatchWorkflow(FileWriteTool fileWriteTool) {
        return new PlannedPatchWorkflow(fileWriteTool);
    }
}
