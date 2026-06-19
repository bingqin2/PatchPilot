package io.patchpilot.backend.task.domain.vo;

import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;

import java.time.Instant;

public record FixTaskTimelineEventVo(
        String id,
        String taskId,
        FixTaskTimelineEventType eventType,
        String message,
        Instant createdAt
) {
}
