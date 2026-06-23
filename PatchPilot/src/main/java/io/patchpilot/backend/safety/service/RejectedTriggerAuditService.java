package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.convert.RejectedTriggerAuditSummaryBuilder;
import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RejectedTriggerAuditService {

    RejectedTriggerAuditVo recordRejectedTrigger(RecordRejectedTriggerCommand command);

    List<RejectedTriggerAuditVo> listRejectedTriggers(int limit);

    default List<RejectedTriggerAuditVo> listRejectedTriggers(int limit, String category) {
        if (category == null || category.isBlank()) {
            return listRejectedTriggers(limit);
        }
        return listRejectedTriggers(limit).stream()
                .filter(audit -> category.trim().equals(audit.category()))
                .toList();
    }

    default RejectedTriggerAuditSummaryVo summarizeRejectedTriggers(int limit) {
        return RejectedTriggerAuditSummaryBuilder.from(listRejectedTriggers(limit));
    }

    Optional<RejectedTriggerAuditVo> findRejectedTrigger(String id);

    RejectedTriggerAuditVo markRetried(String id, String taskId, Instant retriedAt);
}
