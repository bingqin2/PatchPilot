export type TaskStatus =
  | 'PENDING'
  | 'RUNNING'
  | 'RUNNING_TESTS'
  | 'PENDING_REVIEW'
  | 'COMPLETED'
  | 'FAILED'
  | 'CANCELLED';

export type TaskStatusFilter = 'ALL' | TaskStatus;
export type RejectedTriggerCategoryFilter =
  | 'ALL'
  | 'UNKNOWN'
  | 'EMPTY_COMMAND'
  | 'UNSUPPORTED_COMMAND'
  | 'NOT_ACTIONABLE'
  | 'DANGEROUS_INSTRUCTION'
  | 'TRIGGER_USER_NOT_ALLOWED'
  | 'REPOSITORY_NOT_ALLOWED'
  | 'RATE_LIMITED'
  | 'ABUSE_QUARANTINED'
  | 'MODEL_REJECTED'
  | 'MODEL_NEEDS_CLARIFICATION'
  | 'MODEL_CLASSIFICATION_FAILED';

export type TaskSort = 'createdAtDesc' | 'createdAtAsc';

export type QueueItemStatus = 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

export type WebhookDeliveryDiagnosticStatus =
  | 'IGNORED'
  | 'INVALID_SIGNATURE'
  | 'BAD_REQUEST'
  | 'REJECTED'
  | 'DUPLICATE_DELIVERY'
  | 'ACTIVE_TASK_EXISTS'
  | 'TASK_CREATED'
  | 'FAILED';

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
  retrySourceTaskId: string | null;
  retrySourceStatus: TaskStatus | null;
  retrySourceFailureReason: string | null;
  retriedAt: string | null;
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
  adminTokenConfigured: boolean;
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
  rejectedTriggerQuarantineEnabled: boolean;
  rejectedTriggerQuarantineWindowMs: number;
  rejectedTriggerQuarantineThreshold: number;
  rejectedTriggerQuarantineCooldownMs: number;
  triggerUserAllowlistConfigured: boolean;
  repositoryAllowlistConfigured: boolean;
  reviewApprovalAllowlistConfigured: boolean;
  generatedDiffRiskGateEnabled: boolean;
  generatedDiffProtectedPathCount: number;
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

export type DemoSmokeChecklistStatus = 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';

export interface DemoSmokeChecklistStep {
  order: number;
  name: string;
  status: DemoSmokeChecklistStatus;
  message: string;
  evidence: string;
  action: string;
}

export interface DemoSmokeChecklist {
  status: DemoSmokeChecklistStatus;
  summary: string;
  steps: DemoSmokeChecklistStep[];
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

export interface RepositorySupportGuidance {
  status: 'UNSUPPORTED';
  reason: string;
  operatorAction: string;
  supportedAdapters: SupportedLanguageAdapter[];
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

export interface WebhookDeliveryDiagnostic {
  id: string;
  deliveryId: string | null;
  event: string;
  status: WebhookDeliveryDiagnosticStatus;
  taskId: string | null;
  repositoryOwner: string | null;
  repositoryName: string | null;
  issueNumber: number | null;
  triggerUser: string | null;
  triggerComment: string | null;
  message: string;
  redeliveryRecommended: boolean;
  operatorAction: string;
  createdAt: string;
}

export interface RejectedTriggerAudit {
  id: string;
  source: string;
  deliveryId: string | null;
  repositoryOwner: string | null;
  repositoryName: string | null;
  issueNumber: number | null;
  triggerUser: string | null;
  triggerComment: string | null;
  category: string | null;
  reason: string;
  commentId: number | null;
  commentUrl: string | null;
  retriedTaskId: string | null;
  retriedAt: string | null;
  createdAt: string;
}

export interface RejectedTriggerCountSummary {
  value: string;
  count: number;
}

export interface RejectedTriggerAuditSummary {
  totalCount: number;
  categoryCounts: RejectedTriggerCountSummary[];
  sourceCounts: RejectedTriggerCountSummary[];
  triggerUserCounts: RejectedTriggerCountSummary[];
  repositoryCounts: RejectedTriggerCountSummary[];
}

export type TriggerQuarantineScope = 'TRIGGER_USER' | 'REPOSITORY';

export interface CreateTriggerQuarantineInput {
  scope: TriggerQuarantineScope;
  scopeKey: string;
  reason: string;
  durationMs: number;
  operator: string;
}

export interface ReleaseTriggerQuarantineInput {
  operator: string;
  reason: string;
}

export interface TriggerQuarantine {
  id: string;
  scope: TriggerQuarantineScope;
  scopeKey: string;
  reason: string;
  category: string;
  evidenceCount: number;
  windowMs: number;
  startedAt: string;
  expiresAt: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string | null;
  releasedAt: string | null;
  releasedBy: string | null;
  releaseReason: string | null;
  active: boolean;
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
  patchReview: FixTaskPatchReview | null;
  issueContext: IssueContext | null;
  repositorySupportGuidance: RepositorySupportGuidance | null;
}

export interface IssueContext {
  title: string;
  body: string;
  url: string;
  comments: IssueContextComment[];
}

export interface IssueContextComment {
  id: number;
  author: string;
  body: string;
  createdAt: string;
  url: string;
}

export interface FixTaskGeneratedDiff {
  toolCallId: string;
  diff: string;
  generatedAt: string;
}

export interface FixTaskPatchReview {
  id: string;
  taskId: string;
  decision: string;
  reason: string;
  confidence: string;
  requiredFollowUp: string | null;
  editedFiles: string[];
  createdAt: string;
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
