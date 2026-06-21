package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Profile("default")
public class InMemoryFixTaskQueueQueryService implements FixTaskQueueQueryService {

    @Override
    public List<FixTaskQueueItemVo> listItems(FixTaskQueueItemStatus status) {
        return List.of();
    }

    @Override
    public List<FixTaskQueueItemVo> listByTaskId(String taskId) {
        return List.of();
    }

    @Override
    public Optional<FixTaskQueueItemVo> findByTaskId(String taskId) {
        return Optional.empty();
    }

    @Override
    public FixTaskQueueSummaryVo summary() {
        return FixTaskQueueSummaryVo.empty();
    }
}
