package io.patchpilot.backend.task.domain.enums;

public enum FixTaskTimelineEventType {
    TASK_CREATED,
    STATUS_COMMENT_CREATED,
    STATUS_COMMENT_FAILED,
    ACTIVE_TASK_EXISTS,
    RUNNING,
    RUNNING_TESTS,
    PENDING_REVIEW,
    PR_CREATED,
    COMPLETED,
    FAILED,
    CANCELLED,
    REQUEUED
}
