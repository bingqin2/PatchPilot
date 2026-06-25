package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditQuery;
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

    @Test
    void should_list_safety_audits_for_resource_newest_first() {
        auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "MANUAL_QUARANTINE_CREATED",
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                "local-admin",
                "Operator blocked noisy demo trigger user"
        ));
        now.set(Instant.parse("2026-06-24T01:03:00Z"));
        auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "MANUAL_QUARANTINE_CREATED",
                "TRIGGER_QUARANTINE",
                "quarantine-2",
                TriggerQuarantineScope.TRIGGER_USER,
                "other-user",
                "local-admin",
                "Unrelated quarantine"
        ));
        now.set(Instant.parse("2026-06-24T01:05:00Z"));
        auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "TRIGGER_QUARANTINE_RELEASED",
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                "release-captain",
                "False positive during demo"
        ));

        List<OperatorSafetyAuditVo> audits = auditService.listSafetyAuditsForResource(
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                20
        );

        assertThat(audits)
                .extracting(OperatorSafetyAuditVo::action)
                .containsExactly("TRIGGER_QUARANTINE_RELEASED", "MANUAL_QUARANTINE_CREATED");
    }

    @Test
    void should_filter_safety_audits_by_query_fields_newest_first() {
        auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "TASK_CANCELLED",
                "TASK",
                "task-123",
                TriggerQuarantineScope.REPOSITORY,
                "bingqin2/patchpilot",
                "other-admin",
                "Operator cancelled a stuck task"
        ));
        now.set(Instant.parse("2026-06-24T01:03:00Z"));
        auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "TASK_RETRIED",
                "TASK",
                "task-123",
                TriggerQuarantineScope.REPOSITORY,
                "bingqin2/patchpilot",
                "admin-api",
                "Verified failure cause and requested a clean rerun"
        ));
        now.set(Instant.parse("2026-06-24T01:05:00Z"));
        auditService.recordSafetyAudit(new RecordOperatorSafetyAuditCommand(
                "TASK_RETRIED",
                "TASK",
                "task-999",
                TriggerQuarantineScope.REPOSITORY,
                "other/repo",
                "admin-api",
                "Unrelated task retry"
        ));

        List<OperatorSafetyAuditVo> audits = auditService.listSafetyAudits(new OperatorSafetyAuditQuery(
                20,
                "task_retried",
                "task",
                "task-123",
                TriggerQuarantineScope.REPOSITORY,
                "BINGQIN2/PATCHPILOT",
                "admin-api"
        ));

        assertThat(audits)
                .extracting(OperatorSafetyAuditVo::reason)
                .containsExactly("Verified failure cause and requested a clean rerun");
    }
}
