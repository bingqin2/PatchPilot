package io.patchpilot.backend.agent.workflow;

import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.nio.file.Path;

public interface PatchWorkflow {

    PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir);

    default PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir, GitHubIssueContext issueContext) {
        return apply(task, repositoryDir);
    }
}
