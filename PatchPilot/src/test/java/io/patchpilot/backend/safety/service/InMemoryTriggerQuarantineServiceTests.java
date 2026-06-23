package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordTriggerQuarantineCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.service.impl.InMemoryTriggerQuarantineService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTriggerQuarantineServiceTests {

    private final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-06-24T00:00:00Z"));
    private final InMemoryTriggerQuarantineService quarantineService =
            new InMemoryTriggerQuarantineService(now::get);

    @Test
    void should_create_and_find_active_trigger_user_quarantine() {
        TriggerQuarantineVo quarantine = quarantineService.recordQuarantine(new RecordTriggerQuarantineCommand(
                TriggerQuarantineScope.TRIGGER_USER,
                "Alice",
                "Unsafe request rejected: trigger user is temporarily quarantined",
                "ABUSE_QUARANTINED",
                5,
                600_000,
                now.get().plusSeconds(1800)
        ));

        Optional<TriggerQuarantineVo> active = quarantineService.findActiveQuarantine(
                TriggerQuarantineScope.TRIGGER_USER,
                "alice"
        );

        assertThat(active).hasValueSatisfying(found -> {
            assertThat(found.id()).isEqualTo(quarantine.id());
            assertThat(found.scope()).isEqualTo(TriggerQuarantineScope.TRIGGER_USER);
            assertThat(found.scopeKey()).isEqualTo("alice");
            assertThat(found.reason()).isEqualTo("Unsafe request rejected: trigger user is temporarily quarantined");
            assertThat(found.category()).isEqualTo("ABUSE_QUARANTINED");
            assertThat(found.evidenceCount()).isEqualTo(5);
            assertThat(found.windowMs()).isEqualTo(600_000);
            assertThat(found.expiresAt()).isEqualTo(Instant.parse("2026-06-24T00:30:00Z"));
            assertThat(found.active()).isTrue();
        });
    }

    @Test
    void should_extend_existing_repository_quarantine() {
        quarantineService.recordQuarantine(new RecordTriggerQuarantineCommand(
                TriggerQuarantineScope.REPOSITORY,
                "Octocat/Hello-World",
                "Unsafe request rejected: repository is temporarily quarantined",
                "ABUSE_QUARANTINED",
                5,
                600_000,
                now.get().plusSeconds(1800)
        ));

        TriggerQuarantineVo extended = quarantineService.recordQuarantine(new RecordTriggerQuarantineCommand(
                TriggerQuarantineScope.REPOSITORY,
                "octocat/hello-world",
                "Unsafe request rejected: repository is temporarily quarantined",
                "ABUSE_QUARANTINED",
                7,
                600_000,
                now.get().plusSeconds(3600)
        ));
        List<TriggerQuarantineVo> quarantines = quarantineService.listQuarantines(false, 20);

        assertThat(quarantines).hasSize(1);
        assertThat(extended.evidenceCount()).isEqualTo(7);
        assertThat(extended.expiresAt()).isEqualTo(Instant.parse("2026-06-24T01:00:00Z"));
        assertThat(quarantines.get(0).updatedAt()).isEqualTo(now.get());
    }

    @Test
    void should_not_return_expired_quarantine_as_active() {
        quarantineService.recordQuarantine(new RecordTriggerQuarantineCommand(
                TriggerQuarantineScope.TRIGGER_USER,
                "alice",
                "Unsafe request rejected: trigger user is temporarily quarantined",
                "ABUSE_QUARANTINED",
                5,
                600_000,
                now.get().plusSeconds(60)
        ));

        now.set(Instant.parse("2026-06-24T00:02:00Z"));

        assertThat(quarantineService.findActiveQuarantine(TriggerQuarantineScope.TRIGGER_USER, "alice")).isEmpty();
        assertThat(quarantineService.listQuarantines(true, 20)).isEmpty();
        assertThat(quarantineService.listQuarantines(false, 20)).hasSize(1);
    }
}
