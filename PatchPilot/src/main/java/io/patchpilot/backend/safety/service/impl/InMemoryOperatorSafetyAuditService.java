package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.convert.OperatorSafetyAuditConvert;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
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
    public List<OperatorSafetyAuditVo> listSafetyAudits(int limit) {
        return audits.stream()
                .sorted(Comparator.comparing(OperatorSafetyAuditVo::createdAt).reversed())
                .limit(limit)
                .toList();
    }
}
