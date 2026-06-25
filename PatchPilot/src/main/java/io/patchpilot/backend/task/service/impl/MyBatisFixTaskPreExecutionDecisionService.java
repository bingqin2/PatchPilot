package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskPreExecutionDecisionConvert;
import io.patchpilot.backend.task.domain.bo.RecordFixTaskPreExecutionDecisionCommand;
import io.patchpilot.backend.task.domain.entity.FixTaskPreExecutionDecisionEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskPreExecutionDecisionVo;
import io.patchpilot.backend.task.mapper.FixTaskPreExecutionDecisionMapper;
import io.patchpilot.backend.task.service.FixTaskPreExecutionDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisFixTaskPreExecutionDecisionService implements FixTaskPreExecutionDecisionService {

    private final FixTaskPreExecutionDecisionMapper decisionMapper;

    @Override
    public FixTaskPreExecutionDecisionVo recordDecision(RecordFixTaskPreExecutionDecisionCommand command) {
        FixTaskPreExecutionDecisionEntity entity = FixTaskPreExecutionDecisionConvert.newEntity(
                UUID.randomUUID().toString(),
                command
        );
        decisionMapper.insert(entity);
        return FixTaskPreExecutionDecisionConvert.toVo(entity);
    }

    @Override
    public Optional<FixTaskPreExecutionDecisionVo> findLatestDecision(String taskId) {
        LambdaQueryWrapper<FixTaskPreExecutionDecisionEntity> queryWrapper =
                new LambdaQueryWrapper<FixTaskPreExecutionDecisionEntity>()
                        .eq(FixTaskPreExecutionDecisionEntity::getTaskId, taskId);
        return decisionMapper.selectList(queryWrapper).stream()
                .max(Comparator.comparing(FixTaskPreExecutionDecisionEntity::getCreatedAt))
                .map(FixTaskPreExecutionDecisionConvert::toVo);
    }
}
