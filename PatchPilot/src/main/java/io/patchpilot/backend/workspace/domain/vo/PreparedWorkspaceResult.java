package io.patchpilot.backend.workspace.domain.vo;

import java.nio.file.Path;

public record PreparedWorkspaceResult(
        String taskId,
        Path workspaceDir,
        Path repositoryDir,
        String branchName
) {
}
