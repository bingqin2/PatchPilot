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
  latestDeliveryReceiptId: string | null;
  latestDeliveryTarget: string | null;
  latestDeliveryChannel: string | null;
  deliveryReceiptFreshness: string;
  createdAt: string;
  report: string;
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
  repositorySupportGuidance: RepositorySupportGuidance | null;
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
