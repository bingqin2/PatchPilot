package io.patchpilot.backend.task.executor;

import io.patchpilot.backend.task.domain.vo.FixTaskVo;

public interface FixTaskExecutor {

    void execute(FixTaskVo task);
}
