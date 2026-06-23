package io.patchpilot.backend.safety.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.safety.convert.OperatorSafetyAuditConvert;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditEntity;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.mapper.OperatorSafetyAuditMapper;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisOperatorSafetyAuditService implements OperatorSafetyAuditService {

    private final OperatorSafetyAuditMapper auditMapper;

    @Override
    public OperatorSafetyAuditVo recordSafetyAudit(RecordOperatorSafetyAuditCommand command) {
        OperatorSafetyAuditEntity entity = OperatorSafetyAuditConvert.newEntity(
                UUID.randomUUID().toString(),
                command,
                Instant.now()
        );
        auditMapper.insert(entity);
        return OperatorSafetyAuditConvert.toVo(entity);
    }

    @Override
    public List<OperatorSafetyAuditVo> listSafetyAudits(int limit) {
        LambdaQueryWrapper<OperatorSafetyAuditEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(OperatorSafetyAuditEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return auditMapper.selectList(queryWrapper).stream()
                .map(OperatorSafetyAuditConvert::toVo)
                .toList();
    }
}
