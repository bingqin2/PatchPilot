package io.patchpilot.backend.task.domain.enums;

public enum FixTaskTimelineEventType {
    TASK_CREATED,
    STATUS_COMMENT_CREATED,
    ACTIVE_TASK_EXISTS,
    RUNNING,
    RUNNING_TESTS,
    PR_CREATED,
    COMPLETED,
    FAILED
}
