package io.patchpilot.backend.agent.workflow.domain;

public record FileEditContext(
        String path,
        String content
) {
}
