package io.patchpilot.backend.safety.domain;

public record GeneratedDiffRiskDecision(boolean allowed, String reason) {

    public static GeneratedDiffRiskDecision accepted() {
        return new GeneratedDiffRiskDecision(true, "Generated diff passed risk checks");
    }

    public static GeneratedDiffRiskDecision rejected(String reason) {
        return new GeneratedDiffRiskDecision(false, "Generated diff rejected: " + reason);
    }
}
