package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskFailureCauseSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskLatencySummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelUsageSummaryVo;

import java.util.List;

public interface FixTaskMetricsService {

    FixTaskMetricsSummaryVo summary();

    List<FixTaskFailureCauseSummaryVo> failureCauses();

    FixTaskModelUsageSummaryVo modelUsage();

    FixTaskLatencySummaryVo latency();
}
