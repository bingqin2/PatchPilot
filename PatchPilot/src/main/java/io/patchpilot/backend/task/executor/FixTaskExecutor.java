package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.executor.domain.FixTaskExecutionResult;

public interface FixTaskExecutor {

    FixTaskExecutionResult execute(FixTaskVo task);
}
