package io.patchpilot.backend.agent.provider.domain;

public record ModelProviderRequest(
        String taskId,
        String systemPrompt,
        String userPrompt
) {
}
