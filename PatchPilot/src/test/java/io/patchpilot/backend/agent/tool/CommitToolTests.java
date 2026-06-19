package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.recovery.GitWorkspaceRecoveryInspector;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommitToolTests {

    @Test
    void should_stage_all_changes_before_creating_commit() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(
                new GitCommandResult(0, "staged"),
                new GitCommandResult(0, "committed")
        );
        CommitTool tool = new CommitTool(runner);

        String output = tool.commitAll(Path.of("/tmp/repo"), "PatchPilot task task-123");

        assertThat(output).isEqualTo("committed");
        assertThat(runner.stageRepositoryDir()).isEqualTo(Path.of("/tmp/repo"));
        assertThat(runner.commitRepositoryDir()).isEqualTo(Path.of("/tmp/repo"));
        assertThat(runner.commitMessage()).isEqualTo("PatchPilot task task-123");
        assertThat(runner.stageCallOrder()).isLessThan(runner.commitCallOrder());
    }

    @Test
    void should_pass_task_id_to_stage_and_commit() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(
                new GitCommandResult(0, "staged"),
                new GitCommandResult(0, "committed")
        );
        CommitTool tool = new CommitTool(runner);

        String output = tool.commitAll("task-123", Path.of("/tmp/repo"), "PatchPilot task task-123");

        assertThat(output).isEqualTo("committed");
        assertThat(runner.stageTaskId()).isEqualTo("task-123");
        assertThat(runner.commitTaskId()).isEqualTo("task-123");
    }

    @Test
    void should_fail_when_stage_all_fails() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(
                new GitCommandResult(128, "not a git repository"),
                new GitCommandResult(0, "committed")
        );
        CommitTool tool = new CommitTool(runner);

        assertThatThrownBy(() -> tool.commitAll(Path.of("/tmp/repo"), "PatchPilot task task-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git add failed: not a git repository");
        assertThat(runner.commitCallOrder()).isZero();
    }

    @Test
    void should_fail_when_commit_fails() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(
                new GitCommandResult(0, "staged"),
                new GitCommandResult(1, "nothing to commit")
        );
        CommitTool tool = new CommitTool(runner);

        assertThatThrownBy(() -> tool.commitAll(Path.of("/tmp/repo"), "PatchPilot task task-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git commit failed: nothing to commit");
    }

    @Test
    void should_append_recovery_guidance_when_stage_fails_with_git_state() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(
                new GitCommandResult(128, "index locked"),
                new GitCommandResult(0, "committed")
        );
        CommitTool tool = new CommitTool(runner, new FixedRecoveryInspector("Git index lock detected (.git/index.lock); manual cleanup required before retry."));

        assertThatThrownBy(() -> tool.commitAll("task-123", Path.of("/tmp/repo"), "PatchPilot task task-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git add failed: index locked\nGit index lock detected (.git/index.lock); manual cleanup required before retry.");
    }

    @Test
    void should_append_recovery_guidance_when_commit_fails_with_git_state() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(
                new GitCommandResult(0, "staged"),
                new GitCommandResult(128, "merge exists")
        );
        CommitTool tool = new CommitTool(runner, new FixedRecoveryInspector("Git merge in progress detected (.git/MERGE_HEAD); manual cleanup required before retry."));

        assertThatThrownBy(() -> tool.commitAll("task-123", Path.of("/tmp/repo"), "PatchPilot task task-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git commit failed: merge exists\nGit merge in progress detected (.git/MERGE_HEAD); manual cleanup required before retry.");
    }

    private static final class RecordingGitCommandRunner extends GitCommandRunner {

        private final GitCommandResult stageResult;
        private final GitCommandResult commitResult;
        private Path stageRepositoryDir;
        private Path commitRepositoryDir;
        private String stageTaskId;
        private String commitTaskId;
        private String commitMessage;
        private int stageCallOrder;
        private int commitCallOrder;
        private int callOrder;

        private RecordingGitCommandRunner(GitCommandResult stageResult, GitCommandResult commitResult) {
            this.stageResult = stageResult;
            this.commitResult = commitResult;
        }

        @Override
        public GitCommandResult stageAll(Path repositoryDir) {
            return stageAll(null, repositoryDir);
        }

        @Override
        public GitCommandResult stageAll(String taskId, Path repositoryDir) {
            this.stageTaskId = taskId;
            this.stageRepositoryDir = repositoryDir;
            this.stageCallOrder = nextCallOrder();
            return stageResult;
        }

        @Override
        public GitCommandResult commit(Path repositoryDir, String message) {
            return commit(null, repositoryDir, message);
        }

        @Override
        public GitCommandResult commit(String taskId, Path repositoryDir, String message) {
            this.commitTaskId = taskId;
            this.commitRepositoryDir = repositoryDir;
            this.commitMessage = message;
            this.commitCallOrder = nextCallOrder();
            return commitResult;
        }

        private int nextCallOrder() {
            callOrder++;
            return callOrder;
        }

        private Path stageRepositoryDir() {
            return stageRepositoryDir;
        }

        private Path commitRepositoryDir() {
            return commitRepositoryDir;
        }

        private String stageTaskId() {
            return stageTaskId;
        }

        private String commitTaskId() {
            return commitTaskId;
        }

        private String commitMessage() {
            return commitMessage;
        }

        private int stageCallOrder() {
            return stageCallOrder;
        }

        private int commitCallOrder() {
            return commitCallOrder;
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
