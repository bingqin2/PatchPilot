package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PlanDrivenPatchWorkflow implements PatchWorkflow {

    private final FixPlanGenerator fixPlanGenerator;
    private final PlannedPatchWorkflow plannedPatchWorkflow;

    public PlanDrivenPatchWorkflow(FixPlanGenerator fixPlanGenerator, PlannedPatchWorkflow plannedPatchWorkflow) {
        this.fixPlanGenerator = fixPlanGenerator;
        this.plannedPatchWorkflow = plannedPatchWorkflow;
    }

    @Override
    public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir) {
        FixPlan fixPlan = fixPlanGenerator.generatePlan(task);
        return plannedPatchWorkflow.apply(task, repositoryDir, fixPlan);
    }
}
