package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskConvert;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.entity.FixTaskEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.mapper.FixTaskMapper;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile({"local", "docker"})
public class MyBatisFixTaskService implements FixTaskService {

    private final FixTaskMapper fixTaskMapper;

    public MyBatisFixTaskService(FixTaskMapper fixTaskMapper) {
        this.fixTaskMapper = fixTaskMapper;
    }

    @Override
    public FixTaskVo createFixTask(CreateFixTaskCommand command) {
        return createFixTaskIfAbsent(command).task();
    }

    @Override
    public FixTaskCreationResult createFixTaskIfAbsent(CreateFixTaskCommand command) {
        Optional<FixTaskEntity> existingTask = findByDeliveryId(command.deliveryId());
        if (existingTask.isPresent()) {
            return new FixTaskCreationResult(FixTaskConvert.toVo(existingTask.get()), false);
        }

        FixTaskEntity entity = FixTaskConvert.newEntity(UUID.randomUUID().toString(), command, Instant.now());
        try {
            fixTaskMapper.insert(entity);
        } catch (DuplicateKeyException exception) {
            return new FixTaskCreationResult(FixTaskConvert.toVo(findByDeliveryId(command.deliveryId())
                    .orElseThrow(() -> exception)), false);
        }
        return new FixTaskCreationResult(FixTaskConvert.toVo(entity), true);
    }

    @Override
    public FixTaskVo markRunning(String id) {
        return replaceStatus(id, FixTaskStatus.RUNNING, null);
    }

    @Override
    public FixTaskVo markRunningTests(String id) {
        return replaceStatus(id, FixTaskStatus.RUNNING_TESTS, null);
    }

    @Override
    public FixTaskVo markCompleted(String id) {
        return replaceStatus(id, FixTaskStatus.COMPLETED, null);
    }

    @Override
    public FixTaskVo markFailed(String id, String failureReason) {
        return replaceStatus(id, FixTaskStatus.FAILED, failureReason);
    }

    @Override
    public List<FixTaskVo> listTasks() {
        return fixTaskMapper.selectList(null).stream()
                .sorted(Comparator.comparing(FixTaskEntity::getCreatedAt).reversed())
                .map(FixTaskConvert::toVo)
                .toList();
    }

    @Override
    public Optional<FixTaskVo> findTask(String id) {
        return Optional.ofNullable(fixTaskMapper.selectById(id))
                .map(FixTaskConvert::toVo);
    }

    private FixTaskVo replaceStatus(String id, FixTaskStatus status, String failureReason) {
        FixTaskEntity currentTask = Optional.ofNullable(fixTaskMapper.selectById(id))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        FixTaskEntity updatedTask = FixTaskConvert.replaceStatus(currentTask, status, failureReason);
        fixTaskMapper.updateById(updatedTask);
        return FixTaskConvert.toVo(updatedTask);
    }

    private Optional<FixTaskEntity> findByDeliveryId(String deliveryId) {
        LambdaQueryWrapper<FixTaskEntity> queryWrapper = new LambdaQueryWrapper<FixTaskEntity>()
                .eq(FixTaskEntity::getDeliveryId, deliveryId);
        return Optional.ofNullable(fixTaskMapper.selectOne(queryWrapper));
    }
}
