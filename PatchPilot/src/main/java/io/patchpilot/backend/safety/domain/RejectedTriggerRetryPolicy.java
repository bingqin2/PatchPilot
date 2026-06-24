package io.patchpilot.backend.safety.domain;

import org.springframework.util.StringUtils;

public final class RejectedTriggerRetryPolicy {

    private RejectedTriggerRetryPolicy() {
    }

    public static boolean isRetryable(String category, String retriedTaskId) {
        return retryBlockedReason(category, retriedTaskId) == null;
    }

    public static String retryBlockedReason(String category, String retriedTaskId) {
        if (StringUtils.hasText(retriedTaskId)) {
            return "Rejected trigger has already been retried; open the linked retried task instead.";
        }
        return switch (categoryOrUnknown(category)) {
            case RejectedTriggerCategory.NOT_ACTIONABLE,
                 RejectedTriggerCategory.MODEL_REJECTED,
                 RejectedTriggerCategory.MODEL_NEEDS_CLARIFICATION,
                 RejectedTriggerCategory.MODEL_CLASSIFICATION_FAILED -> null;
            case RejectedTriggerCategory.DANGEROUS_INSTRUCTION ->
                    "Remove destructive or secret-related instructions and ask for a specific, safe code change.";
            case RejectedTriggerCategory.TRIGGER_USER_NOT_ALLOWED ->
                    "Add this GitHub user to the trigger allowlist, or have an allowed maintainer post a new `/agent fix` comment.";
            case RejectedTriggerCategory.REPOSITORY_NOT_ALLOWED ->
                    "Add this repository to the allowlist, or retry in an allowed repository.";
            case RejectedTriggerCategory.RATE_LIMITED ->
                    "Wait for the configured rate-limit window to reset before retrying.";
            case RejectedTriggerCategory.ABUSE_QUARANTINED ->
                    "Release the active trigger quarantine before retrying.";
            case RejectedTriggerCategory.EMPTY_COMMAND,
                 RejectedTriggerCategory.UNSUPPORTED_COMMAND ->
                    "Post a new `/agent fix <specific maintenance request>` comment.";
            default ->
                    "Review the rejection reason and create a new safe request instead of retrying this audit row.";
        };
    }

    private static String categoryOrUnknown(String category) {
        return StringUtils.hasText(category) ? category : RejectedTriggerCategory.UNKNOWN;
    }
}
