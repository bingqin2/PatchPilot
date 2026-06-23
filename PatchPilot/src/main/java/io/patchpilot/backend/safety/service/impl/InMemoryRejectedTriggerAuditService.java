package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Profile("default")
public class InMemoryRejectedTriggerAuditService implements RejectedTriggerAuditService {

    private final List<RejectedTriggerAuditVo> audits = new CopyOnWriteArrayList<>();

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
                Instant.now()
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
}
