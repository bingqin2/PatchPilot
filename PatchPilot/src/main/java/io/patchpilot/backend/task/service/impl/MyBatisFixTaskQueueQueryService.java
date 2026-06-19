package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskQueueItemConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskQueueItemEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.mapper.FixTaskQueueItemMapper;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Profile({"local", "docker"})
public class MyBatisFixTaskQueueQueryService implements FixTaskQueueQueryService {

    private final FixTaskQueueItemMapper queueItemMapper;

    public MyBatisFixTaskQueueQueryService(FixTaskQueueItemMapper queueItemMapper) {
        this.queueItemMapper = queueItemMapper;
    }

    @Override
    public List<FixTaskQueueItemVo> listItems(FixTaskQueueItemStatus status) {
        LambdaQueryWrapper<FixTaskQueueItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            queryWrapper.eq(FixTaskQueueItemEntity::getStatus, status.name());
        }
        queryWrapper.orderByDesc(FixTaskQueueItemEntity::getCreatedAt);
        return queueItemMapper.selectList(queryWrapper).stream()
                .map(FixTaskQueueItemConvert::toVo)
                .toList();
    }

    @Override
    public FixTaskQueueSummaryVo summary() {
        Instant now = Instant.now();
        List<FixTaskQueueItemEntity> items = queueItemMapper.selectList(new LambdaQueryWrapper<>());
        long pendingCount = countByStatus(items, FixTaskQueueItemStatus.PENDING);
        long availablePendingCount = items.stream()
                .filter(item -> FixTaskQueueItemStatus.PENDING.name().equals(item.getStatus()))
                .filter(item -> !item.getAvailableAt().isAfter(now))
                .count();
        return new FixTaskQueueSummaryVo(
                items.size(),
                pendingCount,
                availablePendingCount,
                pendingCount - availablePendingCount,
                countByStatus(items, FixTaskQueueItemStatus.RUNNING),
                countByStatus(items, FixTaskQueueItemStatus.COMPLETED),
                countByStatus(items, FixTaskQueueItemStatus.FAILED)
        );
    }

    private static long countByStatus(List<FixTaskQueueItemEntity> items, FixTaskQueueItemStatus status) {
        return items.stream()
                .filter(item -> status.name().equals(item.getStatus()))
                .count();
    }
}
