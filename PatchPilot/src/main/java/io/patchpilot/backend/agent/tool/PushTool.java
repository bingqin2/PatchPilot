package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.recovery.GitWorkspaceRecoveryInspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PushTool {

    private final GitCommandRunner gitCommandRunner;
    private final GitWorkspaceRecoveryInspector recoveryInspector;

    public PushTool(GitCommandRunner gitCommandRunner) {
        this(gitCommandRunner, new GitWorkspaceRecoveryInspector());
    }

    @Autowired
    public PushTool(GitCommandRunner gitCommandRunner, GitWorkspaceRecoveryInspector recoveryInspector) {
        this.gitCommandRunner = gitCommandRunner;
        this.recoveryInspector = recoveryInspector;
    }

    public String pushBranch(Path repositoryDir, String branchName) {
        return pushBranch(null, repositoryDir, branchName);
    }

    public String pushBranch(String taskId, Path repositoryDir, String branchName) {
        GitCommandResult result = gitCommandRunner.pushBranch(taskId, repositoryDir, branchName);
        if (result.exitCode() != 0) {
            throw new IllegalStateException("git push failed: " + recoveryInspector.appendGuidance(repositoryDir, result.output()));
        }
        return result.output();
    }
}
