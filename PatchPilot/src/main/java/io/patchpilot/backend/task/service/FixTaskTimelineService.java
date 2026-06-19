package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;

import java.util.List;

public interface FixTaskTimelineService {

    FixTaskTimelineEventVo recordEvent(String taskId, FixTaskTimelineEventType eventType, String message);

    List<FixTaskTimelineEventVo> listEvents(String taskId);
}
