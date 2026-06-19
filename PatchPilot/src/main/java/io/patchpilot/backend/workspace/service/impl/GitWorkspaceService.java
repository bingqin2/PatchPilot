package io.patchpilot.backend.workspace.service.impl;

import io.patchpilot.backend.workspace.config.WorkspaceProperties;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.domain.vo.WorkspaceCloneResult;
import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitWorkspaceService implements WorkspaceService {

    private static final String REPOSITORY_DIR_NAME = "repo";

    private final WorkspaceProperties workspaceProperties;
    private final GitCommandRunner gitCommandRunner;

    public GitWorkspaceService(WorkspaceProperties workspaceProperties, GitCommandRunner gitCommandRunner) {
        this.workspaceProperties = workspaceProperties;
        this.gitCommandRunner = gitCommandRunner;
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

        GitCommandResult cloneResult = gitCommandRunner.cloneRepository(repositoryUrl(command), repositoryDir);
        if (cloneResult.exitCode() != 0) {
            throw new IllegalStateException("git clone failed: " + cloneResult.output());
        }

        return new WorkspaceCloneResult(command.taskId(), workspaceDir, repositoryDir);
    }

    @Override
    public PreparedWorkspaceResult prepareRepository(CloneWorkspaceCommand command) {
        WorkspaceCloneResult cloneResult = cloneRepository(command);
        String branchName = branchName(command);
        GitCommandResult branchResult = gitCommandRunner.createBranch(cloneResult.repositoryDir(), branchName);
        if (branchResult.exitCode() != 0) {
            throw new IllegalStateException("git branch creation failed: " + branchResult.output());
        }
        return new PreparedWorkspaceResult(
                cloneResult.taskId(),
                cloneResult.workspaceDir(),
                cloneResult.repositoryDir(),
                branchName
        );
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
