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

    private static final String MANUAL_QUARANTINE_CATEGORY = "MANUAL_QUARANTINE";

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
                        existing.createdBy(),
                        null,
                        null,
                        null,
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
                null,
                null,
                null,
                null,
                command.expiresAt().isAfter(now)
        );
        quarantines.add(quarantine);
        return quarantine;
    }

    @Override
    public TriggerQuarantineVo createManualQuarantine(
            TriggerQuarantineScope scope,
            String scopeKey,
            String reason,
            long durationMs,
            String operator
    ) {
        validateManualQuarantine(scope, scopeKey, reason, durationMs, operator);
        Instant now = clock.get();
        String normalizedScopeKey = TriggerQuarantineConvert.normalizedScopeKey(scopeKey);
        for (int index = 0; index < quarantines.size(); index++) {
            TriggerQuarantineVo existing = quarantines.get(index);
            if (existing.scope() == scope && existing.scopeKey().equals(normalizedScopeKey)) {
                TriggerQuarantineVo updated = new TriggerQuarantineVo(
                        existing.id(),
                        scope,
                        normalizedScopeKey,
                        reason.trim(),
                        MANUAL_QUARANTINE_CATEGORY,
                        0,
                        0,
                        existing.startedAt(),
                        now.plusMillis(durationMs),
                        existing.createdAt(),
                        now,
                        trimmed(operator),
                        null,
                        null,
                        null,
                        true
                );
                quarantines.set(index, updated);
                return updated;
            }
        }
        TriggerQuarantineVo quarantine = new TriggerQuarantineVo(
                UUID.randomUUID().toString(),
                scope,
                normalizedScopeKey,
                reason.trim(),
                MANUAL_QUARANTINE_CATEGORY,
                0,
                0,
                now,
                now.plusMillis(durationMs),
                now,
                now,
                trimmed(operator),
                null,
                null,
                null,
                true
        );
        quarantines.add(quarantine);
        return quarantine;
    }

    @Override
    public TriggerQuarantineVo releaseQuarantine(String id, String operator, String reason) {
        validateRelease(id, operator, reason);
        Instant now = clock.get();
        for (int index = 0; index < quarantines.size(); index++) {
            TriggerQuarantineVo existing = quarantines.get(index);
            if (existing.id().equals(id)) {
                TriggerQuarantineVo released = new TriggerQuarantineVo(
                        existing.id(),
                        existing.scope(),
                        existing.scopeKey(),
                        existing.reason(),
                        existing.category(),
                        existing.evidenceCount(),
                        existing.windowMs(),
                        existing.startedAt(),
                        existing.expiresAt(),
                        existing.createdAt(),
                        now,
                        existing.createdBy(),
                        now,
                        trimmed(operator),
                        reason.trim(),
                        false
                );
                quarantines.set(index, released);
                return released;
            }
        }
        throw new IllegalArgumentException("quarantine not found");
    }

    @Override
    public Optional<TriggerQuarantineVo> findActiveQuarantine(TriggerQuarantineScope scope, String scopeKey) {
        String normalizedScopeKey = TriggerQuarantineConvert.normalizedScopeKey(scopeKey);
        Instant now = clock.get();
        return findQuarantine(scope, normalizedScopeKey)
                .filter(quarantine -> quarantine.releasedAt() == null)
                .filter(quarantine -> quarantine.expiresAt().isAfter(now))
                .map(quarantine -> withActive(quarantine, now));
    }

    @Override
    public Optional<TriggerQuarantineVo> findQuarantine(TriggerQuarantineScope scope, String scopeKey) {
        String normalizedScopeKey = TriggerQuarantineConvert.normalizedScopeKey(scopeKey);
        Instant now = clock.get();
        return quarantines.stream()
                .filter(quarantine -> quarantine.scope() == scope)
                .filter(quarantine -> quarantine.scopeKey().equals(normalizedScopeKey))
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
                quarantine.createdBy(),
                quarantine.releasedAt(),
                quarantine.releasedBy(),
                quarantine.releaseReason(),
                quarantine.releasedAt() == null && quarantine.expiresAt().isAfter(now)
        );
    }

    private static void validateManualQuarantine(
            TriggerQuarantineScope scope,
            String scopeKey,
            String reason,
            long durationMs,
            String operator
    ) {
        if (scope == null) {
            throw new IllegalArgumentException("scope is required");
        }
        if (TriggerQuarantineConvert.normalizedScopeKey(scopeKey).isBlank()) {
            throw new IllegalArgumentException("scopeKey is required");
        }
        if (trimmed(reason).isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
        if (durationMs < 60_000 || durationMs > 86_400_000) {
            throw new IllegalArgumentException("durationMs must be between 60000 and 86400000");
        }
        if (trimmed(operator).isBlank()) {
            throw new IllegalArgumentException("operator is required");
        }
    }

    private static void validateRelease(String id, String operator, String reason) {
        if (trimmed(id).isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        if (trimmed(operator).isBlank()) {
            throw new IllegalArgumentException("operator is required");
        }
        if (trimmed(reason).isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
    }

    private static String trimmed(String value) {
        return value == null ? "" : value.trim();
    }
}
