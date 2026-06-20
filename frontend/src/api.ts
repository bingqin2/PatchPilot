import type {
  ApiResponse,
  FixTask,
  FixTaskFailureCauseSummary,
  FixTaskPage,
  FixTaskAuditSummary,
  FixTaskMetricsSummary,
  FixTaskModelUsageSummary,
  FixTaskQueueItem,
  FixTaskQueueSummary,
  FixTaskModelCall,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall,
  TaskStatusFilter
} from './types';

interface ListTasksOptions {
  status?: TaskStatusFilter;
  query?: string;
  limit?: number;
  offset?: number;
}

export async function listTasks(options: TaskStatusFilter | ListTasksOptions = 'ALL'): Promise<FixTaskPage> {
  const normalizedOptions = typeof options === 'string' ? { status: options } : options;
  const searchParams = new URLSearchParams({ limit: String(normalizedOptions.limit ?? 50) });
  if (normalizedOptions.offset !== undefined) {
    searchParams.set('offset', String(normalizedOptions.offset));
  }
  if (normalizedOptions.query?.trim()) {
    searchParams.set('query', normalizedOptions.query.trim());
  }
  const status = normalizedOptions.status ?? 'ALL';
  if (status !== 'ALL') {
    searchParams.set('status', status);
  }
  return getApi<FixTaskPage>(`/api/tasks?${searchParams.toString()}`);
}

export async function getMetricsSummary(): Promise<FixTaskMetricsSummary> {
  return getApi<FixTaskMetricsSummary>('/api/tasks/metrics/summary');
}

export async function getFailureCauseSummary(): Promise<FixTaskFailureCauseSummary[]> {
  return getApi<FixTaskFailureCauseSummary[]>('/api/tasks/metrics/failure-causes');
}

export async function getModelUsageSummary(): Promise<FixTaskModelUsageSummary> {
  return getApi<FixTaskModelUsageSummary>('/api/tasks/metrics/model-usage');
}

export async function getQueueSummary(): Promise<FixTaskQueueSummary> {
  return getApi<FixTaskQueueSummary>('/api/task-queue/summary');
}

export async function listQueueItems(): Promise<FixTaskQueueItem[]> {
  return getApi<FixTaskQueueItem[]>('/api/task-queue/items');
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

export async function cancelTask(taskId: string): Promise<FixTask> {
  return postApi<FixTask>(`/api/tasks/${taskId}/cancel`);
}

export async function retryTask(taskId: string): Promise<FixTask> {
  return postApi<FixTask>(`/api/tasks/${taskId}/retry`);
}

async function getApi<T>(path: string): Promise<T> {
  const response = await fetch(path);
  const body = (await response.json()) as ApiResponse<T>;
  if (!response.ok || !body.success) {
    throw new Error(body.message ?? `Request failed: ${response.status}`);
  }
  return body.data;
}

async function postApi<T>(path: string): Promise<T> {
  const response = await fetch(path, { method: 'POST' });
  const body = (await response.json()) as ApiResponse<T>;
  if (!response.ok || !body.success) {
    throw new Error(body.message ?? `Request failed: ${response.status}`);
  }
  return body.data;
}
