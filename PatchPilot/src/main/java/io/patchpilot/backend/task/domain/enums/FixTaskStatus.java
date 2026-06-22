package io.patchpilot.backend.task.domain.enums;

public enum FixTaskStatus {
    PENDING,
    RUNNING,
    RUNNING_TESTS,
    PENDING_REVIEW,
    COMPLETED,
    FAILED,
    CANCELLED;

    public boolean isActive() {
        return this == PENDING || this == RUNNING || this == RUNNING_TESTS || this == PENDING_REVIEW;
    }
}
