package io.patchpilot.backend.safety.domain;

public record SafetyGateDecision(boolean allowed, String reason, String category) {

    public static SafetyGateDecision accepted() {
        return new SafetyGateDecision(true, "Accepted", RejectedTriggerCategory.UNKNOWN);
    }

    public static SafetyGateDecision rejected(String reason) {
        return rejected(reason, RejectedTriggerCategory.UNKNOWN);
    }

    public static SafetyGateDecision rejected(String reason, String category) {
        return new SafetyGateDecision(false, reason, category);
    }
}
