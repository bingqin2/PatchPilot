package io.patchpilot.backend.task.domain.enums;

public enum TriggerEvaluationSource {
    MANUAL("manual"),
    ISSUE_COMMENT("issue_comment");

    private final String gateSource;

    TriggerEvaluationSource(String gateSource) {
        this.gateSource = gateSource;
    }

    public String gateSource() {
        return gateSource;
    }

    public static TriggerEvaluationSource parse(String value) {
        if (value == null || value.isBlank()) {
            return MANUAL;
        }
        for (TriggerEvaluationSource source : values()) {
            if (source.name().equalsIgnoreCase(value.trim())) {
                return source;
            }
        }
        throw new IllegalArgumentException("source must be MANUAL or ISSUE_COMMENT");
    }
}
