package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DiffTool {

    private final GitCommandRunner gitCommandRunner;

    public DiffTool(GitCommandRunner gitCommandRunner) {
        this.gitCommandRunner = gitCommandRunner;
    }

    public String diff(Path repositoryDir) {
        GitCommandResult result = gitCommandRunner.diff(repositoryDir);
        if (result.exitCode() != 0) {
            throw new IllegalStateException("git diff failed: " + result.output());
        }
        return result.output();
    }
}
