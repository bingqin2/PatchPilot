package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
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
import java.util.stream.Stream;

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
                createdAt,
                null,
                null
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
                    completedAt,
                    currentTask.statusCommentId(),
                    currentTask.statusCommentUrl()
            );
        });
        return updatedTask;
    }

    @Override
    public FixTaskVo markFailed(String id, String failureReason) {
        return replaceStatus(id, FixTaskStatus.FAILED, failureReason);
    }

    @Override
    public FixTaskVo markCancelled(String id, String failureReason) {
        return replaceStatus(id, FixTaskStatus.CANCELLED, failureReason);
    }

    @Override
    public FixTaskVo markPendingForRetry(String id) {
        return replaceForRetry(id);
    }

    @Override
    public FixTaskVo attachStatusComment(String id, long statusCommentId, String statusCommentUrl) {
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
                    currentTask.status(),
                    currentTask.failureReason(),
                    currentTask.createdAt(),
                    currentTask.pullRequestUrl(),
                    currentTask.completedAt(),
                    Instant.now(),
                    statusCommentId,
                    statusCommentUrl
            );
        });
        return updatedTask;
    }

    @Override
    public List<FixTaskVo> listTasks() {
        return listTasks(FixTaskListQuery.all());
    }

    @Override
    public List<FixTaskVo> listTasks(FixTaskListQuery query) {
        return matchingTasks(query)
                .sorted(Comparator.comparing(FixTaskVo::createdAt).reversed())
                .skip(query.offset())
                .limit(query.limit())
                .toList();
    }

    @Override
    public long countTasks(FixTaskListQuery query) {
        return matchingTasks(query).count();
    }

    @Override
    public Optional<FixTaskVo> findTask(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<FixTaskVo> findTaskByDeliveryId(String deliveryId) {
        return tasks.values().stream()
                .filter(task -> task.deliveryId().equals(deliveryId))
                .findFirst();
    }

    @Override
    public Optional<FixTaskVo> findActiveTaskForIssue(String repositoryOwner, String repositoryName, long issueNumber) {
        return tasks.values().stream()
                .filter(task -> task.repositoryOwner().equals(repositoryOwner))
                .filter(task -> task.repositoryName().equals(repositoryName))
                .filter(task -> task.issueNumber() == issueNumber)
                .filter(task -> task.status().isActive())
                .max(Comparator.comparing(FixTaskVo::createdAt));
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
                    Instant.now(),
                    currentTask.statusCommentId(),
                    currentTask.statusCommentUrl()
            );
        });
        return updatedTask;
    }

    private FixTaskVo replaceForRetry(String id) {
        FixTaskVo updatedTask = tasks.compute(id, (taskId, currentTask) -> {
            if (currentTask == null) {
                throw new IllegalArgumentException("Task not found: " + taskId);
            }
            Instant updatedAt = Instant.now();
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
                    FixTaskStatus.PENDING,
                    null,
                    currentTask.createdAt(),
                    null,
                    null,
                    updatedAt,
                    currentTask.statusCommentId(),
                    currentTask.statusCommentUrl()
            );
        });
        return updatedTask;
    }

    private static boolean matchesQuery(FixTaskVo task, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String normalizedQuery = query.toLowerCase();
        String searchable = String.join(
                " ",
                task.id(),
                task.repositoryOwner(),
                task.repositoryName(),
                String.valueOf(task.issueNumber()),
                task.triggerUser(),
                task.triggerComment(),
                task.deliveryId(),
                task.failureReason() == null ? "" : task.failureReason(),
                task.pullRequestUrl() == null ? "" : task.pullRequestUrl()
        ).toLowerCase();
        return searchable.contains(normalizedQuery);
    }

    private Stream<FixTaskVo> matchingTasks(FixTaskListQuery query) {
        return tasks.values().stream()
                .filter(task -> query.status() == null || task.status() == query.status())
                .filter(task -> query.repositoryOwner() == null || task.repositoryOwner().equals(query.repositoryOwner()))
                .filter(task -> query.repositoryName() == null || task.repositoryName().equals(query.repositoryName()))
                .filter(task -> matchesQuery(task, query.query()));
    }
}
