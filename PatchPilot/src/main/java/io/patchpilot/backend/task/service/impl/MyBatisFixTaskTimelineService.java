package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskTimelineEventConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskTimelineEventEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.mapper.FixTaskTimelineEventMapper;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
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
public class MyBatisFixTaskTimelineService implements FixTaskTimelineService {

    private final FixTaskTimelineEventMapper timelineEventMapper;

    @Override
    public FixTaskTimelineEventVo recordEvent(String taskId, FixTaskTimelineEventType eventType, String message) {
        FixTaskTimelineEventEntity entity = FixTaskTimelineEventConvert.newEntity(
                UUID.randomUUID().toString(),
                taskId,
                eventType,
                message,
                Instant.now()
        );
        timelineEventMapper.insert(entity);
        return FixTaskTimelineEventConvert.toVo(entity);
    }

    @Override
    public List<FixTaskTimelineEventVo> listEvents(String taskId) {
        LambdaQueryWrapper<FixTaskTimelineEventEntity> queryWrapper =
                new LambdaQueryWrapper<FixTaskTimelineEventEntity>()
                        .eq(FixTaskTimelineEventEntity::getTaskId, taskId);
        return timelineEventMapper.selectList(queryWrapper).stream()
                .sorted(Comparator.comparing(FixTaskTimelineEventEntity::getCreatedAt))
                .map(FixTaskTimelineEventConvert::toVo)
                .toList();
    }
}
