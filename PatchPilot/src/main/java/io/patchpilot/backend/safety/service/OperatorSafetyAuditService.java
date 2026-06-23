package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;

import java.util.List;

public interface OperatorSafetyAuditService {

    OperatorSafetyAuditVo recordSafetyAudit(RecordOperatorSafetyAuditCommand command);

    List<OperatorSafetyAuditVo> listSafetyAudits(int limit);
}
