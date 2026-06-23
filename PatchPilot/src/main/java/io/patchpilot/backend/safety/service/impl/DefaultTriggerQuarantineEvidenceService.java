package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.domain.TriggerQuarantineEvidenceVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.TriggerQuarantineEvidenceService;
import io.patchpilot.backend.safety.service.TriggerQuarantineRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultTriggerQuarantineEvidenceService implements TriggerQuarantineEvidenceService {

    private static final String TRIGGER_QUARANTINE_RESOURCE_TYPE = "TRIGGER_QUARANTINE";

    private final TriggerQuarantineRecordService quarantineRecordService;
    private final RejectedTriggerAuditService rejectedTriggerAuditService;
    private final OperatorSafetyAuditService operatorSafetyAuditService;

    @Override
    public TriggerQuarantineEvidenceVo getEvidence(String quarantineId, int limit) {
        TriggerQuarantineVo quarantine = quarantineRecordService.findQuarantineById(quarantineId)
                .orElseThrow(() -> new QuarantineNotFoundException("quarantine not found"));
        return new TriggerQuarantineEvidenceVo(
                quarantine,
                rejectedTriggerAuditService.listRejectedTriggersForQuarantine(
                        quarantine.scope(),
                        quarantine.scopeKey(),
                        limit
                ),
                operatorSafetyAuditService.listSafetyAuditsForResource(
                        TRIGGER_QUARANTINE_RESOURCE_TYPE,
                        quarantine.id(),
                        limit
                )
        );
    }
}
