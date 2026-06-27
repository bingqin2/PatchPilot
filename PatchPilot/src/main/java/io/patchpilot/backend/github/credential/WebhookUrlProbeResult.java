package io.patchpilot.backend.github.credential;

public record WebhookUrlProbeResult(
        boolean reachable,
        String message,
        long latencyMs
) {
}
