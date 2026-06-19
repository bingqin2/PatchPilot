package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.agent.tool.CommitTool;
import io.patchpilot.backend.agent.tool.DiffTool;
import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.MavenTestRunner;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.domain.vo.WorkspaceCloneResult;
import io.patchpilot.backend.workspace.service.WorkspaceService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspaceFixTaskExecutorTests {

    @Test
    void should_prepare_task_repository_and_run_maven_tests() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool();
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(0, "tests passed");
        FixTaskExecutor executor = new NoopFixTaskExecutor(workspaceService, mavenTestRunner, patchWorkflow, diffTool, commitTool);

        executor.execute(task());

        assertThat(workspaceService.command().taskId()).isEqualTo("task-123");
        assertThat(workspaceService.command().repositoryOwner()).isEqualTo("octocat");
        assertThat(workspaceService.command().repositoryName()).isEqualTo("hello-world");
        assertThat(patchWorkflow.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(diffTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(mavenTestRunner.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(commitTool.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
        assertThat(commitTool.message()).isEqualTo("PatchPilot task task-123");
        assertThat(patchWorkflow.callOrder()).isLessThan(diffTool.callOrder());
        assertThat(diffTool.callOrder()).isLessThan(mavenTestRunner.callOrder());
        assertThat(mavenTestRunner.callOrder()).isLessThan(commitTool.callOrder());
    }

    @Test
    void should_fail_when_maven_tests_fail() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingPatchWorkflow patchWorkflow = new RecordingPatchWorkflow();
        RecordingDiffTool diffTool = new RecordingDiffTool();
        RecordingCommitTool commitTool = new RecordingCommitTool();
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(1, "test failed");
        FixTaskExecutor executor = new NoopFixTaskExecutor(workspaceService, mavenTestRunner, patchWorkflow, diffTool, commitTool);

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("maven tests failed: test failed");
        assertThat(commitTool.callOrder()).isZero();
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
        private Path repositoryDir;
        private int callOrder;

        private RecordingMavenTestRunner(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

        @Override
        public TestRunResult runTests(Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            this.callOrder = CallOrder.next();
            return new TestRunResult("./mvnw test", exitCode, output);
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private int callOrder() {
            return callOrder;
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

        private Path repositoryDir;
        private String message;
        private int callOrder;

        private RecordingCommitTool() {
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
        }

        @Override
        public String commitAll(Path repositoryDir, String message) {
            this.repositoryDir = repositoryDir;
            this.message = message;
            this.callOrder = CallOrder.next();
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

    private static final class CallOrder {

        private static int value;

        private static int next() {
            value++;
            return value;
        }
    }
}
