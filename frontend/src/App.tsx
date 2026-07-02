import { AlertCircle, RefreshCw } from 'lucide-react';
import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react';
import {
  ADMIN_TOKEN_STORAGE_KEY,
  archiveDemoFinalAcceptanceCompletion,
  archiveDemoFinalAcceptanceCompletionCloseout,
  archiveDemoFinalExternalReviewEvidencePackage,
  archiveDemoFinalExternalReviewEvidencePackageDeliveryFinalization,
  archiveDemoFinalExternalReviewDeliveryCertificate,
  archiveDemoFinalExternalReviewReleaseBundle,
  archiveDemoFinalExternalReviewReleaseBundleDeliveryCertificate,
  archiveDemoFinalExternalReviewReleaseBundleDeliveryFinalization,
  approveTaskReview,
  archiveTaskEvidencePackageAcceptanceCertificate,
  archiveTaskEvidencePackageAcceptanceCloseout,
  archiveTaskEvidencePackage,
  archiveDemoLaunchAcceptanceCertificate,
  archiveDemoLaunchAcceptanceCloseout,
  archiveDemoLaunchEvidencePackage,
  archiveDemoFinalAcceptanceSharePackage,
  archiveDemoFinalHandoffReportPackage,
  archiveDemoHandoffPackage,
  archiveDemoReadinessSnapshot,
  archiveDemoSelfHostedLaunchReadiness,
  archiveDemoSession,
  archiveEvaluationRunSnapshot,
  archiveExternalExposureCloseout,
  archiveExternalExposureReadiness,
  archiveExternalExposureOperatorHandoffChecklist,
  cancelTask,
  composeDemoLaunchCommand,
  createDemoFinalAcceptanceCompletionEvidenceDeliveryReceipt,
  createDemoFinalExternalReviewEvidencePackageDeliveryReceipt,
  createDemoFinalExternalReviewReleaseBundleDeliveryReceipt,
  createDemoLiveDemoHandoffDeliveryReceipt,
  createDemoLiveDemoReviewerDeliveryCenterDeliveryReceipt,
  createDemoFinalReviewerHandoffDeliveryReceipt,
  createDemoFinalAcceptanceShareDeliveryReceipt,
  createDemoHandoffShareDeliveryReceipt,
  createDemoLaunchEvidenceShareDeliveryReceipt,
  createTaskEvidencePackageShareDeliveryReceipt,
  createTask,
  createTriggerQuarantine,
  archiveDemoLiveDemoCompletionCertificate,
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
  downloadDemoLaunchEvidencePackageArchiveReport,
  downloadDemoLaunchEvidencePackageReport,
  downloadDemoLaunchAcceptanceCloseoutArchiveReport,
  downloadDemoLaunchAcceptanceCertificateArchiveReport,
  downloadDemoLaunchAcceptanceCertificateReport,
  downloadDemoAcceptanceSummaryReport,
  downloadDemoFinalAcceptanceCompletionArchiveReport,
  downloadDemoFinalAcceptanceCompletionCloseoutArchiveReport,
  downloadDemoFinalAcceptanceCompletionCloseoutReport,
  downloadDemoFinalAcceptanceCompletionEvidenceBundleReport,
  downloadDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationReport,
  downloadDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptReport,
  downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationReport,
  downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport,
  downloadDemoFinalExternalReviewDeliveryCertificateArchiveReport,
  downloadDemoFinalExternalReviewDeliveryCertificateReport,
  downloadDemoFinalExternalReviewReleaseBundleReport,
  downloadDemoFinalExternalReviewReleaseBundleArchiveReport,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveReport,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateReport,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationReport,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryReceiptReport,
  downloadDemoFinalReviewerHandoffDeliveryFinalizationReport,
  downloadDemoFinalReviewerHandoffDeliveryReceiptReport,
  downloadDemoFinalReviewerHandoffPackageReport,
  downloadDemoFinalExternalReviewEvidencePackageDeliveryReceiptReport,
  downloadDemoFinalExternalReviewEvidencePackageArchiveReport,
  downloadDemoFinalExternalReviewEvidencePackageReport,
  downloadDemoFinalAcceptanceShareDeliveryReceiptReport,
  downloadDemoFinalAcceptanceShareFinalizationReport,
  downloadDemoFinalAcceptanceSharePackageArchiveReport,
  downloadDemoFinalAcceptanceSharePackageReport,
  downloadDemoLaunchAcceptanceCloseoutReport,
  downloadDemoLaunchEvidenceFinalizationReport,
  downloadDemoLaunchEvidenceShareDeliveryReceiptReport,
  downloadDemoLaunchEvidenceShareCenterReport,
  downloadDemoSelfHostedLaunchReadinessArchiveReport,
  downloadDemoSelfHostedLaunchReadinessReport,
  downloadExternalExposureCloseoutReport,
  downloadExternalExposureCloseoutArchiveReport,
  downloadExternalExposureOperatorHandoffChecklistArchiveReport,
  downloadExternalExposureOperatorHandoffChecklistReport,
  downloadExternalExposureHandoffPackageReport,
  downloadExternalExposureReadinessArchiveReport,
  downloadExternalExposureSessionReport,
  downloadDemoHandoffShareDeliveryReceiptReport,
  downloadDemoHandoffShareInstructionsReport,
  downloadDemoHandoffShareChecklistReport,
  downloadDemoFinalHandoffReportPackage,
  downloadDemoFinalHandoffReportPackageArchiveReport,
  downloadTaskEvidencePackageReport,
  downloadTaskEvidencePackageAcceptanceCertificateArchiveReport,
  downloadTaskEvidencePackageAcceptanceCertificateReport,
  downloadTaskEvidencePackageAcceptanceCloseoutArchiveReport,
  downloadTaskEvidencePackageFinalizationReport,
  downloadTaskEvidencePackageShareDeliveryReceiptReport,
  downloadTaskEvidencePackageShareCenterReport,
  downloadTaskReport,
  evaluateTrigger,
  evaluateWebhookPayloadDiagnostic,
  getBackendHealth,
  getConfigurationSummary,
  getDashboardBootstrap,
  getDemoEndToEndAcceptanceMatrix,
  getDemoEvidenceBundle,
  getDemoHandoffPackage,
  getDemoHandoffReadiness,
  getDemoHandoffPackageArchiveSummary,
  getDemoHandoffShareCenter,
  getDemoHandoffFinalization,
  getDemoFinalHandoffReportPackage,
  getDemoLaunchAcceptanceCertificate,
  getDemoLaunchAcceptanceCloseout,
  getDemoLaunchEvidenceFinalization,
  getDemoLaunchEvidencePackage,
  getDemoLaunchEvidenceShareCenter,
  getDemoSelfHostedLaunchReadiness,
  getDemoHandoffShareInstructions,
  getDemoHandoffShareChecklist,
  getDemoSessionSnapshot,
  getDemoSessionReport,
  getDemoScript,
  getDemoRunbook,
  getDemoReadiness,
  getDemoAcceptanceSummary,
  getDemoFinalAcceptanceCompletionCloseout,
  getDemoFinalAcceptanceCompletionEvidenceBundle,
  getDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization,
  getDemoFinalExternalReviewEvidencePackageDeliveryFinalization,
  getDemoFinalExternalReviewDeliveryCertificate,
  getDemoFinalExternalReviewReleaseBundle,
  getDemoFinalExternalReviewReleaseBundleDeliveryCertificate,
  getDemoFinalExternalReviewReleaseBundleDeliveryFinalization,
  getDemoFinalReviewerHandoffDeliveryFinalization,
  getDemoFinalExternalReviewEvidencePackage,
  getDemoFinalAcceptanceShareFinalization,
  getDemoFinalAcceptanceSharePackage,
  getDemoLiveDemoArtifactChainReport,
  getDemoLiveDemoReplayPackage,
  getDemoLiveDemoReviewerDeliveryCenter,
  getDemoLiveDemoCompletionCertificate,
  getDemoLiveDemoEvidenceBundle,
  getDemoLiveDemoHandoffDeliveryFinalization,
  getDemoLiveDemoHandoffPackage,
  archiveDemoLiveDemoReviewerDeliveryCenter,
  archiveDemoLiveDemoEvidenceBundle,
  archiveDemoLiveDemoHandoffDeliveryFinalization,
  listDemoLiveDemoReviewerDeliveryCenterArchives,
  listDemoLiveDemoReviewerDeliveryCenterDeliveryReceipts,
  listDemoLiveDemoCompletionCertificateArchives,
  listDemoLiveDemoEvidenceBundleArchives,
  listDemoLiveDemoHandoffDeliveryFinalizationArchives,
  getDemoReadinessSnapshotTrend,
  getDemoSmokeChecklist,
  listDemoFinalHandoffReportPackageArchives,
  listDemoFinalAcceptanceCompletionArchives,
  listDemoFinalAcceptanceCompletionCloseoutArchives,
  listDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts,
  listDemoFinalExternalReviewEvidencePackageArchives,
  listDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives,
  listDemoFinalExternalReviewEvidencePackageDeliveryReceipts,
  listDemoFinalExternalReviewDeliveryCertificateArchives,
  listDemoFinalExternalReviewReleaseBundleArchives,
  listDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives,
  listDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives,
  listDemoFinalExternalReviewReleaseBundleDeliveryReceipts,
  listDemoFinalReviewerHandoffDeliveryReceipts,
  listDemoFinalAcceptanceShareDeliveryReceipts,
  listDemoFinalAcceptanceSharePackageArchives,
  listDemoLaunchEvidenceShareDeliveryReceipts,
  getEvaluationCaseReadiness,
  getEvaluationFixtureBaselineRunRegressionSummary,
  getEvaluationRunArchiveReadinessSummary,
  getEvaluationSummary,
  getEvaluationRunPreview,
  getExternalExposureCloseout,
  getExternalExposureOperatorHandoffChecklist,
  getExternalExposureHandoffPackage,
  getExternalExposureReadiness,
  closeExternalExposureSession,
  archiveDemoLiveTriggerOutcomeCloseout,
  archiveDemoLiveTriggerLaunchPackage,
  runAndArchiveEvaluation,
  runAndArchiveEvaluationFixtureBaseline,
  runEvaluationFixtureBaseline,
  preflightDemoLaunch,
  downloadDemoLiveDemoEvidenceBundleArchiveReport,
  downloadDemoLiveDemoEvidenceBundleReport,
  downloadDemoLiveDemoArtifactChainReport,
  downloadDemoLiveDemoReplayPackage,
  downloadDemoLiveDemoReviewerDeliveryCenter,
  downloadDemoLiveDemoReviewerDeliveryCenterArchiveReport,
  downloadDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptReport,
  downloadDemoLiveDemoCompletionCertificateArchiveReport,
  downloadDemoLiveDemoCompletionCertificateReport,
  downloadDemoLiveDemoHandoffDeliveryFinalizationArchiveReport,
  downloadDemoLiveDemoHandoffDeliveryFinalizationReport,
  downloadDemoLiveDemoHandoffPackageReport,
  downloadDemoLiveDemoHandoffDeliveryReceiptReport,
  downloadDemoLiveTriggerLaunchPackageArchiveReport,
  downloadDemoLiveTriggerOutcomeCloseoutArchiveReport,
  downloadDemoLiveTriggerOutcomeCloseoutReport,
  listDemoLiveTriggerOutcomeCloseoutArchives,
  listDemoLiveDemoHandoffDeliveryReceipts,
  listDemoLiveTriggerLaunchPackageArchives,
  postDemoLiveLaunchGate,
  postDemoLiveTriggerLaunchPackage,
  postDemoLiveTriggerOutcomeCloseout,
  postGitHubTriggerDryRun,
  startExternalExposureSession,
  getGitHubCredentialReadiness,
  getGitHubLivePublishPreflight,
  getGitHubPublishPermissionReadiness,
  getGitHubPublishReadiness,
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
  getTaskEvidencePackageAcceptanceCertificate,
  getTaskEvidencePackageArchiveSummary,
  getTaskEvidencePackageFinalization,
  getTaskEvidencePackageShareCenter,
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
  listExternalExposureReadinessArchives,
  listExternalExposureCloseoutArchives,
  listExternalExposureOperatorHandoffChecklistArchives,
  listExternalExposureSessions,
  listLanguageAdapterFixtures,
  listLanguageAdapterRuntimeReadiness,
  listLanguageAdapters,
  listDemoHandoffPackageArchives,
  listDemoHandoffShareDeliveryReceipts,
  listDemoLaunchAcceptanceCertificateArchives,
  listDemoLaunchAcceptanceCloseoutArchives,
  listDemoLaunchEvidencePackageArchives,
  listDemoSelfHostedLaunchReadinessArchives,
  listDemoSessionArchives,
  listDemoReadinessSnapshots,
  listQueueItems,
  listRejectedTriggers,
  listTriggerQuarantines,
  listWebhookDeliveries,
  listTasks,
  listTaskEvidencePackageAcceptanceCertificateArchives,
  listTaskEvidencePackageArchives,
  listTaskEvidencePackageAcceptanceCloseoutArchives,
  listTaskEvidencePackageShareDeliveryReceipts,
  listRecentTaskEvidencePackageArchives,
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
import { LiveTriggerDryRunPanel } from './dashboard/components/LiveTriggerDryRunPanel';
import { LiveLaunchGatePanel } from './dashboard/components/LiveLaunchGatePanel';
import { DemoLaunchTrackerPanel } from './dashboard/components/DemoLaunchTrackerPanel';
import { DemoLaunchEvidencePackagePanel } from './dashboard/components/DemoLaunchEvidencePackagePanel';
import { DemoAcceptanceSummaryPanel } from './dashboard/components/DemoAcceptanceSummaryPanel';
import { EndToEndAcceptanceMatrixPanel } from './dashboard/components/EndToEndAcceptanceMatrixPanel';
import { ExternalExposureReadinessPanel } from './dashboard/components/ExternalExposureReadinessPanel';
import { ExternalExposureOperatorHandoffChecklistPanel } from './dashboard/components/ExternalExposureOperatorHandoffChecklistPanel';
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
import { TaskEvidenceArchiveReviewPanel } from './dashboard/components/TaskEvidenceArchiveReviewPanel';
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
  DemoAcceptanceSummary,
  DemoEndToEndAcceptanceMatrix,
  ExternalExposureCloseout,
  ExternalExposureCloseoutArchive,
  ExternalExposureHandoffPackage,
  ExternalExposureOperatorHandoffChecklist,
  ExternalExposureOperatorHandoffChecklistArchive,
  ExternalExposureReadiness,
  ExternalExposureReadinessArchive,
  ExternalExposureSession,
  ExternalExposureSessionCloseInput,
  ExternalExposureSessionInput,
  DemoFinalAcceptanceCompletionArchive,
  DemoFinalAcceptanceCompletionCloseoutArchive,
  DemoFinalAcceptanceCompletionCloseout,
  DemoFinalAcceptanceCompletionEvidenceBundle,
  DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput,
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
  DemoFinalExternalReviewEvidencePackage,
  DemoFinalAcceptanceShareDeliveryReceipt,
  DemoFinalAcceptanceShareDeliveryReceiptInput,
  DemoFinalAcceptanceShareFinalization,
  DemoFinalAcceptanceSharePackageArchive,
  DemoFinalAcceptanceSharePackage,
  DemoReadinessSnapshotArchive,
  DemoReadinessSnapshotTrend,
  DemoEvidenceBundle,
  DemoFinalHandoffReportPackageArchive,
  DemoFinalHandoffReportPackage,
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
  DemoLaunchAcceptanceCloseout,
  DemoLaunchAcceptanceCloseoutArchive,
  DemoLaunchCommand,
  DemoLaunchCommandInput,
  DemoLiveLaunchGate,
  DemoLiveDemoArtifactChainReport,
  DemoLiveDemoReplayPackage,
  DemoLiveDemoReviewerDeliveryCenter,
  DemoLiveDemoReviewerDeliveryCenterArchive,
  DemoLiveDemoReviewerDeliveryCenterDeliveryReceipt,
  DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptInput,
  DemoLiveDemoCompletionCertificate,
  DemoLiveDemoCompletionCertificateArchive,
  DemoLiveDemoEvidenceBundle,
  DemoLiveDemoEvidenceBundleArchive,
  DemoLiveDemoHandoffDeliveryFinalization,
  DemoLiveDemoHandoffDeliveryFinalizationArchive,
  DemoLiveDemoHandoffDeliveryReceipt,
  DemoLiveDemoHandoffDeliveryReceiptInput,
  DemoLiveDemoHandoffPackage,
  DemoLiveTriggerLaunchPackage,
  DemoLiveTriggerLaunchPackageArchive,
  DemoLiveTriggerOutcomeCloseout,
  DemoLiveTriggerOutcomeCloseoutArchive,
  DemoLiveTriggerOutcomeCloseoutInput,
  DemoLaunchEvidencePackageArchive,
  DemoLaunchEvidencePackage,
  DemoLaunchEvidenceFinalization,
  DemoLaunchEvidenceShareCenter,
  DemoLaunchEvidenceShareDeliveryReceipt,
  DemoLaunchEvidenceShareDeliveryReceiptInput,
  DemoLaunchPreflight,
  DemoLaunchPreflightInput,
  GitHubTriggerDryRun,
  GitHubTriggerDryRunInput,
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
  FixTaskEvidencePackageAcceptanceCertificate,
  FixTaskEvidencePackageAcceptanceCertificateArchive,
  FixTaskEvidencePackageAcceptanceCloseoutArchive,
  FixTaskEvidencePackageArchive,
  FixTaskEvidencePackageFinalization,
  FixTaskEvidencePackageArchiveShareCenter,
  FixTaskEvidencePackageArchiveSummary,
  FixTaskEvidencePackageShareDeliveryReceipt,
  FixTaskEvidencePackageShareDeliveryReceiptInput,
  FixTaskFailureCauseSummary,
  FixTaskLatencySummary,
  FixTaskMetricsSummary,
  FixTaskStatusCounts,
  FixTaskModelUsageSummary,
  FixTaskQueueItem,
  FixTaskQueueSummary,
  FixTaskWorkerHealth,
  GitHubCredentialReadiness,
  GitHubLivePublishPreflight,
  GitHubPublishPermissionReadiness,
  GitHubPublishReadiness,
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
  const [githubLivePublishPreflight, setGitHubLivePublishPreflight] = useState<GitHubLivePublishPreflight | null>(null);
  const [githubPublishPermissionReadiness, setGitHubPublishPermissionReadiness] = useState<GitHubPublishPermissionReadiness | null>(null);
  const [githubPublishReadiness, setGitHubPublishReadiness] = useState<GitHubPublishReadiness | null>(null);
  const [githubRepositoryAccessReadiness, setGitHubRepositoryAccessReadiness] = useState<GitHubRepositoryAccessReadiness | null>(null);
  const [githubWebhookUrlReadiness, setGitHubWebhookUrlReadiness] = useState<GitHubWebhookUrlReadiness | null>(null);
  const [githubWebhookSetupReadiness, setGitHubWebhookSetupReadiness] = useState<GitHubWebhookSetupReadiness | null>(null);
  const [modelProviderHealth, setModelProviderHealth] = useState<ModelProviderHealth | null>(null);
  const [backendHealth, setBackendHealth] = useState<BackendHealth | null>(null);
  const [demoReadiness, setDemoReadiness] = useState<DemoReadiness | null>(null);
  const [demoReadinessError, setDemoReadinessError] = useState<string | null>(null);
  const [demoAcceptanceSummary, setDemoAcceptanceSummary] = useState<DemoAcceptanceSummary | null>(null);
  const [demoAcceptanceSummaryError, setDemoAcceptanceSummaryError] = useState<string | null>(null);
  const [demoEndToEndAcceptanceMatrix, setDemoEndToEndAcceptanceMatrix] =
    useState<DemoEndToEndAcceptanceMatrix | null>(null);
  const [demoEndToEndAcceptanceMatrixError, setDemoEndToEndAcceptanceMatrixError] = useState<string | null>(null);
  const [externalExposureReadiness, setExternalExposureReadiness] = useState<ExternalExposureReadiness | null>(null);
  const [externalExposureReadinessError, setExternalExposureReadinessError] = useState<string | null>(null);
  const [externalExposureReadinessArchives, setExternalExposureReadinessArchives] =
    useState<ExternalExposureReadinessArchive[]>([]);
  const [externalExposureReadinessArchiveError, setExternalExposureReadinessArchiveError] =
    useState<string | null>(null);
  const [externalExposureHandoffPackage, setExternalExposureHandoffPackage] =
    useState<ExternalExposureHandoffPackage | null>(null);
  const [externalExposureHandoffPackageError, setExternalExposureHandoffPackageError] =
    useState<string | null>(null);
  const [externalExposureSessions, setExternalExposureSessions] = useState<ExternalExposureSession[]>([]);
  const [externalExposureSessionError, setExternalExposureSessionError] = useState<string | null>(null);
  const [externalExposureCloseout, setExternalExposureCloseout] = useState<ExternalExposureCloseout | null>(null);
  const [externalExposureCloseoutError, setExternalExposureCloseoutError] = useState<string | null>(null);
  const [externalExposureCloseoutArchives, setExternalExposureCloseoutArchives] =
    useState<ExternalExposureCloseoutArchive[]>([]);
  const [externalExposureCloseoutArchiveError, setExternalExposureCloseoutArchiveError] =
    useState<string | null>(null);
  const [externalExposureOperatorHandoffChecklist, setExternalExposureOperatorHandoffChecklist] =
    useState<ExternalExposureOperatorHandoffChecklist | null>(null);
  const [externalExposureOperatorHandoffChecklistError, setExternalExposureOperatorHandoffChecklistError] =
    useState<string | null>(null);
  const [externalExposureOperatorHandoffChecklistArchives, setExternalExposureOperatorHandoffChecklistArchives] =
    useState<ExternalExposureOperatorHandoffChecklistArchive[]>([]);
  const [
    externalExposureOperatorHandoffChecklistArchiveError,
    setExternalExposureOperatorHandoffChecklistArchiveError
  ] = useState<string | null>(null);
  const [demoFinalAcceptanceSharePackage, setDemoFinalAcceptanceSharePackage] =
    useState<DemoFinalAcceptanceSharePackage | null>(null);
  const [demoFinalAcceptanceSharePackageError, setDemoFinalAcceptanceSharePackageError] = useState<string | null>(null);
  const [demoFinalAcceptanceSharePackageArchives, setDemoFinalAcceptanceSharePackageArchives] =
    useState<DemoFinalAcceptanceSharePackageArchive[]>([]);
  const [demoFinalAcceptanceSharePackageArchiveError, setDemoFinalAcceptanceSharePackageArchiveError] =
    useState<string | null>(null);
  const [demoFinalAcceptanceShareDeliveryReceipts, setDemoFinalAcceptanceShareDeliveryReceipts] =
    useState<DemoFinalAcceptanceShareDeliveryReceipt[]>([]);
  const [demoFinalAcceptanceShareDeliveryReceiptError, setDemoFinalAcceptanceShareDeliveryReceiptError] =
    useState<string | null>(null);
  const [demoFinalAcceptanceShareFinalization, setDemoFinalAcceptanceShareFinalization] =
    useState<DemoFinalAcceptanceShareFinalization | null>(null);
  const [demoFinalAcceptanceShareFinalizationError, setDemoFinalAcceptanceShareFinalizationError] =
    useState<string | null>(null);
  const [demoFinalAcceptanceCompletionEvidenceBundle, setDemoFinalAcceptanceCompletionEvidenceBundle] =
    useState<DemoFinalAcceptanceCompletionEvidenceBundle | null>(null);
  const [demoFinalAcceptanceCompletionEvidenceBundleError, setDemoFinalAcceptanceCompletionEvidenceBundleError] =
    useState<string | null>(null);
  const [demoFinalAcceptanceCompletionArchives, setDemoFinalAcceptanceCompletionArchives] =
    useState<DemoFinalAcceptanceCompletionArchive[]>([]);
  const [demoFinalAcceptanceCompletionArchiveError, setDemoFinalAcceptanceCompletionArchiveError] =
    useState<string | null>(null);
  const [
    demoFinalAcceptanceCompletionEvidenceDeliveryReceipts,
    setDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts
  ] = useState<DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt[]>([]);
  const [
    demoFinalAcceptanceCompletionEvidenceDeliveryReceiptError,
    setDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptError
  ] = useState<string | null>(null);
  const [
    demoFinalAcceptanceCompletionEvidenceDeliveryFinalization,
    setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization
  ] = useState<DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization | null>(null);
  const [
    demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationError,
    setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationError
  ] = useState<string | null>(null);
  const [demoFinalAcceptanceCompletionCloseout, setDemoFinalAcceptanceCompletionCloseout] =
    useState<DemoFinalAcceptanceCompletionCloseout | null>(null);
  const [demoFinalAcceptanceCompletionCloseoutError, setDemoFinalAcceptanceCompletionCloseoutError] =
    useState<string | null>(null);
  const [demoFinalAcceptanceCompletionCloseoutArchives, setDemoFinalAcceptanceCompletionCloseoutArchives] =
    useState<DemoFinalAcceptanceCompletionCloseoutArchive[]>([]);
  const [demoFinalAcceptanceCompletionCloseoutArchiveError, setDemoFinalAcceptanceCompletionCloseoutArchiveError] =
    useState<string | null>(null);
  const [demoFinalExternalReviewEvidencePackage, setDemoFinalExternalReviewEvidencePackage] =
    useState<DemoFinalExternalReviewEvidencePackage | null>(null);
  const [demoFinalExternalReviewEvidencePackageError, setDemoFinalExternalReviewEvidencePackageError] =
    useState<string | null>(null);
  const [demoFinalExternalReviewEvidencePackageArchives, setDemoFinalExternalReviewEvidencePackageArchives] =
    useState<DemoFinalExternalReviewEvidencePackageArchive[]>([]);
  const [
    demoFinalExternalReviewEvidencePackageArchiveError,
    setDemoFinalExternalReviewEvidencePackageArchiveError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewEvidencePackageDeliveryReceipts,
    setDemoFinalExternalReviewEvidencePackageDeliveryReceipts
  ] = useState<DemoFinalExternalReviewEvidencePackageDeliveryReceipt[]>([]);
  const [
    demoFinalExternalReviewEvidencePackageDeliveryReceiptError,
    setDemoFinalExternalReviewEvidencePackageDeliveryReceiptError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewEvidencePackageDeliveryFinalization,
    setDemoFinalExternalReviewEvidencePackageDeliveryFinalization
  ] = useState<DemoFinalExternalReviewEvidencePackageDeliveryFinalization | null>(null);
  const [
    demoFinalExternalReviewEvidencePackageDeliveryFinalizationError,
    setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives,
    setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives
  ] = useState<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchive[]>([]);
  const [
    demoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveError,
    setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewDeliveryCertificate,
    setDemoFinalExternalReviewDeliveryCertificate
  ] = useState<DemoFinalExternalReviewDeliveryCertificate | null>(null);
  const [
    demoFinalExternalReviewDeliveryCertificateArchives,
    setDemoFinalExternalReviewDeliveryCertificateArchives
  ] = useState<DemoFinalExternalReviewDeliveryCertificateArchive[]>([]);
  const [
    demoFinalExternalReviewDeliveryCertificateError,
    setDemoFinalExternalReviewDeliveryCertificateError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewDeliveryCertificateArchiveError,
    setDemoFinalExternalReviewDeliveryCertificateArchiveError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewReleaseBundle,
    setDemoFinalExternalReviewReleaseBundle
  ] = useState<DemoFinalExternalReviewReleaseBundle | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleArchives,
    setDemoFinalExternalReviewReleaseBundleArchives
  ] = useState<DemoFinalExternalReviewReleaseBundleArchive[]>([]);
  const [
    demoFinalExternalReviewReleaseBundleError,
    setDemoFinalExternalReviewReleaseBundleError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleArchiveError,
    setDemoFinalExternalReviewReleaseBundleArchiveError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryReceipts,
    setDemoFinalExternalReviewReleaseBundleDeliveryReceipts
  ] = useState<DemoFinalExternalReviewReleaseBundleDeliveryReceipt[]>([]);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryReceiptError,
    setDemoFinalExternalReviewReleaseBundleDeliveryReceiptError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryFinalization,
    setDemoFinalExternalReviewReleaseBundleDeliveryFinalization
  ] = useState<DemoFinalExternalReviewReleaseBundleDeliveryFinalization | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryFinalizationError,
    setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives,
    setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives
  ] = useState<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchive[]>([]);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveError,
    setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryCertificate,
    setDemoFinalExternalReviewReleaseBundleDeliveryCertificate
  ] = useState<DemoFinalExternalReviewReleaseBundleDeliveryCertificate | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryCertificateArchives,
    setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives
  ] = useState<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchive[]>([]);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryCertificateError,
    setDemoFinalExternalReviewReleaseBundleDeliveryCertificateError
  ] = useState<string | null>(null);
  const [
    demoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveError,
    setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveError
  ] = useState<string | null>(null);
  const [
    demoFinalReviewerHandoffDeliveryReceipts,
    setDemoFinalReviewerHandoffDeliveryReceipts
  ] = useState<DemoFinalReviewerHandoffDeliveryReceipt[]>([]);
  const [
    demoFinalReviewerHandoffDeliveryReceiptError,
    setDemoFinalReviewerHandoffDeliveryReceiptError
  ] = useState<string | null>(null);
  const [
    demoFinalReviewerHandoffDeliveryFinalization,
    setDemoFinalReviewerHandoffDeliveryFinalization
  ] = useState<DemoFinalReviewerHandoffDeliveryFinalization | null>(null);
  const [
    demoFinalReviewerHandoffDeliveryFinalizationError,
    setDemoFinalReviewerHandoffDeliveryFinalizationError
  ] = useState<string | null>(null);
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
  const [demoFinalHandoffReportPackage, setDemoFinalHandoffReportPackage] =
    useState<DemoFinalHandoffReportPackage | null>(null);
  const [demoFinalHandoffReportPackageError, setDemoFinalHandoffReportPackageError] = useState<string | null>(null);
  const [demoFinalHandoffReportPackageArchives, setDemoFinalHandoffReportPackageArchives] =
    useState<DemoFinalHandoffReportPackageArchive[]>([]);
  const [demoFinalHandoffReportPackageArchiveError, setDemoFinalHandoffReportPackageArchiveError] =
    useState<string | null>(null);
  const [demoSelfHostedLaunchReadiness, setDemoSelfHostedLaunchReadiness] =
    useState<DemoSelfHostedLaunchReadiness | null>(null);
  const [demoSelfHostedLaunchReadinessError, setDemoSelfHostedLaunchReadinessError] = useState<string | null>(null);
  const [demoSelfHostedLaunchReadinessArchives, setDemoSelfHostedLaunchReadinessArchives] =
    useState<DemoSelfHostedLaunchReadinessArchive[]>([]);
  const [demoSelfHostedLaunchReadinessArchiveError, setDemoSelfHostedLaunchReadinessArchiveError] =
    useState<string | null>(null);
  const [demoLaunchEvidencePackage, setDemoLaunchEvidencePackage] = useState<DemoLaunchEvidencePackage | null>(null);
  const [demoLaunchEvidencePackageError, setDemoLaunchEvidencePackageError] = useState<string | null>(null);
  const [demoLaunchEvidencePackageArchives, setDemoLaunchEvidencePackageArchives] =
    useState<DemoLaunchEvidencePackageArchive[]>([]);
  const [demoLaunchEvidencePackageArchiveError, setDemoLaunchEvidencePackageArchiveError] =
    useState<string | null>(null);
  const [demoLaunchEvidenceShareCenter, setDemoLaunchEvidenceShareCenter] =
    useState<DemoLaunchEvidenceShareCenter | null>(null);
  const [demoLaunchEvidenceShareCenterError, setDemoLaunchEvidenceShareCenterError] = useState<string | null>(null);
  const [demoLaunchEvidenceFinalization, setDemoLaunchEvidenceFinalization] =
    useState<DemoLaunchEvidenceFinalization | null>(null);
  const [demoLaunchEvidenceFinalizationError, setDemoLaunchEvidenceFinalizationError] = useState<string | null>(null);
  const [demoLaunchAcceptanceCloseout, setDemoLaunchAcceptanceCloseout] =
    useState<DemoLaunchAcceptanceCloseout | null>(null);
  const [demoLaunchAcceptanceCloseoutError, setDemoLaunchAcceptanceCloseoutError] = useState<string | null>(null);
  const [demoLaunchAcceptanceCloseoutArchives, setDemoLaunchAcceptanceCloseoutArchives] =
    useState<DemoLaunchAcceptanceCloseoutArchive[]>([]);
  const [demoLaunchAcceptanceCloseoutArchiveError, setDemoLaunchAcceptanceCloseoutArchiveError] =
    useState<string | null>(null);
  const [demoLaunchAcceptanceCertificate, setDemoLaunchAcceptanceCertificate] =
    useState<DemoLaunchAcceptanceCertificate | null>(null);
  const [demoLaunchAcceptanceCertificateError, setDemoLaunchAcceptanceCertificateError] =
    useState<string | null>(null);
  const [demoLaunchAcceptanceCertificateArchives, setDemoLaunchAcceptanceCertificateArchives] =
    useState<DemoLaunchAcceptanceCertificateArchive[]>([]);
  const [demoLaunchAcceptanceCertificateArchiveError, setDemoLaunchAcceptanceCertificateArchiveError] =
    useState<string | null>(null);
  const [demoLaunchEvidenceDeliveryReceipts, setDemoLaunchEvidenceDeliveryReceipts] =
    useState<DemoLaunchEvidenceShareDeliveryReceipt[]>([]);
  const [demoLaunchEvidenceDeliveryReceiptError, setDemoLaunchEvidenceDeliveryReceiptError] =
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
  const [gitHubTriggerDryRun, setGitHubTriggerDryRun] = useState<GitHubTriggerDryRun | null>(null);
  const [gitHubTriggerDryRunError, setGitHubTriggerDryRunError] = useState<string | null>(null);
  const [gitHubTriggerDryRunPending, setGitHubTriggerDryRunPending] = useState(false);
  const [demoLiveLaunchGate, setDemoLiveLaunchGate] = useState<DemoLiveLaunchGate | null>(null);
  const [demoLiveLaunchGateError, setDemoLiveLaunchGateError] = useState<string | null>(null);
  const [demoLiveLaunchGatePending, setDemoLiveLaunchGatePending] = useState(false);
  const [demoLiveTriggerLaunchPackage, setDemoLiveTriggerLaunchPackage] =
    useState<DemoLiveTriggerLaunchPackage | null>(null);
  const [demoLiveTriggerLaunchPackageError, setDemoLiveTriggerLaunchPackageError] = useState<string | null>(null);
  const [demoLiveTriggerLaunchPackagePending, setDemoLiveTriggerLaunchPackagePending] = useState(false);
  const [demoLiveTriggerLaunchPackageArchives, setDemoLiveTriggerLaunchPackageArchives] =
    useState<DemoLiveTriggerLaunchPackageArchive[]>([]);
  const [demoLiveTriggerLaunchPackageArchiveError, setDemoLiveTriggerLaunchPackageArchiveError] =
    useState<string | null>(null);
  const [demoLiveTriggerOutcomeCloseout, setDemoLiveTriggerOutcomeCloseout] =
    useState<DemoLiveTriggerOutcomeCloseout | null>(null);
  const [demoLiveTriggerOutcomeCloseoutError, setDemoLiveTriggerOutcomeCloseoutError] = useState<string | null>(null);
  const [demoLiveTriggerOutcomeCloseoutPending, setDemoLiveTriggerOutcomeCloseoutPending] = useState(false);
  const [demoLiveTriggerOutcomeCloseoutArchives, setDemoLiveTriggerOutcomeCloseoutArchives] =
    useState<DemoLiveTriggerOutcomeCloseoutArchive[]>([]);
  const [demoLiveTriggerOutcomeCloseoutArchiveError, setDemoLiveTriggerOutcomeCloseoutArchiveError] =
    useState<string | null>(null);
  const [demoLiveDemoEvidenceBundle, setDemoLiveDemoEvidenceBundle] =
    useState<DemoLiveDemoEvidenceBundle | null>(null);
  const [demoLiveDemoEvidenceBundleError, setDemoLiveDemoEvidenceBundleError] =
    useState<string | null>(null);
  const [demoLiveDemoEvidenceBundleArchives, setDemoLiveDemoEvidenceBundleArchives] =
    useState<DemoLiveDemoEvidenceBundleArchive[]>([]);
  const [demoLiveDemoEvidenceBundleArchiveError, setDemoLiveDemoEvidenceBundleArchiveError] =
    useState<string | null>(null);
  const [demoLiveDemoHandoffPackage, setDemoLiveDemoHandoffPackage] =
    useState<DemoLiveDemoHandoffPackage | null>(null);
  const [demoLiveDemoHandoffPackageError, setDemoLiveDemoHandoffPackageError] =
    useState<string | null>(null);
  const [demoLiveDemoHandoffDeliveryReceipts, setDemoLiveDemoHandoffDeliveryReceipts] =
    useState<DemoLiveDemoHandoffDeliveryReceipt[]>([]);
  const [demoLiveDemoHandoffDeliveryReceiptError, setDemoLiveDemoHandoffDeliveryReceiptError] =
    useState<string | null>(null);
  const [demoLiveDemoHandoffDeliveryFinalization, setDemoLiveDemoHandoffDeliveryFinalization] =
    useState<DemoLiveDemoHandoffDeliveryFinalization | null>(null);
  const [demoLiveDemoHandoffDeliveryFinalizationError, setDemoLiveDemoHandoffDeliveryFinalizationError] =
    useState<string | null>(null);
  const [demoLiveDemoHandoffDeliveryFinalizationArchives, setDemoLiveDemoHandoffDeliveryFinalizationArchives] =
    useState<DemoLiveDemoHandoffDeliveryFinalizationArchive[]>([]);
  const [demoLiveDemoHandoffDeliveryFinalizationArchiveError, setDemoLiveDemoHandoffDeliveryFinalizationArchiveError] =
    useState<string | null>(null);
  const [demoLiveDemoCompletionCertificate, setDemoLiveDemoCompletionCertificate] =
    useState<DemoLiveDemoCompletionCertificate | null>(null);
  const [demoLiveDemoCompletionCertificateError, setDemoLiveDemoCompletionCertificateError] =
    useState<string | null>(null);
  const [demoLiveDemoCompletionCertificateArchives, setDemoLiveDemoCompletionCertificateArchives] =
    useState<DemoLiveDemoCompletionCertificateArchive[]>([]);
  const [demoLiveDemoCompletionCertificateArchiveError, setDemoLiveDemoCompletionCertificateArchiveError] =
    useState<string | null>(null);
  const [demoLiveDemoArtifactChainReport, setDemoLiveDemoArtifactChainReport] =
    useState<DemoLiveDemoArtifactChainReport | null>(null);
  const [demoLiveDemoArtifactChainReportError, setDemoLiveDemoArtifactChainReportError] =
    useState<string | null>(null);
  const [demoLiveDemoReplayPackage, setDemoLiveDemoReplayPackage] =
    useState<DemoLiveDemoReplayPackage | null>(null);
  const [demoLiveDemoReplayPackageError, setDemoLiveDemoReplayPackageError] =
    useState<string | null>(null);
  const [demoLiveDemoReviewerDeliveryCenter, setDemoLiveDemoReviewerDeliveryCenter] =
    useState<DemoLiveDemoReviewerDeliveryCenter | null>(null);
  const [demoLiveDemoReviewerDeliveryCenterError, setDemoLiveDemoReviewerDeliveryCenterError] =
    useState<string | null>(null);
  const [demoLiveDemoReviewerDeliveryCenterArchives, setDemoLiveDemoReviewerDeliveryCenterArchives] =
    useState<DemoLiveDemoReviewerDeliveryCenterArchive[]>([]);
  const [demoLiveDemoReviewerDeliveryCenterArchiveError, setDemoLiveDemoReviewerDeliveryCenterArchiveError] =
    useState<string | null>(null);
  const [
    demoLiveDemoReviewerDeliveryCenterDeliveryReceipts,
    setDemoLiveDemoReviewerDeliveryCenterDeliveryReceipts
  ] = useState<DemoLiveDemoReviewerDeliveryCenterDeliveryReceipt[]>([]);
  const [
    demoLiveDemoReviewerDeliveryCenterDeliveryReceiptError,
    setDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptError
  ] = useState<string | null>(null);
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
  const [taskEvidenceArchives, setTaskEvidenceArchives] = useState<FixTaskEvidencePackageArchive[]>([]);
  const [taskEvidenceArchiveSummary, setTaskEvidenceArchiveSummary] =
    useState<FixTaskEvidencePackageArchiveSummary | null>(null);
  const [taskEvidenceShareCenter, setTaskEvidenceShareCenter] =
    useState<FixTaskEvidencePackageArchiveShareCenter | null>(null);
  const [taskEvidenceFinalization, setTaskEvidenceFinalization] =
    useState<FixTaskEvidencePackageFinalization | null>(null);
  const [taskEvidenceDeliveryReceipts, setTaskEvidenceDeliveryReceipts] =
    useState<FixTaskEvidencePackageShareDeliveryReceipt[]>([]);
  const [taskEvidenceCloseoutArchives, setTaskEvidenceCloseoutArchives] =
    useState<FixTaskEvidencePackageAcceptanceCloseoutArchive[]>([]);
  const [taskEvidenceAcceptanceCertificate, setTaskEvidenceAcceptanceCertificate] =
    useState<FixTaskEvidencePackageAcceptanceCertificate | null>(null);
  const [taskEvidenceAcceptanceCertificateArchives, setTaskEvidenceAcceptanceCertificateArchives] =
    useState<FixTaskEvidencePackageAcceptanceCertificateArchive[]>([]);
  const [taskEvidenceArchiveError, setTaskEvidenceArchiveError] = useState<string | null>(null);
  const [taskEvidenceShareCenterError, setTaskEvidenceShareCenterError] = useState<string | null>(null);
  const [taskEvidenceFinalizationError, setTaskEvidenceFinalizationError] = useState<string | null>(null);
  const [taskEvidenceDeliveryReceiptError, setTaskEvidenceDeliveryReceiptError] = useState<string | null>(null);
  const [taskEvidenceCloseoutArchiveError, setTaskEvidenceCloseoutArchiveError] = useState<string | null>(null);
  const [taskEvidenceAcceptanceCertificateError, setTaskEvidenceAcceptanceCertificateError] =
    useState<string | null>(null);
  const [taskEvidenceAcceptanceCertificateArchiveError, setTaskEvidenceAcceptanceCertificateArchiveError] =
    useState<string | null>(null);
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
        demoFinalHandoffReportPackageResult,
        demoFinalHandoffReportPackageArchiveResult,
        demoSelfHostedLaunchReadinessResult,
        demoSelfHostedLaunchReadinessArchiveResult,
        demoLaunchEvidencePackageResult,
        demoLaunchEvidencePackageArchiveResult,
        demoLaunchEvidenceShareCenterResult,
        demoLaunchEvidenceFinalizationResult,
        demoLaunchAcceptanceCloseoutResult,
        demoLaunchAcceptanceCloseoutArchiveResult,
        demoLaunchAcceptanceCertificateResult,
        demoLaunchAcceptanceCertificateArchiveResult,
        demoLaunchEvidenceDeliveryReceiptResult,
        demoHandoffShareInstructionsResult,
        demoHandoffShareDeliveryReceiptResult,
        demoScriptResult,
        demoReadinessResult,
        demoEndToEndAcceptanceMatrixResult,
        externalExposureReadinessResult,
        externalExposureReadinessArchiveResult,
        externalExposureHandoffPackageResult,
        externalExposureSessionResult,
        externalExposureCloseoutResult,
        externalExposureCloseoutArchiveResult,
        externalExposureOperatorHandoffChecklistResult,
        externalExposureOperatorHandoffChecklistArchiveResult,
        demoLiveTriggerLaunchPackageArchiveResult,
        demoLiveTriggerOutcomeCloseoutArchiveResult,
        demoLiveDemoEvidenceBundleResult,
        demoLiveDemoEvidenceBundleArchiveResult,
        demoLiveDemoHandoffPackageResult,
        demoLiveDemoHandoffDeliveryReceiptResult,
        demoLiveDemoHandoffDeliveryFinalizationResult,
        demoLiveDemoHandoffDeliveryFinalizationArchiveResult,
        demoLiveDemoCompletionCertificateResult,
        demoLiveDemoCompletionCertificateArchiveResult,
        demoLiveDemoArtifactChainReportResult,
        demoLiveDemoReplayPackageResult,
        demoLiveDemoReviewerDeliveryCenterResult,
        demoLiveDemoReviewerDeliveryCenterArchiveResult,
        demoLiveDemoReviewerDeliveryCenterDeliveryReceiptResult,
        demoAcceptanceSummaryResult,
        demoFinalAcceptanceSharePackageResult,
        demoFinalAcceptanceSharePackageArchiveResult,
        demoFinalAcceptanceShareDeliveryReceiptResult,
        demoFinalAcceptanceShareFinalizationResult,
        demoFinalAcceptanceCompletionEvidenceBundleResult,
        demoFinalAcceptanceCompletionArchiveResult,
        demoFinalAcceptanceCompletionEvidenceDeliveryReceiptResult,
        demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationResult,
        demoFinalAcceptanceCompletionCloseoutResult,
        demoFinalAcceptanceCompletionCloseoutArchiveResult,
        demoFinalExternalReviewEvidencePackageResult,
        demoFinalExternalReviewEvidencePackageArchiveResult,
        demoFinalExternalReviewEvidencePackageDeliveryReceiptResult,
        demoFinalExternalReviewEvidencePackageDeliveryFinalizationResult,
        demoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveResult,
        demoFinalExternalReviewDeliveryCertificateResult,
        demoFinalExternalReviewDeliveryCertificateArchiveResult,
        demoFinalExternalReviewReleaseBundleResult,
        demoFinalExternalReviewReleaseBundleArchiveResult,
        demoFinalExternalReviewReleaseBundleDeliveryReceiptResult,
        demoFinalExternalReviewReleaseBundleDeliveryFinalizationResult,
        demoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveResult,
        demoFinalExternalReviewReleaseBundleDeliveryCertificateResult,
        demoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveResult,
        demoFinalReviewerHandoffDeliveryReceiptResult,
        demoFinalReviewerHandoffDeliveryFinalizationResult,
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
        taskEvidenceArchiveResult,
        taskEvidenceArchiveSummaryResult,
        taskEvidenceShareCenterResult,
        taskEvidenceFinalizationResult,
        taskEvidenceDeliveryReceiptResult,
        taskEvidenceCloseoutArchiveResult,
        taskEvidenceAcceptanceCertificateResult,
        taskEvidenceAcceptanceCertificateArchiveResult,
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
        getDemoFinalHandoffReportPackage().then(
          (reportPackage) => ({ reportPackage, error: null as string | null }),
          (caught) => ({ reportPackage: null, error: errorMessage(caught) })
        ),
        listDemoFinalHandoffReportPackageArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoSelfHostedLaunchReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        listDemoSelfHostedLaunchReadinessArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoLaunchEvidencePackage().then(
          (evidencePackage) => ({ evidencePackage, error: null as string | null }),
          (caught) => ({ evidencePackage: null, error: errorMessage(caught) })
        ),
        listDemoLaunchEvidencePackageArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoLaunchEvidenceShareCenter().then(
          (center) => ({ center, error: null as string | null }),
          (caught) => ({ center: null, error: errorMessage(caught) })
        ),
        getDemoLaunchEvidenceFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
        ),
        getDemoLaunchAcceptanceCloseout().then(
          (closeout) => ({ closeout, error: null as string | null }),
          (caught) => ({ closeout: null, error: errorMessage(caught) })
        ),
        listDemoLaunchAcceptanceCloseoutArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoLaunchAcceptanceCertificate().then(
          (certificate) => ({ certificate, error: null as string | null }),
          (caught) => ({ certificate: null, error: errorMessage(caught) })
        ),
        listDemoLaunchAcceptanceCertificateArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoLaunchEvidenceShareDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
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
        getDemoEndToEndAcceptanceMatrix().then(
          (matrix) => ({ matrix, error: null as string | null }),
          (caught) => ({ matrix: null, error: errorMessage(caught) })
        ),
        getExternalExposureReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        ),
        listExternalExposureReadinessArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getExternalExposureHandoffPackage().then(
          (handoffPackage) => ({ handoffPackage, error: null as string | null }),
          (caught) => ({ handoffPackage: null, error: errorMessage(caught) })
        ),
        listExternalExposureSessions().then(
          (sessions) => ({ sessions, error: null as string | null }),
          (caught) => ({ sessions: null, error: errorMessage(caught) })
        ),
        getExternalExposureCloseout().then(
          (closeout) => ({ closeout, error: null as string | null }),
          (caught) => ({ closeout: null, error: errorMessage(caught) })
        ),
        listExternalExposureCloseoutArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getExternalExposureOperatorHandoffChecklist().then(
          (checklist) => ({ checklist, error: null as string | null }),
          (caught) => ({ checklist: null, error: errorMessage(caught) })
        ),
        listExternalExposureOperatorHandoffChecklistArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoLiveTriggerLaunchPackageArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoLiveTriggerOutcomeCloseoutArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoLiveDemoEvidenceBundle().then(
          (bundle) => ({ bundle, error: null as string | null }),
          (caught) => ({ bundle: null, error: errorMessage(caught) })
        ),
        listDemoLiveDemoEvidenceBundleArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoLiveDemoHandoffPackage().then(
          (handoffPackage) => ({ handoffPackage, error: null as string | null }),
          (caught) => ({ handoffPackage: null, error: errorMessage(caught) })
        ),
        listDemoLiveDemoHandoffDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        getDemoLiveDemoHandoffDeliveryFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
        ),
        listDemoLiveDemoHandoffDeliveryFinalizationArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoLiveDemoCompletionCertificate().then(
          (certificate) => ({ certificate, error: null as string | null }),
          (caught) => ({ certificate: null, error: errorMessage(caught) })
        ),
        listDemoLiveDemoCompletionCertificateArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoLiveDemoArtifactChainReport().then(
          (report) => ({ report, error: null as string | null }),
          (caught) => ({ report: null, error: errorMessage(caught) })
        ),
        getDemoLiveDemoReplayPackage().then(
          (replayPackage) => ({ replayPackage, error: null as string | null }),
          (caught) => ({ replayPackage: null, error: errorMessage(caught) })
        ),
        getDemoLiveDemoReviewerDeliveryCenter().then(
          (deliveryCenter) => ({ deliveryCenter, error: null as string | null }),
          (caught) => ({ deliveryCenter: null, error: errorMessage(caught) })
        ),
        listDemoLiveDemoReviewerDeliveryCenterArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoLiveDemoReviewerDeliveryCenterDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        getDemoAcceptanceSummary().then(
          (summary) => ({ summary, error: null as string | null }),
          (caught) => ({ summary: null, error: errorMessage(caught) })
        ),
        getDemoFinalAcceptanceSharePackage().then(
          (sharePackage) => ({ sharePackage, error: null as string | null }),
          (caught) => ({ sharePackage: null, error: errorMessage(caught) })
        ),
        listDemoFinalAcceptanceSharePackageArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoFinalAcceptanceShareDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        getDemoFinalAcceptanceShareFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
        ),
        getDemoFinalAcceptanceCompletionEvidenceBundle().then(
          (bundle) => ({ bundle, error: null as string | null }),
          (caught) => ({ bundle: null, error: errorMessage(caught) })
        ),
        listDemoFinalAcceptanceCompletionArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        getDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
        ),
        getDemoFinalAcceptanceCompletionCloseout().then(
          (closeout) => ({ closeout, error: null as string | null }),
          (caught) => ({ closeout: null, error: errorMessage(caught) })
        ),
        listDemoFinalAcceptanceCompletionCloseoutArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoFinalExternalReviewEvidencePackage().then(
          (evidencePackage) => ({ evidencePackage, error: null as string | null }),
          (caught) => ({ evidencePackage: null, error: errorMessage(caught) })
        ),
        listDemoFinalExternalReviewEvidencePackageArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoFinalExternalReviewEvidencePackageDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        getDemoFinalExternalReviewEvidencePackageDeliveryFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
        ),
        listDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoFinalExternalReviewDeliveryCertificate().then(
          (certificate) => ({ certificate, error: null as string | null }),
          (caught) => ({ certificate: null, error: errorMessage(caught) })
        ),
        listDemoFinalExternalReviewDeliveryCertificateArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoFinalExternalReviewReleaseBundle().then(
          (bundle) => ({ bundle, error: null as string | null }),
          (caught) => ({ bundle: null, error: errorMessage(caught) })
        ),
        listDemoFinalExternalReviewReleaseBundleArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoFinalExternalReviewReleaseBundleDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        getDemoFinalExternalReviewReleaseBundleDeliveryFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
        ),
        listDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getDemoFinalExternalReviewReleaseBundleDeliveryCertificate().then(
          (certificate) => ({ certificate, error: null as string | null }),
          (caught) => ({ certificate: null, error: errorMessage(caught) })
        ),
        listDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        listDemoFinalReviewerHandoffDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        getDemoFinalReviewerHandoffDeliveryFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
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
        listRecentTaskEvidencePackageArchives(20).then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getTaskEvidencePackageArchiveSummary(50).then(
          (summary) => ({ summary, error: null as string | null }),
          (caught) => ({ summary: null, error: errorMessage(caught) })
        ),
        getTaskEvidencePackageShareCenter(20).then(
          (shareCenter) => ({ shareCenter, error: null as string | null }),
          (caught) => ({ shareCenter: null, error: errorMessage(caught) })
        ),
        getTaskEvidencePackageFinalization().then(
          (finalization) => ({ finalization, error: null as string | null }),
          (caught) => ({ finalization: null, error: errorMessage(caught) })
        ),
        listTaskEvidencePackageShareDeliveryReceipts().then(
          (receipts) => ({ receipts, error: null as string | null }),
          (caught) => ({ receipts: null, error: errorMessage(caught) })
        ),
        listTaskEvidencePackageAcceptanceCloseoutArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
        getTaskEvidencePackageAcceptanceCertificate().then(
          (certificate) => ({ certificate, error: null as string | null }),
          (caught) => ({ certificate: null, error: errorMessage(caught) })
        ),
        listTaskEvidencePackageAcceptanceCertificateArchives().then(
          (archives) => ({ archives, error: null as string | null }),
          (caught) => ({ archives: null, error: errorMessage(caught) })
        ),
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
        const publishReadinessResult = await getGitHubPublishReadiness(
          repositoryTarget.owner,
          repositoryTarget.repository
        ).then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        );
        setGitHubPublishReadiness(publishReadinessResult.readiness);
        const publishPermissionReadinessResult = await getGitHubPublishPermissionReadiness(
          repositoryTarget.owner,
          repositoryTarget.repository
        ).then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        );
        setGitHubPublishPermissionReadiness(publishPermissionReadinessResult.readiness);
        const livePublishPreflightResult = await getGitHubLivePublishPreflight(
          repositoryTarget.owner,
          repositoryTarget.repository
        ).then(
          (preflight) => ({ preflight, error: null as string | null }),
          (caught) => ({ preflight: null, error: errorMessage(caught) })
        );
        setGitHubLivePublishPreflight(livePublishPreflightResult.preflight);
      } else {
        setGitHubRepositoryAccessReadiness(null);
        const publishReadinessResult = await getGitHubPublishReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        );
        setGitHubPublishReadiness(publishReadinessResult.readiness);
        const publishPermissionReadinessResult = await getGitHubPublishPermissionReadiness().then(
          (readiness) => ({ readiness, error: null as string | null }),
          (caught) => ({ readiness: null, error: errorMessage(caught) })
        );
        setGitHubPublishPermissionReadiness(publishPermissionReadinessResult.readiness);
        const livePublishPreflightResult = await getGitHubLivePublishPreflight().then(
          (preflight) => ({ preflight, error: null as string | null }),
          (caught) => ({ preflight: null, error: errorMessage(caught) })
        );
        setGitHubLivePublishPreflight(livePublishPreflightResult.preflight);
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
      if (demoFinalHandoffReportPackageResult.reportPackage) {
        setDemoFinalHandoffReportPackage(demoFinalHandoffReportPackageResult.reportPackage);
      }
      setDemoFinalHandoffReportPackageError(demoFinalHandoffReportPackageResult.error);
      if (demoFinalHandoffReportPackageArchiveResult.archives) {
        setDemoFinalHandoffReportPackageArchives(demoFinalHandoffReportPackageArchiveResult.archives);
      }
      setDemoFinalHandoffReportPackageArchiveError(demoFinalHandoffReportPackageArchiveResult.error);
      if (demoSelfHostedLaunchReadinessResult.readiness) {
        setDemoSelfHostedLaunchReadiness(demoSelfHostedLaunchReadinessResult.readiness);
      }
      setDemoSelfHostedLaunchReadinessError(demoSelfHostedLaunchReadinessResult.error);
      if (demoSelfHostedLaunchReadinessArchiveResult.archives) {
        setDemoSelfHostedLaunchReadinessArchives(demoSelfHostedLaunchReadinessArchiveResult.archives);
      }
      setDemoSelfHostedLaunchReadinessArchiveError(demoSelfHostedLaunchReadinessArchiveResult.error);
      if (demoLaunchEvidencePackageResult.evidencePackage) {
        setDemoLaunchEvidencePackage(demoLaunchEvidencePackageResult.evidencePackage);
      }
      setDemoLaunchEvidencePackageError(demoLaunchEvidencePackageResult.error);
      if (demoLaunchEvidencePackageArchiveResult.archives) {
        setDemoLaunchEvidencePackageArchives(demoLaunchEvidencePackageArchiveResult.archives);
      }
      setDemoLaunchEvidencePackageArchiveError(demoLaunchEvidencePackageArchiveResult.error);
      if (demoLaunchEvidenceShareCenterResult.center) {
        setDemoLaunchEvidenceShareCenter(demoLaunchEvidenceShareCenterResult.center);
      }
      setDemoLaunchEvidenceShareCenterError(demoLaunchEvidenceShareCenterResult.error);
      if (demoLaunchEvidenceFinalizationResult.finalization) {
        setDemoLaunchEvidenceFinalization(demoLaunchEvidenceFinalizationResult.finalization);
      }
      setDemoLaunchEvidenceFinalizationError(demoLaunchEvidenceFinalizationResult.error);
      if (demoLaunchAcceptanceCloseoutResult.closeout) {
        setDemoLaunchAcceptanceCloseout(demoLaunchAcceptanceCloseoutResult.closeout);
      }
      setDemoLaunchAcceptanceCloseoutError(demoLaunchAcceptanceCloseoutResult.error);
      if (demoLaunchAcceptanceCloseoutArchiveResult.archives) {
        setDemoLaunchAcceptanceCloseoutArchives(demoLaunchAcceptanceCloseoutArchiveResult.archives);
      }
      setDemoLaunchAcceptanceCloseoutArchiveError(demoLaunchAcceptanceCloseoutArchiveResult.error);
      if (demoLaunchAcceptanceCertificateResult.certificate) {
        setDemoLaunchAcceptanceCertificate(demoLaunchAcceptanceCertificateResult.certificate);
      }
      setDemoLaunchAcceptanceCertificateError(demoLaunchAcceptanceCertificateResult.error);
      if (demoLaunchAcceptanceCertificateArchiveResult.archives) {
        setDemoLaunchAcceptanceCertificateArchives(demoLaunchAcceptanceCertificateArchiveResult.archives);
      }
      setDemoLaunchAcceptanceCertificateArchiveError(demoLaunchAcceptanceCertificateArchiveResult.error);
      if (demoLaunchEvidenceDeliveryReceiptResult.receipts) {
        setDemoLaunchEvidenceDeliveryReceipts(demoLaunchEvidenceDeliveryReceiptResult.receipts);
      }
      setDemoLaunchEvidenceDeliveryReceiptError(demoLaunchEvidenceDeliveryReceiptResult.error);
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
      if (demoEndToEndAcceptanceMatrixResult.matrix) {
        setDemoEndToEndAcceptanceMatrix(demoEndToEndAcceptanceMatrixResult.matrix);
      }
      setDemoEndToEndAcceptanceMatrixError(demoEndToEndAcceptanceMatrixResult.error);
      if (externalExposureReadinessResult.readiness) {
        setExternalExposureReadiness(externalExposureReadinessResult.readiness);
      }
      setExternalExposureReadinessError(externalExposureReadinessResult.error);
      if (externalExposureReadinessArchiveResult.archives) {
        setExternalExposureReadinessArchives(externalExposureReadinessArchiveResult.archives);
      }
      setExternalExposureReadinessArchiveError(externalExposureReadinessArchiveResult.error);
      if (externalExposureHandoffPackageResult.handoffPackage) {
        setExternalExposureHandoffPackage(externalExposureHandoffPackageResult.handoffPackage);
      }
      setExternalExposureHandoffPackageError(externalExposureHandoffPackageResult.error);
      if (externalExposureSessionResult.sessions) {
        setExternalExposureSessions(externalExposureSessionResult.sessions);
      }
      setExternalExposureSessionError(externalExposureSessionResult.error);
      if (externalExposureCloseoutResult.closeout) {
        setExternalExposureCloseout(externalExposureCloseoutResult.closeout);
      }
      setExternalExposureCloseoutError(externalExposureCloseoutResult.error);
      if (externalExposureCloseoutArchiveResult.archives) {
        setExternalExposureCloseoutArchives(externalExposureCloseoutArchiveResult.archives);
      }
      setExternalExposureCloseoutArchiveError(externalExposureCloseoutArchiveResult.error);
      if (externalExposureOperatorHandoffChecklistResult.checklist) {
        setExternalExposureOperatorHandoffChecklist(externalExposureOperatorHandoffChecklistResult.checklist);
      }
      setExternalExposureOperatorHandoffChecklistError(externalExposureOperatorHandoffChecklistResult.error);
      if (externalExposureOperatorHandoffChecklistArchiveResult.archives) {
        setExternalExposureOperatorHandoffChecklistArchives(externalExposureOperatorHandoffChecklistArchiveResult.archives);
      }
      setExternalExposureOperatorHandoffChecklistArchiveError(
        externalExposureOperatorHandoffChecklistArchiveResult.error
      );
      if (demoLiveTriggerLaunchPackageArchiveResult.archives) {
        setDemoLiveTriggerLaunchPackageArchives(demoLiveTriggerLaunchPackageArchiveResult.archives);
      }
      setDemoLiveTriggerLaunchPackageArchiveError(demoLiveTriggerLaunchPackageArchiveResult.error);
      if (demoLiveTriggerOutcomeCloseoutArchiveResult.archives) {
        setDemoLiveTriggerOutcomeCloseoutArchives(demoLiveTriggerOutcomeCloseoutArchiveResult.archives);
      }
      setDemoLiveTriggerOutcomeCloseoutArchiveError(demoLiveTriggerOutcomeCloseoutArchiveResult.error);
      if (demoLiveDemoEvidenceBundleResult.bundle) {
        setDemoLiveDemoEvidenceBundle(demoLiveDemoEvidenceBundleResult.bundle);
      }
      setDemoLiveDemoEvidenceBundleError(demoLiveDemoEvidenceBundleResult.error);
      if (demoLiveDemoEvidenceBundleArchiveResult.archives) {
        setDemoLiveDemoEvidenceBundleArchives(demoLiveDemoEvidenceBundleArchiveResult.archives);
      }
      setDemoLiveDemoEvidenceBundleArchiveError(demoLiveDemoEvidenceBundleArchiveResult.error);
      if (demoLiveDemoHandoffPackageResult.handoffPackage) {
        setDemoLiveDemoHandoffPackage(demoLiveDemoHandoffPackageResult.handoffPackage);
      }
      setDemoLiveDemoHandoffPackageError(demoLiveDemoHandoffPackageResult.error);
      if (demoLiveDemoHandoffDeliveryReceiptResult.receipts) {
        setDemoLiveDemoHandoffDeliveryReceipts(demoLiveDemoHandoffDeliveryReceiptResult.receipts);
      }
      setDemoLiveDemoHandoffDeliveryReceiptError(demoLiveDemoHandoffDeliveryReceiptResult.error);
      if (demoLiveDemoHandoffDeliveryFinalizationResult.finalization) {
        setDemoLiveDemoHandoffDeliveryFinalization(demoLiveDemoHandoffDeliveryFinalizationResult.finalization);
      }
      setDemoLiveDemoHandoffDeliveryFinalizationError(demoLiveDemoHandoffDeliveryFinalizationResult.error);
      if (demoLiveDemoHandoffDeliveryFinalizationArchiveResult.archives) {
        setDemoLiveDemoHandoffDeliveryFinalizationArchives(
          demoLiveDemoHandoffDeliveryFinalizationArchiveResult.archives
        );
      }
      setDemoLiveDemoHandoffDeliveryFinalizationArchiveError(
        demoLiveDemoHandoffDeliveryFinalizationArchiveResult.error
      );
      if (demoLiveDemoCompletionCertificateResult.certificate) {
        setDemoLiveDemoCompletionCertificate(demoLiveDemoCompletionCertificateResult.certificate);
      }
      setDemoLiveDemoCompletionCertificateError(demoLiveDemoCompletionCertificateResult.error);
      if (demoLiveDemoCompletionCertificateArchiveResult.archives) {
        setDemoLiveDemoCompletionCertificateArchives(demoLiveDemoCompletionCertificateArchiveResult.archives);
      }
      setDemoLiveDemoCompletionCertificateArchiveError(demoLiveDemoCompletionCertificateArchiveResult.error);
      if (demoLiveDemoArtifactChainReportResult.report) {
        setDemoLiveDemoArtifactChainReport(demoLiveDemoArtifactChainReportResult.report);
      }
      setDemoLiveDemoArtifactChainReportError(demoLiveDemoArtifactChainReportResult.error);
      if (demoLiveDemoReplayPackageResult.replayPackage) {
        setDemoLiveDemoReplayPackage(demoLiveDemoReplayPackageResult.replayPackage);
      }
      setDemoLiveDemoReplayPackageError(demoLiveDemoReplayPackageResult.error);
      if (demoLiveDemoReviewerDeliveryCenterResult.deliveryCenter) {
        setDemoLiveDemoReviewerDeliveryCenter(demoLiveDemoReviewerDeliveryCenterResult.deliveryCenter);
      }
      setDemoLiveDemoReviewerDeliveryCenterError(demoLiveDemoReviewerDeliveryCenterResult.error);
      if (demoLiveDemoReviewerDeliveryCenterArchiveResult.archives) {
        setDemoLiveDemoReviewerDeliveryCenterArchives(demoLiveDemoReviewerDeliveryCenterArchiveResult.archives);
      }
      setDemoLiveDemoReviewerDeliveryCenterArchiveError(demoLiveDemoReviewerDeliveryCenterArchiveResult.error);
      if (demoLiveDemoReviewerDeliveryCenterDeliveryReceiptResult.receipts) {
        setDemoLiveDemoReviewerDeliveryCenterDeliveryReceipts(
          demoLiveDemoReviewerDeliveryCenterDeliveryReceiptResult.receipts
        );
      }
      setDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptError(
        demoLiveDemoReviewerDeliveryCenterDeliveryReceiptResult.error
      );
      if (demoAcceptanceSummaryResult.summary) {
        setDemoAcceptanceSummary(demoAcceptanceSummaryResult.summary);
      }
      setDemoAcceptanceSummaryError(demoAcceptanceSummaryResult.error);
      if (demoFinalAcceptanceSharePackageResult.sharePackage) {
        setDemoFinalAcceptanceSharePackage(demoFinalAcceptanceSharePackageResult.sharePackage);
      }
      setDemoFinalAcceptanceSharePackageError(demoFinalAcceptanceSharePackageResult.error);
      if (demoFinalAcceptanceSharePackageArchiveResult.archives) {
        setDemoFinalAcceptanceSharePackageArchives(demoFinalAcceptanceSharePackageArchiveResult.archives);
      }
      setDemoFinalAcceptanceSharePackageArchiveError(demoFinalAcceptanceSharePackageArchiveResult.error);
      if (demoFinalAcceptanceShareDeliveryReceiptResult.receipts) {
        setDemoFinalAcceptanceShareDeliveryReceipts(demoFinalAcceptanceShareDeliveryReceiptResult.receipts);
      }
      setDemoFinalAcceptanceShareDeliveryReceiptError(demoFinalAcceptanceShareDeliveryReceiptResult.error);
      if (demoFinalAcceptanceShareFinalizationResult.finalization) {
        setDemoFinalAcceptanceShareFinalization(demoFinalAcceptanceShareFinalizationResult.finalization);
      }
      setDemoFinalAcceptanceShareFinalizationError(demoFinalAcceptanceShareFinalizationResult.error);
      if (demoFinalAcceptanceCompletionEvidenceBundleResult.bundle) {
        setDemoFinalAcceptanceCompletionEvidenceBundle(demoFinalAcceptanceCompletionEvidenceBundleResult.bundle);
      }
      setDemoFinalAcceptanceCompletionEvidenceBundleError(demoFinalAcceptanceCompletionEvidenceBundleResult.error);
      if (demoFinalAcceptanceCompletionArchiveResult.archives) {
        setDemoFinalAcceptanceCompletionArchives(demoFinalAcceptanceCompletionArchiveResult.archives);
      }
      setDemoFinalAcceptanceCompletionArchiveError(demoFinalAcceptanceCompletionArchiveResult.error);
      if (demoFinalAcceptanceCompletionEvidenceDeliveryReceiptResult.receipts) {
        setDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts(
          demoFinalAcceptanceCompletionEvidenceDeliveryReceiptResult.receipts
        );
      }
      setDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptError(
        demoFinalAcceptanceCompletionEvidenceDeliveryReceiptResult.error
      );
      if (demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationResult.finalization) {
        setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization(
          demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationResult.finalization
        );
      }
      setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationError(
        demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationResult.error
      );
      if (demoFinalAcceptanceCompletionCloseoutResult.closeout) {
        setDemoFinalAcceptanceCompletionCloseout(demoFinalAcceptanceCompletionCloseoutResult.closeout);
      }
      setDemoFinalAcceptanceCompletionCloseoutError(demoFinalAcceptanceCompletionCloseoutResult.error);
      if (demoFinalAcceptanceCompletionCloseoutArchiveResult.archives) {
        setDemoFinalAcceptanceCompletionCloseoutArchives(demoFinalAcceptanceCompletionCloseoutArchiveResult.archives);
      }
      setDemoFinalAcceptanceCompletionCloseoutArchiveError(demoFinalAcceptanceCompletionCloseoutArchiveResult.error);
      if (demoFinalExternalReviewEvidencePackageResult.evidencePackage) {
        setDemoFinalExternalReviewEvidencePackage(demoFinalExternalReviewEvidencePackageResult.evidencePackage);
      }
      setDemoFinalExternalReviewEvidencePackageError(demoFinalExternalReviewEvidencePackageResult.error);
      if (demoFinalExternalReviewEvidencePackageArchiveResult.archives) {
        setDemoFinalExternalReviewEvidencePackageArchives(demoFinalExternalReviewEvidencePackageArchiveResult.archives);
      }
      setDemoFinalExternalReviewEvidencePackageArchiveError(
        demoFinalExternalReviewEvidencePackageArchiveResult.error
      );
      if (demoFinalExternalReviewEvidencePackageDeliveryReceiptResult.receipts) {
        setDemoFinalExternalReviewEvidencePackageDeliveryReceipts(
          demoFinalExternalReviewEvidencePackageDeliveryReceiptResult.receipts
        );
      }
      setDemoFinalExternalReviewEvidencePackageDeliveryReceiptError(
        demoFinalExternalReviewEvidencePackageDeliveryReceiptResult.error
      );
      if (demoFinalExternalReviewEvidencePackageDeliveryFinalizationResult.finalization) {
        setDemoFinalExternalReviewEvidencePackageDeliveryFinalization(
          demoFinalExternalReviewEvidencePackageDeliveryFinalizationResult.finalization
        );
      }
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationError(
        demoFinalExternalReviewEvidencePackageDeliveryFinalizationResult.error
      );
      if (demoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveResult.archives) {
        setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives(
          demoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveResult.archives
        );
      }
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveError(
        demoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveResult.error
      );
      if (demoFinalExternalReviewDeliveryCertificateResult.certificate) {
        setDemoFinalExternalReviewDeliveryCertificate(
          demoFinalExternalReviewDeliveryCertificateResult.certificate
        );
      }
      setDemoFinalExternalReviewDeliveryCertificateError(
        demoFinalExternalReviewDeliveryCertificateResult.error
      );
      if (demoFinalExternalReviewDeliveryCertificateArchiveResult.archives) {
        setDemoFinalExternalReviewDeliveryCertificateArchives(
          demoFinalExternalReviewDeliveryCertificateArchiveResult.archives
        );
      }
      setDemoFinalExternalReviewDeliveryCertificateArchiveError(
        demoFinalExternalReviewDeliveryCertificateArchiveResult.error
      );
      if (demoFinalExternalReviewReleaseBundleResult.bundle) {
        setDemoFinalExternalReviewReleaseBundle(demoFinalExternalReviewReleaseBundleResult.bundle);
      }
      setDemoFinalExternalReviewReleaseBundleError(demoFinalExternalReviewReleaseBundleResult.error);
      if (demoFinalExternalReviewReleaseBundleArchiveResult.archives) {
        setDemoFinalExternalReviewReleaseBundleArchives(
          demoFinalExternalReviewReleaseBundleArchiveResult.archives
        );
      }
      setDemoFinalExternalReviewReleaseBundleArchiveError(
        demoFinalExternalReviewReleaseBundleArchiveResult.error
      );
      if (demoFinalExternalReviewReleaseBundleDeliveryReceiptResult.receipts) {
        setDemoFinalExternalReviewReleaseBundleDeliveryReceipts(
          demoFinalExternalReviewReleaseBundleDeliveryReceiptResult.receipts
        );
      }
      setDemoFinalExternalReviewReleaseBundleDeliveryReceiptError(
        demoFinalExternalReviewReleaseBundleDeliveryReceiptResult.error
      );
      if (demoFinalExternalReviewReleaseBundleDeliveryFinalizationResult.finalization) {
        setDemoFinalExternalReviewReleaseBundleDeliveryFinalization(
          demoFinalExternalReviewReleaseBundleDeliveryFinalizationResult.finalization
        );
      }
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationError(
        demoFinalExternalReviewReleaseBundleDeliveryFinalizationResult.error
      );
      if (demoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveResult.archives) {
        setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives(
          demoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveResult.archives
        );
      }
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveError(
        demoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveResult.error
      );
      if (demoFinalExternalReviewReleaseBundleDeliveryCertificateResult.certificate) {
        setDemoFinalExternalReviewReleaseBundleDeliveryCertificate(
          demoFinalExternalReviewReleaseBundleDeliveryCertificateResult.certificate
        );
      }
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateError(
        demoFinalExternalReviewReleaseBundleDeliveryCertificateResult.error
      );
      if (demoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveResult.archives) {
        setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives(
          demoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveResult.archives
        );
      }
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveError(
        demoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveResult.error
      );
      if (demoFinalReviewerHandoffDeliveryReceiptResult.receipts) {
        setDemoFinalReviewerHandoffDeliveryReceipts(demoFinalReviewerHandoffDeliveryReceiptResult.receipts);
      }
      setDemoFinalReviewerHandoffDeliveryReceiptError(
        demoFinalReviewerHandoffDeliveryReceiptResult.error
      );
      if (demoFinalReviewerHandoffDeliveryFinalizationResult.finalization) {
        setDemoFinalReviewerHandoffDeliveryFinalization(
          demoFinalReviewerHandoffDeliveryFinalizationResult.finalization
        );
      }
      setDemoFinalReviewerHandoffDeliveryFinalizationError(
        demoFinalReviewerHandoffDeliveryFinalizationResult.error
      );
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
      if (taskEvidenceArchiveResult.archives) {
        setTaskEvidenceArchives(taskEvidenceArchiveResult.archives);
      }
      if (taskEvidenceArchiveSummaryResult.summary) {
        setTaskEvidenceArchiveSummary(taskEvidenceArchiveSummaryResult.summary);
      }
      setTaskEvidenceArchiveError(taskEvidenceArchiveResult.error ?? taskEvidenceArchiveSummaryResult.error);
      if (taskEvidenceShareCenterResult.shareCenter) {
        setTaskEvidenceShareCenter(taskEvidenceShareCenterResult.shareCenter);
      }
      setTaskEvidenceShareCenterError(taskEvidenceShareCenterResult.error);
      if (taskEvidenceFinalizationResult.finalization) {
        setTaskEvidenceFinalization(taskEvidenceFinalizationResult.finalization);
      }
      setTaskEvidenceFinalizationError(taskEvidenceFinalizationResult.error);
      if (taskEvidenceDeliveryReceiptResult.receipts) {
        setTaskEvidenceDeliveryReceipts(taskEvidenceDeliveryReceiptResult.receipts);
      }
      setTaskEvidenceDeliveryReceiptError(taskEvidenceDeliveryReceiptResult.error);
      if (taskEvidenceCloseoutArchiveResult.archives) {
        setTaskEvidenceCloseoutArchives(taskEvidenceCloseoutArchiveResult.archives);
      }
      setTaskEvidenceCloseoutArchiveError(taskEvidenceCloseoutArchiveResult.error);
      if (taskEvidenceAcceptanceCertificateResult.certificate) {
        setTaskEvidenceAcceptanceCertificate(taskEvidenceAcceptanceCertificateResult.certificate);
      }
      setTaskEvidenceAcceptanceCertificateError(taskEvidenceAcceptanceCertificateResult.error);
      if (taskEvidenceAcceptanceCertificateArchiveResult.archives) {
        setTaskEvidenceAcceptanceCertificateArchives(taskEvidenceAcceptanceCertificateArchiveResult.archives);
      }
      setTaskEvidenceAcceptanceCertificateArchiveError(taskEvidenceAcceptanceCertificateArchiveResult.error);
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
    Promise.all([
      getTaskDetail(selectedTask.id),
      retryPreflightRequest,
      listTaskEvidencePackageArchives(selectedTask.id).catch(() => [])
    ])
      .then(([taskDetail, retryPreflight, evidencePackageArchives]) => {
        if (!cancelled) {
          setDetail({
            ...taskDetail,
            evidencePackageArchives,
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
  const handleDownloadTaskReport = useCallback((taskId: string) => downloadTaskReport(taskId), []);
  const handleArchiveTaskEvidencePackage = useCallback(async (taskId: string) => {
    const archive = await archiveTaskEvidencePackage(taskId);
    setDetail((current) => ({
      ...current,
      evidencePackageArchives: [
        archive,
        ...(current.evidencePackageArchives ?? []).filter((item) => item.id !== archive.id)
      ]
    }));
    setTaskEvidenceArchives((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setTaskEvidenceArchiveSummary((current) => current
      ? {
          ...current,
          totalArchiveCount: current.totalArchiveCount + 1,
          completedArchiveCount: current.completedArchiveCount + (archive.status === 'COMPLETED' ? 1 : 0),
          failedArchiveCount: current.failedArchiveCount + (archive.status === 'FAILED' ? 1 : 0),
          pendingReviewArchiveCount: current.pendingReviewArchiveCount + (archive.status === 'PENDING_REVIEW' ? 1 : 0),
          cancelledArchiveCount: current.cancelledArchiveCount + (archive.status === 'CANCELLED' ? 1 : 0),
          latestArchiveId: archive.id,
          latestTaskId: archive.taskId,
          latestRepositoryOwner: archive.repositoryOwner,
          latestRepositoryName: archive.repositoryName,
          latestIssueNumber: archive.issueNumber,
          latestArchivedAt: archive.archivedAt,
          nextAction: `Download archived task evidence ${archive.id} or open task ${archive.taskId} before sharing review notes.`
        }
      : current
    );
    try {
      setTaskEvidenceShareCenter(await getTaskEvidencePackageShareCenter(20));
      setTaskEvidenceShareCenterError(null);
    } catch (caught) {
      setTaskEvidenceShareCenterError(errorMessage(caught));
    }
    try {
      setTaskEvidenceFinalization(await getTaskEvidencePackageFinalization());
      setTaskEvidenceFinalizationError(null);
    } catch (caught) {
      setTaskEvidenceFinalizationError(errorMessage(caught));
    }
    setTaskEvidenceArchiveError(null);
    return archive;
  }, []);
  const handleDownloadTaskEvidencePackageShareCenterReport = useCallback(() => (
    downloadTaskEvidencePackageShareCenterReport()
  ), []);
  const handleDownloadTaskEvidencePackageReport = useCallback((archiveId: string) => (
    downloadTaskEvidencePackageReport(archiveId)
  ), []);
  const handleDownloadTaskEvidencePackageFinalizationReport = useCallback(() => (
    downloadTaskEvidencePackageFinalizationReport()
  ), []);
  const handleCreateTaskEvidencePackageShareDeliveryReceipt = useCallback(async (
    input: FixTaskEvidencePackageShareDeliveryReceiptInput
  ) => {
    const receipt = await createTaskEvidencePackageShareDeliveryReceipt(input);
    setTaskEvidenceDeliveryReceipts((current) => [receipt, ...current.filter((item) => item.id !== receipt.id)].slice(0, 20));
    setTaskEvidenceDeliveryReceiptError(null);
    try {
      setTaskEvidenceDeliveryReceipts(await listTaskEvidencePackageShareDeliveryReceipts());
      setTaskEvidenceDeliveryReceiptError(null);
    } catch (caught) {
      setTaskEvidenceDeliveryReceiptError(errorMessage(caught));
    }
    try {
      setTaskEvidenceFinalization(await getTaskEvidencePackageFinalization());
      setTaskEvidenceFinalizationError(null);
    } catch (caught) {
      setTaskEvidenceFinalizationError(errorMessage(caught));
    }
    return receipt;
  }, []);
  const handleDownloadTaskEvidencePackageShareDeliveryReceiptReport = useCallback((receiptId: string) => (
    downloadTaskEvidencePackageShareDeliveryReceiptReport(receiptId)
  ), []);
  const handleArchiveTaskEvidencePackageAcceptanceCloseout = useCallback(async () => {
    const archive = await archiveTaskEvidencePackageAcceptanceCloseout();
    setTaskEvidenceCloseoutArchives((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setTaskEvidenceCloseoutArchiveError(null);
    try {
      setTaskEvidenceAcceptanceCertificate(await getTaskEvidencePackageAcceptanceCertificate());
      setTaskEvidenceAcceptanceCertificateError(null);
    } catch (caught) {
      setTaskEvidenceAcceptanceCertificateError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadTaskEvidencePackageAcceptanceCloseoutArchiveReport = useCallback((archiveId: string) => (
    downloadTaskEvidencePackageAcceptanceCloseoutArchiveReport(archiveId)
  ), []);
  const handleDownloadTaskEvidencePackageAcceptanceCertificateReport = useCallback(() => (
    downloadTaskEvidencePackageAcceptanceCertificateReport()
  ), []);
  const handleArchiveTaskEvidencePackageAcceptanceCertificate = useCallback(async () => {
    const archive = await archiveTaskEvidencePackageAcceptanceCertificate();
    setTaskEvidenceAcceptanceCertificateArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setTaskEvidenceAcceptanceCertificateArchiveError(null);
    try {
      setTaskEvidenceAcceptanceCertificate(await getTaskEvidencePackageAcceptanceCertificate());
      setTaskEvidenceAcceptanceCertificateError(null);
    } catch (caught) {
      setTaskEvidenceAcceptanceCertificateError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadTaskEvidencePackageAcceptanceCertificateArchiveReport = useCallback((archiveId: string) => (
    downloadTaskEvidencePackageAcceptanceCertificateArchiveReport(archiveId)
  ), []);
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
  const handleDownloadDemoFinalHandoffReportPackage = useCallback(() => (
    downloadDemoFinalHandoffReportPackage()
  ), []);
  const handleArchiveDemoFinalHandoffReportPackage = useCallback(async () => {
    const archive = await archiveDemoFinalHandoffReportPackage();
    setDemoFinalHandoffReportPackageArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalHandoffReportPackageArchiveError(null);
    try {
      const reportPackage = await getDemoFinalHandoffReportPackage();
      setDemoFinalHandoffReportPackage(reportPackage);
      setDemoFinalHandoffReportPackageError(null);
    } catch (caught) {
      setDemoFinalHandoffReportPackageError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalHandoffReportPackageArchiveReport = useCallback((archiveId: string) => (
    downloadDemoFinalHandoffReportPackageArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoSelfHostedLaunchReadinessReport = useCallback(() => (
    downloadDemoSelfHostedLaunchReadinessReport()
  ), []);
  const handleDownloadDemoSelfHostedLaunchReadinessArchiveReport = useCallback((archiveId: string) => (
    downloadDemoSelfHostedLaunchReadinessArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoLaunchEvidencePackageReport = useCallback(() => (
    downloadDemoLaunchEvidencePackageReport()
  ), []);
  const handleArchiveDemoLaunchEvidencePackage = useCallback(async () => {
    const archive = await archiveDemoLaunchEvidencePackage();
    setDemoLaunchEvidencePackageArchives((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setDemoLaunchEvidencePackageArchiveError(null);
    try {
      const center = await getDemoLaunchEvidenceShareCenter();
      setDemoLaunchEvidenceShareCenter(center);
      setDemoLaunchEvidenceShareCenterError(null);
    } catch (caught) {
      setDemoLaunchEvidenceShareCenterError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoLaunchEvidenceFinalization();
      setDemoLaunchEvidenceFinalization(finalization);
      setDemoLaunchEvidenceFinalizationError(null);
    } catch (caught) {
      setDemoLaunchEvidenceFinalizationError(errorMessage(caught));
    }
    try {
      const closeout = await getDemoLaunchAcceptanceCloseout();
      setDemoLaunchAcceptanceCloseout(closeout);
      setDemoLaunchAcceptanceCloseoutError(null);
    } catch (caught) {
      setDemoLaunchAcceptanceCloseoutError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoLaunchEvidencePackageArchiveReport = useCallback((archiveId: string) => (
    downloadDemoLaunchEvidencePackageArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoLaunchEvidenceShareCenterReport = useCallback(() => (
    downloadDemoLaunchEvidenceShareCenterReport()
  ), []);
  const handleDownloadDemoLaunchEvidenceFinalizationReport = useCallback(() => (
    downloadDemoLaunchEvidenceFinalizationReport()
  ), []);
  const handleDownloadDemoLaunchAcceptanceCloseoutReport = useCallback(() => (
    downloadDemoLaunchAcceptanceCloseoutReport()
  ), []);
  const handleDownloadDemoLaunchAcceptanceCertificateReport = useCallback(() => (
    downloadDemoLaunchAcceptanceCertificateReport()
  ), []);
  const handleArchiveDemoLaunchAcceptanceCertificate = useCallback(async () => {
    const archive = await archiveDemoLaunchAcceptanceCertificate();
    setDemoLaunchAcceptanceCertificateArchives((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setDemoLaunchAcceptanceCertificateArchiveError(null);
    return archive;
  }, []);
  const handleArchiveDemoLaunchAcceptanceCloseout = useCallback(async () => {
    const archive = await archiveDemoLaunchAcceptanceCloseout();
    setDemoLaunchAcceptanceCloseoutArchives((current) => [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20));
    setDemoLaunchAcceptanceCloseoutArchiveError(null);
    try {
      const certificate = await getDemoLaunchAcceptanceCertificate();
      setDemoLaunchAcceptanceCertificate(certificate);
      setDemoLaunchAcceptanceCertificateError(null);
    } catch (caught) {
      setDemoLaunchAcceptanceCertificateError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoLaunchAcceptanceCloseoutArchiveReport = useCallback((archiveId: string) => (
    downloadDemoLaunchAcceptanceCloseoutArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoLaunchAcceptanceCertificateArchiveReport = useCallback((archiveId: string) => (
    downloadDemoLaunchAcceptanceCertificateArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoAcceptanceSummaryReport = useCallback(() => (
    downloadDemoAcceptanceSummaryReport()
  ), []);
  const handleDownloadDemoFinalAcceptanceSharePackageReport = useCallback(() => (
    downloadDemoFinalAcceptanceSharePackageReport()
  ), []);
  const handleArchiveDemoFinalAcceptanceSharePackage = useCallback(async () => {
    const archive = await archiveDemoFinalAcceptanceSharePackage();
    setDemoFinalAcceptanceSharePackageArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalAcceptanceSharePackageArchiveError(null);
    try {
      const sharePackage = await getDemoFinalAcceptanceSharePackage();
      setDemoFinalAcceptanceSharePackage(sharePackage);
      setDemoFinalAcceptanceSharePackageError(null);
    } catch (caught) {
      setDemoFinalAcceptanceSharePackageError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalAcceptanceSharePackageArchiveReport = useCallback((archiveId: string) => (
    downloadDemoFinalAcceptanceSharePackageArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoFinalAcceptanceShareFinalizationReport = useCallback(() => (
    downloadDemoFinalAcceptanceShareFinalizationReport()
  ), []);
  const handleDownloadDemoFinalAcceptanceCompletionEvidenceBundleReport = useCallback(() => (
    downloadDemoFinalAcceptanceCompletionEvidenceBundleReport()
  ), []);
  const handleArchiveDemoFinalAcceptanceCompletion = useCallback(async () => {
    const archive = await archiveDemoFinalAcceptanceCompletion();
    setDemoFinalAcceptanceCompletionArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalAcceptanceCompletionArchiveError(null);
    try {
      const archives = await listDemoFinalAcceptanceCompletionArchives();
      setDemoFinalAcceptanceCompletionArchives(archives);
      setDemoFinalAcceptanceCompletionArchiveError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionArchiveError(errorMessage(caught));
    }
    try {
      const bundle = await getDemoFinalAcceptanceCompletionEvidenceBundle();
      setDemoFinalAcceptanceCompletionEvidenceBundle(bundle);
      setDemoFinalAcceptanceCompletionEvidenceBundleError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionEvidenceBundleError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization();
      setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization(finalization);
      setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationError(errorMessage(caught));
    }
    try {
      const closeout = await getDemoFinalAcceptanceCompletionCloseout();
      setDemoFinalAcceptanceCompletionCloseout(closeout);
      setDemoFinalAcceptanceCompletionCloseoutError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionCloseoutError(errorMessage(caught));
    }
    try {
      const evidencePackage = await getDemoFinalExternalReviewEvidencePackage();
      setDemoFinalExternalReviewEvidencePackage(evidencePackage);
      setDemoFinalExternalReviewEvidencePackageError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalAcceptanceCompletionArchiveReport = useCallback((archiveId: string) => (
    downloadDemoFinalAcceptanceCompletionArchiveReport(archiveId)
  ), []);
  const handleArchiveDemoFinalAcceptanceCompletionCloseout = useCallback(async () => {
    const archive = await archiveDemoFinalAcceptanceCompletionCloseout();
    setDemoFinalAcceptanceCompletionCloseoutArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalAcceptanceCompletionCloseoutArchiveError(null);
    try {
      const archives = await listDemoFinalAcceptanceCompletionCloseoutArchives();
      setDemoFinalAcceptanceCompletionCloseoutArchives(archives);
      setDemoFinalAcceptanceCompletionCloseoutArchiveError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionCloseoutArchiveError(errorMessage(caught));
    }
    try {
      const closeout = await getDemoFinalAcceptanceCompletionCloseout();
      setDemoFinalAcceptanceCompletionCloseout(closeout);
      setDemoFinalAcceptanceCompletionCloseoutError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionCloseoutError(errorMessage(caught));
    }
    try {
      const evidencePackage = await getDemoFinalExternalReviewEvidencePackage();
      setDemoFinalExternalReviewEvidencePackage(evidencePackage);
      setDemoFinalExternalReviewEvidencePackageError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalAcceptanceCompletionCloseoutArchiveReport = useCallback((archiveId: string) => (
    downloadDemoFinalAcceptanceCompletionCloseoutArchiveReport(archiveId)
  ), []);
  const handleCreateDemoFinalAcceptanceShareDeliveryReceipt = useCallback(async (
    input: DemoFinalAcceptanceShareDeliveryReceiptInput
  ) => {
    const receipt = await createDemoFinalAcceptanceShareDeliveryReceipt(input);
    setDemoFinalAcceptanceShareDeliveryReceipts((current) => [
      receipt,
      ...current.filter((item) => item.id !== receipt.id)
    ].slice(0, 20));
    setDemoFinalAcceptanceShareDeliveryReceiptError(null);
    try {
      const receipts = await listDemoFinalAcceptanceShareDeliveryReceipts();
      setDemoFinalAcceptanceShareDeliveryReceipts(receipts);
      setDemoFinalAcceptanceShareDeliveryReceiptError(null);
    } catch (caught) {
      setDemoFinalAcceptanceShareDeliveryReceiptError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalAcceptanceShareFinalization();
      setDemoFinalAcceptanceShareFinalization(finalization);
      setDemoFinalAcceptanceShareFinalizationError(null);
    } catch (caught) {
      setDemoFinalAcceptanceShareFinalizationError(errorMessage(caught));
    }
    return receipt;
  }, []);
  const handleDownloadDemoFinalAcceptanceShareDeliveryReceiptReport = useCallback((receiptId: string) => (
    downloadDemoFinalAcceptanceShareDeliveryReceiptReport(receiptId)
  ), []);
  const handleCreateDemoFinalAcceptanceCompletionEvidenceDeliveryReceipt = useCallback(async (
    input: DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput
  ) => {
    const receipt = await createDemoFinalAcceptanceCompletionEvidenceDeliveryReceipt(input);
    setDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts((current) => [
      receipt,
      ...current.filter((item) => item.id !== receipt.id)
    ].slice(0, 20));
    setDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptError(null);
    try {
      const receipts = await listDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts();
      setDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts(receipts);
      setDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptError(errorMessage(caught));
    }
    try {
      const bundle = await getDemoFinalAcceptanceCompletionEvidenceBundle();
      setDemoFinalAcceptanceCompletionEvidenceBundle(bundle);
      setDemoFinalAcceptanceCompletionEvidenceBundleError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionEvidenceBundleError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization();
      setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization(finalization);
      setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationError(errorMessage(caught));
    }
    try {
      const closeout = await getDemoFinalAcceptanceCompletionCloseout();
      setDemoFinalAcceptanceCompletionCloseout(closeout);
      setDemoFinalAcceptanceCompletionCloseoutError(null);
    } catch (caught) {
      setDemoFinalAcceptanceCompletionCloseoutError(errorMessage(caught));
    }
    try {
      const evidencePackage = await getDemoFinalExternalReviewEvidencePackage();
      setDemoFinalExternalReviewEvidencePackage(evidencePackage);
      setDemoFinalExternalReviewEvidencePackageError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageError(errorMessage(caught));
    }
    return receipt;
  }, []);
  const handleDownloadDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptReport = useCallback((receiptId: string) => (
    downloadDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptReport(receiptId)
  ), []);
  const handleDownloadDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationReport = useCallback(() => (
    downloadDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationReport()
  ), []);
  const handleDownloadDemoFinalAcceptanceCompletionCloseoutReport = useCallback(() => (
    downloadDemoFinalAcceptanceCompletionCloseoutReport()
  ), []);
  const handleDownloadDemoFinalExternalReviewEvidencePackageReport = useCallback(() => (
    downloadDemoFinalExternalReviewEvidencePackageReport()
  ), []);
  const handleDownloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationReport = useCallback(() => (
    downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationReport()
  ), []);
  const handleDownloadDemoFinalExternalReviewDeliveryCertificateReport = useCallback(() => (
    downloadDemoFinalExternalReviewDeliveryCertificateReport()
  ), []);
  const handleDownloadDemoFinalExternalReviewReleaseBundleReport = useCallback(() => (
    downloadDemoFinalExternalReviewReleaseBundleReport()
  ), []);
  const handleArchiveDemoFinalExternalReviewReleaseBundle = useCallback(async () => {
    const archive = await archiveDemoFinalExternalReviewReleaseBundle();
    setDemoFinalExternalReviewReleaseBundleArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalExternalReviewReleaseBundleArchiveError(null);
    try {
      const archives = await listDemoFinalExternalReviewReleaseBundleArchives();
      setDemoFinalExternalReviewReleaseBundleArchives(archives);
      setDemoFinalExternalReviewReleaseBundleArchiveError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleArchiveError(errorMessage(caught));
    }
    try {
      const bundle = await getDemoFinalExternalReviewReleaseBundle();
      setDemoFinalExternalReviewReleaseBundle(bundle);
      setDemoFinalExternalReviewReleaseBundleError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleError(errorMessage(caught));
    }
    try {
      const evidenceBundle = await getDemoEvidenceBundle();
      setDemoEvidenceBundle(evidenceBundle);
      setDemoEvidenceBundleError(null);
    } catch (caught) {
      setDemoEvidenceBundleError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalExternalReviewReleaseBundleDeliveryFinalization();
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalization(finalization);
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalExternalReviewReleaseBundleArchiveReport = useCallback((
    archiveId: string
  ) => (
    downloadDemoFinalExternalReviewReleaseBundleArchiveReport(archiveId)
  ), []);
  const handleCreateDemoFinalExternalReviewReleaseBundleDeliveryReceipt = useCallback(async (
    input: DemoFinalExternalReviewReleaseBundleDeliveryReceiptInput
  ) => {
    const receipt = await createDemoFinalExternalReviewReleaseBundleDeliveryReceipt(input);
    setDemoFinalExternalReviewReleaseBundleDeliveryReceipts((current) => [
      receipt,
      ...current.filter((item) => item.id !== receipt.id)
    ].slice(0, 20));
    setDemoFinalExternalReviewReleaseBundleDeliveryReceiptError(null);
    try {
      const receipts = await listDemoFinalExternalReviewReleaseBundleDeliveryReceipts();
      setDemoFinalExternalReviewReleaseBundleDeliveryReceipts(receipts);
      setDemoFinalExternalReviewReleaseBundleDeliveryReceiptError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleDeliveryReceiptError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalExternalReviewReleaseBundleDeliveryFinalization();
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalization(finalization);
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationError(errorMessage(caught));
    }
    try {
      const bundle = await getDemoEvidenceBundle();
      setDemoEvidenceBundle(bundle);
      setDemoEvidenceBundleError(null);
    } catch (caught) {
      setDemoEvidenceBundleError(errorMessage(caught));
    }
    return receipt;
  }, []);
  const handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryReceiptReport = useCallback((
    receiptId: string
  ) => (
    downloadDemoFinalExternalReviewReleaseBundleDeliveryReceiptReport(receiptId)
  ), []);
  const handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationReport = useCallback(() => (
    downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationReport()
  ), []);
  const handleArchiveDemoFinalExternalReviewReleaseBundleDeliveryFinalization = useCallback(async () => {
    const archive = await archiveDemoFinalExternalReviewReleaseBundleDeliveryFinalization();
    setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveError(null);
    try {
      const archives = await listDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives();
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives(archives);
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalExternalReviewReleaseBundleDeliveryFinalization();
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalization(finalization);
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleDeliveryFinalizationError(errorMessage(caught));
    }
    try {
      const certificate = await getDemoFinalExternalReviewReleaseBundleDeliveryCertificate();
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificate(certificate);
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateError(errorMessage(caught));
    }
    try {
      const evidenceBundle = await getDemoEvidenceBundle();
      setDemoEvidenceBundle(evidenceBundle);
      setDemoEvidenceBundleError(null);
    } catch (caught) {
      setDemoEvidenceBundleError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport = useCallback((
    archiveId: string
  ) => (
    downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport(archiveId)
  ), []);
  const handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateReport = useCallback(() => (
    downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateReport()
  ), []);
  const handleDownloadDemoFinalReviewerHandoffPackageReport = useCallback(() => (
    downloadDemoFinalReviewerHandoffPackageReport()
  ), []);
  const handleCreateDemoFinalReviewerHandoffDeliveryReceipt = useCallback(async (
    input: DemoFinalReviewerHandoffDeliveryReceiptInput
  ) => {
    const receipt = await createDemoFinalReviewerHandoffDeliveryReceipt(input);
    setDemoFinalReviewerHandoffDeliveryReceipts((current) => [
      receipt,
      ...current.filter((item) => item.id !== receipt.id)
    ].slice(0, 20));
    setDemoFinalReviewerHandoffDeliveryReceiptError(null);
    try {
      const receipts = await listDemoFinalReviewerHandoffDeliveryReceipts();
      setDemoFinalReviewerHandoffDeliveryReceipts(receipts);
      setDemoFinalReviewerHandoffDeliveryReceiptError(null);
    } catch (caught) {
      setDemoFinalReviewerHandoffDeliveryReceiptError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalReviewerHandoffDeliveryFinalization();
      setDemoFinalReviewerHandoffDeliveryFinalization(finalization);
      setDemoFinalReviewerHandoffDeliveryFinalizationError(null);
    } catch (caught) {
      setDemoFinalReviewerHandoffDeliveryFinalizationError(errorMessage(caught));
    }
    try {
      const evidenceBundle = await getDemoEvidenceBundle();
      setDemoEvidenceBundle(evidenceBundle);
      setDemoEvidenceBundleError(null);
    } catch (caught) {
      setDemoEvidenceBundleError(errorMessage(caught));
    }
    return receipt;
  }, []);
  const handleDownloadDemoFinalReviewerHandoffDeliveryReceiptReport = useCallback((
    receiptId: string
  ) => (
    downloadDemoFinalReviewerHandoffDeliveryReceiptReport(receiptId)
  ), []);
  const handleDownloadDemoFinalReviewerHandoffDeliveryFinalizationReport = useCallback(() => (
    downloadDemoFinalReviewerHandoffDeliveryFinalizationReport()
  ), []);
  const handleArchiveDemoFinalExternalReviewReleaseBundleDeliveryCertificate = useCallback(async () => {
    const archive = await archiveDemoFinalExternalReviewReleaseBundleDeliveryCertificate();
    setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveError(null);
    try {
      const archives = await listDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives();
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives(archives);
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveError(errorMessage(caught));
    }
    try {
      const certificate = await getDemoFinalExternalReviewReleaseBundleDeliveryCertificate();
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificate(certificate);
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleDeliveryCertificateError(errorMessage(caught));
    }
    try {
      const evidenceBundle = await getDemoEvidenceBundle();
      setDemoEvidenceBundle(evidenceBundle);
      setDemoEvidenceBundleError(null);
    } catch (caught) {
      setDemoEvidenceBundleError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveReport = useCallback((
    archiveId: string
  ) => (
    downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveReport(archiveId)
  ), []);
  const handleArchiveDemoFinalExternalReviewDeliveryCertificate = useCallback(async () => {
    const archive = await archiveDemoFinalExternalReviewDeliveryCertificate();
    setDemoFinalExternalReviewDeliveryCertificateArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalExternalReviewDeliveryCertificateArchiveError(null);
    try {
      const archives = await listDemoFinalExternalReviewDeliveryCertificateArchives();
      setDemoFinalExternalReviewDeliveryCertificateArchives(archives);
      setDemoFinalExternalReviewDeliveryCertificateArchiveError(null);
    } catch (caught) {
      setDemoFinalExternalReviewDeliveryCertificateArchiveError(errorMessage(caught));
    }
    try {
      const certificate = await getDemoFinalExternalReviewDeliveryCertificate();
      setDemoFinalExternalReviewDeliveryCertificate(certificate);
      setDemoFinalExternalReviewDeliveryCertificateError(null);
    } catch (caught) {
      setDemoFinalExternalReviewDeliveryCertificateError(errorMessage(caught));
    }
    try {
      const releaseBundle = await getDemoFinalExternalReviewReleaseBundle();
      setDemoFinalExternalReviewReleaseBundle(releaseBundle);
      setDemoFinalExternalReviewReleaseBundleError(null);
    } catch (caught) {
      setDemoFinalExternalReviewReleaseBundleError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalExternalReviewDeliveryCertificateArchiveReport = useCallback((
    archiveId: string
  ) => (
    downloadDemoFinalExternalReviewDeliveryCertificateArchiveReport(archiveId)
  ), []);
  const handleArchiveDemoFinalExternalReviewEvidencePackage = useCallback(async () => {
    const archive = await archiveDemoFinalExternalReviewEvidencePackage();
    setDemoFinalExternalReviewEvidencePackageArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalExternalReviewEvidencePackageArchiveError(null);
    try {
      const archives = await listDemoFinalExternalReviewEvidencePackageArchives();
      setDemoFinalExternalReviewEvidencePackageArchives(archives);
      setDemoFinalExternalReviewEvidencePackageArchiveError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageArchiveError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalExternalReviewEvidencePackageDeliveryFinalization();
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalization(finalization);
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationError(errorMessage(caught));
    }
    try {
      const bundle = await getDemoEvidenceBundle();
      setDemoEvidenceBundle(bundle);
      setDemoEvidenceBundleError(null);
    } catch (caught) {
      setDemoEvidenceBundleError(errorMessage(caught));
    }
    try {
      const certificate = await getDemoFinalExternalReviewDeliveryCertificate();
      setDemoFinalExternalReviewDeliveryCertificate(certificate);
      setDemoFinalExternalReviewDeliveryCertificateError(null);
    } catch (caught) {
      setDemoFinalExternalReviewDeliveryCertificateError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalExternalReviewEvidencePackageArchiveReport = useCallback((archiveId: string) => (
    downloadDemoFinalExternalReviewEvidencePackageArchiveReport(archiveId)
  ), []);
  const handleCreateDemoFinalExternalReviewEvidencePackageDeliveryReceipt = useCallback(async (
    input: DemoFinalExternalReviewEvidencePackageDeliveryReceiptInput
  ) => {
    const receipt = await createDemoFinalExternalReviewEvidencePackageDeliveryReceipt(input);
    setDemoFinalExternalReviewEvidencePackageDeliveryReceipts((current) => [
      receipt,
      ...current.filter((item) => item.id !== receipt.id)
    ].slice(0, 20));
    setDemoFinalExternalReviewEvidencePackageDeliveryReceiptError(null);
    try {
      const receipts = await listDemoFinalExternalReviewEvidencePackageDeliveryReceipts();
      setDemoFinalExternalReviewEvidencePackageDeliveryReceipts(receipts);
      setDemoFinalExternalReviewEvidencePackageDeliveryReceiptError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageDeliveryReceiptError(errorMessage(caught));
    }
    try {
      const evidencePackage = await getDemoFinalExternalReviewEvidencePackage();
      setDemoFinalExternalReviewEvidencePackage(evidencePackage);
      setDemoFinalExternalReviewEvidencePackageError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoFinalExternalReviewEvidencePackageDeliveryFinalization();
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalization(finalization);
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationError(errorMessage(caught));
    }
    try {
      const bundle = await getDemoEvidenceBundle();
      setDemoEvidenceBundle(bundle);
      setDemoEvidenceBundleError(null);
    } catch (caught) {
      setDemoEvidenceBundleError(errorMessage(caught));
    }
    return receipt;
  }, []);
  const handleDownloadDemoFinalExternalReviewEvidencePackageDeliveryReceiptReport = useCallback((receiptId: string) => (
    downloadDemoFinalExternalReviewEvidencePackageDeliveryReceiptReport(receiptId)
  ), []);
  const handleArchiveDemoFinalExternalReviewEvidencePackageDeliveryFinalization = useCallback(async () => {
    const archive = await archiveDemoFinalExternalReviewEvidencePackageDeliveryFinalization();
    setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives((current) => [
      archive,
      ...current.filter((item) => item.id !== archive.id)
    ].slice(0, 20));
    setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveError(null);
    try {
      const archives = await listDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives();
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives(archives);
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveError(null);
    } catch (caught) {
      setDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveError(errorMessage(caught));
    }
    try {
      const bundle = await getDemoEvidenceBundle();
      setDemoEvidenceBundle(bundle);
      setDemoEvidenceBundleError(null);
    } catch (caught) {
      setDemoEvidenceBundleError(errorMessage(caught));
    }
    return archive;
  }, []);
  const handleDownloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport = useCallback((
    archiveId: string
  ) => (
    downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport(archiveId)
  ), []);
  const handleCreateDemoLaunchEvidenceDeliveryReceipt = useCallback(async (
    input: DemoLaunchEvidenceShareDeliveryReceiptInput
  ) => {
    const receipt = await createDemoLaunchEvidenceShareDeliveryReceipt(input);
    setDemoLaunchEvidenceDeliveryReceipts((current) => [receipt, ...current.filter((item) => item.id !== receipt.id)].slice(0, 20));
    setDemoLaunchEvidenceDeliveryReceiptError(null);
    try {
      const receipts = await listDemoLaunchEvidenceShareDeliveryReceipts();
      setDemoLaunchEvidenceDeliveryReceipts(receipts);
      setDemoLaunchEvidenceDeliveryReceiptError(null);
    } catch (caught) {
      setDemoLaunchEvidenceDeliveryReceiptError(errorMessage(caught));
    }
    try {
      const center = await getDemoLaunchEvidenceShareCenter();
      setDemoLaunchEvidenceShareCenter(center);
      setDemoLaunchEvidenceShareCenterError(null);
    } catch (caught) {
      setDemoLaunchEvidenceShareCenterError(errorMessage(caught));
    }
    try {
      const finalization = await getDemoLaunchEvidenceFinalization();
      setDemoLaunchEvidenceFinalization(finalization);
      setDemoLaunchEvidenceFinalizationError(null);
    } catch (caught) {
      setDemoLaunchEvidenceFinalizationError(errorMessage(caught));
    }
    try {
      const closeout = await getDemoLaunchAcceptanceCloseout();
      setDemoLaunchAcceptanceCloseout(closeout);
      setDemoLaunchAcceptanceCloseoutError(null);
    } catch (caught) {
      setDemoLaunchAcceptanceCloseoutError(errorMessage(caught));
    }
    return receipt;
  }, []);
  const handleDownloadDemoLaunchEvidenceDeliveryReceiptReport = useCallback((receiptId: string) => (
    downloadDemoLaunchEvidenceShareDeliveryReceiptReport(receiptId)
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
      const reportPackage = await getDemoFinalHandoffReportPackage();
      setDemoFinalHandoffReportPackage(reportPackage);
      setDemoFinalHandoffReportPackageError(null);
    } catch (caught) {
      setDemoFinalHandoffReportPackageError(errorMessage(caught));
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
    try {
      const reportPackage = await getDemoFinalHandoffReportPackage();
      setDemoFinalHandoffReportPackage(reportPackage);
      setDemoFinalHandoffReportPackageError(null);
    } catch (caught) {
      setDemoFinalHandoffReportPackageError(errorMessage(caught));
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

  const handleGitHubTriggerDryRun = useCallback(async (input: GitHubTriggerDryRunInput) => {
    setGitHubTriggerDryRunPending(true);
    setGitHubTriggerDryRunError(null);
    try {
      const result = await postGitHubTriggerDryRun(input);
      setGitHubTriggerDryRun(result);
      return result;
    } catch (caught) {
      setGitHubTriggerDryRunError(errorMessage(caught));
      throw caught;
    } finally {
      setGitHubTriggerDryRunPending(false);
    }
  }, []);

  const clearDemoLiveDemoArtifactChainReport = useCallback(() => {
    setDemoLiveDemoArtifactChainReport(null);
    setDemoLiveDemoArtifactChainReportError(null);
    setDemoLiveDemoReplayPackage(null);
    setDemoLiveDemoReplayPackageError(null);
    setDemoLiveDemoReviewerDeliveryCenter(null);
    setDemoLiveDemoReviewerDeliveryCenterError(null);
  }, []);

  const handleDemoLiveLaunchGate = useCallback(async (input: GitHubTriggerDryRunInput) => {
    setDemoLiveLaunchGatePending(true);
    setDemoLiveLaunchGateError(null);
    setDemoLiveTriggerLaunchPackage(null);
    setDemoLiveTriggerLaunchPackageError(null);
    setDemoLiveTriggerOutcomeCloseout(null);
    setDemoLiveTriggerOutcomeCloseoutError(null);
    setDemoLiveDemoEvidenceBundle(null);
    setDemoLiveDemoEvidenceBundleError(null);
    setDemoLiveDemoEvidenceBundleArchiveError(null);
    setDemoLiveDemoHandoffPackage(null);
    setDemoLiveDemoHandoffPackageError(null);
    setDemoLiveDemoHandoffDeliveryReceiptError(null);
    setDemoLiveDemoHandoffDeliveryFinalization(null);
    setDemoLiveDemoHandoffDeliveryFinalizationError(null);
    setDemoLiveDemoHandoffDeliveryFinalizationArchiveError(null);
    setDemoLiveDemoCompletionCertificate(null);
    setDemoLiveDemoCompletionCertificateError(null);
    setDemoLiveDemoCompletionCertificateArchiveError(null);
    clearDemoLiveDemoArtifactChainReport();
    try {
      const result = await postDemoLiveLaunchGate(input);
      setDemoLiveLaunchGate(result);
      return result;
    } catch (caught) {
      setDemoLiveLaunchGateError(errorMessage(caught));
      throw caught;
    } finally {
      setDemoLiveLaunchGatePending(false);
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDemoLiveTriggerLaunchPackage = useCallback(async (input: GitHubTriggerDryRunInput) => {
    setDemoLiveTriggerLaunchPackagePending(true);
    setDemoLiveTriggerLaunchPackageError(null);
    setDemoLiveTriggerOutcomeCloseout(null);
    setDemoLiveTriggerOutcomeCloseoutError(null);
    setDemoLiveDemoEvidenceBundle(null);
    setDemoLiveDemoEvidenceBundleError(null);
    setDemoLiveDemoEvidenceBundleArchiveError(null);
    setDemoLiveDemoHandoffPackage(null);
    setDemoLiveDemoHandoffPackageError(null);
    setDemoLiveDemoHandoffDeliveryReceiptError(null);
    setDemoLiveDemoHandoffDeliveryFinalization(null);
    setDemoLiveDemoHandoffDeliveryFinalizationError(null);
    setDemoLiveDemoHandoffDeliveryFinalizationArchiveError(null);
    setDemoLiveDemoCompletionCertificate(null);
    setDemoLiveDemoCompletionCertificateError(null);
    setDemoLiveDemoCompletionCertificateArchiveError(null);
    try {
      const result = await postDemoLiveTriggerLaunchPackage(input);
      setDemoLiveTriggerLaunchPackage(result);
      return result;
    } catch (caught) {
      setDemoLiveTriggerLaunchPackageError(errorMessage(caught));
      throw caught;
    } finally {
      setDemoLiveTriggerLaunchPackagePending(false);
    }
  }, []);

  const handleArchiveDemoLiveTriggerLaunchPackage = useCallback(async (input: GitHubTriggerDryRunInput) => {
    setDemoLiveTriggerLaunchPackagePending(true);
    setDemoLiveTriggerLaunchPackageArchiveError(null);
    try {
      const archive = await archiveDemoLiveTriggerLaunchPackage(input);
      setDemoLiveTriggerLaunchPackageArchives((current) =>
        [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20)
      );
      setDemoLiveDemoEvidenceBundle(null);
      setDemoLiveDemoEvidenceBundleArchiveError(null);
      setDemoLiveDemoHandoffPackage(null);
      setDemoLiveDemoHandoffPackageError(null);
      setDemoLiveDemoHandoffDeliveryReceiptError(null);
      setDemoLiveDemoHandoffDeliveryFinalization(null);
      setDemoLiveDemoHandoffDeliveryFinalizationError(null);
      setDemoLiveDemoCompletionCertificate(null);
      setDemoLiveDemoCompletionCertificateError(null);
      setDemoLiveDemoCompletionCertificateArchiveError(null);
      clearDemoLiveDemoArtifactChainReport();
      return archive;
    } catch (caught) {
      setDemoLiveTriggerLaunchPackageArchiveError(errorMessage(caught));
      throw caught;
    } finally {
      setDemoLiveTriggerLaunchPackagePending(false);
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDownloadDemoLiveTriggerLaunchPackageArchiveReport = useCallback(
    (archiveId: string) => downloadDemoLiveTriggerLaunchPackageArchiveReport(archiveId),
    []
  );

  const handleDemoLiveTriggerOutcomeCloseout = useCallback(async (input: DemoLiveTriggerOutcomeCloseoutInput) => {
    setDemoLiveTriggerOutcomeCloseoutPending(true);
    setDemoLiveTriggerOutcomeCloseoutError(null);
    setDemoLiveTriggerOutcomeCloseoutArchiveError(null);
    try {
      const closeout = await postDemoLiveTriggerOutcomeCloseout(input);
      setDemoLiveTriggerOutcomeCloseout(closeout);
      return closeout;
    } catch (caught) {
      setDemoLiveTriggerOutcomeCloseoutError(errorMessage(caught));
      throw caught;
    } finally {
      setDemoLiveTriggerOutcomeCloseoutPending(false);
    }
  }, []);

  const handleDownloadDemoLiveTriggerOutcomeCloseoutReport = useCallback(
    (input: DemoLiveTriggerOutcomeCloseoutInput) => downloadDemoLiveTriggerOutcomeCloseoutReport(input),
    []
  );

  const handleArchiveDemoLiveTriggerOutcomeCloseout = useCallback(async (input: DemoLiveTriggerOutcomeCloseoutInput) => {
    setDemoLiveTriggerOutcomeCloseoutPending(true);
    setDemoLiveTriggerOutcomeCloseoutArchiveError(null);
    try {
      const archive = await archiveDemoLiveTriggerOutcomeCloseout(input);
      setDemoLiveTriggerOutcomeCloseoutArchives((current) =>
        [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20)
      );
      setDemoLiveDemoEvidenceBundleError(null);
      const bundle = await getDemoLiveDemoEvidenceBundle();
      setDemoLiveDemoEvidenceBundle(bundle);
      setDemoLiveDemoEvidenceBundleArchiveError(null);
      setDemoLiveDemoHandoffPackage(null);
      setDemoLiveDemoHandoffPackageError(null);
      setDemoLiveDemoHandoffDeliveryReceiptError(null);
      setDemoLiveDemoHandoffDeliveryFinalization(null);
      setDemoLiveDemoHandoffDeliveryFinalizationError(null);
      setDemoLiveDemoCompletionCertificate(null);
      setDemoLiveDemoCompletionCertificateError(null);
      setDemoLiveDemoCompletionCertificateArchiveError(null);
      clearDemoLiveDemoArtifactChainReport();
      return archive;
    } catch (caught) {
      setDemoLiveTriggerOutcomeCloseoutArchiveError(errorMessage(caught));
      throw caught;
    } finally {
      setDemoLiveTriggerOutcomeCloseoutPending(false);
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDownloadDemoLiveTriggerOutcomeCloseoutArchiveReport = useCallback(
    (archiveId: string) => downloadDemoLiveTriggerOutcomeCloseoutArchiveReport(archiveId),
    []
  );

  const handleRefreshDemoLiveDemoEvidenceBundle = useCallback(async () => {
    setDemoLiveDemoEvidenceBundleError(null);
    try {
      const bundle = await getDemoLiveDemoEvidenceBundle();
      setDemoLiveDemoEvidenceBundle(bundle);
      setDemoLiveDemoHandoffPackage(null);
      setDemoLiveDemoHandoffPackageError(null);
      setDemoLiveDemoHandoffDeliveryReceiptError(null);
      setDemoLiveDemoHandoffDeliveryFinalization(null);
      setDemoLiveDemoHandoffDeliveryFinalizationError(null);
      setDemoLiveDemoCompletionCertificate(null);
      setDemoLiveDemoCompletionCertificateError(null);
      setDemoLiveDemoCompletionCertificateArchiveError(null);
      clearDemoLiveDemoArtifactChainReport();
      return bundle;
    } catch (caught) {
      setDemoLiveDemoEvidenceBundleError(errorMessage(caught));
      throw caught;
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDownloadDemoLiveDemoEvidenceBundleReport = useCallback(
    () => downloadDemoLiveDemoEvidenceBundleReport(),
    []
  );

  const handleArchiveDemoLiveDemoEvidenceBundle = useCallback(async () => {
    setDemoLiveDemoEvidenceBundleArchiveError(null);
    setDemoLiveDemoHandoffPackageError(null);
    setDemoLiveDemoHandoffDeliveryReceiptError(null);
    setDemoLiveDemoHandoffDeliveryFinalization(null);
    setDemoLiveDemoHandoffDeliveryFinalizationError(null);
    setDemoLiveDemoCompletionCertificate(null);
    setDemoLiveDemoCompletionCertificateError(null);
    setDemoLiveDemoCompletionCertificateArchiveError(null);
    clearDemoLiveDemoArtifactChainReport();
    try {
      const archive = await archiveDemoLiveDemoEvidenceBundle();
      setDemoLiveDemoEvidenceBundleArchives((current) =>
        [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20)
      );
      const handoffPackage = await getDemoLiveDemoHandoffPackage();
      setDemoLiveDemoHandoffPackage(handoffPackage);
      return archive;
    } catch (caught) {
      setDemoLiveDemoEvidenceBundleArchiveError(errorMessage(caught));
      throw caught;
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDownloadDemoLiveDemoEvidenceBundleArchiveReport = useCallback(
    (archiveId: string) => downloadDemoLiveDemoEvidenceBundleArchiveReport(archiveId),
    []
  );

  const handleRefreshDemoLiveDemoHandoffPackage = useCallback(async () => {
    setDemoLiveDemoHandoffPackageError(null);
    setDemoLiveDemoHandoffDeliveryReceiptError(null);
    setDemoLiveDemoHandoffDeliveryFinalization(null);
    setDemoLiveDemoHandoffDeliveryFinalizationError(null);
    setDemoLiveDemoCompletionCertificate(null);
    setDemoLiveDemoCompletionCertificateError(null);
    setDemoLiveDemoCompletionCertificateArchiveError(null);
    clearDemoLiveDemoArtifactChainReport();
    try {
      const handoffPackage = await getDemoLiveDemoHandoffPackage();
      setDemoLiveDemoHandoffPackage(handoffPackage);
      return handoffPackage;
    } catch (caught) {
      setDemoLiveDemoHandoffPackageError(errorMessage(caught));
      throw caught;
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDownloadDemoLiveDemoHandoffPackageReport = useCallback(
    () => downloadDemoLiveDemoHandoffPackageReport(),
    []
  );

  const handleRecordDemoLiveDemoHandoffDeliveryReceipt = useCallback(
    async (input: DemoLiveDemoHandoffDeliveryReceiptInput) => {
      setDemoLiveDemoHandoffDeliveryReceiptError(null);
      setDemoLiveDemoHandoffDeliveryFinalizationError(null);
      setDemoLiveDemoHandoffDeliveryFinalizationArchiveError(null);
      setDemoLiveDemoCompletionCertificate(null);
      setDemoLiveDemoCompletionCertificateError(null);
      setDemoLiveDemoCompletionCertificateArchiveError(null);
      clearDemoLiveDemoArtifactChainReport();
      try {
        const receipt = await createDemoLiveDemoHandoffDeliveryReceipt(input);
        setDemoLiveDemoHandoffDeliveryReceipts((current) =>
          [receipt, ...current.filter((item) => item.id !== receipt.id)].slice(0, 20)
        );
        const finalization = await getDemoLiveDemoHandoffDeliveryFinalization();
        setDemoLiveDemoHandoffDeliveryFinalization(finalization);
        return receipt;
      } catch (caught) {
        setDemoLiveDemoHandoffDeliveryReceiptError(errorMessage(caught));
        throw caught;
      }
    },
    [clearDemoLiveDemoArtifactChainReport]
  );

  const handleDownloadDemoLiveDemoHandoffDeliveryReceiptReport = useCallback(
    (receiptId: string) => downloadDemoLiveDemoHandoffDeliveryReceiptReport(receiptId),
    []
  );

  const handleRefreshDemoLiveDemoHandoffDeliveryFinalization = useCallback(async () => {
    setDemoLiveDemoHandoffDeliveryFinalizationError(null);
    setDemoLiveDemoHandoffDeliveryFinalizationArchiveError(null);
    setDemoLiveDemoCompletionCertificate(null);
    setDemoLiveDemoCompletionCertificateError(null);
    setDemoLiveDemoCompletionCertificateArchiveError(null);
    clearDemoLiveDemoArtifactChainReport();
    try {
      const finalization = await getDemoLiveDemoHandoffDeliveryFinalization();
      setDemoLiveDemoHandoffDeliveryFinalization(finalization);
      return finalization;
    } catch (caught) {
      setDemoLiveDemoHandoffDeliveryFinalizationError(errorMessage(caught));
      throw caught;
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDownloadDemoLiveDemoHandoffDeliveryFinalizationReport = useCallback(
    () => downloadDemoLiveDemoHandoffDeliveryFinalizationReport(),
    []
  );

  const handleArchiveDemoLiveDemoHandoffDeliveryFinalization = useCallback(async () => {
    setDemoLiveDemoHandoffDeliveryFinalizationArchiveError(null);
    try {
      const archive = await archiveDemoLiveDemoHandoffDeliveryFinalization();
      setDemoLiveDemoHandoffDeliveryFinalizationArchives((current) =>
        [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20)
      );
      setDemoLiveDemoCompletionCertificateError(null);
      const certificate = await getDemoLiveDemoCompletionCertificate();
      setDemoLiveDemoCompletionCertificate(certificate);
      clearDemoLiveDemoArtifactChainReport();
      return archive;
    } catch (caught) {
      setDemoLiveDemoHandoffDeliveryFinalizationArchiveError(errorMessage(caught));
      throw caught;
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDownloadDemoLiveDemoHandoffDeliveryFinalizationArchiveReport = useCallback(
    (archiveId: string) => downloadDemoLiveDemoHandoffDeliveryFinalizationArchiveReport(archiveId),
    []
  );

  const handleRefreshDemoLiveDemoCompletionCertificate = useCallback(async () => {
    setDemoLiveDemoCompletionCertificateError(null);
    setDemoLiveDemoCompletionCertificateArchiveError(null);
    clearDemoLiveDemoArtifactChainReport();
    try {
      const certificate = await getDemoLiveDemoCompletionCertificate();
      setDemoLiveDemoCompletionCertificate(certificate);
      clearDemoLiveDemoArtifactChainReport();
      return certificate;
    } catch (caught) {
      setDemoLiveDemoCompletionCertificateError(errorMessage(caught));
      throw caught;
    }
  }, [clearDemoLiveDemoArtifactChainReport]);

  const handleDownloadDemoLiveDemoCompletionCertificateReport = useCallback(
    () => downloadDemoLiveDemoCompletionCertificateReport(),
    []
  );

  const handleArchiveDemoLiveDemoCompletionCertificate = useCallback(async () => {
    setDemoLiveDemoCompletionCertificateArchiveError(null);
    try {
      const archive = await archiveDemoLiveDemoCompletionCertificate();
      setDemoLiveDemoCompletionCertificateArchives((current) =>
        [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20)
      );
      setDemoLiveDemoArtifactChainReportError(null);
      const report = await getDemoLiveDemoArtifactChainReport();
      setDemoLiveDemoArtifactChainReport(report);
      setDemoLiveDemoReplayPackageError(null);
      const replayPackage = await getDemoLiveDemoReplayPackage();
      setDemoLiveDemoReplayPackage(replayPackage);
      setDemoLiveDemoReviewerDeliveryCenterError(null);
      const deliveryCenter = await getDemoLiveDemoReviewerDeliveryCenter();
      setDemoLiveDemoReviewerDeliveryCenter(deliveryCenter);
      return archive;
    } catch (caught) {
      setDemoLiveDemoCompletionCertificateArchiveError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadDemoLiveDemoCompletionCertificateArchiveReport = useCallback(
    (archiveId: string) => downloadDemoLiveDemoCompletionCertificateArchiveReport(archiveId),
    []
  );

  const handleRefreshDemoLiveDemoArtifactChainReport = useCallback(async () => {
    setDemoLiveDemoArtifactChainReportError(null);
    setDemoLiveDemoReplayPackage(null);
    setDemoLiveDemoReplayPackageError(null);
    setDemoLiveDemoReviewerDeliveryCenter(null);
    setDemoLiveDemoReviewerDeliveryCenterError(null);
    try {
      const report = await getDemoLiveDemoArtifactChainReport();
      setDemoLiveDemoArtifactChainReport(report);
      return report;
    } catch (caught) {
      setDemoLiveDemoArtifactChainReportError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadDemoLiveDemoArtifactChainReport = useCallback(
    () => downloadDemoLiveDemoArtifactChainReport(),
    []
  );

  const handleRefreshDemoLiveDemoReplayPackage = useCallback(async () => {
    setDemoLiveDemoReplayPackageError(null);
    setDemoLiveDemoReviewerDeliveryCenter(null);
    setDemoLiveDemoReviewerDeliveryCenterError(null);
    try {
      const replayPackage = await getDemoLiveDemoReplayPackage();
      setDemoLiveDemoReplayPackage(replayPackage);
      return replayPackage;
    } catch (caught) {
      setDemoLiveDemoReplayPackageError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadDemoLiveDemoReplayPackage = useCallback(
    () => downloadDemoLiveDemoReplayPackage(),
    []
  );

  const handleRefreshDemoLiveDemoReviewerDeliveryCenter = useCallback(async () => {
    setDemoLiveDemoReviewerDeliveryCenterError(null);
    try {
      const deliveryCenter = await getDemoLiveDemoReviewerDeliveryCenter();
      setDemoLiveDemoReviewerDeliveryCenter(deliveryCenter);
      return deliveryCenter;
    } catch (caught) {
      setDemoLiveDemoReviewerDeliveryCenterError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadDemoLiveDemoReviewerDeliveryCenter = useCallback(
    () => downloadDemoLiveDemoReviewerDeliveryCenter(),
    []
  );

  const handleArchiveDemoLiveDemoReviewerDeliveryCenter = useCallback(async () => {
    setDemoLiveDemoReviewerDeliveryCenterArchiveError(null);
    setDemoLiveDemoReviewerDeliveryCenterError(null);
    setDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptError(null);
    try {
      const archive = await archiveDemoLiveDemoReviewerDeliveryCenter();
      const [archives, deliveryCenter, receipts] = await Promise.all([
        listDemoLiveDemoReviewerDeliveryCenterArchives(),
        getDemoLiveDemoReviewerDeliveryCenter(),
        listDemoLiveDemoReviewerDeliveryCenterDeliveryReceipts()
      ]);
      setDemoLiveDemoReviewerDeliveryCenterArchives(archives);
      setDemoLiveDemoReviewerDeliveryCenter(deliveryCenter);
      setDemoLiveDemoReviewerDeliveryCenterDeliveryReceipts(receipts);
      return archive;
    } catch (caught) {
      setDemoLiveDemoReviewerDeliveryCenterArchiveError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadDemoLiveDemoReviewerDeliveryCenterArchiveReport = useCallback(
    (archiveId: string) => downloadDemoLiveDemoReviewerDeliveryCenterArchiveReport(archiveId),
    []
  );

  const handleRecordDemoLiveDemoReviewerDeliveryCenterDeliveryReceipt = useCallback(
    async (input: DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptInput) => {
      setDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptError(null);
      try {
        const receipt = await createDemoLiveDemoReviewerDeliveryCenterDeliveryReceipt(input);
        const receipts = await listDemoLiveDemoReviewerDeliveryCenterDeliveryReceipts();
        setDemoLiveDemoReviewerDeliveryCenterDeliveryReceipts(receipts);
        return receipt;
      } catch (caught) {
        setDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptError(errorMessage(caught));
        throw caught;
      }
    },
    []
  );

  const handleDownloadDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptReport = useCallback(
    (receiptId: string) => downloadDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptReport(receiptId),
    []
  );

  const handleRefreshEndToEndAcceptanceMatrix = useCallback(async () => {
    setDemoEndToEndAcceptanceMatrixError(null);
    try {
      const matrix = await getDemoEndToEndAcceptanceMatrix();
      setDemoEndToEndAcceptanceMatrix(matrix);
    } catch (caught) {
      setDemoEndToEndAcceptanceMatrixError(errorMessage(caught));
    }
  }, []);

  const handleRefreshExternalExposureReadiness = useCallback(async () => {
    setExternalExposureReadinessError(null);
    setExternalExposureReadinessArchiveError(null);
    setExternalExposureHandoffPackageError(null);
    setExternalExposureCloseoutError(null);
    setExternalExposureCloseoutArchiveError(null);
    setExternalExposureOperatorHandoffChecklistError(null);
    setExternalExposureOperatorHandoffChecklistArchiveError(null);
    try {
      const readiness = await getExternalExposureReadiness();
      setExternalExposureReadiness(readiness);
      const readinessArchives = await listExternalExposureReadinessArchives();
      setExternalExposureReadinessArchives(readinessArchives);
      const handoffPackage = await getExternalExposureHandoffPackage();
      setExternalExposureHandoffPackage(handoffPackage);
      const closeout = await getExternalExposureCloseout();
      setExternalExposureCloseout(closeout);
      const closeoutArchives = await listExternalExposureCloseoutArchives();
      setExternalExposureCloseoutArchives(closeoutArchives);
      const checklist = await getExternalExposureOperatorHandoffChecklist();
      setExternalExposureOperatorHandoffChecklist(checklist);
      const checklistArchives = await listExternalExposureOperatorHandoffChecklistArchives();
      setExternalExposureOperatorHandoffChecklistArchives(checklistArchives);
    } catch (caught) {
      setExternalExposureReadinessError(errorMessage(caught));
      setExternalExposureCloseoutError(errorMessage(caught));
      setExternalExposureOperatorHandoffChecklistError(errorMessage(caught));
      setExternalExposureOperatorHandoffChecklistArchiveError(errorMessage(caught));
    }
  }, []);

  const handleRefreshExternalExposureOperatorHandoffChecklist = useCallback(async () => {
    setExternalExposureOperatorHandoffChecklistError(null);
    setExternalExposureOperatorHandoffChecklistArchiveError(null);
    try {
      const checklist = await getExternalExposureOperatorHandoffChecklist();
      setExternalExposureOperatorHandoffChecklist(checklist);
      const archives = await listExternalExposureOperatorHandoffChecklistArchives();
      setExternalExposureOperatorHandoffChecklistArchives(archives);
    } catch (caught) {
      setExternalExposureOperatorHandoffChecklistError(errorMessage(caught));
      setExternalExposureOperatorHandoffChecklistArchiveError(errorMessage(caught));
    }
  }, []);

  const handleArchiveExternalExposureReadiness = useCallback(async () => {
    setExternalExposureReadinessArchiveError(null);
    setExternalExposureHandoffPackageError(null);
    setExternalExposureCloseoutError(null);
    try {
      const archive = await archiveExternalExposureReadiness();
      setExternalExposureReadinessArchives((current) =>
        [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20)
      );
      const handoffPackage = await getExternalExposureHandoffPackage();
      setExternalExposureHandoffPackage(handoffPackage);
      const closeout = await getExternalExposureCloseout();
      setExternalExposureCloseout(closeout);
      return archive;
    } catch (caught) {
      setExternalExposureReadinessArchiveError(errorMessage(caught));
      setExternalExposureCloseoutError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadExternalExposureReadinessArchiveReport = useCallback(
    (archiveId: string) => downloadExternalExposureReadinessArchiveReport(archiveId),
    []
  );

  const handleDownloadExternalExposureHandoffPackageReport = useCallback(
    () => downloadExternalExposureHandoffPackageReport(),
    []
  );

  const handleDownloadExternalExposureCloseoutReport = useCallback(
    () => downloadExternalExposureCloseoutReport(),
    []
  );

  const handleArchiveExternalExposureCloseout = useCallback(async () => {
    setExternalExposureCloseoutArchiveError(null);
    try {
      const archive = await archiveExternalExposureCloseout();
      setExternalExposureCloseoutArchives((current) =>
        [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20)
      );
      return archive;
    } catch (caught) {
      setExternalExposureCloseoutArchiveError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadExternalExposureCloseoutArchiveReport = useCallback(
    (archiveId: string) => downloadExternalExposureCloseoutArchiveReport(archiveId),
    []
  );

  const handleDownloadExternalExposureOperatorHandoffChecklistReport = useCallback(
    () => downloadExternalExposureOperatorHandoffChecklistReport(),
    []
  );

  const handleArchiveExternalExposureOperatorHandoffChecklist = useCallback(async () => {
    setExternalExposureOperatorHandoffChecklistArchiveError(null);
    try {
      const archive = await archiveExternalExposureOperatorHandoffChecklist();
      setExternalExposureOperatorHandoffChecklistArchives((current) =>
        [archive, ...current.filter((item) => item.id !== archive.id)].slice(0, 20)
      );
      return archive;
    } catch (caught) {
      setExternalExposureOperatorHandoffChecklistArchiveError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadExternalExposureOperatorHandoffChecklistArchiveReport = useCallback(
    (archiveId: string) => downloadExternalExposureOperatorHandoffChecklistArchiveReport(archiveId),
    []
  );

  const handleStartExternalExposureSession = useCallback(async (input: ExternalExposureSessionInput) => {
    setExternalExposureSessionError(null);
    setExternalExposureCloseoutError(null);
    try {
      const session = await startExternalExposureSession(input);
      setExternalExposureSessions((current) =>
        [session, ...current.filter((item) => item.id !== session.id)].slice(0, 50)
      );
      try {
        const closeout = await getExternalExposureCloseout();
        setExternalExposureCloseout(closeout);
      } catch (closeoutCaught) {
        setExternalExposureCloseoutError(errorMessage(closeoutCaught));
      }
      return session;
    } catch (caught) {
      setExternalExposureSessionError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleCloseExternalExposureSession = useCallback(async (
    sessionId: string,
    input: ExternalExposureSessionCloseInput
  ) => {
    setExternalExposureSessionError(null);
    setExternalExposureCloseoutError(null);
    try {
      const session = await closeExternalExposureSession(sessionId, input);
      setExternalExposureSessions((current) =>
        [session, ...current.filter((item) => item.id !== session.id)].slice(0, 50)
      );
      try {
        const closeout = await getExternalExposureCloseout();
        setExternalExposureCloseout(closeout);
      } catch (closeoutCaught) {
        setExternalExposureCloseoutError(errorMessage(closeoutCaught));
      }
      return session;
    } catch (caught) {
      setExternalExposureSessionError(errorMessage(caught));
      throw caught;
    }
  }, []);

  const handleDownloadExternalExposureSessionReport = useCallback(
    (sessionId: string) => downloadExternalExposureSessionReport(sessionId),
    []
  );

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
        githubLivePublishPreflight={githubLivePublishPreflight}
        githubPublishPermissionReadiness={githubPublishPermissionReadiness}
        githubPublishReadiness={githubPublishReadiness}
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

      <DemoLaunchEvidencePackagePanel
        evidencePackage={demoLaunchEvidencePackage}
        error={demoLaunchEvidencePackageError}
        archives={demoLaunchEvidencePackageArchives}
        archiveError={demoLaunchEvidencePackageArchiveError}
        shareCenter={demoLaunchEvidenceShareCenter}
        shareCenterError={demoLaunchEvidenceShareCenterError}
        finalization={demoLaunchEvidenceFinalization}
        finalizationError={demoLaunchEvidenceFinalizationError}
        closeout={demoLaunchAcceptanceCloseout}
        closeoutError={demoLaunchAcceptanceCloseoutError}
        closeoutArchives={demoLaunchAcceptanceCloseoutArchives}
        closeoutArchiveError={demoLaunchAcceptanceCloseoutArchiveError}
        certificate={demoLaunchAcceptanceCertificate}
        certificateError={demoLaunchAcceptanceCertificateError}
        certificateArchives={demoLaunchAcceptanceCertificateArchives}
        certificateArchiveError={demoLaunchAcceptanceCertificateArchiveError}
        deliveryReceipts={demoLaunchEvidenceDeliveryReceipts}
        deliveryReceiptError={demoLaunchEvidenceDeliveryReceiptError}
        onArchivePackage={handleArchiveDemoLaunchEvidencePackage}
        onDownloadReport={handleDownloadDemoLaunchEvidencePackageReport}
        onDownloadArchiveReport={handleDownloadDemoLaunchEvidencePackageArchiveReport}
        onDownloadShareCenterReport={handleDownloadDemoLaunchEvidenceShareCenterReport}
        onDownloadFinalizationReport={handleDownloadDemoLaunchEvidenceFinalizationReport}
        onDownloadCloseoutReport={handleDownloadDemoLaunchAcceptanceCloseoutReport}
        onArchiveCloseout={handleArchiveDemoLaunchAcceptanceCloseout}
        onDownloadCloseoutArchiveReport={handleDownloadDemoLaunchAcceptanceCloseoutArchiveReport}
        onDownloadCertificateReport={handleDownloadDemoLaunchAcceptanceCertificateReport}
        onArchiveCertificate={handleArchiveDemoLaunchAcceptanceCertificate}
        onDownloadCertificateArchiveReport={handleDownloadDemoLaunchAcceptanceCertificateArchiveReport}
        onCreateDeliveryReceipt={handleCreateDemoLaunchEvidenceDeliveryReceipt}
        onDownloadDeliveryReceiptReport={handleDownloadDemoLaunchEvidenceDeliveryReceiptReport}
      />

      <DemoEvidenceBundlePanel
        bundle={demoEvidenceBundle}
        error={demoEvidenceBundleError}
        onCopyRunbook={handleCopyDemoRunbook}
        onDownloadFinalReviewerHandoffPackageReport={handleDownloadDemoFinalReviewerHandoffPackageReport}
      />

      <DemoAcceptanceSummaryPanel
        summary={demoAcceptanceSummary}
        sharePackage={demoFinalAcceptanceSharePackage}
        sharePackageArchives={demoFinalAcceptanceSharePackageArchives}
        shareDeliveryReceipts={demoFinalAcceptanceShareDeliveryReceipts}
        shareFinalization={demoFinalAcceptanceShareFinalization}
        completionEvidenceBundle={demoFinalAcceptanceCompletionEvidenceBundle}
        completionArchives={demoFinalAcceptanceCompletionArchives}
        completionCloseoutArchives={demoFinalAcceptanceCompletionCloseoutArchives}
        completionEvidenceDeliveryReceipts={demoFinalAcceptanceCompletionEvidenceDeliveryReceipts}
        completionEvidenceDeliveryFinalization={demoFinalAcceptanceCompletionEvidenceDeliveryFinalization}
        completionCloseout={demoFinalAcceptanceCompletionCloseout}
        finalExternalReviewEvidencePackage={demoFinalExternalReviewEvidencePackage}
        finalExternalReviewEvidencePackageArchives={demoFinalExternalReviewEvidencePackageArchives}
        finalExternalReviewEvidencePackageDeliveryReceipts={
          demoFinalExternalReviewEvidencePackageDeliveryReceipts
        }
        finalExternalReviewEvidencePackageDeliveryFinalization={
          demoFinalExternalReviewEvidencePackageDeliveryFinalization
        }
        finalExternalReviewEvidencePackageDeliveryFinalizationArchives={
          demoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives
        }
        finalExternalReviewDeliveryCertificate={demoFinalExternalReviewDeliveryCertificate}
        finalExternalReviewDeliveryCertificateArchives={
          demoFinalExternalReviewDeliveryCertificateArchives
        }
        finalExternalReviewReleaseBundle={demoFinalExternalReviewReleaseBundle}
        finalExternalReviewReleaseBundleArchives={demoFinalExternalReviewReleaseBundleArchives}
        finalExternalReviewReleaseBundleDeliveryReceipts={
          demoFinalExternalReviewReleaseBundleDeliveryReceipts
        }
        finalExternalReviewReleaseBundleDeliveryFinalization={
          demoFinalExternalReviewReleaseBundleDeliveryFinalization
        }
        finalExternalReviewReleaseBundleDeliveryFinalizationArchives={
          demoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives
        }
        finalExternalReviewReleaseBundleDeliveryCertificate={
          demoFinalExternalReviewReleaseBundleDeliveryCertificate
        }
        finalExternalReviewReleaseBundleDeliveryCertificateArchives={
          demoFinalExternalReviewReleaseBundleDeliveryCertificateArchives
        }
        finalReviewerHandoffPackage={demoEvidenceBundle?.finalReviewerHandoffPackage ?? null}
        finalReviewerHandoffDeliveryReceipts={demoFinalReviewerHandoffDeliveryReceipts}
        finalReviewerHandoffDeliveryFinalization={
          demoFinalReviewerHandoffDeliveryFinalization
            ?? demoEvidenceBundle?.finalReviewerHandoffDeliveryFinalization
            ?? null
        }
        error={demoAcceptanceSummaryError}
        sharePackageError={demoFinalAcceptanceSharePackageError}
        sharePackageArchiveError={demoFinalAcceptanceSharePackageArchiveError}
        shareDeliveryReceiptError={demoFinalAcceptanceShareDeliveryReceiptError}
        shareFinalizationError={demoFinalAcceptanceShareFinalizationError}
        completionEvidenceBundleError={demoFinalAcceptanceCompletionEvidenceBundleError}
        completionArchiveError={demoFinalAcceptanceCompletionArchiveError}
        completionCloseoutArchiveError={demoFinalAcceptanceCompletionCloseoutArchiveError}
        completionEvidenceDeliveryReceiptError={demoFinalAcceptanceCompletionEvidenceDeliveryReceiptError}
        completionEvidenceDeliveryFinalizationError={demoFinalAcceptanceCompletionEvidenceDeliveryFinalizationError}
        completionCloseoutError={demoFinalAcceptanceCompletionCloseoutError}
        finalExternalReviewEvidencePackageError={demoFinalExternalReviewEvidencePackageError}
        finalExternalReviewEvidencePackageArchiveError={demoFinalExternalReviewEvidencePackageArchiveError}
        finalExternalReviewEvidencePackageDeliveryReceiptError={
          demoFinalExternalReviewEvidencePackageDeliveryReceiptError
        }
        finalExternalReviewEvidencePackageDeliveryFinalizationError={
          demoFinalExternalReviewEvidencePackageDeliveryFinalizationError
        }
        finalExternalReviewEvidencePackageDeliveryFinalizationArchiveError={
          demoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveError
        }
        finalExternalReviewDeliveryCertificateError={demoFinalExternalReviewDeliveryCertificateError}
        finalExternalReviewDeliveryCertificateArchiveError={
          demoFinalExternalReviewDeliveryCertificateArchiveError
        }
        finalExternalReviewReleaseBundleError={demoFinalExternalReviewReleaseBundleError}
        finalExternalReviewReleaseBundleArchiveError={
          demoFinalExternalReviewReleaseBundleArchiveError
        }
        finalExternalReviewReleaseBundleDeliveryReceiptError={
          demoFinalExternalReviewReleaseBundleDeliveryReceiptError
        }
        finalExternalReviewReleaseBundleDeliveryFinalizationError={
          demoFinalExternalReviewReleaseBundleDeliveryFinalizationError
        }
        finalExternalReviewReleaseBundleDeliveryFinalizationArchiveError={
          demoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveError
        }
        finalExternalReviewReleaseBundleDeliveryCertificateError={
          demoFinalExternalReviewReleaseBundleDeliveryCertificateError
        }
        finalExternalReviewReleaseBundleDeliveryCertificateArchiveError={
          demoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveError
        }
        finalReviewerHandoffDeliveryReceiptError={demoFinalReviewerHandoffDeliveryReceiptError}
        finalReviewerHandoffDeliveryFinalizationError={demoFinalReviewerHandoffDeliveryFinalizationError}
        onDownloadReport={handleDownloadDemoAcceptanceSummaryReport}
        onDownloadSharePackageReport={handleDownloadDemoFinalAcceptanceSharePackageReport}
        onArchiveSharePackage={handleArchiveDemoFinalAcceptanceSharePackage}
        onDownloadSharePackageArchiveReport={handleDownloadDemoFinalAcceptanceSharePackageArchiveReport}
        onCreateShareDeliveryReceipt={handleCreateDemoFinalAcceptanceShareDeliveryReceipt}
        onDownloadShareDeliveryReceiptReport={handleDownloadDemoFinalAcceptanceShareDeliveryReceiptReport}
        onDownloadShareFinalizationReport={handleDownloadDemoFinalAcceptanceShareFinalizationReport}
        onDownloadCompletionEvidenceBundleReport={handleDownloadDemoFinalAcceptanceCompletionEvidenceBundleReport}
        onArchiveCompletion={handleArchiveDemoFinalAcceptanceCompletion}
        onDownloadCompletionArchiveReport={handleDownloadDemoFinalAcceptanceCompletionArchiveReport}
        onArchiveCompletionCloseout={handleArchiveDemoFinalAcceptanceCompletionCloseout}
        onDownloadCompletionCloseoutArchiveReport={
          handleDownloadDemoFinalAcceptanceCompletionCloseoutArchiveReport
        }
        onCreateCompletionEvidenceDeliveryReceipt={handleCreateDemoFinalAcceptanceCompletionEvidenceDeliveryReceipt}
        onDownloadCompletionEvidenceDeliveryReceiptReport={
          handleDownloadDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptReport
        }
        onDownloadCompletionEvidenceDeliveryFinalizationReport={
          handleDownloadDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationReport
        }
        onDownloadCompletionCloseoutReport={handleDownloadDemoFinalAcceptanceCompletionCloseoutReport}
        onDownloadFinalExternalReviewEvidencePackageReport={
          handleDownloadDemoFinalExternalReviewEvidencePackageReport
        }
        onArchiveFinalExternalReviewEvidencePackage={handleArchiveDemoFinalExternalReviewEvidencePackage}
        onDownloadFinalExternalReviewEvidencePackageArchiveReport={
          handleDownloadDemoFinalExternalReviewEvidencePackageArchiveReport
        }
        onCreateFinalExternalReviewEvidencePackageDeliveryReceipt={
          handleCreateDemoFinalExternalReviewEvidencePackageDeliveryReceipt
        }
        onDownloadFinalExternalReviewEvidencePackageDeliveryReceiptReport={
          handleDownloadDemoFinalExternalReviewEvidencePackageDeliveryReceiptReport
        }
        onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationReport={
          handleDownloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationReport
        }
        onArchiveFinalExternalReviewEvidencePackageDeliveryFinalization={
          handleArchiveDemoFinalExternalReviewEvidencePackageDeliveryFinalization
        }
        onDownloadFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport={
          handleDownloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport
        }
        onDownloadFinalExternalReviewDeliveryCertificateReport={
          handleDownloadDemoFinalExternalReviewDeliveryCertificateReport
        }
        onArchiveFinalExternalReviewDeliveryCertificate={
          handleArchiveDemoFinalExternalReviewDeliveryCertificate
        }
        onDownloadFinalExternalReviewDeliveryCertificateArchiveReport={
          handleDownloadDemoFinalExternalReviewDeliveryCertificateArchiveReport
        }
        onDownloadFinalExternalReviewReleaseBundleReport={
          handleDownloadDemoFinalExternalReviewReleaseBundleReport
        }
        onArchiveFinalExternalReviewReleaseBundle={
          handleArchiveDemoFinalExternalReviewReleaseBundle
        }
        onDownloadFinalExternalReviewReleaseBundleArchiveReport={
          handleDownloadDemoFinalExternalReviewReleaseBundleArchiveReport
        }
        onCreateFinalExternalReviewReleaseBundleDeliveryReceipt={
          handleCreateDemoFinalExternalReviewReleaseBundleDeliveryReceipt
        }
        onDownloadFinalExternalReviewReleaseBundleDeliveryReceiptReport={
          handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryReceiptReport
        }
        onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationReport={
          handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationReport
        }
        onArchiveFinalExternalReviewReleaseBundleDeliveryFinalization={
          handleArchiveDemoFinalExternalReviewReleaseBundleDeliveryFinalization
        }
        onDownloadFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport={
          handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport
        }
        onDownloadFinalExternalReviewReleaseBundleDeliveryCertificateReport={
          handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateReport
        }
        onArchiveFinalExternalReviewReleaseBundleDeliveryCertificate={
          handleArchiveDemoFinalExternalReviewReleaseBundleDeliveryCertificate
        }
        onDownloadFinalExternalReviewReleaseBundleDeliveryCertificateArchiveReport={
          handleDownloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveReport
        }
        onCreateFinalReviewerHandoffDeliveryReceipt={handleCreateDemoFinalReviewerHandoffDeliveryReceipt}
        onDownloadFinalReviewerHandoffDeliveryReceiptReport={
          handleDownloadDemoFinalReviewerHandoffDeliveryReceiptReport
        }
        onDownloadFinalReviewerHandoffDeliveryFinalizationReport={
          handleDownloadDemoFinalReviewerHandoffDeliveryFinalizationReport
        }
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
        finalHandoffReportPackage={demoFinalHandoffReportPackage}
        finalHandoffReportPackageArchives={demoFinalHandoffReportPackageArchives}
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
        finalHandoffReportPackageError={demoFinalHandoffReportPackageError}
        finalHandoffReportPackageArchiveError={demoFinalHandoffReportPackageArchiveError}
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
        onDownloadFinalHandoffReportPackage={handleDownloadDemoFinalHandoffReportPackage}
        onArchiveFinalHandoffReportPackage={handleArchiveDemoFinalHandoffReportPackage}
        onDownloadFinalHandoffReportPackageArchiveReport={handleDownloadDemoFinalHandoffReportPackageArchiveReport}
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

      <LiveTriggerDryRunPanel
        result={gitHubTriggerDryRun}
        error={gitHubTriggerDryRunError}
        pending={gitHubTriggerDryRunPending}
        onDryRun={handleGitHubTriggerDryRun}
      />

      <LiveLaunchGatePanel
        result={demoLiveLaunchGate}
        error={demoLiveLaunchGateError}
        pending={demoLiveLaunchGatePending}
        onRunGate={handleDemoLiveLaunchGate}
        launchPackage={demoLiveTriggerLaunchPackage}
        launchPackageError={demoLiveTriggerLaunchPackageError}
        launchPackagePending={demoLiveTriggerLaunchPackagePending}
        onCreateLaunchPackage={handleDemoLiveTriggerLaunchPackage}
        launchPackageArchives={demoLiveTriggerLaunchPackageArchives}
        launchPackageArchiveError={demoLiveTriggerLaunchPackageArchiveError}
        onArchiveLaunchPackage={handleArchiveDemoLiveTriggerLaunchPackage}
        onDownloadLaunchPackageArchiveReport={handleDownloadDemoLiveTriggerLaunchPackageArchiveReport}
        outcomeCloseout={demoLiveTriggerOutcomeCloseout}
        outcomeCloseoutError={demoLiveTriggerOutcomeCloseoutError}
        outcomeCloseoutPending={demoLiveTriggerOutcomeCloseoutPending}
        onCreateOutcomeCloseout={handleDemoLiveTriggerOutcomeCloseout}
        onDownloadOutcomeCloseoutReport={handleDownloadDemoLiveTriggerOutcomeCloseoutReport}
        outcomeCloseoutArchives={demoLiveTriggerOutcomeCloseoutArchives}
        outcomeCloseoutArchiveError={demoLiveTriggerOutcomeCloseoutArchiveError}
        onArchiveOutcomeCloseout={handleArchiveDemoLiveTriggerOutcomeCloseout}
        onDownloadOutcomeCloseoutArchiveReport={handleDownloadDemoLiveTriggerOutcomeCloseoutArchiveReport}
        liveDemoEvidenceBundle={demoLiveDemoEvidenceBundle}
        liveDemoEvidenceBundleError={demoLiveDemoEvidenceBundleError}
        onRefreshLiveDemoEvidenceBundle={handleRefreshDemoLiveDemoEvidenceBundle}
        onDownloadLiveDemoEvidenceBundleReport={handleDownloadDemoLiveDemoEvidenceBundleReport}
        liveDemoEvidenceBundleArchives={demoLiveDemoEvidenceBundleArchives}
        liveDemoEvidenceBundleArchiveError={demoLiveDemoEvidenceBundleArchiveError}
        onArchiveLiveDemoEvidenceBundle={handleArchiveDemoLiveDemoEvidenceBundle}
        onDownloadLiveDemoEvidenceBundleArchiveReport={handleDownloadDemoLiveDemoEvidenceBundleArchiveReport}
        liveDemoHandoffPackage={demoLiveDemoHandoffPackage}
        liveDemoHandoffPackageError={demoLiveDemoHandoffPackageError}
        onRefreshLiveDemoHandoffPackage={handleRefreshDemoLiveDemoHandoffPackage}
        onDownloadLiveDemoHandoffPackageReport={handleDownloadDemoLiveDemoHandoffPackageReport}
        liveDemoHandoffDeliveryReceipts={demoLiveDemoHandoffDeliveryReceipts}
        liveDemoHandoffDeliveryReceiptError={demoLiveDemoHandoffDeliveryReceiptError}
        onRecordLiveDemoHandoffDeliveryReceipt={handleRecordDemoLiveDemoHandoffDeliveryReceipt}
        onDownloadLiveDemoHandoffDeliveryReceiptReport={handleDownloadDemoLiveDemoHandoffDeliveryReceiptReport}
        liveDemoHandoffDeliveryFinalization={demoLiveDemoHandoffDeliveryFinalization}
        liveDemoHandoffDeliveryFinalizationError={demoLiveDemoHandoffDeliveryFinalizationError}
        onRefreshLiveDemoHandoffDeliveryFinalization={handleRefreshDemoLiveDemoHandoffDeliveryFinalization}
        onDownloadLiveDemoHandoffDeliveryFinalizationReport={handleDownloadDemoLiveDemoHandoffDeliveryFinalizationReport}
        liveDemoHandoffDeliveryFinalizationArchives={demoLiveDemoHandoffDeliveryFinalizationArchives}
        liveDemoHandoffDeliveryFinalizationArchiveError={demoLiveDemoHandoffDeliveryFinalizationArchiveError}
        onArchiveLiveDemoHandoffDeliveryFinalization={handleArchiveDemoLiveDemoHandoffDeliveryFinalization}
        onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport={
          handleDownloadDemoLiveDemoHandoffDeliveryFinalizationArchiveReport
        }
        liveDemoCompletionCertificate={demoLiveDemoCompletionCertificate}
        liveDemoCompletionCertificateError={demoLiveDemoCompletionCertificateError}
        onRefreshLiveDemoCompletionCertificate={handleRefreshDemoLiveDemoCompletionCertificate}
        onDownloadLiveDemoCompletionCertificateReport={handleDownloadDemoLiveDemoCompletionCertificateReport}
        liveDemoCompletionCertificateArchives={demoLiveDemoCompletionCertificateArchives}
        liveDemoCompletionCertificateArchiveError={demoLiveDemoCompletionCertificateArchiveError}
        onArchiveLiveDemoCompletionCertificate={handleArchiveDemoLiveDemoCompletionCertificate}
        onDownloadLiveDemoCompletionCertificateArchiveReport={
          handleDownloadDemoLiveDemoCompletionCertificateArchiveReport
        }
        liveDemoArtifactChainReport={demoLiveDemoArtifactChainReport}
        liveDemoArtifactChainReportError={demoLiveDemoArtifactChainReportError}
        onRefreshLiveDemoArtifactChainReport={handleRefreshDemoLiveDemoArtifactChainReport}
        onDownloadLiveDemoArtifactChainReport={handleDownloadDemoLiveDemoArtifactChainReport}
        liveDemoReplayPackage={demoLiveDemoReplayPackage}
        liveDemoReplayPackageError={demoLiveDemoReplayPackageError}
        onRefreshLiveDemoReplayPackage={handleRefreshDemoLiveDemoReplayPackage}
        onDownloadLiveDemoReplayPackage={handleDownloadDemoLiveDemoReplayPackage}
        liveDemoReviewerDeliveryCenter={demoLiveDemoReviewerDeliveryCenter}
        liveDemoReviewerDeliveryCenterError={demoLiveDemoReviewerDeliveryCenterError}
        onRefreshLiveDemoReviewerDeliveryCenter={handleRefreshDemoLiveDemoReviewerDeliveryCenter}
        onDownloadLiveDemoReviewerDeliveryCenter={handleDownloadDemoLiveDemoReviewerDeliveryCenter}
        liveDemoReviewerDeliveryCenterArchives={demoLiveDemoReviewerDeliveryCenterArchives}
        liveDemoReviewerDeliveryCenterArchiveError={demoLiveDemoReviewerDeliveryCenterArchiveError}
        onArchiveLiveDemoReviewerDeliveryCenter={handleArchiveDemoLiveDemoReviewerDeliveryCenter}
        onDownloadLiveDemoReviewerDeliveryCenterArchiveReport={
          handleDownloadDemoLiveDemoReviewerDeliveryCenterArchiveReport
        }
        liveDemoReviewerDeliveryCenterDeliveryReceipts={
          demoLiveDemoReviewerDeliveryCenterDeliveryReceipts
        }
        liveDemoReviewerDeliveryCenterDeliveryReceiptError={
          demoLiveDemoReviewerDeliveryCenterDeliveryReceiptError
        }
        onRecordLiveDemoReviewerDeliveryCenterDeliveryReceipt={
          handleRecordDemoLiveDemoReviewerDeliveryCenterDeliveryReceipt
        }
        onDownloadLiveDemoReviewerDeliveryCenterDeliveryReceiptReport={
          handleDownloadDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptReport
        }
      />

      <EndToEndAcceptanceMatrixPanel
        matrix={demoEndToEndAcceptanceMatrix}
        error={demoEndToEndAcceptanceMatrixError}
        onRefresh={handleRefreshEndToEndAcceptanceMatrix}
      />

      <ExternalExposureReadinessPanel
        readiness={externalExposureReadiness}
        error={externalExposureReadinessError}
        archives={externalExposureReadinessArchives}
        archiveError={externalExposureReadinessArchiveError}
        handoffPackage={externalExposureHandoffPackage}
        handoffPackageError={externalExposureHandoffPackageError}
        sessions={externalExposureSessions}
        sessionError={externalExposureSessionError}
        closeout={externalExposureCloseout}
        closeoutError={externalExposureCloseoutError}
        closeoutArchives={externalExposureCloseoutArchives}
        closeoutArchiveError={externalExposureCloseoutArchiveError}
        onArchiveReadiness={handleArchiveExternalExposureReadiness}
        onDownloadArchiveReport={handleDownloadExternalExposureReadinessArchiveReport}
        onDownloadHandoffPackageReport={handleDownloadExternalExposureHandoffPackageReport}
        onArchiveCloseout={handleArchiveExternalExposureCloseout}
        onDownloadCloseoutArchiveReport={handleDownloadExternalExposureCloseoutArchiveReport}
        onDownloadCloseoutReport={handleDownloadExternalExposureCloseoutReport}
        onStartSession={handleStartExternalExposureSession}
        onCloseSession={handleCloseExternalExposureSession}
        onDownloadSessionReport={handleDownloadExternalExposureSessionReport}
        onRefresh={handleRefreshExternalExposureReadiness}
      />

      <ExternalExposureOperatorHandoffChecklistPanel
        checklist={externalExposureOperatorHandoffChecklist}
        archives={externalExposureOperatorHandoffChecklistArchives}
        error={externalExposureOperatorHandoffChecklistError}
        archiveError={externalExposureOperatorHandoffChecklistArchiveError}
        onDownloadReport={handleDownloadExternalExposureOperatorHandoffChecklistReport}
        onArchiveChecklist={handleArchiveExternalExposureOperatorHandoffChecklist}
        onDownloadArchiveReport={handleDownloadExternalExposureOperatorHandoffChecklistArchiveReport}
        onRefresh={handleRefreshExternalExposureOperatorHandoffChecklist}
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
          onDownloadReport={handleDownloadTaskReport}
          onArchiveEvidencePackage={handleArchiveTaskEvidencePackage}
          onDownloadEvidencePackageReport={handleDownloadTaskEvidencePackageReport}
        />
      </section>

      <TaskEvidenceArchiveReviewPanel
        summary={taskEvidenceArchiveSummary}
        shareCenter={taskEvidenceShareCenter}
        finalization={taskEvidenceFinalization}
        deliveryReceipts={taskEvidenceDeliveryReceipts}
        closeoutArchives={taskEvidenceCloseoutArchives}
        certificate={taskEvidenceAcceptanceCertificate}
        certificateArchives={taskEvidenceAcceptanceCertificateArchives}
        archives={taskEvidenceArchives}
        error={taskEvidenceArchiveError}
        shareCenterError={taskEvidenceShareCenterError}
        finalizationError={taskEvidenceFinalizationError}
        deliveryReceiptError={taskEvidenceDeliveryReceiptError}
        closeoutArchiveError={taskEvidenceCloseoutArchiveError}
        certificateError={taskEvidenceAcceptanceCertificateError}
        certificateArchiveError={taskEvidenceAcceptanceCertificateArchiveError}
        onDownloadArchiveReport={handleDownloadTaskEvidencePackageReport}
        onDownloadShareCenterReport={handleDownloadTaskEvidencePackageShareCenterReport}
        onDownloadFinalizationReport={handleDownloadTaskEvidencePackageFinalizationReport}
        onCreateDeliveryReceipt={handleCreateTaskEvidencePackageShareDeliveryReceipt}
        onDownloadDeliveryReceiptReport={handleDownloadTaskEvidencePackageShareDeliveryReceiptReport}
        onArchiveAcceptanceCloseout={handleArchiveTaskEvidencePackageAcceptanceCloseout}
        onDownloadAcceptanceCloseoutArchiveReport={handleDownloadTaskEvidencePackageAcceptanceCloseoutArchiveReport}
        onDownloadAcceptanceCertificateReport={handleDownloadTaskEvidencePackageAcceptanceCertificateReport}
        onArchiveAcceptanceCertificate={handleArchiveTaskEvidencePackageAcceptanceCertificate}
        onDownloadAcceptanceCertificateArchiveReport={handleDownloadTaskEvidencePackageAcceptanceCertificateArchiveReport}
        onSelectTask={selectTask}
      />

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
