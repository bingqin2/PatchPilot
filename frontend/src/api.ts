import type {
  ApproveReviewInput,
  ApiResponse,
  BackendHealth,
  ConfigurationSummary,
  CreateTriggerQuarantineInput,
  CreateTaskInput,
  DemoEvidenceBundle,
  DemoReadiness,
  DemoScript,
  DemoSessionSnapshot,
  DemoSmokeChecklist,
  FixTask,
  FixTaskFailureCauseSummary,
  FixTaskLatencySummary,
  FixTaskPage,
  FixTaskDetail,
  FixTaskAuditSummary,
  FixTaskMetricsSummary,
  FixTaskStatusCounts,
  FixTaskModelUsageSummary,
  FixTaskQueueItem,
  FixTaskQueueSummary,
  OperatorSafetyAudit,
  RejectedTriggerAudit,
  RejectedTriggerAuditSummary,
  ReleaseTriggerQuarantineInput,
  RejectedTriggerCategoryFilter,
  TriggerQuarantine,
  TriggerQuarantineEvidence,
  WebhookDeliveryDiagnostic,
  FixTaskModelCall,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall,
  LanguageAdapterFixtureVerification,
  RepositoryPreflightInput,
  RepositoryPreflightResult,
  SupportedLanguageAdapter,
  TaskSort,
  TaskStatusFilter
} from './types';

interface ListTasksOptions {
  status?: TaskStatusFilter;
  query?: string;
  repositoryOwner?: string;
  repositoryName?: string;
  language?: string;
  buildSystem?: string;
  createdAfter?: string;
  createdBefore?: string;
  limit?: number;
  offset?: number;
  sort?: TaskSort;
}

interface ListRejectedTriggersOptions {
  limit?: number;
  category?: RejectedTriggerCategoryFilter;
}

interface ListTriggerQuarantinesOptions {
  activeOnly?: boolean;
  limit?: number;
}

const backendConnectionError =
  'Backend request failed. Check that PatchPilot backend is running and the frontend proxy target is correct.';

export const ADMIN_TOKEN_STORAGE_KEY = 'patchpilot.adminToken';

export async function createTask(input: CreateTaskInput): Promise<FixTask> {
  return postApi<FixTask>('/api/tasks', input);
}

export async function listTasks(options: TaskStatusFilter | ListTasksOptions = 'ALL'): Promise<FixTaskPage> {
  const normalizedOptions = typeof options === 'string' ? { status: options } : options;
  const searchParams = new URLSearchParams({ limit: String(normalizedOptions.limit ?? 50) });
  if (normalizedOptions.offset !== undefined) {
    searchParams.set('offset', String(normalizedOptions.offset));
  }
  appendTaskFilterSearchParams(searchParams, normalizedOptions);
  if (normalizedOptions.sort && normalizedOptions.sort !== 'createdAtDesc') {
    searchParams.set('sort', normalizedOptions.sort);
  }
  const status = normalizedOptions.status ?? 'ALL';
  if (status !== 'ALL') {
    searchParams.set('status', status);
  }
  return getApi<FixTaskPage>(`/api/tasks?${searchParams.toString()}`);
}

export async function getTaskStatusCounts(options: ListTasksOptions = {}): Promise<FixTaskStatusCounts> {
  const searchParams = new URLSearchParams();
  appendTaskFilterSearchParams(searchParams, options);
  const queryString = searchParams.toString();
  const path = queryString ? `/api/tasks/status-counts?${queryString}` : '/api/tasks/status-counts';
  return getApi<FixTaskStatusCounts>(path);
}

function appendTaskFilterSearchParams(searchParams: URLSearchParams, normalizedOptions: ListTasksOptions) {
  if (normalizedOptions.query?.trim()) {
    searchParams.set('query', normalizedOptions.query.trim());
  }
  if (normalizedOptions.repositoryOwner?.trim()) {
    searchParams.set('repositoryOwner', normalizedOptions.repositoryOwner.trim());
  }
  if (normalizedOptions.repositoryName?.trim()) {
    searchParams.set('repositoryName', normalizedOptions.repositoryName.trim());
  }
  if (normalizedOptions.language?.trim()) {
    searchParams.set('language', normalizedOptions.language.trim());
  }
  if (normalizedOptions.buildSystem?.trim()) {
    searchParams.set('buildSystem', normalizedOptions.buildSystem.trim());
  }
  if (normalizedOptions.createdAfter?.trim()) {
    searchParams.set('createdAfter', normalizedOptions.createdAfter.trim());
  }
  if (normalizedOptions.createdBefore?.trim()) {
    searchParams.set('createdBefore', normalizedOptions.createdBefore.trim());
  }
}

