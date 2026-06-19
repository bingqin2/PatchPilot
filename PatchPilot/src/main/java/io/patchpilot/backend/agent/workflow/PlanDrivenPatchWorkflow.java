package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.workflow.domain.FixPlan;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class PlanDrivenPatchWorkflow implements PatchWorkflow {

    private final FixPlanGenerator fixPlanGenerator;
    private final PlannedPatchWorkflow plannedPatchWorkflow;

    @Override
    public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir) {
        FixPlan fixPlan = fixPlanGenerator.generatePlan(task);
        return plannedPatchWorkflow.apply(task, repositoryDir, fixPlan);
    }
}
