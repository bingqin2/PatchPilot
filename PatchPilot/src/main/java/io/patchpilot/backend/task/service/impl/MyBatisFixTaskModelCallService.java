package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskModelCallConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskModelCallEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.mapper.FixTaskModelCallMapper;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Profile({"local", "docker"})
@RequiredArgsConstructor
public class MyBatisFixTaskModelCallService implements FixTaskModelCallService {

    private final FixTaskModelCallMapper modelCallMapper;

    @Override
    public FixTaskModelCallVo recordModelCall(
            String taskId,
            String provider,
            String model,
            String promptSummary,
            String responseSummary,
            int promptTokens,
            int completionTokens,
            boolean success,
            String errorMessage,
            Instant startedAt,
            Instant finishedAt
    ) {
        FixTaskModelCallEntity entity = FixTaskModelCallConvert.newEntity(
                UUID.randomUUID().toString(),
                taskId,
                provider,
                model,
                promptSummary,
                responseSummary,
                promptTokens,
                completionTokens,
                success,
                errorMessage,
                startedAt,
                finishedAt,
                FixTaskModelCallService.durationMs(startedAt, finishedAt)
        );
        modelCallMapper.insert(entity);
        return FixTaskModelCallConvert.toVo(entity);
    }

    @Override
    public List<FixTaskModelCallVo> listModelCalls(String taskId) {
        LambdaQueryWrapper<FixTaskModelCallEntity> queryWrapper = new LambdaQueryWrapper<FixTaskModelCallEntity>()
                .eq(FixTaskModelCallEntity::getTaskId, taskId);
        return modelCallMapper.selectList(queryWrapper).stream()
                .sorted(Comparator.comparing(FixTaskModelCallEntity::getStartedAt))
                .map(FixTaskModelCallConvert::toVo)
                .toList();
    }
}
