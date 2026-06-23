package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.convert.TriggerQuarantineConvert;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

@Service
@Profile("default")
public class InMemoryRejectedTriggerAuditService implements RejectedTriggerAuditService {

    private final List<RejectedTriggerAuditVo> audits = new CopyOnWriteArrayList<>();
    private final Supplier<Instant> clock;

    public InMemoryRejectedTriggerAuditService() {
        this(Instant::now);
    }

    public InMemoryRejectedTriggerAuditService(Supplier<Instant> clock) {
        this.clock = clock;
    }

    @Override
    public RejectedTriggerAuditVo recordRejectedTrigger(RecordRejectedTriggerCommand command) {
        RejectedTriggerAuditVo audit = new RejectedTriggerAuditVo(
                UUID.randomUUID().toString(),
                command.source(),
                command.deliveryId(),
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.triggerUser(),
                command.triggerComment(),
                command.reason(),
                command.category(),
                command.commentId(),
                command.commentUrl(),
                null,
                null,
                clock.get()
        );
        audits.add(audit);
        return audit;
    }

    @Override
    public List<RejectedTriggerAuditVo> listRejectedTriggers(int limit) {
        return audits.stream()
                .sorted(Comparator.comparing(RejectedTriggerAuditVo::createdAt).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<RejectedTriggerAuditVo> listRejectedTriggers(int limit, String category) {
        if (category == null || category.isBlank()) {
            return listRejectedTriggers(limit);
        }
        return audits.stream()
                .filter(audit -> category.trim().equals(audit.category()))
                .sorted(Comparator.comparing(RejectedTriggerAuditVo::createdAt).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<RejectedTriggerAuditVo> listRejectedTriggersForQuarantine(
            TriggerQuarantineScope scope,
            String scopeKey,
            int limit
    ) {
        String normalizedScopeKey = TriggerQuarantineConvert.normalizedScopeKey(scopeKey);
        return audits.stream()
                .filter(audit -> matchesQuarantine(audit, scope, normalizedScopeKey))
                .sorted(Comparator.comparing(RejectedTriggerAuditVo::createdAt).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<RejectedTriggerAuditVo> findRejectedTrigger(String id) {
        return audits.stream()
                .filter(audit -> audit.id().equals(id))
                .findFirst();
    }

    @Override
    public RejectedTriggerAuditVo markRetried(String id, String taskId, Instant retriedAt) {
        for (int index = 0; index < audits.size(); index++) {
            RejectedTriggerAuditVo audit = audits.get(index);
            if (audit.id().equals(id)) {
                RejectedTriggerAuditVo updated = new RejectedTriggerAuditVo(
                        audit.id(),
                        audit.source(),
                        audit.deliveryId(),
                        audit.repositoryOwner(),
                        audit.repositoryName(),
                        audit.issueNumber(),
                        audit.triggerUser(),
                        audit.triggerComment(),
                        audit.reason(),
                        audit.category(),
                        audit.commentId(),
                        audit.commentUrl(),
                        taskId,
                        retriedAt,
                        audit.createdAt()
                );
                audits.set(index, updated);
                return updated;
            }
        }
        throw new IllegalArgumentException("Rejected trigger not found");
    }

    private static boolean matchesQuarantine(
            RejectedTriggerAuditVo audit,
            TriggerQuarantineScope scope,
            String normalizedScopeKey
    ) {
        if (scope == TriggerQuarantineScope.TRIGGER_USER) {
            return TriggerQuarantineConvert.normalizedScopeKey(audit.triggerUser()).equals(normalizedScopeKey);
        }
        return TriggerQuarantineConvert.normalizedScopeKey(repositoryKey(audit)).equals(normalizedScopeKey);
    }

    private static String repositoryKey(RejectedTriggerAuditVo audit) {
        if (audit.repositoryOwner() == null || audit.repositoryName() == null) {
            return "";
        }
        return audit.repositoryOwner() + "/" + audit.repositoryName();
    }
}
