package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.enums.FixTaskQueueItemStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueItemVo;
import io.patchpilot.backend.task.domain.vo.FixTaskQueueSummaryVo;

import java.util.List;

public interface FixTaskQueueQueryService {

    List<FixTaskQueueItemVo> listItems(FixTaskQueueItemStatus status);

    FixTaskQueueSummaryVo summary();
}
