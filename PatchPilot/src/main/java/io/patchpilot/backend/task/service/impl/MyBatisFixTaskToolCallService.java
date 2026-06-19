package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskToolCallConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskToolCallEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.mapper.FixTaskToolCallMapper;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Profile({"local", "docker"})
public class MyBatisFixTaskToolCallService implements FixTaskToolCallService {

    private final FixTaskToolCallMapper toolCallMapper;

    public MyBatisFixTaskToolCallService(FixTaskToolCallMapper toolCallMapper) {
        this.toolCallMapper = toolCallMapper;
    }

    @Override
    public FixTaskToolCallVo recordToolCall(
            String taskId,
            String toolName,
            String inputSummary,
            String outputSummary,
            boolean success,
            Instant startedAt,
            Instant finishedAt
    ) {
        FixTaskToolCallEntity entity = FixTaskToolCallConvert.newEntity(
                UUID.randomUUID().toString(),
                taskId,
                toolName,
                inputSummary,
                outputSummary,
                success,
                startedAt,
                finishedAt,
                FixTaskToolCallService.durationMs(startedAt, finishedAt)
        );
        toolCallMapper.insert(entity);
        return FixTaskToolCallConvert.toVo(entity);
    }

    @Override
    public List<FixTaskToolCallVo> listToolCalls(String taskId) {
        LambdaQueryWrapper<FixTaskToolCallEntity> queryWrapper = new LambdaQueryWrapper<FixTaskToolCallEntity>()
                .eq(FixTaskToolCallEntity::getTaskId, taskId);
        return toolCallMapper.selectList(queryWrapper).stream()
                .sorted(Comparator.comparing(FixTaskToolCallEntity::getStartedAt))
                .map(FixTaskToolCallConvert::toVo)
                .toList();
    }
}
