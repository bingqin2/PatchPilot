package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PushTool {

    private final GitCommandRunner gitCommandRunner;

    public PushTool(GitCommandRunner gitCommandRunner) {
        this.gitCommandRunner = gitCommandRunner;
    }

    public String pushBranch(Path repositoryDir, String branchName) {
        GitCommandResult result = gitCommandRunner.pushBranch(repositoryDir, branchName);
        if (result.exitCode() != 0) {
            throw new IllegalStateException("git push failed: " + result.output());
        }
        return result.output();
    }
}
