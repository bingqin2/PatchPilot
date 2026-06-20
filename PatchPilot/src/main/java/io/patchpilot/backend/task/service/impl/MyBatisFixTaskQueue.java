package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.patchpilot.backend.task.convert.FixTaskQueueItemConvert;
import io.patchpilot.backend.task.config.TaskQueueProperties;
import io.patchpilot.backend.task.domain.entity.FixTaskQueueItemEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.mapper.FixTaskQueueItemMapper;
import io.patchpilot.backend.task.service.FixTaskQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisFixTaskQueue implements FixTaskQueue {

    private final FixTaskQueueItemMapper queueItemMapper;
    private final TaskQueueProperties taskQueueProperties;

    @Override
    public void enqueue(String taskId) {
        Instant now = Instant.now();
        FixTaskQueueItemEntity entity = FixTaskQueueItemConvert.newPendingEntity(
                UUID.randomUUID().toString(),
                taskId,
                now
        );
        queueItemMapper.insert(entity);
    }

    public Optional<FixTaskQueueItemVo> claimNext() {
        Instant now = Instant.now();
        LambdaQueryWrapper<FixTaskQueueItemEntity> queryWrapper = new LambdaQueryWrapper<FixTaskQueueItemEntity>()
                .eq(FixTaskQueueItemEntity::getStatus, FixTaskQueueItemStatus.PENDING.name())
                .le(FixTaskQueueItemEntity::getAvailableAt, now);
        List<FixTaskQueueItemEntity> pendingItems = queueItemMapper.selectList(queryWrapper);
        Optional<FixTaskQueueItemEntity> nextItem = pendingItems.stream()
                .min(Comparator.comparing(FixTaskQueueItemEntity::getAvailableAt));
        if (nextItem.isEmpty()) {
            return Optional.empty();
        }
        FixTaskQueueItemEntity runningItem = FixTaskQueueItemConvert.replaceRunning(nextItem.get(), now);
        LambdaUpdateWrapper<FixTaskQueueItemEntity> updateWrapper = new LambdaUpdateWrapper<FixTaskQueueItemEntity>()
                .eq(FixTaskQueueItemEntity::getId, nextItem.get().getId())
                .eq(FixTaskQueueItemEntity::getStatus, FixTaskQueueItemStatus.PENDING.name())
                .le(FixTaskQueueItemEntity::getAvailableAt, now);
        int updatedRows = queueItemMapper.update(runningItem, updateWrapper);
        if (updatedRows != 1) {
            return Optional.empty();
        }
        return Optional.of(FixTaskQueueItemConvert.toVo(runningItem));
    }

    public void markCompleted(String queueItemId) {
        FixTaskQueueItemEntity currentItem = currentItem(queueItemId);
        queueItemMapper.updateById(FixTaskQueueItemConvert.replaceCompleted(currentItem, Instant.now()));
    }

    public void markFailed(String queueItemId, String failureReason) {
        FixTaskQueueItemEntity currentItem = currentItem(queueItemId);
        Instant now = Instant.now();
        if (currentItem.getAttemptCount() >= taskQueueProperties.getMaxAttempts()) {
            queueItemMapper.updateById(FixTaskQueueItemConvert.replaceFailed(currentItem, failureReason, now));
            return;
        }
        Instant nextAvailableAt = now.plusMillis(taskQueueProperties.getRetryDelayMs());
        queueItemMapper.updateById(FixTaskQueueItemConvert.replacePendingAfterFailure(
                currentItem,
                failureReason,
                nextAvailableAt,
                now
        ));
    }

    @Override
    public int cancelPendingForTask(String taskId) {
        Instant now = Instant.now();
        LambdaQueryWrapper<FixTaskQueueItemEntity> queryWrapper = new LambdaQueryWrapper<FixTaskQueueItemEntity>()
                .eq(FixTaskQueueItemEntity::getTaskId, taskId)
                .eq(FixTaskQueueItemEntity::getStatus, FixTaskQueueItemStatus.PENDING.name());
        List<FixTaskQueueItemEntity> pendingItems = queueItemMapper.selectList(queryWrapper);
        pendingItems.forEach(item -> queueItemMapper.updateById(FixTaskQueueItemConvert.replaceCancelled(
                item,
                "Task cancelled before execution",
                now
        )));
        return pendingItems.size();
    }

    public int recoverTimedOutRunningItems() {
        Instant now = Instant.now();
        Instant timeoutThreshold = now.minusMillis(taskQueueProperties.getVisibilityTimeoutMs());
        LambdaQueryWrapper<FixTaskQueueItemEntity> queryWrapper = new LambdaQueryWrapper<FixTaskQueueItemEntity>()
                .eq(FixTaskQueueItemEntity::getStatus, FixTaskQueueItemStatus.RUNNING.name())
                .le(FixTaskQueueItemEntity::getLockedAt, timeoutThreshold);
        List<FixTaskQueueItemEntity> timedOutItems = queueItemMapper.selectList(queryWrapper);
        timedOutItems.forEach(item -> queueItemMapper.updateById(
                FixTaskQueueItemConvert.replacePendingAfterTimeout(item, now, now)
        ));
        return timedOutItems.size();
    }

    private FixTaskQueueItemEntity currentItem(String queueItemId) {
        return Optional.ofNullable(queueItemMapper.selectById(queueItemId))
                .orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + queueItemId));
    }
}
