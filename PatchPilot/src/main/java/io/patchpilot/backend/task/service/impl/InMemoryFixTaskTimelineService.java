package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Profile("default")
public class InMemoryFixTaskTimelineService implements FixTaskTimelineService {

    private final List<FixTaskTimelineEventVo> events = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskTimelineEventVo recordEvent(String taskId, FixTaskTimelineEventType eventType, String message) {
        FixTaskTimelineEventVo event = new FixTaskTimelineEventVo(
                UUID.randomUUID().toString(),
                taskId,
                eventType,
                message,
                Instant.now()
        );
        events.add(event);
        return event;
    }

    @Override
    public List<FixTaskTimelineEventVo> listEvents(String taskId) {
        return events.stream()
                .filter(event -> event.taskId().equals(taskId))
                .sorted(Comparator.comparing(FixTaskTimelineEventVo::createdAt))
                .toList();
    }
}
