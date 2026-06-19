package io.patchpilot.backend.agent.provider.domain;

public record ModelProviderResponse(
        String content,
        int promptTokens,
        int completionTokens,
        int totalTokens
) {
}
