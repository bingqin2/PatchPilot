package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.convert.OperatorSafetyAuditConvert;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditQuery;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

@Service
@Profile("default")
public class InMemoryOperatorSafetyAuditService implements OperatorSafetyAuditService {

    private final List<OperatorSafetyAuditVo> audits = new CopyOnWriteArrayList<>();
    private final Supplier<Instant> clock;

    public InMemoryOperatorSafetyAuditService() {
        this(Instant::now);
    }

    public InMemoryOperatorSafetyAuditService(Supplier<Instant> clock) {
        this.clock = clock;
    }

    @Override
    public OperatorSafetyAuditVo recordSafetyAudit(RecordOperatorSafetyAuditCommand command) {
        OperatorSafetyAuditVo audit = OperatorSafetyAuditConvert.newVo(UUID.randomUUID().toString(), command, clock.get());
        audits.add(audit);
        return audit;
    }

    @Override
    public List<OperatorSafetyAuditVo> listSafetyAudits(OperatorSafetyAuditQuery query) {
        return audits.stream()
                .filter(audit -> matches(query, audit))
                .sorted(Comparator.comparing(OperatorSafetyAuditVo::createdAt).reversed())
                .limit(query.limit())
                .toList();
    }

    @Override
    public List<OperatorSafetyAuditVo> listSafetyAuditsForResource(String resourceType, String resourceId, int limit) {
        String normalizedResourceType = normalized(resourceType);
        String normalizedResourceId = normalized(resourceId);
        return audits.stream()
                .filter(audit -> normalized(audit.resourceType()).equals(normalizedResourceType))
                .filter(audit -> normalized(audit.resourceId()).equals(normalizedResourceId))
                .sorted(Comparator.comparing(OperatorSafetyAuditVo::createdAt).reversed())
                .limit(limit)
                .toList();
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean matches(OperatorSafetyAuditQuery query, OperatorSafetyAuditVo audit) {
        return (!query.hasAction() || query.action().equalsIgnoreCase(audit.action()))
                && (!query.hasResourceType() || query.resourceType().equalsIgnoreCase(audit.resourceType()))
                && (!query.hasResourceId() || query.resourceId().equals(audit.resourceId()))
                && (!query.hasScope() || query.scope() == audit.scope())
                && (!query.hasScopeKey() || query.scopeKey().equals(normalized(audit.scopeKey())))
                && (!query.hasOperator() || query.operator().equals(audit.operator()));
    }
}
