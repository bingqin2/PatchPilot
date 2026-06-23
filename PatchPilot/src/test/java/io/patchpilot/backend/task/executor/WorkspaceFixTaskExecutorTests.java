package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.agent.tool.CommitTool;
import io.patchpilot.backend.agent.tool.DiffTool;
import io.patchpilot.backend.agent.tool.PullRequestTool;
import io.patchpilot.backend.agent.tool.PushTool;
import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.github.IssueContextService;
import io.patchpilot.backend.github.client.GitHubIssueContextClient;
import io.patchpilot.backend.github.client.domain.GetIssueContextCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.GitHubIssueContextComment;
import io.patchpilot.backend.github.client.GitHubPullRequestClient;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.language.LanguageAdapterRegistry;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.CommandExecutionGuard;
import io.patchpilot.backend.runner.service.VerificationRunner;
import io.patchpilot.backend.safety.GeneratedDiffRiskGate;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.domain.FixTaskExecutionResult;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.domain.vo.WorkspaceCloneResult;
import io.patchpilot.backend.workspace.service.WorkspaceService;
import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspaceFixTaskExecutorTests {

    @Test
    void should_prepare_task_repository_and_run_maven_tests() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingLanguageAdapterRegistry languageAdapterRegistry = new RecordingLanguageAdapterRegistry(
                LanguageDetectionResult.supported(
                        "java",
                        "maven",
                        List.of("custom-verify", "test"),
                        "Detected custom verifier"
                )
        );
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        RecordingFixTaskService fixTaskService = new RecordingFixTaskService(task());
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                languageAdapterRegistry,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                new RecordingIssueContextService(),
                fixTaskService,
                testRunService,
                toolCallService,
                taskId -> { }
        );

        FixTaskExecutionResult result = executor.execute(task());

        assertThat(result.pullRequestUrl()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(workspaceService.command().taskId()).isEqualTo("task-123");
        assertThat(workspaceService.command().repositoryOwner()).isEqualTo("octocat");
        assertThat(workspaceService.command().repositoryName()).isEqualTo("hello-world");
        assertThat(patchWorkflow.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(patchWorkflow.issueContext().title()).isEqualTo("Calculator add returns wrong value");
        assertThat(diffTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(verificationRunner.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(verificationRunner.taskId()).isEqualTo("task-123");
        assertThat(verificationRunner.command()).containsExactly("custom-verify", "test");
        assertThat(fixTaskService.language()).isEqualTo("java");
        assertThat(fixTaskService.buildSystem()).isEqualTo("maven");
        assertThat(fixTaskService.verificationCommand()).isEqualTo("custom-verify test");
        assertThat(fixTaskService.adapterDetectionReason()).isEqualTo("Detected custom verifier");
        assertThat(commitTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(commitTool.taskId()).isEqualTo("task-123");
        assertThat(commitTool.message()).isEqualTo("PatchPilot task task-123");
        assertThat(pushTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(pushTool.taskId()).isEqualTo("task-123");
        assertThat(pushTool.branchName()).isEqualTo("patchpilot/task-123");
        assertThat(pullRequestTool.taskId()).isEqualTo("task-123");
        assertThat(pullRequestTool.branchName()).isEqualTo("patchpilot/task-123");
        assertThat(patchWorkflow.callOrder()).isLessThan(diffTool.callOrder());
        assertThat(diffTool.callOrder()).isLessThan(verificationRunner.callOrder());
        assertThat(verificationRunner.callOrder()).isLessThan(commitTool.callOrder());
        assertThat(commitTool.callOrder()).isLessThan(pushTool.callOrder());
        assertThat(pushTool.callOrder()).isLessThan(pullRequestTool.callOrder());
        assertThat(testRunService.taskId()).isEqualTo("task-123");
        assertThat(testRunService.command()).isEqualTo("custom-verify test");
        assertThat(testRunService.exitCode()).isZero();
        assertThat(testRunService.output()).isEqualTo("tests passed");
        assertThat(testRunService.startedAt()).isBeforeOrEqualTo(testRunService.finishedAt());
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly(
                        "WorkspaceService",
                        "LanguageAdapterRegistry",
                        "IssueContextService",
                        "PatchWorkflow",
                        "DiffTool",
                        "GeneratedDiffRiskGate",
                        "CommitTool",
                        "PushTool",
                        "PullRequestTool"
                );
        assertThat(toolCallService.toolCalls())
                .allSatisfy(toolCall -> {
                    assertThat(toolCall.taskId()).isEqualTo("task-123");
                    assertThat(toolCall.success()).isTrue();
                    assertThat(toolCall.startedAt()).isBeforeOrEqualTo(toolCall.finishedAt());
                });
    }

    @Test
    void should_fail_when_maven_tests_fail() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(1, "test failed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                testRunService,
                toolCallService,
                taskId -> { }
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("verification failed: test failed");
        assertThat(testRunService.taskId()).isEqualTo("task-123");
        assertThat(testRunService.command()).isEqualTo("./mvnw test");
        assertThat(testRunService.exitCode()).isEqualTo(1);
        assertThat(testRunService.output()).isEqualTo("test failed");
        assertThat(commitTool.callOrder()).isZero();
        assertThat(pushTool.callOrder()).isZero();
        assertThat(pullRequestTool.callOrder()).isZero();
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly(
                        "WorkspaceService",
                        "LanguageAdapterRegistry",
                        "IssueContextService",
                        "PatchWorkflow",
                        "DiffTool",
                        "GeneratedDiffRiskGate"
                );
    }

    @Test
    void should_fail_unsupported_repository_before_patch_workflow_or_tests() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        RecordingLanguageAdapterRegistry languageAdapterRegistry = new RecordingLanguageAdapterRegistry(
                LanguageDetectionResult.unsupported(
                        "unknown",
                        "unknown",
                        "Unsupported repository: no supported language adapter detected"
                )
        );
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                languageAdapterRegistry,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                testRunService,
                toolCallService,
                taskId -> { }
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unsupported repository: no supported language adapter detected");

        assertThat(languageAdapterRegistry.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(patchWorkflow.callOrder()).isZero();
        assertThat(diffTool.callOrder()).isZero();
        assertThat(verificationRunner.callOrder()).isZero();
        assertThat(testRunService.taskId()).isNull();
        assertThat(commitTool.callOrder()).isZero();
        assertThat(pushTool.callOrder()).isZero();
        assertThat(pullRequestTool.callOrder()).isZero();
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly("WorkspaceService", "LanguageAdapterRegistry");
        assertThat(toolCallService.lastToolCall().success()).isFalse();
        assertThat(toolCallService.lastToolCall().outputSummary())
                .isEqualTo("Unsupported repository: no supported language adapter detected");
    }

    @Test
    void should_not_push_when_local_commit_fails() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(true);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                testRunService,
                toolCallService,
                taskId -> { }
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git commit failed: nothing to commit");
        assertThat(pushTool.callOrder()).isZero();
        assertThat(pullRequestTool.callOrder()).isZero();
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly(
                        "WorkspaceService",
                        "LanguageAdapterRegistry",
                        "IssueContextService",
                        "PatchWorkflow",
                        "DiffTool",
                        "GeneratedDiffRiskGate",
                        "CommitTool"
                );
        assertThat(toolCallService.lastToolCall().success()).isFalse();
        assertThat(toolCallService.lastToolCall().outputSummary()).isEqualTo("git commit failed: nothing to commit");
    }

    @Test
    void should_not_create_pull_request_when_push_fails() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(true);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                testRunService,
                toolCallService,
                taskId -> { }
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git push failed: permission denied");
        assertThat(pullRequestTool.callOrder()).isZero();
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly(
                        "WorkspaceService",
                        "LanguageAdapterRegistry",
                        "IssueContextService",
                        "PatchWorkflow",
                        "DiffTool",
                        "GeneratedDiffRiskGate",
                        "CommitTool",
                        "PushTool"
                );
        assertThat(toolCallService.lastToolCall().success()).isFalse();
        assertThat(toolCallService.lastToolCall().outputSummary()).isEqualTo("git push failed: permission denied");
    }

    @Test
    void should_stop_before_verification_when_generated_diff_is_high_risk() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool("""
                diff --git a/.github/workflows/deploy.yml b/.github/workflows/deploy.yml
                index 1111111..2222222 100644
                --- a/.github/workflows/deploy.yml
                +++ b/.github/workflows/deploy.yml
                @@ -1,2 +1,2 @@
                -name: deploy
                +name: deploy changed
                """);
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                new RecordingLanguageAdapterRegistry(LanguageDetectionResult.supported(
                        "java",
                        "maven",
                        List.of("./mvnw", "test"),
                        "pom.xml detected"
                )),
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                new RecordingFixTaskService(task()),
                testRunService,
                toolCallService,
                taskId -> { },
                new GeneratedDiffRiskGate()
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Generated diff rejected");
        assertThat(diffTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(verificationRunner.callOrder()).isZero();
        assertThat(testRunService.taskId()).isNull();
        assertThat(commitTool.callOrder()).isZero();
        assertThat(pushTool.callOrder()).isZero();
        assertThat(pullRequestTool.callOrder()).isZero();
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly(
                        "WorkspaceService",
                        "LanguageAdapterRegistry",
                        "IssueContextService",
                        "PatchWorkflow",
                        "DiffTool",
                        "GeneratedDiffRiskGate"
                );
        assertThat(toolCallService.lastToolCall().success()).isFalse();
        assertThat(toolCallService.lastToolCall().outputSummary())
                .contains("sensitive path .github/workflows/deploy.yml");
    }

    @Test
    void should_resume_approved_pending_review_task_without_regenerating_diff() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingLanguageAdapterRegistry languageAdapterRegistry = new RecordingLanguageAdapterRegistry(
                LanguageDetectionResult.supported(
                        "node",
                        "npm",
                        List.of("npm", "test"),
                        "package.json contains a non-empty scripts.test"
                )
        );
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        RecordingFixTaskService fixTaskService = new RecordingFixTaskService(approvedReviewTask());
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                languageAdapterRegistry,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                fixTaskService,
                testRunService,
                toolCallService,
                taskId -> { }
        );

        FixTaskExecutionResult result = executor.execute(approvedReviewTask());

        assertThat(result.pullRequestUrl()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(workspaceService.command().taskId()).isEqualTo("task-123");
        assertThat(workspaceService.prepared()).isFalse();
        assertThat(workspaceService.resumed()).isTrue();
        assertThat(languageAdapterRegistry.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(patchWorkflow.callOrder()).isZero();
        assertThat(diffTool.callOrder()).isZero();
        assertThat(verificationRunner.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(verificationRunner.command()).containsExactly("npm", "test");
        assertThat(commitTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(pushTool.branchName()).isEqualTo("patchpilot/task-123");
        assertThat(pullRequestTool.branchName()).isEqualTo("patchpilot/task-123");
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly(
                        "WorkspaceService",
                        "LanguageAdapterRegistry",
                        "GeneratedDiffRiskApproval",
                        "CommitTool",
                        "PushTool",
                        "PullRequestTool"
                );
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::inputSummary)
                .contains("approvedBy=release-captain approvedAt=2026-06-18T01:02:03Z");
    }

    @Test
    void should_stop_before_commit_when_task_is_cancelled_after_tests() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        StageCancellingChecker cancellationChecker = new StageCancellingChecker(7);
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                testRunService,
                toolCallService,
                cancellationChecker
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(TaskCancellationException.class)
                .hasMessage("Task cancelled: task-123");
        assertThat(testRunService.taskId()).isEqualTo("task-123");
        assertThat(commitTool.callOrder()).isZero();
        assertThat(pushTool.callOrder()).isZero();
        assertThat(pullRequestTool.callOrder()).isZero();
        assertThat(cancellationChecker.checkedTaskIds()).contains("task-123");
    }

    @Test
    void should_treat_non_zero_maven_result_as_cancellation_when_task_was_cancelled_during_tests() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(130, "verification command interrupted");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        StageCancellingChecker cancellationChecker = new StageCancellingChecker(7);
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                testRunService,
                toolCallService,
                cancellationChecker
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(TaskCancellationException.class)
                .hasMessage("Task cancelled: task-123");
        assertThat(testRunService.exitCode()).isEqualTo(130);
        assertThat(commitTool.callOrder()).isZero();
        assertThat(pushTool.callOrder()).isZero();
        assertThat(pullRequestTool.callOrder()).isZero();
    }

    @Test
    void should_treat_commit_failure_as_cancellation_when_task_was_cancelled_during_commit() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(true);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        StageCancellingChecker cancellationChecker = new StageCancellingChecker(8);
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                testRunService,
                toolCallService,
                cancellationChecker
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(TaskCancellationException.class)
                .hasMessage("Task cancelled: task-123");
        assertThat(pushTool.callOrder()).isZero();
        assertThat(pullRequestTool.callOrder()).isZero();
    }

    @Test
    void should_treat_push_failure_as_cancellation_when_task_was_cancelled_during_push() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(true);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingVerificationRunner verificationRunner = new RecordingVerificationRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        StageCancellingChecker cancellationChecker = new StageCancellingChecker(9);
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                verificationRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
                testRunService,
                toolCallService,
                cancellationChecker
        );

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(TaskCancellationException.class)
                .hasMessage("Task cancelled: task-123");
        assertThat(pullRequestTool.callOrder()).isZero();
    }

    private static FixTaskVo task() {
        return new FixTaskVo(
                "task-123",
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                "delivery-123",
                98765,
                FixTaskStatus.RUNNING,
                null,
                Instant.parse("2026-06-18T00:00:00Z")
        );
    }

    private static FixTaskVo approvedReviewTask() {
        return new FixTaskVo(
                "task-123",
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix",
                "delivery-123",
                98765,
                FixTaskStatus.RUNNING_TESTS,
                null,
                Instant.parse("2026-06-18T00:00:00Z"),
                null,
                null,
                Instant.parse("2026-06-18T01:02:04Z"),
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.parse("2026-06-18T01:02:03Z"),
                "release-captain",
                "Reviewed generated diff and accepted docs-only change"
        );
    }

    private static final class RecordingWorkspaceService implements WorkspaceService {

        private CloneWorkspaceCommand command;
        private boolean prepared;
        private boolean resumed;

        @Override
        public WorkspaceCloneResult cloneRepository(CloneWorkspaceCommand command) {
            throw new AssertionError("Expected executor to call prepareRepository");
        }

        @Override
        public PreparedWorkspaceResult prepareRepository(CloneWorkspaceCommand command) {
            this.command = command;
            this.prepared = true;
            return new PreparedWorkspaceResult(
                    command.taskId(),
                    Path.of("/tmp/workspace"),
                    Path.of("/tmp/workspace/repo"),
                    "patchpilot/" + command.taskId()
            );
        }

        @Override
        public PreparedWorkspaceResult resumePreparedRepository(CloneWorkspaceCommand command) {
            this.command = command;
            this.resumed = true;
            return new PreparedWorkspaceResult(
                    command.taskId(),
                    Path.of("/tmp/workspace"),
                    Path.of("/tmp/workspace/repo"),
                    "patchpilot/" + command.taskId()
            );
        }

        private CloneWorkspaceCommand command() {
            return command;
        }

        private boolean prepared() {
            return prepared;
        }

        private boolean resumed() {
            return resumed;
        }
    }

    private static final class RecordingVerificationRunner extends VerificationRunner {

        private final int exitCode;
        private final String output;
        private String taskId;
        private Path repositoryDir;
        private List<String> command;
        private int callOrder;

        private RecordingVerificationRunner(int exitCode, String output) {
            super(
                    new CommandExecutionGuard(workspaceProperties()),
                    new io.patchpilot.backend.task.process.TaskProcessRegistry()
            );
            this.exitCode = exitCode;
            this.output = output;
        }

        @Override
        public TestRunResult runVerification(String taskId, Path repositoryDir, List<String> command) {
            this.taskId = taskId;
            this.repositoryDir = repositoryDir;
            this.command = List.copyOf(command);
            this.callOrder = CallOrder.next();
            return new TestRunResult(String.join(" ", command), exitCode, output);
        }

        private String taskId() {
            return taskId;
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private int callOrder() {
            return callOrder;
        }

        private List<String> command() {
            return command;
        }

        private static WorkspaceProperties workspaceProperties() {
            WorkspaceProperties properties = new WorkspaceProperties();
            properties.setRootDir(Path.of("/tmp/workspace"));
            return properties;
        }
    }

    private static final class RecordingLanguageAdapterRegistry extends LanguageAdapterRegistry {

        private final LanguageDetectionResult detectionResult;
        private Path repositoryDir;

        private RecordingLanguageAdapterRegistry(LanguageDetectionResult detectionResult) {
            super(List.of());
            this.detectionResult = detectionResult;
        }

        @Override
        public LanguageDetectionResult detect(Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            return detectionResult;
        }

        private Path repositoryDir() {
            return repositoryDir;
        }
    }

    private static final class RecordingFixTaskService extends InMemoryFixTaskService {

        private final FixTaskVo task;
        private String language;
        private String buildSystem;
        private String verificationCommand;
        private String adapterDetectionReason;

        private RecordingFixTaskService(FixTaskVo task) {
            this.task = task;
        }

        @Override
        public FixTaskVo recordAdapterMetadata(
                String id,
                String language,
                String buildSystem,
                String verificationCommand,
                String adapterDetectionReason
        ) {
            this.language = language;
            this.buildSystem = buildSystem;
            this.verificationCommand = verificationCommand;
            this.adapterDetectionReason = adapterDetectionReason;
            return task.withAdapterMetadata(language, buildSystem, verificationCommand, adapterDetectionReason);
        }

        private String language() {
            return language;
        }

        private String buildSystem() {
            return buildSystem;
        }

        private String verificationCommand() {
            return verificationCommand;
        }

        private String adapterDetectionReason() {
            return adapterDetectionReason;
        }
    }

    private static final class RecordingFixTaskTestRunService implements FixTaskTestRunService {

        private String taskId;
        private String command;
        private int exitCode;
        private String output;
        private Instant startedAt;
        private Instant finishedAt;

        @Override
        public FixTaskTestRunVo recordTestRun(
                String taskId,
                String command,
                int exitCode,
                String output,
                Instant startedAt,
                Instant finishedAt
        ) {
            this.taskId = taskId;
            this.command = command;
            this.exitCode = exitCode;
            this.output = output;
            this.startedAt = startedAt;
            this.finishedAt = finishedAt;
            return new FixTaskTestRunVo(
                    "test-run-123",
                    taskId,
                    command,
                    exitCode,
                    output,
                    startedAt,
                    finishedAt,
                    java.time.Duration.between(startedAt, finishedAt).toMillis()
            );
        }

        @Override
        public List<FixTaskTestRunVo> listTestRuns(String taskId) {
            return List.of();
        }

        private String taskId() {
            return taskId;
        }

        private String command() {
            return command;
        }

        private int exitCode() {
            return exitCode;
        }

        private String output() {
            return output;
        }

        private Instant startedAt() {
            return startedAt;
        }

        private Instant finishedAt() {
            return finishedAt;
        }
    }

    private static final class RecordingFixTaskToolCallService implements FixTaskToolCallService {

        private final List<FixTaskToolCallVo> toolCalls = new java.util.ArrayList<>();

        @Override
        public FixTaskToolCallVo recordToolCall(
                String taskId,
                String toolName,
                String inputSummary,
                String outputSummary,
                boolean success,
                Instant startedAt,
                Instant finishedAt
        ) {
            FixTaskToolCallVo toolCall = new FixTaskToolCallVo(
                    "tool-call-" + (toolCalls.size() + 1),
                    taskId,
                    toolName,
                    inputSummary,
                    outputSummary,
                    success,
                    startedAt,
                    finishedAt,
                    java.time.Duration.between(startedAt, finishedAt).toMillis()
            );
            toolCalls.add(toolCall);
            return toolCall;
        }

        @Override
        public List<FixTaskToolCallVo> listToolCalls(String taskId) {
            return toolCalls.stream()
                    .filter(toolCall -> toolCall.taskId().equals(taskId))
                    .toList();
        }

        private List<FixTaskToolCallVo> toolCalls() {
            return toolCalls;
        }

        private FixTaskToolCallVo lastToolCall() {
            return toolCalls.get(toolCalls.size() - 1);
        }
    }

    private static final class RecordingPatchWorkflow implements PatchWorkflow {

        private Path repositoryDir;
        private GitHubIssueContext issueContext;
        private int callOrder;

        @Override
        public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            this.callOrder = CallOrder.next();
            return new PatchWorkflowResult(true, "patch applied");
        }

        @Override
        public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir, GitHubIssueContext issueContext) {
            this.repositoryDir = repositoryDir;
            this.issueContext = issueContext;
            this.callOrder = CallOrder.next();
            return new PatchWorkflowResult(true, "patch applied");
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private GitHubIssueContext issueContext() {
            return issueContext;
        }

        private int callOrder() {
            return callOrder;
        }
    }

    private static final class RecordingIssueContextService extends IssueContextService {

        private RecordingIssueContextService() {
            super(new GitHubIssueContextClient(new GitHubProperties()) {
                @Override
                public GitHubIssueContext getIssueContext(GetIssueContextCommand command) {
                    return new GitHubIssueContext(
                            "Calculator add returns wrong value",
                            "The issue body describes the failing add test.",
                            "https://github.com/octocat/hello-world/issues/42",
                            List.of(new GitHubIssueContextComment(
                                    1001,
                                    "alice",
                                    "Reproduced in CalculatorTest#addsNumbers.",
                                    "2026-06-20T01:00:00Z",
                                    "https://github.com/octocat/hello-world/issues/42#issuecomment-1001"
                            ))
                    );
                }
            });
        }
    }

    private static final class RecordingDiffTool extends DiffTool {

        private Path repositoryDir;
        private int callOrder;

        private final String diff;

        private RecordingDiffTool() {
            this("diff");
        }

        private RecordingDiffTool(String diff) {
            super(new GitCommandRunner() {
                @Override
                public GitCommandResult diff(Path repositoryDir) {
                    return new GitCommandResult(0, "diff");
                }
            });
            this.diff = diff;
        }

        @Override
        public String diff(Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            this.callOrder = CallOrder.next();
            return diff;
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private int callOrder() {
            return callOrder;
        }
    }

    private static final class RecordingCommitTool extends CommitTool {

        private final boolean fail;
        private String taskId;
        private Path repositoryDir;
        private String message;
        private int callOrder;

        private RecordingCommitTool(boolean fail) {
            super(new GitCommandRunner() {
                @Override
                public GitCommandResult stageAll(Path repositoryDir) {
                    return new GitCommandResult(0, "staged");
                }

                @Override
                public GitCommandResult commit(Path repositoryDir, String message) {
                    return new GitCommandResult(0, "committed");
                }
            });
            this.fail = fail;
        }

        @Override
        public String commitAll(Path repositoryDir, String message) {
            return commitAll(null, repositoryDir, message);
        }

        @Override
        public String commitAll(String taskId, Path repositoryDir, String message) {
            this.taskId = taskId;
            this.repositoryDir = repositoryDir;
            this.message = message;
            this.callOrder = CallOrder.next();
            if (fail) {
                throw new IllegalStateException("git commit failed: nothing to commit");
            }
            return "committed";
        }

        private String taskId() {
            return taskId;
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private String message() {
            return message;
        }

        private int callOrder() {
            return callOrder;
        }
    }

    private static final class RecordingPushTool extends PushTool {

        private final boolean fail;
        private String taskId;
        private Path repositoryDir;
        private String branchName;
        private int callOrder;

        private RecordingPushTool(boolean fail) {
            super(new GitCommandRunner() {
                @Override
                public GitCommandResult pushBranch(Path repositoryDir, String branchName) {
                    return new GitCommandResult(0, "pushed");
                }
            });
            this.fail = fail;
        }

        @Override
        public String pushBranch(Path repositoryDir, String branchName) {
            return pushBranch(null, repositoryDir, branchName);
        }

        @Override
        public String pushBranch(String taskId, Path repositoryDir, String branchName) {
            this.taskId = taskId;
            this.repositoryDir = repositoryDir;
            this.branchName = branchName;
            this.callOrder = CallOrder.next();
            if (fail) {
                throw new IllegalStateException("git push failed: permission denied");
            }
            return "pushed";
        }

        private String taskId() {
            return taskId;
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private String branchName() {
            return branchName;
        }

        private int callOrder() {
            return callOrder;
        }
    }

    private static final class RecordingPullRequestTool extends PullRequestTool {

        private String taskId;
        private String branchName;
        private int callOrder;

        private RecordingPullRequestTool() {
            super(new GitHubPullRequestClient(new GitHubProperties()) {
                @Override
                public PullRequestResult createPullRequest(CreatePullRequestCommand command) {
                    return new PullRequestResult("https://github.com/octocat/hello-world/pull/7");
                }
            });
        }

        @Override
        public PullRequestResult createPullRequest(FixTaskVo task, String branchName) {
            this.taskId = task.id();
            this.branchName = branchName;
            this.callOrder = CallOrder.next();
            return new PullRequestResult("https://github.com/octocat/hello-world/pull/7");
        }

        private String taskId() {
            return taskId;
        }

        private String branchName() {
            return branchName;
        }

        private int callOrder() {
            return callOrder;
        }
    }

    private static final class StageCancellingChecker implements TaskCancellationChecker {

        private final int cancelAtCheck;
        private final List<String> checkedTaskIds = new java.util.ArrayList<>();

        private StageCancellingChecker(int cancelAtCheck) {
            this.cancelAtCheck = cancelAtCheck;
        }

        @Override
        public void throwIfCancelled(String taskId) {
            checkedTaskIds.add(taskId);
            if (checkedTaskIds.size() >= cancelAtCheck) {
                throw new TaskCancellationException(taskId);
            }
        }

        private List<String> checkedTaskIds() {
            return checkedTaskIds;
        }
    }

    private static final class CallOrder {

        private static int value;

        private static int next() {
            value++;
            return value;
        }
    }
}
