package io.patchpilot.backend.task.domain.enums;

public enum FixTaskTimelineEventType {
    TRIGGER_ACCEPTED,
    TASK_CREATED,
    STATUS_COMMENT_CREATED,
    STATUS_COMMENT_FAILED,
    ACTIVE_TASK_EXISTS,
    RUNNING,
    RUNNING_TESTS,
    PENDING_REVIEW,
    REVIEW_APPROVED,
    PR_CREATED,
    COMPLETED,
    FAILED,
    CANCELLED,
    REQUEUED
}
