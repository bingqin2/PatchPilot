package io.patchpilot.backend.task.domain.enums;

public enum FixTaskStatus {
    PENDING,
    RUNNING,
    RUNNING_TESTS,
    COMPLETED,
    FAILED;

    public boolean isActive() {
        return this == PENDING || this == RUNNING || this == RUNNING_TESTS;
    }
}
