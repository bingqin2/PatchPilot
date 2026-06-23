package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.convert.RejectedTriggerAuditSummaryBuilder;
import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;

import java.time.Instant;
import java.util.Locale;
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

    default List<RejectedTriggerAuditVo> listRejectedTriggersForQuarantine(
            TriggerQuarantineScope scope,
            String scopeKey,
            int limit
    ) {
        String normalizedScopeKey = normalized(scopeKey);
        return listRejectedTriggers(100).stream()
                .filter(audit -> matchesQuarantine(audit, scope, normalizedScopeKey))
                .limit(limit)
                .toList();
    }

    Optional<RejectedTriggerAuditVo> findRejectedTrigger(String id);

    RejectedTriggerAuditVo markRetried(String id, String taskId, Instant retriedAt);

    private static boolean matchesQuarantine(
            RejectedTriggerAuditVo audit,
            TriggerQuarantineScope scope,
            String normalizedScopeKey
    ) {
        if (scope == TriggerQuarantineScope.TRIGGER_USER) {
            return normalized(audit.triggerUser()).equals(normalizedScopeKey);
        }
        return repositoryKey(audit).equals(normalizedScopeKey);
    }

    private static String repositoryKey(RejectedTriggerAuditVo audit) {
        if (audit.repositoryOwner() == null || audit.repositoryName() == null) {
            return "";
        }
        return normalized(audit.repositoryOwner() + "/" + audit.repositoryName());
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
