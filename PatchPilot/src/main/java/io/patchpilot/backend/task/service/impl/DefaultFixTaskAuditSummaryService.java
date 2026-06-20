package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskAuditSummaryVo;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.domain.vo.FixTaskTimelineEventVo;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.service.FixTaskAuditSummaryService;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTestRunService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.FixTaskToolCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultFixTaskAuditSummaryService implements FixTaskAuditSummaryService {

    private final FixTaskService fixTaskService;
    private final FixTaskTimelineService fixTaskTimelineService;
    private final FixTaskTestRunService fixTaskTestRunService;
    private final FixTaskToolCallService fixTaskToolCallService;
    private final FixTaskModelCallService fixTaskModelCallService;

    @Override
    public Optional<FixTaskAuditSummaryVo> summary(String taskId) {
        return fixTaskService.findTask(taskId)
                .map(this::summary);
    }

    private FixTaskAuditSummaryVo summary(FixTaskVo task) {
        List<FixTaskTimelineEventVo> timelineEvents = fixTaskTimelineService.listEvents(task.id());
        List<FixTaskTestRunVo> testRuns = fixTaskTestRunService.listTestRuns(task.id());
        List<FixTaskModelCallVo> modelCalls = fixTaskModelCallService.listModelCalls(task.id());
        FixTaskTestRunVo latestTestRun = latestTestRun(testRuns);
        return new FixTaskAuditSummaryVo(
                task,
                timelineEvents.size(),
                testRuns.size(),
                fixTaskToolCallService.listToolCalls(task.id()).size(),
                modelCalls.size(),
                totalModelTokens(modelCalls),
                latestTimelineEvent(timelineEvents),
                latestTestRun == null ? null : latestTestRun.exitCode(),
                latestTestRun == null ? null : latestTestRun.durationMs()
        );
    }

    private static long totalModelTokens(List<FixTaskModelCallVo> modelCalls) {
        return modelCalls.stream()
                .mapToLong(FixTaskModelCallVo::totalTokens)
                .sum();
    }

    private static FixTaskTimelineEventVo latestTimelineEvent(List<FixTaskTimelineEventVo> timelineEvents) {
        return timelineEvents.stream()
                .max(Comparator.comparing(FixTaskTimelineEventVo::createdAt))
                .orElse(null);
    }

    private static FixTaskTestRunVo latestTestRun(List<FixTaskTestRunVo> testRuns) {
        return testRuns.stream()
                .max(Comparator.comparing(FixTaskTestRunVo::finishedAt))
                .orElse(null);
    }
}
