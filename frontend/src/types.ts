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

export type WebhookDeliveryOutcomeType =
  | 'TASK'
  | 'REJECTED_TRIGGER'
  | 'IGNORED'
  | 'DUPLICATE'
  | 'ERROR';

export type FixTaskTimelineEventType =
  | 'TRIGGER_ACCEPTED'
  | 'TASK_CREATED'
  | 'STATUS_COMMENT_CREATED'
  | 'STATUS_COMMENT_FAILED'
  | 'ACTIVE_TASK_EXISTS'
  | 'RUNNING'
  | 'RUNNING_TESTS'
  | 'PENDING_REVIEW'
  | 'REVIEW_APPROVED'
  | 'PR_CREATED'
  | 'COMPLETED'
  | 'FAILED'
  | 'CANCELLED'
  | 'REQUEUED';

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
  retryReason: string | null;
  retriedAt: string | null;
}

export interface FixTaskEvidencePackageArchive {
  id: string;
  taskId: string;
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  status: TaskStatus;
  pullRequestUrl: string | null;
  archivedAt: string;
  summary: string;
  report: string;
}

export interface FixTaskEvidencePackageArchiveSummary {
  totalArchiveCount: number;
  completedArchiveCount: number;
  failedArchiveCount: number;
  pendingReviewArchiveCount: number;
  cancelledArchiveCount: number;
  latestArchiveId: string | null;
  latestTaskId: string | null;
  latestRepositoryOwner: string | null;
  latestRepositoryName: string | null;
  latestIssueNumber: number | null;
  latestArchivedAt: string | null;
  sideEffectContract: string;
  nextAction: string;
}

