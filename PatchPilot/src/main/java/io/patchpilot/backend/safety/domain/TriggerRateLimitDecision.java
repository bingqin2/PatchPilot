package io.patchpilot.backend.safety.domain;

public record TriggerRateLimitDecision(boolean allowed, String reason, String category) {

    private static final String ALLOWED_REASON = "Trigger rate limit accepted";

    public static TriggerRateLimitDecision accepted() {
        return new TriggerRateLimitDecision(true, ALLOWED_REASON, RejectedTriggerCategory.UNKNOWN);
    }

    public static TriggerRateLimitDecision rejected(String reason) {
        return new TriggerRateLimitDecision(false, reason, RejectedTriggerCategory.RATE_LIMITED);
    }
}
