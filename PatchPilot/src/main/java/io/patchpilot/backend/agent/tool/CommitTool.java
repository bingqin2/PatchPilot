package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class CommitTool {

    private final GitCommandRunner gitCommandRunner;

    public CommitTool(GitCommandRunner gitCommandRunner) {
        this.gitCommandRunner = gitCommandRunner;
    }

    public String commitAll(Path repositoryDir, String message) {
        GitCommandResult stageResult = gitCommandRunner.stageAll(repositoryDir);
        if (stageResult.exitCode() != 0) {
            throw new IllegalStateException("git add failed: " + stageResult.output());
        }

        GitCommandResult commitResult = gitCommandRunner.commit(repositoryDir, message);
        if (commitResult.exitCode() != 0) {
            throw new IllegalStateException("git commit failed: " + commitResult.output());
        }
        return commitResult.output();
    }
}
