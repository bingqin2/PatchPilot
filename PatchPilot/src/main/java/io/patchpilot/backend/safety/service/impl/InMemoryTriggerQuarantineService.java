package io.patchpilot.backend.safety.service.impl;

import io.patchpilot.backend.safety.convert.TriggerQuarantineConvert;
import io.patchpilot.backend.safety.domain.RecordTriggerQuarantineCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.service.TriggerQuarantineRecordService;
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
public class InMemoryTriggerQuarantineService implements TriggerQuarantineRecordService {

    private final List<TriggerQuarantineVo> quarantines = new CopyOnWriteArrayList<>();
    private final Supplier<Instant> clock;

    public InMemoryTriggerQuarantineService() {
        this(Instant::now);
    }

    public InMemoryTriggerQuarantineService(Supplier<Instant> clock) {
        this.clock = clock;
    }

    @Override
    public TriggerQuarantineVo recordQuarantine(RecordTriggerQuarantineCommand command) {
        Instant now = clock.get();
        String scopeKey = TriggerQuarantineConvert.normalizedScopeKey(command.scopeKey());
        for (int index = 0; index < quarantines.size(); index++) {
            TriggerQuarantineVo existing = quarantines.get(index);
            if (existing.scope() == command.scope() && existing.scopeKey().equals(scopeKey)) {
                TriggerQuarantineVo updated = new TriggerQuarantineVo(
                        existing.id(),
                        existing.scope(),
                        existing.scopeKey(),
                        command.reason(),
                        command.category(),
                        command.evidenceCount(),
                        command.windowMs(),
                        existing.startedAt(),
                        command.expiresAt(),
                        existing.createdAt(),
                        now,
                        command.expiresAt().isAfter(now)
                );
                quarantines.set(index, updated);
                return updated;
            }
        }

        TriggerQuarantineVo quarantine = new TriggerQuarantineVo(
                UUID.randomUUID().toString(),
                command.scope(),
                scopeKey,
                command.reason(),
                command.category(),
                command.evidenceCount(),
                command.windowMs(),
                now,
                command.expiresAt(),
                now,
                now,
                command.expiresAt().isAfter(now)
        );
        quarantines.add(quarantine);
        return quarantine;
    }

    @Override
    public Optional<TriggerQuarantineVo> findActiveQuarantine(TriggerQuarantineScope scope, String scopeKey) {
        String normalizedScopeKey = TriggerQuarantineConvert.normalizedScopeKey(scopeKey);
        Instant now = clock.get();
        return quarantines.stream()
                .filter(quarantine -> quarantine.scope() == scope)
                .filter(quarantine -> quarantine.scopeKey().equals(normalizedScopeKey))
                .filter(quarantine -> quarantine.expiresAt().isAfter(now))
                .findFirst()
                .map(quarantine -> withActive(quarantine, now));
    }

    @Override
    public List<TriggerQuarantineVo> listQuarantines(boolean activeOnly, int limit) {
        Instant now = clock.get();
        return quarantines.stream()
                .map(quarantine -> withActive(quarantine, now))
                .filter(quarantine -> !activeOnly || quarantine.active())
                .sorted(Comparator.comparing(TriggerQuarantineVo::updatedAt).reversed())
                .limit(limit)
                .toList();
    }

    private static TriggerQuarantineVo withActive(TriggerQuarantineVo quarantine, Instant now) {
        return new TriggerQuarantineVo(
                quarantine.id(),
                quarantine.scope(),
                quarantine.scopeKey(),
                quarantine.reason(),
                quarantine.category(),
                quarantine.evidenceCount(),
                quarantine.windowMs(),
                quarantine.startedAt(),
                quarantine.expiresAt(),
                quarantine.createdAt(),
                quarantine.updatedAt(),
                quarantine.expiresAt().isAfter(now)
        );
    }
}
