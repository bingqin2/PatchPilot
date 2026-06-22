export type TaskStatus =
  | 'PENDING'
  | 'RUNNING'
  | 'RUNNING_TESTS'
  | 'PENDING_REVIEW'
  | 'COMPLETED'
  | 'FAILED'
  | 'CANCELLED';

export type TaskStatusFilter = 'ALL' | TaskStatus;

export type TaskSort = 'createdAtDesc' | 'createdAtAsc';

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
  language: string | null;
  buildSystem: string | null;
  verificationCommand: string | null;
  adapterDetectionReason: string | null;
  statusCommentId: number | null;
  statusCommentUrl: string | null;
  riskReviewApprovedAt: string | null;
  riskReviewApprovedBy: string | null;
  riskReviewApprovalReason: string | null;
}

export interface FixTaskPage {
  items: FixTask[];
  limit: number;
  offset: number;
  hasMore: boolean;
  total: number;
}

export interface FixTaskStatusCounts {
  totalCount: number;
  pendingCount: number;
  runningCount: number;
  runningTestsCount: number;
  pendingReviewCount: number;
  completedCount: number;
  failedCount: number;
  cancelledCount: number;
}

export interface CreateTaskInput {
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  triggerComment: string;
}

export interface ApproveReviewInput {
  operator: string;
  reason: string;
}

export interface FixTaskMetricsSummary {
  totalCount: number;
  pendingCount: number;
  runningCount: number;
  runningTestsCount: number;
  pendingReviewCount: number;
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
  modelTriggerClassificationEnabled: boolean;
  triggerRateLimitEnabled: boolean;
  triggerRateLimitWindowMs: number;
  triggerRateLimitMaxPerTriggerUser: number;
  triggerRateLimitMaxPerRepository: number;
  triggerRateLimitMaxPerIssue: number;
  triggerUserAllowlistConfigured: boolean;
  repositoryAllowlistConfigured: boolean;
  reviewApprovalAllowlistConfigured: boolean;
  allowedTriggerUsers: string[];
  allowedRepositories: string[];
  reviewApprovalAllowedOperators: string[];
}

export type DemoReadinessStatus = 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';

export interface DemoReadinessCheck {
  name: string;
  status: DemoReadinessStatus;
  message: string;
  action: string;
}

export interface DemoReadiness {
  status: DemoReadinessStatus;
  summary: string;
  checks: DemoReadinessCheck[];
  nextActions: string[];
}

export interface BackendHealth {
  status: string;
  service: string;
  timestamp: string;
}

export interface SupportedLanguageAdapter {
  language: string;
  buildSystem: string;
  verificationCommand: string[];
  detectionSignals: string[];
  demoFixturePath: string;
  status: 'SUPPORTED';
}

export interface LanguageAdapterFixtureVerification {
  fixtureName: string;
  fixturePath: string;
  expectedLanguage: string;
  expectedBuildSystem: string;
  expectedVerificationCommand: string[];
  actualLanguage: string;
  actualBuildSystem: string;
  actualVerificationCommand: string[];
  reason: string;
  status: 'PASS' | 'FAIL';
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
  generatedDiff: FixTaskGeneratedDiff | null;
}

export interface FixTaskGeneratedDiff {
  toolCallId: string;
  diff: string;
  generatedAt: string;
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
