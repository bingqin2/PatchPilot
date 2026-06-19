package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.agent.tool.CommitTool;
import io.patchpilot.backend.agent.tool.DiffTool;
import io.patchpilot.backend.agent.tool.PullRequestTool;
import io.patchpilot.backend.agent.tool.PushTool;
import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.MavenTestRunner;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.domain.FixTaskExecutionResult;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NoopFixTaskExecutor implements FixTaskExecutor {

    private final WorkspaceService workspaceService;
    private final MavenTestRunner mavenTestRunner;
    private final PatchWorkflow patchWorkflow;
    private final DiffTool diffTool;
    private final CommitTool commitTool;
    private final PushTool pushTool;
    private final PullRequestTool pullRequestTool;
    private final FixTaskTestRunService fixTaskTestRunService;

    public NoopFixTaskExecutor(
            WorkspaceService workspaceService,
            MavenTestRunner mavenTestRunner,
            PatchWorkflow patchWorkflow,
            DiffTool diffTool,
            CommitTool commitTool,
            PushTool pushTool,
            PullRequestTool pullRequestTool,
            FixTaskTestRunService fixTaskTestRunService
    ) {
        this.workspaceService = workspaceService;
        this.mavenTestRunner = mavenTestRunner;
        this.patchWorkflow = patchWorkflow;
        this.diffTool = diffTool;
        this.commitTool = commitTool;
        this.pushTool = pushTool;
        this.pullRequestTool = pullRequestTool;
        this.fixTaskTestRunService = fixTaskTestRunService;
    }

    @Override
    public FixTaskExecutionResult execute(FixTaskVo task) {
        PreparedWorkspaceResult preparedWorkspace = workspaceService.prepareRepository(new CloneWorkspaceCommand(
                task.id(),
                task.repositoryOwner(),
                task.repositoryName()
        ));
        patchWorkflow.apply(task, preparedWorkspace.repositoryDir());
        diffTool.diff(preparedWorkspace.repositoryDir());
        Instant testStartedAt = Instant.now();
        TestRunResult testRunResult = mavenTestRunner.runTests(preparedWorkspace.repositoryDir());
        Instant testFinishedAt = Instant.now();
        fixTaskTestRunService.recordTestRun(
                task.id(),
                testRunResult.command(),
                testRunResult.exitCode(),
                testRunResult.output(),
                testStartedAt,
                testFinishedAt
        );
        if (testRunResult.exitCode() != 0) {
            throw new IllegalStateException("maven tests failed: " + testRunResult.output());
        }
        commitTool.commitAll(preparedWorkspace.repositoryDir(), "PatchPilot task " + task.id());
        pushTool.pushBranch(preparedWorkspace.repositoryDir(), preparedWorkspace.branchName());
        PullRequestResult pullRequestResult = pullRequestTool.createPullRequest(task, preparedWorkspace.branchName());
        return new FixTaskExecutionResult(pullRequestResult.url());
    }
}
