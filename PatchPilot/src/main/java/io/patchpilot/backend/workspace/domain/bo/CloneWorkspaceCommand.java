package io.patchpilot.backend.workspace.domain.bo;

public record CloneWorkspaceCommand(
        String taskId,
        String repositoryOwner,
        String repositoryName
) {
}
