package io.patchpilot.backend.agent.tool;

import io.patchpilot.backend.workspace.runner.GitCommandResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class DiffTool {

    private final GitCommandRunner gitCommandRunner;

    public String diff(Path repositoryDir) {
        GitCommandResult result = gitCommandRunner.diff(repositoryDir);
        if (result.exitCode() != 0) {
            throw new IllegalStateException("git diff failed: " + result.output());
        }
        return result.output();
    }
}
