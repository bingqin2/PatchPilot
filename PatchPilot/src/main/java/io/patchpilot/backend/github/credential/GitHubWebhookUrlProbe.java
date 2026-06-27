package io.patchpilot.backend.github.credential;

@FunctionalInterface
public interface GitHubWebhookUrlProbe {

    WebhookUrlProbeResult check(String healthUrl);
}
