package io.patchpilot.backend.task.domain.vo;

import java.util.List;

public record FixTaskDetailVo(
        FixTaskAuditSummaryVo summary,
        List<FixTaskTimelineEventVo> timeline,
        List<FixTaskTestRunVo> testRuns,
        List<FixTaskToolCallVo> toolCalls,
        List<FixTaskModelCallVo> modelCalls,
        FixTaskTriggerIntentAuditVo triggerIntentAudit,
        FixTaskPreExecutionSafetySnapshotVo preExecutionSafetySnapshot,
        FixTaskGeneratedDiffVo generatedDiff,
        FixTaskPatchReviewVo patchReview,
        IssueContextVo issueContext,
        FixTaskFailureDiagnosisVo failureDiagnosis,
        FixTaskQueueItemVo queueItem,
        List<FixTaskQueueItemVo> queueItems,
        FixTaskAdapterExecutionEvidenceVo adapterExecutionEvidence,
        RepositorySupportGuidanceVo repositorySupportGuidance
) {
}
