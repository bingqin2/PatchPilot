package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

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
    void should_fail_when_push_fails() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(new GitCommandResult(128, "permission denied"));
        PushTool tool = new PushTool(runner);

        assertThatThrownBy(() -> tool.pushBranch(Path.of("/tmp/repo"), "patchpilot/task-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git push failed: permission denied");
    }

    private static final class RecordingGitCommandRunner extends GitCommandRunner {

        private final GitCommandResult result;
        private Path repositoryDir;
        private String branchName;

        private RecordingGitCommandRunner(GitCommandResult result) {
            this.result = result;
        }

        @Override
        public GitCommandResult pushBranch(Path repositoryDir, String branchName) {
            this.repositoryDir = repositoryDir;
            this.branchName = branchName;
            return result;
        }

        private Path repositoryDir() {
            return repositoryDir;
        }

        private String branchName() {
            return branchName;
        }
    }
}
