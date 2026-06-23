package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineDecision;
import io.patchpilot.backend.safety.domain.TriggerQuarantineRequest;
import io.patchpilot.backend.safety.service.impl.InMemoryRejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.impl.InMemoryTriggerQuarantineService;
import io.patchpilot.backend.safety.service.impl.RejectedTriggerQuarantineService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RejectedTriggerQuarantineServiceTests {

    private final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-06-23T00:00:00Z"));
    private final InMemoryRejectedTriggerAuditService auditService = new InMemoryRejectedTriggerAuditService(now::get);
    private final InMemoryTriggerQuarantineService quarantineRecordService =
            new InMemoryTriggerQuarantineService(now::get);

    @Test
    void should_quarantine_trigger_user_after_recent_rejected_trigger_threshold() {
        RejectedTriggerQuarantineService quarantineService = new RejectedTriggerQuarantineService(
                properties(true, 600_000, 2, 1_800_000),
                auditService,
                quarantineRecordService,
                now::get
        );
        auditService.recordRejectedTrigger(command("alice", "octocat", "hello-world", "delivery-1"));
        auditService.recordRejectedTrigger(command("alice", "octocat", "other-repo", "delivery-2"));

        TriggerQuarantineDecision decision = quarantineService.check(request("alice", "octocat", "hello-world"));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: trigger user is temporarily quarantined");
        assertThat(decision.category()).isEqualTo("ABUSE_QUARANTINED");
    }

    @Test
    void should_quarantine_repository_after_recent_rejected_trigger_threshold() {
        RejectedTriggerQuarantineService quarantineService = new RejectedTriggerQuarantineService(
                properties(true, 600_000, 2, 1_800_000),
                auditService,
                quarantineRecordService,
                now::get
        );
        auditService.recordRejectedTrigger(command("alice", "octocat", "hello-world", "delivery-1"));
        auditService.recordRejectedTrigger(command("bob", "octocat", "hello-world", "delivery-2"));

        TriggerQuarantineDecision decision = quarantineService.check(request("charlie", "octocat", "hello-world"));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: repository is temporarily quarantined");
        assertThat(decision.category()).isEqualTo("ABUSE_QUARANTINED");
    }

    @Test
    void should_accept_when_quarantine_is_disabled() {
        RejectedTriggerQuarantineService quarantineService = new RejectedTriggerQuarantineService(
                properties(false, 600_000, 1, 1_800_000),
                auditService,
                quarantineRecordService,
                now::get
        );
        auditService.recordRejectedTrigger(command("alice", "octocat", "hello-world", "delivery-1"));

        TriggerQuarantineDecision decision = quarantineService.check(request("alice", "octocat", "hello-world"));

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void should_accept_when_threshold_is_invalid() {
        RejectedTriggerQuarantineService quarantineService = new RejectedTriggerQuarantineService(
                properties(true, 600_000, 0, 1_800_000),
                auditService,
                quarantineRecordService,
                now::get
        );
        auditService.recordRejectedTrigger(command("alice", "octocat", "hello-world", "delivery-1"));

        TriggerQuarantineDecision decision = quarantineService.check(request("alice", "octocat", "hello-world"));

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void should_accept_when_rejections_are_outside_window_or_cooldown() {
        RejectedTriggerQuarantineService quarantineService = new RejectedTriggerQuarantineService(
                properties(true, 600_000, 2, 1_800_000),
                auditService,
                quarantineRecordService,
                now::get
        );
        auditService.recordRejectedTrigger(command("alice", "octocat", "hello-world", "delivery-1"));
        now.set(Instant.parse("2026-06-23T00:31:00Z"));
        auditService.recordRejectedTrigger(command("alice", "octocat", "hello-world", "delivery-2"));

        TriggerQuarantineDecision decision = quarantineService.check(request("alice", "octocat", "hello-world"));

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void should_keep_quarantine_until_cooldown_expires_after_threshold_is_reached() {
        RejectedTriggerQuarantineService quarantineService = new RejectedTriggerQuarantineService(
                properties(true, 600_000, 2, 1_800_000),
                auditService,
                quarantineRecordService,
                now::get
        );
        auditService.recordRejectedTrigger(command("alice", "octocat", "hello-world", "delivery-1"));
        now.set(Instant.parse("2026-06-23T00:05:00Z"));
        auditService.recordRejectedTrigger(command("alice", "octocat", "hello-world", "delivery-2"));

        now.set(Instant.parse("2026-06-23T00:20:00Z"));

        assertThat(quarantineService.check(request("alice", "octocat", "hello-world")).allowed()).isFalse();

        now.set(Instant.parse("2026-06-23T00:36:00Z"));

        assertThat(quarantineService.check(request("alice", "octocat", "hello-world")).allowed()).isTrue();
    }

    @Test
    void should_reject_from_active_durable_quarantine_before_scanning_recent_audits() {
        RejectedTriggerQuarantineService quarantineService = new RejectedTriggerQuarantineService(
                properties(true, 600_000, 5, 1_800_000),
                auditService,
                quarantineRecordService,
                now::get
        );
        quarantineRecordService.recordQuarantine(new io.patchpilot.backend.safety.domain.RecordTriggerQuarantineCommand(
                TriggerQuarantineScope.TRIGGER_USER,
                "alice",
                "Unsafe request rejected: trigger user is temporarily quarantined",
                "ABUSE_QUARANTINED",
                5,
                600_000,
                now.get().plusSeconds(1800)
        ));

        TriggerQuarantineDecision decision = quarantineService.check(request("Alice", "octocat", "hello-world"));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).isEqualTo("Unsafe request rejected: trigger user is temporarily quarantined");
        assertThat(decision.category()).isEqualTo("ABUSE_QUARANTINED");
    }

    private static SafetyProperties properties(
            boolean enabled,
            long windowMs,
            int threshold,
            long cooldownMs
    ) {
        SafetyProperties properties = new SafetyProperties();
        properties.setRejectedTriggerQuarantineEnabled(enabled);
        properties.setRejectedTriggerQuarantineWindowMs(windowMs);
        properties.setRejectedTriggerQuarantineThreshold(threshold);
        properties.setRejectedTriggerQuarantineCooldownMs(cooldownMs);
        return properties;
    }

    private static TriggerQuarantineRequest request(String triggerUser, String repositoryOwner, String repositoryName) {
        return new TriggerQuarantineRequest("issue_comment", repositoryOwner, repositoryName, 42, triggerUser);
    }

    private static RecordRejectedTriggerCommand command(
            String triggerUser,
            String repositoryOwner,
            String repositoryName,
            String deliveryId
    ) {
        return new RecordRejectedTriggerCommand(
                "issue_comment",
                deliveryId,
                repositoryOwner,
                repositoryName,
                42L,
                triggerUser,
                "/agent fix make it better",
                "Unsafe request rejected: instruction is not actionable",
                "NOT_ACTIONABLE"
        );
    }
}
