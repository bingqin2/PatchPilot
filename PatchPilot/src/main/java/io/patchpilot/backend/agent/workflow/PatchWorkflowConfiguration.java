package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.tool.FileReadTool;
import io.patchpilot.backend.agent.tool.FileWriteTool;
import io.patchpilot.backend.task.service.FixTaskPatchReviewService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatchWorkflowConfiguration {

    @Bean
    PlannedPatchWorkflow plannedPatchWorkflow(
            FileWriteTool fileWriteTool,
            FileReadTool fileReadTool,
            FileEditPlanGenerator fileEditPlanGenerator,
            PatchReviewGenerator patchReviewGenerator,
            FixTaskPatchReviewService patchReviewService
    ) {
        return new PlannedPatchWorkflow(fileWriteTool, fileReadTool, fileEditPlanGenerator, patchReviewGenerator, patchReviewService);
    }
}
