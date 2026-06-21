import type {
  ApiResponse,
  BackendHealth,
  ConfigurationSummary,
  FixTask,
  FixTaskFailureCauseSummary,
  FixTaskLatencySummary,
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

const backendConnectionError =
  'Backend request failed. Check that PatchPilot backend is running and the frontend proxy target is correct.';

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

export async function getLatencySummary(): Promise<FixTaskLatencySummary> {
  return getApi<FixTaskLatencySummary>('/api/tasks/metrics/latency');
}

export async function getConfigurationSummary(): Promise<ConfigurationSummary> {
  return getApi<ConfigurationSummary>('/api/configuration/summary');
}

export async function getBackendHealth(): Promise<BackendHealth> {
  return getApi<BackendHealth>('/health');
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
  return requestApi<T>(path);
}

async function postApi<T>(path: string): Promise<T> {
  return requestApi<T>(path, { method: 'POST' });
}

async function requestApi<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = init ? await fetch(path, init) : await fetch(path);
  } catch {
    throw new Error(backendConnectionError);
  }

  const body = await parseApiResponse<T>(response);
  if (!response.ok || !body.success) {
    throw new Error(body.message ?? `Request failed: ${response.status}`);
  }
  return body.data;
}

async function parseApiResponse<T>(response: Response): Promise<ApiResponse<T>> {
  try {
    return (await response.json()) as ApiResponse<T>;
  } catch {
    throw new Error(backendConnectionError);
  }
}
