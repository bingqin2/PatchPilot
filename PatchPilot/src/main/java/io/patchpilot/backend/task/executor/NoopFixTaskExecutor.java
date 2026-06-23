package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.agent.tool.CommitTool;
import io.patchpilot.backend.agent.tool.DiffTool;
import io.patchpilot.backend.agent.tool.PullRequestTool;
import io.patchpilot.backend.agent.tool.PushTool;
import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.github.IssueContextService;
import io.patchpilot.backend.github.client.GitHubIssueContextClient;
import io.patchpilot.backend.github.client.domain.GetIssueContextCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.safety.GeneratedDiffRiskGate;
import io.patchpilot.backend.safety.domain.GeneratedDiffRiskDecision;
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
    private final IssueContextService issueContextService;
    private final GeneratedDiffRiskGate generatedDiffRiskGate;
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
                new GeneratedDiffRiskGate(),
                defaultIssueContextService(),
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
                new GeneratedDiffRiskGate(),
                defaultIssueContextService(),
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
            IssueContextService issueContextService,
            FixTaskAdapterMetadataRecorder adapterMetadataRecorder,
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
                generatedDiffRiskGate(),
                issueContextService,
                adapterMetadataRecorder,
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
            FixTaskAdapterMetadataRecorder adapterMetadataRecorder,
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
                generatedDiffRiskGate(),
                defaultIssueContextService(),
                adapterMetadataRecorder,
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
            FixTaskAdapterMetadataRecorder adapterMetadataRecorder,
            FixTaskTestRunService fixTaskTestRunService,
            FixTaskToolCallService fixTaskToolCallService,
            TaskCancellationChecker taskCancellationChecker,
            GeneratedDiffRiskGate generatedDiffRiskGate
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
                generatedDiffRiskGate,
                defaultIssueContextService(),
                adapterMetadataRecorder,
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
            GeneratedDiffRiskGate generatedDiffRiskGate,
            IssueContextService issueContextService,
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
        this.issueContextService = issueContextService;
        this.generatedDiffRiskGate = generatedDiffRiskGate;
        this.adapterMetadataRecorder = adapterMetadataRecorder;
        this.fixTaskTestRunService = fixTaskTestRunService;
        this.fixTaskToolCallService = fixTaskToolCallService;
        this.taskCancellationChecker = taskCancellationChecker;
    }

    private static GeneratedDiffRiskGate generatedDiffRiskGate() {
        return new GeneratedDiffRiskGate();
    }

    private static LanguageAdapterRegistry directConstructionRegistry() {
        return new LanguageAdapterRegistry(List.of(repositoryDir -> LanguageDetectionResult.supported(
                "java",
                "maven",
                List.of("./mvnw", "test"),
                "Assumed Java/Maven project for direct executor construction"
        )));
    }

    private static IssueContextService defaultIssueContextService() {
        return new IssueContextService(new GitHubIssueContextClient(new GitHubProperties()) {
            @Override
            public GitHubIssueContext getIssueContext(GetIssueContextCommand command) {
                return new GitHubIssueContext("", "", "", List.of());
            }
        });
    }

    @Override
    public FixTaskExecutionResult execute(FixTaskVo task) {
        taskCancellationChecker.throwIfCancelled(task.id());
        PreparedWorkspaceResult preparedWorkspace = prepareWorkspace(task);
        taskCancellationChecker.throwIfCancelled(task.id());
        LanguageDetectionResult detectionResult = detectAndRecordAdapter(task, preparedWorkspace);
        if (task.riskReviewApprovedAt() == null) {
            generateAndRiskCheckDiff(task, preparedWorkspace);
        } else {
            recordApprovedRiskReview(task);
        }
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

    private PreparedWorkspaceResult prepareWorkspace(FixTaskVo task) {
        CloneWorkspaceCommand command = new CloneWorkspaceCommand(
                task.id(),
                task.repositoryOwner(),
                task.repositoryName()
        );
        boolean approvedReviewResume = task.riskReviewApprovedAt() != null;
        return auditToolCall(
                task.id(),
                "WorkspaceService",
                "repository=%s/%s, mode=%s".formatted(
                        task.repositoryOwner(),
                        task.repositoryName(),
                        approvedReviewResume ? "resume-approved-review" : "prepare"
                ),
                () -> approvedReviewResume
                        ? workspaceService.resumePreparedRepository(command)
                        : workspaceService.prepareRepository(command),
                result -> "workspaceDir=%s, repositoryDir=%s, branchName=%s".formatted(
                        result.workspaceDir(),
                        result.repositoryDir(),
                        result.branchName()
                )
        );
    }

    private LanguageDetectionResult detectAndRecordAdapter(FixTaskVo task, PreparedWorkspaceResult preparedWorkspace) {
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
        return detectionResult;
    }

    private void generateAndRiskCheckDiff(FixTaskVo task, PreparedWorkspaceResult preparedWorkspace) {
        GitHubIssueContext issueContext = auditToolCall(
                task.id(),
                "IssueContextService",
                "repository=%s/%s, issueNumber=%d".formatted(
                        task.repositoryOwner(),
                        task.repositoryName(),
                        task.issueNumber()
                ),
                () -> issueContextService.loadIssueContext(task),
                context -> "title=%s, comments=%d".formatted(context.title(), context.comments().size())
        );
        auditToolCall(
                task.id(),
                "PatchWorkflow",
                "repositoryDir=%s, triggerComment=%s".formatted(
                        preparedWorkspace.repositoryDir(),
                        task.triggerComment()
                ),
                () -> patchWorkflow.apply(task, preparedWorkspace.repositoryDir(), issueContext).summary()
        );
        taskCancellationChecker.throwIfCancelled(task.id());
        String diff = auditToolCall(
                task.id(),
                "DiffTool",
                "repositoryDir=%s".formatted(preparedWorkspace.repositoryDir()),
                () -> diffTool.diff(preparedWorkspace.repositoryDir())
        );
        taskCancellationChecker.throwIfCancelled(task.id());
        auditToolCall(
                task.id(),
                "GeneratedDiffRiskGate",
                "changedBytes=%d".formatted(diff.length()),
                () -> evaluateGeneratedDiffRisk(diff),
                GeneratedDiffRiskDecision::reason
        );
    }

    private void recordApprovedRiskReview(FixTaskVo task) {
        auditToolCall(
                task.id(),
                "GeneratedDiffRiskApproval",
                "approvedBy=%s approvedAt=%s".formatted(task.riskReviewApprovedBy(), task.riskReviewApprovedAt()),
                () -> "Operator approved the generated diff; resuming after risk gate"
        );
    }

    private LanguageDetectionResult detectSupportedRepository(java.nio.file.Path repositoryDir) {
        LanguageDetectionResult detectionResult = languageAdapterRegistry.detect(repositoryDir);
        if (!detectionResult.supported()) {
            throw new IllegalStateException(detectionResult.reason());
        }
        return detectionResult;
    }

    private GeneratedDiffRiskDecision evaluateGeneratedDiffRisk(String diff) {
        GeneratedDiffRiskDecision decision = generatedDiffRiskGate.evaluate(diff);
        if (!decision.allowed()) {
            throw new IllegalStateException(decision.reason());
        }
        return decision;
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
