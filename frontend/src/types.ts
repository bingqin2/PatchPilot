export type TaskStatus = 'PENDING' | 'RUNNING' | 'RUNNING_TESTS' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

export type TaskStatusFilter = 'ALL' | TaskStatus;

export type QueueItemStatus = 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string | null;
}

export interface FixTask {
  id: string;
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  installationId: number;
  triggerUser: string;
  triggerComment: string;
  deliveryId: string;
  commentId: number;
  status: TaskStatus;
  failureReason: string | null;
  createdAt: string;
  pullRequestUrl: string | null;
  completedAt: string | null;
  updatedAt: string;
  statusCommentId: number | null;
  statusCommentUrl: string | null;
}

export interface FixTaskPage {
  items: FixTask[];
  limit: number;
  offset: number;
  hasMore: boolean;
  total: number;
}

export interface FixTaskMetricsSummary {
  totalCount: number;
  pendingCount: number;
  runningCount: number;
  runningTestsCount: number;
  completedCount: number;
  failedCount: number;
  cancelledCount: number;
  completionRate: number;
  failureRate: number;
  averageCompletionDurationMs: number;
  totalModelTokens: number;
  averageModelTokensPerCompletedTask: number;
  testRunCount: number;
  passedTestRunCount: number;
  failedTestRunCount: number;
  testPassRate: number;
}

export interface FixTaskFailureCauseSummary {
  cause: string;
  count: number;
}

export interface FixTaskModelUsageSummary {
  totalPromptTokens: number;
  totalCompletionTokens: number;
  totalTokens: number;
  successfulCalls: number;
  failedCalls: number;
  estimatedCostUsd: number;
}

export interface FixTaskLatencySummary {
  completedTaskCount: number;
  averageTaskDurationMs: number;
  maxTaskDurationMs: number;
  modelCallCount: number;
  averageModelCallDurationMs: number;
  maxModelCallDurationMs: number;
  toolCallCount: number;
  averageToolCallDurationMs: number;
  maxToolCallDurationMs: number;
  testRunCount: number;
  averageTestRunDurationMs: number;
  maxTestRunDurationMs: number;
}

export interface ConfigurationSummary {
  agentProvider: string;
  agentModel: string;
  agentBaseUrl: string;
  agentApiKeyConfigured: boolean;
  githubTokenConfigured: boolean;
  githubWebhookSecretConfigured: boolean;
  workspaceRootDir: string;
  queueMaxAttempts: number;
  queueRetryDelayMs: number;
  queueVisibilityTimeoutMs: number;
  modelCostConfigured: boolean;
}

export interface BackendHealth {
  status: string;
  service: string;
  timestamp: string;
}

export interface FixTaskQueueSummary {
  totalCount: number;
  pendingCount: number;
  availablePendingCount: number;
  delayedPendingCount: number;
  runningCount: number;
  completedCount: number;
  failedCount: number;
  cancelledCount: number;
}

export interface FixTaskQueueItem {
  id: string;
  taskId: string;
  status: QueueItemStatus;
  attemptCount: number;
  lastError: string | null;
  availableAt: string;
  lockedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface FixTaskAuditSummary {
  task: FixTask;
  timelineEventCount: number;
  testRunCount: number;
  toolCallCount: number;
  modelCallCount: number;
  totalModelTokens: number;
  latestTimelineEvent: FixTaskTimelineEvent | null;
  latestTestRunExitCode: number | null;
  latestTestRunDurationMs: number | null;
}

export interface FixTaskDetail {
  summary: FixTaskAuditSummary;
  queueItem: FixTaskQueueItem | null;
  queueItems: FixTaskQueueItem[];
  timeline: FixTaskTimelineEvent[];
  testRuns: FixTaskTestRun[];
  toolCalls: FixTaskToolCall[];
  modelCalls: FixTaskModelCall[];
}

export interface FixTaskTimelineEvent {
  id: string;
  taskId: string;
  eventType: string;
  message: string;
  createdAt: string;
}

export interface FixTaskTestRun {
  id: string;
  taskId: string;
  command: string;
  exitCode: number;
  output: string;
  startedAt: string;
  finishedAt: string;
  durationMs: number;
}

export interface FixTaskToolCall {
  id: string;
  taskId: string;
  toolName: string;
  inputSummary: string;
  outputSummary: string;
  success: boolean;
  startedAt: string;
  finishedAt: string;
  durationMs: number;
}

export interface FixTaskModelCall {
  id: string;
  taskId: string;
  provider: string;
  model: string;
  promptSummary: string;
  responseSummary: string;
  promptTokens: number;
  completionTokens: number;
  totalTokens: number;
  success: boolean;
  errorMessage: string | null;
  startedAt: string;
  finishedAt: string;
  durationMs: number;
}
