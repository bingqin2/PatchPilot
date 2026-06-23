package io.patchpilot.backend.safety.domain;

import java.util.List;

public record TriggerQuarantineEvidenceVo(
        TriggerQuarantineVo quarantine,
        List<RejectedTriggerAuditVo> rejectedTriggers,
        List<OperatorSafetyAuditVo> operatorSafetyAudits
) {
}
