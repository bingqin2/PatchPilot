package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.bo.FixTaskListQuery;
import io.patchpilot.backend.task.domain.vo.FixTaskMetricsSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskFailureCauseSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskLatencySummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelUsageSummaryVo;

import java.util.List;

public interface FixTaskMetricsService {

    FixTaskMetricsSummaryVo summary();

    FixTaskMetricsSummaryVo summary(FixTaskListQuery query);

    List<FixTaskFailureCauseSummaryVo> failureCauses();

    List<FixTaskFailureCauseSummaryVo> failureCauses(FixTaskListQuery query);

    FixTaskModelUsageSummaryVo modelUsage();

    FixTaskModelUsageSummaryVo modelUsage(FixTaskListQuery query);

    FixTaskLatencySummaryVo latency();

    FixTaskLatencySummaryVo latency(FixTaskListQuery query);
}
