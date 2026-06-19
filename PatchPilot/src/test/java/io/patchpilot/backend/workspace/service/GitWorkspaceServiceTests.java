package io.patchpilot.backend.workspace.service;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.domain.vo.WorkspaceCloneResult;
import io.patchpilot.backend.workspace.recovery.GitWorkspaceRecoveryInspector;
import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.service.impl.GitWorkspaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitWorkspaceServiceTests {

    @TempDir
    private Path rootDir;

    @Test
    void should_create_task_workspace_and_clone_repository() throws Exception {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(0, "cloned");
        WorkspaceService workspaceService = new GitWorkspaceService(properties(), runner);

        WorkspaceCloneResult result = workspaceService.cloneRepository(new CloneWorkspaceCommand(
                "task-123",
                "octocat",
                "hello-world"
        ));

        assertThat(result.taskId()).isEqualTo("task-123");
        assertThat(result.workspaceDir()).isEqualTo(rootDir.resolve("task-123"));
        assertThat(result.repositoryDir()).isEqualTo(rootDir.resolve("task-123").resolve("repo"));
        assertThat(Files.isDirectory(result.workspaceDir())).isTrue();
        assertThat(runner.repositoryUrl()).isEqualTo("https://github.com/octocat/hello-world.git");
        assertThat(runner.targetDir()).isEqualTo(result.repositoryDir());
        assertThat(runner.cloneTaskId()).isEqualTo("task-123");
    }

    @Test
    void should_reject_path_traversal_task_id() {
        WorkspaceService workspaceService = new GitWorkspaceService(
                properties(),
                new RecordingGitCommandRunner(0, "cloned")
        );

        assertThatThrownBy(() -> workspaceService.cloneRepository(new CloneWorkspaceCommand(
                "../outside",
                "octocat",
                "hello-world"
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid task id: ../outside");
    }

    @Test
    void should_fail_when_git_clone_fails() {
        WorkspaceService workspaceService = new GitWorkspaceService(
                properties(),
                new RecordingGitCommandRunner(128, "repository not found")
        );

        assertThatThrownBy(() -> workspaceService.cloneRepository(new CloneWorkspaceCommand(
                "task-fail",
                "octocat",
                "missing"
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("git clone failed")
                .hasMessageContaining("repository not found");
    }

    @Test
    void should_append_recovery_guidance_when_git_clone_fails_with_git_state() {
        WorkspaceService workspaceService = new GitWorkspaceService(
                properties(),
                new RecordingGitCommandRunner(128, "clone interrupted"),
                new FixedRecoveryInspector("Git index lock detected (.git/index.lock); manual cleanup required before retry.")
        );

        assertThatThrownBy(() -> workspaceService.cloneRepository(new CloneWorkspaceCommand(
                "task-clone-recovery",
                "octocat",
                "hello-world"
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git clone failed: clone interrupted\nGit index lock detected (.git/index.lock); manual cleanup required before retry.");
    }

    @Test
    void should_prepare_workspace_by_cloning_and_creating_task_branch() throws Exception {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(0, "ok");
        WorkspaceService workspaceService = new GitWorkspaceService(properties(), runner);

        PreparedWorkspaceResult result = workspaceService.prepareRepository(new CloneWorkspaceCommand(
                "task-branch",
                "octocat",
                "hello-world"
        ));

        assertThat(result.taskId()).isEqualTo("task-branch");
        assertThat(result.workspaceDir()).isEqualTo(rootDir.resolve("task-branch"));
        assertThat(result.repositoryDir()).isEqualTo(rootDir.resolve("task-branch").resolve("repo"));
        assertThat(result.branchName()).isEqualTo("patchpilot/task-branch");
        assertThat(runner.repositoryUrl()).isEqualTo("https://github.com/octocat/hello-world.git");
        assertThat(runner.targetDir()).isEqualTo(result.repositoryDir());
        assertThat(runner.branchRepositoryDir()).isEqualTo(result.repositoryDir());
        assertThat(runner.branchName()).isEqualTo("patchpilot/task-branch");
        assertThat(runner.cloneTaskId()).isEqualTo("task-branch");
        assertThat(runner.branchTaskId()).isEqualTo("task-branch");
    }

    @Test
    void should_fail_when_branch_creation_fails() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(0, "cloned", 128, "branch exists");
        WorkspaceService workspaceService = new GitWorkspaceService(properties(), runner);

        assertThatThrownBy(() -> workspaceService.prepareRepository(new CloneWorkspaceCommand(
                "task-existing-branch",
                "octocat",
                "hello-world"
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("git branch creation failed")
                .hasMessageContaining("branch exists");
    }

    @Test
    void should_append_recovery_guidance_when_branch_creation_fails_with_git_state() {
        RecordingGitCommandRunner runner = new RecordingGitCommandRunner(0, "cloned", 128, "head locked");
        WorkspaceService workspaceService = new GitWorkspaceService(
                properties(),
                runner,
                new FixedRecoveryInspector("Git HEAD lock detected (.git/HEAD.lock); manual cleanup required before retry.")
        );

        assertThatThrownBy(() -> workspaceService.prepareRepository(new CloneWorkspaceCommand(
                "task-branch-recovery",
                "octocat",
                "hello-world"
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("git branch creation failed: head locked\nGit HEAD lock detected (.git/HEAD.lock); manual cleanup required before retry.");
    }

    private WorkspaceProperties properties() {
        WorkspaceProperties properties = new WorkspaceProperties();
        properties.setRootDir(rootDir);
        return properties;
    }

    private static final class RecordingGitCommandRunner extends GitCommandRunner {

        private final int exitCode;
        private final String output;
        private final int branchExitCode;
        private final String branchOutput;
        private String repositoryUrl;
        private Path targetDir;
        private String cloneTaskId;
        private Path branchRepositoryDir;
        private String branchName;
        private String branchTaskId;

        private RecordingGitCommandRunner(int exitCode, String output) {
            this(exitCode, output, 0, "branch created");
        }

        private RecordingGitCommandRunner(int exitCode, String output, int branchExitCode, String branchOutput) {
            this.exitCode = exitCode;
            this.output = output;
            this.branchExitCode = branchExitCode;
            this.branchOutput = branchOutput;
        }

        @Override
        public GitCommandResult cloneRepository(String repositoryUrl, Path targetDir) {
            return cloneRepository(null, repositoryUrl, targetDir);
        }

        @Override
        public GitCommandResult cloneRepository(String taskId, String repositoryUrl, Path targetDir) {
            this.cloneTaskId = taskId;
            this.repositoryUrl = repositoryUrl;
            this.targetDir = targetDir;
            return new GitCommandResult(exitCode, output);
        }

        @Override
        public GitCommandResult createBranch(Path repositoryDir, String branchName) {
            return createBranch(null, repositoryDir, branchName);
        }

        @Override
        public GitCommandResult createBranch(String taskId, Path repositoryDir, String branchName) {
            this.branchTaskId = taskId;
            this.branchRepositoryDir = repositoryDir;
            this.branchName = branchName;
            return new GitCommandResult(branchExitCode, branchOutput);
        }

        private String repositoryUrl() {
            return repositoryUrl;
        }

        private Path targetDir() {
            return targetDir;
        }

        private String cloneTaskId() {
            return cloneTaskId;
        }

        private Path branchRepositoryDir() {
            return branchRepositoryDir;
        }

        private String branchName() {
            return branchName;
        }

        private String branchTaskId() {
            return branchTaskId;
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
