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
            quarantineMapper.updateById(updateEntity);
            TriggerQuarantineEntity updated = existing.get();
            updated.setReason(command.reason());
            updated.setCategory(command.category());
            updated.setEvidenceCount(command.evidenceCount());
            updated.setWindowMs(command.windowMs());
            updated.setExpiresAt(command.expiresAt());
            updated.setUpdatedAt(now);
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
    public Optional<TriggerQuarantineVo> findActiveQuarantine(TriggerQuarantineScope scope, String scopeKey) {
        Instant now = Instant.now();
        return findEntity(scope, TriggerQuarantineConvert.normalizedScopeKey(scopeKey))
                .filter(entity -> entity.getExpiresAt() != null && entity.getExpiresAt().isAfter(now))
                .map(entity -> TriggerQuarantineConvert.toVo(entity, now));
    }

    @Override
    public List<TriggerQuarantineVo> listQuarantines(boolean activeOnly, int limit) {
        Instant now = Instant.now();
        LambdaQueryWrapper<TriggerQuarantineEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (activeOnly) {
            queryWrapper.gt(TriggerQuarantineEntity::getExpiresAt, now);
        }
        queryWrapper
                .orderByDesc(TriggerQuarantineEntity::getUpdatedAt)
                .last("LIMIT " + limit);
        return quarantineMapper.selectList(queryWrapper).stream()
                .map(entity -> TriggerQuarantineConvert.toVo(entity, now))
                .toList();
    }

    private Optional<TriggerQuarantineEntity> findEntity(TriggerQuarantineScope scope, String scopeKey) {
        LambdaQueryWrapper<TriggerQuarantineEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TriggerQuarantineEntity::getScope, scope.name())
                .eq(TriggerQuarantineEntity::getScopeKey, scopeKey)
                .last("LIMIT 1");
        return quarantineMapper.selectList(queryWrapper).stream().findFirst();
    }
}
