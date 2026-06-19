package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskTimelineEventEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskTimelineEventConvertTests {

    @Test
    void should_create_entity_and_convert_to_vo() {
        Instant createdAt = Instant.parse("2026-06-19T08:00:00Z");

        FixTaskTimelineEventEntity entity = FixTaskTimelineEventConvert.newEntity(
                "event-123",
                "task-123",
                FixTaskTimelineEventType.RUNNING_TESTS,
                "Running Maven tests",
                createdAt
        );
        FixTaskTimelineEventVo vo = FixTaskTimelineEventConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("event-123");
        assertThat(entity.getTaskId()).isEqualTo("task-123");
        assertThat(entity.getEventType()).isEqualTo("RUNNING_TESTS");
        assertThat(entity.getMessage()).isEqualTo("Running Maven tests");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);

        assertThat(vo.id()).isEqualTo("event-123");
        assertThat(vo.taskId()).isEqualTo("task-123");
        assertThat(vo.eventType()).isEqualTo(FixTaskTimelineEventType.RUNNING_TESTS);
        assertThat(vo.message()).isEqualTo("Running Maven tests");
        assertThat(vo.createdAt()).isEqualTo(createdAt);
    }
}
