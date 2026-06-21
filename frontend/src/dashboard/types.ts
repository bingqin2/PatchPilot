import type {
  FixTaskAuditSummary,
  FixTaskModelCall,
  FixTaskQueueItem,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall
} from '../types';

export interface TaskDetailState {
  summary: FixTaskAuditSummary | null;
  queueItem: FixTaskQueueItem | null;
  queueItems: FixTaskQueueItem[];
  timeline: FixTaskTimelineEvent[];
  testRuns: FixTaskTestRun[];
  toolCalls: FixTaskToolCall[];
  modelCalls: FixTaskModelCall[];
}

export const emptyDetail: TaskDetailState = {
  summary: null,
  queueItem: null,
  queueItems: [],
  timeline: [],
  testRuns: [],
  toolCalls: [],
  modelCalls: []
};
