package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;

import java.util.List;
import java.util.Optional;

public interface RejectedTriggerAuditService {

    RejectedTriggerAuditVo recordRejectedTrigger(RecordRejectedTriggerCommand command);

    List<RejectedTriggerAuditVo> listRejectedTriggers(int limit);

    Optional<RejectedTriggerAuditVo> findRejectedTrigger(String id);
}
