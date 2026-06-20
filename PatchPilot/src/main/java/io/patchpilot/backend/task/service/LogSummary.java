package io.patchpilot.backend.task.service;

public final class LogSummary {

    public static final int MAX_TEXT_COLUMN_CHARS = 60_000;
    public static final int MAX_TEST_RUN_OUTPUT_CHARS = 1_000_000;
    public static final int MAX_FAILURE_REASON_CHARS = 2_000;

    private LogSummary() {
    }

    public static String truncateForTextColumn(String value) {
        return truncate(value, MAX_TEXT_COLUMN_CHARS);
    }

    public static String truncateTestRunOutput(String value) {
        return truncate(value, MAX_TEST_RUN_OUTPUT_CHARS);
    }

    public static String truncateFailureReason(String value) {
        return truncate(value, MAX_FAILURE_REASON_CHARS);
    }

    private static String truncate(String value, int maxChars) {
        if (value == null || value.length() <= maxChars) {
            return value;
        }
        String marker = "\n...[truncated " + (value.length() - maxChars) + " chars]";
        int keepChars = Math.max(0, maxChars - marker.length());
        return value.substring(0, keepChars) + marker;
    }
}
