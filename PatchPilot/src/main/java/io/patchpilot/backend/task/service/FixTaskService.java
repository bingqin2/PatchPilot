package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.util.List;
import java.util.Optional;

public interface FixTaskService extends FixTaskAdapterMetadataRecorder {

    FixTaskVo createFixTask(CreateFixTaskCommand command);

    default FixTaskCreationResult createFixTaskIfAbsent(CreateFixTaskCommand command) {
        return new FixTaskCreationResult(createFixTask(command), true);
    }

    FixTaskVo markRunning(String id);

    FixTaskVo markRunningTests(String id);

    default FixTaskVo markCompleted(String id) {
        return markCompleted(id, null);
    }

    FixTaskVo markCompleted(String id, String pullRequestUrl);

    FixTaskVo markFailed(String id, String failureReason);

    default FixTaskVo markCancelled(String id, String failureReason) {
        throw new UnsupportedOperationException("Task cancellation is not supported");
    }

    default FixTaskVo markPendingForRetry(String id) {
        throw new UnsupportedOperationException("Task retry is not supported");
    }

    FixTaskVo attachStatusComment(String id, long statusCommentId, String statusCommentUrl);

    @Override
    default FixTaskVo recordAdapterMetadata(
            String id,
            String language,
            String buildSystem,
            String verificationCommand,
            String adapterDetectionReason
    ) {
        throw new UnsupportedOperationException("Task adapter metadata is not supported");
    }

    List<FixTaskVo> listTasks();

    default List<FixTaskVo> listTasks(FixTaskListQuery query) {
        return listTasks();
    }

    default long countTasks(FixTaskListQuery query) {
        return listTasks(new FixTaskListQuery(
                query.query(),
                query.status(),
                query.repositoryOwner(),
                query.repositoryName(),
                query.language(),
                query.buildSystem(),
                Integer.MAX_VALUE,
                0
        )).size();
    }

    Optional<FixTaskVo> findTask(String id);

    Optional<FixTaskVo> findTaskByDeliveryId(String deliveryId);

    Optional<FixTaskVo> findActiveTaskForIssue(String repositoryOwner, String repositoryName, long issueNumber);
}
