package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.agent.tool.CommitTool;
import io.patchpilot.backend.agent.tool.DiffTool;
import io.patchpilot.backend.agent.tool.PullRequestTool;
import io.patchpilot.backend.agent.tool.PushTool;
import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.github.client.GitHubPullRequestClient;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.MavenTestRunner;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.domain.FixTaskExecutionResult;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.domain.vo.WorkspaceCloneResult;
import io.patchpilot.backend.workspace.service.WorkspaceService;
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
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                mavenTestRunner,
                patchWorkflow,
                diffTool,
                commitTool,
                pushTool,
                pullRequestTool,
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
        assertThat(diffTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(mavenTestRunner.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(mavenTestRunner.taskId()).isEqualTo("task-123");
        assertThat(commitTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(commitTool.message()).isEqualTo("PatchPilot task task-123");
        assertThat(pushTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(pushTool.branchName()).isEqualTo("patchpilot/task-123");
        assertThat(pullRequestTool.taskId()).isEqualTo("task-123");
        assertThat(pullRequestTool.branchName()).isEqualTo("patchpilot/task-123");
        assertThat(patchWorkflow.callOrder()).isLessThan(diffTool.callOrder());
        assertThat(diffTool.callOrder()).isLessThan(mavenTestRunner.callOrder());
        assertThat(mavenTestRunner.callOrder()).isLessThan(commitTool.callOrder());
        assertThat(commitTool.callOrder()).isLessThan(pushTool.callOrder());
        assertThat(pushTool.callOrder()).isLessThan(pullRequestTool.callOrder());
        assertThat(testRunService.taskId()).isEqualTo("task-123");
        assertThat(testRunService.command()).isEqualTo("./mvnw test");
        assertThat(testRunService.exitCode()).isZero();
        assertThat(testRunService.output()).isEqualTo("tests passed");
        assertThat(testRunService.startedAt()).isBeforeOrEqualTo(testRunService.finishedAt());
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly("PatchWorkflow", "DiffTool", "CommitTool", "PushTool", "PullRequestTool");
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
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(1, "test failed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                mavenTestRunner,
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
                .hasMessage("maven tests failed: test failed");
        assertThat(testRunService.taskId()).isEqualTo("task-123");
        assertThat(testRunService.command()).isEqualTo("./mvnw test");
        assertThat(testRunService.exitCode()).isEqualTo(1);
        assertThat(testRunService.output()).isEqualTo("test failed");
        assertThat(commitTool.callOrder()).isZero();
        assertThat(pushTool.callOrder()).isZero();
        assertThat(pullRequestTool.callOrder()).isZero();
        assertThat(toolCallService.toolCalls())
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly("PatchWorkflow", "DiffTool");
    }

    @Test
    void should_not_push_when_local_commit_fails() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(true);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                mavenTestRunner,
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
                .containsExactly("PatchWorkflow", "DiffTool", "CommitTool");
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
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                mavenTestRunner,
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
                .containsExactly("PatchWorkflow", "DiffTool", "CommitTool", "PushTool");
        assertThat(toolCallService.lastToolCall().success()).isFalse();
        assertThat(toolCallService.lastToolCall().outputSummary()).isEqualTo("git push failed: permission denied");
    }

    @Test
    void should_stop_before_commit_when_task_is_cancelled_after_tests() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool(false);
        RecordingPushTool pushTool = new RecordingPushTool(false);
        RecordingPullRequestTool pullRequestTool = new RecordingPullRequestTool();
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(0, "tests passed");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        StageCancellingChecker cancellationChecker = new StageCancellingChecker(5);
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                mavenTestRunner,
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
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(130, "maven test command interrupted");
        RecordingFixTaskTestRunService testRunService = new RecordingFixTaskTestRunService();
        RecordingFixTaskToolCallService toolCallService = new RecordingFixTaskToolCallService();
        StageCancellingChecker cancellationChecker = new StageCancellingChecker(5);
        FixTaskExecutor executor = new NoopFixTaskExecutor(
                workspaceService,
                mavenTestRunner,
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

    private static final class RecordingWorkspaceService implements WorkspaceService {

        private CloneWorkspaceCommand command;

        @Override
        public WorkspaceCloneResult cloneRepository(CloneWorkspaceCommand command) {
            throw new AssertionError("Expected executor to call prepareRepository");
        }

        @Override
        public PreparedWorkspaceResult prepareRepository(CloneWorkspaceCommand command) {
            this.command = command;
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
    }

    private static final class RecordingMavenTestRunner extends MavenTestRunner {

        private final int exitCode;
        private final String output;
        private String taskId;
        private Path repositoryDir;
        private int callOrder;

        private RecordingMavenTestRunner(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

        @Override
        public TestRunResult runTests(String taskId, Path repositoryDir) {
            this.taskId = taskId;
            return runTests(repositoryDir);
        }

        @Override
        public TestRunResult runTests(Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            this.callOrder = CallOrder.next();
            return new TestRunResult("./mvnw test", exitCode, output);
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
        private int callOrder;

        @Override
        public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            this.callOrder = CallOrder.next();
            return new PatchWorkflowResult(true, "patch applied");
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private int callOrder() {
            return callOrder;
        }
    }

    private static final class RecordingDiffTool extends DiffTool {

        private Path repositoryDir;
        private int callOrder;

        private RecordingDiffTool() {
            super(new GitCommandRunner() {
                @Override
                public GitCommandResult diff(Path repositoryDir) {
                    return new GitCommandResult(0, "diff");
                }
            });
        }

        @Override
        public String diff(Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            this.callOrder = CallOrder.next();
            return "diff";
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
            this.repositoryDir = repositoryDir;
            this.message = message;
            this.callOrder = CallOrder.next();
            if (fail) {
                throw new IllegalStateException("git commit failed: nothing to commit");
            }
            return "committed";
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
            this.repositoryDir = repositoryDir;
            this.branchName = branchName;
            this.callOrder = CallOrder.next();
            if (fail) {
                throw new IllegalStateException("git push failed: permission denied");
            }
            return "pushed";
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
