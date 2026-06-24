package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskConvert;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.entity.FixTaskEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskSort;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.mapper.FixTaskMapper;
import io.patchpilot.backend.task.service.FixTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisFixTaskService implements FixTaskService {

    private final FixTaskMapper fixTaskMapper;

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
    public FixTaskVo markCompleted(String id, String pullRequestUrl) {
        FixTaskEntity currentTask = Optional.ofNullable(fixTaskMapper.selectById(id))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        FixTaskEntity updatedTask = FixTaskConvert.replaceCompleted(currentTask, pullRequestUrl, Instant.now());
        fixTaskMapper.updateById(updatedTask);
        return FixTaskConvert.toVo(updatedTask);
    }

    @Override
    public FixTaskVo markFailed(String id, String failureReason) {
        return replaceStatus(id, FixTaskStatus.FAILED, failureReason);
    }

    @Override
    public FixTaskVo markPendingReview(String id, String failureReason) {
        return replaceStatus(id, FixTaskStatus.PENDING_REVIEW, failureReason);
    }

    @Override
    public FixTaskVo markCancelled(String id, String failureReason) {
        return replaceStatus(id, FixTaskStatus.CANCELLED, failureReason);
    }

    @Override
    public FixTaskVo markPendingForRetry(String id) {
        return markPendingForRetry(id, null);
    }

    @Override
    public FixTaskVo markPendingForRetry(String id, String retryReason) {
        FixTaskEntity currentTask = Optional.ofNullable(fixTaskMapper.selectById(id))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        FixTaskEntity updatedTask = FixTaskConvert.replacePendingForRetry(currentTask, Instant.now(), retryReason);
        fixTaskMapper.updateById(updatedTask);
        return FixTaskConvert.toVo(updatedTask);
    }

    @Override
    public FixTaskVo markPendingForReviewApproval(String id, String approvedBy, String approvalReason) {
        FixTaskEntity currentTask = Optional.ofNullable(fixTaskMapper.selectById(id))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        FixTaskEntity updatedTask = FixTaskConvert.replacePendingForReviewApproval(
                currentTask,
                Instant.now(),
                approvedBy,
                approvalReason
        );
        fixTaskMapper.updateById(updatedTask);
        return FixTaskConvert.toVo(updatedTask);
    }

    @Override
    public FixTaskVo attachStatusComment(String id, long statusCommentId, String statusCommentUrl) {
        FixTaskEntity currentTask = Optional.ofNullable(fixTaskMapper.selectById(id))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        FixTaskEntity updatedTask = FixTaskConvert.attachStatusComment(
                currentTask,
                statusCommentId,
                statusCommentUrl,
                Instant.now()
        );
        fixTaskMapper.updateById(updatedTask);
        return FixTaskConvert.toVo(updatedTask);
    }

    @Override
    public FixTaskVo recordAdapterMetadata(
            String id,
            String language,
            String buildSystem,
            String verificationCommand,
            String adapterDetectionReason
    ) {
        FixTaskEntity currentTask = Optional.ofNullable(fixTaskMapper.selectById(id))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        FixTaskEntity updatedTask = FixTaskConvert.attachAdapterMetadata(
                currentTask,
                language,
                buildSystem,
                verificationCommand,
                adapterDetectionReason,
                Instant.now()
        );
        fixTaskMapper.updateById(updatedTask);
        return FixTaskConvert.toVo(updatedTask);
    }

    @Override
    public List<FixTaskVo> listTasks() {
        return listTasks(FixTaskListQuery.all());
    }

    @Override
    public List<FixTaskVo> listTasks(FixTaskListQuery query) {
        LambdaQueryWrapper<FixTaskEntity> queryWrapper = taskListQueryWrapper(query);
        applySort(queryWrapper, query.sort());
        queryWrapper.last("LIMIT " + query.offset() + ", " + query.limit());
        return fixTaskMapper.selectList(queryWrapper).stream()
                .sorted(taskComparator(query.sort()))
                .map(FixTaskConvert::toVo)
                .toList();
    }

    @Override
    public long countTasks(FixTaskListQuery query) {
        return fixTaskMapper.selectCount(taskListQueryWrapper(query));
    }

    @Override
    public Optional<FixTaskVo> findTask(String id) {
        return Optional.ofNullable(fixTaskMapper.selectById(id))
                .map(FixTaskConvert::toVo);
    }

    @Override
    public Optional<FixTaskVo> findTaskByDeliveryId(String deliveryId) {
        return findByDeliveryId(deliveryId)
                .map(FixTaskConvert::toVo);
    }

    @Override
    public Optional<FixTaskVo> findActiveTaskForIssue(String repositoryOwner, String repositoryName, long issueNumber) {
        LambdaQueryWrapper<FixTaskEntity> queryWrapper = new LambdaQueryWrapper<FixTaskEntity>()
                .eq(FixTaskEntity::getRepositoryOwner, repositoryOwner)
                .eq(FixTaskEntity::getRepositoryName, repositoryName)
                .eq(FixTaskEntity::getIssueNumber, issueNumber)
                .in(FixTaskEntity::getStatus, activeStatusNames());
        return fixTaskMapper.selectList(queryWrapper).stream()
                .max(Comparator.comparing(FixTaskEntity::getCreatedAt))
                .map(FixTaskConvert::toVo);
    }

    private FixTaskVo replaceStatus(String id, FixTaskStatus status, String failureReason) {
        FixTaskEntity currentTask = Optional.ofNullable(fixTaskMapper.selectById(id))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        FixTaskEntity updatedTask = FixTaskConvert.replaceStatus(currentTask, status, failureReason, Instant.now());
        fixTaskMapper.updateById(updatedTask);
        return FixTaskConvert.toVo(updatedTask);
    }

    private Optional<FixTaskEntity> findByDeliveryId(String deliveryId) {
        LambdaQueryWrapper<FixTaskEntity> queryWrapper = new LambdaQueryWrapper<FixTaskEntity>()
                .eq(FixTaskEntity::getDeliveryId, deliveryId);
        return Optional.ofNullable(fixTaskMapper.selectOne(queryWrapper));
    }

    private static List<String> activeStatusNames() {
        return List.of(
                FixTaskStatus.PENDING.name(),
                FixTaskStatus.RUNNING.name(),
                FixTaskStatus.RUNNING_TESTS.name(),
                FixTaskStatus.PENDING_REVIEW.name()
        );
    }

    private static void addSearchConditions(LambdaQueryWrapper<FixTaskEntity> wrapper, String query) {
        String escapedQuery = escapeLike(query);
        String issueNumberPattern = "%" + escapedQuery + "%";
        wrapper.like(FixTaskEntity::getId, escapedQuery)
                .or()
                .like(FixTaskEntity::getRepositoryOwner, escapedQuery)
                .or()
                .like(FixTaskEntity::getRepositoryName, escapedQuery)
                .or()
                .apply("CAST(issue_number AS CHAR) LIKE {0}", issueNumberPattern)
                .or()
                .like(FixTaskEntity::getTriggerUser, escapedQuery)
                .or()
                .like(FixTaskEntity::getTriggerComment, escapedQuery)
                .or()
                .like(FixTaskEntity::getDeliveryId, escapedQuery)
                .or()
                .like(FixTaskEntity::getFailureReason, escapedQuery)
                .or()
                .like(FixTaskEntity::getPullRequestUrl, escapedQuery);
        wrapper.or()
                .like(FixTaskEntity::getLanguage, escapedQuery)
                .or()
                .like(FixTaskEntity::getBuildSystem, escapedQuery)
                .or()
                .like(FixTaskEntity::getVerificationCommand, escapedQuery)
                .or()
                .like(FixTaskEntity::getAdapterDetectionReason, escapedQuery);
        wrapper.or()
                .like(FixTaskEntity::getRiskReviewApprovedBy, escapedQuery)
                .or()
                .like(FixTaskEntity::getRiskReviewApprovalReason, escapedQuery);
    }

    private static LambdaQueryWrapper<FixTaskEntity> taskListQueryWrapper(FixTaskListQuery query) {
        return new LambdaQueryWrapper<FixTaskEntity>()
                .eq(query.status() != null, FixTaskEntity::getStatus, query.status() == null ? null : query.status().name())
                .eq(query.repositoryOwner() != null, FixTaskEntity::getRepositoryOwner, query.repositoryOwner())
                .eq(query.repositoryName() != null, FixTaskEntity::getRepositoryName, query.repositoryName())
                .eq(query.language() != null, FixTaskEntity::getLanguage, query.language())
                .eq(query.buildSystem() != null, FixTaskEntity::getBuildSystem, query.buildSystem())
                .ge(query.createdAfter() != null, FixTaskEntity::getCreatedAt, query.createdAfter())
                .le(query.createdBefore() != null, FixTaskEntity::getCreatedAt, query.createdBefore())
                .and(query.query() != null, wrapper -> addSearchConditions(wrapper, query.query()));
    }

    private static void applySort(LambdaQueryWrapper<FixTaskEntity> queryWrapper, FixTaskSort sort) {
        if (sort == FixTaskSort.CREATED_AT_ASC) {
            queryWrapper.orderByAsc(FixTaskEntity::getCreatedAt)
                    .orderByAsc(FixTaskEntity::getId);
            return;
        }
        queryWrapper.orderByDesc(FixTaskEntity::getCreatedAt)
                .orderByDesc(FixTaskEntity::getId);
    }

    private static Comparator<FixTaskEntity> taskComparator(FixTaskSort sort) {
        Comparator<FixTaskEntity> comparator = Comparator.comparing(FixTaskEntity::getCreatedAt)
                .thenComparing(FixTaskEntity::getId);
        return sort == FixTaskSort.CREATED_AT_ASC ? comparator : comparator.reversed();
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
