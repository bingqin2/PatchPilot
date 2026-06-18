package io.patchpilot.backend.workspace.service;

import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.domain.vo.WorkspaceCloneResult;

public interface WorkspaceService {

    WorkspaceCloneResult cloneRepository(CloneWorkspaceCommand command);

    PreparedWorkspaceResult prepareRepository(CloneWorkspaceCommand command);
}
