import type {
  ApiResponse,
  FixTask,
  FixTaskAuditSummary,
  FixTaskMetricsSummary,
  FixTaskModelCall,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall
} from './types';

export async function listTasks(): Promise<FixTask[]> {
  return getApi<FixTask[]>('/api/tasks?limit=50');
}

export async function getMetricsSummary(): Promise<FixTaskMetricsSummary> {
  return getApi<FixTaskMetricsSummary>('/api/tasks/metrics/summary');
}

export async function getTaskSummary(taskId: string): Promise<FixTaskAuditSummary> {
  return getApi<FixTaskAuditSummary>(`/api/tasks/${taskId}/summary`);
}

export async function getTimeline(taskId: string): Promise<FixTaskTimelineEvent[]> {
  return getApi<FixTaskTimelineEvent[]>(`/api/tasks/${taskId}/timeline`);
}

export async function getTestRuns(taskId: string): Promise<FixTaskTestRun[]> {
  return getApi<FixTaskTestRun[]>(`/api/tasks/${taskId}/test-runs`);
}

export async function getToolCalls(taskId: string): Promise<FixTaskToolCall[]> {
  return getApi<FixTaskToolCall[]>(`/api/tasks/${taskId}/tool-calls`);
}

export async function getModelCalls(taskId: string): Promise<FixTaskModelCall[]> {
  return getApi<FixTaskModelCall[]>(`/api/tasks/${taskId}/model-calls`);
}

async function getApi<T>(path: string): Promise<T> {
  const response = await fetch(path);
  const body = (await response.json()) as ApiResponse<T>;
  if (!response.ok || !body.success) {
    throw new Error(body.message ?? `Request failed: ${response.status}`);
  }
  return body.data;
}
