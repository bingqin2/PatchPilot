package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskTimelineEventEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;

import java.time.Instant;

public final class FixTaskTimelineEventConvert {

    private FixTaskTimelineEventConvert() {
    }

    public static FixTaskTimelineEventEntity newEntity(
            String id,
            String taskId,
            FixTaskTimelineEventType eventType,
            String message,
            Instant createdAt
    ) {
        FixTaskTimelineEventEntity entity = new FixTaskTimelineEventEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setEventType(eventType.name());
        entity.setMessage(message);
        entity.setCreatedAt(createdAt);
        return entity;
    }

    public static FixTaskTimelineEventVo toVo(FixTaskTimelineEventEntity entity) {
        return new FixTaskTimelineEventVo(
                entity.getId(),
                entity.getTaskId(),
                FixTaskTimelineEventType.valueOf(entity.getEventType()),
                entity.getMessage(),
                entity.getCreatedAt()
        );
    }
}
