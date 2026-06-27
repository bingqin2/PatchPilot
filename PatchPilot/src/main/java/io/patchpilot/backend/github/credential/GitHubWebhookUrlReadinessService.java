package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookUrlReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.function.Supplier;

@Service
public class GitHubWebhookUrlReadinessService {

    public static final String READY = "READY";
    public static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";

    private static final String WEBHOOK_PATH = "/api/github/webhook";
    private static final String HEALTH_PATH = "/health";

    private final Supplier<GitHubWebhookUrlReadinessVo> readinessSupplier;

    @Autowired
    public GitHubWebhookUrlReadinessService(GitHubProperties gitHubProperties, GitHubWebhookUrlProbe probe) {
        this(gitHubProperties, probe, Instant::now);
    }

    GitHubWebhookUrlReadinessService(
            GitHubProperties gitHubProperties,
            GitHubWebhookUrlProbe probe,
            Supplier<Instant> clock
    ) {
        this(() -> buildReadiness(gitHubProperties, probe, clock));
    }

    public GitHubWebhookUrlReadinessService(Supplier<GitHubWebhookUrlReadinessVo> readinessSupplier) {
        this.readinessSupplier = readinessSupplier;
    }

    public GitHubWebhookUrlReadinessVo getReadiness() {
        return readinessSupplier.get();
    }

    private static GitHubWebhookUrlReadinessVo buildReadiness(
            GitHubProperties gitHubProperties,
            GitHubWebhookUrlProbe probe,
            Supplier<Instant> clock
    ) {
        Instant checkedAt = clock.get();
        String publicBaseUrl = normalizeBaseUrl(gitHubProperties.getWebhookPublicBaseUrl());
        if (!StringUtils.hasText(publicBaseUrl)) {
            return attention(
                    false,
                    "",
                    "",
                    "",
                    "GitHub webhook public base URL is not configured.",
                    0,
                    checkedAt,
                    "Set PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL to the current cloudflared or hosted HTTPS base URL, then configure GitHub Payload URL as <base>/api/github/webhook."
            );
        }

        String payloadUrl = publicBaseUrl + WEBHOOK_PATH;
        String healthUrl = publicBaseUrl + HEALTH_PATH;
        WebhookUrlProbeResult result = probe.check(healthUrl);
        if (result.reachable()) {
            return new GitHubWebhookUrlReadinessVo(
                    true,
                    READY,
                    publicBaseUrl,
                    payloadUrl,
                    healthUrl,
                    "Configured public webhook URL reaches PatchPilot health.",
                    result.latencyMs(),
                    checkedAt,
                    "Use the payload URL in the GitHub webhook settings."
            );
        }
        return attention(
                true,
                publicBaseUrl,
                payloadUrl,
                healthUrl,
                result.message(),
                result.latencyMs(),
                checkedAt,
                "Restart cloudflared, update PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL, and set the GitHub webhook Payload URL again."
        );
    }

    private static GitHubWebhookUrlReadinessVo attention(
            boolean publicBaseUrlConfigured,
            String publicBaseUrl,
            String payloadUrl,
            String healthUrl,
            String message,
            long latencyMs,
            Instant checkedAt,
            String operatorAction
    ) {
        return new GitHubWebhookUrlReadinessVo(
                publicBaseUrlConfigured,
                NEEDS_ATTENTION,
                publicBaseUrl,
                payloadUrl,
                healthUrl,
                message,
                latencyMs,
                checkedAt,
                operatorAction
        );
    }

    private static String normalizeBaseUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
