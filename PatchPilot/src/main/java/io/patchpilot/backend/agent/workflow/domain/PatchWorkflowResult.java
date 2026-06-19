package io.patchpilot.backend.agent.workflow.domain;

public record PatchWorkflowResult(
        boolean patchApplied,
        String summary
) {
}
