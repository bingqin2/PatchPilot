package io.patchpilot.backend.safety.domain;

public record TriggerIntentDecision(
        TriggerIntentDecisionStatus status,
        String reason
) {

    public static TriggerIntentDecision shouldExecute(String reason) {
        return new TriggerIntentDecision(TriggerIntentDecisionStatus.SHOULD_EXECUTE, reason);
    }

    public static TriggerIntentDecision needsClarification(String reason) {
        return new TriggerIntentDecision(TriggerIntentDecisionStatus.NEEDS_CLARIFICATION, reason);
    }

    public static TriggerIntentDecision rejected(String reason) {
        return new TriggerIntentDecision(TriggerIntentDecisionStatus.REJECTED, reason);
    }

    public boolean shouldExecute() {
        return status == TriggerIntentDecisionStatus.SHOULD_EXECUTE;
    }

    public String rejectionReason() {
        if (status == TriggerIntentDecisionStatus.NEEDS_CLARIFICATION) {
            return "Model trigger classification needs clarification: " + reason;
        }
        if (status == TriggerIntentDecisionStatus.REJECTED) {
            return "Model trigger classification rejected: " + reason;
        }
        return reason;
    }

    public String rejectionCategory() {
        if (status == TriggerIntentDecisionStatus.NEEDS_CLARIFICATION) {
            return RejectedTriggerCategory.MODEL_NEEDS_CLARIFICATION;
        }
        if (status == TriggerIntentDecisionStatus.REJECTED && reason != null && reason.startsWith("Model trigger classification failed:")) {
            return RejectedTriggerCategory.MODEL_CLASSIFICATION_FAILED;
        }
        if (status == TriggerIntentDecisionStatus.REJECTED) {
            return RejectedTriggerCategory.MODEL_REJECTED;
        }
        return RejectedTriggerCategory.UNKNOWN;
    }
}
