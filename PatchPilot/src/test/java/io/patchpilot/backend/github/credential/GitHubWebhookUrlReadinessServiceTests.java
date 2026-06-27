package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookUrlReadinessVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubWebhookUrlReadinessServiceTests {

    @Test
    void should_report_ready_when_public_base_url_reaches_health_endpoint() {
        GitHubProperties properties = properties(" https://demo.trycloudflare.com/ ");
        GitHubWebhookUrlReadinessService service = new GitHubWebhookUrlReadinessService(
                properties,
                url -> {
                    assertThat(url).isEqualTo("https://demo.trycloudflare.com/health");
                    return new WebhookUrlProbeResult(true, "Backend health endpoint returned 2xx.", 47);
                },
                () -> Instant.parse("2026-06-27T01:00:00Z")
        );

        GitHubWebhookUrlReadinessVo readiness = service.getReadiness();

        assertThat(readiness.publicBaseUrlConfigured()).isTrue();
        assertThat(readiness.status()).isEqualTo("READY");
        assertThat(readiness.publicBaseUrl()).isEqualTo("https://demo.trycloudflare.com");
        assertThat(readiness.payloadUrl()).isEqualTo("https://demo.trycloudflare.com/api/github/webhook");
        assertThat(readiness.healthUrl()).isEqualTo("https://demo.trycloudflare.com/health");
        assertThat(readiness.message()).isEqualTo("Configured public webhook URL reaches PatchPilot health.");
        assertThat(readiness.latencyMs()).isEqualTo(47);
        assertThat(readiness.checkedAt()).isEqualTo(Instant.parse("2026-06-27T01:00:00Z"));
        assertThat(readiness.operatorAction()).isEqualTo("Use the payload URL in the GitHub webhook settings.");
    }

    @Test
    void should_report_attention_without_probe_when_public_base_url_is_missing() {
        GitHubWebhookUrlReadinessService service = new GitHubWebhookUrlReadinessService(
                properties(" "),
                url -> {
                    throw new AssertionError("probe should not be called when URL is missing");
                },
                () -> Instant.parse("2026-06-27T01:00:00Z")
        );

        GitHubWebhookUrlReadinessVo readiness = service.getReadiness();

        assertThat(readiness.publicBaseUrlConfigured()).isFalse();
        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.publicBaseUrl()).isEmpty();
        assertThat(readiness.payloadUrl()).isEmpty();
        assertThat(readiness.healthUrl()).isEmpty();
        assertThat(readiness.message()).isEqualTo("GitHub webhook public base URL is not configured.");
        assertThat(readiness.latencyMs()).isZero();
        assertThat(readiness.operatorAction()).contains("PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL");
    }

    @Test
    void should_report_attention_when_public_base_url_cannot_reach_backend() {
        GitHubWebhookUrlReadinessService service = new GitHubWebhookUrlReadinessService(
                properties("https://stale.trycloudflare.com"),
                url -> new WebhookUrlProbeResult(false, "HTTP 502 from public URL.", 82),
                () -> Instant.parse("2026-06-27T01:00:00Z")
        );

        GitHubWebhookUrlReadinessVo readiness = service.getReadiness();

        assertThat(readiness.publicBaseUrlConfigured()).isTrue();
        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.payloadUrl()).isEqualTo("https://stale.trycloudflare.com/api/github/webhook");
        assertThat(readiness.healthUrl()).isEqualTo("https://stale.trycloudflare.com/health");
        assertThat(readiness.message()).isEqualTo("HTTP 502 from public URL.");
        assertThat(readiness.latencyMs()).isEqualTo(82);
        assertThat(readiness.operatorAction()).contains("Restart cloudflared");
    }

    private static GitHubProperties properties(String publicBaseUrl) {
        GitHubProperties properties = new GitHubProperties();
        properties.setWebhookPublicBaseUrl(publicBaseUrl);
        return properties;
    }
}
