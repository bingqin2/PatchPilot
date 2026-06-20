import type {
  FixTaskAuditSummary,
  FixTaskModelCall,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall
} from '../types';

export interface TaskDetailState {
  summary: FixTaskAuditSummary | null;
  timeline: FixTaskTimelineEvent[];
  testRuns: FixTaskTestRun[];
  toolCalls: FixTaskToolCall[];
  modelCalls: FixTaskModelCall[];
}

export const emptyDetail: TaskDetailState = {
  summary: null,
  timeline: [],
  testRuns: [],
  toolCalls: [],
  modelCalls: []
};
