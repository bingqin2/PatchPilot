import type {
  FixTaskAuditSummary,
  FixTaskModelCall,
  FixTaskQueueItem,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall,
  FixTaskGeneratedDiff,
  FixTaskPatchReview,
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
  generatedDiff: FixTaskGeneratedDiff | null;
  patchReview: FixTaskPatchReview | null;
  issueContext: IssueContext | null;
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
  generatedDiff: null,
  patchReview: null,
  issueContext: null,
  repositorySupportGuidance: null
};
