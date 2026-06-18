package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.MavenTestRunner;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;

@Service
public class NoopFixTaskExecutor implements FixTaskExecutor {

    private final WorkspaceService workspaceService;
    private final MavenTestRunner mavenTestRunner;

    public NoopFixTaskExecutor(WorkspaceService workspaceService, MavenTestRunner mavenTestRunner) {
        this.workspaceService = workspaceService;
        this.mavenTestRunner = mavenTestRunner;
    }

    @Override
    public void execute(FixTaskVo task) {
        PreparedWorkspaceResult preparedWorkspace = workspaceService.prepareRepository(new CloneWorkspaceCommand(
                task.id(),
                task.repositoryOwner(),
                task.repositoryName()
        ));
        TestRunResult testRunResult = mavenTestRunner.runTests(preparedWorkspace.repositoryDir());
        if (testRunResult.exitCode() != 0) {
            throw new IllegalStateException("maven tests failed: " + testRunResult.output());
        }
    }
}
