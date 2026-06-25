package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.bo.RecordFixTaskPreExecutionDecisionCommand;
import io.patchpilot.backend.task.domain.entity.FixTaskPreExecutionDecisionEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskPreExecutionDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;

public final class FixTaskPreExecutionDecisionConvert {

    private FixTaskPreExecutionDecisionConvert() {
    }

    public static FixTaskPreExecutionDecisionEntity newEntity(
            String id,
            RecordFixTaskPreExecutionDecisionCommand command
    ) {
        FixTaskPreExecutionDecisionEntity entity = new FixTaskPreExecutionDecisionEntity();
        entity.setId(id);
        entity.setTaskId(command.taskId());
        entity.setSource(command.source());
        entity.setFinalDecision(command.finalDecision());
        entity.setSafetyAllowed(command.safetyDecision().allowed());
        entity.setSafetyReason(command.safetyDecision().reason());
        entity.setSafetyCategory(command.safetyDecision().category());
        entity.setActiveTaskAllowed(command.activeTaskDecision().allowed());
        entity.setActiveTaskReason(command.activeTaskDecision().reason());
        entity.setActiveTaskCategory(command.activeTaskDecision().category());
        entity.setQuarantineAllowed(command.quarantineDecision().allowed());
        entity.setQuarantineReason(command.quarantineDecision().reason());
        entity.setQuarantineCategory(command.quarantineDecision().category());
        entity.setRateLimitAllowed(command.rateLimitDecision().allowed());
        entity.setRateLimitReason(command.rateLimitDecision().reason());
        entity.setRateLimitCategory(command.rateLimitDecision().category());
        entity.setTriggerIntentAllowed(command.triggerIntentDecision().allowed());
        entity.setTriggerIntentReason(command.triggerIntentDecision().reason());
        entity.setTriggerIntentCategory(command.triggerIntentDecision().category());
        entity.setIssueContextLoaded(command.issueContextLoaded());
        entity.setCreatedAt(command.createdAt());
        return entity;
    }

    public static FixTaskPreExecutionDecisionVo toVo(FixTaskPreExecutionDecisionEntity entity) {
        return new FixTaskPreExecutionDecisionVo(
                entity.getId(),
                entity.getTaskId(),
                entity.getSource(),
                entity.getFinalDecision(),
                decision(entity.getSafetyAllowed(), entity.getSafetyReason(), entity.getSafetyCategory()),
                decision(entity.getActiveTaskAllowed(), entity.getActiveTaskReason(), entity.getActiveTaskCategory()),
                decision(entity.getQuarantineAllowed(), entity.getQuarantineReason(), entity.getQuarantineCategory()),
                decision(entity.getRateLimitAllowed(), entity.getRateLimitReason(), entity.getRateLimitCategory()),
                decision(entity.getTriggerIntentAllowed(), entity.getTriggerIntentReason(), entity.getTriggerIntentCategory()),
                Boolean.TRUE.equals(entity.getIssueContextLoaded()),
                entity.getCreatedAt()
        );
    }

    private static TriggerEvaluationDecisionVo decision(Boolean allowed, String reason, String category) {
        return new TriggerEvaluationDecisionVo(Boolean.TRUE.equals(allowed), reason, category);
    }
}
