package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Profile("default")
public class InMemoryFixTaskService implements FixTaskService {

    private final ConcurrentMap<String, FixTaskVo> tasks = new ConcurrentHashMap<>();

    @Override
    public FixTaskVo createFixTask(CreateFixTaskCommand command) {
        return createFixTaskIfAbsent(command).task();
    }

    @Override
    public FixTaskCreationResult createFixTaskIfAbsent(CreateFixTaskCommand command) {
        String taskId = UUID.randomUUID().toString();
        Instant createdAt = Instant.now();
        FixTaskVo task = new FixTaskVo(
                taskId,
                command.repositoryOwner(),
                command.repositoryName(),
                command.issueNumber(),
                command.installationId(),
                command.triggerUser(),
                command.triggerComment(),
                command.deliveryId(),
                command.commentId(),
                FixTaskStatus.PENDING,
                null,
                createdAt,
                null,
                null,
                createdAt
        );
        tasks.put(taskId, task);
        return new FixTaskCreationResult(task, true);
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
    public FixTaskVo markCompleted(String id, String pullRequestUrl) {
        Instant completedAt = Instant.now();
        FixTaskVo updatedTask = tasks.compute(id, (taskId, currentTask) -> {
            if (currentTask == null) {
                throw new IllegalArgumentException("Task not found: " + taskId);
            }
            return new FixTaskVo(
                    currentTask.id(),
                    currentTask.repositoryOwner(),
                    currentTask.repositoryName(),
                    currentTask.issueNumber(),
                    currentTask.installationId(),
                    currentTask.triggerUser(),
                    currentTask.triggerComment(),
                    currentTask.deliveryId(),
                    currentTask.commentId(),
                    FixTaskStatus.COMPLETED,
                    null,
                    currentTask.createdAt(),
                    pullRequestUrl,
                    completedAt,
                    completedAt
            );
        });
        return updatedTask;
    }

    @Override
    public FixTaskVo markFailed(String id, String failureReason) {
        return replaceStatus(id, FixTaskStatus.FAILED, failureReason);
    }

    @Override
    public List<FixTaskVo> listTasks() {
        return tasks.values().stream()
                .sorted(Comparator.comparing(FixTaskVo::createdAt).reversed())
                .toList();
    }

    @Override
    public Optional<FixTaskVo> findTask(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    private FixTaskVo replaceStatus(String id, FixTaskStatus status, String failureReason) {
        FixTaskVo updatedTask = tasks.compute(id, (taskId, currentTask) -> {
            if (currentTask == null) {
                throw new IllegalArgumentException("Task not found: " + taskId);
            }
            return new FixTaskVo(
                    currentTask.id(),
                    currentTask.repositoryOwner(),
                    currentTask.repositoryName(),
                    currentTask.issueNumber(),
                    currentTask.installationId(),
                    currentTask.triggerUser(),
                    currentTask.triggerComment(),
                    currentTask.deliveryId(),
                    currentTask.commentId(),
                    status,
                    failureReason,
                    currentTask.createdAt(),
                    currentTask.pullRequestUrl(),
                    currentTask.completedAt(),
                    Instant.now()
            );
        });
        return updatedTask;
    }
}
