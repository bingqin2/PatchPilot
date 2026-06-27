package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookUrlReadinessVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubWebhookSetupReadinessServiceTests {

    @Test
    void should_report_ready_when_secret_url_and_latest_delivery_are_healthy() {
        GitHubWebhookSetupReadinessService service = new GitHubWebhookSetupReadinessService(
                () -> configuration(true),
                () -> webhookUrl("READY"),
                () -> List.of(delivery(WebhookDeliveryDiagnosticStatus.TASK_CREATED, false)),
                () -> Instant.parse("2026-06-27T02:00:00Z")
        );

        GitHubWebhookSetupReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo("READY");
        assertThat(readiness.secretConfigured()).isTrue();
        assertThat(readiness.publicUrlReady()).isTrue();
        assertThat(readiness.latestDeliveryStatus()).isEqualTo("TASK_CREATED");
        assertThat(readiness.redeliveryRecommended()).isFalse();
        assertThat(readiness.payloadUrl()).isEqualTo("https://demo.trycloudflare.com/api/github/webhook");
        assertThat(readiness.nextActions()).containsExactly("Use the payload URL in GitHub Webhooks and continue the live demo.");
        assertThat(readiness.markdownReport())
                .contains("# PatchPilot Webhook Setup Readiness")
                .contains("- Status: `READY`")
                .contains("- Payload URL: `https://demo.trycloudflare.com/api/github/webhook`")
                .doesNotContain("secret-value");
    }

    @Test
    void should_block_when_required_webhook_setup_is_missing() {
        GitHubWebhookSetupReadinessService service = new GitHubWebhookSetupReadinessService(
                () -> configuration(false),
                () -> webhookUrl("NEEDS_ATTENTION"),
                List::of,
                () -> Instant.parse("2026-06-27T02:05:00Z")
        );

        GitHubWebhookSetupReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo("BLOCKED");
        assertThat(readiness.secretConfigured()).isFalse();
        assertThat(readiness.publicUrlReady()).isFalse();
        assertThat(readiness.latestDeliveryStatus()).isNull();
        assertThat(readiness.nextActions())
                .containsExactly(
                        "Set PATCHPILOT_GITHUB_WEBHOOK_SECRET to the same value configured in GitHub Webhooks.",
                        "Set PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL and verify /api/github/webhook-url-readiness is READY."
                );
    }

    @Test
    void should_need_attention_when_latest_delivery_recommends_redelivery() {
        GitHubWebhookSetupReadinessService service = new GitHubWebhookSetupReadinessService(
                () -> configuration(true),
                () -> webhookUrl("READY"),
                () -> List.of(delivery(WebhookDeliveryDiagnosticStatus.INVALID_SIGNATURE, true)),
                () -> Instant.parse("2026-06-27T02:10:00Z")
        );

        GitHubWebhookSetupReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.latestDeliveryStatus()).isEqualTo("INVALID_SIGNATURE");
        assertThat(readiness.redeliveryRecommended()).isTrue();
        assertThat(readiness.nextActions()).containsExactly(
                "Fix the latest webhook delivery issue, then use GitHub's Redeliver action for that delivery."
        );
        assertThat(readiness.markdownReport()).contains("- Latest delivery: `INVALID_SIGNATURE`");
    }

    private static ConfigurationSummaryVo configuration(boolean webhookSecretConfigured) {
        return new ConfigurationSummaryVo(
                "openai-compatible",
                "gpt-5.5",
                "https://api.example.test/v1",
                true,
                true,
                webhookSecretConfigured,
                true,
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                true,
                false,
                "/tmp/patchpilot/workspaces",
                3,
                30000,
                300000,
                10000,
                true,
                true,
                true,
                600000,
                30,
                60,
                20,
                true,
                900000,
                4,
                1800000,
                true,
                true,
                true,
                true,
                15,
                List.of("alice"),
                List.of("octocat/hello-world"),
                List.of("operator"),
                List.of("/tmp/patchpilot/workspaces")
        );
    }

    private static GitHubWebhookUrlReadinessVo webhookUrl(String status) {
        return new GitHubWebhookUrlReadinessVo(
                true,
                status,
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "https://demo.trycloudflare.com/health",
                "Configured public webhook URL reaches PatchPilot health.",
                44,
                Instant.parse("2026-06-27T01:00:00Z"),
                "Use the payload URL in the GitHub webhook settings."
        );
    }

    private static WebhookDeliveryDiagnosticVo delivery(
            WebhookDeliveryDiagnosticStatus status,
            boolean redeliveryRecommended
    ) {
        return new WebhookDeliveryDiagnosticVo(
                "diagnostic-1",
                "delivery-1",
                "issue_comment",
                status,
                status == WebhookDeliveryDiagnosticStatus.TASK_CREATED ? "task-1" : null,
                "octocat",
                "hello-world",
                42L,
                "alice",
                "/agent fix touch docs/demo.md",
                "Recorded delivery",
                redeliveryRecommended,
                "Operator action",
                Instant.parse("2026-06-27T01:30:00Z")
        );
    }
}
