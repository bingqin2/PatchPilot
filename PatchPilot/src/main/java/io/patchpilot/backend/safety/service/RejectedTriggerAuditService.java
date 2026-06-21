package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;

import java.util.List;

public interface RejectedTriggerAuditService {

    RejectedTriggerAuditVo recordRejectedTrigger(RecordRejectedTriggerCommand command);

    List<RejectedTriggerAuditVo> listRejectedTriggers(int limit);
}
