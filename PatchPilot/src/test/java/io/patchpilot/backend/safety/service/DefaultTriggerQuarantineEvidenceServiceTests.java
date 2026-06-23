package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineEvidenceVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.service.impl.DefaultTriggerQuarantineEvidenceService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultTriggerQuarantineEvidenceServiceTests {

    private final TriggerQuarantineRecordService quarantineRecordService = mock(TriggerQuarantineRecordService.class);
    private final RejectedTriggerAuditService rejectedTriggerAuditService = mock(RejectedTriggerAuditService.class);
    private final OperatorSafetyAuditService operatorSafetyAuditService = mock(OperatorSafetyAuditService.class);
    private final TriggerQuarantineEvidenceService evidenceService = new DefaultTriggerQuarantineEvidenceService(
            quarantineRecordService,
            rejectedTriggerAuditService,
            operatorSafetyAuditService
    );

    @Test
    void should_collect_quarantine_rejected_trigger_and_operator_evidence() {
        TriggerQuarantineVo quarantine = quarantine();
        when(quarantineRecordService.findQuarantineById("quarantine-1")).thenReturn(Optional.of(quarantine));
        when(rejectedTriggerAuditService.listRejectedTriggersForQuarantine(
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                20
        )).thenReturn(List.of(rejectedTrigger()));
        when(operatorSafetyAuditService.listSafetyAuditsForResource(
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                20
        )).thenReturn(List.of(operatorAudit()));

        TriggerQuarantineEvidenceVo evidence = evidenceService.getEvidence("quarantine-1", 20);

        assertThat(evidence.quarantine().id()).isEqualTo("quarantine-1");
        assertThat(evidence.rejectedTriggers())
                .extracting(RejectedTriggerAuditVo::id)
                .containsExactly("rejected-1");
        assertThat(evidence.operatorSafetyAudits())
                .extracting(OperatorSafetyAuditVo::action)
                .containsExactly("MANUAL_QUARANTINE_CREATED");
    }

    @Test
    void should_reject_missing_quarantine() {
        when(quarantineRecordService.findQuarantineById("missing-quarantine")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evidenceService.getEvidence("missing-quarantine", 20))
                .isInstanceOf(TriggerQuarantineEvidenceService.QuarantineNotFoundException.class)
                .hasMessage("quarantine not found");
    }

    private static TriggerQuarantineVo quarantine() {
        return new TriggerQuarantineVo(
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                "Unsafe request rejected: trigger user is temporarily quarantined",
                "ABUSE_QUARANTINED",
                5,
                600_000,
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:30:00Z"),
                Instant.parse("2026-06-24T00:00:00Z"),
                Instant.parse("2026-06-24T00:05:00Z"),
                null,
                null,
                null,
                null,
                true
        );
    }

    private static RejectedTriggerAuditVo rejectedTrigger() {
        return new RejectedTriggerAuditVo(
                "rejected-1",
                "issue_comment",
                "delivery-rejected",
                "bingqin2",
                "PatchPilot",
                1L,
                "drive-by-user",
                "/agent fix make it better",
                "Unsafe request rejected: instruction is not actionable",
                "NOT_ACTIONABLE",
                456L,
                "https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456",
                null,
                null,
                Instant.parse("2026-06-24T00:01:00Z")
        );
    }

    private static OperatorSafetyAuditVo operatorAudit() {
        return new OperatorSafetyAuditVo(
                "operator-audit-1",
                "MANUAL_QUARANTINE_CREATED",
                "TRIGGER_QUARANTINE",
                "quarantine-1",
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                "local-admin",
                "Operator blocked noisy demo trigger user",
                Instant.parse("2026-06-24T00:02:00Z")
        );
    }
}
