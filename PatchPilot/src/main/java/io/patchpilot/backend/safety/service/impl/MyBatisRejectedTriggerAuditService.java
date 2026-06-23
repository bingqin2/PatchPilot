package io.patchpilot.backend.safety.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.safety.convert.RejectedTriggerAuditConvert;
import io.patchpilot.backend.safety.convert.TriggerQuarantineConvert;
import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditEntity;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.mapper.RejectedTriggerAuditMapper;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisRejectedTriggerAuditService implements RejectedTriggerAuditService {

    private final RejectedTriggerAuditMapper auditMapper;

    @Override
    public RejectedTriggerAuditVo recordRejectedTrigger(RecordRejectedTriggerCommand command) {
        RejectedTriggerAuditEntity entity = RejectedTriggerAuditConvert.newEntity(
                UUID.randomUUID().toString(),
                command,
                Instant.now()
        );
        auditMapper.insert(entity);
        return RejectedTriggerAuditConvert.toVo(entity);
    }

    @Override
    public List<RejectedTriggerAuditVo> listRejectedTriggers(int limit) {
        return listRejectedTriggers(limit, null);
    }

    @Override
    public List<RejectedTriggerAuditVo> listRejectedTriggers(int limit, String category) {
        LambdaQueryWrapper<RejectedTriggerAuditEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(RejectedTriggerAuditEntity::getCategory, category.trim());
        }
        queryWrapper
                .orderByDesc(RejectedTriggerAuditEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return auditMapper.selectList(queryWrapper).stream()
                .map(RejectedTriggerAuditConvert::toVo)
                .toList();
    }

    @Override
    public List<RejectedTriggerAuditVo> listRejectedTriggersForQuarantine(
            TriggerQuarantineScope scope,
            String scopeKey,
            int limit
    ) {
        LambdaQueryWrapper<RejectedTriggerAuditEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (scope == TriggerQuarantineScope.TRIGGER_USER) {
            queryWrapper.eq(RejectedTriggerAuditEntity::getTriggerUser, TriggerQuarantineConvert.normalizedScopeKey(scopeKey));
        } else {
            String[] repository = TriggerQuarantineConvert.normalizedScopeKey(scopeKey).split("/", 2);
            queryWrapper
                    .eq(RejectedTriggerAuditEntity::getRepositoryOwner, repository.length > 0 ? repository[0] : "")
                    .eq(RejectedTriggerAuditEntity::getRepositoryName, repository.length > 1 ? repository[1] : "");
        }
        queryWrapper
                .orderByDesc(RejectedTriggerAuditEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return auditMapper.selectList(queryWrapper).stream()
                .map(RejectedTriggerAuditConvert::toVo)
                .toList();
    }

    @Override
    public Optional<RejectedTriggerAuditVo> findRejectedTrigger(String id) {
        RejectedTriggerAuditEntity entity = auditMapper.selectById(id);
        return Optional.ofNullable(entity).map(RejectedTriggerAuditConvert::toVo);
    }

    @Override
    public RejectedTriggerAuditVo markRetried(String id, String taskId, Instant retriedAt) {
        RejectedTriggerAuditEntity updateEntity = new RejectedTriggerAuditEntity();
        updateEntity.setId(id);
        updateEntity.setRetriedTaskId(taskId);
        updateEntity.setRetriedAt(retriedAt);
        auditMapper.updateById(updateEntity);
        return findRejectedTrigger(id)
                .orElseThrow(() -> new IllegalArgumentException("Rejected trigger not found"));
    }
}
