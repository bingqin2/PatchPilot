import type {
  AdminAuditFilterOptions,
  ApproveReviewInput,
  ApiResponse,
  BackendHealth,
  ConfigurationSummary,
  CreateTriggerQuarantineInput,
  CreateTaskInput,
  DashboardBootstrap,
  DemoEvidenceBundle,
  DemoAcceptanceSummary,
  DemoFinalAcceptanceCompletionArchive,
  DemoFinalAcceptanceCompletionCloseoutArchive,
  DemoFinalAcceptanceCompletionCloseout,
  DemoFinalAcceptanceCompletionEvidenceBundle,
  DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput,
  DemoFinalExternalReviewEvidencePackage,
  DemoFinalExternalReviewEvidencePackageArchive,
  DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive,
  DemoFinalExternalReviewEvidencePackageDeliveryFinalization,
  DemoFinalExternalReviewEvidencePackageDeliveryReceipt,
  DemoFinalExternalReviewEvidencePackageDeliveryReceiptInput,
  DemoFinalExternalReviewDeliveryCertificateArchive,
  DemoFinalExternalReviewDeliveryCertificate,
  DemoFinalExternalReviewReleaseBundle,
  DemoFinalExternalReviewReleaseBundleArchive,
  DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchive,
  DemoFinalExternalReviewReleaseBundleDeliveryCertificate,
  DemoFinalExternalReviewReleaseBundleDeliveryFinalization,
  DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive,
  DemoFinalExternalReviewReleaseBundleDeliveryReceipt,
  DemoFinalExternalReviewReleaseBundleDeliveryReceiptInput,
  DemoFinalReviewerHandoffDeliveryFinalization,
  DemoFinalReviewerHandoffDeliveryReceipt,
  DemoFinalReviewerHandoffDeliveryReceiptInput,
  DemoFinalReviewerHandoffPackage,
  DemoFinalAcceptanceShareDeliveryReceipt,
  DemoFinalAcceptanceShareDeliveryReceiptInput,
  DemoFinalAcceptanceShareFinalization,
  DemoFinalAcceptanceSharePackageArchive,
  DemoFinalAcceptanceSharePackage,
  DemoFinalHandoffReportPackage,
  DemoFinalHandoffReportPackageArchive,
  DemoHandoffFinalization,
  DemoHandoffReadiness,
  DemoHandoffPackageArchive,
  DemoHandoffPackageArchiveSummary,
  DemoHandoffShareCenter,
  DemoHandoffShareDeliveryReceipt,
  DemoHandoffShareDeliveryReceiptInput,
  DemoHandoffShareInstructions,
  DemoHandoffShareChecklist,
  DemoLaunchAcceptanceCertificate,
  DemoLaunchAcceptanceCertificateArchive,
  DemoLaunchCommand,
  DemoLaunchAcceptanceCloseout,
  DemoLaunchAcceptanceCloseoutArchive,
  DemoLaunchEvidencePackageArchive,
  DemoLaunchEvidenceFinalization,
  DemoLaunchEvidencePackage,
  DemoLaunchEvidenceShareCenter,
  DemoLaunchEvidenceShareDeliveryReceipt,
  DemoLaunchEvidenceShareDeliveryReceiptInput,
  DemoLaunchCommandInput,
  DemoLiveLaunchGate,
  DemoLiveDemoEvidenceBundle,
  DemoLiveDemoEvidenceBundleArchive,
  DemoLiveDemoHandoffPackage,
  DemoLiveDemoHandoffDeliveryReceipt,
  DemoLiveDemoHandoffDeliveryReceiptInput,
  DemoLiveTriggerLaunchPackage,
  DemoLiveTriggerLaunchPackageArchive,
  DemoLiveTriggerOutcomeCloseout,
  DemoLiveTriggerOutcomeCloseoutArchive,
  DemoLiveTriggerOutcomeCloseoutInput,
  DemoEndToEndAcceptanceMatrix,
  DemoLaunchPreflight,
  DemoLaunchPreflightInput,
  DemoReadiness,
  DemoReadinessSnapshotArchive,
  DemoReadinessSnapshotTrend,
  DemoSelfHostedLaunchReadinessArchive,
  DemoSelfHostedLaunchReadiness,
  DemoScript,
  DemoSessionArchive,
  DemoSessionReportInput,
  DemoSessionSnapshot,
  DemoSmokeChecklist,
  EvaluationCase,
  EvaluationCaseFixtureReadinessSummary,
  EvaluationCaseSummary,
  EvaluationFixtureBaselineRunArchive,
  EvaluationFixtureBaselineRunRegressionSummary,
  EvaluationFixtureBaselineSummary,
  EvaluationRunArchive,
  EvaluationRunArchiveReadinessSummary,
  EvaluationRunPreview,
  EvaluationRunSnapshotArchive,
  ExternalExposureHandoffPackage,
  ExternalExposureCloseout,
  ExternalExposureCloseoutArchive,
  ExternalExposureOperatorHandoffChecklist,
  ExternalExposureOperatorHandoffChecklistArchive,
  ExternalExposureReadiness,
  ExternalExposureReadinessArchive,
  ExternalExposureSession,
  ExternalExposureSessionCloseInput,
  ExternalExposureSessionInput,
  FixTaskEvidencePackageAcceptanceCertificate,
  FixTaskEvidencePackageAcceptanceCertificateArchive,
  FixTask,
  FixTaskEvidencePackageAcceptanceCloseoutArchive,
  FixTaskEvidencePackageArchive,
  FixTaskEvidencePackageFinalization,
  FixTaskEvidencePackageArchiveShareCenter,
  FixTaskEvidencePackageArchiveSummary,
  FixTaskEvidencePackageShareDeliveryReceipt,
  FixTaskEvidencePackageShareDeliveryReceiptInput,
  FixTaskFailureCauseSummary,
  FixTaskLatencySummary,
  FixTaskPage,
  FixTaskDetail,
  FixTaskAuditSummary,
  FixTaskMetricsSummary,
  FixTaskStatusCounts,
  FixTaskModelUsageSummary,
  FixTaskRetryPreflight,
  FixTaskQueueItem,
  FixTaskQueueSummary,
  FixTaskWorkerHealth,
  GitHubCredentialReadiness,
  GitHubLivePublishPreflight,
  GitHubPublishPermissionReadiness,
  GitHubPublishReadiness,
  GitHubRepositoryAccessReadiness,
  GitHubTriggerDryRun,
  GitHubTriggerDryRunInput,
  GitHubWebhookSetupReadiness,
  GitHubWebhookUrlReadiness,
  OperatorSafetyAudit,
  AcceptedTriggerDecision,
  RejectedTriggerAudit,
  RejectedTriggerAuditSummary,
  ReleaseTriggerQuarantineInput,
  RejectedTriggerCategoryFilter,
  RetryTaskInput,
  TriggerQuarantine,
  TriggerQuarantineEvidence,
  WebhookDeliveryDiagnostic,
  WebhookPayloadDiagnosticInput,
  WebhookPayloadDiagnosticResult,
  FixTaskModelCall,
  FixTaskTestRun,
  FixTaskTimelineEvent,
  FixTaskToolCall,
  LanguageAdapterFixtureVerification,
  LanguageAdapterRuntimeReadiness,
  ModelProviderHealth,
  RepositoryPreflightInput,
  RepositoryPreflightResult,
  SupportedLanguageAdapter,
  TaskSort,
  TaskStatusFilter,
  TriggerEvaluationResult
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

export async function evaluateTrigger(input: CreateTaskInput): Promise<TriggerEvaluationResult> {
  return postApi<TriggerEvaluationResult>('/api/tasks/evaluate-trigger', input);
}

export async function postGitHubTriggerDryRun(input: GitHubTriggerDryRunInput): Promise<GitHubTriggerDryRun> {
  return postApi<GitHubTriggerDryRun>('/api/github/trigger-dry-run', input);
}

export async function postDemoLiveLaunchGate(input: GitHubTriggerDryRunInput): Promise<DemoLiveLaunchGate> {
  return postApi<DemoLiveLaunchGate>('/api/demo/live-launch-gate', input);
}

export async function postDemoLiveTriggerLaunchPackage(
  input: GitHubTriggerDryRunInput
): Promise<DemoLiveTriggerLaunchPackage> {
  return postApi<DemoLiveTriggerLaunchPackage>('/api/demo/live-trigger-launch-package', input);
}

export async function downloadDemoLiveTriggerLaunchPackageReport(input: GitHubTriggerDryRunInput): Promise<Blob> {
  return postBlobApi('/api/demo/live-trigger-launch-package/report/download', input);
}

export async function archiveDemoLiveTriggerLaunchPackage(
  input: GitHubTriggerDryRunInput
): Promise<DemoLiveTriggerLaunchPackageArchive> {
  return postApi<DemoLiveTriggerLaunchPackageArchive>('/api/demo/live-trigger-launch-package/archives', input);
}

export async function listDemoLiveTriggerLaunchPackageArchives(): Promise<DemoLiveTriggerLaunchPackageArchive[]> {
  return getApi<DemoLiveTriggerLaunchPackageArchive[]>('/api/demo/live-trigger-launch-package/archives');
}

export async function downloadDemoLiveTriggerLaunchPackageArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/live-trigger-launch-package/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function postDemoLiveTriggerOutcomeCloseout(
  input: DemoLiveTriggerOutcomeCloseoutInput
): Promise<DemoLiveTriggerOutcomeCloseout> {
  return postApi<DemoLiveTriggerOutcomeCloseout>('/api/demo/live-trigger-outcome-closeout', input);
}

export async function downloadDemoLiveTriggerOutcomeCloseoutReport(
  input: DemoLiveTriggerOutcomeCloseoutInput
): Promise<Blob> {
  return postBlobApi('/api/demo/live-trigger-outcome-closeout/report/download', input);
}

export async function archiveDemoLiveTriggerOutcomeCloseout(
  input: DemoLiveTriggerOutcomeCloseoutInput
): Promise<DemoLiveTriggerOutcomeCloseoutArchive> {
  return postApi<DemoLiveTriggerOutcomeCloseoutArchive>('/api/demo/live-trigger-outcome-closeout/archives', input);
}

export async function listDemoLiveTriggerOutcomeCloseoutArchives(): Promise<DemoLiveTriggerOutcomeCloseoutArchive[]> {
  return getApi<DemoLiveTriggerOutcomeCloseoutArchive[]>('/api/demo/live-trigger-outcome-closeout/archives');
}

export async function downloadDemoLiveTriggerOutcomeCloseoutArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(
    `/api/demo/live-trigger-outcome-closeout/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function getDemoLiveDemoEvidenceBundle(): Promise<DemoLiveDemoEvidenceBundle> {
  return getApi<DemoLiveDemoEvidenceBundle>('/api/demo/live-demo-evidence-bundle');
}

export async function downloadDemoLiveDemoEvidenceBundleReport(): Promise<Blob> {
  return getBlobApi('/api/demo/live-demo-evidence-bundle/report/download');
}

export async function archiveDemoLiveDemoEvidenceBundle(): Promise<DemoLiveDemoEvidenceBundleArchive> {
  return postApi<DemoLiveDemoEvidenceBundleArchive>('/api/demo/live-demo-evidence-bundle/archives');
}

export async function listDemoLiveDemoEvidenceBundleArchives(): Promise<DemoLiveDemoEvidenceBundleArchive[]> {
  return getApi<DemoLiveDemoEvidenceBundleArchive[]>('/api/demo/live-demo-evidence-bundle/archives');
}

export async function downloadDemoLiveDemoEvidenceBundleArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/live-demo-evidence-bundle/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function getDemoLiveDemoHandoffPackage(): Promise<DemoLiveDemoHandoffPackage> {
  return getApi<DemoLiveDemoHandoffPackage>('/api/demo/live-demo-handoff-package');
}

export async function downloadDemoLiveDemoHandoffPackageReport(): Promise<Blob> {
  return getBlobApi('/api/demo/live-demo-handoff-package/report/download');
}

export async function createDemoLiveDemoHandoffDeliveryReceipt(
  input: DemoLiveDemoHandoffDeliveryReceiptInput
): Promise<DemoLiveDemoHandoffDeliveryReceipt> {
  return postApi<DemoLiveDemoHandoffDeliveryReceipt>(
    '/api/demo/live-demo-handoff-package/delivery-receipts',
    input
  );
}

export async function listDemoLiveDemoHandoffDeliveryReceipts(): Promise<DemoLiveDemoHandoffDeliveryReceipt[]> {
  return getApi<DemoLiveDemoHandoffDeliveryReceipt[]>('/api/demo/live-demo-handoff-package/delivery-receipts');
}

export async function downloadDemoLiveDemoHandoffDeliveryReceiptReport(receiptId: string): Promise<Blob> {
  return getBlobApi(
    `/api/demo/live-demo-handoff-package/delivery-receipts/${encodeURIComponent(receiptId)}/report/download`
  );
}

export async function getDemoEndToEndAcceptanceMatrix(): Promise<DemoEndToEndAcceptanceMatrix> {
  return getApi<DemoEndToEndAcceptanceMatrix>('/api/demo/end-to-end-acceptance-matrix');
}

export async function getExternalExposureReadiness(): Promise<ExternalExposureReadiness> {
  return getApi<ExternalExposureReadiness>('/api/security/external-exposure-readiness');
}

export async function archiveExternalExposureReadiness(): Promise<ExternalExposureReadinessArchive> {
  return postApi<ExternalExposureReadinessArchive>('/api/security/external-exposure-readiness/archives');
}

export async function listExternalExposureReadinessArchives(): Promise<ExternalExposureReadinessArchive[]> {
  return getApi<ExternalExposureReadinessArchive[]>('/api/security/external-exposure-readiness/archives');
}

export async function downloadExternalExposureReadinessArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/security/external-exposure-readiness/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function getExternalExposureHandoffPackage(): Promise<ExternalExposureHandoffPackage> {
  return getApi<ExternalExposureHandoffPackage>('/api/security/external-exposure-handoff-package');
}

export async function downloadExternalExposureHandoffPackageReport(): Promise<Blob> {
  return getBlobApi('/api/security/external-exposure-handoff-package/report/download');
}

export async function getExternalExposureCloseout(): Promise<ExternalExposureCloseout> {
  return getApi<ExternalExposureCloseout>('/api/security/external-exposure-closeout');
}

export async function downloadExternalExposureCloseoutReport(): Promise<Blob> {
  return getBlobApi('/api/security/external-exposure-closeout/report/download');
}

export async function archiveExternalExposureCloseout(): Promise<ExternalExposureCloseoutArchive> {
  return postApi<ExternalExposureCloseoutArchive>('/api/security/external-exposure-closeout/archives');
}

export async function listExternalExposureCloseoutArchives(): Promise<ExternalExposureCloseoutArchive[]> {
  return getApi<ExternalExposureCloseoutArchive[]>('/api/security/external-exposure-closeout/archives');
}

export async function downloadExternalExposureCloseoutArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(
    `/api/security/external-exposure-closeout/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function getExternalExposureOperatorHandoffChecklist(): Promise<ExternalExposureOperatorHandoffChecklist> {
  return getApi<ExternalExposureOperatorHandoffChecklist>(
    '/api/security/external-exposure-operator-handoff-checklist'
  );
}

export async function downloadExternalExposureOperatorHandoffChecklistReport(): Promise<Blob> {
  return getBlobApi('/api/security/external-exposure-operator-handoff-checklist/report/download');
}

export async function archiveExternalExposureOperatorHandoffChecklist(): Promise<ExternalExposureOperatorHandoffChecklistArchive> {
  return postApi<ExternalExposureOperatorHandoffChecklistArchive>(
    '/api/security/external-exposure-operator-handoff-checklist/archives'
  );
}

export async function listExternalExposureOperatorHandoffChecklistArchives(): Promise<ExternalExposureOperatorHandoffChecklistArchive[]> {
  return getApi<ExternalExposureOperatorHandoffChecklistArchive[]>(
    '/api/security/external-exposure-operator-handoff-checklist/archives'
  );
}

export async function downloadExternalExposureOperatorHandoffChecklistArchiveReport(
  archiveId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/security/external-exposure-operator-handoff-checklist/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function startExternalExposureSession(
  input: ExternalExposureSessionInput
): Promise<ExternalExposureSession> {
  return postApi<ExternalExposureSession>('/api/security/external-exposure-sessions', input);
}

export async function closeExternalExposureSession(
  sessionId: string,
  input: ExternalExposureSessionCloseInput
): Promise<ExternalExposureSession> {
  return postApi<ExternalExposureSession>(
    `/api/security/external-exposure-sessions/${encodeURIComponent(sessionId)}/close`,
    input
  );
}

export async function listExternalExposureSessions(): Promise<ExternalExposureSession[]> {
  return getApi<ExternalExposureSession[]>('/api/security/external-exposure-sessions');
}

export async function downloadExternalExposureSessionReport(sessionId: string): Promise<Blob> {
  return getBlobApi(`/api/security/external-exposure-sessions/${encodeURIComponent(sessionId)}/report/download`);
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

function appendSearchParam(searchParams: URLSearchParams, key: string, value: string | undefined | null) {
  if (value?.trim()) {
    searchParams.set(key, value.trim());
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

export async function getModelProviderHealth(): Promise<ModelProviderHealth> {
  return getApi<ModelProviderHealth>('/api/model-provider/health');
}

export async function getGitHubCredentialReadiness(): Promise<GitHubCredentialReadiness> {
  return getApi<GitHubCredentialReadiness>('/api/github/credential-readiness');
}

export async function getGitHubWebhookUrlReadiness(): Promise<GitHubWebhookUrlReadiness> {
  return getApi<GitHubWebhookUrlReadiness>('/api/github/webhook-url-readiness');
}

export async function getGitHubWebhookSetupReadiness(): Promise<GitHubWebhookSetupReadiness> {
  return getApi<GitHubWebhookSetupReadiness>('/api/github/webhook-setup-readiness');
}

export async function getGitHubRepositoryAccessReadiness(
  owner?: string,
  repository?: string
): Promise<GitHubRepositoryAccessReadiness> {
  const searchParams = new URLSearchParams();
  appendSearchParam(searchParams, 'owner', owner);
  appendSearchParam(searchParams, 'repository', repository);
  const queryString = searchParams.toString();
  return getApi<GitHubRepositoryAccessReadiness>(
    queryString ? `/api/github/repository-access-readiness?${queryString}` : '/api/github/repository-access-readiness'
  );
}

export async function getGitHubPublishReadiness(
  owner?: string,
  repository?: string
): Promise<GitHubPublishReadiness> {
  const searchParams = new URLSearchParams();
  appendSearchParam(searchParams, 'owner', owner);
  appendSearchParam(searchParams, 'repository', repository);
  const queryString = searchParams.toString();
  return getApi<GitHubPublishReadiness>(
    queryString ? `/api/github/publish-readiness?${queryString}` : '/api/github/publish-readiness'
  );
}

export async function getGitHubPublishPermissionReadiness(
  owner?: string,
  repository?: string
): Promise<GitHubPublishPermissionReadiness> {
  const searchParams = new URLSearchParams();
  appendSearchParam(searchParams, 'owner', owner);
  appendSearchParam(searchParams, 'repository', repository);
  const queryString = searchParams.toString();
  return getApi<GitHubPublishPermissionReadiness>(
    queryString ? `/api/github/publish-permission-readiness?${queryString}` : '/api/github/publish-permission-readiness'
  );
}

export async function getGitHubLivePublishPreflight(
  owner?: string,
  repository?: string
): Promise<GitHubLivePublishPreflight> {
  const searchParams = new URLSearchParams();
  appendSearchParam(searchParams, 'owner', owner);
  appendSearchParam(searchParams, 'repository', repository);
  const queryString = searchParams.toString();
  return getApi<GitHubLivePublishPreflight>(
    queryString ? `/api/github/live-publish-preflight?${queryString}` : '/api/github/live-publish-preflight'
  );
}

export async function getDemoReadiness(): Promise<DemoReadiness> {
  return getApi<DemoReadiness>('/api/demo/readiness');
}

export async function getDemoAcceptanceSummary(): Promise<DemoAcceptanceSummary> {
  return getApi<DemoAcceptanceSummary>('/api/demo/acceptance-summary');
}

export async function downloadDemoAcceptanceSummaryReport(): Promise<Blob> {
  return getBlobApi('/api/demo/acceptance-summary/report/download');
}

export async function getDemoFinalAcceptanceSharePackage(): Promise<DemoFinalAcceptanceSharePackage> {
  return getApi<DemoFinalAcceptanceSharePackage>('/api/demo/final-acceptance-share-package');
}

export async function downloadDemoFinalAcceptanceSharePackageReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-acceptance-share-package/report/download');
}

export async function archiveDemoFinalAcceptanceSharePackage(): Promise<DemoFinalAcceptanceSharePackageArchive> {
  return postApi<DemoFinalAcceptanceSharePackageArchive>('/api/demo/final-acceptance-share-package/archives');
}

export async function listDemoFinalAcceptanceSharePackageArchives(): Promise<DemoFinalAcceptanceSharePackageArchive[]> {
  return getApi<DemoFinalAcceptanceSharePackageArchive[]>('/api/demo/final-acceptance-share-package/archives');
}

export async function downloadDemoFinalAcceptanceSharePackageArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/final-acceptance-share-package/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function createDemoFinalAcceptanceShareDeliveryReceipt(
  input: DemoFinalAcceptanceShareDeliveryReceiptInput
): Promise<DemoFinalAcceptanceShareDeliveryReceipt> {
  return postApi<DemoFinalAcceptanceShareDeliveryReceipt>('/api/demo/final-acceptance-share-delivery-receipts', input);
}

export async function listDemoFinalAcceptanceShareDeliveryReceipts(): Promise<DemoFinalAcceptanceShareDeliveryReceipt[]> {
  return getApi<DemoFinalAcceptanceShareDeliveryReceipt[]>('/api/demo/final-acceptance-share-delivery-receipts');
}

export async function downloadDemoFinalAcceptanceShareDeliveryReceiptReport(receiptId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/final-acceptance-share-delivery-receipts/${encodeURIComponent(receiptId)}/report/download`);
}

export async function getDemoFinalAcceptanceShareFinalization(): Promise<DemoFinalAcceptanceShareFinalization> {
  return getApi<DemoFinalAcceptanceShareFinalization>('/api/demo/final-acceptance-share-finalization');
}

export async function downloadDemoFinalAcceptanceShareFinalizationReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-acceptance-share-finalization/report/download');
}

export async function getDemoFinalAcceptanceCompletionEvidenceBundle(): Promise<DemoFinalAcceptanceCompletionEvidenceBundle> {
  return getApi<DemoFinalAcceptanceCompletionEvidenceBundle>('/api/demo/final-acceptance-completion-evidence-bundle');
}

export async function downloadDemoFinalAcceptanceCompletionEvidenceBundleReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-acceptance-completion-evidence-bundle/report/download');
}

export async function getDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization(): Promise<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization> {
  return getApi<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization>(
    '/api/demo/final-acceptance-completion-evidence-delivery-finalization'
  );
}

export async function downloadDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-acceptance-completion-evidence-delivery-finalization/report/download');
}

export async function getDemoFinalAcceptanceCompletionCloseout(): Promise<DemoFinalAcceptanceCompletionCloseout> {
  return getApi<DemoFinalAcceptanceCompletionCloseout>('/api/demo/final-acceptance-completion-closeout');
}

export async function downloadDemoFinalAcceptanceCompletionCloseoutReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-acceptance-completion-closeout/report/download');
}

export async function getDemoFinalExternalReviewEvidencePackage(): Promise<DemoFinalExternalReviewEvidencePackage> {
  return getApi<DemoFinalExternalReviewEvidencePackage>('/api/demo/final-external-review-evidence-package');
}

export async function downloadDemoFinalExternalReviewEvidencePackageReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-external-review-evidence-package/report/download');
}

export async function archiveDemoFinalExternalReviewEvidencePackage(): Promise<DemoFinalExternalReviewEvidencePackageArchive> {
  return postApi<DemoFinalExternalReviewEvidencePackageArchive>(
    '/api/demo/final-external-review-evidence-package/archives'
  );
}

export async function listDemoFinalExternalReviewEvidencePackageArchives(): Promise<DemoFinalExternalReviewEvidencePackageArchive[]> {
  return getApi<DemoFinalExternalReviewEvidencePackageArchive[]>(
    '/api/demo/final-external-review-evidence-package/archives'
  );
}

export async function downloadDemoFinalExternalReviewEvidencePackageArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-external-review-evidence-package/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function createDemoFinalExternalReviewEvidencePackageDeliveryReceipt(
  input: DemoFinalExternalReviewEvidencePackageDeliveryReceiptInput
): Promise<DemoFinalExternalReviewEvidencePackageDeliveryReceipt> {
  return postApi<DemoFinalExternalReviewEvidencePackageDeliveryReceipt>(
    '/api/demo/final-external-review-evidence-package/delivery-receipts',
    input
  );
}

export async function listDemoFinalExternalReviewEvidencePackageDeliveryReceipts(): Promise<
  DemoFinalExternalReviewEvidencePackageDeliveryReceipt[]
> {
  return getApi<DemoFinalExternalReviewEvidencePackageDeliveryReceipt[]>(
    '/api/demo/final-external-review-evidence-package/delivery-receipts'
  );
}

export async function downloadDemoFinalExternalReviewEvidencePackageDeliveryReceiptReport(
  receiptId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-external-review-evidence-package/delivery-receipts/${encodeURIComponent(receiptId)}/report/download`
  );
}

export async function getDemoFinalExternalReviewEvidencePackageDeliveryFinalization(): Promise<
  DemoFinalExternalReviewEvidencePackageDeliveryFinalization
> {
  return getApi<DemoFinalExternalReviewEvidencePackageDeliveryFinalization>(
    '/api/demo/final-external-review-evidence-package/delivery-finalization'
  );
}

export async function downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-external-review-evidence-package/delivery-finalization/report/download');
}

export async function archiveDemoFinalExternalReviewEvidencePackageDeliveryFinalization(): Promise<
  DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive
> {
  return postApi<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive>(
    '/api/demo/final-external-review-evidence-package/delivery-finalization/archives'
  );
}

export async function listDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives(): Promise<
  DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive[]
> {
  return getApi<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive[]>(
    '/api/demo/final-external-review-evidence-package/delivery-finalization/archives'
  );
}

export async function downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport(
  archiveId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-external-review-evidence-package/delivery-finalization/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function getDemoFinalExternalReviewDeliveryCertificate(): Promise<
  DemoFinalExternalReviewDeliveryCertificate
> {
  return getApi<DemoFinalExternalReviewDeliveryCertificate>(
    '/api/demo/final-external-review-delivery-certificate'
  );
}

export async function downloadDemoFinalExternalReviewDeliveryCertificateReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-external-review-delivery-certificate/report/download');
}

export async function archiveDemoFinalExternalReviewDeliveryCertificate(): Promise<
  DemoFinalExternalReviewDeliveryCertificateArchive
> {
  return postApi<DemoFinalExternalReviewDeliveryCertificateArchive>(
    '/api/demo/final-external-review-delivery-certificate/archives'
  );
}

export async function listDemoFinalExternalReviewDeliveryCertificateArchives(): Promise<
  DemoFinalExternalReviewDeliveryCertificateArchive[]
> {
  return getApi<DemoFinalExternalReviewDeliveryCertificateArchive[]>(
    '/api/demo/final-external-review-delivery-certificate/archives'
  );
}

export async function downloadDemoFinalExternalReviewDeliveryCertificateArchiveReport(
  archiveId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-external-review-delivery-certificate/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function getDemoFinalExternalReviewReleaseBundle(): Promise<
  DemoFinalExternalReviewReleaseBundle
> {
  return getApi<DemoFinalExternalReviewReleaseBundle>(
    '/api/demo/final-external-review-release-bundle'
  );
}

export async function downloadDemoFinalExternalReviewReleaseBundleReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-external-review-release-bundle/report/download');
}

export async function archiveDemoFinalExternalReviewReleaseBundle(): Promise<
  DemoFinalExternalReviewReleaseBundleArchive
> {
  return postApi<DemoFinalExternalReviewReleaseBundleArchive>(
    '/api/demo/final-external-review-release-bundle/archives'
  );
}

export async function listDemoFinalExternalReviewReleaseBundleArchives(): Promise<
  DemoFinalExternalReviewReleaseBundleArchive[]
> {
  return getApi<DemoFinalExternalReviewReleaseBundleArchive[]>(
    '/api/demo/final-external-review-release-bundle/archives'
  );
}

export async function downloadDemoFinalExternalReviewReleaseBundleArchiveReport(
  archiveId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-external-review-release-bundle/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function createDemoFinalExternalReviewReleaseBundleDeliveryReceipt(
  input: DemoFinalExternalReviewReleaseBundleDeliveryReceiptInput
): Promise<DemoFinalExternalReviewReleaseBundleDeliveryReceipt> {
  return postApi<DemoFinalExternalReviewReleaseBundleDeliveryReceipt>(
    '/api/demo/final-external-review-release-bundle/delivery-receipts',
    input
  );
}

export async function listDemoFinalExternalReviewReleaseBundleDeliveryReceipts(): Promise<
  DemoFinalExternalReviewReleaseBundleDeliveryReceipt[]
> {
  return getApi<DemoFinalExternalReviewReleaseBundleDeliveryReceipt[]>(
    '/api/demo/final-external-review-release-bundle/delivery-receipts'
  );
}

export async function downloadDemoFinalExternalReviewReleaseBundleDeliveryReceiptReport(
  receiptId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-external-review-release-bundle/delivery-receipts/${encodeURIComponent(receiptId)}/report/download`
  );
}

export async function getDemoFinalExternalReviewReleaseBundleDeliveryFinalization(): Promise<
  DemoFinalExternalReviewReleaseBundleDeliveryFinalization
> {
  return getApi<DemoFinalExternalReviewReleaseBundleDeliveryFinalization>(
    '/api/demo/final-external-review-release-bundle/delivery-finalization'
  );
}

export async function downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-external-review-release-bundle/delivery-finalization/report/download');
}

export async function archiveDemoFinalExternalReviewReleaseBundleDeliveryFinalization(): Promise<
  DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive
> {
  return postApi<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive>(
    '/api/demo/final-external-review-release-bundle/delivery-finalization/archives'
  );
}

export async function listDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives(): Promise<
  DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive[]
> {
  return getApi<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive[]>(
    '/api/demo/final-external-review-release-bundle/delivery-finalization/archives'
  );
}

export async function downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport(
  archiveId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-external-review-release-bundle/delivery-finalization/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function getDemoFinalExternalReviewReleaseBundleDeliveryCertificate(): Promise<
  DemoFinalExternalReviewReleaseBundleDeliveryCertificate
> {
  return getApi<DemoFinalExternalReviewReleaseBundleDeliveryCertificate>(
    '/api/demo/final-external-review-release-bundle/delivery-certificate'
  );
}

export async function downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-external-review-release-bundle/delivery-certificate/report/download');
}

export async function archiveDemoFinalExternalReviewReleaseBundleDeliveryCertificate(): Promise<
  DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchive
> {
  return postApi<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchive>(
    '/api/demo/final-external-review-release-bundle/delivery-certificate/archives'
  );
}

export async function listDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives(): Promise<
  DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchive[]
> {
  return getApi<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchive[]>(
    '/api/demo/final-external-review-release-bundle/delivery-certificate/archives'
  );
}

export async function downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveReport(
  archiveId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-external-review-release-bundle/delivery-certificate/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function getDemoFinalReviewerHandoffPackage(): Promise<DemoFinalReviewerHandoffPackage> {
  return getApi<DemoFinalReviewerHandoffPackage>('/api/demo/final-reviewer-handoff-package');
}

export async function downloadDemoFinalReviewerHandoffPackageReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-reviewer-handoff-package/report/download');
}

export async function createDemoFinalReviewerHandoffDeliveryReceipt(
  input: DemoFinalReviewerHandoffDeliveryReceiptInput
): Promise<DemoFinalReviewerHandoffDeliveryReceipt> {
  return postApi<DemoFinalReviewerHandoffDeliveryReceipt>(
    '/api/demo/final-reviewer-handoff-package/delivery-receipts',
    input
  );
}

export async function listDemoFinalReviewerHandoffDeliveryReceipts(): Promise<
  DemoFinalReviewerHandoffDeliveryReceipt[]
> {
  return getApi<DemoFinalReviewerHandoffDeliveryReceipt[]>(
    '/api/demo/final-reviewer-handoff-package/delivery-receipts'
  );
}

export async function downloadDemoFinalReviewerHandoffDeliveryReceiptReport(
  receiptId: string
): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-reviewer-handoff-package/delivery-receipts/${encodeURIComponent(receiptId)}/report/download`
  );
}

export async function getDemoFinalReviewerHandoffDeliveryFinalization(): Promise<
  DemoFinalReviewerHandoffDeliveryFinalization
> {
  return getApi<DemoFinalReviewerHandoffDeliveryFinalization>(
    '/api/demo/final-reviewer-handoff-package/delivery-finalization'
  );
}

export async function downloadDemoFinalReviewerHandoffDeliveryFinalizationReport(): Promise<Blob> {
  return getBlobApi('/api/demo/final-reviewer-handoff-package/delivery-finalization/report/download');
}

export async function archiveDemoFinalAcceptanceCompletionCloseout(): Promise<DemoFinalAcceptanceCompletionCloseoutArchive> {
  return postApi<DemoFinalAcceptanceCompletionCloseoutArchive>(
    '/api/demo/final-acceptance-completion-closeout/archives'
  );
}

export async function listDemoFinalAcceptanceCompletionCloseoutArchives(): Promise<DemoFinalAcceptanceCompletionCloseoutArchive[]> {
  return getApi<DemoFinalAcceptanceCompletionCloseoutArchive[]>(
    '/api/demo/final-acceptance-completion-closeout/archives'
  );
}

export async function downloadDemoFinalAcceptanceCompletionCloseoutArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-acceptance-completion-closeout/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function createDemoFinalAcceptanceCompletionEvidenceDeliveryReceipt(
  input: DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput
): Promise<DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt> {
  return postApi<DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt>(
    '/api/demo/final-acceptance-completion-evidence-delivery-receipts',
    input
  );
}

export async function listDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts(): Promise<DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt[]> {
  return getApi<DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt[]>(
    '/api/demo/final-acceptance-completion-evidence-delivery-receipts'
  );
}

export async function downloadDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptReport(receiptId: string): Promise<Blob> {
  return getBlobApi(
    `/api/demo/final-acceptance-completion-evidence-delivery-receipts/${encodeURIComponent(receiptId)}/report/download`
  );
}

export async function archiveDemoFinalAcceptanceCompletion(): Promise<DemoFinalAcceptanceCompletionArchive> {
  return postApi<DemoFinalAcceptanceCompletionArchive>('/api/demo/final-acceptance-completion-archives');
}

export async function listDemoFinalAcceptanceCompletionArchives(): Promise<DemoFinalAcceptanceCompletionArchive[]> {
  return getApi<DemoFinalAcceptanceCompletionArchive[]>('/api/demo/final-acceptance-completion-archives');
}

export async function downloadDemoFinalAcceptanceCompletionArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/final-acceptance-completion-archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function getDemoSelfHostedLaunchReadiness(): Promise<DemoSelfHostedLaunchReadiness> {
  return getApi<DemoSelfHostedLaunchReadiness>('/api/demo/self-hosted-launch-readiness');
}

export async function downloadDemoSelfHostedLaunchReadinessReport(): Promise<Blob> {
  return getBlobApi('/api/demo/self-hosted-launch-readiness/report/download');
}

export async function archiveDemoSelfHostedLaunchReadiness(): Promise<DemoSelfHostedLaunchReadinessArchive> {
  return postApi<DemoSelfHostedLaunchReadinessArchive>('/api/demo/self-hosted-launch-readiness/archives');
}

export async function listDemoSelfHostedLaunchReadinessArchives(): Promise<DemoSelfHostedLaunchReadinessArchive[]> {
  return getApi<DemoSelfHostedLaunchReadinessArchive[]>('/api/demo/self-hosted-launch-readiness/archives');
}

export async function downloadDemoSelfHostedLaunchReadinessArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/self-hosted-launch-readiness/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function getDemoLaunchEvidencePackage(): Promise<DemoLaunchEvidencePackage> {
  return getApi<DemoLaunchEvidencePackage>('/api/demo/launch-evidence-package');
}

export async function downloadDemoLaunchEvidencePackageReport(): Promise<Blob> {
  return getBlobApi('/api/demo/launch-evidence-package/report/download');
}

export async function archiveDemoLaunchEvidencePackage(): Promise<DemoLaunchEvidencePackageArchive> {
  return postApi<DemoLaunchEvidencePackageArchive>('/api/demo/launch-evidence-package/archives');
}

export async function listDemoLaunchEvidencePackageArchives(): Promise<DemoLaunchEvidencePackageArchive[]> {
  return getApi<DemoLaunchEvidencePackageArchive[]>('/api/demo/launch-evidence-package/archives');
}

export async function downloadDemoLaunchEvidencePackageArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/launch-evidence-package/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function getDemoLaunchEvidenceShareCenter(): Promise<DemoLaunchEvidenceShareCenter> {
  return getApi<DemoLaunchEvidenceShareCenter>('/api/demo/launch-evidence-share-center');
}

export async function downloadDemoLaunchEvidenceShareCenterReport(): Promise<Blob> {
  return getBlobApi('/api/demo/launch-evidence-share-center/report/download');
}

export async function getDemoLaunchEvidenceFinalization(): Promise<DemoLaunchEvidenceFinalization> {
  return getApi<DemoLaunchEvidenceFinalization>('/api/demo/launch-evidence-finalization');
}

export async function downloadDemoLaunchEvidenceFinalizationReport(): Promise<Blob> {
  return getBlobApi('/api/demo/launch-evidence-finalization/report/download');
}

export async function getDemoLaunchAcceptanceCloseout(): Promise<DemoLaunchAcceptanceCloseout> {
  return getApi<DemoLaunchAcceptanceCloseout>('/api/demo/launch-acceptance-closeout');
}

export async function getDemoLaunchAcceptanceCertificate(): Promise<DemoLaunchAcceptanceCertificate> {
  return getApi<DemoLaunchAcceptanceCertificate>('/api/demo/launch-acceptance-certificate');
}

export async function downloadDemoLaunchAcceptanceCloseoutReport(): Promise<Blob> {
  return getBlobApi('/api/demo/launch-acceptance-closeout/report/download');
}

export async function downloadDemoLaunchAcceptanceCertificateReport(): Promise<Blob> {
  return getBlobApi('/api/demo/launch-acceptance-certificate/report/download');
}

export async function archiveDemoLaunchAcceptanceCertificate(): Promise<DemoLaunchAcceptanceCertificateArchive> {
  return postApi<DemoLaunchAcceptanceCertificateArchive>('/api/demo/launch-acceptance-certificate/archives');
}

export async function listDemoLaunchAcceptanceCertificateArchives(): Promise<DemoLaunchAcceptanceCertificateArchive[]> {
  return getApi<DemoLaunchAcceptanceCertificateArchive[]>('/api/demo/launch-acceptance-certificate/archives');
}

export async function downloadDemoLaunchAcceptanceCertificateArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/launch-acceptance-certificate/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function archiveDemoLaunchAcceptanceCloseout(): Promise<DemoLaunchAcceptanceCloseoutArchive> {
  return postApi<DemoLaunchAcceptanceCloseoutArchive>('/api/demo/launch-acceptance-closeout/archives');
}

export async function listDemoLaunchAcceptanceCloseoutArchives(): Promise<DemoLaunchAcceptanceCloseoutArchive[]> {
  return getApi<DemoLaunchAcceptanceCloseoutArchive[]>('/api/demo/launch-acceptance-closeout/archives');
}

export async function downloadDemoLaunchAcceptanceCloseoutArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/launch-acceptance-closeout/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function createDemoLaunchEvidenceShareDeliveryReceipt(
  input: DemoLaunchEvidenceShareDeliveryReceiptInput
): Promise<DemoLaunchEvidenceShareDeliveryReceipt> {
  return postApi<DemoLaunchEvidenceShareDeliveryReceipt>('/api/demo/launch-evidence-share-delivery-receipts', input);
}

export async function listDemoLaunchEvidenceShareDeliveryReceipts(): Promise<DemoLaunchEvidenceShareDeliveryReceipt[]> {
  return getApi<DemoLaunchEvidenceShareDeliveryReceipt[]>('/api/demo/launch-evidence-share-delivery-receipts');
}

export async function downloadDemoLaunchEvidenceShareDeliveryReceiptReport(receiptId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/launch-evidence-share-delivery-receipts/${encodeURIComponent(receiptId)}/report/download`);
}

export async function archiveDemoReadinessSnapshot(): Promise<DemoReadinessSnapshotArchive> {
  return postApi<DemoReadinessSnapshotArchive>('/api/demo/readiness-snapshots');
}

export async function listDemoReadinessSnapshots(): Promise<DemoReadinessSnapshotArchive[]> {
  return getApi<DemoReadinessSnapshotArchive[]>('/api/demo/readiness-snapshots');
}

export async function getDemoReadinessSnapshotTrend(): Promise<DemoReadinessSnapshotTrend> {
  return getApi<DemoReadinessSnapshotTrend>('/api/demo/readiness-snapshots/summary');
}

export async function downloadDemoReadinessSnapshotReport(snapshotId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/readiness-snapshots/${encodeURIComponent(snapshotId)}/report/download`);
}

export async function preflightDemoLaunch(input: DemoLaunchPreflightInput): Promise<DemoLaunchPreflight> {
  return postApi<DemoLaunchPreflight>('/api/demo/launch-preflight', input);
}

export async function composeDemoLaunchCommand(input: DemoLaunchCommandInput): Promise<DemoLaunchCommand> {
  return postApi<DemoLaunchCommand>('/api/demo/launch-command', input);
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

export async function getDemoSessionReport(input?: DemoSessionReportInput): Promise<string> {
  return input ? postApi<string>('/api/demo/session-report', input) : getApi<string>('/api/demo/session-report');
}

export async function getDemoHandoffPackage(input: DemoSessionReportInput): Promise<string> {
  return postApi<string>('/api/demo/handoff-package', input);
}

export async function getDemoHandoffReadiness(input: DemoSessionReportInput): Promise<DemoHandoffReadiness> {
  return postApi<DemoHandoffReadiness>('/api/demo/handoff-readiness', input);
}

export async function downloadDemoSessionReport(input?: DemoSessionReportInput): Promise<Blob> {
  return input ? postBlobApi('/api/demo/session-report/download', input) : getBlobApi('/api/demo/session-report/download');
}

export async function downloadDemoHandoffPackage(input: DemoSessionReportInput): Promise<Blob> {
  return postBlobApi('/api/demo/handoff-package/download', input);
}

export async function archiveDemoHandoffPackage(input?: DemoSessionReportInput): Promise<DemoHandoffPackageArchive> {
  return postApi<DemoHandoffPackageArchive>('/api/demo/handoff-package-archives', input);
}

export async function listDemoHandoffPackageArchives(): Promise<DemoHandoffPackageArchive[]> {
  return getApi<DemoHandoffPackageArchive[]>('/api/demo/handoff-package-archives');
}

export async function getDemoHandoffPackageArchiveSummary(): Promise<DemoHandoffPackageArchiveSummary> {
  return getApi<DemoHandoffPackageArchiveSummary>('/api/demo/handoff-package-archives/summary');
}

export async function getDemoHandoffShareChecklist(): Promise<DemoHandoffShareChecklist> {
  return getApi<DemoHandoffShareChecklist>('/api/demo/handoff-share-checklist');
}

export async function downloadDemoHandoffShareChecklistReport(): Promise<Blob> {
  return getBlobApi('/api/demo/handoff-share-checklist/report/download');
}

export async function getDemoHandoffShareCenter(): Promise<DemoHandoffShareCenter> {
  return getApi<DemoHandoffShareCenter>('/api/demo/handoff-share-center');
}

export async function getDemoHandoffFinalization(): Promise<DemoHandoffFinalization> {
  return getApi<DemoHandoffFinalization>('/api/demo/handoff-finalization');
}

export async function getDemoFinalHandoffReportPackage(): Promise<DemoFinalHandoffReportPackage> {
  return getApi<DemoFinalHandoffReportPackage>('/api/demo/final-handoff-report-package');
}

export async function archiveDemoFinalHandoffReportPackage(): Promise<DemoFinalHandoffReportPackageArchive> {
  return postApi<DemoFinalHandoffReportPackageArchive>('/api/demo/final-handoff-report-package/archives');
}

export async function listDemoFinalHandoffReportPackageArchives(): Promise<DemoFinalHandoffReportPackageArchive[]> {
  return getApi<DemoFinalHandoffReportPackageArchive[]>('/api/demo/final-handoff-report-package/archives');
}

export async function getDemoHandoffShareInstructions(): Promise<DemoHandoffShareInstructions> {
  return getApi<DemoHandoffShareInstructions>('/api/demo/handoff-share-instructions');
}

export async function createDemoHandoffShareDeliveryReceipt(
  input: DemoHandoffShareDeliveryReceiptInput
): Promise<DemoHandoffShareDeliveryReceipt> {
  return postApi<DemoHandoffShareDeliveryReceipt>('/api/demo/handoff-share-delivery-receipts', input);
}

export async function listDemoHandoffShareDeliveryReceipts(): Promise<DemoHandoffShareDeliveryReceipt[]> {
  return getApi<DemoHandoffShareDeliveryReceipt[]>('/api/demo/handoff-share-delivery-receipts');
}

export async function downloadDemoHandoffShareCenterReport(): Promise<Blob> {
  return getBlobApi('/api/demo/handoff-share-center/report/download');
}

export async function downloadDemoHandoffFinalizationReport(): Promise<Blob> {
  return getBlobApi('/api/demo/handoff-finalization/report/download');
}

export async function downloadDemoFinalHandoffReportPackage(): Promise<Blob> {
  return getBlobApi('/api/demo/final-handoff-report-package/report/download');
}

export async function downloadDemoFinalHandoffReportPackageArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/final-handoff-report-package/archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function downloadDemoHandoffShareInstructionsReport(): Promise<Blob> {
  return getBlobApi('/api/demo/handoff-share-instructions/report/download');
}

export async function downloadDemoHandoffShareDeliveryReceiptReport(receiptId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/handoff-share-delivery-receipts/${encodeURIComponent(receiptId)}/report/download`);
}

export async function downloadDemoHandoffPackageArchiveSummaryReport(): Promise<Blob> {
  return getBlobApi('/api/demo/handoff-package-archives/summary-report/download');
}

export async function downloadDemoHandoffPackageArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/handoff-package-archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function archiveDemoSession(input?: DemoSessionReportInput): Promise<DemoSessionArchive> {
  return postApi<DemoSessionArchive>('/api/demo/session-archives', input);
}

export async function listDemoSessionArchives(): Promise<DemoSessionArchive[]> {
  return getApi<DemoSessionArchive[]>('/api/demo/session-archives');
}

export async function downloadDemoSessionArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/demo/session-archives/${encodeURIComponent(archiveId)}/report/download`);
}

export async function getDemoRunbook(): Promise<string> {
  return getApi<string>('/api/demo/runbook');
}

export async function getBackendHealth(): Promise<BackendHealth> {
  return getApi<BackendHealth>('/health');
}

export async function getDashboardBootstrap(): Promise<DashboardBootstrap> {
  return getApi<DashboardBootstrap>('/api/dashboard/bootstrap');
}

export async function listLanguageAdapters(): Promise<SupportedLanguageAdapter[]> {
  return getApi<SupportedLanguageAdapter[]>('/api/language-adapters');
}

export async function listLanguageAdapterFixtures(): Promise<LanguageAdapterFixtureVerification[]> {
  return getApi<LanguageAdapterFixtureVerification[]>('/api/language-adapters/fixtures');
}

export async function listLanguageAdapterRuntimeReadiness(): Promise<LanguageAdapterRuntimeReadiness[]> {
  return getApi<LanguageAdapterRuntimeReadiness[]>('/api/language-adapters/runtime-readiness');
}

export async function listEvaluationCases(): Promise<EvaluationCase[]> {
  return getApi<EvaluationCase[]>('/api/evaluation/cases');
}

export async function getEvaluationSummary(): Promise<EvaluationCaseSummary> {
  return getApi<EvaluationCaseSummary>('/api/evaluation/summary');
}

export async function getEvaluationCaseReadiness(): Promise<EvaluationCaseFixtureReadinessSummary> {
  return getApi<EvaluationCaseFixtureReadinessSummary>('/api/evaluation/case-readiness');
}

export async function runEvaluationFixtureBaseline(): Promise<EvaluationFixtureBaselineSummary> {
  return postApi<EvaluationFixtureBaselineSummary>('/api/evaluation/fixture-baseline');
}

export async function runAndArchiveEvaluationFixtureBaseline(): Promise<EvaluationFixtureBaselineRunArchive> {
  return postApi<EvaluationFixtureBaselineRunArchive>('/api/evaluation/fixture-baseline-runs');
}

export async function listEvaluationFixtureBaselineRuns(): Promise<EvaluationFixtureBaselineRunArchive[]> {
  return getApi<EvaluationFixtureBaselineRunArchive[]>('/api/evaluation/fixture-baseline-runs');
}

export async function getEvaluationFixtureBaselineRunRegressionSummary(): Promise<EvaluationFixtureBaselineRunRegressionSummary> {
  return getApi<EvaluationFixtureBaselineRunRegressionSummary>('/api/evaluation/fixture-baseline-runs/summary');
}

export async function downloadEvaluationFixtureBaselineRunReport(runId: string): Promise<Blob> {
  return getBlobApi(`/api/evaluation/fixture-baseline-runs/${encodeURIComponent(runId)}/report/download`);
}

export async function getEvaluationRunPreview(): Promise<EvaluationRunPreview> {
  return getApi<EvaluationRunPreview>('/api/evaluation/run-preview');
}

export async function archiveEvaluationRunSnapshot(): Promise<EvaluationRunSnapshotArchive> {
  return postApi<EvaluationRunSnapshotArchive>('/api/evaluation/run-snapshots');
}

export async function listEvaluationRunSnapshots(): Promise<EvaluationRunSnapshotArchive[]> {
  return getApi<EvaluationRunSnapshotArchive[]>('/api/evaluation/run-snapshots');
}

export async function downloadEvaluationRunSnapshotReport(snapshotId: string): Promise<Blob> {
  return getBlobApi(`/api/evaluation/run-snapshots/${encodeURIComponent(snapshotId)}/report/download`);
}

export async function runAndArchiveEvaluation(): Promise<EvaluationRunArchive> {
  return postApi<EvaluationRunArchive>('/api/evaluation/runs');
}

export async function listEvaluationRuns(): Promise<EvaluationRunArchive[]> {
  return getApi<EvaluationRunArchive[]>('/api/evaluation/runs');
}

export async function getEvaluationRunArchiveReadinessSummary(): Promise<EvaluationRunArchiveReadinessSummary> {
  return getApi<EvaluationRunArchiveReadinessSummary>('/api/evaluation/runs/summary');
}

export async function downloadEvaluationRunReport(runId: string): Promise<Blob> {
  return getBlobApi(`/api/evaluation/runs/${encodeURIComponent(runId)}/report/download`);
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

export async function getWorkerHealth(): Promise<FixTaskWorkerHealth> {
  return getApi<FixTaskWorkerHealth>('/api/task-queue/worker-health');
}

export async function listWebhookDeliveries(limit = 10): Promise<WebhookDeliveryDiagnostic[]> {
  return getApi<WebhookDeliveryDiagnostic[]>(`/api/github/webhook-deliveries?limit=${limit}`);
}

export async function evaluateWebhookPayloadDiagnostic(
  input: WebhookPayloadDiagnosticInput
): Promise<WebhookPayloadDiagnosticResult> {
  return postApi<WebhookPayloadDiagnosticResult>('/api/github/webhook-diagnostics/evaluate-payload', input);
}

export async function listRejectedTriggers(options: number | ListRejectedTriggersOptions = 20): Promise<RejectedTriggerAudit[]> {
  const normalizedOptions = typeof options === 'number' ? { limit: options } : options;
  const searchParams = new URLSearchParams({ limit: String(normalizedOptions.limit ?? 20) });
  if (normalizedOptions.category && normalizedOptions.category !== 'ALL') {
    searchParams.set('category', normalizedOptions.category);
  }
  return getApi<RejectedTriggerAudit[]>(`/api/rejected-triggers?${searchParams.toString()}`);
}

export async function listAcceptedTriggerDecisions(limit = 20): Promise<AcceptedTriggerDecision[]> {
  return getApi<AcceptedTriggerDecision[]>(`/api/tasks/pre-execution-decisions?limit=${limit}`);
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

export async function listAdminAuditEvents(options: number | AdminAuditFilterOptions = 20): Promise<OperatorSafetyAudit[]> {
  const normalizedOptions = typeof options === 'number' ? { limit: options } : options;
  const searchParams = new URLSearchParams({
    limit: String(normalizedOptions.limit ?? 20)
  });
  appendSearchParam(searchParams, 'action', normalizedOptions.action);
  appendSearchParam(searchParams, 'resourceType', normalizedOptions.resourceType);
  appendSearchParam(searchParams, 'resourceId', normalizedOptions.resourceId);
  appendSearchParam(searchParams, 'scope', normalizedOptions.scope);
  appendSearchParam(searchParams, 'scopeKey', normalizedOptions.scopeKey);
  appendSearchParam(searchParams, 'operator', normalizedOptions.operator);
  return getApi<OperatorSafetyAudit[]>(`/api/admin-audit-events?${searchParams.toString()}`);
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

export async function getTaskRetryPreflight(taskId: string): Promise<FixTaskRetryPreflight> {
  return getApi<FixTaskRetryPreflight>(`/api/tasks/${taskId}/retry-preflight`);
}

export async function getTaskReport(taskId: string): Promise<string> {
  return getApi<string>(`/api/tasks/${taskId}/report`);
}

export async function downloadTaskReport(taskId: string): Promise<Blob> {
  return getBlobApi(`/api/tasks/${encodeURIComponent(taskId)}/report/download`);
}

export async function archiveTaskEvidencePackage(taskId: string): Promise<FixTaskEvidencePackageArchive> {
  return postApi<FixTaskEvidencePackageArchive>(`/api/tasks/${encodeURIComponent(taskId)}/evidence-packages`);
}

export async function listTaskEvidencePackageArchives(taskId: string): Promise<FixTaskEvidencePackageArchive[]> {
  return getApi<FixTaskEvidencePackageArchive[]>(`/api/tasks/${encodeURIComponent(taskId)}/evidence-packages`);
}

export async function listRecentTaskEvidencePackageArchives(limit = 20): Promise<FixTaskEvidencePackageArchive[]> {
  return getApi<FixTaskEvidencePackageArchive[]>(`/api/tasks/evidence-packages?limit=${encodeURIComponent(limit)}`);
}

export async function getTaskEvidencePackageArchiveSummary(limit = 50): Promise<FixTaskEvidencePackageArchiveSummary> {
  return getApi<FixTaskEvidencePackageArchiveSummary>(
    `/api/tasks/evidence-packages/summary?limit=${encodeURIComponent(limit)}`
  );
}

export async function getTaskEvidencePackageShareCenter(limit = 20): Promise<FixTaskEvidencePackageArchiveShareCenter> {
  return getApi<FixTaskEvidencePackageArchiveShareCenter>(
    `/api/tasks/evidence-packages/share-center?limit=${encodeURIComponent(limit)}`
  );
}

export async function downloadTaskEvidencePackageReport(archiveId: string): Promise<Blob> {
  return getBlobApi(`/api/tasks/evidence-packages/${encodeURIComponent(archiveId)}/report/download`);
}

export async function downloadTaskEvidencePackageShareCenterReport(): Promise<Blob> {
  return getBlobApi('/api/tasks/evidence-packages/share-center/report/download');
}

export async function createTaskEvidencePackageShareDeliveryReceipt(
  input: FixTaskEvidencePackageShareDeliveryReceiptInput
): Promise<FixTaskEvidencePackageShareDeliveryReceipt> {
  return postApi<FixTaskEvidencePackageShareDeliveryReceipt>(
    '/api/tasks/evidence-packages/share-delivery-receipts',
    input
  );
}

export async function listTaskEvidencePackageShareDeliveryReceipts(): Promise<FixTaskEvidencePackageShareDeliveryReceipt[]> {
  return getApi<FixTaskEvidencePackageShareDeliveryReceipt[]>(
    '/api/tasks/evidence-packages/share-delivery-receipts'
  );
}

export async function downloadTaskEvidencePackageShareDeliveryReceiptReport(receiptId: string): Promise<Blob> {
  return getBlobApi(
    `/api/tasks/evidence-packages/share-delivery-receipts/${encodeURIComponent(receiptId)}/report/download`
  );
}

export async function getTaskEvidencePackageFinalization(): Promise<FixTaskEvidencePackageFinalization> {
  return getApi<FixTaskEvidencePackageFinalization>('/api/tasks/evidence-packages/finalization');
}

export async function downloadTaskEvidencePackageFinalizationReport(): Promise<Blob> {
  return getBlobApi('/api/tasks/evidence-packages/finalization/report/download');
}

export async function archiveTaskEvidencePackageAcceptanceCloseout(): Promise<FixTaskEvidencePackageAcceptanceCloseoutArchive> {
  return postApi<FixTaskEvidencePackageAcceptanceCloseoutArchive>(
    '/api/tasks/evidence-packages/acceptance-closeout/archives'
  );
}

export async function listTaskEvidencePackageAcceptanceCloseoutArchives(): Promise<FixTaskEvidencePackageAcceptanceCloseoutArchive[]> {
  return getApi<FixTaskEvidencePackageAcceptanceCloseoutArchive[]>(
    '/api/tasks/evidence-packages/acceptance-closeout/archives'
  );
}

export async function downloadTaskEvidencePackageAcceptanceCloseoutArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(
    `/api/tasks/evidence-packages/acceptance-closeout/archives/${encodeURIComponent(archiveId)}/report/download`
  );
}

export async function getTaskEvidencePackageAcceptanceCertificate(): Promise<FixTaskEvidencePackageAcceptanceCertificate> {
  return getApi<FixTaskEvidencePackageAcceptanceCertificate>('/api/tasks/evidence-packages/acceptance-certificate');
}

export async function downloadTaskEvidencePackageAcceptanceCertificateReport(): Promise<Blob> {
  return getBlobApi('/api/tasks/evidence-packages/acceptance-certificate/report/download');
}

export async function archiveTaskEvidencePackageAcceptanceCertificate(): Promise<FixTaskEvidencePackageAcceptanceCertificateArchive> {
  return postApi<FixTaskEvidencePackageAcceptanceCertificateArchive>(
    '/api/tasks/evidence-packages/acceptance-certificate/archives'
  );
}

export async function listTaskEvidencePackageAcceptanceCertificateArchives(): Promise<FixTaskEvidencePackageAcceptanceCertificateArchive[]> {
  return getApi<FixTaskEvidencePackageAcceptanceCertificateArchive[]>(
    '/api/tasks/evidence-packages/acceptance-certificate/archives'
  );
}

export async function downloadTaskEvidencePackageAcceptanceCertificateArchiveReport(archiveId: string): Promise<Blob> {
  return getBlobApi(
    `/api/tasks/evidence-packages/acceptance-certificate/archives/${encodeURIComponent(archiveId)}/report/download`
  );
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

export async function retryTask(taskId: string, input: RetryTaskInput): Promise<FixTask> {
  return postApi<FixTask>(`/api/tasks/${taskId}/retry`, input);
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

async function getBlobApi(path: string): Promise<Blob> {
  let response: Response;
  const securedInit = withAdminToken();
  try {
    response = securedInit ? await fetch(path, securedInit) : await fetch(path);
  } catch {
    throw new Error(backendConnectionError);
  }
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }
  return response.blob();
}

async function postBlobApi(path: string, body: unknown): Promise<Blob> {
  let response: Response;
  const securedInit = withAdminToken(postRequest(body));
  try {
    response = await fetch(path, securedInit);
  } catch {
    throw new Error(backendConnectionError);
  }
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }
  return response.blob();
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
