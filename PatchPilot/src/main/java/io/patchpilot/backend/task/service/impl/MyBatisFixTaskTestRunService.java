package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskTestRunConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskTestRunEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.mapper.FixTaskTestRunMapper;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
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
public class MyBatisFixTaskTestRunService implements FixTaskTestRunService {

    private final FixTaskTestRunMapper testRunMapper;

    @Override
    public FixTaskTestRunVo recordTestRun(
            String taskId,
            String command,
            int exitCode,
            String output,
            Instant startedAt,
            Instant finishedAt
    ) {
        FixTaskTestRunEntity entity = FixTaskTestRunConvert.newEntity(
                UUID.randomUUID().toString(),
                taskId,
                command,
                exitCode,
                output,
                startedAt,
                finishedAt,
                FixTaskTestRunService.durationMs(startedAt, finishedAt)
        );
        testRunMapper.insert(entity);
        return FixTaskTestRunConvert.toVo(entity);
    }

    @Override
    public List<FixTaskTestRunVo> listTestRuns(String taskId) {
        LambdaQueryWrapper<FixTaskTestRunEntity> queryWrapper = new LambdaQueryWrapper<FixTaskTestRunEntity>()
                .eq(FixTaskTestRunEntity::getTaskId, taskId);
        return testRunMapper.selectList(queryWrapper).stream()
                .sorted(Comparator.comparing(FixTaskTestRunEntity::getStartedAt))
                .map(FixTaskTestRunConvert::toVo)
                .toList();
    }
}
