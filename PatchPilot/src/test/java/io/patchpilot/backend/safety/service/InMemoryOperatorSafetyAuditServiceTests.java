package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.service.impl.InMemoryOperatorSafetyAuditService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryOperatorSafetyAuditServiceTests {

    private final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-06-24T01:00:00Z"));
    private final OperatorSafetyAuditService auditService = new InMemoryOperatorSafetyAuditService(now::get);

    @Test
    void should_record_and_list_recent_safety_audits_newest_first() {
        OperatorSafetyAuditVo created = auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "MANUAL_QUARANTINE_CREATED",
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                "local-admin",
                "Operator blocked noisy demo trigger user"
        ));

        now.set(Instant.parse("2026-06-24T01:05:00Z"));
        OperatorSafetyAuditVo released = auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "TRIGGER_QUARANTINE_RELEASED",
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                "local-admin",
                "False positive during demo"
        ));

        List<OperatorSafetyAuditVo> audits = auditService.listSafetyAudits(20);

        assertThat(created.id()).isNotBlank();
        assertThat(created.createdAt()).isEqualTo(Instant.parse("2026-06-24T01:00:00Z"));
        assertThat(released.createdAt()).isEqualTo(Instant.parse("2026-06-24T01:05:00Z"));
        assertThat(audits)
                .extracting(OperatorSafetyAuditVo::action)
                .containsExactly("TRIGGER_QUARANTINE_RELEASED", "MANUAL_QUARANTINE_CREATED");
    }
}
