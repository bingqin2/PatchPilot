package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.RecordFixTaskPreExecutionDecisionCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskPreExecutionDecisionVo;

import java.util.List;
import java.util.Optional;

public interface FixTaskPreExecutionDecisionService {

    FixTaskPreExecutionDecisionVo recordDecision(RecordFixTaskPreExecutionDecisionCommand command);

    Optional<FixTaskPreExecutionDecisionVo> findLatestDecision(String taskId);

    List<FixTaskPreExecutionDecisionVo> listRecentDecisions(int limit);
}
