package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;

public final class TriggerDecisionEvidenceFormatter {

    private static final String MODEL_DISABLED_REASON = "Model trigger classification is disabled";

    private TriggerDecisionEvidenceFormatter() {
    }

    public static String accepted(
            SafetyGateDecision safetyDecision,
            TriggerIntentDecision triggerIntentDecision,
            boolean issueContextLoaded
    ) {
        return "Trigger accepted: "
                + safetySummary(safetyDecision)
                + "; "
                + issueContextSummary(issueContextLoaded)
                + "; "
                + modelSummary(triggerIntentDecision);
    }

    private static String safetySummary(SafetyGateDecision safetyDecision) {
        if (safetyDecision.allowed()) {
            return "safety gate accepted";
        }
        if (RejectedTriggerCategory.NOT_ACTIONABLE.equals(safetyDecision.category())) {
            return "safety gate requested issue-context classification because "
                    + "command is too short to describe a concrete code change";
        }
        return "safety gate returned " + safetyDecision.category() + ": " + normalizedReason(safetyDecision.reason());
    }

    private static String issueContextSummary(boolean issueContextLoaded) {
        return issueContextLoaded ? "issue context loaded" : "issue context not loaded";
    }

    private static String modelSummary(TriggerIntentDecision triggerIntentDecision) {
        if (triggerIntentDecision.shouldExecute() && MODEL_DISABLED_REASON.equals(triggerIntentDecision.reason())) {
            return "model trigger classification disabled";
        }
        if (triggerIntentDecision.shouldExecute()) {
            return "model accepted trigger: " + normalizedReason(triggerIntentDecision.reason());
        }
        return "model did not accept trigger: " + normalizedReason(triggerIntentDecision.rejectionReason());
    }

    private static String normalizedReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "no reason provided";
        }
        String normalized = reason.trim();
        while (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }
        return normalized;
    }
}
