package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.CreateManualFixTaskCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;

public interface ManualFixTaskService {

    FixTaskVo createManualTask(CreateManualFixTaskCommand command);
}
