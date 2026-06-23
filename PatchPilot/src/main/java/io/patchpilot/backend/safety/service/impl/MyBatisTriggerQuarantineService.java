package io.patchpilot.backend.safety.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.safety.convert.TriggerQuarantineConvert;
import io.patchpilot.backend.safety.domain.RecordTriggerQuarantineCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineEntity;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.domain.TriggerQuarantineVo;
import io.patchpilot.backend.safety.mapper.TriggerQuarantineMapper;
import io.patchpilot.backend.safety.service.TriggerQuarantineRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisTriggerQuarantineService implements TriggerQuarantineRecordService {

    private static final String MANUAL_QUARANTINE_CATEGORY = "MANUAL_QUARANTINE";

    private final TriggerQuarantineMapper quarantineMapper;

    @Override
    public TriggerQuarantineVo recordQuarantine(RecordTriggerQuarantineCommand command) {
        Instant now = Instant.now();
        String scopeKey = TriggerQuarantineConvert.normalizedScopeKey(command.scopeKey());
        Optional<TriggerQuarantineEntity> existing = findEntity(command.scope(), scopeKey);
        if (existing.isPresent()) {
            TriggerQuarantineEntity updateEntity = new TriggerQuarantineEntity();
            updateEntity.setId(existing.get().getId());
            updateEntity.setReason(command.reason());
            updateEntity.setCategory(command.category());
            updateEntity.setEvidenceCount(command.evidenceCount());
            updateEntity.setWindowMs(command.windowMs());
            updateEntity.setExpiresAt(command.expiresAt());
            updateEntity.setUpdatedAt(now);
            updateEntity.setReleasedAt(null);
            updateEntity.setReleasedBy(null);
            updateEntity.setReleaseReason(null);
            quarantineMapper.updateById(updateEntity);
            TriggerQuarantineEntity updated = existing.get();
            updated.setReason(command.reason());
            updated.setCategory(command.category());
            updated.setEvidenceCount(command.evidenceCount());
            updated.setWindowMs(command.windowMs());
            updated.setExpiresAt(command.expiresAt());
            updated.setUpdatedAt(now);
            updated.setReleasedAt(null);
            updated.setReleasedBy(null);
            updated.setReleaseReason(null);
            return TriggerQuarantineConvert.toVo(updated, now);
        }

        TriggerQuarantineEntity entity = TriggerQuarantineConvert.newEntity(
                UUID.randomUUID().toString(),
                new RecordTriggerQuarantineCommand(
                        command.scope(),
                        scopeKey,
                        command.reason(),
                        command.category(),
                        command.evidenceCount(),
                        command.windowMs(),
                        command.expiresAt()
                ),
                now
        );
        quarantineMapper.insert(entity);
        return TriggerQuarantineConvert.toVo(entity, now);
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
        Instant now = Instant.now();
        String normalizedScopeKey = TriggerQuarantineConvert.normalizedScopeKey(scopeKey);
        Optional<TriggerQuarantineEntity> existing = findEntity(scope, normalizedScopeKey);
        if (existing.isPresent()) {
            TriggerQuarantineEntity updateEntity = new TriggerQuarantineEntity();
            updateEntity.setId(existing.get().getId());
            updateEntity.setReason(reason.trim());
            updateEntity.setCategory(MANUAL_QUARANTINE_CATEGORY);
            updateEntity.setEvidenceCount(0);
            updateEntity.setWindowMs(0L);
            updateEntity.setExpiresAt(now.plusMillis(durationMs));
            updateEntity.setUpdatedAt(now);
            updateEntity.setCreatedBy(trimmed(operator));
            updateEntity.setReleasedAt(null);
            updateEntity.setReleasedBy(null);
            updateEntity.setReleaseReason(null);
            quarantineMapper.updateById(updateEntity);

            TriggerQuarantineEntity updated = existing.get();
            updated.setReason(reason.trim());
            updated.setCategory(MANUAL_QUARANTINE_CATEGORY);
            updated.setEvidenceCount(0);
            updated.setWindowMs(0L);
            updated.setExpiresAt(now.plusMillis(durationMs));
            updated.setUpdatedAt(now);
            updated.setCreatedBy(trimmed(operator));
            updated.setReleasedAt(null);
            updated.setReleasedBy(null);
            updated.setReleaseReason(null);
            return TriggerQuarantineConvert.toVo(updated, now);
        }

        TriggerQuarantineEntity entity = new TriggerQuarantineEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setScope(scope.name());
        entity.setScopeKey(normalizedScopeKey);
        entity.setReason(reason.trim());
        entity.setCategory(MANUAL_QUARANTINE_CATEGORY);
        entity.setEvidenceCount(0);
        entity.setWindowMs(0L);
        entity.setStartedAt(now);
        entity.setExpiresAt(now.plusMillis(durationMs));
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setCreatedBy(trimmed(operator));
        entity.setReleasedAt(null);
        entity.setReleasedBy(null);
        entity.setReleaseReason(null);
        quarantineMapper.insert(entity);
        return TriggerQuarantineConvert.toVo(entity, now);
    }

    @Override
    public TriggerQuarantineVo releaseQuarantine(String id, String operator, String reason) {
        validateRelease(id, operator, reason);
        Instant now = Instant.now();
        TriggerQuarantineEntity existing = findEntityById(id)
                .orElseThrow(() -> new IllegalArgumentException("quarantine not found"));

        TriggerQuarantineEntity updateEntity = new TriggerQuarantineEntity();
        updateEntity.setId(id);
        updateEntity.setUpdatedAt(now);
        updateEntity.setReleasedAt(now);
        updateEntity.setReleasedBy(trimmed(operator));
        updateEntity.setReleaseReason(reason.trim());
        quarantineMapper.updateById(updateEntity);

        existing.setUpdatedAt(now);
        existing.setReleasedAt(now);
        existing.setReleasedBy(trimmed(operator));
        existing.setReleaseReason(reason.trim());
        return TriggerQuarantineConvert.toVo(existing, now);
    }

    @Override
    public Optional<TriggerQuarantineVo> findActiveQuarantine(TriggerQuarantineScope scope, String scopeKey) {
        Instant now = Instant.now();
        return findEntity(scope, TriggerQuarantineConvert.normalizedScopeKey(scopeKey))
                .filter(entity -> entity.getReleasedAt() == null)
                .filter(entity -> entity.getExpiresAt() != null && entity.getExpiresAt().isAfter(now))
                .map(entity -> TriggerQuarantineConvert.toVo(entity, now));
    }

    @Override
    public Optional<TriggerQuarantineVo> findQuarantine(TriggerQuarantineScope scope, String scopeKey) {
        Instant now = Instant.now();
        return findEntity(scope, TriggerQuarantineConvert.normalizedScopeKey(scopeKey))
                .map(entity -> TriggerQuarantineConvert.toVo(entity, now));
    }

    @Override
    public Optional<TriggerQuarantineVo> findQuarantineById(String id) {
        Instant now = Instant.now();
        return findEntityById(trimmed(id))
                .map(entity -> TriggerQuarantineConvert.toVo(entity, now));
    }

    @Override
    public List<TriggerQuarantineVo> listQuarantines(boolean activeOnly, int limit) {
        Instant now = Instant.now();
        LambdaQueryWrapper<TriggerQuarantineEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (activeOnly) {
            queryWrapper
                    .isNull(TriggerQuarantineEntity::getReleasedAt)
                    .gt(TriggerQuarantineEntity::getExpiresAt, now);
        }
        queryWrapper
                .orderByDesc(TriggerQuarantineEntity::getUpdatedAt)
                .last("LIMIT " + limit);
        return quarantineMapper.selectList(queryWrapper).stream()
                .map(entity -> TriggerQuarantineConvert.toVo(entity, now))
                .toList();
    }

    private Optional<TriggerQuarantineEntity> findEntityById(String id) {
        LambdaQueryWrapper<TriggerQuarantineEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TriggerQuarantineEntity::getId, id)
                .last("LIMIT 1");
        return quarantineMapper.selectList(queryWrapper).stream().findFirst();
    }

    private Optional<TriggerQuarantineEntity> findEntity(TriggerQuarantineScope scope, String scopeKey) {
        LambdaQueryWrapper<TriggerQuarantineEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TriggerQuarantineEntity::getScope, scope.name())
                .eq(TriggerQuarantineEntity::getScopeKey, scopeKey)
                .last("LIMIT 1");
        return quarantineMapper.selectList(queryWrapper).stream().findFirst();
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
