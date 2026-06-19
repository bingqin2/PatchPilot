package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.recovery.GitWorkspaceRecoveryInspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class CommitTool {

    private final GitCommandRunner gitCommandRunner;
    private final GitWorkspaceRecoveryInspector recoveryInspector;

    public CommitTool(GitCommandRunner gitCommandRunner) {
        this(gitCommandRunner, new GitWorkspaceRecoveryInspector());
    }

    @Autowired
    public CommitTool(GitCommandRunner gitCommandRunner, GitWorkspaceRecoveryInspector recoveryInspector) {
        this.gitCommandRunner = gitCommandRunner;
        this.recoveryInspector = recoveryInspector;
    }

    public String commitAll(Path repositoryDir, String message) {
        return commitAll(null, repositoryDir, message);
    }

    public String commitAll(String taskId, Path repositoryDir, String message) {
        GitCommandResult stageResult = gitCommandRunner.stageAll(taskId, repositoryDir);
        if (stageResult.exitCode() != 0) {
            throw new IllegalStateException("git add failed: " + recoveryInspector.appendGuidance(repositoryDir, stageResult.output()));
        }

        GitCommandResult commitResult = gitCommandRunner.commit(taskId, repositoryDir, message);
        if (commitResult.exitCode() != 0) {
            throw new IllegalStateException("git commit failed: " + recoveryInspector.appendGuidance(repositoryDir, commitResult.output()));
        }
        return commitResult.output();
    }
}
