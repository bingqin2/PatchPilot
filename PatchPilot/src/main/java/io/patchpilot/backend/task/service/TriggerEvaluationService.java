package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.EvaluateTriggerCommand;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;

public interface TriggerEvaluationService {

    TriggerEvaluationResultVo evaluate(EvaluateTriggerCommand command);
}
