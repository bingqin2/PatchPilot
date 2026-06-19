package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import org.junit.jupiter.api.Test;

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

    private static final class RecordingGitCommandRunner extends GitCommandRunner {

        private final GitCommandResult stageResult;
        private final GitCommandResult commitResult;
        private Path stageRepositoryDir;
        private Path commitRepositoryDir;
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
            this.stageRepositoryDir = repositoryDir;
            this.stageCallOrder = nextCallOrder();
            return stageResult;
        }

        @Override
        public GitCommandResult commit(Path repositoryDir, String message) {
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
}
