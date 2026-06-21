package io.patchpilot.backend.safety.domain;

public record SafetyGateDecision(boolean allowed, String reason) {

    public static SafetyGateDecision accepted() {
        return new SafetyGateDecision(true, "Accepted");
    }

    public static SafetyGateDecision rejected(String reason) {
        return new SafetyGateDecision(false, reason);
    }
}
