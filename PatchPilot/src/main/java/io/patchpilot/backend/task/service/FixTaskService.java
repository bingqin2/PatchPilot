package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

import java.util.List;
import java.util.Optional;

public interface FixTaskService {

    FixTaskVo createFixTask(CreateFixTaskCommand command);

    default FixTaskCreationResult createFixTaskIfAbsent(CreateFixTaskCommand command) {
        return new FixTaskCreationResult(createFixTask(command), true);
    }

    FixTaskVo markRunning(String id);

    FixTaskVo markRunningTests(String id);

    FixTaskVo markCompleted(String id);

    FixTaskVo markFailed(String id, String failureReason);

    List<FixTaskVo> listTasks();

    Optional<FixTaskVo> findTask(String id);
}