export async function getMetricsSummary(options: ListTasksOptions = {}): Promise<FixTaskMetricsSummary> {
  return getFilteredMetricsApi<FixTaskMetricsSummary>('/api/tasks/metrics/summary', options);
}

export async function getFailureCauseSummary(options: ListTasksOptions = {}): Promise<FixTaskFailureCauseSummary[]> {
  return getFilteredMetricsApi<FixTaskFailureCauseSummary[]>('/api/tasks/metrics/failure-causes', options);
}

export async function getModelUsageSummary(options: ListTasksOptions = {}): Promise<FixTaskModelUsageSummary> {
  return getFilteredMetricsApi<FixTaskModelUsageSummary>('/api/tasks/metrics/model-usage', options);
}

export async function getLatencySummary(options: ListTasksOptions = {}): Promise<FixTaskLatencySummary> {
  return getFilteredMetricsApi<FixTaskLatencySummary>('/api/tasks/metrics/latency', options);
}

function getFilteredMetricsApi<T>(path: string, options: ListTasksOptions): Promise<T> {
  const searchParams = new URLSearchParams();
  appendTaskFilterSearchParams(searchParams, options);
  const queryString = searchParams.toString();
  return getApi<T>(queryString ? `${path}?${queryString}` : path);
}

export async function getConfigurationSummary(): Promise<ConfigurationSummary> {
  return getApi<ConfigurationSummary>('/api/configuration/summary');
}

export async function getDemoReadiness(): Promise<DemoReadiness> {
  return getApi<DemoReadiness>('/api/demo/readiness');
}

export async function getDemoSmokeChecklist(): Promise<DemoSmokeChecklist> {
  return getApi<DemoSmokeChecklist>('/api/demo/smoke-checklist');
}

export async function getDemoEvidenceBundle(): Promise<DemoEvidenceBundle> {
  return getApi<DemoEvidenceBundle>('/api/demo/evidence-bundle');
}

export async function getDemoScript(): Promise<DemoScript> {
  return getApi<DemoScript>('/api/demo/script');
}

export async function getDemoSessionSnapshot(): Promise<DemoSessionSnapshot> {
  return getApi<DemoSessionSnapshot>('/api/demo/session-snapshot');
}

export async function getDemoSessionReport(): Promise<string> {
  return getApi<string>('/api/demo/session-report');
}

export async function getDemoRunbook(): Promise<string> {
  return getApi<string>('/api/demo/runbook');
}

export async function getBackendHealth(): Promise<BackendHealth> {
  return getApi<BackendHealth>('/health');
}

export async function listLanguageAdapters(): Promise<SupportedLanguageAdapter[]> {
  return getApi<SupportedLanguageAdapter[]>('/api/language-adapters');
}

export async function listLanguageAdapterFixtures(): Promise<LanguageAdapterFixtureVerification[]> {
  return getApi<LanguageAdapterFixtureVerification[]>('/api/language-adapters/fixtures');
}

export async function preflightRepository(input: RepositoryPreflightInput): Promise<RepositoryPreflightResult> {
  return postApi<RepositoryPreflightResult>('/api/repository-preflight', input);
}

export async function getQueueSummary(): Promise<FixTaskQueueSummary> {
  return getApi<FixTaskQueueSummary>('/api/task-queue/summary');
}

export async function listQueueItems(): Promise<FixTaskQueueItem[]> {
  return getApi<FixTaskQueueItem[]>('/api/task-queue/items');
}

export async function listWebhookDeliveries(limit = 10): Promise<WebhookDeliveryDiagnostic[]> {
  return getApi<WebhookDeliveryDiagnostic[]>(`/api/github/webhook-deliveries?limit=${limit}`);
}

export async function listRejectedTriggers(options: number | ListRejectedTriggersOptions = 20): Promise<RejectedTriggerAudit[]> {
  const normalizedOptions = typeof options === 'number' ? { limit: options } : options;
  const searchParams = new URLSearchParams({ limit: String(normalizedOptions.limit ?? 20) });
  if (normalizedOptions.category && normalizedOptions.category !== 'ALL') {
    searchParams.set('category', normalizedOptions.category);
  }
  return getApi<RejectedTriggerAudit[]>(`/api/rejected-triggers?${searchParams.toString()}`);
}

