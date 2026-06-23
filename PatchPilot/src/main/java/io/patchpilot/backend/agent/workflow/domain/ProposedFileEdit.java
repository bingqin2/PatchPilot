package io.patchpilot.backend.agent.workflow.domain;

public record ProposedFileEdit(
        String path,
        String content,
        String rationale
) {
}
