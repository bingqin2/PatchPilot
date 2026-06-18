package io.patchpilot.backend.workspace.domain.vo;

import java.nio.file.Path;

public record WorkspaceCloneResult(
        String taskId,
        Path workspaceDir,
        Path repositoryDir
) {
}
