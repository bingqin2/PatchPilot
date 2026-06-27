import { AlertCircle, RefreshCw } from 'lucide-react';
import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react';
import {
  ADMIN_TOKEN_STORAGE_KEY,
  approveTaskReview,
  archiveDemoHandoffPackage,
  archiveDemoReadinessSnapshot,
  archiveDemoSelfHostedLaunchReadiness,
  archiveDemoSession,
  archiveEvaluationRunSnapshot,
  cancelTask,
  composeDemoLaunchCommand,
  createDemoHandoffShareDeliveryReceipt,
  createTask,
  createTriggerQuarantine,
  downloadDemoSessionArchiveReport,
  downloadDemoHandoffPackageArchiveReport,
  downloadDemoHandoffPackageArchiveSummaryReport,
  downloadDemoReadinessSnapshotReport,
  downloadEvaluationFixtureBaselineRunReport,
  downloadEvaluationRunReport,
  downloadEvaluationRunSnapshotReport,
  downloadDemoHandoffPackage,
  downloadDemoSessionReport,
  downloadDemoHandoffShareCenterReport,
  downloadDemoHandoffFinalizationReport,
  downloadDemoSelfHostedLaunchReadinessArchiveReport,
  downloadDemoSelfHostedLaunchReadinessReport,
  downloadDemoHandoffShareDeliveryReceiptReport,
  downloadDemoHandoffShareInstructionsReport,
  downloadDemoHandoffShareChecklistReport,
  evaluateTrigger,
  evaluateWebhookPayloadDiagnostic,
  getBackendHealth,
  getConfigurationSummary,
  getDashboardBootstrap,
  getDemoEvidenceBundle,
  getDemoHandoffPackage,
  getDemoHandoffReadiness,
  getDemoHandoffPackageArchiveSummary,
  getDemoHandoffShareCenter,
  getDemoHandoffFinalization,
  getDemoSelfHostedLaunchReadiness,
  getDemoHandoffShareInstructions,
  getDemoHandoffShareChecklist,
  getDemoSessionSnapshot,
  getDemoSessionReport,
  getDemoScript,
  getDemoRunbook,
  getDemoReadiness,
  getDemoReadinessSnapshotTrend,
  getDemoSmokeChecklist,
  getEvaluationCaseReadiness,
  getEvaluationFixtureBaselineRunRegressionSummary,
  getEvaluationRunArchiveReadinessSummary,
  getEvaluationSummary,
  getEvaluationRunPreview,
  runAndArchiveEvaluation,
  runAndArchiveEvaluationFixtureBaseline,
  runEvaluationFixtureBaseline,
  preflightDemoLaunch,
  getGitHubCredentialReadiness,
  getGitHubRepositoryAccessReadiness,
  getGitHubWebhookSetupReadiness,
  getGitHubWebhookUrlReadiness,
  getRejectedTriggerSummary,
  getTriggerQuarantineEvidence,
  getFailureCauseSummary,
  getLatencySummary,
  getMetricsSummary,
  getModelProviderHealth,
  getModelUsageSummary,
  getQueueSummary,
  getTaskDetail,
  getTaskRetryPreflight,
  getTaskReport,
  getTaskStatusCounts,
  getWorkerHealth,
  listAcceptedTriggerDecisions,
  listAdminAuditEvents,
  listEvaluationCases,
  listEvaluationFixtureBaselineRuns,
  listEvaluationRuns,
  listEvaluationRunSnapshots,
  listLanguageAdapterFixtures,
  listLanguageAdapterRuntimeReadiness,
  listLanguageAdapters,
  listDemoHandoffPackageArchives,
  listDemoHandoffShareDeliveryReceipts,
  listDemoSelfHostedLaunchReadinessArchives,
  listDemoSessionArchives,
  listDemoReadinessSnapshots,
  listQueueItems,
  listRejectedTriggers,
  listTriggerQuarantines,
  listWebhookDeliveries,
  listTasks,
  preflightRepository,
  retryRejectedTrigger,
  releaseTriggerQuarantine,
  retryTask
} from './api';
import { AdapterFixtureVerificationPanel } from './dashboard/components/AdapterFixtureVerificationPanel';
import { ConfigurationPanel } from './dashboard/components/ConfigurationPanel';
import { ConnectivityPanel } from './dashboard/components/ConnectivityPanel';
import { DemoReadinessPanel } from './dashboard/components/DemoReadinessPanel';
import { DemoEvidenceBundlePanel } from './dashboard/components/DemoEvidenceBundlePanel';
import { DemoLaunchCommandPanel } from './dashboard/components/DemoLaunchCommandPanel';
import { DemoLaunchPreflightPanel } from './dashboard/components/DemoLaunchPreflightPanel';
import { DemoLaunchTrackerPanel } from './dashboard/components/DemoLaunchTrackerPanel';
import { DemoSessionSnapshotPanel } from './dashboard/components/DemoSessionSnapshotPanel';
import { DemoScriptPanel } from './dashboard/components/DemoScriptPanel';
import { DemoSmokeChecklistPanel } from './dashboard/components/DemoSmokeChecklistPanel';
import { SelfHostedLaunchReadinessPanel } from './dashboard/components/SelfHostedLaunchReadinessPanel';
import { EvaluationCaseCatalogPanel } from './dashboard/components/EvaluationCaseCatalogPanel';
import { FailureCausePanel } from './dashboard/components/FailureCausePanel';
import { LatencyPanel } from './dashboard/components/LatencyPanel';
import { MetricCard } from './dashboard/components/MetricCard';
import { ModelUsagePanel } from './dashboard/components/ModelUsagePanel';
import { ManualTaskForm } from './dashboard/components/ManualTaskForm';
import { OperatorSetupChecklistPanel } from './dashboard/components/OperatorSetupChecklistPanel';
import { QueuePanel } from './dashboard/components/QueuePanel';
import { AcceptedTriggerDecisionPanel } from './dashboard/components/AcceptedTriggerDecisionPanel';
import { RejectedTriggerPanel } from './dashboard/components/RejectedTriggerPanel';
import { RepositoryPreflightPanel } from './dashboard/components/RepositoryPreflightPanel';
import { SupportedAdaptersPanel } from './dashboard/components/SupportedAdaptersPanel';
import { TaskDetailPanel } from './dashboard/components/TaskDetailPanel';
import { TaskListPanel } from './dashboard/components/TaskListPanel';
import { TriggerDecisionPanel } from './dashboard/components/TriggerDecisionPanel';
import { WebhookDeliveryPanel } from './dashboard/components/WebhookDeliveryPanel';
import { compactDateTime, duration, percent } from './dashboard/format';
import { loadDemoLaunchCommandHistory, toPreparedLaunchCommands } from './dashboard/demoLaunchCommandHistory';
import { loadDemoLaunchOutcomeArchive, toArchivedLaunchOutcomes } from './dashboard/demoLaunchOutcomeArchive';
import { emptyDetail } from './dashboard/types';
import type { TaskDetailState } from './dashboard/types';
import { AdminAuditPanel } from './dashboard/components/AdminAuditPanel';
import { AdapterReadinessReportPanel } from './dashboard/components/AdapterReadinessReportPanel';
import type {
  ApproveReviewInput,
  ConfigurationSummary,
  BackendHealth,
  CreateTaskInput,
  CreateTriggerQuarantineInput,
  DemoReadiness,
  DemoReadinessSnapshotArchive,
  DemoReadinessSnapshotTrend,
  DemoEvidenceBundle,
  DemoHandoffFinalization,
  DemoHandoffReadiness,
  DemoHandoffPackageArchive,
  DemoHandoffPackageArchiveSummary,
  DemoHandoffShareCenter,
  DemoHandoffShareDeliveryReceipt,
  DemoHandoffShareDeliveryReceiptInput,
  DemoHandoffShareInstructions,
  DemoHandoffShareChecklist,
  DemoLaunchCommand,
  DemoLaunchCommandInput,
  DemoLaunchPreflight,
  DemoLaunchPreflightInput,
  DemoScript,
  DemoSelfHostedLaunchReadinessArchive,
  DemoSelfHostedLaunchReadiness,
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
  FixTask,
  FixTaskFailureCauseSummary,
  FixTaskLatencySummary,
  FixTaskMetricsSummary,
  FixTaskStatusCounts,
  FixTaskModelUsageSummary,
  FixTaskQueueItem,
  FixTaskQueueSummary,
  FixTaskWorkerHealth,
  GitHubCredentialReadiness,
  GitHubRepositoryAccessReadiness,
  GitHubWebhookSetupReadiness,
  GitHubWebhookUrlReadiness,
  ModelProviderHealth,
  AdminAuditFilterOptions,
  AcceptedTriggerDecision,
  OperatorSafetyAudit,
  RejectedTriggerCategoryFilter,
  RejectedTriggerAudit,
  RejectedTriggerAuditSummary,
  ReleaseTriggerQuarantineInput,
  TriggerQuarantine,
  TriggerQuarantineEvidence,
  WebhookDeliveryDiagnostic,
  WebhookPayloadDiagnosticInput,
  WebhookPayloadDiagnosticResult,
  LanguageAdapterFixtureVerification,
  LanguageAdapterRuntimeReadiness,
  RepositoryPreflightInput,
  RepositoryPreflightResult,
  RetryTaskInput,
  SupportedLanguageAdapter,
  TaskSort,
  TaskStatusFilter,
  TriggerEvaluationResult
} from './types';

const TASK_PAGE_SIZE = 50;
const TASK_STATUS_FILTERS: TaskStatusFilter[] = [
  'ALL',
  'PENDING',
  'RUNNING',
  'RUNNING_TESTS',
  'PENDING_REVIEW',
  'COMPLETED',
  'FAILED',
  'CANCELLED'
];
const TASK_SORTS: TaskSort[] = ['createdAtDesc', 'createdAtAsc'];
const REJECTED_TRIGGER_CATEGORY_FILTERS: RejectedTriggerCategoryFilter[] = [
  'ALL',
  'UNKNOWN',
  'EMPTY_COMMAND',
  'UNSUPPORTED_COMMAND',
  'NOT_ACTIONABLE',
  'DANGEROUS_INSTRUCTION',
  'TRIGGER_USER_NOT_ALLOWED',
  'REPOSITORY_NOT_ALLOWED',
  'RATE_LIMITED',
  'ABUSE_QUARANTINED',
  'MODEL_REJECTED',
  'MODEL_NEEDS_CLARIFICATION',
  'MODEL_CLASSIFICATION_FAILED'
];
const ADMIN_TOKEN_REQUIRED_MESSAGE = 'Admin token is required';

