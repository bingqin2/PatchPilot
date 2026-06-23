package io.patchpilot.backend.safety.domain;

public record TriggerQuarantineDecision(boolean allowed, String reason, String category) {

    private static final String ALLOWED_REASON = "Trigger quarantine accepted";

    public static TriggerQuarantineDecision accepted() {
        return new TriggerQuarantineDecision(true, ALLOWED_REASON, RejectedTriggerCategory.UNKNOWN);
    }

    public static TriggerQuarantineDecision rejected(String reason) {
        return new TriggerQuarantineDecision(false, reason, RejectedTriggerCategory.ABUSE_QUARANTINED);
    }
}
