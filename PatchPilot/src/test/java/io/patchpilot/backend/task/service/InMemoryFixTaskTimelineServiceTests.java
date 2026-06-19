package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskTimelineService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskTimelineServiceTests {

    private final FixTaskTimelineService timelineService = new InMemoryFixTaskTimelineService();

    @Test
    void should_record_and_list_events_oldest_first() {
        FixTaskTimelineEventVo created = timelineService.recordEvent(
                "task-123",
                FixTaskTimelineEventType.TASK_CREATED,
                "Task accepted"
        );
        FixTaskTimelineEventVo running = timelineService.recordEvent(
                "task-123",
                FixTaskTimelineEventType.RUNNING,
                "Task is running"
        );
        timelineService.recordEvent("task-other", FixTaskTimelineEventType.FAILED, "Other task failed");

        List<FixTaskTimelineEventVo> events = timelineService.listEvents("task-123");

        assertThat(created.id()).isNotBlank();
        assertThat(created.createdAt()).isNotNull();
        assertThat(running.createdAt()).isAfterOrEqualTo(created.createdAt());
        assertThat(events)
                .extracting(FixTaskTimelineEventVo::eventType)
                .containsExactly(FixTaskTimelineEventType.TASK_CREATED, FixTaskTimelineEventType.RUNNING);
        assertThat(events)
                .extracting(FixTaskTimelineEventVo::message)
                .containsExactly("Task accepted", "Task is running");
    }
}
