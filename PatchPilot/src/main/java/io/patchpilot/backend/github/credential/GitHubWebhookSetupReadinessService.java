package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.configuration.ConfigurationSummaryService;
import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookSetupReadinessVo;
import io.patchpilot.backend.github.credential.domain.GitHubWebhookUrlReadinessVo;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class GitHubWebhookSetupReadinessService {

    public static final String READY = "READY";
    public static final String NEEDS_ATTENTION = "NEEDS_ATTENTION";
    public static final String BLOCKED = "BLOCKED";

    private final Supplier<GitHubWebhookSetupReadinessVo> readinessSupplier;

    @Autowired
    public GitHubWebhookSetupReadinessService(
            ConfigurationSummaryService configurationSummaryService,
            GitHubWebhookUrlReadinessService webhookUrlReadinessService,
            WebhookDeliveryDiagnosticService webhookDeliveryDiagnosticService
    ) {
        this(
                configurationSummaryService::getConfigurationSummary,
                webhookUrlReadinessService::getReadiness,
                () -> webhookDeliveryDiagnosticService.listRecent(1),
                Instant::now
        );
    }

    GitHubWebhookSetupReadinessService(
            Supplier<ConfigurationSummaryVo> configurationSupplier,
            Supplier<GitHubWebhookUrlReadinessVo> webhookUrlReadinessSupplier,
            Supplier<List<WebhookDeliveryDiagnosticVo>> deliveriesSupplier,
            Supplier<Instant> clock
    ) {
        this(() -> buildReadiness(configurationSupplier, webhookUrlReadinessSupplier, deliveriesSupplier, clock));
    }

    public GitHubWebhookSetupReadinessService(Supplier<GitHubWebhookSetupReadinessVo> readinessSupplier) {
        this.readinessSupplier = readinessSupplier;
    }

    public GitHubWebhookSetupReadinessVo getReadiness() {
        return readinessSupplier.get();
    }

    private static GitHubWebhookSetupReadinessVo buildReadiness(
            Supplier<ConfigurationSummaryVo> configurationSupplier,
            Supplier<GitHubWebhookUrlReadinessVo> webhookUrlReadinessSupplier,
            Supplier<List<WebhookDeliveryDiagnosticVo>> deliveriesSupplier,
            Supplier<Instant> clock
    ) {
        ConfigurationSummaryVo configuration = configurationSupplier.get();
        GitHubWebhookUrlReadinessVo webhookUrlReadiness = webhookUrlReadinessSupplier.get();
        WebhookDeliveryDiagnosticVo latestDelivery = latestDelivery(deliveriesSupplier.get());
        Instant checkedAt = clock.get();

        boolean secretConfigured = configuration.githubWebhookSecretConfigured();
        boolean publicUrlReady = webhookUrlReadiness != null && READY.equals(webhookUrlReadiness.status());
        boolean redeliveryRecommended = latestDelivery != null && latestDelivery.redeliveryRecommended();
        String status = status(secretConfigured, publicUrlReady, redeliveryRecommended);
        List<String> nextActions = nextActions(secretConfigured, publicUrlReady, redeliveryRecommended);
        String summary = summary(status);
        String publicBaseUrl = webhookUrlReadiness == null ? "" : valueOrEmpty(webhookUrlReadiness.publicBaseUrl());
        String payloadUrl = webhookUrlReadiness == null ? "" : valueOrEmpty(webhookUrlReadiness.payloadUrl());
        String healthUrl = webhookUrlReadiness == null ? "" : valueOrEmpty(webhookUrlReadiness.healthUrl());
        String latestDeliveryStatus = latestDelivery == null ? null : latestDelivery.status().name();
        String latestDeliveryId = latestDelivery == null ? null : latestDelivery.deliveryId();

        GitHubWebhookSetupReadinessVo readiness = new GitHubWebhookSetupReadinessVo(
                status,
                secretConfigured,
                publicUrlReady,
                publicBaseUrl,
                payloadUrl,
                healthUrl,
                latestDeliveryStatus,
                latestDeliveryId,
                redeliveryRecommended,
                summary,
                nextActions,
                checkedAt,
                ""
        );
        return new GitHubWebhookSetupReadinessVo(
                readiness.status(),
                readiness.secretConfigured(),
                readiness.publicUrlReady(),
                readiness.publicBaseUrl(),
                readiness.payloadUrl(),
                readiness.healthUrl(),
                readiness.latestDeliveryStatus(),
                readiness.latestDeliveryId(),
                readiness.redeliveryRecommended(),
                readiness.summary(),
                readiness.nextActions(),
                readiness.checkedAt(),
                markdownReport(readiness)
        );
    }

    private static WebhookDeliveryDiagnosticVo latestDelivery(List<WebhookDeliveryDiagnosticVo> deliveries) {
        if (deliveries == null || deliveries.isEmpty()) {
            return null;
        }
        return deliveries.get(0);
    }

    private static String status(boolean secretConfigured, boolean publicUrlReady, boolean redeliveryRecommended) {
        if (!secretConfigured || !publicUrlReady) {
            return BLOCKED;
        }
        if (redeliveryRecommended) {
            return NEEDS_ATTENTION;
        }
        return READY;
    }

    private static String summary(String status) {
        return switch (status) {
            case READY -> "Webhook setup is ready for GitHub deliveries.";
            case NEEDS_ATTENTION -> "Webhook setup needs attention before redelivery.";
            case BLOCKED -> "Webhook setup is blocked until required configuration is fixed.";
            default -> "Webhook setup readiness is unknown.";
        };
    }

    private static List<String> nextActions(
            boolean secretConfigured,
            boolean publicUrlReady,
            boolean redeliveryRecommended
    ) {
        List<String> actions = new ArrayList<>();
        if (!secretConfigured) {
            actions.add("Set PATCHPILOT_GITHUB_WEBHOOK_SECRET to the same value configured in GitHub Webhooks.");
        }
        if (!publicUrlReady) {
            actions.add("Set PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL and verify /api/github/webhook-url-readiness is READY.");
        }
        if (actions.isEmpty() && redeliveryRecommended) {
            actions.add("Fix the latest webhook delivery issue, then use GitHub's Redeliver action for that delivery.");
        }
        if (actions.isEmpty()) {
            actions.add("Use the payload URL in GitHub Webhooks and continue the live demo.");
        }
        return actions;
    }

    private static String markdownReport(GitHubWebhookSetupReadinessVo readiness) {
        List<String> lines = new ArrayList<>();
        lines.add("# PatchPilot Webhook Setup Readiness");
        lines.add("");
        lines.add("- Status: `" + readiness.status() + "`");
        lines.add("- Secret configured: `" + readiness.secretConfigured() + "`");
        lines.add("- Public URL ready: `" + readiness.publicUrlReady() + "`");
        if (StringUtils.hasText(readiness.payloadUrl())) {
            lines.add("- Payload URL: `" + readiness.payloadUrl() + "`");
        }
        if (StringUtils.hasText(readiness.healthUrl())) {
            lines.add("- Health URL: `" + readiness.healthUrl() + "`");
        }
        if (StringUtils.hasText(readiness.latestDeliveryStatus())) {
            lines.add("- Latest delivery: `" + readiness.latestDeliveryStatus() + "`");
        } else {
            lines.add("- Latest delivery: `none`");
        }
        lines.add("");
        lines.add("## Next Actions");
        readiness.nextActions().forEach(action -> lines.add("- " + action));
        return String.join("\n", lines);
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
