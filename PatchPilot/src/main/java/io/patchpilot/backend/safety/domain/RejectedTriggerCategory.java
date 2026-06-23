package io.patchpilot.backend.safety.domain;

public final class RejectedTriggerCategory {

    public static final String UNKNOWN = "UNKNOWN";
    public static final String EMPTY_COMMAND = "EMPTY_COMMAND";
    public static final String UNSUPPORTED_COMMAND = "UNSUPPORTED_COMMAND";
    public static final String NOT_ACTIONABLE = "NOT_ACTIONABLE";
    public static final String DANGEROUS_INSTRUCTION = "DANGEROUS_INSTRUCTION";
    public static final String TRIGGER_USER_NOT_ALLOWED = "TRIGGER_USER_NOT_ALLOWED";
    public static final String REPOSITORY_NOT_ALLOWED = "REPOSITORY_NOT_ALLOWED";
    public static final String RATE_LIMITED = "RATE_LIMITED";
    public static final String MODEL_REJECTED = "MODEL_REJECTED";
    public static final String MODEL_NEEDS_CLARIFICATION = "MODEL_NEEDS_CLARIFICATION";
    public static final String MODEL_CLASSIFICATION_FAILED = "MODEL_CLASSIFICATION_FAILED";

    private RejectedTriggerCategory() {
    }
}
