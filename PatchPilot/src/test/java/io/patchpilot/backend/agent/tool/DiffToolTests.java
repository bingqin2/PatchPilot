package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiffToolTests {

    @Test
    void should_return_git_diff_output() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(0, "diff --git a/README.md b/README.md\n+changed\n");
        DiffTool tool = new DiffTool(runner);

        String diff = tool.diff(Path.of("/tmp/repo"));

        assertThat(diff).contains("+changed");
        assertThat(runner.repositoryDir()).isEqualTo(Path.of("/tmp/repo"));
    }

    @Test
    void should_fail_when_git_diff_fails() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(128, "not a git repository");
        DiffTool tool = new DiffTool(runner);

        assertThatThrownBy(() -> tool.diff(Path.of("/tmp/repo")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git diff failed: not a git repository");
    }

    private static final class RecordingGitCommandRunner extends GitCommandRunner {

        private final int exitCode;
        private final String output;
        private Path repositoryDir;

        private RecordingGitCommandRunner(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

        @Override
        public GitCommandResult diff(Path repositoryDir) {
            this.repositoryDir = repositoryDir;
            return new GitCommandResult(exitCode, output);
        }

        private Path repositoryDir() {
            return repositoryDir;
        }
    }
}
