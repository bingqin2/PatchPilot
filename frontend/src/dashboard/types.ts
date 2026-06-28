import type {
  FixTaskAuditSummary,
  FixTaskModelCall,
  FixTaskQueueItem,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall,
  FixTaskGeneratedDiff,
  FixTaskPatchReview,
  FixTaskPreExecutionSafetySnapshot,
  FixTaskAdapterExecutionEvidence,
  FixTaskFailureDiagnosis,
  FixTaskRetryPreflight,
  FixTaskTriggerIntentAudit,
  IssueContext,
  RepositorySupportGuidance
} from '../types';

export interface TaskDetailState {
  summary: FixTaskAuditSummary | null;
  queueItem: FixTaskQueueItem | null;
  queueItems: FixTaskQueueItem[];
  timeline: FixTaskTimelineEvent[];
  testRuns: FixTaskTestRun[];
  toolCalls: FixTaskToolCall[];
  modelCalls: FixTaskModelCall[];
  triggerIntentAudit: FixTaskTriggerIntentAudit | null;
  preExecutionSafetySnapshot: FixTaskPreExecutionSafetySnapshot | null;
  generatedDiff: FixTaskGeneratedDiff | null;
  patchReview: FixTaskPatchReview | null;
  issueContext: IssueContext | null;
  failureDiagnosis: FixTaskFailureDiagnosis | null;
  retryPreflight: FixTaskRetryPreflight | null;
  adapterExecutionEvidence: FixTaskAdapterExecutionEvidence | null;
  repositorySupportGuidance: RepositorySupportGuidance | null;
}

export const emptyDetail: TaskDetailState = {
  summary: null,
  queueItem: null,
  queueItems: [],
  timeline: [],
  testRuns: [],
  toolCalls: [],
  modelCalls: [],
  triggerIntentAudit: null,
  preExecutionSafetySnapshot: null,
  generatedDiff: null,
  patchReview: null,
  issueContext: null,
  failureDiagnosis: null,
  retryPreflight: null,
  adapterExecutionEvidence: null,
  repositorySupportGuidance: null
};
