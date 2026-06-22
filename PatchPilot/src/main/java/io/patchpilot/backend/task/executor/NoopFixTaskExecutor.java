package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.agent.tool.CommitTool;
import io.patchpilot.backend.agent.tool.DiffTool;
import io.patchpilot.backend.agent.tool.PullRequestTool;
import io.patchpilot.backend.agent.tool.PushTool;
import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.VerificationRunner;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.domain.FixTaskExecutionResult;
import io.patchpilot.backend.task.service.FixTaskAdapterMetadataRecorder;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NoopFixTaskExecutor implements FixTaskExecutor {

    private final WorkspaceService workspaceService;
    private final LanguageAdapterRegistry languageAdapterRegistry;
    private final VerificationRunner verificationRunner;
    private final PatchWorkflow patchWorkflow;
    private final DiffTool diffTool;
    private final CommitTool commitTool;
    private final PushTool pushTool;
    private final PullRequestTool pullRequestTool;
    private final FixTaskAdapterMetadataRecorder adapterMetadataRecorder;
    private final FixTaskTestRunService fixTaskTestRunService;
    private final FixTaskToolCallService fixTaskToolCallService;
    private final TaskCancellationChecker taskCancellationChecker;

    public NoopFixTaskExecutor(
            WorkspaceService workspaceService,
            VerificationRunner verificationRunner,
            PatchWorkflow patchWorkflow,
            DiffTool diffTool,
            CommitTool commitTool,
            PushTool pushTool,
            PullRequestTool pullRequestTool,
            FixTaskTestRunService fixTaskTestRunService,
            FixTaskToolCallService fixTaskToolCallService,
            TaskCancellationChecker taskCancellationChecker
    ) {
        this(
                workspaceService,
                directConstructionRegistry(),
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                FixTaskAdapterMetadataRecorder.NOOP,
                fixTaskTestRunService,
                fixTaskToolCallService,
                taskCancellationChecker
        );
    }

    public NoopFixTaskExecutor(
            WorkspaceService workspaceService,
            LanguageAdapterRegistry languageAdapterRegistry,
            VerificationRunner verificationRunner,
            PatchWorkflow patchWorkflow,
            DiffTool diffTool,
            CommitTool commitTool,
            PushTool pushTool,
            PullRequestTool pullRequestTool,
            FixTaskTestRunService fixTaskTestRunService,
            FixTaskToolCallService fixTaskToolCallService,
            TaskCancellationChecker taskCancellationChecker
    ) {
        this(
                workspaceService,
                languageAdapterRegistry,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                FixTaskAdapterMetadataRecorder.NOOP,
                fixTaskTestRunService,
                fixTaskToolCallService,
                taskCancellationChecker
        );
    }

    @Autowired
    public NoopFixTaskExecutor(
            WorkspaceService workspaceService,
            LanguageAdapterRegistry languageAdapterRegistry,
            VerificationRunner verificationRunner,
            PatchWorkflow patchWorkflow,
            DiffTool diffTool,
            CommitTool commitTool,
            PushTool pushTool,
            PullRequestTool pullRequestTool,
            FixTaskAdapterMetadataRecorder adapterMetadataRecorder,
            FixTaskTestRunService fixTaskTestRunService,
            FixTaskToolCallService fixTaskToolCallService,
            TaskCancellationChecker taskCancellationChecker
    ) {
        this.workspaceService = workspaceService;
        this.languageAdapterRegistry = languageAdapterRegistry;
        this.verificationRunner = verificationRunner;
        this.patchWorkflow = patchWorkflow;
        this.diffTool = diffTool;
        this.commitTool = commitTool;
        this.pushTool = pushTool;
        this.pullRequestTool = pullRequestTool;
        this.adapterMetadataRecorder = adapterMetadataRecorder;
        this.fixTaskTestRunService = fixTaskTestRunService;
        this.fixTaskToolCallService = fixTaskToolCallService;
        this.taskCancellationChecker = taskCancellationChecker;
    }

    private static LanguageAdapterRegistry directConstructionRegistry() {
        return new LanguageAdapterRegistry(List.of(repositoryDir -> LanguageDetectionResult.supported(
                "java",
                "maven",
                List.of("./mvnw", "test"),
                "Assumed Java/Maven project for direct executor construction"
        )));
    }

    @Override
    public FixTaskExecutionResult execute(FixTaskVo task) {
        taskCancellationChecker.throwIfCancelled(task.id());
        PreparedWorkspaceResult preparedWorkspace = auditToolCall(
                task.id(),
                "WorkspaceService",
                "repository=%s/%s".formatted(task.repositoryOwner(), task.repositoryName()),
                () -> workspaceService.prepareRepository(new CloneWorkspaceCommand(
                        task.id(),
                        task.repositoryOwner(),
                        task.repositoryName()
                )),
                result -> "workspaceDir=%s, repositoryDir=%s, branchName=%s".formatted(
                        result.workspaceDir(),
                        result.repositoryDir(),
                        result.branchName()
                )
        );
        taskCancellationChecker.throwIfCancelled(task.id());
        LanguageDetectionResult detectionResult = auditToolCall(
                task.id(),
                "LanguageAdapterRegistry",
                "repositoryDir=%s".formatted(preparedWorkspace.repositoryDir()),
                () -> detectSupportedRepository(preparedWorkspace.repositoryDir()),
                result -> "%s/%s: %s".formatted(
                        result.language(),
                        result.buildSystem(),
                        result.reason()
                )
        );
        adapterMetadataRecorder.recordAdapterMetadata(
                task.id(),
                detectionResult.language(),
                detectionResult.buildSystem(),
                String.join(" ", detectionResult.verificationCommand()),
                detectionResult.reason()
        );
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
        TestRunResult testRunResult = verificationRunner.runVerification(
                task.id(),
                preparedWorkspace.repositoryDir(),
                detectionResult.verificationCommand()
        );
        Instant testFinishedAt = Instant.now();
        fixTaskTestRunService.recordTestRun(
                task.id(),
                testRunResult.command(),
                testRunResult.exitCode(),
                testRunResult.output(),
                testStartedAt,
                testFinishedAt
        );
        taskCancellationChecker.throwIfCancelled(task.id());
        if (testRunResult.exitCode() != 0) {
            throw new IllegalStateException("verification failed: " + testRunResult.output());
        }
        auditToolCall(
                task.id(),
                "CommitTool",
                "repositoryDir=%s, message=%s".formatted(
                        preparedWorkspace.repositoryDir(),
                        "PatchPilot task " + task.id()
                ),
                () -> commitTool.commitAll(task.id(), preparedWorkspace.repositoryDir(), "PatchPilot task " + task.id())
        );
        taskCancellationChecker.throwIfCancelled(task.id());
        auditToolCall(
                task.id(),
                "PushTool",
                "repositoryDir=%s, branchName=%s".formatted(
                        preparedWorkspace.repositoryDir(),
                        preparedWorkspace.branchName()
                ),
                () -> pushTool.pushBranch(task.id(), preparedWorkspace.repositoryDir(), preparedWorkspace.branchName())
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

    private LanguageDetectionResult detectSupportedRepository(java.nio.file.Path repositoryDir) {
        LanguageDetectionResult detectionResult = languageAdapterRegistry.detect(repositoryDir);
        if (!detectionResult.supported()) {
            throw new IllegalStateException(detectionResult.reason());
        }
        return detectionResult;
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
            taskCancellationChecker.throwIfCancelled(taskId);
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
