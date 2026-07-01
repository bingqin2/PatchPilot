package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.configuration.ConfigurationSummaryVo;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureReadinessServiceTests {

    private static final Instant NOW = Instant.parse("2026-07-01T12:00:00Z");

    @Test
    void should_report_ready_when_public_exposure_safeguards_are_configured() {
        AdminApiSecurityProperties securityProperties = new AdminApiSecurityProperties();
        securityProperties.setAdminToken("admin-token");
        securityProperties.setDashboardAdminTokenBootstrapEnabled(false);
        ExternalExposureReadinessService service = service(readyConfiguration(), securityProperties);

        ExternalExposureReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo("READY");
        assertThat(readiness.safeToExpose()).isTrue();
        assertThat(readiness.blockedCount()).isZero();
        assertThat(readiness.needsAttentionCount()).isZero();
        assertThat(readiness.readyCount()).isEqualTo(readiness.checks().size());
        assertThat(readiness.summary()).isEqualTo("PatchPilot is configured for controlled temporary public exposure.");
        assertThat(readiness.nextActions()).containsExactly("Start the temporary tunnel and keep monitoring webhook deliveries, rejected triggers, and queue health.");
        assertThat(readiness.sideEffectContract())
                .contains("does not create tasks")
                .contains("does not expose secrets");
        assertThat(readiness.markdownReport())
                .contains("# PatchPilot External Exposure Readiness")
                .contains("- Status: READY")
                .contains("Admin API token");
    }

    @Test
    void should_block_when_required_public_exposure_controls_are_missing_or_bootstrap_is_enabled() {
        AdminApiSecurityProperties securityProperties = new AdminApiSecurityProperties();
        securityProperties.setDashboardAdminTokenBootstrapEnabled(true);
        ExternalExposureReadinessService service = service(insecureConfiguration(), securityProperties);

        ExternalExposureReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo("BLOCKED");
        assertThat(readiness.safeToExpose()).isFalse();
        assertThat(readiness.blockedCount()).isEqualTo(4);
        assertThat(readiness.needsAttentionCount()).isGreaterThan(0);
        assertThat(readiness.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .contains(
                        "Admin API token:BLOCKED",
                        "Dashboard token bootstrap:BLOCKED",
                        "Webhook secret:BLOCKED",
                        "Public webhook URL:BLOCKED",
                        "Trigger user allowlist:NEEDS_ATTENTION",
                        "Repository allowlist:NEEDS_ATTENTION"
                );
        assertThat(readiness.nextActions())
                .contains("Configure PATCHPILOT_ADMIN_TOKEN before exposing PatchPilot outside localhost.")
                .contains("Set PATCHPILOT_DASHBOARD_ADMIN_TOKEN_BOOTSTRAP_ENABLED=false before using a public tunnel.");
    }

    @Test
    void should_need_attention_when_optional_abuse_controls_are_open() {
        ConfigurationSummaryVo configuration = readyConfiguration(
                false,
                false,
                true,
                false,
                false,
                false,
                true
        );
        AdminApiSecurityProperties securityProperties = new AdminApiSecurityProperties();
        securityProperties.setAdminToken("admin-token");
        ExternalExposureReadinessService service = service(configuration, securityProperties);

        ExternalExposureReadinessVo readiness = service.getReadiness();

        assertThat(readiness.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(readiness.safeToExpose()).isFalse();
        assertThat(readiness.blockedCount()).isZero();
        assertThat(readiness.needsAttentionCount()).isEqualTo(5);
        assertThat(readiness.nextActions())
                .contains("Configure PATCHPILOT_ALLOWED_TRIGGER_USERS before inviting external trigger traffic.")
                .contains("Enable PATCHPILOT_REJECTED_TRIGGER_QUARANTINE_ENABLED before exposing the webhook.");
    }

    private static ExternalExposureReadinessService service(
            ConfigurationSummaryVo configuration,
            AdminApiSecurityProperties securityProperties
    ) {
        return new ExternalExposureReadinessService(
                () -> configuration,
                securityProperties,
                () -> NOW
        );
    }

    private static ConfigurationSummaryVo readyConfiguration() {
        return readyConfiguration(true, true, true, true, true, true, true);
    }

    private static ConfigurationSummaryVo insecureConfiguration() {
        return new ConfigurationSummaryVo(
                "openai",
                "gpt-5.5",
                "https://api.example.test/v1",
                true,
                true,
                false,
                false,
                "",
                "",
                false,
                true,
                "/tmp/patchpilot",
                3,
                1000,
                600000,
                60000,
                true,
                true,
                false,
                600000,
                30,
                60,
                20,
                false,
                600000,
                5,
                1800000,
                false,
                false,
                false,
                false,
                0,
                List.of(),
                List.of(),
                List.of(),
                List.of("/tmp/patchpilot")
        );
    }

    private static ConfigurationSummaryVo readyConfiguration(
            boolean triggerUserAllowlistConfigured,
            boolean repositoryAllowlistConfigured,
            boolean triggerRateLimitEnabled,
            boolean rejectedTriggerQuarantineEnabled,
            boolean reviewApprovalAllowlistConfigured,
            boolean generatedDiffRiskGateEnabled,
            boolean publicWebhookBaseUrlConfigured
    ) {
        return new ConfigurationSummaryVo(
                "openai",
                "gpt-5.5",
                "https://api.example.test/v1",
                true,
                true,
                true,
                publicWebhookBaseUrlConfigured,
                publicWebhookBaseUrlConfigured ? "https://patchpilot.trycloudflare.com" : "",
                publicWebhookBaseUrlConfigured ? "https://patchpilot.trycloudflare.com/api/github/webhook" : "",
                true,
                true,
                "/tmp/patchpilot",
                3,
                1000,
                600000,
                60000,
                true,
                true,
                triggerRateLimitEnabled,
                600000,
                30,
                60,
                20,
                rejectedTriggerQuarantineEnabled,
                600000,
                5,
                1800000,
                triggerUserAllowlistConfigured,
                repositoryAllowlistConfigured,
                reviewApprovalAllowlistConfigured,
                generatedDiffRiskGateEnabled,
                generatedDiffRiskGateEnabled ? 6 : 0,
                triggerUserAllowlistConfigured ? List.of("bingqin2") : List.of(),
                repositoryAllowlistConfigured ? List.of("bingqin2/PatchPilot") : List.of(),
                reviewApprovalAllowlistConfigured ? List.of("bingqin2") : List.of(),
                List.of("/tmp/patchpilot")
        );
    }
}
