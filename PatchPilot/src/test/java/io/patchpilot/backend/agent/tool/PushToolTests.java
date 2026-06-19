package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.recovery.GitWorkspaceRecoveryInspector;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PushToolTests {

    @Test
    void should_push_repository_branch() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(new GitCommandResult(0, "pushed"));
        PushTool tool = new PushTool(runner);

        String output = tool.pushBranch(Path.of("/tmp/repo"), "patchpilot/task-123");

        assertThat(output).isEqualTo("pushed");
        assertThat(runner.repositoryDir()).isEqualTo(Path.of("/tmp/repo"));
        assertThat(runner.branchName()).isEqualTo("patchpilot/task-123");
    }

    @Test
    void should_pass_task_id_to_push() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(new GitCommandResult(0, "pushed"));
        PushTool tool = new PushTool(runner);

        String output = tool.pushBranch("task-123", Path.of("/tmp/repo"), "patchpilot/task-123");

        assertThat(output).isEqualTo("pushed");
        assertThat(runner.taskId()).isEqualTo("task-123");
    }

    @Test
    void should_fail_when_push_fails() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(new GitCommandResult(128, "permission denied"));
        PushTool tool = new PushTool(runner);

        assertThatThrownBy(() -> tool.pushBranch(Path.of("/tmp/repo"), "patchpilot/task-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git push failed: permission denied");
    }

    @Test
    void should_append_recovery_guidance_when_push_fails_with_git_state() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(new GitCommandResult(128, "lock exists"));
        PushTool tool = new PushTool(runner, new FixedRecoveryInspector("Git HEAD lock detected (.git/HEAD.lock); manual cleanup required before retry."));

        assertThatThrownBy(() -> tool.pushBranch("task-123", Path.of("/tmp/repo"), "patchpilot/task-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git push failed: lock exists\nGit HEAD lock detected (.git/HEAD.lock); manual cleanup required before retry.");
    }

    private static final class RecordingGitCommandRunner extends GitCommandRunner {

        private final GitCommandResult result;
        private String taskId;
        private Path repositoryDir;
        private String branchName;

        private RecordingGitCommandRunner(GitCommandResult result) {
            this.result = result;
        }

        @Override
        public GitCommandResult pushBranch(Path repositoryDir, String branchName) {
            return pushBranch(null, repositoryDir, branchName);
        }

        @Override
        public GitCommandResult pushBranch(String taskId, Path repositoryDir, String branchName) {
            this.taskId = taskId;
            this.repositoryDir = repositoryDir;
            this.branchName = branchName;
            return result;
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
    }

    private static final class FixedRecoveryInspector extends GitWorkspaceRecoveryInspector {

        private final String guidance;

        private FixedRecoveryInspector(String guidance) {
            this.guidance = guidance;
        }

        @Override
        public Optional<String> inspect(Path repositoryDir) {
            return Optional.of(guidance);
        }
    }
}
