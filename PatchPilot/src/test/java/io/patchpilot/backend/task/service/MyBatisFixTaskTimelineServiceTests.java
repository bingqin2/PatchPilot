package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.entity.FixTaskTimelineEventEntity;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.mapper.FixTaskTimelineEventMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskTimelineService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskTimelineServiceTests {

    private final FixTaskTimelineEventMapper timelineEventMapper = mock(FixTaskTimelineEventMapper.class);
    private final FixTaskTimelineService timelineService = new MyBatisFixTaskTimelineService(timelineEventMapper);

    @Test
    void should_insert_timeline_event() {
        when(timelineEventMapper.insert(any(FixTaskTimelineEventEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskTimelineEventEntity> entityCaptor =
                ArgumentCaptor.forClass(FixTaskTimelineEventEntity.class);

        FixTaskTimelineEventVo event = timelineService.recordEvent(
                "task-123",
                FixTaskTimelineEventType.PR_CREATED,
                "Created PR"
        );

        verify(timelineEventMapper).insert(entityCaptor.capture());
        FixTaskTimelineEventEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isNotBlank();
        assertThat(insertedEntity.getTaskId()).isEqualTo("task-123");
        assertThat(insertedEntity.getEventType()).isEqualTo("PR_CREATED");
        assertThat(insertedEntity.getMessage()).isEqualTo("Created PR");
        assertThat(insertedEntity.getCreatedAt()).isNotNull();
        assertThat(event.id()).isEqualTo(insertedEntity.getId());
        assertThat(event.eventType()).isEqualTo(FixTaskTimelineEventType.PR_CREATED);
    }

    @Test
    void should_list_events_oldest_first() {
        FixTaskTimelineEventEntity newer = entity(
                "event-newer",
                "task-123",
                FixTaskTimelineEventType.COMPLETED,
                "Completed",
                Instant.parse("2026-06-19T08:05:00Z")
        );
        FixTaskTimelineEventEntity older = entity(
                "event-older",
                "task-123",
                FixTaskTimelineEventType.TASK_CREATED,
                "Created",
                Instant.parse("2026-06-19T08:00:00Z")
        );
        when(timelineEventMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<FixTaskTimelineEventVo> events = timelineService.listEvents("task-123");

        assertThat(events)
                .extracting(FixTaskTimelineEventVo::id)
                .containsExactly("event-older", "event-newer");
    }

    private static FixTaskTimelineEventEntity entity(
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
}