export async function getRejectedTriggerSummary(limit = 100): Promise<RejectedTriggerAuditSummary> {
  return getApi<RejectedTriggerAuditSummary>(`/api/rejected-triggers/summary?limit=${limit}`);
}

export async function listTriggerQuarantines(
  options: ListTriggerQuarantinesOptions = {}
): Promise<TriggerQuarantine[]> {
  const searchParams = new URLSearchParams({
    activeOnly: String(options.activeOnly ?? true),
    limit: String(options.limit ?? 20)
  });
  return getApi<TriggerQuarantine[]>(`/api/trigger-quarantines?${searchParams.toString()}`);
}

export async function getTriggerQuarantineEvidence(
  quarantineId: string,
  limit = 20
): Promise<TriggerQuarantineEvidence> {
  return getApi<TriggerQuarantineEvidence>(
    `/api/trigger-quarantines/${encodeURIComponent(quarantineId)}/evidence?limit=${limit}`
  );
}

export async function listOperatorSafetyAudits(limit = 20): Promise<OperatorSafetyAudit[]> {
  return getApi<OperatorSafetyAudit[]>(`/api/operator-safety-audits?limit=${limit}`);
}

export async function createTriggerQuarantine(input: CreateTriggerQuarantineInput): Promise<TriggerQuarantine> {
  return postApi<TriggerQuarantine>('/api/trigger-quarantines', input);
}

export async function releaseTriggerQuarantine(
  quarantineId: string,
  input: ReleaseTriggerQuarantineInput
): Promise<TriggerQuarantine> {
  return postApi<TriggerQuarantine>(`/api/trigger-quarantines/${encodeURIComponent(quarantineId)}/release`, input);
}

export async function retryRejectedTrigger(rejectedTriggerId: string): Promise<FixTask> {
  return postApi<FixTask>(`/api/rejected-triggers/${encodeURIComponent(rejectedTriggerId)}/retry`);
}

export async function getTaskSummary(taskId: string): Promise<FixTaskAuditSummary> {
  return getApi<FixTaskAuditSummary>(`/api/tasks/${taskId}/summary`);
}

export async function getTaskDetail(taskId: string): Promise<FixTaskDetail> {
  return getApi<FixTaskDetail>(`/api/tasks/${taskId}/detail`);
}

export async function getTaskReport(taskId: string): Promise<string> {
  return getApi<string>(`/api/tasks/${taskId}/report`);
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

export async function approveTaskReview(taskId: string, input: ApproveReviewInput): Promise<FixTask> {
  return postApi<FixTask>(`/api/tasks/${taskId}/approve-review`, input);
}

async function getApi<T>(path: string): Promise<T> {
  return requestApi<T>(path);
}

async function postApi<T>(path: string, body?: unknown): Promise<T> {
  return requestApi<T>(path, postRequest(body));
}

async function requestApi<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  const securedInit = withAdminToken(init);
  try {
    response = securedInit ? await fetch(path, securedInit) : await fetch(path);
  } catch {
    throw new Error(backendConnectionError);
  }

  const body = await parseApiResponse<T>(response);
  if (!response.ok || !body.success) {
    throw new Error(body.message ?? `Request failed: ${response.status}`);
  }
  return body.data;
}

function withAdminToken(init?: RequestInit): RequestInit | undefined {
  const adminToken = getStoredAdminToken();
  if (!adminToken) {
    return init;
  }
  return {
    ...init,
    headers: {
      ...headersAsRecord(init?.headers),
      'X-PatchPilot-Admin-Token': adminToken
    }
  };
}

function getStoredAdminToken() {
  if (typeof globalThis.localStorage === 'undefined') {
    return '';
  }
  return globalThis.localStorage.getItem(ADMIN_TOKEN_STORAGE_KEY)?.trim() ?? '';
}

function headersAsRecord(headers?: HeadersInit): Record<string, string> {
  if (!headers) {
    return {};
  }
  if (headers instanceof Headers) {
    return Object.fromEntries(headers.entries());
  }
  if (Array.isArray(headers)) {
    return Object.fromEntries(headers);
  }
  return { ...headers };
}

function postRequest(body?: unknown): RequestInit {
  if (body === undefined) {
    return { method: 'POST' };
  }
  return {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  };
}

async function parseApiResponse<T>(response: Response): Promise<ApiResponse<T>> {
  try {
    return (await response.json()) as ApiResponse<T>;
  } catch {
    throw new Error(backendConnectionError);
  }
}
