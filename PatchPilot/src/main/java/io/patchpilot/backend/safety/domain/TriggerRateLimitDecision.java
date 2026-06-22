package io.patchpilot.backend.safety.domain;

public record TriggerRateLimitDecision(boolean allowed, String reason) {

    private static final String ALLOWED_REASON = "Trigger rate limit accepted";

    public static TriggerRateLimitDecision accepted() {
        return new TriggerRateLimitDecision(true, ALLOWED_REASON);
    }

    public static TriggerRateLimitDecision rejected(String reason) {
        return new TriggerRateLimitDecision(false, reason);
    }
}
