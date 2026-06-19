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
import io.patchpilot.backend.task.service.FixTaskToolCallService;
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
    private final FixTaskToolCallService fixTaskToolCallService;
    private final TaskCancellationChecker taskCancellationChecker;

    public NoopFixTaskExecutor(
            WorkspaceService workspaceService,
            MavenTestRunner mavenTestRunner,
            PatchWorkflow patchWorkflow,
            DiffTool diffTool,
            CommitTool commitTool,
            PushTool pushTool,
            PullRequestTool pullRequestTool,
            FixTaskTestRunService fixTaskTestRunService,
            FixTaskToolCallService fixTaskToolCallService,
            TaskCancellationChecker taskCancellationChecker
    ) {
        this.workspaceService = workspaceService;
        this.mavenTestRunner = mavenTestRunner;
        this.patchWorkflow = patchWorkflow;
        this.diffTool = diffTool;
        this.commitTool = commitTool;
        this.pushTool = pushTool;
        this.pullRequestTool = pullRequestTool;
        this.fixTaskTestRunService = fixTaskTestRunService;
        this.fixTaskToolCallService = fixTaskToolCallService;
        this.taskCancellationChecker = taskCancellationChecker;
    }

    @Override
    public FixTaskExecutionResult execute(FixTaskVo task) {
        taskCancellationChecker.throwIfCancelled(task.id());
        PreparedWorkspaceResult preparedWorkspace = workspaceService.prepareRepository(new CloneWorkspaceCommand(
                task.id(),
                task.repositoryOwner(),
                task.repositoryName()
        ));
        taskCancellationChecker.throwIfCancelled(task.id());
        auditToolCall(
                task.id(),
                "PatchWorkflow",
                "repositoryDir=%s, triggerComment=%s".formatted(
                        preparedWorkspace.repositoryDir(),
                        task.triggerComment()
                ),
                () -> patchWorkflow.apply(task, preparedWorkspace.repositoryDir()).summary()
        );
        taskCancellationChecker.throwIfCancelled(task.id());
        auditToolCall(
                task.id(),
                "DiffTool",
                "repositoryDir=%s".formatted(preparedWorkspace.repositoryDir()),
                () -> diffTool.diff(preparedWorkspace.repositoryDir())
        );
        taskCancellationChecker.throwIfCancelled(task.id());
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
        taskCancellationChecker.throwIfCancelled(task.id());
        auditToolCall(
                task.id(),
                "CommitTool",
                "repositoryDir=%s, message=%s".formatted(
                        preparedWorkspace.repositoryDir(),
                        "PatchPilot task " + task.id()
                ),
                () -> commitTool.commitAll(preparedWorkspace.repositoryDir(), "PatchPilot task " + task.id())
        );
        taskCancellationChecker.throwIfCancelled(task.id());
        auditToolCall(
                task.id(),
                "PushTool",
                "repositoryDir=%s, branchName=%s".formatted(
                        preparedWorkspace.repositoryDir(),
                        preparedWorkspace.branchName()
                ),
                () -> pushTool.pushBranch(preparedWorkspace.repositoryDir(), preparedWorkspace.branchName())
        );
        taskCancellationChecker.throwIfCancelled(task.id());
        PullRequestResult pullRequestResult = auditToolCall(
                task.id(),
                "PullRequestTool",
                "repository=%s/%s, branchName=%s, issueNumber=%d".formatted(
                        task.repositoryOwner(),
                        task.repositoryName(),
                        preparedWorkspace.branchName(),
                        task.issueNumber()
                ),
                () -> pullRequestTool.createPullRequest(task, preparedWorkspace.branchName()),
                PullRequestResult::url
        );
        return new FixTaskExecutionResult(pullRequestResult.url());
    }

    private String auditToolCall(String taskId, String toolName, String inputSummary, ToolCall<String> toolCall) {
        return auditToolCall(taskId, toolName, inputSummary, toolCall, output -> output);
    }

    private <T> T auditToolCall(
            String taskId,
            String toolName,
            String inputSummary,
            ToolCall<T> toolCall,
            ToolCallOutputSummary<T> outputSummary
    ) {
        Instant startedAt = Instant.now();
        try {
            T output = toolCall.call();
            Instant finishedAt = Instant.now();
            fixTaskToolCallService.recordToolCall(
                    taskId,
                    toolName,
                    inputSummary,
                    outputSummary.summary(output),
                    true,
                    startedAt,
                    finishedAt
            );
            return output;
        } catch (RuntimeException exception) {
            Instant finishedAt = Instant.now();
            fixTaskToolCallService.recordToolCall(
                    taskId,
                    toolName,
                    inputSummary,
                    exception.getMessage(),
                    false,
                    startedAt,
                    finishedAt
            );
            throw exception;
        }
    }

    @FunctionalInterface
    private interface ToolCall<T> {

        T call();
    }

    @FunctionalInterface
    private interface ToolCallOutputSummary<T> {

        String summary(T output);
    }
}
