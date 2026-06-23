package io.patchpilot.backend.task.domain.vo;

import java.util.List;

public record FixTaskDetailVo(
        FixTaskAuditSummaryVo summary,
        List<FixTaskTimelineEventVo> timeline,
        List<FixTaskTestRunVo> testRuns,
        List<FixTaskToolCallVo> toolCalls,
        List<FixTaskModelCallVo> modelCalls,
        FixTaskGeneratedDiffVo generatedDiff,
        FixTaskPatchReviewVo patchReview,
        IssueContextVo issueContext,
        FixTaskQueueItemVo queueItem,
        List<FixTaskQueueItemVo> queueItems,
        RepositorySupportGuidanceVo repositorySupportGuidance
) {
}
