package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.configuration.ConfigurationSummaryService;
import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessCheckVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ExternalExposureReadinessService implements ExternalExposureReadinessProvider {

    private static final String STATUS_READY = "READY";
    private static final String STATUS_NEEDS_ATTENTION = "NEEDS_ATTENTION";
    private static final String STATUS_BLOCKED = "BLOCKED";
    private static final String SIDE_EFFECT_CONTRACT = """
            Read-only external exposure readiness: this endpoint does not create tasks, does not call the model, \
            does not run tests, does not probe the network, does not mutate Git, does not create branches, \
            does not open Pull Requests, does not write GitHub comments, does not archive records, \
            does not mutate GitHub, and does not expose secrets.\
            """;

    private final Supplier<ConfigurationSummaryVo> configurationSupplier;
    private final AdminApiSecurityProperties securityProperties;
    private final Supplier<Instant> nowSupplier;

    @Autowired
    public ExternalExposureReadinessService(
            ConfigurationSummaryService configurationSummaryService,
            AdminApiSecurityProperties securityProperties
    ) {
        this(configurationSummaryService::getConfigurationSummary, securityProperties, Instant::now);
    }

    ExternalExposureReadinessService(
            Supplier<ConfigurationSummaryVo> configurationSupplier,
            AdminApiSecurityProperties securityProperties,
            Supplier<Instant> nowSupplier
    ) {
        this.configurationSupplier = configurationSupplier;
        this.securityProperties = securityProperties;
        this.nowSupplier = nowSupplier;
    }

    @Override
    public ExternalExposureReadinessVo getReadiness() {
        ConfigurationSummaryVo configuration = configurationSupplier.get();
        List<ExternalExposureReadinessCheckVo> checks = List.of(
                adminTokenCheck(configuration),
                dashboardBootstrapCheck(),
                webhookSecretCheck(configuration),
                publicWebhookUrlCheck(configuration),
                triggerUserAllowlistCheck(configuration),
                repositoryAllowlistCheck(configuration),
                triggerRateLimitCheck(configuration),
                rejectedTriggerQuarantineCheck(configuration),
                reviewApprovalAllowlistCheck(configuration),
                generatedDiffRiskGateCheck(configuration)
        );

        int readyCount = count(checks, STATUS_READY);
        int needsAttentionCount = count(checks, STATUS_NEEDS_ATTENTION);
        int blockedCount = count(checks, STATUS_BLOCKED);
        String status = aggregateStatus(blockedCount, needsAttentionCount);
        List<String> nextActions = nextActions(checks, status);
        String summary = summary(status);
        Instant generatedAt = nowSupplier.get();
        return new ExternalExposureReadinessVo(
                status,
                STATUS_READY.equals(status),
                readyCount,
                needsAttentionCount,
                blockedCount,
                checks.size(),
                summary,
                nextActions,
                SIDE_EFFECT_CONTRACT,
                checks,
                generatedAt,
                markdownReport(status, summary, checks, nextActions, generatedAt)
        );
    }

    private static ExternalExposureReadinessCheckVo adminTokenCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.adminTokenConfigured()) {
            return blocked(
                    "Admin API token",
                    "Admin API token is missing.",
                    "Configure PATCHPILOT_ADMIN_TOKEN before exposing PatchPilot outside localhost."
            );
        }
        return ready("Admin API token", "Admin API token is configured.");
    }

    private ExternalExposureReadinessCheckVo dashboardBootstrapCheck() {
        if (securityProperties.isDashboardAdminTokenBootstrapEnabled()) {
            return blocked(
                    "Dashboard token bootstrap",
                    "Dashboard admin token bootstrap is enabled.",
                    "Set PATCHPILOT_DASHBOARD_ADMIN_TOKEN_BOOTSTRAP_ENABLED=false before using a public tunnel."
            );
        }
        return ready("Dashboard token bootstrap", "Dashboard admin token bootstrap is disabled.");
    }

    private static ExternalExposureReadinessCheckVo webhookSecretCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.githubWebhookSecretConfigured()) {
            return blocked(
                    "Webhook secret",
                    "GitHub webhook secret is missing.",
                    "Configure PATCHPILOT_GITHUB_WEBHOOK_SECRET before accepting GitHub deliveries."
            );
        }
        return ready("Webhook secret", "GitHub webhook secret is configured.");
    }

    private static ExternalExposureReadinessCheckVo publicWebhookUrlCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.githubWebhookPublicBaseUrlConfigured()) {
            return blocked(
                    "Public webhook URL",
                    "GitHub webhook public base URL is missing.",
                    "Configure PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL with the current HTTPS tunnel URL."
            );
        }
        return ready("Public webhook URL", "Payload URL is " + configuration.githubWebhookPayloadUrl() + ".");
    }

    private static ExternalExposureReadinessCheckVo triggerUserAllowlistCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.triggerUserAllowlistConfigured()) {
            return needsAttention(
                    "Trigger user allowlist",
                    "Trigger user allowlist is open.",
                    "Configure PATCHPILOT_ALLOWED_TRIGGER_USERS before inviting external trigger traffic."
            );
        }
        return ready("Trigger user allowlist", "Allowed trigger users: " + join(configuration.allowedTriggerUsers()) + ".");
    }

    private static ExternalExposureReadinessCheckVo repositoryAllowlistCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.repositoryAllowlistConfigured()) {
            return needsAttention(
                    "Repository allowlist",
                    "Repository allowlist is open.",
                    "Configure PATCHPILOT_ALLOWED_REPOSITORIES before accepting external repository traffic."
            );
        }
        return ready("Repository allowlist", "Allowed repositories: " + join(configuration.allowedRepositories()) + ".");
    }

    private static ExternalExposureReadinessCheckVo triggerRateLimitCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.triggerRateLimitEnabled()) {
            return needsAttention(
                    "Trigger rate limit",
                    "Trigger rate limiting is disabled.",
                    "Enable PATCHPILOT_TRIGGER_RATE_LIMIT_ENABLED before exposing the webhook."
            );
        }
        return ready(
                "Trigger rate limit",
                "Trigger rate limiting is enabled for users, repositories, and issues."
        );
    }

    private static ExternalExposureReadinessCheckVo rejectedTriggerQuarantineCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.rejectedTriggerQuarantineEnabled()) {
            return needsAttention(
                    "Rejected-trigger quarantine",
                    "Rejected-trigger quarantine is disabled.",
                    "Enable PATCHPILOT_REJECTED_TRIGGER_QUARANTINE_ENABLED before exposing the webhook."
            );
        }
        return ready("Rejected-trigger quarantine", "Repeated rejected triggers can be quarantined.");
    }

    private static ExternalExposureReadinessCheckVo reviewApprovalAllowlistCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.reviewApprovalAllowlistConfigured()) {
            return needsAttention(
                    "Review approval allowlist",
                    "Review approval allowlist is missing.",
                    "Configure PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS before approving risky diffs."
            );
        }
        return ready(
                "Review approval allowlist",
                "Allowed review operators: " + join(configuration.reviewApprovalAllowedOperators()) + "."
        );
    }

    private static ExternalExposureReadinessCheckVo generatedDiffRiskGateCheck(ConfigurationSummaryVo configuration) {
        if (!configuration.generatedDiffRiskGateEnabled()) {
            return needsAttention(
                    "Generated diff risk gate",
                    "Generated diff risk gate is disabled.",
                    "Enable generated diff risk checks before exposing task execution."
            );
        }
        return ready(
                "Generated diff risk gate",
                configuration.generatedDiffProtectedPathCount() + " protected path rule" + plural(configuration.generatedDiffProtectedPathCount()) + " configured."
        );
    }

    private static List<String> nextActions(
            List<ExternalExposureReadinessCheckVo> checks,
            String status
    ) {
        if (STATUS_READY.equals(status)) {
            return List.of("Start the temporary tunnel and keep monitoring webhook deliveries, rejected triggers, and queue health.");
        }
        return checks.stream()
                .filter(check -> !STATUS_READY.equals(check.status()))
                .map(ExternalExposureReadinessCheckVo::nextAction)
                .distinct()
                .toList();
    }

    private static String summary(String status) {
        return switch (status) {
            case STATUS_READY -> "PatchPilot is configured for controlled temporary public exposure.";
            case STATUS_BLOCKED -> "PatchPilot is blocked from safe public exposure.";
            default -> "PatchPilot needs more safeguards before public exposure.";
        };
    }

    private static String aggregateStatus(int blockedCount, int needsAttentionCount) {
        if (blockedCount > 0) {
            return STATUS_BLOCKED;
        }
        if (needsAttentionCount > 0) {
            return STATUS_NEEDS_ATTENTION;
        }
        return STATUS_READY;
    }

    private static int count(List<ExternalExposureReadinessCheckVo> checks, String status) {
        return Math.toIntExact(checks.stream()
                .filter(check -> status.equals(check.status()))
                .count());
    }

    private static String markdownReport(
            String status,
            String summary,
            List<ExternalExposureReadinessCheckVo> checks,
            List<String> nextActions,
            Instant generatedAt
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot External Exposure Readiness\n\n");
        builder.append("- Status: ").append(status).append("\n");
        builder.append("- Summary: ").append(summary).append("\n");
        builder.append("- Generated at: ").append(generatedAt).append("\n\n");
        builder.append("## Checks\n\n");
        for (ExternalExposureReadinessCheckVo check : checks) {
            builder.append("- ").append(check.status()).append(" - ").append(check.name())
                    .append(": ").append(check.summary()).append(" Next action: ")
                    .append(check.nextAction()).append("\n");
        }
        builder.append("\n## Next Actions\n\n");
        for (String action : nextActions) {
            builder.append("- ").append(action).append("\n");
        }
        builder.append("\n## Side Effect Contract\n\n");
        builder.append(SIDE_EFFECT_CONTRACT).append("\n");
        return builder.toString();
    }

    private static ExternalExposureReadinessCheckVo ready(String name, String summary) {
        return new ExternalExposureReadinessCheckVo(name, STATUS_READY, summary, "No action needed.");
    }

    private static ExternalExposureReadinessCheckVo needsAttention(String name, String summary, String nextAction) {
        return new ExternalExposureReadinessCheckVo(name, STATUS_NEEDS_ATTENTION, summary, nextAction);
    }

    private static ExternalExposureReadinessCheckVo blocked(String name, String summary, String nextAction) {
        return new ExternalExposureReadinessCheckVo(name, STATUS_BLOCKED, summary, nextAction);
    }

    private static String join(List<String> values) {
        return values == null || values.isEmpty() ? "none" : String.join(", ", values);
    }

    private static String plural(int count) {
        return count == 1 ? "" : "s";
    }
}
