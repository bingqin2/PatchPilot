package io.patchpilot.backend.agent.workflow.domain;

import java.util.List;

public record FileEditPlan(
        List<ProposedFileEdit> edits
) {

    public static FileEditPlan empty() {
        return new FileEditPlan(List.of());
    }

    public FileEditPlan {
        edits = edits == null ? List.of() : List.copyOf(edits);
    }
}