export default function App() {
  const initialFilters = useMemo(() => filtersFromUrl(), []);
  const initialStoredAdminToken = useMemo(() => storedAdminToken(), []);
  const [tasks, setTasks] = useState<FixTask[]>([]);
  const [metrics, setMetrics] = useState<FixTaskMetricsSummary | null>(null);
  const [statusCounts, setStatusCounts] = useState<FixTaskStatusCounts | null>(null);
  const [failureCauses, setFailureCauses] = useState<FixTaskFailureCauseSummary[]>([]);
  const [modelUsage, setModelUsage] = useState<FixTaskModelUsageSummary | null>(null);
  const [latency, setLatency] = useState<FixTaskLatencySummary | null>(null);
  const [configuration, setConfiguration] = useState<ConfigurationSummary | null>(null);
  const [githubCredentialReadiness, setGitHubCredentialReadiness] = useState<GitHubCredentialReadiness | null>(null);
  const [githubRepositoryAccessReadiness, setGitHubRepositoryAccessReadiness] = useState<GitHubRepositoryAccessReadiness | null>(null);
  const [githubWebhookUrlReadiness, setGitHubWebhookUrlReadiness] = useState<GitHubWebhookUrlReadiness | null>(null);
  const [githubWebhookSetupReadiness, setGitHubWebhookSetupReadiness] = useState<GitHubWebhookSetupReadiness | null>(null);
  const [modelProviderHealth, setModelProviderHealth] = useState<ModelProviderHealth | null>(null);
  const [backendHealth, setBackendHealth] = useState<BackendHealth | null>(null);
  const [demoReadiness, setDemoReadiness] = useState<DemoReadiness | null>(null);
  const [demoReadinessError, setDemoReadinessError] = useState<string | null>(null);
  const [demoReadinessSnapshots, setDemoReadinessSnapshots] = useState<DemoReadinessSnapshotArchive[]>([]);
  const [demoReadinessSnapshotError, setDemoReadinessSnapshotError] = useState<string | null>(null);
  const [demoReadinessSnapshotTrend, setDemoReadinessSnapshotTrend] = useState<DemoReadinessSnapshotTrend | null>(null);
  const [demoReadinessSnapshotTrendError, setDemoReadinessSnapshotTrendError] = useState<string | null>(null);
  const [demoEvidenceBundle, setDemoEvidenceBundle] = useState<DemoEvidenceBundle | null>(null);
  const [demoEvidenceBundleError, setDemoEvidenceBundleError] = useState<string | null>(null);
  const [demoSessionSnapshot, setDemoSessionSnapshot] = useState<DemoSessionSnapshot | null>(null);
  const [demoSessionSnapshotError, setDemoSessionSnapshotError] = useState<string | null>(null);
  const [demoHandoffReadiness, setDemoHandoffReadiness] = useState<DemoHandoffReadiness | null>(null);
  const [demoHandoffReadinessError, setDemoHandoffReadinessError] = useState<string | null>(null);
  const [demoSessionArchives, setDemoSessionArchives] = useState<DemoSessionArchive[]>([]);
  const [demoSessionArchiveError, setDemoSessionArchiveError] = useState<string | null>(null);
  const [demoHandoffPackageArchives, setDemoHandoffPackageArchives] = useState<DemoHandoffPackageArchive[]>([]);
  const [demoHandoffPackageArchiveError, setDemoHandoffPackageArchiveError] = useState<string | null>(null);
  const [demoHandoffPackageArchiveSummary, setDemoHandoffPackageArchiveSummary] =
    useState<DemoHandoffPackageArchiveSummary | null>(null);
  const [demoHandoffPackageArchiveSummaryError, setDemoHandoffPackageArchiveSummaryError] =
    useState<string | null>(null);
  const [demoHandoffShareChecklist, setDemoHandoffShareChecklist] = useState<DemoHandoffShareChecklist | null>(null);
  const [demoHandoffShareChecklistError, setDemoHandoffShareChecklistError] = useState<string | null>(null);
  const [demoHandoffShareCenter, setDemoHandoffShareCenter] = useState<DemoHandoffShareCenter | null>(null);
  const [demoHandoffShareCenterError, setDemoHandoffShareCenterError] = useState<string | null>(null);
  const [demoHandoffFinalization, setDemoHandoffFinalization] = useState<DemoHandoffFinalization | null>(null);
  const [demoHandoffFinalizationError, setDemoHandoffFinalizationError] = useState<string | null>(null);
  const [demoSelfHostedLaunchReadiness, setDemoSelfHostedLaunchReadiness] =
    useState<DemoSelfHostedLaunchReadiness | null>(null);
  const [demoSelfHostedLaunchReadinessError, setDemoSelfHostedLaunchReadinessError] = useState<string | null>(null);
  const [demoSelfHostedLaunchReadinessArchives, setDemoSelfHostedLaunchReadinessArchives] =
    useState<DemoSelfHostedLaunchReadinessArchive[]>([]);
  const [demoSelfHostedLaunchReadinessArchiveError, setDemoSelfHostedLaunchReadinessArchiveError] =
    useState<string | null>(null);
  const [demoHandoffShareInstructions, setDemoHandoffShareInstructions] =
    useState<DemoHandoffShareInstructions | null>(null);
  const [demoHandoffShareInstructionsError, setDemoHandoffShareInstructionsError] = useState<string | null>(null);
  const [demoHandoffShareDeliveryReceipts, setDemoHandoffShareDeliveryReceipts] =
    useState<DemoHandoffShareDeliveryReceipt[]>([]);
  const [demoHandoffShareDeliveryReceiptError, setDemoHandoffShareDeliveryReceiptError] =
    useState<string | null>(null);
  const [demoScript, setDemoScript] = useState<DemoScript | null>(null);
  const [demoScriptError, setDemoScriptError] = useState<string | null>(null);
  const [demoSmokeChecklist, setDemoSmokeChecklist] = useState<DemoSmokeChecklist | null>(null);
  const [demoSmokeChecklistError, setDemoSmokeChecklistError] = useState<string | null>(null);
  const [demoLaunchCommand, setDemoLaunchCommand] = useState<DemoLaunchCommand | null>(null);
  const [demoLaunchCommandError, setDemoLaunchCommandError] = useState<string | null>(null);
  const [demoLaunchCommandPending, setDemoLaunchCommandPending] = useState(false);
  const [demoLaunchCommandHistoryRevision, setDemoLaunchCommandHistoryRevision] = useState(0);
  const [demoLaunchOutcomeArchiveRevision, setDemoLaunchOutcomeArchiveRevision] = useState(0);
  const [composedPreflightInput, setComposedPreflightInput] = useState<DemoLaunchPreflightInput | null>(null);
  const [demoLaunchPreflight, setDemoLaunchPreflight] = useState<DemoLaunchPreflight | null>(null);
  const [demoLaunchPreflightError, setDemoLaunchPreflightError] = useState<string | null>(null);
  const [demoLaunchPreflightPending, setDemoLaunchPreflightPending] = useState(false);
  const [supportedAdapters, setSupportedAdapters] = useState<SupportedLanguageAdapter[]>([]);
  const [adapterError, setAdapterError] = useState<string | null>(null);
  const [adapterFixtureVerifications, setAdapterFixtureVerifications] = useState<LanguageAdapterFixtureVerification[]>([]);
  const [adapterFixtureError, setAdapterFixtureError] = useState<string | null>(null);
  const [adapterRuntimeReadiness, setAdapterRuntimeReadiness] = useState<LanguageAdapterRuntimeReadiness[]>([]);
  const [adapterRuntimeReadinessError, setAdapterRuntimeReadinessError] = useState<string | null>(null);
  const [evaluationCases, setEvaluationCases] = useState<EvaluationCase[]>([]);
  const [evaluationSummary, setEvaluationSummary] = useState<EvaluationCaseSummary | null>(null);
  const [evaluationCaseReadiness, setEvaluationCaseReadiness] = useState<EvaluationCaseFixtureReadinessSummary | null>(null);
  const [evaluationFixtureBaseline, setEvaluationFixtureBaseline] = useState<EvaluationFixtureBaselineSummary | null>(null);
  const [evaluationFixtureBaselineRuns, setEvaluationFixtureBaselineRuns] = useState<EvaluationFixtureBaselineRunArchive[]>([]);
  const [evaluationFixtureBaselineRegressionSummary, setEvaluationFixtureBaselineRegressionSummary] = useState<EvaluationFixtureBaselineRunRegressionSummary | null>(null);
  const [evaluationRunPreview, setEvaluationRunPreview] = useState<EvaluationRunPreview | null>(null);
  const [evaluationRuns, setEvaluationRuns] = useState<EvaluationRunArchive[]>([]);
  const [evaluationRunSummary, setEvaluationRunSummary] = useState<EvaluationRunArchiveReadinessSummary | null>(null);
  const [evaluationRunSnapshots, setEvaluationRunSnapshots] = useState<EvaluationRunSnapshotArchive[]>([]);
  const [evaluationCaseError, setEvaluationCaseError] = useState<string | null>(null);
  const [evaluationSummaryError, setEvaluationSummaryError] = useState<string | null>(null);
  const [evaluationCaseReadinessError, setEvaluationCaseReadinessError] = useState<string | null>(null);
  const [evaluationFixtureBaselineError, setEvaluationFixtureBaselineError] = useState<string | null>(null);
  const [evaluationFixtureBaselineRegressionError, setEvaluationFixtureBaselineRegressionError] = useState<string | null>(null);
  const [evaluationFixtureBaselineLoading, setEvaluationFixtureBaselineLoading] = useState(false);
  const [evaluationRunPreviewError, setEvaluationRunPreviewError] = useState<string | null>(null);
  const [evaluationRunError, setEvaluationRunError] = useState<string | null>(null);
  const [evaluationRunSummaryError, setEvaluationRunSummaryError] = useState<string | null>(null);
  const [evaluationRunLoading, setEvaluationRunLoading] = useState(false);
  const [evaluationRunSnapshotError, setEvaluationRunSnapshotError] = useState<string | null>(null);
  const [repositoryPreflightResult, setRepositoryPreflightResult] = useState<RepositoryPreflightResult | null>(null);
  const [repositoryPreflightError, setRepositoryPreflightError] = useState<string | null>(null);
  const [repositoryPreflightLoading, setRepositoryPreflightLoading] = useState(false);
  const [queueSummary, setQueueSummary] = useState<FixTaskQueueSummary | null>(null);
  const [workerHealth, setWorkerHealth] = useState<FixTaskWorkerHealth | null>(null);
  const [queueItems, setQueueItems] = useState<FixTaskQueueItem[]>([]);
  const [webhookDeliveries, setWebhookDeliveries] = useState<WebhookDeliveryDiagnostic[]>([]);
  const [webhookDeliveryError, setWebhookDeliveryError] = useState<string | null>(null);
  const [webhookPayloadDiagnostic, setWebhookPayloadDiagnostic] = useState<WebhookPayloadDiagnosticResult | null>(null);
  const [webhookPayloadDiagnosticError, setWebhookPayloadDiagnosticError] = useState<string | null>(null);
  const [evaluatingWebhookPayload, setEvaluatingWebhookPayload] = useState(false);
  const [rejectedTriggers, setRejectedTriggers] = useState<RejectedTriggerAudit[]>([]);
  const [acceptedTriggerDecisions, setAcceptedTriggerDecisions] = useState<AcceptedTriggerDecision[]>([]);
  const [rejectedTriggerSummary, setRejectedTriggerSummary] = useState<RejectedTriggerAuditSummary | null>(null);
  const [triggerQuarantines, setTriggerQuarantines] = useState<TriggerQuarantine[]>([]);
  const [adminAuditEvents, setAdminAuditEvents] = useState<OperatorSafetyAudit[]>([]);
  const [adminAuditFilters, setAdminAuditFilters] = useState<AdminAuditFilterOptions>({ limit: 20 });
  const [operatorSafetyAudits, setOperatorSafetyAudits] = useState<OperatorSafetyAudit[]>([]);
  const [triggerQuarantineEvidence, setTriggerQuarantineEvidence] = useState<TriggerQuarantineEvidence | null>(null);
  const [acceptedTriggerDecisionError, setAcceptedTriggerDecisionError] = useState<string | null>(null);
  const [rejectedTriggerError, setRejectedTriggerError] = useState<string | null>(null);
  const [rejectedTriggerCategoryFilter, setRejectedTriggerCategoryFilter] = useState<RejectedTriggerCategoryFilter>(
    initialFilters.rejectedCategory
  );
  const [statusFilter, setStatusFilter] = useState<TaskStatusFilter>(initialFilters.status);
  const [searchQuery, setSearchQuery] = useState(initialFilters.query);
  const [repositoryOwnerFilter, setRepositoryOwnerFilter] = useState(initialFilters.repositoryOwner);
  const [repositoryNameFilter, setRepositoryNameFilter] = useState(initialFilters.repositoryName);
  const [languageFilter, setLanguageFilter] = useState(initialFilters.language);
  const [buildSystemFilter, setBuildSystemFilter] = useState(initialFilters.buildSystem);
  const [createdAfterFilter, setCreatedAfterFilter] = useState(initialFilters.createdAfter);
  const [createdBeforeFilter, setCreatedBeforeFilter] = useState(initialFilters.createdBefore);
  const [taskSort, setTaskSort] = useState<TaskSort>(initialFilters.sort);
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(() => taskIdFromUrl());
  const [detail, setDetail] = useState<TaskDetailState>(emptyDetail);
  const [loading, setLoading] = useState(true);
  const [detailLoading, setDetailLoading] = useState(false);
  const [actionTaskId, setActionTaskId] = useState<string | null>(null);
  const [retryingRejectedTriggerId, setRetryingRejectedTriggerId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [adminTokenInput, setAdminTokenInput] = useState('');
  const [dashboardAdminTokenInput, setDashboardAdminTokenInput] = useState(initialStoredAdminToken);
  const [hasStoredAdminToken, setHasStoredAdminToken] = useState(initialStoredAdminToken.length > 0);
  const [canLoadMoreTasks, setCanLoadMoreTasks] = useState(false);
  const [loadingMoreTasks, setLoadingMoreTasks] = useState(false);
  const [taskTotal, setTaskTotal] = useState(0);
  const [lastRefreshedAt, setLastRefreshedAt] = useState<string | null>(null);
  const [creatingTask, setCreatingTask] = useState(false);
  const [evaluatingTrigger, setEvaluatingTrigger] = useState(false);
  const [triggerEvaluation, setTriggerEvaluation] = useState<TriggerEvaluationResult | null>(null);
  const [createTaskStatus, setCreateTaskStatus] = useState<string | null>(null);
  const [creatingTriggerQuarantine, setCreatingTriggerQuarantine] = useState(false);
  const [releasingTriggerQuarantineId, setReleasingTriggerQuarantineId] = useState<string | null>(null);
  const [inspectingTriggerQuarantineId, setInspectingTriggerQuarantineId] = useState<string | null>(null);

  const selectedTask = useMemo(
    () => tasks.find((task) => task.id === selectedTaskId) ?? tasks[0] ?? null,
    [selectedTaskId, tasks]
  );
  const preparedDemoLaunchCommands = useMemo(
    () => toPreparedLaunchCommands(loadDemoLaunchCommandHistory()),
    [demoLaunchCommandHistoryRevision]
  );
  const archivedDemoLaunchOutcomes = useMemo(
    () => toArchivedLaunchOutcomes(loadDemoLaunchOutcomeArchive()),
    [demoLaunchOutcomeArchiveRevision]
  );

  const selectTask = useCallback((taskId: string) => {
    setSelectedTaskId(taskId);
    writeTaskIdToUrl(taskId);
  }, []);

  const handleStatusFilterChange = useCallback((status: TaskStatusFilter) => {
    setStatusFilter(status);
    writeTaskListStateToUrl({
      status,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      language: languageFilter,
      buildSystem: buildSystemFilter,
      createdAfter: createdAfterFilter,
      createdBefore: createdBeforeFilter,
      sort: taskSort
    });
  }, [buildSystemFilter, createdAfterFilter, createdBeforeFilter, languageFilter, repositoryNameFilter, repositoryOwnerFilter, searchQuery, taskSort]);

  const handleSearchQueryChange = useCallback((query: string) => {
    setSearchQuery(query);
    writeTaskListStateToUrl({
      status: statusFilter,
      query,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      language: languageFilter,
      buildSystem: buildSystemFilter,
      createdAfter: createdAfterFilter,
      createdBefore: createdBeforeFilter,
      sort: taskSort
    });
  }, [buildSystemFilter, createdAfterFilter, createdBeforeFilter, languageFilter, repositoryNameFilter, repositoryOwnerFilter, statusFilter, taskSort]);

  const handleRepositoryOwnerFilterChange = useCallback((repositoryOwner: string) => {
    setRepositoryOwnerFilter(repositoryOwner);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner,
      repositoryName: repositoryNameFilter,
      language: languageFilter,
      buildSystem: buildSystemFilter,
      createdAfter: createdAfterFilter,
      createdBefore: createdBeforeFilter,
      sort: taskSort
    });
  }, [buildSystemFilter, createdAfterFilter, createdBeforeFilter, languageFilter, repositoryNameFilter, searchQuery, statusFilter, taskSort]);

  const handleRepositoryNameFilterChange = useCallback((repositoryName: string) => {
    setRepositoryNameFilter(repositoryName);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName,
      language: languageFilter,
      buildSystem: buildSystemFilter,
      createdAfter: createdAfterFilter,
      createdBefore: createdBeforeFilter,
      sort: taskSort
    });
  }, [buildSystemFilter, createdAfterFilter, createdBeforeFilter, languageFilter, repositoryOwnerFilter, searchQuery, statusFilter, taskSort]);

  const handleLanguageFilterChange = useCallback((language: string) => {
    setLanguageFilter(language);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      language,
      buildSystem: buildSystemFilter,
      createdAfter: createdAfterFilter,
      createdBefore: createdBeforeFilter,
      sort: taskSort
    });
  }, [buildSystemFilter, createdAfterFilter, createdBeforeFilter, repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter, taskSort]);

  const handleBuildSystemFilterChange = useCallback((buildSystem: string) => {
    setBuildSystemFilter(buildSystem);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      language: languageFilter,
      buildSystem,
      createdAfter: createdAfterFilter,
      createdBefore: createdBeforeFilter,
      sort: taskSort
    });
  }, [createdAfterFilter, createdBeforeFilter, languageFilter, repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter, taskSort]);

  const handleCreatedAfterFilterChange = useCallback((createdAfter: string) => {
    setCreatedAfterFilter(createdAfter);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      language: languageFilter,
      buildSystem: buildSystemFilter,
      createdAfter,
      createdBefore: createdBeforeFilter,
      sort: taskSort
    });
  }, [buildSystemFilter, createdBeforeFilter, languageFilter, repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter, taskSort]);

  const handleCreatedBeforeFilterChange = useCallback((createdBefore: string) => {
    setCreatedBeforeFilter(createdBefore);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      language: languageFilter,
      buildSystem: buildSystemFilter,
      createdAfter: createdAfterFilter,
      createdBefore,
      sort: taskSort
    });
  }, [buildSystemFilter, createdAfterFilter, languageFilter, repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter, taskSort]);

  const handleTaskSortChange = useCallback((sort: TaskSort) => {
    setTaskSort(sort);
    writeTaskListStateToUrl({
      status: statusFilter,
      query: searchQuery,
      repositoryOwner: repositoryOwnerFilter,
      repositoryName: repositoryNameFilter,
      language: languageFilter,
      buildSystem: buildSystemFilter,
      createdAfter: createdAfterFilter,
      createdBefore: createdBeforeFilter,
      sort
    });
  }, [buildSystemFilter, createdAfterFilter, createdBeforeFilter, languageFilter, repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter]);

  const handleClearFilters = useCallback(() => {
    setStatusFilter('ALL');
    setSearchQuery('');
    setRepositoryOwnerFilter('');
    setRepositoryNameFilter('');
    setLanguageFilter('');
    setBuildSystemFilter('');
    setCreatedAfterFilter('');
    setCreatedBeforeFilter('');
    writeTaskListStateToUrl({
      status: 'ALL',
      query: '',
      repositoryOwner: '',
      repositoryName: '',
      language: '',
      buildSystem: '',
      createdAfter: '',
      createdBefore: '',
      sort: taskSort
    });
  }, [taskSort]);

  const handleRejectedTriggerCategoryFilterChange = useCallback((category: RejectedTriggerCategoryFilter) => {
    setRejectedTriggerCategoryFilter(category);
    writeRejectedTriggerStateToUrl(category);
  }, []);

  const handleRepositoryPreflight = useCallback(async (input: RepositoryPreflightInput) => {
    setRepositoryPreflightLoading(true);
    setRepositoryPreflightError(null);
    try {
      const result = await preflightRepository(input);
      setRepositoryPreflightResult(result);
    } catch (caught) {
      setRepositoryPreflightError(errorMessage(caught));
    } finally {
      setRepositoryPreflightLoading(false);
    }
  }, []);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const healthSummary = await getBackendHealth().catch(() => null);
      setBackendHealth(healthSummary);
      const bootstrappedAdminToken = await bootstrapAdminToken();
      if (bootstrappedAdminToken) {
        setDashboardAdminTokenInput(bootstrappedAdminToken);
        setHasStoredAdminToken(true);
      }
      const taskFilters = {
        query: searchQuery,
        repositoryOwner: repositoryOwnerFilter,
        repositoryName: repositoryNameFilter,
        language: languageFilter,
        buildSystem: buildSystemFilter,
        createdAfter: createdAfterFilter,
        createdBefore: createdBeforeFilter
      };
      const demoSessionReportInput = {
        preparedLaunchCommands: preparedDemoLaunchCommands,
        archivedLaunchOutcomes: archivedDemoLaunchOutcomes
      };
      const [
        taskList,
        taskStatusCounts,
        metricsSummary,
        failureCauseSummary,
        modelUsageSummary,
        latencySummary,
        configurationSummary,
        githubCredentialReadinessResult,
        githubWebhookUrlReadinessResult,
        githubWebhookSetupReadinessResult,
        modelProviderHealthResult,
        demoEvidenceBundleResult,
        demoSessionSnapshotResult,
        demoHandoffReadinessResult,
        demoSessionArchiveResult,
        demoHandoffPackageArchiveResult,
        demoHandoffPackageArchiveSummaryResult,
        demoHandoffShareChecklistResult,
        demoHandoffShareCenterResult,
        demoHandoffFinalizationResult,
        demoSelfHostedLaunchReadinessResult,
        demoSelfHostedLaunchReadinessArchiveResult,
        demoHandoffShareInstructionsResult,
        demoHandoffShareDeliveryReceiptResult,
        demoScriptResult,
        demoReadinessResult,
        demoReadinessSnapshotResult,
        demoReadinessSnapshotTrendResult,
        demoSmokeChecklistResult,
        adapterListResult,
        adapterFixtureResult,
        adapterRuntimeReadinessResult,
        evaluationCaseResult,
        evaluationSummaryResult,
        evaluationCaseReadinessResult,
        evaluationFixtureBaselineRunResult,
        evaluationFixtureBaselineRegressionResult,
        evaluationRunPreviewResult,
        evaluationRunResult,
        evaluationRunSummaryResult,
        evaluationRunSnapshotResult,
        queueSummaryData,
        queueItemList,
        workerHealthData,
        webhookDeliveryResult,
        acceptedTriggerDecisionResult,
        rejectedTriggerResult,
        rejectedTriggerSummaryResult,
        triggerQuarantineResult,
        adminAuditResult
      ] = await Promise.all([
        listTasks({
          status: statusFilter,
          ...taskFilters,
          sort: taskSort,
          limit: TASK_PAGE_SIZE
        }),
        getTaskStatusCounts(taskFilters),
        getMetricsSummary(taskFilters),
        getFailureCauseSummary(taskFilters),
        getModelUsageSummary(taskFilters),
        getLatencySummary(taskFilters),
        getConfigurationSummary(),
        getGitHubCredentialReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        getGitHubWebhookUrlReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        getGitHubWebhookSetupReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        getModelProviderHealth().then(
          (health) => ({ health, error: null as string | null }),
          (caught) => ({ health: null, error: errorMessage(caught) })
        ),
        getDemoEvidenceBundle().then(
          (bundle) => ({ bundle, error: null as string | null }),
          (caught) => ({ bundle: null, error: errorMessage(caught) })
        ),
        getDemoSessionSnapshot().then(
          (snapshot) => ({ snapshot, error: null as string | null }),
          (caught) => ({ snapshot: null, error: errorMessage(caught) })
        ),
        getDemoHandoffReadiness(demoSessionReportInput).then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        listDemoSessionArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoHandoffPackageArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoHandoffPackageArchiveSummary().then(
          (summary) => ({ summary, error: null as string | null }),
          (caught) => ({ summary: null, error: errorMessage(caught) })
        ),
        getDemoHandoffShareChecklist().then(
          (checklist) => ({ checklist, error: null as string | null }),
          (caught) => ({ checklist: null, error: errorMessage(caught) })
        ),
        getDemoHandoffShareCenter().then(
          (center) => ({ center, error: null as string | null }),
          (caught) => ({ center: null, error: errorMessage(caught) })
        ),
        getDemoHandoffFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
        ),
        getDemoSelfHostedLaunchReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        listDemoSelfHostedLaunchReadinessArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoHandoffShareInstructions().then(
          (instructions) => ({ instructions, error: null as string | null }),
          (caught) => ({ instructions: null, error: errorMessage(caught) })
        ),
        listDemoHandoffShareDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        getDemoScript().then(
          (script) => ({ script, error: null as string | null }),
          (caught) => ({ script: null, error: errorMessage(caught) })
        ),
        getDemoReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        listDemoReadinessSnapshots().then(
          (snapshots) => ({ snapshots, error: null as string | null }),
          (caught) => ({ snapshots: null, error: errorMessage(caught) })
        ),
        getDemoReadinessSnapshotTrend().then(
          (trend) => ({ trend, error: null as string | null }),
          (caught) => ({ trend: null, error: errorMessage(caught) })
        ),
        getDemoSmokeChecklist().then(
          (checklist) => ({ checklist, error: null as string | null }),
          (caught) => ({ checklist: null, error: errorMessage(caught) })
        ),
        listLanguageAdapters().then(
          (adapters) => ({ adapters, error: null as string | null }),
          (caught) => ({ adapters: null, error: errorMessage(caught) })
        ),
        listLanguageAdapterFixtures().then(
          (verifications) => ({ verifications, error: null as string | null }),
          (caught) => ({ verifications: null, error: errorMessage(caught) })
        ),
        listLanguageAdapterRuntimeReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        listEvaluationCases().then(
          (cases) => ({ cases, error: null as string | null }),
          (caught) => ({ cases: null, error: errorMessage(caught) })
        ),
        getEvaluationSummary().then(
          (summary) => ({ summary, error: null as string | null }),
          (caught) => ({ summary: null, error: errorMessage(caught) })
        ),
        getEvaluationCaseReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        listEvaluationFixtureBaselineRuns().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getEvaluationFixtureBaselineRunRegressionSummary().then(
          (summary) => ({ summary, error: null as string | null }),
          (caught) => ({ summary: null, error: errorMessage(caught) })
        ),
        getEvaluationRunPreview().then(
          (preview) => ({ preview, error: null as string | null }),
          (caught) => ({ preview: null, error: errorMessage(caught) })
        ),
        listEvaluationRuns().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getEvaluationRunArchiveReadinessSummary().then(
          (summary) => ({ summary, error: null as string | null }),
          (caught) => ({ summary: null, error: errorMessage(caught) })
        ),
        listEvaluationRunSnapshots().then(
          (snapshots) => ({ snapshots, error: null as string | null }),
          (caught) => ({ snapshots: null, error: errorMessage(caught) })
        ),
        getQueueSummary(),
        listQueueItems(),
        getWorkerHealth(),
        listWebhookDeliveries(10).then(
          (deliveries) => ({ deliveries, error: null as string | null }),
          (caught) => ({ deliveries: null, error: errorMessage(caught) })
        ),
        listAcceptedTriggerDecisions(20).then(
          (decisions) => ({ decisions, error: null as string | null }),
          (caught) => ({ decisions: null, error: errorMessage(caught) })
        ),
        listRejectedTriggers({ limit: 20, category: rejectedTriggerCategoryFilter }).then(
          (rejections) => ({ rejections, error: null as string | null }),
          (caught) => ({ rejections: null, error: errorMessage(caught) })
        ),
        getRejectedTriggerSummary(100).then(
          (summary) => ({ summary, error: null as string | null }),
          (caught) => ({ summary: null, error: errorMessage(caught) })
        ),
        listTriggerQuarantines({ activeOnly: true, limit: 20 }).then(
          (quarantines) => ({ quarantines, error: null as string | null }),
          (caught) => ({ quarantines: null, error: errorMessage(caught) })
        ),
        listAdminAuditEvents(adminAuditFilters).then(
          (audits) => ({ audits, error: null as string | null }),
          (caught) => ({ audits: null, error: errorMessage(caught) })
        )
      ]);
      setTasks(taskList.items);
      const repositoryTarget = repositoryAccessTarget(taskList.items, repositoryOwnerFilter, repositoryNameFilter);
      if (repositoryTarget) {
        const repositoryAccessResult = await getGitHubRepositoryAccessReadiness(
          repositoryTarget.owner,
          repositoryTarget.repository
        ).then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        );
        setGitHubRepositoryAccessReadiness(repositoryAccessResult.readiness);
      } else {
        setGitHubRepositoryAccessReadiness(null);
      }
      setStatusCounts(taskStatusCounts);
      setMetrics(metricsSummary);
      setFailureCauses(failureCauseSummary);
      setModelUsage(modelUsageSummary);
      setLatency(latencySummary);
      setConfiguration(configurationSummary);
      if (githubCredentialReadinessResult.readiness) {
        setGitHubCredentialReadiness(githubCredentialReadinessResult.readiness);
      }
      if (githubWebhookUrlReadinessResult.readiness) {
        setGitHubWebhookUrlReadiness(githubWebhookUrlReadinessResult.readiness);
      }
      if (githubWebhookSetupReadinessResult.readiness) {
        setGitHubWebhookSetupReadiness(githubWebhookSetupReadinessResult.readiness);
      }
      if (modelProviderHealthResult.health) {
        setModelProviderHealth(modelProviderHealthResult.health);
      }
      if (demoEvidenceBundleResult.bundle) {
        setDemoEvidenceBundle(demoEvidenceBundleResult.bundle);
      }
      setDemoEvidenceBundleError(demoEvidenceBundleResult.error);
      if (demoSessionSnapshotResult.snapshot) {
        setDemoSessionSnapshot(demoSessionSnapshotResult.snapshot);
      }
      setDemoSessionSnapshotError(demoSessionSnapshotResult.error);
      if (demoHandoffReadinessResult.readiness) {
        setDemoHandoffReadiness(demoHandoffReadinessResult.readiness);
      }
      setDemoHandoffReadinessError(demoHandoffReadinessResult.error);
      if (demoSessionArchiveResult.archives) {
        setDemoSessionArchives(demoSessionArchiveResult.archives);
      }
      setDemoSessionArchiveError(demoSessionArchiveResult.error);
      if (demoHandoffPackageArchiveResult.archives) {
        setDemoHandoffPackageArchives(demoHandoffPackageArchiveResult.archives);
      }
      setDemoHandoffPackageArchiveError(demoHandoffPackageArchiveResult.error);
      if (demoHandoffPackageArchiveSummaryResult.summary) {
        setDemoHandoffPackageArchiveSummary(demoHandoffPackageArchiveSummaryResult.summary);
      }
      setDemoHandoffPackageArchiveSummaryError(demoHandoffPackageArchiveSummaryResult.error);
      if (demoHandoffShareChecklistResult.checklist) {
        setDemoHandoffShareChecklist(demoHandoffShareChecklistResult.checklist);
      }
      setDemoHandoffShareChecklistError(demoHandoffShareChecklistResult.error);
      if (demoHandoffShareCenterResult.center) {
        setDemoHandoffShareCenter(demoHandoffShareCenterResult.center);
      }
      setDemoHandoffShareCenterError(demoHandoffShareCenterResult.error);
      if (demoHandoffFinalizationResult.finalization) {
        setDemoHandoffFinalization(demoHandoffFinalizationResult.finalization);
      }
      setDemoHandoffFinalizationError(demoHandoffFinalizationResult.error);
      if (demoSelfHostedLaunchReadinessResult.readiness) {
        setDemoSelfHostedLaunchReadiness(demoSelfHostedLaunchReadinessResult.readiness);
      }
      setDemoSelfHostedLaunchReadinessError(demoSelfHostedLaunchReadinessResult.error);
      if (demoSelfHostedLaunchReadinessArchiveResult.archives) {
        setDemoSelfHostedLaunchReadinessArchives(demoSelfHostedLaunchReadinessArchiveResult.archives);
      }
      setDemoSelfHostedLaunchReadinessArchiveError(demoSelfHostedLaunchReadinessArchiveResult.error);
      if (demoHandoffShareInstructionsResult.instructions) {
        setDemoHandoffShareInstructions(demoHandoffShareInstructionsResult.instructions);
      }
      setDemoHandoffShareInstructionsError(demoHandoffShareInstructionsResult.error);
      if (demoHandoffShareDeliveryReceiptResult.receipts) {
        setDemoHandoffShareDeliveryReceipts(demoHandoffShareDeliveryReceiptResult.receipts);
      }
      setDemoHandoffShareDeliveryReceiptError(demoHandoffShareDeliveryReceiptResult.error);
      if (demoScriptResult.script) {
        setDemoScript(demoScriptResult.script);
      }
      setDemoScriptError(demoScriptResult.error);
      if (demoReadinessResult.readiness) {
        setDemoReadiness(demoReadinessResult.readiness);
      }
      setDemoReadinessError(demoReadinessResult.error);
      if (demoReadinessSnapshotResult.snapshots) {
        setDemoReadinessSnapshots(demoReadinessSnapshotResult.snapshots);
      }
      setDemoReadinessSnapshotError(demoReadinessSnapshotResult.error);
      if (demoReadinessSnapshotTrendResult.trend) {
        setDemoReadinessSnapshotTrend(demoReadinessSnapshotTrendResult.trend);
      }
      setDemoReadinessSnapshotTrendError(demoReadinessSnapshotTrendResult.error);
      if (demoSmokeChecklistResult.checklist) {
        setDemoSmokeChecklist(demoSmokeChecklistResult.checklist);
      }
      setDemoSmokeChecklistError(demoSmokeChecklistResult.error);
      if (adapterListResult.adapters) {
        setSupportedAdapters(adapterListResult.adapters);
      }
      setAdapterError(adapterListResult.error);
      if (adapterFixtureResult.verifications) {
        setAdapterFixtureVerifications(adapterFixtureResult.verifications);
      }
      setAdapterFixtureError(adapterFixtureResult.error);
      if (adapterRuntimeReadinessResult.readiness) {
        setAdapterRuntimeReadiness(adapterRuntimeReadinessResult.readiness);
      }
      setAdapterRuntimeReadinessError(adapterRuntimeReadinessResult.error);
      if (evaluationCaseResult.cases) {
        setEvaluationCases(evaluationCaseResult.cases);
      }
      setEvaluationCaseError(evaluationCaseResult.error);
      if (evaluationSummaryResult.summary) {
        setEvaluationSummary(evaluationSummaryResult.summary);
      }
      setEvaluationSummaryError(evaluationSummaryResult.error);
      if (evaluationCaseReadinessResult.readiness) {
        setEvaluationCaseReadiness(evaluationCaseReadinessResult.readiness);
      }
      setEvaluationCaseReadinessError(evaluationCaseReadinessResult.error);
      if (evaluationFixtureBaselineRunResult.archives) {
        setEvaluationFixtureBaselineRuns(evaluationFixtureBaselineRunResult.archives);
      }
      if (evaluationFixtureBaselineRunResult.error) {
        setEvaluationFixtureBaselineError((current) => current ?? evaluationFixtureBaselineRunResult.error);
      }
      if (evaluationFixtureBaselineRegressionResult.summary) {
        setEvaluationFixtureBaselineRegressionSummary(evaluationFixtureBaselineRegressionResult.summary);
      }
      setEvaluationFixtureBaselineRegressionError(evaluationFixtureBaselineRegressionResult.error);
      if (evaluationRunPreviewResult.preview) {
        setEvaluationRunPreview(evaluationRunPreviewResult.preview);
      }
      setEvaluationRunPreviewError(evaluationRunPreviewResult.error);
      if (evaluationRunResult.archives) {
        setEvaluationRuns(evaluationRunResult.archives);
      }
      setEvaluationRunError(evaluationRunResult.error);
      if (evaluationRunSummaryResult.summary) {
        setEvaluationRunSummary(evaluationRunSummaryResult.summary);
      }
      setEvaluationRunSummaryError(evaluationRunSummaryResult.error);
      if (evaluationRunSnapshotResult.snapshots) {
        setEvaluationRunSnapshots(evaluationRunSnapshotResult.snapshots);
      }
      setEvaluationRunSnapshotError(evaluationRunSnapshotResult.error);
      setQueueSummary(queueSummaryData);
      setQueueItems(queueItemList);
      setWorkerHealth(workerHealthData);
      if (webhookDeliveryResult.deliveries) {
        setWebhookDeliveries(webhookDeliveryResult.deliveries);
      }
      setWebhookDeliveryError(githubWebhookSetupReadinessResult.error ?? webhookDeliveryResult.error);
      if (acceptedTriggerDecisionResult.decisions) {
        setAcceptedTriggerDecisions(acceptedTriggerDecisionResult.decisions);
      }
      setAcceptedTriggerDecisionError(acceptedTriggerDecisionResult.error);
      if (rejectedTriggerResult.rejections) {
        setRejectedTriggers(rejectedTriggerResult.rejections);
      }
      if (rejectedTriggerSummaryResult.summary) {
        setRejectedTriggerSummary(rejectedTriggerSummaryResult.summary);
      }
      if (triggerQuarantineResult.quarantines) {
        setTriggerQuarantines(triggerQuarantineResult.quarantines);
      }
      if (adminAuditResult.audits) {
        setAdminAuditEvents(adminAuditResult.audits);
        setOperatorSafetyAudits(adminAuditResult.audits.filter(isTriggerQuarantineAudit));
      }
      setRejectedTriggerError(
        rejectedTriggerResult.error
        ?? rejectedTriggerSummaryResult.error
        ?? triggerQuarantineResult.error
        ?? adminAuditResult.error
      );
      setCanLoadMoreTasks(taskList.hasMore);
      setTaskTotal(taskList.total);
      setSelectedTaskId((current) => selectedTaskIdFromList(taskList.items, current));
      setLastRefreshedAt(new Date().toISOString());
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setLoading(false);
    }
  }, [
    adminAuditFilters,
    archivedDemoLaunchOutcomes,
    buildSystemFilter,
    createdAfterFilter,
    createdBeforeFilter,
    languageFilter,
    preparedDemoLaunchCommands,
    rejectedTriggerCategoryFilter,
    repositoryNameFilter,
    repositoryOwnerFilter,
    searchQuery,
    statusFilter,
    taskSort
  ]);

  const handleLoadMoreTasks = useCallback(async () => {
    setLoadingMoreTasks(true);
    setError(null);
    try {
      const nextTaskPage = await listTasks({
        status: statusFilter,
        query: searchQuery,
        repositoryOwner: repositoryOwnerFilter,
        repositoryName: repositoryNameFilter,
        language: languageFilter,
        buildSystem: buildSystemFilter,
        createdAfter: createdAfterFilter,
        createdBefore: createdBeforeFilter,
        sort: taskSort,
        limit: TASK_PAGE_SIZE,
        offset: tasks.length
      });
      setTasks((current) => [...current, ...nextTaskPage.items]);
      setCanLoadMoreTasks(nextTaskPage.hasMore);
      setTaskTotal(nextTaskPage.total);
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setLoadingMoreTasks(false);
    }
  }, [buildSystemFilter, createdAfterFilter, createdBeforeFilter, languageFilter, repositoryNameFilter, repositoryOwnerFilter, searchQuery, statusFilter, taskSort, tasks.length]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  useEffect(() => {
    if (!selectedTask) {
      setDetail(emptyDetail);
      return;
    }

    let cancelled = false;
    setDetail(emptyDetail);
    setDetailLoading(true);
    setError(null);
    const retryPreflightRequest = selectedTask.status === 'FAILED' || selectedTask.status === 'CANCELLED'
      ? getTaskRetryPreflight(selectedTask.id)
      : Promise.resolve(null);
    Promise.all([getTaskDetail(selectedTask.id), retryPreflightRequest])
      .then(([taskDetail, retryPreflight]) => {
        if (!cancelled) {
          setDetail({
            ...taskDetail,
            retryPreflight
          });
        }
      })
      .catch((caught) => {
        if (!cancelled) {
          setError(errorMessage(caught));
          setDetail(emptyDetail);
        }
      })
      .finally(() => {
        if (!cancelled) {
          setDetailLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [selectedTask]);

  const handleCancelTask = useCallback(async (taskId: string) => {
    setActionTaskId(taskId);
    setError(null);
    try {
      await cancelTask(taskId);
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setActionTaskId(null);
    }
  }, [refresh]);

  const handleRetryTask = useCallback(async (taskId: string, input: RetryTaskInput) => {
    setActionTaskId(taskId);
    setError(null);
    try {
      await retryTask(taskId, input);
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setActionTaskId(null);
    }
  }, [refresh]);

  const handleRetryRejectedTrigger = useCallback(async (rejectedTriggerId: string) => {
    setRetryingRejectedTriggerId(rejectedTriggerId);
    setError(null);
    try {
      const task = await retryRejectedTrigger(rejectedTriggerId);
      setSelectedTaskId(task.id);
      writeTaskIdToUrl(task.id);
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setRetryingRejectedTriggerId(null);
    }
  }, [refresh]);

  const handleApproveReview = useCallback(async (taskId: string, input: ApproveReviewInput) => {
    setActionTaskId(taskId);
    setError(null);
    try {
      await approveTaskReview(taskId, input);
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setActionTaskId(null);
    }
  }, [refresh]);

  const handleCopyReport = useCallback((taskId: string) => getTaskReport(taskId), []);
  const handleCopyDemoRunbook = useCallback(() => getDemoRunbook(), []);
  const handleCopyDemoSessionReport = useCallback((input: DemoSessionReportInput) => getDemoSessionReport(input), []);
  const handleDownloadDemoSessionReport = useCallback((input: DemoSessionReportInput) => (
    downloadDemoSessionReport(input)
  ), []);
  const handleCopyDemoHandoffPackage = useCallback((input: DemoSessionReportInput) => getDemoHandoffPackage(input), []);
  const handleDownloadDemoHandoffPackage = useCallback((input: DemoSessionReportInput) => (
    downloadDemoHandoffPackage(input)
  ), []);
  const handleDownloadDemoSessionArchiveReport = useCallback((archiveId: string) => (
    downloadDemoSessionArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoHandoffPackageArchiveReport = useCallback((archiveId: string) => (
    downloadDemoHandoffPackageArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoHandoffPackageArchiveSummaryReport = useCallback(() => (
    downloadDemoHandoffPackageArchiveSummaryReport()
  ), []);
  const handleDownloadDemoHandoffShareChecklistReport = useCallback(() => (
    downloadDemoHandoffShareChecklistReport()
  ), []);
  const handleDownloadDemoHandoffShareCenterReport = useCallback(() => (
    downloadDemoHandoffShareCenterReport()
  ), []);
  const handleDownloadDemoHandoffFinalizationReport = useCallback(() => (
    downloadDemoHandoffFinalizationReport()
  ), []);
  const handleDownloadDemoSelfHostedLaunchReadinessReport = useCallback(() => (
    downloadDemoSelfHostedLaunchReadinessReport()
  ), []);
  const handleDownloadDemoSelfHostedLaunchReadinessArchiveReport = useCallback((archiveId: string) => (
    downloadDemoSelfHostedLaunchReadinessArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoHandoffShareInstructionsReport = useCallback(() => (
    downloadDemoHandoffShareInstructionsReport()
  ), []);
  const handleCreateDemoHandoffShareDeliveryReceipt = useCallback(async (
    input: DemoHandoffShareDeliveryReceiptInput
  ) => {
    const receipt = await createDemoHandoffShareDeliveryReceipt(input);
    setDemoHandoffShareDeliveryReceipts((current) => [receipt, ...current.filter((item) => item.id !== receipt.id)].slice(0, 20));
    setDemoHandoffShareDeliveryReceiptError(null);
    try {
      const receipts = await listDemoHandoffShareDeliveryReceipts();
      setDemoHandoffShareDeliveryReceipts(receipts);
      setDemoHandoffShareDeliveryReceiptError(null);
    } catch (caught) {
      setDemoHandoffShareDeliveryReceiptError(errorMessage(caught));
    }
    try {
      const center = await getDemoHandoffShareCenter();
      setDemoHandoffShareCenter(center);
      setDemoHandoffShareCenterError(null);
    } catch (caught) {
      setDemoHandoffShareCenterError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoHandoffFinalization();
      setDemoHandoffFinalization(finalization);
      setDemoHandoffFinalizationError(null);
    } catch (caught) {
      setDemoHandoffFinalizationError(errorMessage(caught));
    }
    try {
      const readiness = await getDemoSelfHostedLaunchReadiness();
      setDemoSelfHostedLaunchReadiness(readiness);
      setDemoSelfHostedLaunchReadinessError(null);
    } catch (caught) {
      setDemoSelfHostedLaunchReadinessError(errorMessage(caught));
    }
    return receipt;
  }, []);
  const handleDownloadDemoHandoffShareDeliveryReceiptReport = useCallback((receiptId: string) => (
    downloadDemoHandoffShareDeliveryReceiptReport(receiptId)
  ), []);
  const handleArchiveDemoSession = useCallback(async (input: DemoSessionReportInput) => {
    const archive = await archiveDemoSession(input);
    setDemoSessionArchives((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setDemoSessionArchiveError(null);
    return archive;
  }, []);
  const handleArchiveDemoHandoffPackage = useCallback(async (input: DemoSessionReportInput) => {
    const archive = await archiveDemoHandoffPackage(input);
    setDemoHandoffPackageArchives((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setDemoHandoffPackageArchiveError(null);
    try {
      const summary = await getDemoHandoffPackageArchiveSummary();
      setDemoHandoffPackageArchiveSummary(summary);
      setDemoHandoffPackageArchiveSummaryError(null);
    } catch (caught) {
      setDemoHandoffPackageArchiveSummaryError(errorMessage(caught));
    }
    try {
      const checklist = await getDemoHandoffShareChecklist();
      setDemoHandoffShareChecklist(checklist);
      setDemoHandoffShareChecklistError(null);
    } catch (caught) {
      setDemoHandoffShareChecklistError(errorMessage(caught));
    }
    try {
      const center = await getDemoHandoffShareCenter();
      setDemoHandoffShareCenter(center);
      setDemoHandoffShareCenterError(null);
    } catch (caught) {
      setDemoHandoffShareCenterError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoHandoffFinalization();
      setDemoHandoffFinalization(finalization);
      setDemoHandoffFinalizationError(null);
    } catch (caught) {
      setDemoHandoffFinalizationError(errorMessage(caught));
    }
    try {
      const readiness = await getDemoSelfHostedLaunchReadiness();
      setDemoSelfHostedLaunchReadiness(readiness);
      setDemoSelfHostedLaunchReadinessError(null);
    } catch (caught) {
      setDemoSelfHostedLaunchReadinessError(errorMessage(caught));
    }
    try {
      const instructions = await getDemoHandoffShareInstructions();
      setDemoHandoffShareInstructions(instructions);
      setDemoHandoffShareInstructionsError(null);
    } catch (caught) {
      setDemoHandoffShareInstructionsError(errorMessage(caught));
    }
    return archive;
  }, []);

  const handleArchiveDemoSelfHostedLaunchReadiness = useCallback(async () => {
    const archive = await archiveDemoSelfHostedLaunchReadiness();
    setDemoSelfHostedLaunchReadinessArchives((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setDemoSelfHostedLaunchReadinessArchiveError(null);
    try {
      const readiness = await getDemoSelfHostedLaunchReadiness();
      setDemoSelfHostedLaunchReadiness(readiness);
      setDemoSelfHostedLaunchReadinessError(null);
    } catch (caught) {
      setDemoSelfHostedLaunchReadinessError(errorMessage(caught));
    }
    return archive;
  }, []);

  const handleArchiveDemoReadinessSnapshot = useCallback(async () => {
    const archive = await archiveDemoReadinessSnapshot();
    setDemoReadinessSnapshots((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setDemoReadinessSnapshotError(null);
    try {
      const trend = await getDemoReadinessSnapshotTrend();
      setDemoReadinessSnapshotTrend(trend);
      setDemoReadinessSnapshotTrendError(null);
    } catch (caught) {
      setDemoReadinessSnapshotTrendError(errorMessage(caught));
    }
    return archive;
  }, []);

  const handleDownloadDemoReadinessSnapshotReport = useCallback((snapshotId: string) => (
    downloadDemoReadinessSnapshotReport(snapshotId)
  ), []);

  const handleArchiveEvaluationRunSnapshot = useCallback(async () => {
    const archive = await archiveEvaluationRunSnapshot();
    setEvaluationRunSnapshots((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setEvaluationRunSnapshotError(null);
    return archive;
  }, []);

  const handleRunAndArchiveEvaluation = useCallback(async () => {
    setEvaluationRunLoading(true);
    setEvaluationRunError(null);
    try {
      const archive = await runAndArchiveEvaluation();
      setEvaluationRuns((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
      try {
        const summary = await getEvaluationRunArchiveReadinessSummary();
        setEvaluationRunSummary(summary);
        setEvaluationRunSummaryError(null);
      } catch (caught) {
        setEvaluationRunSummaryError(errorMessage(caught));
      }
      return archive;
    } catch (caught) {
      setEvaluationRunError(errorMessage(caught));
      throw caught;
    } finally {
      setEvaluationRunLoading(false);
    }
  }, []);

  const handleRunEvaluationFixtureBaseline = useCallback(async () => {
    setEvaluationFixtureBaselineLoading(true);
    setEvaluationFixtureBaselineError(null);
    try {
      const baseline = await runEvaluationFixtureBaseline();
      setEvaluationFixtureBaseline(baseline);
      return baseline;
    } catch (caught) {
      setEvaluationFixtureBaselineError(errorMessage(caught));
      throw caught;
    } finally {
      setEvaluationFixtureBaselineLoading(false);
    }
  }, []);

  const handleRunAndArchiveEvaluationFixtureBaseline = useCallback(async () => {
    setEvaluationFixtureBaselineLoading(true);
    setEvaluationFixtureBaselineError(null);
    setEvaluationFixtureBaselineRegressionError(null);
    try {
      const archive = await runAndArchiveEvaluationFixtureBaseline();
      setEvaluationFixtureBaselineRuns((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
      try {
        const regressionSummary = await getEvaluationFixtureBaselineRunRegressionSummary();
        setEvaluationFixtureBaselineRegressionSummary(regressionSummary);
        setEvaluationFixtureBaselineRegressionError(null);
      } catch (caught) {
        setEvaluationFixtureBaselineRegressionError(errorMessage(caught));
      }
      return archive;
    } catch (caught) {
      setEvaluationFixtureBaselineError(errorMessage(caught));
      throw caught;
    } finally {
      setEvaluationFixtureBaselineLoading(false);
    }
  }, []);

  const handleDownloadEvaluationFixtureBaselineRunReport = useCallback((runId: string) => (
    downloadEvaluationFixtureBaselineRunReport(runId)
  ), []);

  const handleDownloadEvaluationRunSnapshotReport = useCallback((snapshotId: string) => (
    downloadEvaluationRunSnapshotReport(snapshotId)
  ), []);

  const handleDownloadEvaluationRunReport = useCallback((runId: string) => (
    downloadEvaluationRunReport(runId)
  ), []);

  const handleComposeDemoLaunchCommand = useCallback(async (input: DemoLaunchCommandInput) => {
    setDemoLaunchCommandPending(true);
    setDemoLaunchCommandError(null);
    try {
      const result = await composeDemoLaunchCommand(input);
      setDemoLaunchCommand(result);
      return result;
    } catch (caught) {
      setDemoLaunchCommandError(errorMessage(caught));
      throw caught;
    } finally {
      setDemoLaunchCommandPending(false);
    }
  }, []);

  const handleApplyDemoLaunchCommandToPreflight = useCallback((input: DemoLaunchPreflightInput) => {
    setComposedPreflightInput(input);
  }, []);

  const handleDemoLaunchCommandHistoryChange = useCallback(() => {
    setDemoLaunchCommandHistoryRevision((currentRevision) => currentRevision + 1);
  }, []);

  const handleDemoLaunchOutcomeArchiveChange = useCallback(() => {
    setDemoLaunchOutcomeArchiveRevision((currentRevision) => currentRevision + 1);
  }, []);

  const handleDemoLaunchPreflight = useCallback(async (input: DemoLaunchPreflightInput) => {
    setDemoLaunchPreflightPending(true);
    setDemoLaunchPreflightError(null);
    try {
      const result = await preflightDemoLaunch(input);
      setDemoLaunchPreflight(result);
      return result;
    } catch (caught) {
      setDemoLaunchPreflightError(errorMessage(caught));
      throw caught;
    } finally {
      setDemoLaunchPreflightPending(false);
    }
  }, []);

  const handleCreateTask = useCallback(async (input: CreateTaskInput) => {
    setCreatingTask(true);
    setCreateTaskStatus(null);
    setTriggerEvaluation(null);
    setError(null);
    try {
      const task = await createTask(input);
      setSelectedTaskId(task.id);
      writeTaskIdToUrl(task.id);
      setCreateTaskStatus('Manual task queued');
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
      throw caught;
    } finally {
      setCreatingTask(false);
    }
  }, [refresh]);

  const handleEvaluateTrigger = useCallback(async (input: CreateTaskInput) => {
    setEvaluatingTrigger(true);
    setCreateTaskStatus(null);
    setError(null);
    try {
      const result = await evaluateTrigger(input);
      setTriggerEvaluation(result);
      return result;
    } catch (caught) {
      setError(errorMessage(caught));
      throw caught;
    } finally {
      setEvaluatingTrigger(false);
    }
  }, []);

  const handleEvaluateWebhookPayload = useCallback(async (input: WebhookPayloadDiagnosticInput) => {
    setEvaluatingWebhookPayload(true);
    setWebhookPayloadDiagnosticError(null);
    try {
      const result = await evaluateWebhookPayloadDiagnostic(input);
      setWebhookPayloadDiagnostic(result);
      return result;
    } catch (caught) {
      setWebhookPayloadDiagnosticError(errorMessage(caught));
      throw caught;
    } finally {
      setEvaluatingWebhookPayload(false);
    }
  }, []);

  const handleCreateTriggerQuarantine = useCallback(async (input: CreateTriggerQuarantineInput) => {
    setCreatingTriggerQuarantine(true);
    setError(null);
    try {
      await createTriggerQuarantine(input);
      setTriggerQuarantineEvidence(null);
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setCreatingTriggerQuarantine(false);
    }
  }, [refresh]);

  const handleReleaseTriggerQuarantine = useCallback(async (
    quarantineId: string,
    input: ReleaseTriggerQuarantineInput
  ) => {
    setReleasingTriggerQuarantineId(quarantineId);
    setError(null);
    try {
      await releaseTriggerQuarantine(quarantineId, input);
      setTriggerQuarantineEvidence(null);
      await refresh();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setReleasingTriggerQuarantineId(null);
    }
  }, [refresh]);

  const handleInspectTriggerQuarantine = useCallback(async (quarantineId: string) => {
    setInspectingTriggerQuarantineId(quarantineId);
    setError(null);
    try {
      setTriggerQuarantineEvidence(await getTriggerQuarantineEvidence(quarantineId, 20));
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setInspectingTriggerQuarantineId(null);
    }
  }, []);

  const handleAdminTokenSubmit = useCallback((event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const trimmedToken = adminTokenInput.trim();
    if (!trimmedToken || typeof globalThis.localStorage === 'undefined') {
      return;
    }
    globalThis.localStorage.setItem(ADMIN_TOKEN_STORAGE_KEY, trimmedToken);
    setDashboardAdminTokenInput(trimmedToken);
    setHasStoredAdminToken(true);
    setAdminTokenInput('');
    setError(null);
    void refresh();
  }, [adminTokenInput, refresh]);

  const handleDashboardAdminTokenSubmit = useCallback((event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const trimmedToken = dashboardAdminTokenInput.trim();
    if (!trimmedToken || typeof globalThis.localStorage === 'undefined') {
      return;
    }
    globalThis.localStorage.setItem(ADMIN_TOKEN_STORAGE_KEY, trimmedToken);
    setDashboardAdminTokenInput(trimmedToken);
    setHasStoredAdminToken(true);
    setError(null);
    void refresh();
  }, [dashboardAdminTokenInput, refresh]);

  const handleClearAdminToken = useCallback(() => {
    if (typeof globalThis.localStorage !== 'undefined') {
      globalThis.localStorage.removeItem(ADMIN_TOKEN_STORAGE_KEY);
    }
    setDashboardAdminTokenInput('');
    setHasStoredAdminToken(false);
    setError(null);
    void refresh();
  }, [refresh]);

  const adminTokenRequired = error === ADMIN_TOKEN_REQUIRED_MESSAGE;

  return (
    <main className="app-shell">
      <header className="top-bar">
        <div>
          <p className="eyebrow">Self-hosted agent control plane</p>
          <h1>PatchPilot Operations</h1>
          {lastRefreshedAt ? (
            <time className="last-refresh-time" dateTime={lastRefreshedAt}>
              Last refreshed {compactDateTime(lastRefreshedAt)}
            </time>
          ) : null}
        </div>
        <div className="top-bar-actions">
          <section className="admin-token-manager" aria-label="Admin token management">
            <div>
              <span>Admin token</span>
              <strong>{hasStoredAdminToken ? 'Admin token saved' : 'No admin token saved'}</strong>
            </div>
            <form onSubmit={handleDashboardAdminTokenSubmit}>
              <label>
                <span>Dashboard admin token</span>
                <input
                  type="password"
                  value={dashboardAdminTokenInput}
                  onChange={(event) => setDashboardAdminTokenInput(event.target.value)}
                  autoComplete="off"
                />
              </label>
              <button className="secondary-button" type="submit" disabled={!dashboardAdminTokenInput.trim()}>
                Save dashboard admin token
              </button>
              {hasStoredAdminToken ? (
                <button className="secondary-button" type="button" onClick={handleClearAdminToken}>
                  Clear admin token
                </button>
              ) : null}
            </form>
          </section>
          <button
            className="icon-button"
            type="button"
            onClick={() => void refresh()}
            aria-label={loading ? 'Refreshing dashboard' : 'Refresh dashboard'}
            disabled={loading}
          >
            <RefreshCw size={17} />
            {loading ? 'Refreshing' : 'Refresh'}
          </button>
        </div>
      </header>

      {loading ? (
        <section className="refresh-status" role="status">
          Dashboard refreshing
        </section>
      ) : null}

      {error ? (
        <section className="alert" role="alert">
          <AlertCircle size={18} />
          <div className="alert-content">
            <span>{error}</span>
            {adminTokenRequired ? (
              <form className="admin-token-form" onSubmit={handleAdminTokenSubmit}>
                <label>
                  <span>Admin API token</span>
                  <input
                    type="password"
                    value={adminTokenInput}
                    onChange={(event) => setAdminTokenInput(event.target.value)}
                    autoComplete="off"
                  />
                </label>
                <button className="secondary-button" type="submit" disabled={!adminTokenInput.trim()}>
                  Save admin token
                </button>
              </form>
            ) : null}
          </div>
        </section>
      ) : null}

      <ConnectivityPanel
        backendHealth={backendHealth}
        configuration={configuration}
        hasStoredAdminToken={hasStoredAdminToken}
        error={error}
      />

      <OperatorSetupChecklistPanel
        backendHealth={backendHealth}
        configuration={configuration}
        githubCredentialReadiness={githubCredentialReadiness}
        githubRepositoryAccessReadiness={githubRepositoryAccessReadiness}
        githubWebhookUrlReadiness={githubWebhookUrlReadiness}
        modelProviderHealth={modelProviderHealth}
        demoReadiness={demoReadiness}
        adapterFixtureVerifications={adapterFixtureVerifications}
        adapterRuntimeReadiness={adapterRuntimeReadiness}
        queueSummary={queueSummary}
        workerHealth={workerHealth}
        tasks={tasks}
        hasStoredAdminToken={hasStoredAdminToken}
      />

      <SelfHostedLaunchReadinessPanel
        readiness={demoSelfHostedLaunchReadiness}
        error={demoSelfHostedLaunchReadinessError}
        archives={demoSelfHostedLaunchReadinessArchives}
        archiveError={demoSelfHostedLaunchReadinessArchiveError}
        onArchiveReadiness={handleArchiveDemoSelfHostedLaunchReadiness}
        onDownloadReport={handleDownloadDemoSelfHostedLaunchReadinessReport}
        onDownloadArchiveReport={handleDownloadDemoSelfHostedLaunchReadinessArchiveReport}
      />

      <DemoEvidenceBundlePanel
        bundle={demoEvidenceBundle}
        error={demoEvidenceBundleError}
        onCopyRunbook={handleCopyDemoRunbook}
      />

      <DemoSessionSnapshotPanel
        snapshot={demoSessionSnapshot}
        preparedLaunchCommands={preparedDemoLaunchCommands}
        archivedLaunchOutcomes={archivedDemoLaunchOutcomes}
        handoffReadiness={demoHandoffReadiness}
        archives={demoSessionArchives}
        handoffPackageArchives={demoHandoffPackageArchives}
        handoffPackageArchiveSummary={demoHandoffPackageArchiveSummary}
        handoffShareChecklist={demoHandoffShareChecklist}
        handoffShareCenter={demoHandoffShareCenter}
        handoffFinalization={demoHandoffFinalization}
        handoffShareInstructions={demoHandoffShareInstructions}
        handoffShareDeliveryReceipts={demoHandoffShareDeliveryReceipts}
        error={demoSessionSnapshotError}
        handoffReadinessError={demoHandoffReadinessError}
        archiveError={demoSessionArchiveError}
        handoffPackageArchiveError={demoHandoffPackageArchiveError}
        handoffPackageArchiveSummaryError={demoHandoffPackageArchiveSummaryError}
        handoffShareChecklistError={demoHandoffShareChecklistError}
        handoffShareCenterError={demoHandoffShareCenterError}
        handoffFinalizationError={demoHandoffFinalizationError}
        handoffShareInstructionsError={demoHandoffShareInstructionsError}
        handoffShareDeliveryReceiptError={demoHandoffShareDeliveryReceiptError}
        onCopyReport={handleCopyDemoSessionReport}
        onDownloadReport={handleDownloadDemoSessionReport}
        onArchiveSession={handleArchiveDemoSession}
        onCopyHandoffPackage={handleCopyDemoHandoffPackage}
        onDownloadHandoffPackage={handleDownloadDemoHandoffPackage}
        onArchiveHandoffPackage={handleArchiveDemoHandoffPackage}
        onDownloadArchiveReport={handleDownloadDemoSessionArchiveReport}
        onDownloadHandoffPackageArchiveReport={handleDownloadDemoHandoffPackageArchiveReport}
        onDownloadHandoffPackageArchiveSummaryReport={handleDownloadDemoHandoffPackageArchiveSummaryReport}
        onDownloadHandoffShareCenterReport={handleDownloadDemoHandoffShareCenterReport}
        onDownloadHandoffFinalizationReport={handleDownloadDemoHandoffFinalizationReport}
        onDownloadHandoffShareInstructionsReport={handleDownloadDemoHandoffShareInstructionsReport}
        onCreateHandoffShareDeliveryReceipt={handleCreateDemoHandoffShareDeliveryReceipt}
        onDownloadHandoffShareDeliveryReceiptReport={handleDownloadDemoHandoffShareDeliveryReceiptReport}
        onDownloadHandoffShareChecklistReport={handleDownloadDemoHandoffShareChecklistReport}
      />

      <DemoScriptPanel script={demoScript} error={demoScriptError} />

      <DemoReadinessPanel
        readiness={demoReadiness}
        error={demoReadinessError}
        snapshots={demoReadinessSnapshots}
        snapshotError={demoReadinessSnapshotError}
        snapshotTrend={demoReadinessSnapshotTrend}
        snapshotTrendError={demoReadinessSnapshotTrendError}
        onArchiveReadiness={handleArchiveDemoReadinessSnapshot}
        onDownloadSnapshotReport={handleDownloadDemoReadinessSnapshotReport}
      />

      <DemoSmokeChecklistPanel checklist={demoSmokeChecklist} error={demoSmokeChecklistError} />

      <DemoLaunchCommandPanel
        result={demoLaunchCommand}
        error={demoLaunchCommandError}
        pending={demoLaunchCommandPending}
        onComposeCommand={handleComposeDemoLaunchCommand}
        onApplyToPreflight={handleApplyDemoLaunchCommandToPreflight}
        onHistoryChange={handleDemoLaunchCommandHistoryChange}
      />

      <DemoLaunchPreflightPanel
        result={demoLaunchPreflight}
        error={demoLaunchPreflightError}
        pending={demoLaunchPreflightPending}
        preparedLaunchCommands={preparedDemoLaunchCommands}
        composedPreflightInput={composedPreflightInput}
        onRunPreflight={handleDemoLaunchPreflight}
      />

      <DemoLaunchTrackerPanel
        preparedLaunchCommands={preparedDemoLaunchCommands}
        tasks={tasks}
        webhookDeliveries={webhookDeliveries}
        onOutcomeArchiveChange={handleDemoLaunchOutcomeArchiveChange}
      />

      <section className="metrics-grid" aria-label="Task metrics">
        <MetricCard label="Tasks" value={metrics?.totalCount ?? 0} detail={`${metrics?.completedCount ?? 0} completed`} />
        <MetricCard label="Failed" value={metrics?.failedCount ?? 0} detail={`${percent(metrics?.failureRate)} failure rate`} />
        <MetricCard label="Completion" value={percent(metrics?.completionRate)} detail={`${duration(metrics?.averageCompletionDurationMs)} avg`} />
        <MetricCard label="Test pass" value={percent(metrics?.testPassRate)} detail={`${metrics?.passedTestRunCount ?? 0}/${metrics?.testRunCount ?? 0} runs`} />
        <MetricCard label="Model tokens" value={metrics?.totalModelTokens ?? 0} detail={`${metrics?.averageModelTokensPerCompletedTask ?? 0} avg completed`} />
      </section>

      <section className="summary-panel-grid" aria-label="Operational summaries">
        <FailureCausePanel causes={failureCauses} />
        <ModelUsagePanel usage={modelUsage} />
        <LatencyPanel latency={latency} />
      </section>

      <ManualTaskForm
        creating={creatingTask}
        evaluating={evaluatingTrigger}
        evaluation={triggerEvaluation}
        successMessage={createTaskStatus}
        onCreateTask={handleCreateTask}
        onEvaluateTrigger={handleEvaluateTrigger}
      />

      <section className="workspace-grid">
        <TaskListPanel
          tasks={tasks}
          selectedTask={selectedTask}
          statusFilter={statusFilter}
          searchQuery={searchQuery}
          repositoryOwnerFilter={repositoryOwnerFilter}
          repositoryNameFilter={repositoryNameFilter}
          languageFilter={languageFilter}
          buildSystemFilter={buildSystemFilter}
          createdAfterFilter={createdAfterFilter}
          createdBeforeFilter={createdBeforeFilter}
          statusCounts={statusCounts}
          taskSort={taskSort}
          loading={loading}
          totalCount={taskTotal}
          canLoadMore={canLoadMoreTasks}
          loadingMore={loadingMoreTasks}
          canClearFilters={
            statusFilter !== 'ALL' ||
            searchQuery.trim().length > 0 ||
            repositoryOwnerFilter.trim().length > 0 ||
            repositoryNameFilter.trim().length > 0 ||
            languageFilter.trim().length > 0 ||
            buildSystemFilter.trim().length > 0 ||
            createdAfterFilter.trim().length > 0 ||
            createdBeforeFilter.trim().length > 0
          }
          onStatusFilterChange={handleStatusFilterChange}
          onSearchQueryChange={handleSearchQueryChange}
          onRepositoryOwnerFilterChange={handleRepositoryOwnerFilterChange}
          onRepositoryNameFilterChange={handleRepositoryNameFilterChange}
          onLanguageFilterChange={handleLanguageFilterChange}
          onBuildSystemFilterChange={handleBuildSystemFilterChange}
          onCreatedAfterFilterChange={handleCreatedAfterFilterChange}
          onCreatedBeforeFilterChange={handleCreatedBeforeFilterChange}
          onTaskSortChange={handleTaskSortChange}
          onClearFilters={handleClearFilters}
          onSelectTask={selectTask}
          onLoadMoreTasks={handleLoadMoreTasks}
        />

        <TaskDetailPanel
          task={selectedTask}
          detail={detail}
          loading={detailLoading}
          actionInFlight={actionTaskId === selectedTask?.id}
          reviewApprovalAllowedOperators={configuration?.reviewApprovalAllowedOperators ?? []}
          onCancelTask={handleCancelTask}
          onRetryTask={handleRetryTask}
          onApproveReview={handleApproveReview}
          onCopyReport={handleCopyReport}
        />
      </section>

      <TriggerDecisionPanel
        task={selectedTask}
        timeline={detail.timeline}
        rejectedTriggers={rejectedTriggers}
        summary={rejectedTriggerSummary}
      />

      <AcceptedTriggerDecisionPanel
        decisions={acceptedTriggerDecisions}
        error={acceptedTriggerDecisionError}
        onSelectTask={selectTask}
      />

      <ConfigurationPanel configuration={configuration} backendHealth={backendHealth} />

      <AdapterReadinessReportPanel
        adapters={supportedAdapters}
        verifications={adapterFixtureVerifications}
        runtimeReadiness={adapterRuntimeReadiness}
        error={adapterError ?? adapterFixtureError ?? adapterRuntimeReadinessError}
      />

      <EvaluationCaseCatalogPanel
        cases={evaluationCases}
        summary={evaluationSummary}
        caseReadiness={evaluationCaseReadiness}
        fixtureBaseline={evaluationFixtureBaseline}
        fixtureBaselineLoading={evaluationFixtureBaselineLoading}
        fixtureBaselineRuns={evaluationFixtureBaselineRuns}
        fixtureBaselineRegressionSummary={evaluationFixtureBaselineRegressionSummary}
        runPreview={evaluationRunPreview}
        evaluationRuns={evaluationRuns}
        evaluationRunSummary={evaluationRunSummary}
        evaluationRunLoading={evaluationRunLoading}
        archives={evaluationRunSnapshots}
        error={evaluationCaseError}
        summaryError={evaluationSummaryError}
        caseReadinessError={evaluationCaseReadinessError}
        fixtureBaselineError={evaluationFixtureBaselineError}
        fixtureBaselineRegressionError={evaluationFixtureBaselineRegressionError}
        runPreviewError={evaluationRunPreviewError}
        evaluationRunError={evaluationRunError}
        evaluationRunSummaryError={evaluationRunSummaryError}
        archiveError={evaluationRunSnapshotError}
        onRunFixtureBaseline={handleRunEvaluationFixtureBaseline}
        onRunAndArchiveFixtureBaseline={handleRunAndArchiveEvaluationFixtureBaseline}
        onDownloadFixtureBaselineRunReport={handleDownloadEvaluationFixtureBaselineRunReport}
        onArchiveRunSnapshot={handleArchiveEvaluationRunSnapshot}
        onRunAndArchiveEvaluation={handleRunAndArchiveEvaluation}
        onDownloadEvaluationRunReport={handleDownloadEvaluationRunReport}
        onDownloadArchiveReport={handleDownloadEvaluationRunSnapshotReport}
      />

      <SupportedAdaptersPanel adapters={supportedAdapters} error={adapterError} />

      <RepositoryPreflightPanel
        result={repositoryPreflightResult}
        error={repositoryPreflightError}
        loading={repositoryPreflightLoading}
        allowedRootDirs={configuration?.repositoryPreflightAllowedRootDirs ?? []}
        onRunPreflight={handleRepositoryPreflight}
      />

      <AdapterFixtureVerificationPanel verifications={adapterFixtureVerifications} error={adapterFixtureError} />

      <QueuePanel summary={queueSummary} items={queueItems} workerHealth={workerHealth} />

      <WebhookDeliveryPanel
        setupReadiness={githubWebhookSetupReadiness}
        deliveries={webhookDeliveries}
        error={webhookDeliveryError}
        evaluatingPayload={evaluatingWebhookPayload}
        payloadDiagnostic={webhookPayloadDiagnostic}
        payloadDiagnosticError={webhookPayloadDiagnosticError}
        onEvaluatePayload={handleEvaluateWebhookPayload}
      />

      <AdminAuditPanel
        audits={adminAuditEvents}
        error={rejectedTriggerError}
        filters={adminAuditFilters}
        onFiltersChange={setAdminAuditFilters}
      />

      <RejectedTriggerPanel
        rejectedTriggers={rejectedTriggers}
        summary={rejectedTriggerSummary}
        quarantines={triggerQuarantines}
        operatorSafetyAudits={operatorSafetyAudits}
        categoryFilter={rejectedTriggerCategoryFilter}
        error={rejectedTriggerError}
        retryingRejectedTriggerId={retryingRejectedTriggerId}
        onCategoryFilterChange={handleRejectedTriggerCategoryFilterChange}
        onRetryRejectedTrigger={handleRetryRejectedTrigger}
        onSelectTask={selectTask}
        onCreateTriggerQuarantine={handleCreateTriggerQuarantine}
        onReleaseTriggerQuarantine={handleReleaseTriggerQuarantine}
        onInspectTriggerQuarantine={handleInspectTriggerQuarantine}
        creatingTriggerQuarantine={creatingTriggerQuarantine}
        releasingTriggerQuarantineId={releasingTriggerQuarantineId}
        inspectingTriggerQuarantineId={inspectingTriggerQuarantineId}
        triggerQuarantineEvidence={triggerQuarantineEvidence}
      />
    </main>
  );
}

function errorMessage(caught: unknown) {
  return caught instanceof Error ? caught.message : 'Dashboard request failed';
}

function storedAdminToken() {
  if (typeof globalThis.localStorage === 'undefined') {
    return '';
  }
  return globalThis.localStorage.getItem(ADMIN_TOKEN_STORAGE_KEY)?.trim() ?? '';
}

async function bootstrapAdminToken() {
  if (storedAdminToken()) {
    return null;
  }
  const bootstrap = await getDashboardBootstrap().catch(() => null);
  if (!bootstrap?.adminToken) {
    return null;
  }
  globalThis.localStorage.setItem(ADMIN_TOKEN_STORAGE_KEY, bootstrap.adminToken);
  return bootstrap.adminToken;
}

function isTriggerQuarantineAudit(audit: OperatorSafetyAudit) {
  return audit.resourceType === 'TRIGGER_QUARANTINE';
}

function taskIdFromUrl() {
  const pathMatch = window.location.pathname.match(/^\/tasks\/([^/]+)$/);
  if (pathMatch) {
    return decodeURIComponent(pathMatch[1]);
  }
  return new URLSearchParams(window.location.search).get('taskId');
}

function filtersFromUrl() {
  const searchParams = new URLSearchParams(window.location.search);
  return {
    status: statusFilterFromUrl(searchParams.get('status')),
    query: searchParams.get('query') ?? '',
    repositoryOwner: searchParams.get('repositoryOwner') ?? '',
    repositoryName: searchParams.get('repositoryName') ?? '',
    language: searchParams.get('language') ?? '',
    buildSystem: searchParams.get('buildSystem') ?? '',
    createdAfter: searchParams.get('createdAfter') ?? '',
    createdBefore: searchParams.get('createdBefore') ?? '',
    sort: sortFromUrl(searchParams.get('sort')),
    rejectedCategory: rejectedTriggerCategoryFromUrl(searchParams.get('rejectedCategory'))
  };
}

function statusFilterFromUrl(value: string | null): TaskStatusFilter {
  if (value && TASK_STATUS_FILTERS.includes(value as TaskStatusFilter)) {
    return value as TaskStatusFilter;
  }
  return 'ALL';
}

function sortFromUrl(value: string | null): TaskSort {
  if (value && TASK_SORTS.includes(value as TaskSort)) {
    return value as TaskSort;
  }
  return 'createdAtDesc';
}

function rejectedTriggerCategoryFromUrl(value: string | null): RejectedTriggerCategoryFilter {
  if (value && REJECTED_TRIGGER_CATEGORY_FILTERS.includes(value as RejectedTriggerCategoryFilter)) {
    return value as RejectedTriggerCategoryFilter;
  }
  return 'ALL';
}

function selectedTaskIdFromList(tasks: FixTask[], currentTaskId: string | null) {
  if (currentTaskId && tasks.some((task) => task.id === currentTaskId)) {
    return currentTaskId;
  }
  return tasks[0]?.id ?? null;
}

function repositoryAccessTarget(tasks: FixTask[], repositoryOwnerFilter: string, repositoryNameFilter: string) {
  const owner = repositoryOwnerFilter.trim();
  const repository = repositoryNameFilter.trim();
  if (owner && repository) {
    return { owner, repository };
  }
  const task = tasks.find((candidate) => candidate.repositoryOwner && candidate.repositoryName);
  if (!task) {
    return null;
  }
  return {
    owner: task.repositoryOwner,
    repository: task.repositoryName
  };
}

function writeTaskIdToUrl(taskId: string) {
  const nextUrl = new URL(window.location.href);
  nextUrl.pathname = `/tasks/${encodeURIComponent(taskId)}`;
  nextUrl.searchParams.delete('taskId');
  window.history.replaceState(null, '', `${nextUrl.pathname}${nextUrl.search}${nextUrl.hash}`);
}

interface TaskListUrlState {
  status: TaskStatusFilter;
  query: string;
  repositoryOwner: string;
  repositoryName: string;
  language: string;
  buildSystem: string;
  createdAfter: string;
  createdBefore: string;
  sort: TaskSort;
}

function writeRejectedTriggerStateToUrl(category: RejectedTriggerCategoryFilter) {
  const nextUrl = new URL(window.location.href);
  if (category === 'ALL') {
    nextUrl.searchParams.delete('rejectedCategory');
  } else {
    nextUrl.searchParams.set('rejectedCategory', category);
  }
  window.history.replaceState(null, '', `${nextUrl.pathname}${nextUrl.search}${nextUrl.hash}`);
}

function writeTaskListStateToUrl({
  status,
  query,
  repositoryOwner,
  repositoryName,
  language,
  buildSystem,
  createdAfter,
  createdBefore,
  sort
}: TaskListUrlState) {
  const nextUrl = new URL(window.location.href);
  nextUrl.searchParams.delete('taskId');
  if (status === 'ALL') {
    nextUrl.searchParams.delete('status');
  } else {
    nextUrl.searchParams.set('status', status);
  }
  if (query.trim()) {
    nextUrl.searchParams.set('query', query.trim());
  } else {
    nextUrl.searchParams.delete('query');
  }
  if (repositoryOwner.trim()) {
    nextUrl.searchParams.set('repositoryOwner', repositoryOwner.trim());
  } else {
    nextUrl.searchParams.delete('repositoryOwner');
  }
  if (repositoryName.trim()) {
    nextUrl.searchParams.set('repositoryName', repositoryName.trim());
  } else {
    nextUrl.searchParams.delete('repositoryName');
  }
  if (language.trim()) {
    nextUrl.searchParams.set('language', language.trim());
  } else {
    nextUrl.searchParams.delete('language');
  }
  if (buildSystem.trim()) {
    nextUrl.searchParams.set('buildSystem', buildSystem.trim());
  } else {
    nextUrl.searchParams.delete('buildSystem');
  }
  if (sort === 'createdAtDesc') {
    nextUrl.searchParams.delete('sort');
  } else {
    nextUrl.searchParams.set('sort', sort);
  }
  if (createdAfter.trim()) {
    nextUrl.searchParams.set('createdAfter', createdAfter.trim());
  } else {
    nextUrl.searchParams.delete('createdAfter');
  }
  if (createdBefore.trim()) {
    nextUrl.searchParams.set('createdBefore', createdBefore.trim());
  } else {
    nextUrl.searchParams.delete('createdBefore');
  }
  window.history.replaceState(null, '', `${nextUrl.pathname}${nextUrl.search}${nextUrl.hash}`);
}
