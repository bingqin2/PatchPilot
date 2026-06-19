package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.nio.file.Path;

public interface PatchWorkflow {

    PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir);
}
