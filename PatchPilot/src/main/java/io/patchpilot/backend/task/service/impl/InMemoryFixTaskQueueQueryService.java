package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;
import io.patchpilot.backend.task.service.FixTaskQueueQueryService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("default")
public class InMemoryFixTaskQueueQueryService implements FixTaskQueueQueryService {

    @Override
    public List<FixTaskQueueItemVo> listItems(FixTaskQueueItemStatus status) {
        return List.of();
    }

    @Override
    public FixTaskQueueSummaryVo summary() {
        return FixTaskQueueSummaryVo.empty();
    }
}