export interface FixTaskEvidencePackageArchiveShareCenter {
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  shareReady: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  completedArchiveCount: number;
  failedArchiveCount: number;
  pendingReviewArchiveCount: number;
  cancelledArchiveCount: number;
  latestArchiveId: string | null;
  latestTaskId: string | null;
  latestRepositoryOwner: string | null;
  latestRepositoryName: string | null;
  latestIssueNumber: number | null;
  latestArchivedAt: string | null;
  shareableArchiveId: string | null;
  shareableTaskId: string | null;
  shareableRepositoryOwner: string | null;
  shareableRepositoryName: string | null;
  shareableIssueNumber: number | null;
  shareablePullRequestUrl: string | null;
  downloadActions: string[];
  evidenceNotes: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface FixTaskEvidencePackageShareDeliveryReceipt {
  id: string;
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  taskEvidenceArchiveId: string;
  taskId: string;
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  pullRequestUrl: string;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  messageSubject: string;
  deliveredAt: string;
  createdAt: string;
  markdownReport: string;
}

export interface FixTaskEvidencePackageShareDeliveryReceiptInput {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt?: string;
}

export interface FixTaskEvidencePackageFinalizationCheck {
  name: string;
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  summary: string;
  nextAction: string;
}

export interface FixTaskEvidencePackageFinalization {
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: 'FRESH' | 'MISSING' | 'STALE';
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  checks: FixTaskEvidencePackageFinalizationCheck[];
  evidenceNotes: string[];
  markdownReport: string;
  generatedAt: string;
}

export interface FixTaskEvidencePackageAcceptanceCloseoutArchive {
  id: string;
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  accepted: boolean;
  summary: string;
  latestArchiveId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  deliveryReceiptFreshness: 'FRESH' | 'MISSING' | 'STALE';
  createdAt: string;
  report: string;
}

export interface FixTaskEvidencePackageAcceptanceCertificate {
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  certified: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestCloseoutArchiveId: string | null;
  latestEvidenceArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  deliveryReceiptFreshness: 'FRESH' | 'MISSING' | 'STALE';
  latestArchivedAt: string | null;
  generatedAt: string;
  downloadActions: string[];
  markdownReport: string;
}

export interface FixTaskEvidencePackageAcceptanceCertificateArchive {
  id: string;
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  certified: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestCloseoutArchiveId: string | null;
  latestEvidenceArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  deliveryReceiptFreshness: 'FRESH' | 'MISSING' | 'STALE';
  latestArchivedAt: string | null;
  generatedAt: string;
  archivedAt: string;
  downloadActions: string[];
  report: string;
}

export interface FixTaskRetryPreflight {
  taskId: string;
  status: TaskStatus;
  retryable: boolean;
  category: string;
  reason: string | null;
  operatorAction: string;
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
  source?: TriggerEvaluationSource;
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  triggerComment: string;
}

export type TriggerEvaluationSource = 'MANUAL' | 'ISSUE_COMMENT';

export interface TriggerEvaluationResult {
  status: 'WOULD_CREATE_TASK' | 'BLOCKED';
  source: TriggerEvaluationSource;
  wouldCreateTask: boolean;
  blockedReason: string | null;
  blockedCategory: string | null;
  safetyDecision: TriggerEvaluationDecision | null;
  activeTaskDecision: TriggerEvaluationDecision | null;
  quarantineDecision: TriggerEvaluationDecision | null;
  rateLimitDecision: TriggerEvaluationDecision | null;
  triggerIntentDecision: TriggerEvaluationDecision | null;
  issueContextLoaded: boolean;
  nextAction: string;
}

export interface TriggerEvaluationDecision {
  allowed: boolean;
  reason: string;
  category: string;
}

export interface ApproveReviewInput {
  operator: string;
  reason: string;
}

export interface RetryTaskInput {
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
  nextAction: string;
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
  githubWebhookPublicBaseUrlConfigured: boolean;
  githubWebhookPublicBaseUrl: string;
  githubWebhookPayloadUrl: string;
  adminTokenConfigured: boolean;
  dashboardBaseUrlConfigured: boolean;
  workspaceRootDir: string;
  queueMaxAttempts: number;
  queueRetryDelayMs: number;
  queueVisibilityTimeoutMs: number;
  queueWorkerHeartbeatStaleMs: number;
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
  repositoryPreflightAllowedRootDirs: string[];
}

export interface ExternalExposureReadinessCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface ExternalExposureReadiness {
  status: DemoReadinessStatus;
  safeToExpose: boolean;
  readyCount: number;
  needsAttentionCount: number;
  blockedCount: number;
  totalCount: number;
  summary: string;
  nextActions: string[];
  sideEffectContract: string;
  checks: ExternalExposureReadinessCheck[];
  generatedAt: string;
  markdownReport: string;
}

export interface ExternalExposureReadinessArchive {
  id: string;
  status: DemoReadinessStatus;
  safeToExpose: boolean;
  summary: string;
  readyCount: number;
  needsAttentionCount: number;
  blockedCount: number;
  totalCount: number;
  createdAt: string;
  report: string;
}

export interface DashboardBootstrap {
  adminTokenConfigured: boolean;
  adminTokenBootstrapEnabled: boolean;
  adminToken: string | null;
  message: string;
  operatorAction: string;
}

export type ModelProviderHealthStatus = 'READY' | 'NEEDS_ATTENTION';

export interface ModelProviderHealth {
  provider: string;
  model: string;
  baseUrlConfigured: boolean;
  apiKeyConfigured: boolean;
  status: ModelProviderHealthStatus;
  message: string;
  latencyMs: number;
  checkedAt: string;
  operatorAction: string;
}

export type GitHubCredentialReadinessStatus = 'READY' | 'NEEDS_ATTENTION';

export interface GitHubCredentialReadiness {
  tokenConfigured: boolean;
  status: GitHubCredentialReadinessStatus;
  message: string;
  latencyMs: number;
  checkedAt: string;
  operatorAction: string;
}

export type GitHubRepositoryAccessReadinessStatus = 'READY' | 'NEEDS_ATTENTION';

export interface GitHubRepositoryAccessReadiness {
  tokenConfigured: boolean;
  repositoryConfigured: boolean;
  repository: string;
  status: GitHubRepositoryAccessReadinessStatus;
  message: string;
  defaultBranch: string | null;
  latencyMs: number;
  checkedAt: string;
  operatorAction: string;
}

export interface GitHubPublishReadinessCheck {
  name: string;
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  summary: string;
  nextAction: string;
}

export interface GitHubPublishReadiness {
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  publishReady: boolean;
  tokenConfigured: boolean;
  repositoryConfigured: boolean;
  repository: string;
  defaultBranch: string | null;
  summary: string;
  nextAction: string;
  safePublishCommand: string;
  sideEffectContract: string;
  checks: GitHubPublishReadinessCheck[];
  evidenceNotes: string[];
  checkedAt: string;
}

export interface GitHubPublishPermissionReadinessCheck {
  name: string;
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  summary: string;
  nextAction: string;
}

export interface GitHubPublishPermissionReadiness {
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  publishPermissionReady: boolean;
  tokenConfigured: boolean;
  repositoryConfigured: boolean;
  repository: string;
  defaultBranch: string | null;
  canReadRepository: boolean;
  canPushBranches: boolean;
  canCreatePullRequests: boolean;
  issueFeedbackPermissionLikely: boolean;
  summary: string;
  nextAction: string;
  sideEffectContract: string;
  permissionChecks: GitHubPublishPermissionReadinessCheck[];
  evidenceNotes: string[];
  latencyMs: number;
  checkedAt: string;
}

export interface GitHubLivePublishPreflightCheck {
  name: string;
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  summary: string;
  nextAction: string;
}

export interface GitHubLivePublishPreflight {
  status: 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';
  livePublishReady: boolean;
  tokenConfigured: boolean;
  repositoryConfigured: boolean;
  repository: string;
  defaultBranch: string | null;
  patchpilotBranches: string[];
  openPatchpilotPullRequests: string[];
  summary: string;
  nextAction: string;
  sideEffectContract: string;
  checks: GitHubLivePublishPreflightCheck[];
  evidenceNotes: string[];
  latencyMs: number;
  checkedAt: string;
}

export interface GitHubTriggerDryRunInput {
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  triggerComment: string;
}

export interface GitHubTriggerDryRun {
  status: 'WOULD_CREATE_TASK' | 'BLOCKED';
  wouldCreateTask: boolean;
  repository: string;
  issueNumber: number;
  issueUrl: string;
  triggerUser: string;
  triggerComment: string;
  summary: string;
  nextAction: string;
  sideEffectContract: string;
  evaluation: TriggerEvaluationResult;
}

export interface DemoLiveLaunchGateCheck {
  name: string;
  status: DemoReadinessStatus;
  message: string;
  action: string;
}

export interface DemoLiveLaunchGate {
  status: DemoReadinessStatus;
  readyToPost: boolean;
  repository: string;
  issueNumber: number;
  issueUrl: string;
  triggerUser: string;
  triggerComment: string;
  summary: string;
  nextActions: string[];
  sideEffectContract: string;
  launchReadiness: DemoSelfHostedLaunchReadiness;
  webhookSetup: GitHubWebhookSetupReadiness;
  livePublishPreflight: GitHubLivePublishPreflight;
  triggerDryRun: GitHubTriggerDryRun;
  checks: DemoLiveLaunchGateCheck[];
  generatedAt: string;
  markdownReport: string;
}

export interface DemoEndToEndAcceptanceMatrixItem {
  category: string;
  name: string;
  status: DemoReadinessStatus;
  evidence: string;
  gap: string;
  nextAction: string;
}

export interface DemoEndToEndAcceptanceMatrix {
  status: DemoReadinessStatus;
  readyForFinalDemo: boolean;
  readinessPercent: number;
  readyCount: number;
  needsAttentionCount: number;
  blockedCount: number;
  totalCount: number;
  summary: string;
  nextActions: string[];
  sideEffectContract: string;
  items: DemoEndToEndAcceptanceMatrixItem[];
  generatedAt: string;
  markdownReport: string;
}

export type GitHubWebhookUrlReadinessStatus = 'READY' | 'NEEDS_ATTENTION';

export interface GitHubWebhookUrlReadiness {
  publicBaseUrlConfigured: boolean;
  status: GitHubWebhookUrlReadinessStatus;
  publicBaseUrl: string;
  payloadUrl: string;
  healthUrl: string;
  message: string;
  latencyMs: number;
  checkedAt: string;
  operatorAction: string;
}

export type GitHubWebhookSetupReadinessStatus = 'READY' | 'NEEDS_ATTENTION' | 'BLOCKED';

export interface GitHubWebhookSetupReadiness {
  status: GitHubWebhookSetupReadinessStatus;
  secretConfigured: boolean;
  publicUrlReady: boolean;
  publicBaseUrl: string;
  payloadUrl: string;
  healthUrl: string;
  latestDeliveryStatus: WebhookDeliveryDiagnosticStatus | null;
  latestDeliveryId: string | null;
  redeliveryRecommended: boolean;
  summary: string;
  nextActions: string[];
  checkedAt: string;
  markdownReport: string;
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

export interface DemoSelfHostedLaunchCheck {
  name: string;
  status: DemoReadinessStatus;
  message: string;
  action: string;
}

export interface DemoSelfHostedLaunchReadiness {
  status: DemoReadinessStatus;
  readyToLaunch: boolean;
  summary: string;
  checks: DemoSelfHostedLaunchCheck[];
  nextActions: string[];
  generatedAt: string;
  markdownReport: string;
}

export interface DemoSelfHostedLaunchReadinessArchive {
  id: string;
  status: DemoReadinessStatus;
  readyToLaunch: boolean;
  summary: string;
  readyCheckCount: number;
  needsAttentionCheckCount: number;
  blockedCheckCount: number;
  createdAt: string;
  report: string;
}

export interface DemoLaunchEvidencePackage {
  status: DemoReadinessStatus;
  readyToShare: boolean;
  summary: string;
  sessionId: string;
  launchReadinessStatus: DemoReadinessStatus;
  evidenceBundleStatus: DemoReadinessStatus;
  handoffFinalizationStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveReady: boolean;
  finalHandoffReportPackageArchiveId: string | null;
  finalHandoffReportPackageArchiveSummary: string;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestWebhookDeliveryId: string | null;
  evaluationRunId: string | null;
  evaluationCoverage: string[];
  preLaunchChecks: DemoSelfHostedLaunchCheck[];
  liveRunProof: string[];
  postDemoProof: string[];
  nextActions: string[];
  healthContract: string[];
  markdownReport: string;
  generatedAt: string;
}

export interface DemoLaunchEvidencePackageArchive {
  id: string;
  status: DemoReadinessStatus;
  readyToShare: boolean;
  summary: string;
  sessionId: string;
  launchReadinessStatus: DemoReadinessStatus;
  evidenceBundleStatus: DemoReadinessStatus;
  handoffFinalizationStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveReady: boolean;
  finalHandoffReportPackageArchiveId: string | null;
  finalHandoffReportPackageArchiveSummary: string;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestWebhookDeliveryId: string | null;
  evaluationRunId: string | null;
  createdAt: string;
  report: string;
}

export interface DemoLaunchEvidenceShareCenter {
  status: 'NO_ARCHIVE' | DemoReadinessStatus;
  shareReady: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestSessionId: string | null;
  latestCreatedAt: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestWebhookDeliveryId: string | null;
  evaluationRunId: string | null;
  finalHandoffReportPackageArchiveStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveReady: boolean;
  finalHandoffReportPackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptRecorded: boolean;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  downloadActions: string[];
  evidenceNotes: string[];
  markdownReport: string;
  generatedAt: string;
}

export interface DemoLaunchEvidenceFinalizationCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoLaunchEvidenceFinalization {
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestSessionId: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  checks: DemoLaunchEvidenceFinalizationCheck[];
  evidenceNotes: string[];
  markdownReport: string;
  generatedAt: string;
}

export interface DemoLaunchAcceptanceCloseoutCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoLaunchAcceptanceCloseout {
  status: DemoReadinessStatus;
  accepted: boolean;
  summary: string;
  nextAction: string;
  sessionId: string;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestWebhookDeliveryId: string | null;
  evaluationRunId: string | null;
  latestArchiveId: string | null;
  finalHandoffReportPackageArchiveStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveReady: boolean;
  finalHandoffReportPackageArchiveId: string | null;
  finalHandoffReportPackageArchiveSummary: string;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  generatedAt: string;
  checks: DemoLaunchAcceptanceCloseoutCheck[];
  evidenceNotes: string[];
  downloadActions: string[];
  markdownReport: string;
}

export interface DemoLaunchAcceptanceCloseoutArchive {
  id: string;
  status: DemoReadinessStatus;
  accepted: boolean;
  summary: string;
  sessionId: string;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestWebhookDeliveryId: string | null;
  evaluationRunId: string | null;
  latestArchiveId: string | null;
  finalHandoffReportPackageArchiveStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveReady: boolean;
  finalHandoffReportPackageArchiveId: string | null;
  finalHandoffReportPackageArchiveSummary: string;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  deliveryReceiptFreshness: string;
  createdAt: string;
  report: string;
}

export interface DemoLaunchAcceptanceCertificate {
  status: DemoReadinessStatus;
  certified: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestCloseoutArchiveId: string | null;
  latestLaunchEvidenceArchiveId: string | null;
  finalHandoffReportPackageArchiveStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveReady: boolean;
  finalHandoffReportPackageArchiveId: string | null;
  finalHandoffReportPackageArchiveSummary: string;
  latestDeliveryReceiptId: string | null;
  latestSessionId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestWebhookDeliveryId: string | null;
  evaluationRunId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  deliveryReceiptFreshness: string;
  latestArchivedAt: string | null;
  generatedAt: string;
  downloadActions: string[];
  markdownReport: string;
}

export interface DemoLaunchAcceptanceCertificateArchive {
  id: string;
  status: DemoReadinessStatus;
  certified: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestCloseoutArchiveId: string | null;
  latestLaunchEvidenceArchiveId: string | null;
  finalHandoffReportPackageArchiveStatus: DemoReadinessStatus;
  finalHandoffReportPackageArchiveReady: boolean;
  finalHandoffReportPackageArchiveId: string | null;
  finalHandoffReportPackageArchiveSummary: string;
  latestDeliveryReceiptId: string | null;
  latestSessionId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestWebhookDeliveryId: string | null;
  evaluationRunId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  deliveryReceiptFreshness: string;
  latestArchivedAt: string | null;
  generatedAt: string;
  archivedAt: string;
  downloadActions: string[];
  report: string;
}

export interface DemoAcceptanceSummaryCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoAcceptanceSummary {
  status: DemoReadinessStatus;
  accepted: boolean;
  summary: string;
  nextAction: string;
  launchCertificateStatus: DemoReadinessStatus;
  launchCertificateArchived: boolean;
  launchCertificateCertified: boolean;
  launchCertificateArchiveId: string | null;
  launchCloseoutArchiveId: string | null;
  launchEvidenceArchiveId: string | null;
  launchDeliveryReceiptId: string | null;
  taskCertificateStatus: DemoReadinessStatus;
  taskCertificateArchived: boolean;
  taskCertificateCertified: boolean;
  taskCertificateArchiveId: string | null;
  taskCloseoutArchiveId: string | null;
  taskEvidenceArchiveId: string | null;
  taskDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  generatedAt: string;
  checks: DemoAcceptanceSummaryCheck[];
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
}

export interface DemoFinalAcceptanceSharePackage {
  status: DemoReadinessStatus;
  sendReady: boolean;
  summary: string;
  nextAction: string;
  launchCertificateArchiveId: string | null;
  taskCertificateArchiveId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  recommendedRecipients: string[];
  requiredAttachments: string[];
  preSendChecks: string[];
  messageSubject: string;
  messageBody: string;
  evidenceNotes: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalAcceptanceSharePackageArchive {
  id: string;
  status: DemoReadinessStatus;
  sendReady: boolean;
  summary: string;
  nextAction: string;
  launchCertificateArchiveId: string | null;
  taskCertificateArchiveId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  recommendedRecipients: string[];
  requiredAttachments: string[];
  preSendChecks: string[];
  messageSubject: string;
  messageBody: string;
  evidenceNotes: string[];
  sideEffectContract: string;
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoFinalAcceptanceShareDeliveryReceipt {
  id: string;
  status: DemoReadinessStatus;
  finalAcceptanceSharePackageArchiveId: string;
  latestTaskId: string;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  messageSubject: string;
  deliveredAt: string;
  createdAt: string;
  markdownReport: string;
}

export interface DemoFinalAcceptanceShareDeliveryReceiptInput {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt?: string;
}

export interface DemoFinalAcceptanceShareFinalizationCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoFinalAcceptanceShareFinalization {
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestTaskId: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  checks: DemoFinalAcceptanceShareFinalizationCheck[];
  evidenceNotes: string[];
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalAcceptanceCompletionArchive {
  id: string;
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestTaskId: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  evidenceNotes: string[];
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoFinalAcceptanceCompletionEvidenceBundle {
  status: DemoReadinessStatus;
  readyToShare: boolean;
  summary: string;
  nextAction: string;
  latestCompletionArchiveId: string | null;
  latestSharePackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestTaskId: string | null;
  completionArchiveCount: number;
  latestArchivedAt: string | null;
  generatedAt: string;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
}

export interface DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt {
  id: string;
  status: DemoReadinessStatus;
  readyToShare: boolean;
  completionEvidenceBundleStatus: DemoReadinessStatus;
  summary: string;
  nextAction: string;
  latestCompletionArchiveId: string;
  latestSharePackageArchiveId: string;
  latestDeliveryReceiptId: string;
  latestTaskId: string;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt: string;
  createdAt: string;
  markdownReport: string;
}

export interface DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization {
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestCompletionArchiveId: string | null;
  latestSharePackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestCompletionEvidenceDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  checks: DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationCheck[];
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalAcceptanceCompletionCloseoutCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoFinalAcceptanceCompletionCloseout {
  status: DemoReadinessStatus;
  closed: boolean;
  summary: string;
  nextAction: string;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestSharePackageArchiveId: string | null;
  latestCompletionArchiveId: string | null;
  latestCompletionEvidenceDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  checks: DemoFinalAcceptanceCompletionCloseoutCheck[];
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalAcceptanceCompletionCloseoutArchive {
  id: string;
  status: DemoReadinessStatus;
  closed: boolean;
  summary: string;
  nextAction: string;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestSharePackageArchiveId: string | null;
  latestCompletionArchiveId: string | null;
  latestCompletionEvidenceDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoFinalExternalReviewEvidencePackageArchive {
  id: string;
  status: DemoReadinessStatus;
  readyForExternalReview: boolean;
  summary: string;
  nextAction: string;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  finalAcceptanceSharePackageArchiveId: string | null;
  completionArchiveId: string | null;
  completionEvidenceDeliveryReceiptId: string | null;
  closeoutArchiveId: string | null;
  deliveryTarget: string | null;
  deliveryChannel: string | null;
  deliveredAt: string | null;
  deliveryReceiptFreshness: string | null;
  closeoutArchivedAt: string | null;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoFinalExternalReviewEvidencePackageDeliveryReceiptInput {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt?: string;
}

export interface DemoFinalExternalReviewEvidencePackageDeliveryReceipt {
  id: string;
  status: DemoReadinessStatus;
  finalExternalReviewPackageArchiveStatus: DemoReadinessStatus;
  finalExternalReviewPackageArchiveId: string;
  closeoutArchiveId: string | null;
  completionArchiveId: string | null;
  completionEvidenceDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  summary: string;
  nextAction: string;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt: string;
  createdAt: string;
  markdownReport: string;
}

export interface DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt?: string;
}

export interface DemoLaunchEvidenceShareDeliveryReceipt {
  id: string;
  status: string;
  launchEvidenceArchiveId: string;
  sessionId: string;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  messageSubject: string;
  deliveredAt: string;
  createdAt: string;
  markdownReport: string;
}

export interface DemoLaunchEvidenceShareDeliveryReceiptInput {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt: string;
}

export interface DemoReadinessSnapshotArchive {
  id: string;
  status: DemoReadinessStatus;
  summary: string;
  readyCheckCount: number;
  needsAttentionCheckCount: number;
  blockedCheckCount: number;
  createdAt: string;
  report: string;
}

export type DemoReadinessSnapshotTrendStatus = 'NO_BASELINE' | 'IMPROVING' | 'STABLE' | 'REGRESSING';

export interface DemoReadinessSnapshotTrend {
  status: DemoReadinessSnapshotTrendStatus;
  summary: string;
  latestSnapshotId: string | null;
  previousSnapshotId: string | null;
  latestReadinessStatus: DemoReadinessStatus | null;
  previousReadinessStatus: DemoReadinessStatus | null;
  readyCheckDelta: number;
  needsAttentionCheckDelta: number;
  blockedCheckDelta: number;
  nextAction: string;
  markdownReport: string;
}

export interface DemoLaunchPreflightInput {
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  triggerComment: string;
}

export type DemoLaunchCommandOperation = 'replace' | 'touch';

export interface DemoLaunchCommandInput {
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  operation: DemoLaunchCommandOperation;
  targetPath: string;
  replacementText?: string | null;
}

export interface DemoLaunchCommand {
  triggerComment: string;
  preflightInput: DemoLaunchPreflightInput;
  githubIssueUrl: string;
  summary: string;
  nextActions: string[];
}

export interface DemoLaunchPreflight {
  status: DemoReadinessStatus;
  readyToPost: boolean;
  summary: string;
  readiness: DemoReadiness;
  triggerEvaluation: TriggerEvaluationResult;
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

export interface DemoEvidenceBundleSummary {
  adapterFixtureCount: number;
  failedAdapterFixtureCount: number;
  recentTaskCount: number;
  activeQuarantineCount: number;
  recentPullRequestAvailable: boolean;
}

export interface DemoAdapterFixtureEvidence {
  totalCount: number;
  failedCount: number;
}

export interface DemoEvaluationRunReadinessEvidence {
  status: DemoReadinessStatus;
  latestRunId: string | null;
  previousRunId: string | null;
  passedDelta: number;
  failedDelta: number;
  skippedDelta: number;
  coveredLanguages: string[];
  coveredBuildSystems: string[];
  safetyRejectionCategories: string[];
  sideEffectContract: string;
  nextAction: string;
}

export interface DemoLaunchAcceptanceCloseoutEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  accepted: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestEvidenceArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoLaunchAcceptanceCertificateEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  certified: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestCloseoutArchiveId: string | null;
  latestEvidenceArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoTaskEvidenceAcceptanceCertificateEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  certified: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestCloseoutArchiveId: string | null;
  latestEvidenceArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalHandoffReportPackageArchiveEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  downloadReady: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestHandoffArchiveId: string | null;
  latestSessionId: string | null;
  latestDeliveryReceiptId: string | null;
  taskCertificateArchiveId: string | null;
  taskCertificateReady: boolean;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalAcceptanceCompletionCloseoutArchiveEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  closed: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestCompletionArchiveId: string | null;
  latestCompletionEvidenceDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalExternalReviewEvidencePackageArchiveEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  readyForExternalReview: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestCloseoutArchiveId: string | null;
  latestCompletionArchiveId: string | null;
  latestCompletionEvidenceDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidence {
  status: DemoReadinessStatus;
  recorded: boolean;
  fresh: boolean;
  freshness: string;
  summary: string;
  nextAction: string;
  receiptCount: number;
  latestReceiptId: string | null;
  latestPackageArchiveId: string | null;
  latestCloseoutArchiveId: string | null;
  latestCompletionArchiveId: string | null;
  latestCompletionEvidenceDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalExternalReviewEvidencePackageDeliveryFinalization {
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestCloseoutArchiveId: string | null;
  latestCompletionArchiveId: string | null;
  latestCompletionEvidenceDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive {
  id: string;
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestCloseoutArchiveId: string | null;
  latestCompletionArchiveId: string | null;
  latestCompletionEvidenceDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoFinalExternalReviewDeliveryCertificate {
  status: DemoReadinessStatus;
  certified: boolean;
  summary: string;
  nextAction: string;
  latestDeliveryFinalizationArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  latestArchivedAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalExternalReviewDeliveryCertificateArchive {
  id: string;
  status: DemoReadinessStatus;
  certified: boolean;
  summary: string;
  nextAction: string;
  latestDeliveryFinalizationArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  latestArchivedAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoFinalExternalReviewReleaseBundle {
  status: DemoReadinessStatus;
  releaseReady: boolean;
  summary: string;
  nextAction: string;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  latestCertificateArchivedAt: string | null;
  generatedAt: string;
  requiredAttachments: string[];
  releaseChecks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
}

export interface DemoFinalExternalReviewReleaseBundleArchive {
  id: string;
  status: DemoReadinessStatus;
  releaseReady: boolean;
  summary: string;
  nextAction: string;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  latestCertificateArchivedAt: string | null;
  generatedAt: string;
  archivedAt: string;
  requiredAttachments: string[];
  releaseChecks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  report: string;
}

export interface DemoFinalExternalReviewReleaseBundleDeliveryReceiptInput {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt?: string;
}

export interface DemoFinalExternalReviewReleaseBundleDeliveryReceipt {
  id: string;
  status: DemoReadinessStatus;
  releaseBundleArchiveStatus: DemoReadinessStatus;
  releaseBundleArchiveId: string;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestPackageDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  summary: string;
  nextAction: string;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt: string;
  createdAt: string;
  markdownReport: string;
}

export interface DemoFinalExternalReviewReleaseBundleDeliveryFinalization {
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestPackageDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  releaseBundleDeliveryReceiptFreshness: string;
  releaseBundleDeliveryReceiptFresh: boolean;
  releaseBundleDeliveryReceiptFreshnessSummary: string;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive {
  id: string;
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestPackageDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  releaseBundleDeliveryReceiptFreshness: string;
  releaseBundleDeliveryReceiptFresh: boolean;
  releaseBundleDeliveryReceiptFreshnessSummary: string;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoFinalExternalReviewReleaseBundleDeliveryCertificate {
  status: DemoReadinessStatus;
  certified: boolean;
  summary: string;
  nextAction: string;
  latestDeliveryFinalizationArchiveId: string | null;
  latestReleaseBundleArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestCertificateArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestPackageDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  latestArchivedAt: string | null;
  releaseBundleDeliveryReceiptFreshness: string;
  releaseBundleDeliveryReceiptFresh: boolean;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchive {
  id: string;
  status: DemoReadinessStatus;
  certified: boolean;
  summary: string;
  nextAction: string;
  latestDeliveryFinalizationArchiveId: string | null;
  latestReleaseBundleArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestCertificateArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestPackageDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  latestArchivedAt: string | null;
  releaseBundleDeliveryReceiptFreshness: string;
  releaseBundleDeliveryReceiptFresh: boolean;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  finalized: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalExternalReviewReleaseBundleArchiveEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  releaseReady: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  finalized: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestReleaseBundleArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestCertificateArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEvidence {
  status: DemoReadinessStatus;
  archived: boolean;
  certified: boolean;
  summary: string;
  nextAction: string;
  archiveCount: number;
  latestArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestReleaseBundleArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestCertificateArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestArchivedAt: string | null;
  downloadActions: string[];
}

export interface DemoFinalReviewerHandoffPackage {
  status: DemoReadinessStatus;
  readyForReview: boolean;
  summary: string;
  nextAction: string;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestReleaseBundleArchiveId: string | null;
  latestDeliveryReceiptId: string | null;
  latestPackageCertificateArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestPackageDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  latestArchivedAt: string | null;
  requiredAttachments: string[];
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalReviewerHandoffDeliveryReceiptInput {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt?: string;
}

export interface DemoFinalReviewerHandoffDeliveryReceipt {
  id: string;
  status: DemoReadinessStatus;
  fresh: boolean;
  freshness: string;
  summary: string;
  nextAction: string;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt: string;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestReleaseBundleArchiveId: string | null;
  latestReleaseBundleDeliveryReceiptId: string | null;
  latestPackageCertificateArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestPackageDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  createdAt: string;
}

export interface DemoFinalReviewerHandoffDeliveryFinalization {
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestDeliveryReceiptId: string | null;
  latestCertificateArchiveId: string | null;
  latestDeliveryFinalizationArchiveId: string | null;
  latestReleaseBundleArchiveId: string | null;
  latestReleaseBundleDeliveryReceiptId: string | null;
  latestPackageCertificateArchiveId: string | null;
  latestPackageArchiveId: string | null;
  latestPackageDeliveryReceiptId: string | null;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  handoffDeliveryReceiptFreshness: string;
  handoffDeliveryReceiptFresh: boolean;
  handoffDeliveryReceiptFreshnessSummary: string;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalExternalReviewEvidencePackage {
  status: DemoReadinessStatus;
  readyForExternalReview: boolean;
  summary: string;
  nextAction: string;
  finalAcceptanceSummaryStatus: DemoReadinessStatus;
  finalAcceptanceShareFinalizationStatus: DemoReadinessStatus;
  completionEvidenceBundleStatus: DemoReadinessStatus;
  completionDeliveryFinalizationStatus: DemoReadinessStatus;
  completionCloseoutStatus: DemoReadinessStatus;
  closeoutArchiveStatus: DemoReadinessStatus;
  latestTaskId: string | null;
  latestPullRequestUrl: string | null;
  finalAcceptanceSharePackageArchiveId: string | null;
  completionArchiveId: string | null;
  completionEvidenceDeliveryReceiptId: string | null;
  closeoutArchiveId: string | null;
  deliveryTarget: string | null;
  deliveryChannel: string | null;
  deliveredAt: string | null;
  deliveryReceiptFreshness: string | null;
  closeoutArchivedAt: string | null;
  generatedAt: string;
  checks: Array<{
    name: string;
    status: DemoReadinessStatus;
    summary: string;
    nextAction: string;
  }>;
  evidenceNotes: string[];
  downloadActions: string[];
  sideEffectContract: string;
  markdownReport: string;
}

export interface DemoEvidenceBundle {
  status: DemoReadinessStatus;
  summary: string;
  summaryCounts: DemoEvidenceBundleSummary;
  readiness: DemoReadiness;
  smokeChecklist: DemoSmokeChecklist;
  configuration: ConfigurationSummary | null;
  adapterFixtures: DemoAdapterFixtureEvidence;
  evaluationRunReadiness: DemoEvaluationRunReadinessEvidence;
  queueSummary: FixTaskQueueSummary;
  recentTask: FixTask | null;
  recentPullRequestUrl: string | null;
  webhookSetupReadiness: GitHubWebhookSetupReadiness | null;
  latestWebhookDelivery: WebhookDeliveryDiagnostic | null;
  recentWebhookDeliveries: WebhookDeliveryDiagnostic[];
  rejectedTriggerSummary: RejectedTriggerAuditSummary | null;
  activeQuarantineCount: number;
  handoffShareChecklistStatus: DemoReadinessStatus;
  handoffShareChecklistSummary: string;
  handoffShareChecklistNextAction: string;
  handoffShareCenterStatus: DemoReadinessStatus;
  handoffShareCenterSummary: string;
  handoffShareCenterNextAction: string;
  handoffShareCenterDownloadActions: string[];
  launchEvidenceShareCenterStatus: 'NO_ARCHIVE' | DemoReadinessStatus;
  launchEvidenceShareCenterReady: boolean;
  launchEvidenceShareCenterSummary: string;
  launchEvidenceShareCenterNextAction: string;
  launchEvidenceShareCenterArchiveCount: number;
  launchEvidenceShareCenterLatestArchiveId: string | null;
  launchEvidenceShareCenterLatestSessionId: string | null;
  launchEvidenceShareCenterLatestPullRequestUrl: string | null;
  launchEvidenceShareCenterDownloadActions: string[];
  launchEvidenceFinalizationStatus: DemoReadinessStatus;
  launchEvidenceFinalized: boolean;
  launchEvidenceFinalizationSummary: string;
  launchEvidenceFinalizationNextAction: string;
  launchEvidenceFinalizationDeliveryReceiptFreshness: string;
  launchEvidenceFinalizationDeliveryReceiptFresh: boolean;
  launchEvidenceFinalizationLatestDeliveryReceiptId: string | null;
  launchAcceptanceCloseoutEvidence: DemoLaunchAcceptanceCloseoutEvidence;
  launchAcceptanceCertificateEvidence: DemoLaunchAcceptanceCertificateEvidence;
  taskEvidenceAcceptanceCertificateEvidence: DemoTaskEvidenceAcceptanceCertificateEvidence;
  finalHandoffReportPackageArchiveEvidence: DemoFinalHandoffReportPackageArchiveEvidence;
  finalAcceptanceShareFinalization: DemoFinalAcceptanceShareFinalization;
  finalAcceptanceCompletionCloseoutEvidence: DemoFinalAcceptanceCompletionCloseout;
  finalAcceptanceCompletionCloseoutArchiveEvidence: DemoFinalAcceptanceCompletionCloseoutArchiveEvidence;
  finalExternalReviewEvidencePackage?: DemoFinalExternalReviewEvidencePackage;
  finalExternalReviewEvidencePackageArchiveEvidence?: DemoFinalExternalReviewEvidencePackageArchiveEvidence;
  finalExternalReviewEvidencePackageDeliveryReceiptEvidence?:
    DemoFinalExternalReviewEvidencePackageDeliveryReceiptEvidence;
  finalExternalReviewEvidencePackageDeliveryFinalization?:
    DemoFinalExternalReviewEvidencePackageDeliveryFinalization;
  finalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence?:
    DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEvidence;
  finalExternalReviewReleaseBundle?: DemoFinalExternalReviewReleaseBundle;
  finalExternalReviewReleaseBundleArchiveEvidence?: DemoFinalExternalReviewReleaseBundleArchiveEvidence;
  finalExternalReviewReleaseBundleDeliveryFinalization?:
    DemoFinalExternalReviewReleaseBundleDeliveryFinalization;
  finalExternalReviewReleaseBundleDeliveryFinalizationArchiveEvidence?:
    DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEvidence;
  finalExternalReviewReleaseBundleDeliveryCertificateArchiveEvidence?:
    DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEvidence;
  finalReviewerHandoffPackage?: DemoFinalReviewerHandoffPackage;
  finalReviewerHandoffDeliveryFinalization?: DemoFinalReviewerHandoffDeliveryFinalization;
  handoffShareDeliveryReceiptRecorded: boolean;
  handoffShareLatestDeliveryReceiptId: string | null;
  handoffShareLatestDeliveryTarget: string | null;
  handoffShareLatestDeliveryChannel: string | null;
  handoffShareLatestDeliveredAt: string | null;
  handoffShareDeliveryReceiptFreshness: string;
  handoffShareDeliveryReceiptFresh: boolean;
  handoffShareDeliveryReceiptFreshnessSummary: string;
  handoffFinalizationStatus: DemoReadinessStatus;
  handoffFinalized: boolean;
  handoffFinalizationSummary: string;
  handoffFinalizationNextAction: string;
  handoffFinalizationDeliveryReceiptFreshness: string;
  handoffFinalizationDeliveryReceiptFresh: boolean;
  handoffFinalizationLatestDeliveryReceiptId: string | null;
  generatedAt: string;
  nextActions: string[];
}

export interface DemoScriptStep {
  order: number;
  name: string;
  status: DemoReadinessStatus;
  operatorAction: string;
  verificationCommand: string;
  successCriteria: string;
  troubleshootingPanel: string;
  evidence: string;
}

export interface DemoScript {
  status: DemoReadinessStatus;
  summary: string;
  steps: DemoScriptStep[];
  healthContract: string[];
  nextActions: string[];
  generatedAt: string;
}

export interface DemoSessionSnapshot {
  sessionId: string;
  status: DemoReadinessStatus;
  summary: string;
  generatedAt: string;
  evidenceBundle: DemoEvidenceBundle;
  script: DemoScript;
  runbook: string;
  readinessSnapshotTrend: DemoReadinessSnapshotTrend;
  operatorChecklist: string[];
  healthContract: string[];
  shareSummary: string;
  nextActions: string[];
}

export interface DemoHandoffReadinessCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoHandoffReadiness {
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
  checks: DemoHandoffReadinessCheck[];
}

export interface DemoPreparedLaunchCommand {
  triggerComment: string;
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  operation: DemoLaunchCommandOperation;
  targetPath: string;
  replacementText: string | null;
  savedAt: string;
}

export interface DemoSessionReportInput {
  preparedLaunchCommands: DemoPreparedLaunchCommand[];
  archivedLaunchOutcomes: DemoArchivedLaunchOutcome[];
}

export interface DemoArchivedLaunchOutcome {
  triggerComment: string;
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  taskId: string | null;
  taskStatus: TaskStatus | 'PENDING';
  pullRequestUrl: string | null;
  archivedAt: string;
  report: string;
}

export interface DemoSessionArchive {
  id: string;
  sessionId: string;
  status: DemoReadinessStatus;
  summary: string;
  shareSummary: string;
  recentPullRequestUrl: string | null;
  createdAt: string;
  report: string;
}

export interface DemoHandoffPackageArchive extends DemoSessionArchive {
  handoffReadinessStatus: DemoReadinessStatus;
  handoffReadinessSummary: string;
  handoffReadinessNextAction: string;
  handoffReadyCheckCount: number;
  handoffNeedsAttentionCheckCount: number;
  handoffBlockedCheckCount: number;
}

export type DemoHandoffPackageArchiveSummaryStatus = 'NO_ARCHIVE' | DemoReadinessStatus;

export interface DemoHandoffPackageArchiveSummary {
  status: DemoHandoffPackageArchiveSummaryStatus;
  shareReady: boolean;
  archiveCount: number;
  latestArchiveId: string | null;
  latestSessionId: string | null;
  latestHandoffReadinessStatus: DemoReadinessStatus | null;
  latestCreatedAt: string | null;
  summary: string;
  nextAction: string;
  markdownReport: string;
}

export interface DemoHandoffShareChecklistItem {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoHandoffShareChecklist {
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
  checks: DemoHandoffShareChecklistItem[];
  markdownReport: string;
  generatedAt: string;
}

export interface DemoHandoffShareCenter {
  status: DemoReadinessStatus;
  shareReady: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestSessionId: string | null;
  latestCreatedAt: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptRecorded: boolean;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  taskCertificateStatus?: DemoReadinessStatus;
  taskCertificateReady?: boolean;
  taskCertificateSummary?: string;
  taskCertificateNextAction?: string;
  taskCertificateArchiveId?: string | null;
  taskCertificateTaskId?: string | null;
  taskCertificatePullRequestUrl?: string | null;
  downloadActions: string[];
  evidenceNotes: string[];
  markdownReport: string;
  generatedAt: string;
}

export interface DemoHandoffFinalizationCheck {
  name: string;
  status: DemoReadinessStatus;
  summary: string;
  nextAction: string;
}

export interface DemoHandoffFinalization {
  status: DemoReadinessStatus;
  finalized: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestSessionId: string | null;
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  latestDeliveredAt: string | null;
  deliveryReceiptFreshness: string;
  deliveryReceiptFresh: boolean;
  deliveryReceiptFreshnessSummary: string;
  checks: DemoHandoffFinalizationCheck[];
  evidenceNotes: string[];
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalHandoffReportPackage {
  status: DemoReadinessStatus;
  downloadReady: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestSessionId: string | null;
  latestDeliveryReceiptId: string | null;
  taskCertificateArchiveId: string | null;
  taskCertificateReady: boolean;
  readinessChecks: string[];
  requiredAttachments: string[];
  preSendChecks: string[];
  evidenceNotes: string[];
  sourceReports: string[];
  markdownReport: string;
  generatedAt: string;
}

export interface DemoFinalHandoffReportPackageArchive {
  id: string;
  status: DemoReadinessStatus;
  downloadReady: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestSessionId: string | null;
  latestDeliveryReceiptId: string | null;
  taskCertificateArchiveId: string | null;
  taskCertificateReady: boolean;
  readinessChecks: string[];
  requiredAttachments: string[];
  preSendChecks: string[];
  evidenceNotes: string[];
  sourceReports: string[];
  report: string;
  generatedAt: string;
  archivedAt: string;
}

export interface DemoHandoffShareInstructions {
  status: DemoReadinessStatus;
  sendReady: boolean;
  summary: string;
  nextAction: string;
  latestArchiveId: string | null;
  latestSessionId: string | null;
  recommendedRecipients: string[];
  requiredAttachments: string[];
  preSendChecks: string[];
  messageSubject: string;
  messageBody: string;
  markdownReport: string;
  generatedAt: string;
}

export interface DemoHandoffShareDeliveryReceipt {
  id: string;
  status: DemoReadinessStatus;
  handoffArchiveId: string;
  sessionId: string;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  messageSubject: string;
  deliveredAt: string;
  createdAt: string;
  markdownReport: string;
}

export interface DemoHandoffShareDeliveryReceiptInput {
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  notes: string;
  deliveredAt: string;
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

export interface RepositoryPreflightInput {
  repositoryPath: string;
}

export interface RepositoryPreflightResult {
  supported: boolean;
  language: string;
  buildSystem: string;
  verificationCommand: string[];
  reason: string;
  operatorAction: string;
  repositoryPath: string;
  supportedAdapters: SupportedLanguageAdapter[];
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

export interface LanguageAdapterRuntimeReadiness {
  language: string;
  buildSystem: string;
  executable: string;
  verificationCommand: string[];
  status: 'READY' | 'MISSING';
  reason: string;
}

export type EvaluationCaseCategory = 'SUPPORTED_FIX' | 'SAFETY_REJECTION';
export type EvaluationCaseExpectedDecision = 'ACCEPT_AND_CREATE_PR' | 'REJECT_BEFORE_TASK';
export type EvaluationSummaryStatus = 'READY' | 'NEEDS_ATTENTION';

export interface EvaluationCase {
  id: string;
  title: string;
  category: EvaluationCaseCategory;
  language: string | null;
  buildSystem: string | null;
  repositoryFixturePath: string | null;
  issueText: string;
  expectedVerificationCommand: string[];
  expectedChangedFiles: string[];
  successCriteria: string[];
  expectedDecision: EvaluationCaseExpectedDecision;
  expectedRejectionCategory: string | null;
  safetyExpectation: string;
}

export interface EvaluationCaseSummary {
  status: EvaluationSummaryStatus;
  totalCaseCount: number;
  supportedFixCaseCount: number;
  safetyRejectionCaseCount: number;
  coveredLanguages: string[];
  coveredBuildSystems: string[];
  rejectionCategories: string[];
  nextAction: string;
  readOnly: boolean;
  healthContract: string;
}

export type EvaluationCaseFixtureReadinessStatus = 'PASS' | 'FAIL' | 'NO_FIXTURE_REQUIRED';

export interface EvaluationCaseFixtureReadiness {
  caseId: string;
  title: string;
  category: EvaluationCaseCategory;
  status: EvaluationCaseFixtureReadinessStatus;
  fixtureRequired: boolean;
  fixturePath: string;
  fixtureExists: boolean;
  expectedLanguage: string;
  actualLanguage: string;
  expectedBuildSystem: string;
  actualBuildSystem: string;
  expectedVerificationCommand: string[];
  actualVerificationCommand: string[];
  adapterMatches: boolean;
  expectedChangedFiles: string[];
  missingExpectedFiles: string[];
  expectedFilesExist: boolean;
  reason: string;
  nextAction: string;
}

export interface EvaluationCaseFixtureReadinessSummary {
  status: EvaluationSummaryStatus;
  totalCaseCount: number;
  passingCaseCount: number;
  noFixtureRequiredCaseCount: number;
  failingCaseCount: number;
  cases: EvaluationCaseFixtureReadiness[];
  sideEffectContract: string;
  nextAction: string;
  markdownReport: string;
}

export type EvaluationFixtureBaselineCaseStatus = 'PASSED' | 'FAILED' | 'SKIPPED';

export interface EvaluationFixtureBaselineCase {
  caseId: string;
  title: string;
  category: EvaluationCaseCategory;
  status: EvaluationFixtureBaselineCaseStatus;
  executed: boolean;
  fixturePath: string;
  language: string;
  buildSystem: string;
  verificationCommand: string[];
  exitCode: number | null;
  outputSnippet: string;
  reason: string;
  nextAction: string;
}

export interface EvaluationFixtureBaselineSummary {
  status: EvaluationSummaryStatus;
  totalCaseCount: number;
  executedCaseCount: number;
  passedCaseCount: number;
  failedCaseCount: number;
  skippedCaseCount: number;
  cases: EvaluationFixtureBaselineCase[];
  sideEffectContract: string;
  nextAction: string;
  markdownReport: string;
}

export interface EvaluationFixtureBaselineRunArchive {
  id: string;
  status: EvaluationSummaryStatus;
  totalCaseCount: number;
  executedCaseCount: number;
  passedCaseCount: number;
  failedCaseCount: number;
  skippedCaseCount: number;
  createdAt: string;
  sideEffectContract: string;
  nextAction: string;
  report: string;
}

export interface EvaluationFixtureBaselineRunDigest {
  id: string;
  status: EvaluationSummaryStatus;
  totalCaseCount: number;
  executedCaseCount: number;
  passedCaseCount: number;
  failedCaseCount: number;
  skippedCaseCount: number;
  createdAt: string;
}

export type EvaluationFixtureBaselineRegressionStatus =
  | 'NO_ARCHIVES'
  | 'SINGLE_ARCHIVE'
  | 'STABLE'
  | 'REGRESSED'
  | 'IMPROVED';

export interface EvaluationFixtureBaselineRunRegressionSummary {
  status: EvaluationFixtureBaselineRegressionStatus;
  latestRun: EvaluationFixtureBaselineRunDigest | null;
  previousRun: EvaluationFixtureBaselineRunDigest | null;
  passedDelta: number;
  failedDelta: number;
  skippedDelta: number;
  latestFailedCaseIds: string[];
  newlyFailedCaseIds: string[];
  recoveredCaseIds: string[];
  sideEffectContract: string;
  nextAction: string;
  markdownReport: string;
}

export interface EvaluationRunPreview {
  status: EvaluationSummaryStatus;
  title: string;
  previewRunId: string;
  caseCount: number;
  supportedFixCaseCount: number;
  safetyRejectionCaseCount: number;
  coveredLanguages: string[];
  coveredBuildSystems: string[];
  expectedVerificationCommands: string[];
  safetyRejectionCategories: string[];
  gaps: string[];
  nextAction: string;
  readOnly: boolean;
  sideEffectContract: string;
  markdownReport: string;
}

export interface EvaluationRunSnapshotArchive {
  id: string;
  previewRunId: string;
  title: string;
  status: EvaluationSummaryStatus;
  caseCount: number;
  supportedFixCaseCount: number;
  safetyRejectionCaseCount: number;
  coveredLanguages: string[];
  coveredBuildSystems: string[];
  expectedVerificationCommands: string[];
  safetyRejectionCategories: string[];
  createdAt: string;
  sideEffectContract: string;
  report: string;
}

export interface EvaluationRunArchive {
  id: string;
  status: EvaluationSummaryStatus;
  totalCaseCount: number;
  supportedFixCaseCount: number;
  safetyRejectionCaseCount: number;
  executedFixCaseCount: number;
  passedFixCaseCount: number;
  failedFixCaseCount: number;
  skippedCaseCount: number;
  coveredLanguages: string[];
  coveredBuildSystems: string[];
  safetyRejectionCategories: string[];
  createdAt: string;
  sideEffectContract: string;
  nextAction: string;
  report: string;
}

export type EvaluationRunArchiveReadinessStatus = 'NO_ARCHIVES' | 'READY' | 'BLOCKED';

export interface EvaluationRunArchiveDigest {
  id: string;
  status: EvaluationSummaryStatus;
  totalCaseCount: number;
  supportedFixCaseCount: number;
  safetyRejectionCaseCount: number;
  executedFixCaseCount: number;
  passedFixCaseCount: number;
  failedFixCaseCount: number;
  skippedCaseCount: number;
  createdAt: string;
}

export interface EvaluationRunArchiveReadinessSummary {
  status: EvaluationRunArchiveReadinessStatus;
  latestRun: EvaluationRunArchiveDigest | null;
  previousRun: EvaluationRunArchiveDigest | null;
  passedDelta: number;
  failedDelta: number;
  skippedDelta: number;
  coveredLanguages: string[];
  coveredBuildSystems: string[];
  safetyRejectionCategories: string[];
  sideEffectContract: string;
  nextAction: string;
  markdownReport: string;
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

export type FixTaskWorkerState = 'NOT_STARTED' | 'POLLING' | 'IDLE' | 'ACTIVE' | 'ERROR';

export interface FixTaskWorkerHealth {
  state: FixTaskWorkerState;
  message: string;
  startedAt: string | null;
  lastPollAt: string | null;
  pollCount: number;
  claimedCount: number;
  completedCount: number;
  failedCount: number;
  idlePollCount: number;
  lastClaimedQueueItemId: string | null;
  lastClaimedTaskId: string | null;
  lastError: string | null;
  lastPollAgeMs: number;
  readinessStatus: 'READY' | 'NEEDS_ATTENTION';
  operatorAction: string;
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
  outcomeType: WebhookDeliveryOutcomeType | null;
  outcomeId: string | null;
  outcomeUrl: string | null;
  createdAt: string;
}

export type WebhookPayloadDiagnosticStatus =
  | 'READY_FOR_WEBHOOK'
  | 'INVALID_SIGNATURE'
  | 'MALFORMED_PAYLOAD'
  | 'UNSUPPORTED_EVENT'
  | 'UNSUPPORTED_ACTION'
  | 'IGNORED_COMMENT';

export type WebhookSignatureDiagnosticStatus = 'NOT_PROVIDED' | 'VALID' | 'INVALID';

export interface WebhookPayloadDiagnosticInput {
  event: string;
  deliveryId: string;
  signature: string;
  payload: string;
}

export interface WebhookPayloadDiagnosticResult {
  status: WebhookPayloadDiagnosticStatus;
  signatureStatus: WebhookSignatureDiagnosticStatus;
  validJson: boolean;
  supportedEvent: boolean;
  supportedAction: boolean;
  agentFixCommand: boolean;
  repositoryOwner: string | null;
  repositoryName: string | null;
  issueNumber: number | null;
  triggerUser: string | null;
  triggerComment: string | null;
  message: string;
  nextAction: string;
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
  retryable: boolean;
  retryBlockedReason: string | null;
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

export interface AcceptedTriggerDecision {
  id: string;
  taskId: string;
  repositoryOwner: string;
  repositoryName: string;
  issueNumber: number;
  triggerUser: string;
  triggerComment: string;
  taskStatus: TaskStatus;
  source: string;
  finalDecision: string;
  safetyDecision: TriggerEvaluationDecision;
  activeTaskDecision: TriggerEvaluationDecision;
  quarantineDecision: TriggerEvaluationDecision;
  rateLimitDecision: TriggerEvaluationDecision;
  triggerIntentDecision: TriggerEvaluationDecision;
  issueContextLoaded: boolean;
  createdAt: string;
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

export interface OperatorSafetyAudit {
  id: string;
  action: string;
  resourceType: string;
  resourceId: string;
  scope: TriggerQuarantineScope;
  scopeKey: string;
  operator: string;
  reason: string;
  createdAt: string;
}

export interface AdminAuditFilterOptions {
  limit?: number;
  action?: string;
  resourceType?: string;
  resourceId?: string;
  scope?: TriggerQuarantineScope;
  scopeKey?: string;
  operator?: string;
}

export interface TriggerQuarantineEvidence {
  quarantine: TriggerQuarantine;
  rejectedTriggers: RejectedTriggerAudit[];
  operatorSafetyAudits: OperatorSafetyAudit[];
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
  triggerIntentAudit: FixTaskTriggerIntentAudit | null;
  preExecutionSafetySnapshot: FixTaskPreExecutionSafetySnapshot | null;
  generatedDiff: FixTaskGeneratedDiff | null;
  patchReview: FixTaskPatchReview | null;
  issueContext: IssueContext | null;
  failureDiagnosis: FixTaskFailureDiagnosis | null;
  retryPreflight: FixTaskRetryPreflight | null;
  adapterExecutionEvidence: FixTaskAdapterExecutionEvidence | null;
  repositorySupportGuidance: RepositorySupportGuidance | null;
}

export interface FixTaskAdapterExecutionEvidence {
  status: string;
  language: string | null;
  buildSystem: string | null;
  verificationCommand: string | null;
  detectionReason: string | null;
  operatorAction: string;
  safetyNote: string;
  supportedAdapters: SupportedLanguageAdapter[];
}

export interface FixTaskTriggerIntentAudit {
  eventId: string;
  summary: string;
  safetyDecision: string;
  issueContextStatus: string;
  modelDecision: string;
  createdAt: string;
}

export interface FixTaskPreExecutionSafetySnapshot {
  eventId: string;
  source: string;
  finalDecision: string;
  safetyDecision: string;
  quarantineDecision: string;
  rateLimitDecision: string;
  issueContextStatus: string;
  modelDecision: string;
  createdAt: string;
}

export interface FixTaskFailureDiagnosis {
  category: string;
  nextAction: string;
  safeReason: string;
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
  eventType: FixTaskTimelineEventType;
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
