package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.MavenTestRunner;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
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
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(0, "tests passed");
        FixTaskExecutor executor = new NoopFixTaskExecutor(workspaceService, mavenTestRunner);

        executor.execute(task());

        assertThat(workspaceService.command().taskId()).isEqualTo("task-123");
        assertThat(workspaceService.command().repositoryOwner()).isEqualTo("octocat");
        assertThat(workspaceService.command().repositoryName()).isEqualTo("hello-world");
        assertThat(mavenTestRunner.repositoryDir()).isEqualTo(Path.of("/tmp/workspace/repo"));
    }

    @Test
    void should_fail_when_maven_tests_fail() {
        RecordingWorkspaceService workspaceService = new RecordingWorkspaceService();
        RecordingMavenTestRunner mavenTestRunner = new RecordingMavenTestRunner(1, "test failed");
        FixTaskExecutor executor = new NoopFixTaskExecutor(workspaceService, mavenTestRunner);

        assertThatThrownBy(() -> executor.execute(task()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("maven tests failed: test failed");
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

        private RecordingMavenTestRunner(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

        @Override
        public TestRunResult runTests(Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            return new TestRunResult("./mvnw test", exitCode, output);
        }

        private Path repositoryDir() {
            return repositoryDir;
        }
    }
}
