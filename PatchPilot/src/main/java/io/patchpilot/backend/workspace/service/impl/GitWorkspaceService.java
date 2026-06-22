package io.patchpilot.backend.workspace.service.impl;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.domain.vo.WorkspaceCloneResult;
import io.patchpilot.backend.workspace.recovery.GitWorkspaceRecoveryInspector;
import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitWorkspaceService implements WorkspaceService {

    private static final String REPOSITORY_DIR_NAME = "repo";

    private final WorkspaceProperties workspaceProperties;
    private final GitCommandRunner gitCommandRunner;
    private final GitWorkspaceRecoveryInspector recoveryInspector;

    public GitWorkspaceService(WorkspaceProperties workspaceProperties, GitCommandRunner gitCommandRunner) {
        this(workspaceProperties, gitCommandRunner, new GitWorkspaceRecoveryInspector());
    }

    @Autowired
    public GitWorkspaceService(
            WorkspaceProperties workspaceProperties,
            GitCommandRunner gitCommandRunner,
            GitWorkspaceRecoveryInspector recoveryInspector
    ) {
        this.workspaceProperties = workspaceProperties;
        this.gitCommandRunner = gitCommandRunner;
        this.recoveryInspector = recoveryInspector;
    }

    @Override
    public WorkspaceCloneResult cloneRepository(CloneWorkspaceCommand command) {
        Path rootDir = workspaceProperties.getRootDir().toAbsolutePath().normalize();
        Path workspaceDir = rootDir.resolve(command.taskId()).normalize();
        if (!workspaceDir.startsWith(rootDir)) {
            throw new IllegalArgumentException("Invalid task id: " + command.taskId());
        }
        Path repositoryDir = workspaceDir.resolve(REPOSITORY_DIR_NAME);
        createDirectory(workspaceDir);

        GitCommandResult cloneResult = gitCommandRunner.cloneRepository(command.taskId(), repositoryUrl(command), repositoryDir);
        if (cloneResult.exitCode() != 0) {
            throw new IllegalStateException("git clone failed: " + recoveryInspector.appendGuidance(repositoryDir, cloneResult.output()));
        }

        return new WorkspaceCloneResult(command.taskId(), workspaceDir, repositoryDir);
    }

    @Override
    public PreparedWorkspaceResult prepareRepository(CloneWorkspaceCommand command) {
        WorkspaceCloneResult cloneResult = cloneRepository(command);
        String branchName = branchName(command);
        GitCommandResult branchResult = gitCommandRunner.createBranch(command.taskId(), cloneResult.repositoryDir(), branchName);
        if (branchResult.exitCode() != 0) {
            throw new IllegalStateException("git branch creation failed: " + recoveryInspector.appendGuidance(cloneResult.repositoryDir(), branchResult.output()));
        }
        return new PreparedWorkspaceResult(
                cloneResult.taskId(),
                cloneResult.workspaceDir(),
                cloneResult.repositoryDir(),
                branchName
        );
    }

    @Override
    public PreparedWorkspaceResult resumePreparedRepository(CloneWorkspaceCommand command) {
        Path rootDir = workspaceProperties.getRootDir().toAbsolutePath().normalize();
        Path workspaceDir = rootDir.resolve(command.taskId()).normalize();
        if (!workspaceDir.startsWith(rootDir)) {
            throw new IllegalArgumentException("Invalid task id: " + command.taskId());
        }
        Path repositoryDir = workspaceDir.resolve(REPOSITORY_DIR_NAME);
        if (!Files.isDirectory(repositoryDir)) {
            throw new IllegalStateException(
                    "Approved review workspace is missing: " + repositoryDir
                            + ". Retry the task to regenerate a fresh patch instead."
            );
        }
        return new PreparedWorkspaceResult(command.taskId(), workspaceDir, repositoryDir, branchName(command));
    }

    private static void createDirectory(Path workspaceDir) {
        try {
            Files.createDirectories(workspaceDir);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to create workspace: " + workspaceDir, exception);
        }
    }

    private static String repositoryUrl(CloneWorkspaceCommand command) {
        return "https://github.com/%s/%s.git".formatted(command.repositoryOwner(), command.repositoryName());
    }

    private static String branchName(CloneWorkspaceCommand command) {
        return "patchpilot/" + command.taskId();
    }
}
