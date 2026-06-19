package io.patchpilot.backend.workspace.runner;

import io.patchpilot.backend.github.config.GitHubProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitCommandRunnerTests {

    @TempDir
    private Path tempDir;

    @Test
    void should_not_rewrite_output_when_token_is_blank() throws Exception {
        GitHubProperties properties = new GitHubProperties();
        properties.setToken("");
        GitCommandRunner runner = new GitCommandRunner(properties);

        String sanitized = sanitize(runner, "fatal: could not resolve host: github.com");

        assertThat(sanitized).isEqualTo("fatal: could not resolve host: github.com");
    }

    @Test
    void should_redact_configured_token_from_output() throws Exception {
        GitHubProperties properties = new GitHubProperties();
        properties.setToken("secret-token");
        GitCommandRunner runner = new GitCommandRunner(properties);

        String sanitized = sanitize(runner, "https://x-access-token:secret-token@github.com/octocat/repo.git");

        assertThat(sanitized).isEqualTo("https://x-access-token:***@github.com/octocat/repo.git");
    }

    @Test
    void should_create_branch_in_repository() throws Exception {
        Path repositoryDir = tempDir.resolve("repo");
        createGitRepository(repositoryDir);
        GitCommandRunner runner = new GitCommandRunner(new GitHubProperties());

        GitCommandResult result = runner.createBranch(repositoryDir, "patchpilot/task-123");

        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(currentBranch(repositoryDir)).isEqualTo("patchpilot/task-123");
    }

    @Test
    void should_reject_blank_branch_name() {
        GitCommandRunner runner = new GitCommandRunner(new GitHubProperties());

        assertThatThrownBy(() -> runner.createBranch(tempDir, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Branch name must not be blank");
    }

    @Test
    void should_return_working_tree_diff() throws Exception {
        Path repositoryDir = tempDir.resolve("diff-repo");
        createGitRepository(repositoryDir);
        Files.writeString(repositoryDir.resolve("README.md"), "hello\nchanged\n");
        GitCommandRunner runner = new GitCommandRunner(new GitHubProperties());

        GitCommandResult result = runner.diff(repositoryDir);

        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.output()).contains("+changed");
    }

    @Test
    void should_stage_all_repository_changes() throws Exception {
        Path repositoryDir = tempDir.resolve("stage-repo");
        createGitRepository(repositoryDir);
        Files.writeString(repositoryDir.resolve("README.md"), "hello\nchanged\n");
        GitCommandRunner runner = new GitCommandRunner(new GitHubProperties());

        GitCommandResult result = runner.stageAll(repositoryDir);

        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(runGit(repositoryDir, "diff", "--cached", "--")).contains("+changed");
    }

    @Test
    void should_commit_staged_repository_changes() throws Exception {
        Path repositoryDir = tempDir.resolve("commit-repo");
        createGitRepository(repositoryDir);
        Files.writeString(repositoryDir.resolve("README.md"), "hello\nchanged\n");
        runGit(repositoryDir, "add", "--all");
        GitCommandRunner runner = new GitCommandRunner(new GitHubProperties());

        GitCommandResult result = runner.commit(repositoryDir, "PatchPilot task task-123");

        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(lastCommitMessage(repositoryDir)).isEqualTo("PatchPilot task task-123");
    }

    @Test
    void should_reject_blank_commit_message() {
        GitCommandRunner runner = new GitCommandRunner(new GitHubProperties());

        assertThatThrownBy(() -> runner.commit(tempDir, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Commit message must not be blank");
    }

    @Test
    void should_push_current_head_to_origin_branch() throws Exception {
        Path originDir = tempDir.resolve("origin.git");
        runGit(tempDir, "init", "--bare", originDir.toString());
        Path repositoryDir = tempDir.resolve("push-repo");
        createGitRepository(repositoryDir);
        runGit(repositoryDir, "remote", "add", "origin", originDir.toString());
        runGit(repositoryDir, "checkout", "-b", "patchpilot/task-123");
        Files.writeString(repositoryDir.resolve("README.md"), "hello\nchanged\n");
        runGit(repositoryDir, "add", "--all");
        runGit(repositoryDir, "commit", "-m", "PatchPilot task task-123");
        GitCommandRunner runner = new GitCommandRunner(new GitHubProperties());

        GitCommandResult result = runner.pushBranch(repositoryDir, "patchpilot/task-123");

        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(runGit(originDir, "rev-parse", "--verify", "refs/heads/patchpilot/task-123")).isNotBlank();
    }

    @Test
    void should_reject_blank_push_branch_name() {
        GitCommandRunner runner = new GitCommandRunner(new GitHubProperties());

        assertThatThrownBy(() -> runner.pushBranch(tempDir, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Branch name must not be blank");
    }

    private static String sanitize(GitCommandRunner runner, String value) throws Exception {
        Method sanitize = GitCommandRunner.class.getDeclaredMethod("sanitize", String.class);
        sanitize.setAccessible(true);
        return (String) sanitize.invoke(runner, value);
    }

    private static void createGitRepository(Path repositoryDir) throws Exception {
        Files.createDirectories(repositoryDir);
        runGit(repositoryDir, "init");
        runGit(repositoryDir, "config", "user.email", "patchpilot@example.com");
        runGit(repositoryDir, "config", "user.name", "PatchPilot Test");
        Files.writeString(repositoryDir.resolve("README.md"), "hello\n");
        runGit(repositoryDir, "add", "README.md");
        runGit(repositoryDir, "commit", "-m", "Initial commit");
    }

    private static String currentBranch(Path repositoryDir) throws Exception {
        return runGit(repositoryDir, "branch", "--show-current").trim();
    }

    private static String lastCommitMessage(Path repositoryDir) throws Exception {
        return runGit(repositoryDir, "log", "-1", "--pretty=%s").trim();
    }

    private static String runGit(Path repositoryDir, String... args) throws Exception {
        String[] command = new String[args.length + 3];
        command[0] = "git";
        command[1] = "-C";
        command[2] = repositoryDir.toString();
        System.arraycopy(args, 0, command, 3, args.length);
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        String output = new String(process.getInputStream().readAllBytes());
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("git command timed out");
        }
        if (process.exitValue() != 0) {
            throw new IllegalStateException(output);
        }
        return output;
    }
}
