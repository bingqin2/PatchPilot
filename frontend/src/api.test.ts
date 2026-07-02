import {
  approveTaskReview,
  composeDemoLaunchCommand,
  createTask,
  createTriggerQuarantine,
  ADMIN_TOKEN_STORAGE_KEY,
  evaluateTrigger,
  postGitHubTriggerDryRun,
  postDemoLiveLaunchGate,
  postDemoLiveTriggerLaunchPackage,
  postDemoLiveTriggerOutcomeCloseout,
  getDemoLiveDemoEvidenceBundle,
  downloadDemoLiveDemoEvidenceBundleReport,
  archiveDemoLiveDemoEvidenceBundle,
  listDemoLiveDemoEvidenceBundleArchives,
  downloadDemoLiveDemoEvidenceBundleArchiveReport,
  getDemoLiveDemoHandoffPackage,
  downloadDemoLiveDemoHandoffPackageReport,
  createDemoLiveDemoHandoffDeliveryReceipt,
  listDemoLiveDemoHandoffDeliveryReceipts,
  downloadDemoLiveDemoHandoffDeliveryReceiptReport,
  downloadDemoLiveTriggerOutcomeCloseoutReport,
  archiveDemoLiveTriggerOutcomeCloseout,
  listDemoLiveTriggerOutcomeCloseoutArchives,
  downloadDemoLiveTriggerOutcomeCloseoutArchiveReport,
  archiveDemoLiveTriggerLaunchPackage,
  listDemoLiveTriggerLaunchPackageArchives,
  downloadDemoLiveTriggerLaunchPackageArchiveReport,
  getDemoEndToEndAcceptanceMatrix,
  getExternalExposureReadiness,
  archiveExternalExposureReadiness,
  listExternalExposureReadinessArchives,
  downloadExternalExposureReadinessArchiveReport,
  getExternalExposureHandoffPackage,
  downloadExternalExposureHandoffPackageReport,
  getExternalExposureCloseout,
  downloadExternalExposureCloseoutReport,
  archiveExternalExposureCloseout,
  listExternalExposureCloseoutArchives,
  downloadExternalExposureCloseoutArchiveReport,
  getExternalExposureOperatorHandoffChecklist,
  downloadExternalExposureOperatorHandoffChecklistReport,
  archiveExternalExposureOperatorHandoffChecklist,
  listExternalExposureOperatorHandoffChecklistArchives,
  downloadExternalExposureOperatorHandoffChecklistArchiveReport,
  startExternalExposureSession,
  closeExternalExposureSession,
  listExternalExposureSessions,
  downloadExternalExposureSessionReport,
  getDashboardBootstrap,
  getBackendHealth,
  getConfigurationSummary,
  getGitHubCredentialReadiness,
  getGitHubPublishPermissionReadiness,
  getGitHubPublishReadiness,
  getGitHubLivePublishPreflight,
  getGitHubWebhookSetupReadiness,
  getGitHubWebhookUrlReadiness,
  getGitHubRepositoryAccessReadiness,
  getFailureCauseSummary,
  getLatencySummary,
  getModelProviderHealth,
  getModelUsageSummary,
  getDemoScript,
  archiveDemoFinalHandoffReportPackage,
  archiveDemoHandoffPackage,
  archiveDemoSession,
  listDemoFinalHandoffReportPackageArchives,
  listDemoHandoffPackageArchives,
  archiveEvaluationRunSnapshot,
  listDemoSessionArchives,
  preflightDemoLaunch,
  getDemoSessionSnapshot,
  getDemoSessionReport,
  getDemoHandoffPackage,
  getDemoHandoffFinalization,
  getDemoFinalHandoffReportPackage,
  getDemoSelfHostedLaunchReadiness,
  archiveDemoSelfHostedLaunchReadiness,
  listDemoSelfHostedLaunchReadinessArchives,
  getDemoHandoffReadiness,
  getDemoHandoffPackageArchiveSummary,
  createDemoHandoffShareDeliveryReceipt,
  downloadDemoHandoffShareDeliveryReceiptReport,
  getDemoLaunchEvidencePackage,
  getDemoLaunchEvidenceShareCenter,
  getDemoLaunchEvidenceFinalization,
  getDemoLaunchAcceptanceCloseout,
  getDemoLaunchAcceptanceCertificate,
  createDemoLaunchEvidenceShareDeliveryReceipt,
  listDemoLaunchEvidenceShareDeliveryReceipts,
  downloadDemoLaunchEvidenceShareDeliveryReceiptReport,
  downloadDemoLaunchEvidenceFinalizationReport,
  downloadDemoLaunchAcceptanceCloseoutReport,
  downloadDemoLaunchAcceptanceCertificateReport,
  downloadDemoAcceptanceSummaryReport,
  getDemoFinalAcceptanceSharePackage,
  downloadDemoFinalAcceptanceSharePackageReport,
  archiveDemoFinalAcceptanceSharePackage,
  listDemoFinalAcceptanceSharePackageArchives,
  downloadDemoFinalAcceptanceSharePackageArchiveReport,
  createDemoFinalAcceptanceShareDeliveryReceipt,
  listDemoFinalAcceptanceShareDeliveryReceipts,
  downloadDemoFinalAcceptanceShareDeliveryReceiptReport,
  getDemoFinalAcceptanceShareFinalization,
  downloadDemoFinalAcceptanceShareFinalizationReport,
  archiveDemoFinalAcceptanceCompletion,
  listDemoFinalAcceptanceCompletionArchives,
  downloadDemoFinalAcceptanceCompletionArchiveReport,
  getDemoFinalAcceptanceCompletionEvidenceBundle,
  downloadDemoFinalAcceptanceCompletionEvidenceBundleReport,
  getDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization,
  downloadDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationReport,
  getDemoFinalAcceptanceCompletionCloseout,
  downloadDemoFinalAcceptanceCompletionCloseoutReport,
  getDemoFinalExternalReviewEvidencePackage,
  downloadDemoFinalExternalReviewEvidencePackageReport,
  archiveDemoFinalExternalReviewEvidencePackage,
  listDemoFinalExternalReviewEvidencePackageArchives,
  downloadDemoFinalExternalReviewEvidencePackageArchiveReport,
  createDemoFinalExternalReviewEvidencePackageDeliveryReceipt,
  listDemoFinalExternalReviewEvidencePackageDeliveryReceipts,
  downloadDemoFinalExternalReviewEvidencePackageDeliveryReceiptReport,
  getDemoFinalExternalReviewEvidencePackageDeliveryFinalization,
  getDemoFinalExternalReviewDeliveryCertificate,
  downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationReport,
  downloadDemoFinalExternalReviewDeliveryCertificateReport,
  getDemoFinalExternalReviewReleaseBundle,
  downloadDemoFinalExternalReviewReleaseBundleReport,
  archiveDemoFinalExternalReviewReleaseBundle,
  listDemoFinalExternalReviewReleaseBundleArchives,
  downloadDemoFinalExternalReviewReleaseBundleArchiveReport,
  createDemoFinalExternalReviewReleaseBundleDeliveryReceipt,
  listDemoFinalExternalReviewReleaseBundleDeliveryReceipts,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryReceiptReport,
  getDemoFinalExternalReviewReleaseBundleDeliveryFinalization,
  getDemoFinalExternalReviewReleaseBundleDeliveryCertificate,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateReport,
  archiveDemoFinalExternalReviewReleaseBundleDeliveryCertificate,
  listDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveReport,
  getDemoFinalReviewerHandoffPackage,
  downloadDemoFinalReviewerHandoffPackageReport,
  createDemoFinalReviewerHandoffDeliveryReceipt,
  listDemoFinalReviewerHandoffDeliveryReceipts,
  downloadDemoFinalReviewerHandoffDeliveryReceiptReport,
  getDemoFinalReviewerHandoffDeliveryFinalization,
  downloadDemoFinalReviewerHandoffDeliveryFinalizationReport,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationReport,
  archiveDemoFinalExternalReviewReleaseBundleDeliveryFinalization,
  listDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives,
  downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport,
  archiveDemoFinalExternalReviewEvidencePackageDeliveryFinalization,
  listDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives,
  downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport,
  archiveDemoFinalExternalReviewDeliveryCertificate,
  listDemoFinalExternalReviewDeliveryCertificateArchives,
  downloadDemoFinalExternalReviewDeliveryCertificateArchiveReport,
  archiveDemoFinalAcceptanceCompletionCloseout,
  listDemoFinalAcceptanceCompletionCloseoutArchives,
  downloadDemoFinalAcceptanceCompletionCloseoutArchiveReport,
  createDemoFinalAcceptanceCompletionEvidenceDeliveryReceipt,
  listDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts,
  downloadDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptReport,
  archiveDemoLaunchAcceptanceCertificate,
  listDemoLaunchAcceptanceCertificateArchives,
  downloadDemoLaunchAcceptanceCertificateArchiveReport,
  archiveDemoLaunchAcceptanceCloseout,
  listDemoLaunchAcceptanceCloseoutArchives,
  downloadDemoLaunchAcceptanceCloseoutArchiveReport,
  archiveDemoLaunchEvidencePackage,
  listDemoLaunchEvidencePackageArchives,
  downloadDemoLaunchEvidencePackageArchiveReport,
  downloadDemoLaunchEvidencePackageReport,
  downloadDemoLaunchEvidenceShareCenterReport,
  getDemoHandoffShareCenter,
  getDemoHandoffShareInstructions,
  listDemoHandoffShareDeliveryReceipts,
  getDemoHandoffShareChecklist,
  downloadDemoSessionReport,
  downloadDemoHandoffPackage,
  downloadDemoHandoffShareCenterReport,
  downloadDemoHandoffFinalizationReport,
  downloadDemoFinalHandoffReportPackage,
  downloadDemoFinalHandoffReportPackageArchiveReport,
  downloadDemoSelfHostedLaunchReadinessReport,
  downloadDemoSelfHostedLaunchReadinessArchiveReport,
  downloadDemoHandoffShareInstructionsReport,
  downloadDemoHandoffShareChecklistReport,
  downloadDemoHandoffPackageArchiveSummaryReport,
  downloadDemoHandoffPackageArchiveReport,
  downloadDemoSessionArchiveReport,
  downloadEvaluationRunSnapshotReport,
  getDemoSmokeChecklist,
  getDemoEvidenceBundle,
  getDemoRunbook,
  getEvaluationSummary,
  getEvaluationCaseReadiness,
  getEvaluationRunPreview,
  getEvaluationRunArchiveReadinessSummary,
  runAndArchiveEvaluation,
  runEvaluationFixtureBaseline,
  runAndArchiveEvaluationFixtureBaseline,
  getEvaluationFixtureBaselineRunRegressionSummary,
  listEvaluationFixtureBaselineRuns,
  downloadEvaluationFixtureBaselineRunReport,
  getRejectedTriggerSummary,
  getTriggerQuarantineEvidence,
  getWorkerHealth,
  listAcceptedTriggerDecisions,
  listAdminAuditEvents,
  listEvaluationCases,
  listEvaluationRuns,
  listEvaluationRunSnapshots,
  listLanguageAdapterFixtures,
  listLanguageAdapterRuntimeReadiness,
  listLanguageAdapters,
  listOperatorSafetyAudits,
  listRejectedTriggers,
  listTriggerQuarantines,
  preflightRepository,
  releaseTriggerQuarantine,
  retryRejectedTrigger,
  evaluateWebhookPayloadDiagnostic,
  getDemoReadiness,
  getDemoAcceptanceSummary,
  archiveDemoReadinessSnapshot,
  downloadDemoReadinessSnapshotReport,
  getDemoReadinessSnapshotTrend,
  listDemoReadinessSnapshots,
  getTaskReport,
  downloadTaskReport,
  archiveTaskEvidencePackage,
  archiveTaskEvidencePackageAcceptanceCloseout,
  archiveTaskEvidencePackageAcceptanceCertificate,
  createTaskEvidencePackageShareDeliveryReceipt,
  downloadTaskEvidencePackageAcceptanceCertificateArchiveReport,
  downloadTaskEvidencePackageAcceptanceCertificateReport,
  downloadTaskEvidencePackageFinalizationReport,
  downloadTaskEvidencePackageAcceptanceCloseoutArchiveReport,
  getTaskEvidencePackageAcceptanceCertificate,
  getTaskEvidencePackageArchiveSummary,
  getTaskEvidencePackageFinalization,
  getTaskEvidencePackageShareCenter,
  listTaskEvidencePackageArchives,
  listTaskEvidencePackageAcceptanceCloseoutArchives,
  listTaskEvidencePackageAcceptanceCertificateArchives,
  listTaskEvidencePackageShareDeliveryReceipts,
  listRecentTaskEvidencePackageArchives,
  downloadTaskEvidencePackageReport,
  downloadTaskEvidencePackageShareDeliveryReceiptReport,
  downloadTaskEvidencePackageShareCenterReport,
  getTaskDetail,
  getTaskRetryPreflight,
  getTaskStatusCounts,
  listWebhookDeliveries,
  listTasks,
  downloadEvaluationRunReport,
  retryTask
} from './api';
import type { DemoSessionReportInput } from './types';

afterEach(() => {
  vi.unstubAllGlobals();
  vi.unstubAllEnvs();
});

test('creates manual task through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 201,
    json: async () => ({
      success: true,
      data: {
        id: 'manual-task-1',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 7,
        installationId: 0,
        triggerUser: 'local-operator',
        triggerComment: '/agent fix touch docs/manual-task.md',
        deliveryId: 'manual-123',
        commentId: 0,
        status: 'PENDING',
        failureReason: null,
        createdAt: '2026-06-21T10:00:00Z',
        pullRequestUrl: null,
            completedAt: null,
            updatedAt: '2026-06-21T10:00:00Z',
            language: null,
            buildSystem: null,
            verificationCommand: null,
            adapterDetectionReason: null,
            statusCommentId: null,
            statusCommentUrl: null
          },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const task = await createTask({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 7,
    triggerUser: 'local-operator',
    triggerComment: '/agent fix touch docs/manual-task.md'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 7,
      triggerUser: 'local-operator',
      triggerComment: '/agent fix touch docs/manual-task.md'
    })
  });
  expect(task.id).toBe('manual-task-1');
  expect(task.status).toBe('PENDING');
});

test('loads demo end-to-end acceptance matrix through backend API', async () => {
  const matrix = {
    status: 'READY',
    readyForFinalDemo: true,
    readinessPercent: 100,
    readyCount: 2,
    needsAttentionCount: 0,
    blockedCount: 0,
    totalCount: 2,
    summary: 'PatchPilot is ready for a final issue-to-PR demo.',
    nextActions: ['Run the live launch gate before posting a real trigger.'],
    sideEffectContract:
      'GET /api/demo/end-to-end-acceptance-matrix is read-only and does not mutate GitHub.',
    items: [
      {
        category: 'Launch',
        name: 'Live launch gate',
        status: 'READY',
        evidence: 'Live launch gate is READY.',
        gap: 'No launch gate gap.',
        nextAction: 'No action needed.'
      },
      {
        category: 'Recent Pull Request evidence',
        name: 'Completed issue-to-PR task',
        status: 'READY',
        evidence: 'Task task-1 produced https://github.com/bingqin2/PatchPilot/pull/42.',
        gap: 'No recent PR gap.',
        nextAction: 'No action needed.'
      }
    ],
    generatedAt: '2026-07-01T12:00:00Z',
    markdownReport: '# PatchPilot End-to-End Acceptance Matrix'
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: matrix,
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await expect(getDemoEndToEndAcceptanceMatrix()).resolves.toEqual(matrix);
  expect(fetchMock).toHaveBeenCalledWith('/api/demo/end-to-end-acceptance-matrix');
});

test('loads external exposure readiness through backend API', async () => {
  const readiness = {
    status: 'BLOCKED',
    safeToExpose: false,
    readyCount: 6,
    needsAttentionCount: 2,
    blockedCount: 2,
    totalCount: 10,
    summary: 'PatchPilot is blocked from safe public exposure.',
    nextActions: ['Configure PATCHPILOT_ADMIN_TOKEN before exposing PatchPilot outside localhost.'],
    sideEffectContract: 'GET /api/security/external-exposure-readiness is read-only and does not expose secrets.',
    checks: [
      {
        name: 'Admin API token',
        status: 'BLOCKED',
        summary: 'Admin API token is missing.',
        nextAction: 'Configure PATCHPILOT_ADMIN_TOKEN before exposing PatchPilot outside localhost.'
      }
    ],
    generatedAt: '2026-07-01T12:00:00Z',
    markdownReport: '# PatchPilot External Exposure Readiness'
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: readiness,
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await expect(getExternalExposureReadiness()).resolves.toEqual(readiness);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-readiness');
});

test('archives external exposure readiness through backend API', async () => {
  const archive = {
    id: 'exposure-archive-1',
    status: 'NEEDS_ATTENTION',
    safeToExpose: false,
    summary: 'PatchPilot needs more safeguards before public exposure.',
    readyCount: 7,
    needsAttentionCount: 3,
    blockedCount: 0,
    totalCount: 10,
    createdAt: '2026-07-01T13:30:00Z',
    report: '# PatchPilot External Exposure Readiness'
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: archive,
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await expect(archiveExternalExposureReadiness()).resolves.toEqual(archive);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-readiness/archives', {
    method: 'POST'
  });
});

test('lists external exposure readiness archives through backend API', async () => {
  const archive = {
    id: 'exposure-archive-1',
    status: 'READY',
    safeToExpose: true,
    summary: 'PatchPilot is configured for controlled temporary public exposure.',
    readyCount: 10,
    needsAttentionCount: 0,
    blockedCount: 0,
    totalCount: 10,
    createdAt: '2026-07-01T13:30:00Z',
    report: '# PatchPilot External Exposure Readiness'
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [archive],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listExternalExposureReadinessArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-readiness/archives');
  expect(archives[0].id).toBe('exposure-archive-1');
  expect(archives[0].safeToExpose).toBe(true);
});

test('downloads archived external exposure readiness markdown through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot External Exposure Readiness'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadExternalExposureReadinessArchiveReport('exposure-archive-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-readiness/archives/exposure-archive-1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches external exposure handoff package through backend API', async () => {
  const handoffPackage = {
    status: 'READY',
    handoffReady: true,
    summary: 'External exposure handoff package is ready to share.',
    nextAction: 'Start the temporary tunnel, share the current payload URL, and monitor webhook deliveries.',
    readinessStatus: 'READY',
    readinessSafeToExpose: true,
    readinessReadyCount: 10,
    readinessNeedsAttentionCount: 0,
    readinessBlockedCount: 0,
    readinessTotalCount: 10,
    latestArchiveId: 'exposure-archive-1',
    latestArchiveStatus: 'READY',
    latestArchiveSafeToExpose: true,
    latestArchiveCreatedAt: '2026-07-01T13:30:00Z',
    archiveFreshness: 'CURRENT',
    nextActions: ['Start the temporary tunnel and keep monitoring.'],
    evidenceNotes: ['Latest archive exposure-archive-1 captures READY readiness evidence.'],
    downloadActions: ['GET /api/security/external-exposure-handoff-package/report/download'],
    sideEffectContract: 'GET /api/security/external-exposure-handoff-package is read-only.',
    generatedAt: '2026-07-01T14:00:00Z',
    markdownReport: '# PatchPilot External Exposure Handoff Package'
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: handoffPackage,
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await expect(getExternalExposureHandoffPackage()).resolves.toEqual(handoffPackage);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-handoff-package');
});

test('downloads external exposure handoff package markdown through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot External Exposure Handoff Package'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadExternalExposureHandoffPackageReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-handoff-package/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches and downloads external exposure closeout through backend API', async () => {
  const closeout = {
    status: 'READY',
    closeoutReady: true,
    summary: 'External exposure session is closed with complete local evidence.',
    nextAction: 'Keep the closeout report with the demo evidence bundle.',
    latestSessionId: 'exposure-session-1',
    latestSessionStatus: 'CLOSED',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    purpose: 'Live GitHub webhook smoke test',
    operator: 'bingqin2',
    startedAt: '2026-07-01T15:00:00Z',
    closedBy: 'bingqin2',
    closedAt: '2026-07-01T16:30:00Z',
    closeNotes: 'Tunnel process stopped.',
    linkedReadinessArchiveId: 'exposure-archive-1',
    handoffStatus: 'READY',
    archiveFreshness: 'CURRENT',
    readyCount: 4,
    needsAttentionCount: 0,
    blockedCount: 0,
    totalCount: 4,
    nextActions: ['Keep the closeout report with the demo evidence bundle.'],
    evidenceNotes: ['Latest session exposure-session-1 is CLOSED.'],
    downloadActions: ['GET /api/security/external-exposure-closeout/report/download'],
    sideEffectContract: 'GET /api/security/external-exposure-closeout is read-only.',
    generatedAt: '2026-07-01T18:00:00Z',
    markdownReport: '# PatchPilot External Exposure Closeout'
  };
  const reportBlob = new Blob(['# PatchPilot External Exposure Closeout'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async (url: RequestInfo | URL) => {
    if (String(url).endsWith('/report/download')) {
      return {
        ok: true,
        status: 200,
        blob: async () => reportBlob
      } as Response;
    }
    return {
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: closeout,
        message: null
      })
    } as Response;
  });
  vi.stubGlobal('fetch', fetchMock);

  await expect(getExternalExposureCloseout()).resolves.toEqual(closeout);
  await expect(downloadExternalExposureCloseoutReport()).resolves.toBe(reportBlob);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-closeout');
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-closeout/report/download');
});

test('archives, lists, and downloads external exposure closeout through backend API', async () => {
  const archive = {
    id: 'closeout-archive-1',
    status: 'READY',
    closeoutReady: true,
    summary: 'External exposure session is closed with complete local evidence.',
    nextAction: 'Keep the closeout report with the demo evidence bundle.',
    latestSessionId: 'exposure-session-1',
    latestSessionStatus: 'CLOSED',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    purpose: 'Live GitHub webhook smoke test',
    operator: 'bingqin2',
    startedAt: '2026-07-01T15:00:00Z',
    closedBy: 'bingqin2',
    closedAt: '2026-07-01T16:30:00Z',
    closeNotes: 'Tunnel process stopped.',
    linkedReadinessArchiveId: 'exposure-archive-1',
    handoffStatus: 'READY',
    archiveFreshness: 'CURRENT',
    readyCount: 4,
    needsAttentionCount: 0,
    blockedCount: 0,
    totalCount: 4,
    nextActions: ['Keep the closeout report with the demo evidence bundle.'],
    evidenceNotes: ['Latest session exposure-session-1 is CLOSED.'],
    downloadActions: ['GET /api/security/external-exposure-closeout/report/download'],
    sideEffectContract: 'GET /api/security/external-exposure-closeout is read-only.',
    generatedAt: '2026-07-01T18:00:00Z',
    archivedAt: '2026-07-01T18:05:00Z',
    report: '# PatchPilot External Exposure Closeout'
  };
  const reportBlob = new Blob(['# PatchPilot External Exposure Closeout'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async (url: RequestInfo | URL, init?: RequestInit) => {
    if (String(url).endsWith('/report/download')) {
      return {
        ok: true,
        status: 200,
        blob: async () => reportBlob
      } as Response;
    }
    return {
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: init?.method === 'POST' ? archive : [archive],
        message: null
      })
    } as Response;
  });
  vi.stubGlobal('fetch', fetchMock);

  await expect(archiveExternalExposureCloseout()).resolves.toEqual(archive);
  await expect(listExternalExposureCloseoutArchives()).resolves.toEqual([archive]);
  await expect(downloadExternalExposureCloseoutArchiveReport('closeout/archive 1')).resolves.toBe(reportBlob);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-closeout/archives', {
    method: 'POST'
  });
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-closeout/archives');
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-closeout/archives/closeout%2Farchive%201/report/download');
});

test('gets and downloads external exposure operator handoff checklist through backend API', async () => {
  const checklist = {
    status: 'READY',
    readyForNextLiveStep: true,
    summary: 'External exposure evidence is closed and ready for the next live step.',
    nextAction:
      'Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.',
    repository: 'bingqin2/PatchPilot',
    latestCloseoutArchiveId: 'closeout-archive-1',
    latestSessionId: 'exposure-session-1',
    latestSessionStatus: 'CLOSED',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    handoffStatus: 'READY',
    archiveFreshness: 'CURRENT',
    livePublishStatus: 'READY',
    livePublishReady: true,
    activeSessionCount: 0,
    readyCount: 4,
    needsAttentionCount: 0,
    blockedCount: 0,
    totalCount: 4,
    nextActions: [
      'Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.'
    ],
    evidenceNotes: ['Latest closeout archive closeout-archive-1 is READY.'],
    downloadActions: ['GET /api/security/external-exposure-operator-handoff-checklist/report/download'],
    sideEffectContract: 'GET /api/security/external-exposure-operator-handoff-checklist is read-only.',
    checks: [
      {
        name: 'Closeout archive',
        status: 'READY',
        summary: 'Latest closeout archive closeout-archive-1 is READY.',
        nextAction: 'Ready.'
      }
    ],
    generatedAt: '2026-07-01T19:00:00Z',
    markdownReport: '# PatchPilot External Exposure Operator Handoff Checklist'
  };
  const reportBlob = new Blob(['# PatchPilot External Exposure Operator Handoff Checklist'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async (url: RequestInfo | URL) => {
    if (String(url).endsWith('/report/download')) {
      return {
        ok: true,
        status: 200,
        blob: async () => reportBlob
      } as Response;
    }
    return {
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: checklist,
        message: null
      })
    } as Response;
  });
  vi.stubGlobal('fetch', fetchMock);

  await expect(getExternalExposureOperatorHandoffChecklist()).resolves.toEqual(checklist);
  await expect(downloadExternalExposureOperatorHandoffChecklistReport()).resolves.toBe(reportBlob);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-operator-handoff-checklist');
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-operator-handoff-checklist/report/download');
});

test('archives, lists, and downloads external exposure operator handoff checklist archives through backend API', async () => {
  const archive = {
    id: 'operator-handoff-archive-1',
    status: 'READY',
    readyForNextLiveStep: true,
    summary: 'External exposure evidence is closed and ready for the next live step.',
    nextAction:
      'Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.',
    repository: 'bingqin2/PatchPilot',
    latestCloseoutArchiveId: 'closeout-archive-1',
    latestSessionId: 'exposure-session-1',
    latestSessionStatus: 'CLOSED',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    handoffStatus: 'READY',
    archiveFreshness: 'CURRENT',
    livePublishStatus: 'READY',
    livePublishReady: true,
    activeSessionCount: 0,
    readyCount: 4,
    needsAttentionCount: 0,
    blockedCount: 0,
    totalCount: 4,
    nextActions: [
      'Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.'
    ],
    evidenceNotes: ['Latest closeout archive closeout-archive-1 is READY.'],
    downloadActions: ['GET /api/security/external-exposure-operator-handoff-checklist/report/download'],
    sideEffectContract: 'GET /api/security/external-exposure-operator-handoff-checklist is read-only.',
    checks: [
      {
        name: 'Closeout archive',
        status: 'READY',
        summary: 'Latest closeout archive closeout-archive-1 is READY.',
        nextAction: 'Ready.'
      }
    ],
    generatedAt: '2026-07-01T19:00:00Z',
    archivedAt: '2026-07-01T19:05:00Z',
    report: '# PatchPilot External Exposure Operator Handoff Checklist'
  };
  const reportBlob = new Blob(['# PatchPilot External Exposure Operator Handoff Checklist'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async (url: RequestInfo | URL, init?: RequestInit) => {
    if (String(url).endsWith('/report/download')) {
      return {
        ok: true,
        status: 200,
        blob: async () => reportBlob
      } as Response;
    }
    return {
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: init?.method === 'POST' ? archive : [archive],
        message: null
      })
    } as Response;
  });
  vi.stubGlobal('fetch', fetchMock);

  await expect(archiveExternalExposureOperatorHandoffChecklist()).resolves.toEqual(archive);
  await expect(listExternalExposureOperatorHandoffChecklistArchives()).resolves.toEqual([archive]);
  await expect(downloadExternalExposureOperatorHandoffChecklistArchiveReport('operator/archive 1')).resolves.toBe(reportBlob);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-operator-handoff-checklist/archives', {
    method: 'POST'
  });
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-operator-handoff-checklist/archives');
  expect(fetchMock).toHaveBeenCalledWith(
    '/api/security/external-exposure-operator-handoff-checklist/archives/operator%2Farchive%201/report/download'
  );
});

test('starts external exposure session through backend API', async () => {
  const session = {
    id: 'exposure-session-1',
    status: 'ACTIVE',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    purpose: 'Live GitHub webhook smoke test',
    operator: 'bingqin2',
    expectedShutdownAt: '2026-07-01T17:00:00Z',
    notes: 'Keep terminal visible during test.',
    linkedHandoffStatus: 'READY',
    linkedReadinessArchiveId: 'exposure-archive-1',
    startedAt: '2026-07-01T15:00:00Z',
    closedBy: null,
    closedAt: null,
    closeNotes: null,
    markdownReport: '# PatchPilot External Exposure Session'
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: session,
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await expect(startExternalExposureSession({
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    purpose: 'Live GitHub webhook smoke test',
    operator: 'bingqin2',
    expectedShutdownAt: '2026-07-01T17:00:00Z',
    notes: 'Keep terminal visible during test.'
  })).resolves.toEqual(session);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-sessions', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      publicUrl: 'https://demo.trycloudflare.com',
      webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
      purpose: 'Live GitHub webhook smoke test',
      operator: 'bingqin2',
      expectedShutdownAt: '2026-07-01T17:00:00Z',
      notes: 'Keep terminal visible during test.'
    })
  });
});

test('closes external exposure session through backend API', async () => {
  const session = {
    id: 'exposure-session-1',
    status: 'CLOSED',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    purpose: 'Live GitHub webhook smoke test',
    operator: 'bingqin2',
    expectedShutdownAt: '2026-07-01T17:00:00Z',
    notes: 'Keep terminal visible during test.',
    linkedHandoffStatus: 'READY',
    linkedReadinessArchiveId: 'exposure-archive-1',
    startedAt: '2026-07-01T15:00:00Z',
    closedBy: 'bingqin2',
    closedAt: '2026-07-01T16:30:00Z',
    closeNotes: 'Tunnel process stopped.',
    markdownReport: '# PatchPilot External Exposure Session'
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: session,
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await expect(closeExternalExposureSession('exposure-session-1', {
    closedBy: 'bingqin2',
    closedAt: '2026-07-01T16:30:00Z',
    closeNotes: 'Tunnel process stopped.'
  })).resolves.toEqual(session);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-sessions/exposure-session-1/close', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      closedBy: 'bingqin2',
      closedAt: '2026-07-01T16:30:00Z',
      closeNotes: 'Tunnel process stopped.'
    })
  });
});

test('lists and downloads external exposure sessions through backend API', async () => {
  const session = {
    id: 'exposure-session-1',
    status: 'ACTIVE',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    purpose: 'Live GitHub webhook smoke test',
    operator: 'bingqin2',
    expectedShutdownAt: '2026-07-01T17:00:00Z',
    notes: 'Keep terminal visible during test.',
    linkedHandoffStatus: 'READY',
    linkedReadinessArchiveId: 'exposure-archive-1',
    startedAt: '2026-07-01T15:00:00Z',
    closedBy: null,
    closedAt: null,
    closeNotes: null,
    markdownReport: '# PatchPilot External Exposure Session'
  };
  const reportBlob = new Blob(['# PatchPilot External Exposure Session'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async (url: RequestInfo | URL) => {
    if (String(url).endsWith('/report/download')) {
      return {
        ok: true,
        status: 200,
        blob: async () => reportBlob
      } as Response;
    }
    return {
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: [session],
        message: null
      })
    } as Response;
  });
  vi.stubGlobal('fetch', fetchMock);

  await expect(listExternalExposureSessions()).resolves.toEqual([session]);
  await expect(downloadExternalExposureSessionReport('exposure-session-1')).resolves.toBe(reportBlob);
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-sessions');
  expect(fetchMock).toHaveBeenCalledWith('/api/security/external-exposure-sessions/exposure-session-1/report/download');
});

test('loads dashboard bootstrap through backend API without a saved admin token', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        adminTokenConfigured: true,
        adminTokenBootstrapEnabled: true,
        adminToken: 'local-admin-token',
        message: 'Local dashboard admin token bootstrap is enabled.',
        operatorAction: 'The dashboard can store this token for the current local browser.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const bootstrap = await getDashboardBootstrap();

  expect(fetchMock).toHaveBeenCalledWith('/api/dashboard/bootstrap');
  expect(bootstrap.adminToken).toBe('local-admin-token');
  expect(bootstrap.adminTokenBootstrapEnabled).toBe(true);
});

test('gets GitHub credential readiness through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        tokenConfigured: true,
        status: 'READY',
        message: 'GitHub API accepted the configured token.',
        latencyMs: 31,
        checkedAt: '2026-06-25T03:00:00Z',
        operatorAction: 'No action needed.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getGitHubCredentialReadiness();

  expect(fetchMock).toHaveBeenCalledWith('/api/github/credential-readiness');
  expect(readiness.status).toBe('READY');
  expect(readiness.tokenConfigured).toBe(true);
  expect(readiness.message).toBe('GitHub API accepted the configured token.');
});

test('gets GitHub repository access readiness through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        tokenConfigured: true,
        repositoryConfigured: true,
        repository: 'bingqin2/PatchPilot',
        status: 'READY',
        message: 'GitHub token can read repository bingqin2/PatchPilot.',
        defaultBranch: 'main',
        latencyMs: 42,
        checkedAt: '2026-06-25T04:00:00Z',
        operatorAction: 'No action needed.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getGitHubRepositoryAccessReadiness(' bingqin2 ', ' PatchPilot ');

  expect(fetchMock).toHaveBeenCalledWith('/api/github/repository-access-readiness?owner=bingqin2&repository=PatchPilot');
  expect(readiness.status).toBe('READY');
  expect(readiness.repository).toBe('bingqin2/PatchPilot');
  expect(readiness.defaultBranch).toBe('main');
});

test('gets GitHub publish readiness through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        publishReady: true,
        tokenConfigured: true,
        repositoryConfigured: true,
        repository: 'bingqin2/PatchPilot',
        defaultBranch: 'main',
        summary: 'GitHub publish path is ready for PatchPilot push and Pull Request creation.',
        nextAction: 'Continue with the live /agent fix demo.',
        safePublishCommand: 'git push origin HEAD:<patchpilot-branch>',
        sideEffectContract: 'Read-only readiness probe: this endpoint does not run git push, does not create Pull Requests, and does not expose tokens.',
        checks: [
          {
            name: 'GitHub token',
            status: 'READY',
            summary: 'GitHub API accepted the configured token.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Token configured: true'],
        checkedAt: '2026-06-30T01:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getGitHubPublishReadiness(' bingqin2 ', ' PatchPilot ');

  expect(fetchMock).toHaveBeenCalledWith('/api/github/publish-readiness?owner=bingqin2&repository=PatchPilot');
  expect(readiness.status).toBe('READY');
  expect(readiness.repository).toBe('bingqin2/PatchPilot');
  expect(readiness.safePublishCommand).toBe('git push origin HEAD:<patchpilot-branch>');
  expect(readiness.sideEffectContract).toContain('does not run git push');
});

test('gets GitHub publish permission readiness through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        publishPermissionReady: true,
        tokenConfigured: true,
        repositoryConfigured: true,
        repository: 'bingqin2/PatchPilot',
        defaultBranch: 'main',
        canReadRepository: true,
        canPushBranches: true,
        canCreatePullRequests: true,
        issueFeedbackPermissionLikely: true,
        summary: 'GitHub token has repository publish permissions for PatchPilot push and Pull Request creation.',
        nextAction: 'Continue with the live /agent fix demo.',
        sideEffectContract: 'Read-only permission probe: this endpoint does not run git push, does not create Pull Requests, and does not expose tokens.',
        permissionChecks: [
          {
            name: 'Branch push',
            status: 'READY',
            summary: 'Token can publish PatchPilot branches.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Repository: bingqin2/PatchPilot'],
        latencyMs: 35,
        checkedAt: '2026-06-30T06:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getGitHubPublishPermissionReadiness(' bingqin2 ', ' PatchPilot ');

  expect(fetchMock).toHaveBeenCalledWith('/api/github/publish-permission-readiness?owner=bingqin2&repository=PatchPilot');
  expect(readiness.status).toBe('READY');
  expect(readiness.repository).toBe('bingqin2/PatchPilot');
  expect(readiness.canPushBranches).toBe(true);
  expect(readiness.canCreatePullRequests).toBe(true);
  expect(readiness.sideEffectContract).toContain('does not run git push');
});

test('gets GitHub live publish preflight through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'NEEDS_ATTENTION',
        livePublishReady: false,
        tokenConfigured: true,
        repositoryConfigured: true,
        repository: 'bingqin2/PatchPilot',
        defaultBranch: 'main',
        patchpilotBranches: ['patchpilot/task-1'],
        openPatchpilotPullRequests: ['https://github.com/bingqin2/PatchPilot/pull/4'],
        summary: 'Live GitHub publish preflight found existing PatchPilot publish artifacts.',
        nextAction: 'Review, close or merge stale PatchPilot Pull Requests, and delete old patchpilot/* branches before the live demo.',
        sideEffectContract: 'Read-only live publish preflight: this endpoint does not run git push, does not create branches, does not open Pull Requests, does not write issue comments, and does not expose tokens.',
        checks: [
          {
            name: 'Open PatchPilot Pull Requests',
            status: 'NEEDS_ATTENTION',
            summary: 'Found 1 open PatchPilot Pull Request.',
            nextAction: 'Close, merge, or intentionally keep the existing PatchPilot Pull Request before demo launch.'
          }
        ],
        evidenceNotes: ['Repository: bingqin2/PatchPilot'],
        latencyMs: 42,
        checkedAt: '2026-06-30T09:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const preflight = await getGitHubLivePublishPreflight(' bingqin2 ', ' PatchPilot ');

  expect(fetchMock).toHaveBeenCalledWith('/api/github/live-publish-preflight?owner=bingqin2&repository=PatchPilot');
  expect(preflight.status).toBe('NEEDS_ATTENTION');
  expect(preflight.repository).toBe('bingqin2/PatchPilot');
  expect(preflight.patchpilotBranches).toContain('patchpilot/task-1');
  expect(preflight.openPatchpilotPullRequests).toContain('https://github.com/bingqin2/PatchPilot/pull/4');
  expect(preflight.sideEffectContract).toContain('does not open Pull Requests');
});

test('gets GitHub webhook URL readiness through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        publicBaseUrlConfigured: true,
        status: 'READY',
        publicBaseUrl: 'https://demo.trycloudflare.com',
        payloadUrl: 'https://demo.trycloudflare.com/api/github/webhook',
        healthUrl: 'https://demo.trycloudflare.com/health',
        message: 'Configured public webhook URL reaches PatchPilot health.',
        latencyMs: 44,
        checkedAt: '2026-06-27T01:00:00Z',
        operatorAction: 'Use the payload URL in the GitHub webhook settings.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getGitHubWebhookUrlReadiness();

  expect(fetchMock).toHaveBeenCalledWith('/api/github/webhook-url-readiness');
  expect(readiness.status).toBe('READY');
  expect(readiness.payloadUrl).toBe('https://demo.trycloudflare.com/api/github/webhook');
  expect(readiness.healthUrl).toBe('https://demo.trycloudflare.com/health');
});

test('gets GitHub webhook setup readiness through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        secretConfigured: true,
        publicUrlReady: true,
        publicBaseUrl: 'https://demo.trycloudflare.com',
        payloadUrl: 'https://demo.trycloudflare.com/api/github/webhook',
        healthUrl: 'https://demo.trycloudflare.com/health',
        latestDeliveryStatus: 'TASK_CREATED',
        latestDeliveryId: 'delivery-1',
        redeliveryRecommended: false,
        summary: 'Webhook setup is ready for GitHub deliveries.',
        nextActions: ['Use the payload URL in GitHub Webhooks and continue the live demo.'],
        checkedAt: '2026-06-27T02:00:00Z',
        markdownReport: '# PatchPilot Webhook Setup Readiness\n\n- Status: `READY`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getGitHubWebhookSetupReadiness();

  expect(fetchMock).toHaveBeenCalledWith('/api/github/webhook-setup-readiness');
  expect(readiness.status).toBe('READY');
  expect(readiness.secretConfigured).toBe(true);
  expect(readiness.publicUrlReady).toBe(true);
  expect(readiness.payloadUrl).toBe('https://demo.trycloudflare.com/api/github/webhook');
  expect(readiness.latestDeliveryStatus).toBe('TASK_CREATED');
  expect(readiness.nextActions).toContain('Use the payload URL in GitHub Webhooks and continue the live demo.');
});

test('gets demo launch evidence package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        readyToShare: true,
        summary: 'PatchPilot launch evidence package is ready to share.',
        sessionId: 'demo-session-20260624T003000Z',
        launchReadinessStatus: 'READY',
        evidenceBundleStatus: 'READY',
        handoffFinalizationStatus: 'READY',
        finalHandoffReportPackageArchiveStatus: 'READY',
        finalHandoffReportPackageArchiveReady: true,
        finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
        finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        latestWebhookDeliveryId: 'delivery-1',
        evaluationRunId: 'evaluation-run-2',
        evaluationCoverage: ['java', 'python', 'maven', 'pytest'],
        preLaunchChecks: [
          {
            name: 'Demo readiness',
            status: 'READY',
            message: 'PatchPilot is ready for a controlled demo.',
            action: 'No action needed.'
          }
        ],
        liveRunProof: [
          'Recent task task-1 reached COMPLETED.',
          'Recent Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is available.'
        ],
        postDemoProof: [
          'Handoff finalization is READY.',
          'Latest delivery receipt delivery-receipt-1 is fresh.'
        ],
        nextActions: ['Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review.'],
        healthContract: [
          'GET /api/demo/launch-evidence-package is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.'
        ],
        markdownReport: '# PatchPilot Demo Launch Evidence Package',
        generatedAt: '2026-06-28T02:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const evidencePackage = await getDemoLaunchEvidencePackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-package');
  expect(evidencePackage.status).toBe('READY');
  expect(evidencePackage.readyToShare).toBe(true);
  expect(evidencePackage.sessionId).toBe('demo-session-20260624T003000Z');
  expect(evidencePackage.latestPullRequestUrl).toBe('https://github.com/bingqin2/PatchPilot/pull/42');
  expect(evidencePackage.evaluationCoverage).toEqual(['java', 'python', 'maven', 'pytest']);
  expect(evidencePackage.liveRunProof).toContain('Recent task task-1 reached COMPLETED.');
});

test('downloads demo launch evidence package report through backend API', async () => {
  const blob = new Blob(['# PatchPilot Demo Launch Evidence Package'], { type: 'text/markdown' });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => blob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await downloadDemoLaunchEvidencePackageReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-package/report/download');
  expect(report).toBe(blob);
});

test('archives demo launch evidence package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'launch-evidence-archive-1',
        status: 'READY',
        readyToShare: true,
        summary: 'PatchPilot launch evidence package is ready to share.',
        sessionId: 'demo-session-20260624T003000Z',
        launchReadinessStatus: 'READY',
        evidenceBundleStatus: 'READY',
        handoffFinalizationStatus: 'READY',
        finalHandoffReportPackageArchiveStatus: 'READY',
        finalHandoffReportPackageArchiveReady: true,
        finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
        finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        latestWebhookDeliveryId: 'delivery-1',
        evaluationRunId: 'evaluation-run-2',
        createdAt: '2026-06-28T02:30:00Z',
        report: '# PatchPilot Demo Launch Evidence Package'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoLaunchEvidencePackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-package/archives', {
    method: 'POST'
  });
  expect(archive.id).toBe('launch-evidence-archive-1');
  expect(archive.readyToShare).toBe(true);
  expect(archive.sessionId).toBe('demo-session-20260624T003000Z');
});

test('lists demo launch evidence package archives from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'launch-evidence-archive-1',
          status: 'READY',
          readyToShare: true,
          summary: 'PatchPilot launch evidence package is ready to share.',
          sessionId: 'demo-session-20260624T003000Z',
          launchReadinessStatus: 'READY',
          evidenceBundleStatus: 'READY',
          handoffFinalizationStatus: 'READY',
          finalHandoffReportPackageArchiveStatus: 'READY',
          finalHandoffReportPackageArchiveReady: true,
          finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
          finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
          latestTaskId: 'task-1',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          latestWebhookDeliveryId: 'delivery-1',
          evaluationRunId: 'evaluation-run-2',
          createdAt: '2026-06-28T02:30:00Z',
          report: '# PatchPilot Demo Launch Evidence Package'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoLaunchEvidencePackageArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-package/archives');
  expect(archives[0].id).toBe('launch-evidence-archive-1');
  expect(archives[0].latestPullRequestUrl).toBe('https://github.com/bingqin2/PatchPilot/pull/42');
});

test('downloads archived demo launch evidence package report through backend API', async () => {
  const blob = new Blob(['# PatchPilot Demo Launch Evidence Package'], { type: 'text/markdown' });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => blob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await downloadDemoLaunchEvidencePackageArchiveReport('launch-evidence-archive-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-package/archives/launch-evidence-archive-1/report/download');
  expect(report).toBe(blob);
});

test('gets demo launch evidence share center through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        shareReady: true,
        summary: 'Latest archived launch evidence package is READY and can be shared.',
        nextAction: 'Download the archived launch evidence package and share it with reviewers.',
        archiveCount: 1,
        latestArchiveId: 'launch-evidence-archive-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestCreatedAt: '2026-06-28T02:30:00Z',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        latestWebhookDeliveryId: 'delivery-1',
        evaluationRunId: 'evaluation-run-2',
        finalHandoffReportPackageArchiveStatus: 'READY',
        finalHandoffReportPackageArchiveReady: true,
        finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
        downloadActions: [
          'Download launch evidence package archive launch-evidence-archive-1.',
          'Download launch evidence share center report.'
        ],
        evidenceNotes: [
          'Latest launch evidence archive status is READY.',
          'Latest Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is ready for review.'
        ],
        markdownReport: '# PatchPilot Demo Launch Evidence Share Center',
        generatedAt: '2026-06-28T02:45:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const center = await getDemoLaunchEvidenceShareCenter();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-share-center');
  expect(center.status).toBe('READY');
  expect(center.shareReady).toBe(true);
  expect(center.latestArchiveId).toBe('launch-evidence-archive-1');
  expect(center.downloadActions).toContain('Download launch evidence share center report.');
});

test('downloads demo launch evidence share center report through backend API', async () => {
  const blob = new Blob(['# PatchPilot Demo Launch Evidence Share Center'], { type: 'text/markdown' });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => blob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await downloadDemoLaunchEvidenceShareCenterReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-share-center/report/download');
  expect(report).toBe(blob);
});

test('loads demo launch evidence finalization from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        finalized: true,
        summary: 'Demo launch evidence is finalized with a fresh delivery receipt for the current archive.',
        nextAction: 'Use the finalization report as the launch evidence delivery acceptance record.',
        latestArchiveId: 'launch-evidence-archive-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestDeliveryReceiptId: 'launch-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-28T06:05:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current launch evidence archive and session.',
        checks: [
          {
            name: 'Launch evidence share readiness',
            status: 'READY',
            summary: 'Share center is ready.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Finalization report can be downloaded as the launch delivery acceptance record.'],
        markdownReport: '# PatchPilot Demo Launch Evidence Finalization Gate\n\n- Status: `READY`',
        generatedAt: '2026-06-28T06:30:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const finalization = await getDemoLaunchEvidenceFinalization();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-finalization');
  expect(finalization.status).toBe('READY');
  expect(finalization.finalized).toBe(true);
  expect(finalization.latestDeliveryReceiptId).toBe('launch-delivery-receipt-1');
  expect(finalization.deliveryReceiptFreshness).toBe('FRESH');
  expect(finalization.checks[0].name).toBe('Launch evidence share readiness');
  expect(finalization.markdownReport).toContain('# PatchPilot Demo Launch Evidence Finalization Gate');
});

test('loads demo launch acceptance closeout from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        accepted: true,
        summary: 'PatchPilot launch acceptance closeout is complete.',
        nextAction: 'Use this closeout report as the final self-hosted launch acceptance record.',
        sessionId: 'demo-session-20260624T003000Z',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        latestWebhookDeliveryId: 'delivery-1',
        evaluationRunId: 'evaluation-run-2',
        latestArchiveId: 'launch-evidence-archive-1',
        latestDeliveryReceiptId: 'launch-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-28T06:05:00Z',
        deliveryReceiptFreshness: 'FRESH',
        generatedAt: '2026-06-28T07:15:00Z',
        checks: [
          {
            name: 'Self-hosted launch readiness',
            status: 'READY',
            summary: 'Self-hosted PatchPilot is ready.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Delivery receipt launch-delivery-receipt-1 is fresh for demo-session-20260624T003000Z.'],
        downloadActions: ['Download launch acceptance closeout report.'],
        markdownReport: '# PatchPilot Launch Acceptance Closeout\n\n- Status: `READY`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const closeout = await getDemoLaunchAcceptanceCloseout();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-acceptance-closeout');
  expect(closeout.status).toBe('READY');
  expect(closeout.accepted).toBe(true);
  expect(closeout.latestDeliveryReceiptId).toBe('launch-delivery-receipt-1');
  expect(closeout.checks[0].name).toBe('Self-hosted launch readiness');
  expect(closeout.markdownReport).toContain('# PatchPilot Launch Acceptance Closeout');
});

test('creates demo launch evidence share delivery receipt through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'launch-delivery-receipt-1',
        status: 'READY',
        launchEvidenceArchiveId: 'launch-evidence-archive-1',
        sessionId: 'demo-session-20260624T003000Z',
        deliveryChannel: 'email',
        deliveryTarget: 'reviewer@example.com',
        operator: 'local-operator',
        notes: 'Sent final launch evidence after the smoke demo.',
        messageSubject: 'PatchPilot demo launch evidence: demo-session-20260624T003000Z',
        deliveredAt: '2026-06-28T06:05:00Z',
        createdAt: '2026-06-28T06:10:00Z',
        markdownReport: '# PatchPilot Demo Launch Evidence Delivery Receipt\n\n- Status: `READY`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createDemoLaunchEvidenceShareDeliveryReceipt({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'local-operator',
    notes: 'Sent final launch evidence after the smoke demo.',
    deliveredAt: '2026-06-28T06:05:00Z'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-share-delivery-receipts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      deliveryChannel: 'email',
      deliveryTarget: 'reviewer@example.com',
      operator: 'local-operator',
      notes: 'Sent final launch evidence after the smoke demo.',
      deliveredAt: '2026-06-28T06:05:00Z'
    })
  });
  expect(receipt.id).toBe('launch-delivery-receipt-1');
  expect(receipt.launchEvidenceArchiveId).toBe('launch-evidence-archive-1');
});

test('loads demo launch evidence share delivery receipts from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'launch-delivery-receipt-1',
          status: 'READY',
          launchEvidenceArchiveId: 'launch-evidence-archive-1',
          sessionId: 'demo-session-20260624T003000Z',
          deliveryChannel: 'email',
          deliveryTarget: 'reviewer@example.com',
          operator: 'local-operator',
          notes: 'Sent final launch evidence after the smoke demo.',
          messageSubject: 'PatchPilot demo launch evidence: demo-session-20260624T003000Z',
          deliveredAt: '2026-06-28T06:05:00Z',
          createdAt: '2026-06-28T06:10:00Z',
          markdownReport: '# PatchPilot Demo Launch Evidence Delivery Receipt\n\n- Status: `READY`'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipts = await listDemoLaunchEvidenceShareDeliveryReceipts();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-share-delivery-receipts');
  expect(receipts).toHaveLength(1);
  expect(receipts[0].deliveryTarget).toBe('reviewer@example.com');
});

test('downloads demo launch evidence share delivery receipt markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Launch Evidence Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoLaunchEvidenceShareDeliveryReceiptReport('launch-delivery-receipt-1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/launch-evidence-share-delivery-receipts/launch-delivery-receipt-1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads demo launch evidence finalization markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Launch Evidence Finalization Gate\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoLaunchEvidenceFinalizationReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-evidence-finalization/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads demo launch acceptance closeout markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Launch Acceptance Closeout\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoLaunchAcceptanceCloseoutReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-acceptance-closeout/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('loads demo launch acceptance certificate from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        certified: true,
        summary: 'PatchPilot launch acceptance is certified from the latest accepted closeout archive.',
        nextAction: 'Share the certificate and archived closeout report with reviewers.',
        archiveCount: 1,
        latestCloseoutArchiveId: 'launch-closeout-archive-1',
        latestLaunchEvidenceArchiveId: 'launch-evidence-archive-1',
        latestDeliveryReceiptId: 'launch-delivery-receipt-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        latestWebhookDeliveryId: 'delivery-1',
        evaluationRunId: 'evaluation-run-2',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        deliveryReceiptFreshness: 'FRESH',
        latestArchivedAt: '2026-06-28T08:30:00Z',
        generatedAt: '2026-06-28T09:00:00Z',
        downloadActions: ['Download launch acceptance certificate.'],
        markdownReport: '# PatchPilot Launch Acceptance Certificate'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const certificate = await getDemoLaunchAcceptanceCertificate();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-acceptance-certificate');
  expect(certificate.certified).toBe(true);
  expect(certificate.latestCloseoutArchiveId).toBe('launch-closeout-archive-1');
  expect(certificate.latestDeliveryReceiptId).toBe('launch-delivery-receipt-1');
});

test('downloads demo launch acceptance certificate markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Launch Acceptance Certificate\n\n- Certified: `true`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoLaunchAcceptanceCertificateReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-acceptance-certificate/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('loads final demo acceptance summary through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        accepted: true,
        summary: 'PatchPilot final demo acceptance is ready for external review.',
        nextAction: 'Share the launch and task evidence certificates with reviewers.',
        launchCertificateStatus: 'READY',
        launchCertificateArchived: true,
        launchCertificateCertified: true,
        launchCertificateArchiveId: 'launch-certificate-archive-1',
        launchCloseoutArchiveId: 'launch-closeout-archive-1',
        launchEvidenceArchiveId: 'launch-evidence-archive-1',
        launchDeliveryReceiptId: 'launch-delivery-receipt-1',
        taskCertificateStatus: 'READY',
        taskCertificateArchived: true,
        taskCertificateCertified: true,
        taskCertificateArchiveId: 'task-evidence-certificate-archive-1',
        taskCloseoutArchiveId: 'task-evidence-closeout-archive-1',
        taskEvidenceArchiveId: 'task-evidence-archive-1',
        taskDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        generatedAt: '2026-06-28T09:20:00Z',
        checks: [
          {
            name: 'Launch acceptance certificate',
            status: 'READY',
            summary: 'Latest launch acceptance certificate archive is certified.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Launch acceptance certificate archive launch-certificate-archive-1 is certified.'],
        downloadActions: ['Download final demo acceptance summary.'],
        sideEffectContract:
          'GET /api/demo/acceptance-summary is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.',
        markdownReport: '# PatchPilot Final Demo Acceptance Summary'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const summary = await getDemoAcceptanceSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/acceptance-summary');
  expect(summary.accepted).toBe(true);
  expect(summary.launchCertificateArchiveId).toBe('launch-certificate-archive-1');
  expect(summary.taskCertificateArchiveId).toBe('task-evidence-certificate-archive-1');
});

test('downloads final demo acceptance summary report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Demo Acceptance Summary'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoAcceptanceSummaryReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/acceptance-summary/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('loads final demo acceptance share package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        sendReady: true,
        summary: 'PatchPilot final demo acceptance package is ready to send.',
        nextAction: 'Send the prepared final acceptance message with all required attachments.',
        launchCertificateArchiveId: 'launch-certificate-archive-1',
        taskCertificateArchiveId: 'task-evidence-certificate-archive-1',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        recommendedRecipients: ['Repository owner or maintainer', 'Demo reviewer'],
        requiredAttachments: ['Final demo acceptance summary report'],
        preSendChecks: ['Confirm final demo acceptance status is READY and accepted.'],
        messageSubject: 'PatchPilot final demo acceptance: task-1',
        messageBody: 'PatchPilot final demo acceptance is ready for external review.',
        evidenceNotes: ['Final acceptance status is READY.'],
        sideEffectContract:
          'GET /api/demo/final-acceptance-share-package is read-only: it does not create tasks, call the model, run tests, mutate Git, archive records, record receipts, send messages, or write to GitHub.',
        markdownReport: '# PatchPilot Final Demo Acceptance Share Package',
        generatedAt: '2026-06-28T15:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const sharePackage = await getDemoFinalAcceptanceSharePackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-package');
  expect(sharePackage.sendReady).toBe(true);
  expect(sharePackage.messageSubject).toBe('PatchPilot final demo acceptance: task-1');
  expect(sharePackage.requiredAttachments).toContain('Final demo acceptance summary report');
});

test('downloads final demo acceptance share package report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Demo Acceptance Share Package'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceSharePackageReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-package/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final demo acceptance share package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-acceptance-share-package-archive-1',
        status: 'READY',
        sendReady: true,
        summary: 'PatchPilot final demo acceptance package is ready to send.',
        nextAction: 'Send the prepared final acceptance message with all required attachments.',
        launchCertificateArchiveId: 'launch-certificate-archive-1',
        taskCertificateArchiveId: 'task-evidence-certificate-archive-1',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        recommendedRecipients: ['Repository owner or maintainer', 'Demo reviewer'],
        requiredAttachments: ['Final demo acceptance summary report'],
        preSendChecks: ['Confirm final demo acceptance status is READY and accepted.'],
        messageSubject: 'PatchPilot final demo acceptance: task-1',
        messageBody: 'PatchPilot final demo acceptance is ready for external review.',
        evidenceNotes: ['Final acceptance status is READY.'],
        sideEffectContract:
          'POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.',
        report: '# PatchPilot Final Demo Acceptance Share Package',
        generatedAt: '2026-06-29T01:30:00Z',
        archivedAt: '2026-06-29T02:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalAcceptanceSharePackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-package/archives', { method: 'POST' });
  expect(archive.id).toBe('final-acceptance-share-package-archive-1');
  expect(archive.sendReady).toBe(true);
});

test('lists final demo acceptance share package archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-acceptance-share-package-archive-1',
          status: 'READY',
          sendReady: true,
          summary: 'PatchPilot final demo acceptance package is ready to send.',
          nextAction: 'Send the prepared final acceptance message with all required attachments.',
          launchCertificateArchiveId: 'launch-certificate-archive-1',
          taskCertificateArchiveId: 'task-evidence-certificate-archive-1',
          latestTaskId: 'task-1',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          recommendedRecipients: ['Repository owner or maintainer', 'Demo reviewer'],
          requiredAttachments: ['Final demo acceptance summary report'],
          preSendChecks: ['Confirm final demo acceptance status is READY and accepted.'],
          messageSubject: 'PatchPilot final demo acceptance: task-1',
          messageBody: 'PatchPilot final demo acceptance is ready for external review.',
          evidenceNotes: ['Final acceptance status is READY.'],
          sideEffectContract:
            'POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.',
          report: '# PatchPilot Final Demo Acceptance Share Package',
          generatedAt: '2026-06-29T01:30:00Z',
          archivedAt: '2026-06-29T02:00:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalAcceptanceSharePackageArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-package/archives');
  expect(archives).toHaveLength(1);
  expect(archives[0].id).toBe('final-acceptance-share-package-archive-1');
});

test('downloads final demo acceptance share package archive report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Demo Acceptance Share Package'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceSharePackageArchiveReport('archive/1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-package/archives/archive%2F1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('records final demo acceptance share delivery receipt through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-acceptance-delivery-receipt-1',
        status: 'READY',
        finalAcceptanceSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
        latestTaskId: 'task-1',
        deliveryChannel: 'email',
        deliveryTarget: 'reviewer@example.com',
        operator: 'local-operator',
        notes: 'Sent final acceptance share package to the reviewer.',
        messageSubject: 'PatchPilot final demo acceptance: task-1',
        deliveredAt: '2026-06-29T03:05:00Z',
        createdAt: '2026-06-29T03:10:00Z',
        markdownReport: '# PatchPilot Final Demo Acceptance Share Delivery Receipt'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createDemoFinalAcceptanceShareDeliveryReceipt({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'local-operator',
    notes: 'Sent final acceptance share package to the reviewer.'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-delivery-receipts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      deliveryChannel: 'email',
      deliveryTarget: 'reviewer@example.com',
      operator: 'local-operator',
      notes: 'Sent final acceptance share package to the reviewer.'
    })
  });
  expect(receipt.id).toBe('final-acceptance-delivery-receipt-1');
  expect(receipt.finalAcceptanceSharePackageArchiveId).toBe('final-acceptance-share-package-archive-1');
});

test('lists final demo acceptance share delivery receipts through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-acceptance-delivery-receipt-1',
          status: 'READY',
          finalAcceptanceSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
          latestTaskId: 'task-1',
          deliveryChannel: 'email',
          deliveryTarget: 'reviewer@example.com',
          operator: 'local-operator',
          notes: 'Sent final acceptance share package to the reviewer.',
          messageSubject: 'PatchPilot final demo acceptance: task-1',
          deliveredAt: '2026-06-29T03:05:00Z',
          createdAt: '2026-06-29T03:10:00Z',
          markdownReport: '# PatchPilot Final Demo Acceptance Share Delivery Receipt'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipts = await listDemoFinalAcceptanceShareDeliveryReceipts();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-delivery-receipts');
  expect(receipts).toHaveLength(1);
  expect(receipts[0].id).toBe('final-acceptance-delivery-receipt-1');
});

test('downloads final demo acceptance share delivery receipt report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Demo Acceptance Share Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceShareDeliveryReceiptReport('receipt/1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-delivery-receipts/receipt%2F1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('loads final demo acceptance share finalization through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        finalized: true,
        summary: 'Final demo acceptance share package is finalized with a fresh delivery receipt.',
        nextAction: 'Use the finalization report as the external-review acceptance delivery record.',
        latestArchiveId: 'final-acceptance-share-package-archive-1',
        latestTaskId: 'task-1',
        latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T03:05:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current final acceptance share package archive.',
        checks: [],
        evidenceNotes: [],
        markdownReport: '# PatchPilot Final Demo Acceptance Share Finalization Gate',
        generatedAt: '2026-06-29T03:30:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const finalization = await getDemoFinalAcceptanceShareFinalization();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-finalization');
  expect(finalization.finalized).toBe(true);
  expect(finalization.latestDeliveryReceiptId).toBe('final-acceptance-delivery-receipt-1');
});

test('downloads final demo acceptance share finalization report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Demo Acceptance Share Finalization Gate'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceShareFinalizationReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-share-finalization/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final acceptance completion through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-acceptance-completion-archive-1',
        status: 'READY',
        finalized: true,
        summary: 'Final demo acceptance share package is finalized with a fresh delivery receipt.',
        nextAction: 'Use the finalization report as the external-review acceptance delivery record.',
        latestArchiveId: 'final-acceptance-share-package-archive-1',
        latestTaskId: 'task-1',
        latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T03:05:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current final acceptance share package archive.',
        evidenceNotes: ['Latest final acceptance share package archive is send-ready.'],
        report: '# PatchPilot Final Demo Acceptance Share Finalization Gate',
        generatedAt: '2026-06-29T03:30:00Z',
        archivedAt: '2026-06-29T04:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalAcceptanceCompletion();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-archives', { method: 'POST' });
  expect(archive.id).toBe('final-acceptance-completion-archive-1');
  expect(archive.finalized).toBe(true);
  expect(archive.latestDeliveryReceiptId).toBe('final-acceptance-delivery-receipt-1');
});

test('lists final acceptance completion archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-acceptance-completion-archive-1',
          status: 'READY',
          finalized: true,
          summary: 'Final demo acceptance share package is finalized with a fresh delivery receipt.',
          nextAction: 'Use the finalization report as the external-review acceptance delivery record.',
          latestArchiveId: 'final-acceptance-share-package-archive-1',
          latestTaskId: 'task-1',
          latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          latestDeliveredAt: '2026-06-29T03:05:00Z',
          deliveryReceiptFreshness: 'FRESH',
          deliveryReceiptFresh: true,
          deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current final acceptance share package archive.',
          evidenceNotes: ['Latest final acceptance share package archive is send-ready.'],
          report: '# PatchPilot Final Demo Acceptance Share Finalization Gate',
          generatedAt: '2026-06-29T03:30:00Z',
          archivedAt: '2026-06-29T04:00:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalAcceptanceCompletionArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-archives');
  expect(archives).toHaveLength(1);
  expect(archives[0].id).toBe('final-acceptance-completion-archive-1');
});

test('downloads final acceptance completion archive report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Demo Acceptance Share Finalization Gate'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceCompletionArchiveReport('completion/1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-archives/completion%2F1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('loads final acceptance completion evidence bundle through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        readyToShare: true,
        summary: 'PatchPilot final acceptance completion evidence bundle is ready to share.',
        nextAction: 'Share this final acceptance completion evidence bundle with reviewers.',
        latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
        latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
        latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestTaskId: 'task-1',
        completionArchiveCount: 1,
        latestArchivedAt: '2026-06-29T04:00:00Z',
        generatedAt: '2026-06-29T04:05:00Z',
        evidenceNotes: ['Latest completion archive final-acceptance-completion-archive-1 is finalized.'],
        downloadActions: ['Download final acceptance completion evidence bundle.'],
        sideEffectContract:
          'GET /api/demo/final-acceptance-completion-evidence-bundle is read-only: it does not create tasks, call the model, run tests, archive records, record receipts, mutate Git, send messages, or write to GitHub.',
        markdownReport: '# PatchPilot Final Acceptance Completion Evidence Bundle'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const bundle = await getDemoFinalAcceptanceCompletionEvidenceBundle();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-evidence-bundle');
  expect(bundle.readyToShare).toBe(true);
  expect(bundle.latestCompletionArchiveId).toBe('final-acceptance-completion-archive-1');
  expect(bundle.latestDeliveryReceiptId).toBe('final-acceptance-delivery-receipt-1');
});

test('downloads final acceptance completion evidence bundle report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Acceptance Completion Evidence Bundle'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceCompletionEvidenceBundleReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-evidence-bundle/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches final acceptance completion evidence delivery finalization through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        finalized: true,
        summary: 'Final acceptance completion evidence delivery is finalized with a fresh delivery receipt.',
        nextAction: 'Use the finalization report as the reviewer-facing completion delivery record.',
        latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
        latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
        latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
        latestTaskId: 'task-1',
        latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T04:25:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        deliveryReceiptFreshnessSummary:
          'Latest completion evidence delivery receipt matches the current completion evidence bundle.',
        checks: [
          {
            name: 'Completion evidence bundle',
            status: 'READY',
            summary: 'Completion evidence bundle is ready to share.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: [
          'Completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1 is fresh.'
        ],
        downloadActions: ['Download final acceptance completion evidence delivery finalization report.'],
        sideEffectContract:
          'GET /api/demo/final-acceptance-completion-evidence-delivery-finalization is read-only.',
        markdownReport: '# PatchPilot Final Acceptance Completion Evidence Delivery Finalization',
        generatedAt: '2026-06-29T05:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const finalization = await getDemoFinalAcceptanceCompletionEvidenceDeliveryFinalization();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-evidence-delivery-finalization');
  expect(finalization.finalized).toBe(true);
  expect(finalization.deliveryReceiptFreshness).toBe('FRESH');
  expect(finalization.latestCompletionEvidenceDeliveryReceiptId)
    .toBe('final-acceptance-completion-evidence-delivery-receipt-1');
});

test('downloads final acceptance completion evidence delivery finalization report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Acceptance Completion Evidence Delivery Finalization'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationReport();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-acceptance-completion-evidence-delivery-finalization/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches final acceptance completion closeout through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        closed: true,
        summary:
          'PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.',
        nextAction: 'Use this closeout report as the final external-review completion record.',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
        latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
        latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T04:25:00Z',
        deliveryReceiptFreshness: 'FRESH',
        checks: [
          {
            name: 'Final acceptance summary',
            status: 'READY',
            summary: 'Final demo acceptance summary is accepted.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Final demo acceptance summary is accepted.'],
        downloadActions: ['Download final acceptance completion closeout report.'],
        sideEffectContract: 'GET /api/demo/final-acceptance-completion-closeout is read-only.',
        markdownReport: '# PatchPilot Final Acceptance Completion Closeout',
        generatedAt: '2026-06-29T06:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const closeout = await getDemoFinalAcceptanceCompletionCloseout();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-closeout');
  expect(closeout.closed).toBe(true);
  expect(closeout.latestCompletionEvidenceDeliveryReceiptId)
    .toBe('final-acceptance-completion-evidence-delivery-receipt-1');
});

test('downloads final acceptance completion closeout report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Acceptance Completion Closeout'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceCompletionCloseoutReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-closeout/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches final external-review evidence package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        readyForExternalReview: true,
        summary: 'PatchPilot final external-review evidence package is ready.',
        nextAction: 'Share this package with reviewers as the frozen external-review record.',
        finalAcceptanceSummaryStatus: 'READY',
        finalAcceptanceShareFinalizationStatus: 'READY',
        completionEvidenceBundleStatus: 'READY',
        completionDeliveryFinalizationStatus: 'READY',
        completionCloseoutStatus: 'READY',
        closeoutArchiveStatus: 'READY',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        finalAcceptanceSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
        completionArchiveId: 'final-acceptance-completion-archive-1',
        completionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
        closeoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
        deliveryTarget: 'reviewer@example.com',
        deliveryChannel: 'email',
        deliveredAt: '2026-06-29T04:25:00Z',
        deliveryReceiptFreshness: 'FRESH',
        closeoutArchivedAt: '2026-06-29T06:30:00Z',
        generatedAt: '2026-06-29T07:00:00Z',
        checks: [
          {
            name: 'Final demo acceptance summary',
            status: 'READY',
            summary: 'Final demo acceptance summary is accepted.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed.'],
        downloadActions: ['Download final external-review evidence package.'],
        sideEffectContract: 'GET /api/demo/final-external-review-evidence-package is read-only.',
        markdownReport: '# PatchPilot Final External Review Evidence Package'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const evidencePackage = await getDemoFinalExternalReviewEvidencePackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-evidence-package');
  expect(evidencePackage.readyForExternalReview).toBe(true);
  expect(evidencePackage.closeoutArchiveId).toBe('final-acceptance-completion-closeout-archive-1');
});

test('downloads final external-review evidence package through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Evidence Package'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewEvidencePackageReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-evidence-package/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final external-review evidence package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-external-review-package-archive-1',
        status: 'READY',
        readyForExternalReview: true,
        summary: 'PatchPilot final external-review evidence package is ready.',
        nextAction: 'Share this package with reviewers as the frozen external-review record.',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        finalAcceptanceSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
        completionArchiveId: 'final-acceptance-completion-archive-1',
        completionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
        closeoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
        deliveryTarget: 'reviewer@example.com',
        deliveryChannel: 'email',
        deliveredAt: '2026-06-29T04:25:00Z',
        deliveryReceiptFreshness: 'FRESH',
        closeoutArchivedAt: '2026-06-29T06:30:00Z',
        evidenceNotes: ['Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed.'],
        downloadActions: ['Download final external-review evidence package.'],
        sideEffectContract: 'POST /api/demo/final-external-review-evidence-package/archives archives the READY package.',
        report: '# PatchPilot Final External Review Evidence Package Archive',
        generatedAt: '2026-06-29T07:00:00Z',
        archivedAt: '2026-06-29T07:10:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalExternalReviewEvidencePackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-evidence-package/archives', {
    method: 'POST'
  });
  expect(archive.id).toBe('final-external-review-package-archive-1');
  expect(archive.readyForExternalReview).toBe(true);
});

test('lists final external-review evidence package archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-external-review-package-archive-1',
          status: 'READY',
          readyForExternalReview: true,
          summary: 'PatchPilot final external-review evidence package is ready.',
          nextAction: 'Share this package with reviewers as the frozen external-review record.',
          latestTaskId: 'task-8',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          finalAcceptanceSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
          completionArchiveId: 'final-acceptance-completion-archive-1',
          completionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
          closeoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
          deliveryTarget: 'reviewer@example.com',
          deliveryChannel: 'email',
          deliveredAt: '2026-06-29T04:25:00Z',
          deliveryReceiptFreshness: 'FRESH',
          closeoutArchivedAt: '2026-06-29T06:30:00Z',
          evidenceNotes: ['Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed.'],
          downloadActions: ['Download final external-review evidence package.'],
          sideEffectContract: 'GET /api/demo/final-external-review-evidence-package/archives is read-only.',
          report: '# PatchPilot Final External Review Evidence Package Archive',
          generatedAt: '2026-06-29T07:00:00Z',
          archivedAt: '2026-06-29T07:10:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalExternalReviewEvidencePackageArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-evidence-package/archives');
  expect(archives).toHaveLength(1);
  expect(archives[0].id).toBe('final-external-review-package-archive-1');
});

test('downloads final external-review evidence package archive report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Evidence Package Archive'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport =
    await downloadDemoFinalExternalReviewEvidencePackageArchiveReport('final/package archive 1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-evidence-package/archives/final%2Fpackage%20archive%201/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('records final external-review package delivery receipt through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-external-review-package-delivery-receipt-1',
        status: 'READY',
        finalExternalReviewPackageArchiveStatus: 'READY',
        finalExternalReviewPackageArchiveId: 'final-external-review-package-archive-1',
        closeoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
        completionArchiveId: 'final-acceptance-completion-archive-1',
        completionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        summary: 'PatchPilot final external-review evidence package archive was delivered.',
        nextAction: 'Use the delivery receipt as proof that the frozen final external-review package was shared.',
        deliveryChannel: 'email',
        deliveryTarget: 'reviewer@example.com',
        operator: 'release-captain',
        notes: 'Sent to reviewer mailbox.',
        deliveredAt: '2026-06-29T07:20:00Z',
        createdAt: '2026-06-29T07:25:00Z',
        markdownReport: '# PatchPilot Final External Review Package Delivery Receipt'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createDemoFinalExternalReviewEvidencePackageDeliveryReceipt({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'release-captain',
    notes: 'Sent to reviewer mailbox.'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-evidence-package/delivery-receipts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      deliveryChannel: 'email',
      deliveryTarget: 'reviewer@example.com',
      operator: 'release-captain',
      notes: 'Sent to reviewer mailbox.'
    })
  });
  expect(receipt.id).toBe('final-external-review-package-delivery-receipt-1');
  expect(receipt.finalExternalReviewPackageArchiveId).toBe('final-external-review-package-archive-1');
});

test('lists final external-review package delivery receipts through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-external-review-package-delivery-receipt-1',
          status: 'READY',
          finalExternalReviewPackageArchiveStatus: 'READY',
          finalExternalReviewPackageArchiveId: 'final-external-review-package-archive-1',
          closeoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
          completionArchiveId: 'final-acceptance-completion-archive-1',
          completionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
          latestTaskId: 'task-8',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          summary: 'PatchPilot final external-review evidence package archive was delivered.',
          nextAction: 'Use the delivery receipt as proof that the frozen final external-review package was shared.',
          deliveryChannel: 'email',
          deliveryTarget: 'reviewer@example.com',
          operator: 'release-captain',
          notes: 'Sent to reviewer mailbox.',
          deliveredAt: '2026-06-29T07:20:00Z',
          createdAt: '2026-06-29T07:25:00Z',
          markdownReport: '# PatchPilot Final External Review Package Delivery Receipt'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipts = await listDemoFinalExternalReviewEvidencePackageDeliveryReceipts();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-evidence-package/delivery-receipts');
  expect(receipts).toHaveLength(1);
  expect(receipts[0].deliveryTarget).toBe('reviewer@example.com');
});

test('downloads final external-review package delivery receipt report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Package Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport =
    await downloadDemoFinalExternalReviewEvidencePackageDeliveryReceiptReport('receipt/1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-evidence-package/delivery-receipts/receipt%2F1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches final external-review package delivery finalization through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        finalized: true,
        summary: 'Final external-review package delivery is finalized with a fresh package delivery receipt.',
        nextAction: 'Use the finalization report as proof that the frozen external-review package was delivered.',
        latestArchiveId: 'final-external-review-package-archive-1',
        latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestCloseoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
        latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
        latestCompletionEvidenceDeliveryReceiptId:
          'final-acceptance-completion-evidence-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T07:20:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        deliveryReceiptFreshnessSummary:
          'Latest package delivery receipt matches the current frozen final external-review package.',
        checks: [
          {
            name: 'Frozen final external-review package',
            status: 'READY',
            summary: 'Frozen final external-review package is ready.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Frozen final external-review package final-external-review-package-archive-1 is ready.'],
        downloadActions: ['Download final external-review package delivery finalization report.'],
        sideEffectContract:
          'GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only.',
        markdownReport: '# PatchPilot Final External Review Package Delivery Finalization',
        generatedAt: '2026-06-29T10:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const finalization = await getDemoFinalExternalReviewEvidencePackageDeliveryFinalization();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-evidence-package/delivery-finalization'
  );
  expect(finalization.finalized).toBe(true);
  expect(finalization.latestDeliveryReceiptId)
    .toBe('final-external-review-package-delivery-receipt-1');
});

test('downloads final external-review package delivery finalization report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Package Delivery Finalization'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationReport();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-evidence-package/delivery-finalization/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final external-review package delivery finalization through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-external-review-package-delivery-finalization-archive-1',
        status: 'READY',
        finalized: true,
        summary: 'Final external-review package delivery finalization archive is ready.',
        nextAction: 'Use this archive as the frozen external-review package delivery closure record.',
        latestArchiveId: 'final-external-review-package-archive-1',
        latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestCloseoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
        latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
        latestCompletionEvidenceDeliveryReceiptId:
          'final-acceptance-completion-evidence-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T07:20:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        deliveryReceiptFreshnessSummary:
          'Latest package delivery receipt matches the current frozen final external-review package.',
        checks: [
          {
            name: 'Frozen final external-review package',
            status: 'READY',
            summary: 'Frozen final external-review package is ready.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Frozen final external-review package final-external-review-package-archive-1 is ready.'],
        downloadActions: [
          'Download final external-review package delivery finalization archive final-external-review-package-delivery-finalization-archive-1.'
        ],
        sideEffectContract:
          'POST /api/demo/final-external-review-evidence-package/delivery-finalization/archives archives the current READY delivery finalization.',
        report: '# PatchPilot Final External Review Package Delivery Finalization Archive',
        generatedAt: '2026-06-29T10:00:00Z',
        archivedAt: '2026-06-29T10:05:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalExternalReviewEvidencePackageDeliveryFinalization();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-evidence-package/delivery-finalization/archives',
    { method: 'POST' }
  );
  expect(archive.id).toBe('final-external-review-package-delivery-finalization-archive-1');
  expect(archive.finalized).toBe(true);
});

test('lists final external-review package delivery finalization archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-external-review-package-delivery-finalization-archive-1',
          status: 'READY',
          finalized: true,
          summary: 'Final external-review package delivery finalization archive is ready.',
          nextAction: 'Use this archive as the frozen external-review package delivery closure record.',
          latestArchiveId: 'final-external-review-package-archive-1',
          latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
          latestCloseoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
          latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
          latestCompletionEvidenceDeliveryReceiptId:
            'final-acceptance-completion-evidence-delivery-receipt-1',
          latestTaskId: 'task-8',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          latestDeliveredAt: '2026-06-29T07:20:00Z',
          deliveryReceiptFreshness: 'FRESH',
          deliveryReceiptFresh: true,
          deliveryReceiptFreshnessSummary:
            'Latest package delivery receipt matches the current frozen final external-review package.',
          checks: [],
          evidenceNotes: [],
          downloadActions: [
            'Download final external-review package delivery finalization archive final-external-review-package-delivery-finalization-archive-1.'
          ],
          sideEffectContract:
            'POST /api/demo/final-external-review-evidence-package/delivery-finalization/archives archives the current READY delivery finalization.',
          report: '# PatchPilot Final External Review Package Delivery Finalization Archive',
          generatedAt: '2026-06-29T10:00:00Z',
          archivedAt: '2026-06-29T10:05:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchives();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-evidence-package/delivery-finalization/archives'
  );
  expect(archives[0].latestDeliveryReceiptId)
    .toBe('final-external-review-package-delivery-receipt-1');
});

test('downloads final external-review package delivery finalization archive report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Package Delivery Finalization Archive'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveReport(
    'finalization/archive 1'
  );

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-evidence-package/delivery-finalization/archives/finalization%2Farchive%201/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches final external-review delivery certificate through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        certified: true,
        summary: 'Final external-review delivery is certified from the latest finalized archive.',
        nextAction: 'Use this certificate as the final external-review delivery proof.',
        latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T07:20:00Z',
        latestArchivedAt: '2026-06-29T10:05:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        checks: [
          {
            name: 'Finalized external-review delivery archive',
            status: 'READY',
            summary: 'Latest external-review delivery finalization archive is READY and finalized.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Final external-review delivery archive final-external-review-package-delivery-finalization-archive-1 is certified.'],
        downloadActions: ['Download final external-review delivery certificate report.'],
        sideEffectContract:
          'GET /api/demo/final-external-review-delivery-certificate is read-only.',
        markdownReport: '# PatchPilot Final External Review Delivery Certificate',
        generatedAt: '2026-06-29T10:10:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const certificate = await getDemoFinalExternalReviewDeliveryCertificate();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-delivery-certificate');
  expect(certificate.certified).toBe(true);
  expect(certificate.latestDeliveryFinalizationArchiveId)
    .toBe('final-external-review-package-delivery-finalization-archive-1');
});

test('downloads final external-review delivery certificate report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Delivery Certificate'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewDeliveryCertificateReport();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-delivery-certificate/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final external-review delivery certificate through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-external-review-delivery-certificate-archive-1',
        status: 'READY',
        certified: true,
        summary: 'Final external-review delivery is certified from the latest finalized archive.',
        nextAction: 'Use this certificate as the final external-review delivery proof.',
        latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T07:20:00Z',
        latestArchivedAt: '2026-06-29T10:05:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        checks: [],
        evidenceNotes: [],
        downloadActions: ['Download final external-review delivery certificate report.'],
        sideEffectContract: 'certificate archive creation writes PatchPilot-local evidence only.',
        report: '# PatchPilot Final External Review Delivery Certificate',
        generatedAt: '2026-06-29T10:10:00Z',
        archivedAt: '2026-06-29T10:20:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalExternalReviewDeliveryCertificate();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-delivery-certificate/archives',
    { method: 'POST' }
  );
  expect(archive.certified).toBe(true);
  expect(archive.id).toBe('final-external-review-delivery-certificate-archive-1');
});

test('lists final external-review delivery certificate archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-external-review-delivery-certificate-archive-1',
          status: 'READY',
          certified: true,
          summary: 'Final external-review delivery is certified from the latest finalized archive.',
          nextAction: 'Use this certificate as the final external-review delivery proof.',
          latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
          latestPackageArchiveId: 'final-external-review-package-archive-1',
          latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
          latestTaskId: 'task-8',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          latestDeliveredAt: '2026-06-29T07:20:00Z',
          latestArchivedAt: '2026-06-29T10:05:00Z',
          deliveryReceiptFreshness: 'FRESH',
          deliveryReceiptFresh: true,
          checks: [],
          evidenceNotes: [],
          downloadActions: [],
          sideEffectContract: 'certificate archive creation writes PatchPilot-local evidence only.',
          report: '# PatchPilot Final External Review Delivery Certificate',
          generatedAt: '2026-06-29T10:10:00Z',
          archivedAt: '2026-06-29T10:20:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalExternalReviewDeliveryCertificateArchives();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-delivery-certificate/archives'
  );
  expect(archives).toHaveLength(1);
  expect(archives[0].latestDeliveryReceiptId).toBe('final-external-review-package-delivery-receipt-1');
});

test('downloads final external-review delivery certificate archive report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Delivery Certificate'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewDeliveryCertificateArchiveReport(
    'certificate/archive 1'
  );

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-delivery-certificate/archives/certificate%2Farchive%201/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches final external-review release bundle through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        releaseReady: true,
        summary: 'PatchPilot final external-review release bundle is ready.',
        nextAction: 'Share the release bundle report and listed attachments with external reviewers.',
        latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T07:20:00Z',
        latestCertificateArchivedAt: '2026-06-29T10:20:00Z',
        generatedAt: '2026-06-29T10:30:00Z',
        requiredAttachments: [
          'Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1'
        ],
        releaseChecks: [
          {
            name: 'Final delivery certificate archive',
            status: 'READY',
            summary: 'Latest final external-review delivery certificate archive is certified.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: [
          'Certified final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release source of truth.'
        ],
        downloadActions: ['Download final external-review release bundle report.'],
        sideEffectContract: 'GET /api/demo/final-external-review-release-bundle is read-only.',
        markdownReport: '# PatchPilot Final External Review Release Bundle'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const bundle = await getDemoFinalExternalReviewReleaseBundle();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-release-bundle');
  expect(bundle.releaseReady).toBe(true);
  expect(bundle.latestCertificateArchiveId)
    .toBe('final-external-review-delivery-certificate-archive-1');
});

test('downloads final external-review release bundle report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Release Bundle'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewReleaseBundleReport();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final external-review release bundle through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-external-review-release-bundle-archive-1',
        status: 'READY',
        releaseReady: true,
        summary: 'PatchPilot final external-review release bundle archive is frozen.',
        nextAction: 'Use this frozen release bundle archive as the external review source.',
        latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T07:20:00Z',
        latestCertificateArchivedAt: '2026-06-29T10:20:00Z',
        generatedAt: '2026-06-29T10:30:00Z',
        archivedAt: '2026-06-29T10:35:00Z',
        requiredAttachments: [
          'Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1'
        ],
        releaseChecks: [
          {
            name: 'Final delivery certificate archive',
            status: 'READY',
            summary: 'Latest final external-review delivery certificate archive is certified.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: [
          'Certified final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release source of truth.'
        ],
        downloadActions: ['Download final external-review release bundle archive report.'],
        sideEffectContract:
          'POST /api/demo/final-external-review-release-bundle/archives archives the current READY release bundle.',
        report: '# PatchPilot Final External Review Release Bundle Archive'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalExternalReviewReleaseBundle();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/archives',
    { method: 'POST' }
  );
  expect(archive.id).toBe('final-external-review-release-bundle-archive-1');
  expect(archive.releaseReady).toBe(true);
});

test('lists final external-review release bundle archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-external-review-release-bundle-archive-1',
          status: 'READY',
          releaseReady: true,
          summary: 'PatchPilot final external-review release bundle archive is frozen.',
          nextAction: 'Use this frozen release bundle archive as the external review source.',
          latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
          latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
          latestPackageArchiveId: 'final-external-review-package-archive-1',
          latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
          latestTaskId: 'task-8',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          latestDeliveredAt: '2026-06-29T07:20:00Z',
          latestCertificateArchivedAt: '2026-06-29T10:20:00Z',
          generatedAt: '2026-06-29T10:30:00Z',
          archivedAt: '2026-06-29T10:35:00Z',
          requiredAttachments: [
            'Final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1'
          ],
          releaseChecks: [
            {
              name: 'Final delivery certificate archive',
              status: 'READY',
              summary: 'Latest final external-review delivery certificate archive is certified.',
              nextAction: 'No action needed.'
            }
          ],
          evidenceNotes: [
            'Certified final external-review delivery certificate archive final-external-review-delivery-certificate-archive-1 is the release source of truth.'
          ],
          downloadActions: ['Download final external-review release bundle archive report.'],
          sideEffectContract:
            'POST /api/demo/final-external-review-release-bundle/archives archives the current READY release bundle.',
          report: '# PatchPilot Final External Review Release Bundle Archive'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalExternalReviewReleaseBundleArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-release-bundle/archives');
  expect(archives[0].id).toBe('final-external-review-release-bundle-archive-1');
});

test('downloads final external-review release bundle archive report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Release Bundle Archive'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewReleaseBundleArchiveReport(
    'release bundle/archive 1'
  );

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/archives/release%20bundle%2Farchive%201/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('records final external-review release bundle delivery receipt through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-external-review-release-bundle-delivery-receipt-1',
        status: 'READY',
        releaseBundleArchiveStatus: 'READY',
        releaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
        latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        summary: 'PatchPilot final external-review release bundle archive was delivered.',
        nextAction: 'Use the receipt as proof that the frozen release bundle was shared.',
        deliveryChannel: 'email',
        deliveryTarget: 'reviewer@example.com',
        operator: 'release-captain',
        notes: 'Sent frozen release bundle to reviewer mailbox.',
        deliveredAt: '2026-06-29T11:00:00Z',
        createdAt: '2026-06-29T11:05:00Z',
        markdownReport: '# PatchPilot Final External Review Release Bundle Delivery Receipt'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createDemoFinalExternalReviewReleaseBundleDeliveryReceipt({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'release-captain',
    notes: 'Sent frozen release bundle to reviewer mailbox.'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-release-bundle/delivery-receipts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      deliveryChannel: 'email',
      deliveryTarget: 'reviewer@example.com',
      operator: 'release-captain',
      notes: 'Sent frozen release bundle to reviewer mailbox.'
    })
  });
  expect(receipt.id).toBe('final-external-review-release-bundle-delivery-receipt-1');
  expect(receipt.releaseBundleArchiveId).toBe('final-external-review-release-bundle-archive-1');
});

test('lists final external-review release bundle delivery receipts through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-external-review-release-bundle-delivery-receipt-1',
          status: 'READY',
          releaseBundleArchiveStatus: 'READY',
          releaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
          latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
          latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
          latestPackageArchiveId: 'final-external-review-package-archive-1',
          latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
          latestTaskId: 'task-8',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          summary: 'PatchPilot final external-review release bundle archive was delivered.',
          nextAction: 'Use the receipt as proof that the frozen release bundle was shared.',
          deliveryChannel: 'email',
          deliveryTarget: 'reviewer@example.com',
          operator: 'release-captain',
          notes: 'Sent frozen release bundle to reviewer mailbox.',
          deliveredAt: '2026-06-29T11:00:00Z',
          createdAt: '2026-06-29T11:05:00Z',
          markdownReport: '# PatchPilot Final External Review Release Bundle Delivery Receipt'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipts = await listDemoFinalExternalReviewReleaseBundleDeliveryReceipts();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-external-review-release-bundle/delivery-receipts');
  expect(receipts).toHaveLength(1);
  expect(receipts[0].deliveryTarget).toBe('reviewer@example.com');
});

test('downloads final external-review release bundle delivery receipt report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Release Bundle Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport =
    await downloadDemoFinalExternalReviewReleaseBundleDeliveryReceiptReport('receipt/1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-receipts/receipt%2F1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches final external-review release bundle delivery finalization through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        finalized: true,
        summary: 'Final external-review release bundle delivery is finalized with a fresh receipt.',
        nextAction: 'Use the finalization report as release-bundle delivery proof.',
        latestArchiveId: 'final-external-review-release-bundle-archive-1',
        latestDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
        latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T11:00:00Z',
        releaseBundleDeliveryReceiptFreshness: 'FRESH',
        releaseBundleDeliveryReceiptFresh: true,
        releaseBundleDeliveryReceiptFreshnessSummary:
          'Latest release bundle delivery receipt matches the current frozen release bundle archive.',
        checks: [
          {
            name: 'Frozen final external-review release bundle',
            status: 'READY',
            summary: 'Frozen final external-review release bundle is ready.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Frozen final external-review release bundle archive is delivered.'],
        downloadActions: ['Download final external-review release bundle delivery finalization report.'],
        sideEffectContract:
          'GET /api/demo/final-external-review-release-bundle/delivery-finalization is read-only.',
        markdownReport: '# PatchPilot Final External Review Release Bundle Delivery Finalization',
        generatedAt: '2026-06-29T11:10:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const finalization = await getDemoFinalExternalReviewReleaseBundleDeliveryFinalization();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-finalization'
  );
  expect(finalization.finalized).toBe(true);
  expect(finalization.latestDeliveryReceiptId)
    .toBe('final-external-review-release-bundle-delivery-receipt-1');
});

test('downloads final external-review release bundle delivery finalization report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final External Review Release Bundle Delivery Finalization'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationReport();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-finalization/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final external-review release bundle delivery finalization through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-external-review-release-bundle-delivery-finalization-archive-1',
        status: 'READY',
        finalized: true,
        summary:
          'Final external-review release bundle delivery is finalized and archived.',
        nextAction: 'Use this immutable archive as the external reviewer handoff proof.',
        latestArchiveId: 'final-external-review-release-bundle-archive-1',
        latestDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
        latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T11:00:00Z',
        releaseBundleDeliveryReceiptFreshness: 'FRESH',
        releaseBundleDeliveryReceiptFresh: true,
        releaseBundleDeliveryReceiptFreshnessSummary:
          'Latest release bundle delivery receipt matches the current frozen release bundle archive.',
        checks: [
          {
            name: 'Frozen final external-review release bundle',
            status: 'READY',
            summary: 'Frozen final external-review release bundle is ready.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Final external-review release bundle delivery finalization is frozen.'],
        downloadActions: ['Download final external-review release bundle delivery finalization archive report.'],
        sideEffectContract:
          'POST /api/demo/final-external-review-release-bundle/delivery-finalization/archives creates an immutable finalization archive.',
        report:
          '# PatchPilot Final External Review Release Bundle Delivery Finalization Archive',
        generatedAt: '2026-06-29T11:10:00Z',
        archivedAt: '2026-06-29T11:15:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalExternalReviewReleaseBundleDeliveryFinalization();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-finalization/archives',
    { method: 'POST' }
  );
  expect(archive.finalized).toBe(true);
  expect(archive.latestDeliveryReceiptId)
    .toBe('final-external-review-release-bundle-delivery-receipt-1');
});

test('lists final external-review release bundle delivery finalization archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-external-review-release-bundle-delivery-finalization-archive-1',
          status: 'READY',
          finalized: true,
          summary:
            'Final external-review release bundle delivery is finalized and archived.',
          nextAction: 'Use this immutable archive as the external reviewer handoff proof.',
          latestArchiveId: 'final-external-review-release-bundle-archive-1',
          latestDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
          latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
          latestDeliveryFinalizationArchiveId: 'final-external-review-package-delivery-finalization-archive-1',
          latestPackageArchiveId: 'final-external-review-package-archive-1',
          latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
          latestTaskId: 'task-1',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          latestDeliveredAt: '2026-06-29T11:00:00Z',
          releaseBundleDeliveryReceiptFreshness: 'FRESH',
          releaseBundleDeliveryReceiptFresh: true,
          releaseBundleDeliveryReceiptFreshnessSummary:
            'Latest release bundle delivery receipt matches the current frozen release bundle archive.',
          checks: [
            {
              name: 'Frozen final external-review release bundle',
              status: 'READY',
              summary: 'Frozen final external-review release bundle is ready.',
              nextAction: 'No action needed.'
            }
          ],
          evidenceNotes: ['Final external-review release bundle delivery finalization is frozen.'],
          downloadActions: ['Download final external-review release bundle delivery finalization archive report.'],
          sideEffectContract:
            'POST /api/demo/final-external-review-release-bundle/delivery-finalization/archives creates an immutable finalization archive.',
          report:
            '# PatchPilot Final External Review Release Bundle Delivery Finalization Archive',
          generatedAt: '2026-06-29T11:10:00Z',
          archivedAt: '2026-06-29T11:15:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchives();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-finalization/archives'
  );
  expect(archives).toHaveLength(1);
  expect(archives[0].id).toBe('final-external-review-release-bundle-delivery-finalization-archive-1');
});

test('downloads final external-review release bundle delivery finalization archive report through backend API', async () => {
  const reportBlob = new Blob([
    '# PatchPilot Final External Review Release Bundle Delivery Finalization Archive'
  ], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveReport(
    'final-external-review-release-bundle-delivery-finalization-archive-1'
  );

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-finalization/archives/final-external-review-release-bundle-delivery-finalization-archive-1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('fetches final external-review release bundle delivery certificate through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        certified: true,
        summary:
          'Final external-review release bundle delivery is certified from the latest finalized archive.',
        nextAction:
          'Share the release bundle delivery certificate report as the terminal reviewer handoff proof.',
        latestDeliveryFinalizationArchiveId:
          'final-external-review-release-bundle-delivery-finalization-archive-1',
        latestReleaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
        latestDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
        latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T12:10:00Z',
        latestArchivedAt: '2026-06-29T12:30:00Z',
        releaseBundleDeliveryReceiptFreshness: 'FRESH',
        releaseBundleDeliveryReceiptFresh: true,
        checks: [
          {
            name: 'Final external-review release bundle delivery finalization archive',
            status: 'READY',
            summary: 'Latest release bundle delivery finalization archive is finalized.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: [
          'Release bundle delivery finalization archive final-external-review-release-bundle-delivery-finalization-archive-1 is finalized.'
        ],
        downloadActions: [
          'Download final external-review release bundle delivery certificate report.'
        ],
        sideEffectContract:
          'GET /api/demo/final-external-review-release-bundle/delivery-certificate is read-only.',
        markdownReport:
          '# PatchPilot Final External Review Release Bundle Delivery Certificate',
        generatedAt: '2026-06-29T12:40:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const certificate = await getDemoFinalExternalReviewReleaseBundleDeliveryCertificate();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-certificate'
  );
  expect(certificate.certified).toBe(true);
  expect(certificate.latestDeliveryFinalizationArchiveId)
    .toBe('final-external-review-release-bundle-delivery-finalization-archive-1');
});

test('downloads final external-review release bundle delivery certificate report through backend API', async () => {
  const reportBlob = new Blob([
    '# PatchPilot Final External Review Release Bundle Delivery Certificate'
  ], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateReport();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-certificate/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final external-review release bundle delivery certificate through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-external-review-release-bundle-delivery-certificate-archive-1',
        status: 'READY',
        certified: true,
        summary:
          'Final external-review release bundle delivery is certified from the latest finalized archive.',
        nextAction: 'Share the release bundle delivery certificate report as the terminal reviewer handoff proof.',
        latestDeliveryFinalizationArchiveId:
          'final-external-review-release-bundle-delivery-finalization-archive-1',
        latestReleaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
        latestDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
        latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T13:20:00Z',
        latestArchivedAt: '2026-06-29T14:30:00Z',
        releaseBundleDeliveryReceiptFreshness: 'FRESH',
        releaseBundleDeliveryReceiptFresh: true,
        checks: [],
        evidenceNotes: [],
        downloadActions: ['Download final external-review release bundle delivery certificate report.'],
        sideEffectContract:
          'GET /api/demo/final-external-review-release-bundle/delivery-certificate is read-only.',
        report: '# PatchPilot Final External Review Release Bundle Delivery Certificate',
        generatedAt: '2026-06-29T15:00:00Z',
        archivedAt: '2026-06-29T15:30:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalExternalReviewReleaseBundleDeliveryCertificate();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-certificate/archives',
    { method: 'POST' }
  );
  expect(archive.certified).toBe(true);
  expect(archive.latestDeliveryReceiptId)
    .toBe('final-external-review-release-bundle-delivery-receipt-1');
});

test('lists final external-review release bundle delivery certificate archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-external-review-release-bundle-delivery-certificate-archive-1',
          status: 'READY',
          certified: true,
          summary:
            'Final external-review release bundle delivery is certified from the latest finalized archive.',
          nextAction:
            'Share the release bundle delivery certificate report as the terminal reviewer handoff proof.',
          latestDeliveryFinalizationArchiveId:
            'final-external-review-release-bundle-delivery-finalization-archive-1',
          latestReleaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
          latestDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
          latestCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
          latestPackageArchiveId: 'final-external-review-package-archive-1',
          latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
          latestTaskId: 'task-8',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          latestDeliveredAt: '2026-06-29T13:20:00Z',
          latestArchivedAt: '2026-06-29T14:30:00Z',
          releaseBundleDeliveryReceiptFreshness: 'FRESH',
          releaseBundleDeliveryReceiptFresh: true,
          checks: [],
          evidenceNotes: [],
          downloadActions: [],
          sideEffectContract:
            'GET /api/demo/final-external-review-release-bundle/delivery-certificate is read-only.',
          report: '# PatchPilot Final External Review Release Bundle Delivery Certificate',
          generatedAt: '2026-06-29T15:00:00Z',
          archivedAt: '2026-06-29T15:30:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchives();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-certificate/archives'
  );
  expect(archives).toHaveLength(1);
  expect(archives[0].latestDeliveryReceiptId)
    .toBe('final-external-review-release-bundle-delivery-receipt-1');
});

test('downloads final external-review release bundle delivery certificate archive report through backend API', async () => {
  const reportBlob = new Blob([
    '# PatchPilot Final External Review Release Bundle Delivery Certificate'
  ], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveReport(
    'certificate/archive 1'
  );

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-external-review-release-bundle/delivery-certificate/archives/certificate%2Farchive%201/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('gets final reviewer handoff package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        readyForReview: true,
        summary: 'Final reviewer handoff package is ready from the latest terminal delivery certificate archive.',
        nextAction: 'Send the handoff package report and listed attachments to the external reviewer.',
        latestCertificateArchiveId: 'final-reviewer-certificate-archive-1',
        latestDeliveryFinalizationArchiveId:
          'final-external-review-release-bundle-delivery-finalization-archive-1',
        latestReleaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
        latestDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
        latestPackageCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T07:20:00Z',
        latestArchivedAt: '2026-06-29T07:40:00Z',
        requiredAttachments: [
          'Final reviewer handoff package report.',
          'Terminal release-bundle delivery certificate archive final-reviewer-certificate-archive-1.'
        ],
        checks: [
          {
            name: 'Terminal delivery certificate archive',
            status: 'READY',
            summary: 'Latest terminal certificate archive is certified.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: [
          'Terminal certificate archive final-reviewer-certificate-archive-1 is the final reviewer handoff root.'
        ],
        downloadActions: ['Download final reviewer handoff package report.'],
        sideEffectContract: 'GET /api/demo/final-reviewer-handoff-package is read-only.',
        markdownReport: '# PatchPilot Final Reviewer Handoff Package',
        generatedAt: '2026-06-29T07:45:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const handoffPackage = await getDemoFinalReviewerHandoffPackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-reviewer-handoff-package');
  expect(handoffPackage.readyForReview).toBe(true);
  expect(handoffPackage.latestCertificateArchiveId).toBe('final-reviewer-certificate-archive-1');
  expect(handoffPackage.requiredAttachments).toContain('Final reviewer handoff package report.');
});

test('downloads final reviewer handoff package report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Reviewer Handoff Package'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalReviewerHandoffPackageReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-reviewer-handoff-package/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('records final reviewer handoff delivery receipt through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-reviewer-handoff-delivery-receipt-1',
        status: 'READY',
        fresh: true,
        freshness: 'FRESH',
        summary: 'Final reviewer handoff delivery receipt is fresh.',
        nextAction: 'Use this receipt as proof that the final reviewer handoff was delivered.',
        deliveryChannel: 'email',
        deliveryTarget: 'reviewer@example.com',
        operator: 'PatchPilot operator',
        notes: 'Sent final reviewer handoff package to reviewer.',
        deliveredAt: '2026-06-29T08:00:00Z',
        latestCertificateArchiveId: 'final-reviewer-certificate-archive-1',
        latestDeliveryFinalizationArchiveId:
          'final-external-review-release-bundle-delivery-finalization-archive-1',
        latestReleaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
        latestReleaseBundleDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
        latestPackageCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        evidenceNotes: ['Final reviewer handoff delivery receipt is fresh.'],
        downloadActions: [
          'Download final reviewer handoff delivery receipt final-reviewer-handoff-delivery-receipt-1.'
        ],
        sideEffectContract:
          'POST /api/demo/final-reviewer-handoff-package/delivery-receipts records local evidence only.',
        markdownReport: '# PatchPilot Final Reviewer Handoff Delivery Receipt',
        createdAt: '2026-06-29T08:01:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createDemoFinalReviewerHandoffDeliveryReceipt({
    deliveryChannel: 'email',
    deliveryTarget: ' reviewer@example.com ',
    operator: ' PatchPilot operator ',
    notes: 'Sent final reviewer handoff package to reviewer.'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-reviewer-handoff-package/delivery-receipts', {
    method: 'POST',
    body: JSON.stringify({
      deliveryChannel: 'email',
      deliveryTarget: ' reviewer@example.com ',
      operator: ' PatchPilot operator ',
      notes: 'Sent final reviewer handoff package to reviewer.'
    }),
    headers: {
      'Content-Type': 'application/json'
    }
  });
  expect(receipt.id).toBe('final-reviewer-handoff-delivery-receipt-1');
  expect(receipt.latestCertificateArchiveId).toBe('final-reviewer-certificate-archive-1');
});

test('lists final reviewer handoff delivery receipts through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-reviewer-handoff-delivery-receipt-1',
          status: 'READY',
          fresh: true,
          freshness: 'FRESH',
          summary: 'Final reviewer handoff delivery receipt is fresh.',
          nextAction: 'Use this receipt as proof that the final reviewer handoff was delivered.',
          deliveryChannel: 'email',
          deliveryTarget: 'reviewer@example.com',
          operator: 'PatchPilot operator',
          notes: 'Sent final reviewer handoff package to reviewer.',
          deliveredAt: '2026-06-29T08:00:00Z',
          latestCertificateArchiveId: 'final-reviewer-certificate-archive-1',
          latestDeliveryFinalizationArchiveId:
            'final-external-review-release-bundle-delivery-finalization-archive-1',
          latestReleaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
          latestReleaseBundleDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
          latestPackageCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
          latestPackageArchiveId: 'final-external-review-package-archive-1',
          latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
          latestTaskId: 'task-8',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          evidenceNotes: ['Final reviewer handoff delivery receipt is fresh.'],
          downloadActions: [
            'Download final reviewer handoff delivery receipt final-reviewer-handoff-delivery-receipt-1.'
          ],
          sideEffectContract:
            'POST /api/demo/final-reviewer-handoff-package/delivery-receipts records local evidence only.',
          markdownReport: '# PatchPilot Final Reviewer Handoff Delivery Receipt',
          createdAt: '2026-06-29T08:01:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipts = await listDemoFinalReviewerHandoffDeliveryReceipts();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-reviewer-handoff-package/delivery-receipts');
  expect(receipts[0].id).toBe('final-reviewer-handoff-delivery-receipt-1');
});

test('downloads final reviewer handoff delivery receipt report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Reviewer Handoff Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalReviewerHandoffDeliveryReceiptReport('receipt/1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-reviewer-handoff-package/delivery-receipts/receipt%2F1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('loads final reviewer handoff delivery finalization through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        finalized: true,
        summary: 'Final reviewer handoff delivery is finalized with a fresh handoff delivery receipt.',
        nextAction: 'Use the final reviewer handoff delivery finalization report as the terminal demo closeout record.',
        latestDeliveryReceiptId: 'final-reviewer-handoff-delivery-receipt-1',
        latestCertificateArchiveId: 'final-reviewer-certificate-archive-1',
        latestDeliveryFinalizationArchiveId:
          'final-external-review-release-bundle-delivery-finalization-archive-1',
        latestReleaseBundleArchiveId: 'final-external-review-release-bundle-archive-1',
        latestReleaseBundleDeliveryReceiptId: 'final-external-review-release-bundle-delivery-receipt-1',
        latestPackageCertificateArchiveId: 'final-external-review-delivery-certificate-archive-1',
        latestPackageArchiveId: 'final-external-review-package-archive-1',
        latestPackageDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
        latestTaskId: 'task-8',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T08:00:00Z',
        handoffDeliveryReceiptFreshness: 'FRESH',
        handoffDeliveryReceiptFresh: true,
        handoffDeliveryReceiptFreshnessSummary:
          'Latest final reviewer handoff delivery receipt matches the current final reviewer handoff package.',
        checks: [
          {
            name: 'Final reviewer handoff delivery receipt',
            status: 'READY',
            summary: 'Final reviewer handoff delivery receipt final-reviewer-handoff-delivery-receipt-1 is fresh.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: [
          'Final reviewer handoff delivery receipt final-reviewer-handoff-delivery-receipt-1 is fresh.'
        ],
        downloadActions: [
          'Download final reviewer handoff delivery finalization report.',
          'Download final reviewer handoff delivery receipt final-reviewer-handoff-delivery-receipt-1.'
        ],
        sideEffectContract:
          'GET /api/demo/final-reviewer-handoff-package/delivery-finalization is read-only.',
        markdownReport: '# PatchPilot Final Reviewer Handoff Delivery Finalization',
        generatedAt: '2026-06-29T08:05:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const finalization = await getDemoFinalReviewerHandoffDeliveryFinalization();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-reviewer-handoff-package/delivery-finalization');
  expect(finalization.finalized).toBe(true);
  expect(finalization.latestDeliveryReceiptId).toBe('final-reviewer-handoff-delivery-receipt-1');
});

test('downloads final reviewer handoff delivery finalization report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Reviewer Handoff Delivery Finalization'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalReviewerHandoffDeliveryFinalizationReport();

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-reviewer-handoff-package/delivery-finalization/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('archives final acceptance completion closeout through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-acceptance-completion-closeout-archive-1',
        status: 'READY',
        closed: true,
        summary:
          'PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.',
        nextAction: 'Use this closeout report as the final external-review completion record.',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
        latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
        latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-29T04:25:00Z',
        deliveryReceiptFreshness: 'FRESH',
        evidenceNotes: ['Final demo acceptance summary is accepted.'],
        downloadActions: ['Download final acceptance completion closeout report.'],
        sideEffectContract: 'GET /api/demo/final-acceptance-completion-closeout is read-only.',
        report: '# PatchPilot Final Acceptance Completion Closeout',
        generatedAt: '2026-06-29T06:00:00Z',
        archivedAt: '2026-06-29T06:30:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalAcceptanceCompletionCloseout();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-closeout/archives', {
    method: 'POST'
  });
  expect(archive.closed).toBe(true);
  expect(archive.latestCompletionEvidenceDeliveryReceiptId)
    .toBe('final-acceptance-completion-evidence-delivery-receipt-1');
});

test('lists final acceptance completion closeout archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-acceptance-completion-closeout-archive-1',
          status: 'READY',
          closed: true,
          summary:
            'PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.',
          nextAction: 'Use this closeout report as the final external-review completion record.',
          latestTaskId: 'task-1',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
          latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
          latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          latestDeliveredAt: '2026-06-29T04:25:00Z',
          deliveryReceiptFreshness: 'FRESH',
          evidenceNotes: ['Final demo acceptance summary is accepted.'],
          downloadActions: ['Download final acceptance completion closeout report.'],
          sideEffectContract: 'GET /api/demo/final-acceptance-completion-closeout is read-only.',
          report: '# PatchPilot Final Acceptance Completion Closeout',
          generatedAt: '2026-06-29T06:00:00Z',
          archivedAt: '2026-06-29T06:30:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalAcceptanceCompletionCloseoutArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-closeout/archives');
  expect(archives).toHaveLength(1);
  expect(archives[0].id).toBe('final-acceptance-completion-closeout-archive-1');
});

test('downloads final acceptance completion closeout archive report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Acceptance Completion Closeout'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport =
    await downloadDemoFinalAcceptanceCompletionCloseoutArchiveReport('closeout/archive 1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-acceptance-completion-closeout/archives/closeout%2Farchive%201/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('records final acceptance completion evidence delivery receipt through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-acceptance-completion-evidence-delivery-receipt-1',
        status: 'READY',
        readyToShare: true,
        completionEvidenceBundleStatus: 'READY',
        summary: 'PatchPilot final acceptance completion evidence bundle is ready to share.',
        nextAction: 'Share this final acceptance completion evidence bundle with reviewers.',
        latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
        latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
        latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
        latestTaskId: 'task-1',
        deliveryChannel: 'email',
        deliveryTarget: 'reviewer@example.com',
        operator: 'local-operator',
        notes: 'Sent final completion evidence bundle to the reviewer.',
        deliveredAt: '2026-06-29T04:25:00Z',
        createdAt: '2026-06-29T04:30:00Z',
        markdownReport: '# PatchPilot Final Acceptance Completion Evidence Delivery Receipt'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createDemoFinalAcceptanceCompletionEvidenceDeliveryReceipt({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'local-operator',
    notes: 'Sent final completion evidence bundle to the reviewer.'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-evidence-delivery-receipts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      deliveryChannel: 'email',
      deliveryTarget: 'reviewer@example.com',
      operator: 'local-operator',
      notes: 'Sent final completion evidence bundle to the reviewer.'
    })
  });
  expect(receipt.id).toBe('final-acceptance-completion-evidence-delivery-receipt-1');
  expect(receipt.latestCompletionArchiveId).toBe('final-acceptance-completion-archive-1');
});

test('lists final acceptance completion evidence delivery receipts through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-acceptance-completion-evidence-delivery-receipt-1',
          status: 'READY',
          readyToShare: true,
          completionEvidenceBundleStatus: 'READY',
          summary: 'PatchPilot final acceptance completion evidence bundle is ready to share.',
          nextAction: 'Share this final acceptance completion evidence bundle with reviewers.',
          latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
          latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
          latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
          latestTaskId: 'task-1',
          deliveryChannel: 'email',
          deliveryTarget: 'reviewer@example.com',
          operator: 'local-operator',
          notes: 'Sent final completion evidence bundle to the reviewer.',
          deliveredAt: '2026-06-29T04:25:00Z',
          createdAt: '2026-06-29T04:30:00Z',
          markdownReport: '# PatchPilot Final Acceptance Completion Evidence Delivery Receipt'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipts = await listDemoFinalAcceptanceCompletionEvidenceDeliveryReceipts();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-evidence-delivery-receipts');
  expect(receipts).toHaveLength(1);
  expect(receipts[0].id).toBe('final-acceptance-completion-evidence-delivery-receipt-1');
});

test('downloads final acceptance completion evidence delivery receipt report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Acceptance Completion Evidence Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptReport('receipt/1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-acceptance-completion-evidence-delivery-receipts/receipt%2F1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('archives demo launch acceptance certificate through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'launch-certificate-archive-1',
        status: 'READY',
        certified: true,
        summary: 'PatchPilot launch acceptance is certified from the latest accepted closeout archive.',
        nextAction: 'Share the certificate and archived closeout report with reviewers.',
        archiveCount: 1,
        latestCloseoutArchiveId: 'launch-closeout-archive-1',
        latestLaunchEvidenceArchiveId: 'launch-evidence-archive-1',
        latestDeliveryReceiptId: 'launch-delivery-receipt-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        latestWebhookDeliveryId: 'delivery-1',
        evaluationRunId: 'evaluation-run-2',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        deliveryReceiptFreshness: 'FRESH',
        latestArchivedAt: '2026-06-28T08:30:00Z',
        generatedAt: '2026-06-28T09:00:00Z',
        archivedAt: '2026-06-28T10:30:00Z',
        downloadActions: ['Download launch acceptance certificate.'],
        report: '# PatchPilot Launch Acceptance Certificate'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoLaunchAcceptanceCertificate();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-acceptance-certificate/archives', { method: 'POST' });
  expect(archive.id).toBe('launch-certificate-archive-1');
  expect(archive.certified).toBe(true);
  expect(archive.latestCloseoutArchiveId).toBe('launch-closeout-archive-1');
});

test('lists demo launch acceptance certificate archives from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'launch-certificate-archive-1',
          status: 'READY',
          certified: true,
          summary: 'PatchPilot launch acceptance is certified from the latest accepted closeout archive.',
          nextAction: 'Share the certificate and archived closeout report with reviewers.',
          archiveCount: 1,
          latestCloseoutArchiveId: 'launch-closeout-archive-1',
          latestLaunchEvidenceArchiveId: 'launch-evidence-archive-1',
          latestDeliveryReceiptId: 'launch-delivery-receipt-1',
          latestSessionId: 'demo-session-20260624T003000Z',
          latestTaskId: 'task-1',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          latestWebhookDeliveryId: 'delivery-1',
          evaluationRunId: 'evaluation-run-2',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          deliveryReceiptFreshness: 'FRESH',
          latestArchivedAt: '2026-06-28T08:30:00Z',
          generatedAt: '2026-06-28T09:00:00Z',
          archivedAt: '2026-06-28T10:30:00Z',
          downloadActions: ['Download launch acceptance certificate.'],
          report: '# PatchPilot Launch Acceptance Certificate'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoLaunchAcceptanceCertificateArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-acceptance-certificate/archives');
  expect(archives).toHaveLength(1);
  expect(archives[0].id).toBe('launch-certificate-archive-1');
  expect(archives[0].archivedAt).toBe('2026-06-28T10:30:00Z');
});

test('downloads archived demo launch acceptance certificate markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Launch Acceptance Certificate\n\n- Archive: `launch-certificate-archive-1`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoLaunchAcceptanceCertificateArchiveReport('launch-certificate-archive-1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/launch-acceptance-certificate/archives/launch-certificate-archive-1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('archives demo launch acceptance closeout through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'launch-closeout-archive-1',
        status: 'READY',
        accepted: true,
        summary: 'PatchPilot launch acceptance closeout is complete.',
        sessionId: 'demo-session-20260624T003000Z',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        latestWebhookDeliveryId: 'delivery-1',
        evaluationRunId: 'evaluation-run-2',
        latestArchiveId: 'launch-evidence-archive-1',
        latestDeliveryReceiptId: 'launch-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        deliveryReceiptFreshness: 'FRESH',
        createdAt: '2026-06-28T08:30:00Z',
        report: '# PatchPilot Launch Acceptance Closeout\n\n- Status: `READY`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoLaunchAcceptanceCloseout();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-acceptance-closeout/archives', { method: 'POST' });
  expect(archive.id).toBe('launch-closeout-archive-1');
  expect(archive.accepted).toBe(true);
  expect(archive.latestDeliveryReceiptId).toBe('launch-delivery-receipt-1');
});

test('lists demo launch acceptance closeout archives from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'launch-closeout-archive-1',
          status: 'READY',
          accepted: true,
          summary: 'PatchPilot launch acceptance closeout is complete.',
          sessionId: 'demo-session-20260624T003000Z',
          latestTaskId: 'task-1',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          latestWebhookDeliveryId: 'delivery-1',
          evaluationRunId: 'evaluation-run-2',
          latestArchiveId: 'launch-evidence-archive-1',
          latestDeliveryReceiptId: 'launch-delivery-receipt-1',
          latestDeliveryTarget: 'reviewer@example.com',
          latestDeliveryChannel: 'email',
          deliveryReceiptFreshness: 'FRESH',
          createdAt: '2026-06-28T08:30:00Z',
          report: '# PatchPilot Launch Acceptance Closeout\n\n- Status: `READY`'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoLaunchAcceptanceCloseoutArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-acceptance-closeout/archives');
  expect(archives).toHaveLength(1);
  expect(archives[0].id).toBe('launch-closeout-archive-1');
  expect(archives[0].latestPullRequestUrl).toBe('https://github.com/bingqin2/PatchPilot/pull/42');
});

test('downloads archived demo launch acceptance closeout markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Launch Acceptance Closeout\n\n- Archive: `launch-closeout-archive-1`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoLaunchAcceptanceCloseoutArchiveReport('launch-closeout-archive-1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/launch-acceptance-closeout/archives/launch-closeout-archive-1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('evaluates trigger without creating manual task through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'WOULD_CREATE_TASK',
        source: 'ISSUE_COMMENT',
        wouldCreateTask: true,
        blockedReason: null,
        blockedCategory: null,
        safetyDecision: {
          allowed: true,
          reason: 'Accepted',
          category: 'UNKNOWN'
        },
        activeTaskDecision: {
          allowed: true,
          reason: 'No active task exists for this issue',
          category: 'UNKNOWN'
        },
        quarantineDecision: {
          allowed: true,
          reason: 'Trigger quarantine accepted',
          category: 'UNKNOWN'
        },
        rateLimitDecision: {
          allowed: true,
          reason: 'Trigger rate limit accepted',
          category: 'UNKNOWN'
        },
        triggerIntentDecision: {
          allowed: true,
          reason: 'Model trigger classification is disabled',
          category: 'UNKNOWN'
        },
        issueContextLoaded: false,
        nextAction: 'Create task is allowed for this trigger.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await evaluateTrigger({
    source: 'ISSUE_COMMENT',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 7,
    triggerUser: 'local-operator',
    triggerComment: '/agent fix touch docs/manual-task.md'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evaluate-trigger', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      source: 'ISSUE_COMMENT',
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 7,
      triggerUser: 'local-operator',
      triggerComment: '/agent fix touch docs/manual-task.md'
    })
  });
  expect(result.status).toBe('WOULD_CREATE_TASK');
  expect(result.source).toBe('ISSUE_COMMENT');
  expect(result.wouldCreateTask).toBe(true);
  expect(result.triggerIntentDecision?.reason).toBe('Model trigger classification is disabled');
});

test('runs GitHub trigger dry run through backend API without creating a task', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'WOULD_CREATE_TASK',
        wouldCreateTask: true,
        repository: 'bingqin2/PatchPilot',
        issueNumber: 1,
        issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
        triggerUser: 'bingqin2',
        triggerComment: '/agent fix touch docs/live-trigger-preview.md',
        summary: 'Live GitHub trigger dry run would create a PatchPilot task.',
        nextAction: 'Post this /agent fix comment on the GitHub issue when publish preflight is ready.',
        sideEffectContract: 'Read-only live trigger dry run: this endpoint does not create tasks.',
        evaluation: {
          status: 'WOULD_CREATE_TASK',
          source: 'ISSUE_COMMENT',
          wouldCreateTask: true,
          blockedReason: null,
          blockedCategory: null,
          safetyDecision: {
            allowed: true,
            reason: 'Accepted',
            category: 'UNKNOWN'
          },
          activeTaskDecision: {
            allowed: true,
            reason: 'No active task exists for this issue',
            category: 'UNKNOWN'
          },
          quarantineDecision: {
            allowed: true,
            reason: 'Trigger quarantine accepted',
            category: 'UNKNOWN'
          },
          rateLimitDecision: {
            allowed: true,
            reason: 'Trigger rate limit accepted',
            category: 'UNKNOWN'
          },
          triggerIntentDecision: {
            allowed: true,
            reason: 'Model trigger classification accepted',
            category: 'UNKNOWN'
          },
          issueContextLoaded: true,
          nextAction: 'Create task is allowed for this trigger.'
        }
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await postGitHubTriggerDryRun({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-trigger-preview.md'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/github/trigger-dry-run', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      triggerComment: '/agent fix touch docs/live-trigger-preview.md'
    })
  });
  expect(result.status).toBe('WOULD_CREATE_TASK');
  expect(result.repository).toBe('bingqin2/PatchPilot');
  expect(result.evaluation.source).toBe('ISSUE_COMMENT');
  expect(result.sideEffectContract).toContain('does not create tasks');
});

test('runs demo live launch gate through backend API without creating a task', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        readyToPost: true,
        repository: 'bingqin2/PatchPilot',
        issueNumber: 1,
        issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
        triggerUser: 'bingqin2',
        triggerComment: '/agent fix touch docs/live-gate.md',
        summary: 'PatchPilot is ready for a live /agent fix launch.',
        nextActions: ['Post the exact /agent fix comment on the GitHub issue and watch webhook delivery, task execution, and Pull Request creation.'],
        sideEffectContract: 'Read-only live launch gate: this endpoint does not create tasks.',
        launchReadiness: null,
        webhookSetup: null,
        livePublishPreflight: null,
        triggerDryRun: null,
        checks: [],
        generatedAt: '2026-07-01T10:00:00Z',
        markdownReport: '# PatchPilot Live Launch Gate'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await postDemoLiveLaunchGate({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-gate.md'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/live-launch-gate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      triggerComment: '/agent fix touch docs/live-gate.md'
    })
  });
  expect(result.status).toBe('READY');
  expect(result.readyToPost).toBe(true);
  expect(result.sideEffectContract).toContain('does not create tasks');
});

test('creates demo live trigger launch package through backend API without creating a task', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        readyToPost: true,
        repository: 'bingqin2/PatchPilot',
        issueNumber: 1,
        issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
        triggerUser: 'bingqin2',
        triggerComment: '/agent fix touch docs/live-package.md',
        summary: 'PatchPilot is ready for the operator to post the live trigger.',
        operatorHandoffArchiveId: 'operator-archive-1',
        operatorHandoffArchiveReady: true,
        operatorHandoffArchivedAt: '2026-07-02T00:00:00Z',
        liveLaunchGateStatus: 'READY',
        liveLaunchGateReady: true,
        evidenceNotes: ['Latest external exposure operator handoff archive operator-archive-1 is ready.'],
        nextActions: [
          'Post `/agent fix touch docs/live-package.md` on https://github.com/bingqin2/PatchPilot/issues/1.'
        ],
        sideEffectContract: 'Read-only live trigger launch package: this endpoint does not create tasks.',
        liveLaunchGate: null,
        generatedAt: '2026-07-02T00:00:00Z',
        markdownReport: '# PatchPilot Live Trigger Launch Package'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await postDemoLiveTriggerLaunchPackage({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-package.md'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/live-trigger-launch-package', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      triggerComment: '/agent fix touch docs/live-package.md'
    })
  });
  expect(result.status).toBe('READY');
  expect(result.operatorHandoffArchiveId).toBe('operator-archive-1');
  expect(result.markdownReport).toContain('PatchPilot Live Trigger Launch Package');
  expect(result.sideEffectContract).toContain('does not create tasks');
});

test('archives and downloads demo live trigger launch package reports through backend API', async () => {
  const fetchMock = vi.fn()
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: {
          id: 'launch-package-archive-1',
          status: 'READY',
          readyToPost: true,
          repository: 'bingqin2/PatchPilot',
          issueNumber: 1,
          issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
          triggerUser: 'bingqin2',
          triggerComment: '/agent fix touch docs/live-package.md',
          summary: 'PatchPilot is ready for the operator to post the live trigger.',
          operatorHandoffArchiveId: 'operator-archive-1',
          operatorHandoffArchiveReady: true,
          operatorHandoffArchivedAt: '2026-07-02T00:00:00Z',
          liveLaunchGateStatus: 'READY',
          liveLaunchGateReady: true,
          evidenceNotes: ['Latest external exposure operator handoff archive operator-archive-1 is ready.'],
          nextActions: ['Post `/agent fix touch docs/live-package.md`.'],
          sideEffectContract: 'Archive creation writes only PatchPilot local archive records.',
          packageGeneratedAt: '2026-07-02T00:00:00Z',
          archivedAt: '2026-07-02T00:00:05Z',
          report: '# PatchPilot Live Trigger Launch Package'
        },
        message: null
      })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: [
          {
            id: 'launch-package-archive-1',
            status: 'READY',
            readyToPost: true,
            repository: 'bingqin2/PatchPilot',
            issueNumber: 1,
            issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
            triggerUser: 'bingqin2',
            triggerComment: '/agent fix touch docs/live-package.md',
            summary: 'PatchPilot is ready for the operator to post the live trigger.',
            operatorHandoffArchiveId: 'operator-archive-1',
            operatorHandoffArchiveReady: true,
            operatorHandoffArchivedAt: '2026-07-02T00:00:00Z',
            liveLaunchGateStatus: 'READY',
            liveLaunchGateReady: true,
            evidenceNotes: ['Latest external exposure operator handoff archive operator-archive-1 is ready.'],
            nextActions: ['Post `/agent fix touch docs/live-package.md`.'],
            sideEffectContract: 'Archive creation writes only PatchPilot local archive records.',
            packageGeneratedAt: '2026-07-02T00:00:00Z',
            archivedAt: '2026-07-02T00:00:05Z',
            report: '# PatchPilot Live Trigger Launch Package'
          }
        ],
        message: null
      })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      blob: async () => new Blob(['# PatchPilot Live Trigger Launch Package'], { type: 'text/markdown' })
    } as Response);
  vi.stubGlobal('fetch', fetchMock);

  const input = {
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-package.md'
  };
  const archive = await archiveDemoLiveTriggerLaunchPackage(input);
  const archives = await listDemoLiveTriggerLaunchPackageArchives();
  const report = await downloadDemoLiveTriggerLaunchPackageArchiveReport('launch-package-archive-1');

  expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/demo/live-trigger-launch-package/archives', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/demo/live-trigger-launch-package/archives');
  expect(fetchMock).toHaveBeenNthCalledWith(
    3,
    '/api/demo/live-trigger-launch-package/archives/launch-package-archive-1/report/download'
  );
  expect(archive.id).toBe('launch-package-archive-1');
  expect(archives).toHaveLength(1);
  expect(report.type).toBe('text/markdown');
});

test('creates and downloads demo live trigger outcome closeout reports through backend API', async () => {
  const fetchMock = vi.fn()
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: {
          status: 'READY',
          successful: true,
          repository: 'bingqin2/PatchPilot',
          issueNumber: 1,
          issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
          triggerUser: 'bingqin2',
          triggerComment: '/agent fix touch docs/live-outcome.md',
          launchPackageArchiveId: 'launch-package-archive-1',
          launchPackageStatus: 'READY',
          launchPackageArchivedAt: '2026-07-02T00:00:05Z',
          taskId: 'task-1',
          taskStatus: 'COMPLETED',
          failureReason: null,
          taskCreatedAt: '2026-07-02T00:10:00Z',
          taskUpdatedAt: '2026-07-02T00:11:00Z',
          pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          webhookDeliveryId: 'delivery-1',
          webhookDeliveryStatus: 'TASK_CREATED',
          summary: 'Live trigger completed and created Pull Request.',
          evidenceNotes: ['Task task-1 completed.'],
          nextActions: ['Review and merge https://github.com/bingqin2/PatchPilot/pull/42.'],
          sideEffectContract: 'Read-only live trigger outcome closeout: this endpoint does not mutate GitHub or task state.',
          generatedAt: '2026-07-02T01:00:00Z',
          markdownReport: '# PatchPilot Live Trigger Outcome Closeout'
        },
        message: null
      })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      blob: async () => new Blob(['# PatchPilot Live Trigger Outcome Closeout'], { type: 'text/markdown' })
    } as Response);
  vi.stubGlobal('fetch', fetchMock);

  const input = {
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-outcome.md',
    launchPackageArchiveId: 'launch-package-archive-1'
  };
  const closeout = await postDemoLiveTriggerOutcomeCloseout(input);
  const report = await downloadDemoLiveTriggerOutcomeCloseoutReport(input);

  expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/demo/live-trigger-outcome-closeout', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/demo/live-trigger-outcome-closeout/report/download', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(closeout.status).toBe('READY');
  expect(closeout.pullRequestUrl).toBe('https://github.com/bingqin2/PatchPilot/pull/42');
  expect(report.type).toBe('text/markdown');
});

test('archives lists and downloads live trigger outcome closeout archives through backend API', async () => {
  const archivePayload = {
    id: 'outcome-closeout-archive-1',
    status: 'READY',
    successful: true,
    repository: 'bingqin2/PatchPilot',
    issueNumber: 1,
    issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-package.md',
    launchPackageArchiveId: 'launch-package-archive-1',
    launchPackageStatus: 'READY',
    launchPackageArchivedAt: '2026-07-02T00:00:05Z',
    taskId: 'task-1',
    taskStatus: 'COMPLETED',
    failureReason: null,
    taskCreatedAt: '2026-07-02T00:10:00Z',
    taskUpdatedAt: '2026-07-02T00:11:00Z',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    webhookDeliveryId: 'delivery-1',
    webhookDeliveryStatus: 'TASK_CREATED',
    summary: 'Live trigger completed.',
    evidenceNotes: ['Task task-1 completed.'],
    nextActions: ['Review PR.'],
    sideEffectContract: 'Archive creation writes only PatchPilot local archive records.',
    closeoutGeneratedAt: '2026-07-02T01:00:00Z',
    archivedAt: '2026-07-02T01:05:00Z',
    report: '# PatchPilot Live Trigger Outcome Closeout'
  };
  const fetchMock = vi.fn()
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ success: true, data: archivePayload, message: null })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ success: true, data: [archivePayload], message: null })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      blob: async () => new Blob(['archive report'], { type: 'text/markdown' })
    } as Response);
  vi.stubGlobal('fetch', fetchMock);

  const input = {
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-package.md',
    launchPackageArchiveId: 'launch-package-archive-1'
  };
  const archive = await archiveDemoLiveTriggerOutcomeCloseout(input);
  const archives = await listDemoLiveTriggerOutcomeCloseoutArchives();
  const report = await downloadDemoLiveTriggerOutcomeCloseoutArchiveReport('outcome-closeout-archive-1');

  expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/demo/live-trigger-outcome-closeout/archives', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/demo/live-trigger-outcome-closeout/archives');
  expect(fetchMock).toHaveBeenNthCalledWith(
    3,
    '/api/demo/live-trigger-outcome-closeout/archives/outcome-closeout-archive-1/report/download'
  );
  expect(archive.id).toBe('outcome-closeout-archive-1');
  expect(archives).toHaveLength(1);
  expect(report.type).toBe('text/markdown');
});

test('loads and downloads live demo evidence bundle through backend API', async () => {
  const bundlePayload = {
    status: 'READY',
    readyForHandoff: true,
    repository: 'bingqin2/PatchPilot',
    issueNumber: 1,
    issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-package.md',
    launchPackageArchiveId: 'launch-package-archive-1',
    launchPackageArchivedAt: '2026-07-02T00:00:05Z',
    outcomeCloseoutArchiveId: 'outcome-closeout-archive-1',
    outcomeCloseoutArchivedAt: '2026-07-02T01:05:00Z',
    taskId: 'task-1',
    taskStatus: 'COMPLETED',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    webhookDeliveryId: 'delivery-1',
    summary: 'Live demo evidence bundle is ready for handoff.',
    evidenceNotes: ['Launch package archive launch-package-archive-1 is ready.'],
    nextActions: ['Review and merge https://github.com/bingqin2/PatchPilot/pull/42.'],
    sideEffectContract: 'Read-only live demo evidence bundle: this endpoint does not mutate GitHub or task state.',
    generatedAt: '2026-07-02T02:00:00Z',
    markdownReport: '# PatchPilot Live Demo Evidence Bundle'
  };
  const fetchMock = vi.fn()
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ success: true, data: bundlePayload, message: null })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      blob: async () => new Blob(['bundle report'], { type: 'text/markdown' })
    } as Response);
  vi.stubGlobal('fetch', fetchMock);

  const bundle = await getDemoLiveDemoEvidenceBundle();
  const report = await downloadDemoLiveDemoEvidenceBundleReport();

  expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/demo/live-demo-evidence-bundle');
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/demo/live-demo-evidence-bundle/report/download');
  expect(bundle.status).toBe('READY');
  expect(bundle.readyForHandoff).toBe(true);
  expect(bundle.pullRequestUrl).toBe('https://github.com/bingqin2/PatchPilot/pull/42');
  expect(report.type).toBe('text/markdown');
});

test('archives and downloads live demo evidence bundle snapshots through backend API', async () => {
  const archivePayload = {
    id: 'live-demo-evidence-bundle-archive-1',
    status: 'READY',
    readyForHandoff: true,
    repository: 'bingqin2/PatchPilot',
    issueNumber: 1,
    issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-package.md',
    launchPackageArchiveId: 'launch-package-archive-1',
    launchPackageArchivedAt: '2026-07-02T00:00:05Z',
    outcomeCloseoutArchiveId: 'outcome-closeout-archive-1',
    outcomeCloseoutArchivedAt: '2026-07-02T01:05:00Z',
    taskId: 'task-1',
    taskStatus: 'COMPLETED',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    webhookDeliveryId: 'delivery-1',
    summary: 'Live demo evidence bundle is ready for handoff.',
    evidenceNotes: ['Launch package archive launch-package-archive-1 is ready.'],
    nextActions: ['Review and merge https://github.com/bingqin2/PatchPilot/pull/42.'],
    sideEffectContract: 'Archive creation writes only PatchPilot local archive records.',
    bundleGeneratedAt: '2026-07-02T02:00:00Z',
    archivedAt: '2026-07-02T03:00:00Z',
    report: '# PatchPilot Live Demo Evidence Bundle Archive'
  };
  const fetchMock = vi.fn()
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ success: true, data: archivePayload, message: null })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ success: true, data: [archivePayload], message: null })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      blob: async () => new Blob(['bundle archive report'], { type: 'text/markdown' })
    } as Response);
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoLiveDemoEvidenceBundle();
  const archives = await listDemoLiveDemoEvidenceBundleArchives();
  const report = await downloadDemoLiveDemoEvidenceBundleArchiveReport('live-demo-evidence-bundle-archive-1');

  expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/demo/live-demo-evidence-bundle/archives', {
    method: 'POST'
  });
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/demo/live-demo-evidence-bundle/archives');
  expect(fetchMock).toHaveBeenNthCalledWith(
    3,
    '/api/demo/live-demo-evidence-bundle/archives/live-demo-evidence-bundle-archive-1/report/download'
  );
  expect(archive.id).toBe('live-demo-evidence-bundle-archive-1');
  expect(archive.readyForHandoff).toBe(true);
  expect(archives).toHaveLength(1);
  expect(report.type).toBe('text/markdown');
});

test('loads and downloads live demo handoff package through backend API', async () => {
  const packagePayload = {
    status: 'READY',
    readyForReview: true,
    evidenceBundleArchiveId: 'live-demo-evidence-bundle-archive-1',
    repository: 'bingqin2/PatchPilot',
    issueNumber: 1,
    issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-package.md',
    taskId: 'task-1',
    taskStatus: 'COMPLETED',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    webhookDeliveryId: 'delivery-1',
    summary: 'Live demo handoff package is ready for reviewer handoff.',
    reviewChecklist: ['Open the Pull Request and review the files changed.'],
    deliveryInstructions: ['Share this handoff package and archived evidence report with the reviewer.'],
    evidenceNotes: ['Launch package archive launch-package-archive-1 is ready.'],
    sideEffectContract: 'read-only live demo handoff package',
    generatedAt: '2026-07-02T04:00:00Z',
    markdownReport: '# PatchPilot Live Demo Handoff Package'
  };
  const fetchMock = vi.fn()
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ success: true, data: packagePayload, message: null })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      blob: async () => new Blob(['handoff package'], { type: 'text/markdown' })
    } as Response);
  vi.stubGlobal('fetch', fetchMock);

  const handoffPackage = await getDemoLiveDemoHandoffPackage();
  const report = await downloadDemoLiveDemoHandoffPackageReport();

  expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/demo/live-demo-handoff-package');
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/demo/live-demo-handoff-package/report/download');
  expect(handoffPackage.status).toBe('READY');
  expect(handoffPackage.readyForReview).toBe(true);
  expect(handoffPackage.evidenceBundleArchiveId).toBe('live-demo-evidence-bundle-archive-1');
  expect(handoffPackage.pullRequestUrl).toBe('https://github.com/bingqin2/PatchPilot/pull/42');
  expect(report.type).toBe('text/markdown');
});

test('records, lists, and downloads live demo handoff delivery receipts through backend API', async () => {
  const receiptPayload = {
    id: 'live-demo-handoff-delivery-receipt-1',
    status: 'READY',
    handoffPackageStatus: 'READY',
    evidenceBundleArchiveId: 'live-demo-evidence-bundle-archive-1',
    repository: 'bingqin2/PatchPilot',
    issueNumber: 1,
    issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-package.md',
    taskId: 'task-1',
    taskStatus: 'COMPLETED',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    webhookDeliveryId: 'delivery-1',
    summary: 'Live demo handoff package delivery receipt is recorded.',
    deliveryChannel: 'github-comment',
    deliveryTarget: 'https://github.com/bingqin2/PatchPilot/pull/42',
    operator: 'local-operator',
    notes: 'Sent the live demo handoff package to the reviewer.',
    deliveredAt: '2026-07-02T04:55:00Z',
    createdAt: '2026-07-02T05:00:00Z',
    markdownReport: '# PatchPilot Live Demo Handoff Delivery Receipt'
  };
  const fetchMock = vi.fn()
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ success: true, data: receiptPayload, message: null })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({ success: true, data: [receiptPayload], message: null })
    } as Response)
    .mockResolvedValueOnce({
      ok: true,
      status: 200,
      blob: async () => new Blob(['receipt'], { type: 'text/markdown' })
    } as Response);
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createDemoLiveDemoHandoffDeliveryReceipt({
    deliveryChannel: 'github-comment',
    deliveryTarget: 'https://github.com/bingqin2/PatchPilot/pull/42',
    operator: 'local-operator',
    notes: 'Sent the live demo handoff package to the reviewer.',
    deliveredAt: '2026-07-02T04:55:00Z'
  });
  const receipts = await listDemoLiveDemoHandoffDeliveryReceipts();
  const report = await downloadDemoLiveDemoHandoffDeliveryReceiptReport('live-demo-handoff-delivery-receipt-1');

  expect(fetchMock).toHaveBeenNthCalledWith(
    1,
    '/api/demo/live-demo-handoff-package/delivery-receipts',
    expect.objectContaining({
      body: JSON.stringify({
        deliveryChannel: 'github-comment',
        deliveryTarget: 'https://github.com/bingqin2/PatchPilot/pull/42',
        operator: 'local-operator',
        notes: 'Sent the live demo handoff package to the reviewer.',
        deliveredAt: '2026-07-02T04:55:00Z'
      })
    })
  );
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/demo/live-demo-handoff-package/delivery-receipts');
  expect(fetchMock).toHaveBeenNthCalledWith(
    3,
    '/api/demo/live-demo-handoff-package/delivery-receipts/live-demo-handoff-delivery-receipt-1/report/download'
  );
  expect(receipt.id).toBe('live-demo-handoff-delivery-receipt-1');
  expect(receipts[0].evidenceBundleArchiveId).toBe('live-demo-evidence-bundle-archive-1');
  expect(report.type).toBe('text/markdown');
});

test('runs demo launch preflight through backend API without creating a task', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        readyToPost: true,
        summary: 'Demo launch preflight is ready to post the tested /agent fix comment.',
        readiness: {
          status: 'READY',
          summary: 'PatchPilot is ready for a live demo.',
          checks: [],
          nextActions: []
        },
        triggerEvaluation: {
          status: 'WOULD_CREATE_TASK',
          source: 'ISSUE_COMMENT',
          wouldCreateTask: true,
          blockedReason: null,
          blockedCategory: null,
          safetyDecision: {
            allowed: true,
            reason: 'Accepted',
            category: 'UNKNOWN'
          },
          activeTaskDecision: {
            allowed: true,
            reason: 'No active task exists for this issue',
            category: 'UNKNOWN'
          },
          quarantineDecision: {
            allowed: true,
            reason: 'Trigger quarantine accepted',
            category: 'UNKNOWN'
          },
          rateLimitDecision: {
            allowed: true,
            reason: 'Trigger rate limit accepted',
            category: 'UNKNOWN'
          },
          triggerIntentDecision: {
            allowed: true,
            reason: 'Model trigger classification accepted',
            category: 'UNKNOWN'
          },
          issueContextLoaded: true,
          nextAction: 'Create task is allowed for this trigger.'
        },
        nextActions: ['Post the tested /agent fix comment on the controlled GitHub issue.']
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await preflightDemoLaunch({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-preflight', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test'
    })
  });
  expect(result.status).toBe('READY');
  expect(result.readyToPost).toBe(true);
  expect(result.triggerEvaluation.source).toBe('ISSUE_COMMENT');
});

test('composes a demo launch command through backend API without creating a task', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
        preflightInput: {
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          triggerUser: 'bingqin2',
          triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test'
        },
        githubIssueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
        summary: 'Prepared a demo /agent fix replace command for bingqin2/PatchPilot#1.',
        nextActions: ['Run launch preflight with the generated command before posting it on GitHub.']
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await composeDemoLaunchCommand({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    operation: 'replace',
    targetPath: 'docs/demo.md',
    replacementText: 'PatchPilot smoke test'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/launch-command', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      operation: 'replace',
      targetPath: 'docs/demo.md',
      replacementText: 'PatchPilot smoke test'
    })
  });
  expect(result.triggerComment).toBe('/agent fix replace docs/demo.md PatchPilot smoke test');
  expect(result.preflightInput.triggerComment).toBe('/agent fix replace docs/demo.md PatchPilot smoke test');
  expect(result.githubIssueUrl).toBe('https://github.com/bingqin2/PatchPilot/issues/1');
});

test('approves pending review tasks through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'task-review',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 4,
        installationId: 0,
        triggerUser: 'bingqin2',
        triggerComment: '/agent fix update deployment workflow',
        deliveryId: 'delivery-review',
        commentId: 104,
        status: 'PENDING',
        failureReason: null,
        createdAt: '2026-06-20T01:08:00Z',
        pullRequestUrl: null,
        completedAt: null,
        updatedAt: '2026-06-20T01:09:00Z',
        language: 'node',
        buildSystem: 'npm',
        verificationCommand: 'npm test',
        adapterDetectionReason: 'package.json contains a non-empty scripts.test',
            statusCommentId: null,
            statusCommentUrl: null,
            riskReviewApprovedAt: '2026-06-20T01:09:00Z',
            riskReviewApprovedBy: 'release-captain',
            riskReviewApprovalReason: 'Reviewed generated diff and accepted docs-only change',
            retrySourceTaskId: 'task-review',
            retrySourceStatus: 'FAILED',
            retrySourceFailureReason: 'Model patch review rejected generated edits: unsafe edit',
            retryReason: null,
            retriedAt: '2026-06-20T01:09:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const task = await approveTaskReview('task-review', {
    operator: 'release-captain',
    reason: 'Reviewed generated diff and accepted docs-only change'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-review/approve-review', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      operator: 'release-captain',
      reason: 'Reviewed generated diff and accepted docs-only change'
    })
  });
  expect(task.status).toBe('PENDING');
  expect(task.failureReason).toBeNull();
  expect(task.riskReviewApprovedAt).toBe('2026-06-20T01:09:00Z');
  expect(task.riskReviewApprovedBy).toBe('release-captain');
  expect(task.riskReviewApprovalReason).toBe('Reviewed generated diff and accepted docs-only change');
  expect(task.retrySourceTaskId).toBe('task-review');
  expect(task.retrySourceStatus).toBe('FAILED');
  expect(task.retrySourceFailureReason).toBe('Model patch review rejected generated edits: unsafe edit');
  expect(task.retriedAt).toBe('2026-06-20T01:09:00Z');
});

test('builds backend task search sort and pagination query parameters', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        items: [],
        limit: 25,
        offset: 50,
        hasMore: true,
        total: 74
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const page = await listTasks({
    status: 'FAILED',
    query: ' search target ',
    repositoryOwner: ' bingqin2 ',
    repositoryName: ' PatchPilot ',
    language: ' node ',
    buildSystem: ' npm ',
    createdAfter: ' 2026-06-20T01:00:00Z ',
    createdBefore: ' 2026-06-21T01:00:00Z ',
    limit: 25,
    offset: 50,
    sort: 'createdAtAsc'
  });

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/tasks?limit=25&offset=50&query=search+target&repositoryOwner=bingqin2&repositoryName=PatchPilot&language=node&buildSystem=npm&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z&sort=createdAtAsc&status=FAILED'
  );
  expect(page).toEqual({
    items: [],
    limit: 25,
    offset: 50,
    hasMore: true,
    total: 74
  });
});

test('lists recent webhook deliveries through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'diagnostic-1',
          deliveryId: 'delivery-1',
          event: 'issue_comment',
          status: 'TASK_CREATED',
          taskId: 'task-1',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          triggerUser: 'bingqin2',
          triggerComment: '/agent fix touch docs/demo.md',
          message: 'Task created from /agent fix',
          redeliveryRecommended: false,
          operatorAction: 'Task was created. Do not redeliver this webhook unless you intentionally want GitHub to report a duplicate delivery.',
          createdAt: '2026-06-23T01:00:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const deliveries = await listWebhookDeliveries(20);

  expect(fetchMock).toHaveBeenCalledWith('/api/github/webhook-deliveries?limit=20');
  expect(deliveries[0].status).toBe('TASK_CREATED');
  expect(deliveries[0].redeliveryRecommended).toBe(false);
  expect(deliveries[0].operatorAction).toBe('Task was created. Do not redeliver this webhook unless you intentionally want GitHub to report a duplicate delivery.');
});

test('evaluates webhook payload diagnostics through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY_FOR_WEBHOOK',
        signatureStatus: 'VALID',
        validJson: true,
        supportedEvent: true,
        supportedAction: true,
        agentFixCommand: true,
        repositoryOwner: 'octocat',
        repositoryName: 'hello-world',
        issueNumber: 42,
        triggerUser: 'alice',
        triggerComment: '/agent fix touch docs/webhook-diagnostic.md',
        message: 'Payload is an issue_comment.created /agent fix trigger.',
        nextAction: 'Use GitHub redeliver.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await evaluateWebhookPayloadDiagnostic({
    event: 'issue_comment',
    deliveryId: 'diagnostic-delivery',
    signature: 'sha256=test',
    payload: '{"action":"created"}'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/github/webhook-diagnostics/evaluate-payload', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      event: 'issue_comment',
      deliveryId: 'diagnostic-delivery',
      signature: 'sha256=test',
      payload: '{"action":"created"}'
    })
  });
  expect(result.status).toBe('READY_FOR_WEBHOOK');
  expect(result.repositoryOwner).toBe('octocat');
});

test('gets demo evidence bundle through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        summary: 'Demo evidence bundle is ready.',
        summaryCounts: {
          adapterFixtureCount: 12,
          failedAdapterFixtureCount: 0,
          recentTaskCount: 1,
          activeQuarantineCount: 0,
          recentPullRequestAvailable: true
        },
        readiness: {
          status: 'READY',
          summary: 'PatchPilot is ready for a controlled demo.',
          checks: [],
          nextActions: []
        },
        smokeChecklist: {
          status: 'READY',
          summary: 'Live demo smoke checklist is ready.',
          steps: [],
          nextActions: []
        },
        configuration: null,
        adapterFixtures: {
          totalCount: 12,
          failedCount: 0
        },
        evaluationRunReadiness: {
          status: 'READY',
          latestRunId: 'evaluation-run-2',
          previousRunId: 'evaluation-run-1',
          passedDelta: 1,
          failedDelta: 0,
          skippedDelta: 0,
          coveredLanguages: ['java', 'python'],
          coveredBuildSystems: ['maven', 'pytest'],
          safetyRejectionCategories: ['DANGEROUS_REQUEST', 'SECRET_EXFILTRATION'],
          sideEffectContract: 'Evaluation run readiness summary reads archived full evaluation runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.',
          nextAction: 'Full evaluation run archive is ready; use it as current demo evidence.'
        },
        queueSummary: {
          totalCount: 0,
          pendingCount: 0,
          availablePendingCount: 0,
          delayedPendingCount: 0,
          runningCount: 0,
          completedCount: 0,
          failedCount: 0,
          cancelledCount: 0
        },
        recentTask: null,
        recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        webhookSetupReadiness: null,
        latestWebhookDelivery: null,
        recentWebhookDeliveries: [],
        rejectedTriggerSummary: {
          totalCount: 0,
          categoryCounts: [],
          sourceCounts: [],
          triggerUserCounts: [],
          repositoryCounts: []
        },
        activeQuarantineCount: 0,
        launchAcceptanceCloseoutEvidence: {
          status: 'READY',
          archived: true,
          accepted: true,
          summary: 'Latest launch acceptance closeout archive is accepted and ready.',
          nextAction: 'Use the archived launch acceptance closeout as the final launch evidence record.',
          archiveCount: 1,
          latestArchiveId: 'launch-closeout-archive-1',
          latestEvidenceArchiveId: 'launch-evidence-archive-1',
          latestDeliveryReceiptId: 'launch-delivery-receipt-1',
          latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          latestArchivedAt: '2026-06-24T08:00:00Z',
          downloadActions: ['Download launch acceptance closeout archive launch-closeout-archive-1.']
        },
        generatedAt: '2026-06-24T00:10:00Z',
        nextActions: ['Use this evidence bundle as the live demo baseline.']
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const bundle = await getDemoEvidenceBundle();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/evidence-bundle');
  expect(bundle.status).toBe('READY');
  expect(bundle.summaryCounts.adapterFixtureCount).toBe(12);
  expect(bundle.evaluationRunReadiness.latestRunId).toBe('evaluation-run-2');
  expect(bundle.evaluationRunReadiness.coveredLanguages).toEqual(['java', 'python']);
  expect(bundle.launchAcceptanceCloseoutEvidence.accepted).toBe(true);
  expect(bundle.launchAcceptanceCloseoutEvidence.latestArchiveId).toBe('launch-closeout-archive-1');
  expect(bundle.recentPullRequestUrl).toBe('https://github.com/bingqin2/PatchPilot/pull/42');
});

test('gets demo script through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        summary: 'Demo script is ready.',
        steps: [
          {
            order: 1,
            name: 'Confirm backend and dashboard access',
            status: 'READY',
            operatorAction: 'Open the dashboard and confirm protected APIs load.',
            verificationCommand: 'curl http://127.0.0.1:8080/health',
            successCriteria: 'Backend reports UP and dashboard data loads.',
            troubleshootingPanel: 'Connectivity panel',
            evidence: 'Backend readiness endpoint is reachable.'
          }
        ],
        healthContract: [
          'GET /api/demo/script is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.'
        ],
        nextActions: ['Follow the script from step 1 through Pull Request review.'],
        generatedAt: '2026-06-24T00:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const script = await getDemoScript();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/script');
  expect(script.status).toBe('READY');
  expect(script.steps[0].name).toBe('Confirm backend and dashboard access');
  expect(script.steps[0].verificationCommand).toBe('curl http://127.0.0.1:8080/health');
  expect(script.healthContract[0]).toContain('read-only');
});

test('gets demo session snapshot through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        sessionId: 'demo-session-20260624T003000Z',
        status: 'READY',
        summary: 'Demo session snapshot is ready.',
        generatedAt: '2026-06-24T00:30:00Z',
        evidenceBundle: {
          status: 'READY',
          summary: 'Demo evidence bundle is ready.',
          summaryCounts: {
            adapterFixtureCount: 12,
            failedAdapterFixtureCount: 0,
            recentTaskCount: 2,
            activeQuarantineCount: 0,
            recentPullRequestAvailable: true
          },
          readiness: {
            status: 'READY',
            summary: 'PatchPilot is ready for a controlled demo.',
            checks: [],
            nextActions: []
          },
          smokeChecklist: {
            status: 'READY',
            summary: 'Live demo smoke checklist is ready.',
            steps: [],
            nextActions: []
          },
          configuration: null,
          adapterFixtures: {
            totalCount: 12,
            failedCount: 0
          },
          queueSummary: {
            totalCount: 2,
            pendingCount: 0,
            availablePendingCount: 0,
            delayedPendingCount: 0,
            runningCount: 0,
            completedCount: 2,
            failedCount: 0,
            cancelledCount: 0
          },
          recentTask: null,
          recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          webhookSetupReadiness: null,
          latestWebhookDelivery: null,
          recentWebhookDeliveries: [],
          rejectedTriggerSummary: null,
          activeQuarantineCount: 0,
          generatedAt: '2026-06-24T00:00:00Z',
          nextActions: ['Follow the script from step 1 through Pull Request review.']
        },
        script: {
          status: 'READY',
          summary: 'Demo script is ready.',
          steps: [],
          healthContract: ['The script endpoint is read-only.'],
          nextActions: ['Follow the script from step 1 through Pull Request review.'],
          generatedAt: '2026-06-24T00:30:00Z'
        },
        runbook: '# PatchPilot Demo Runbook\n\n- Status: `READY`',
        readinessSnapshotTrend: {
          status: 'IMPROVING',
          summary: 'Demo readiness improved from BLOCKED to READY.',
          latestSnapshotId: 'readiness-snapshot-new',
          previousSnapshotId: 'readiness-snapshot-old',
          latestReadinessStatus: 'READY',
          previousReadinessStatus: 'BLOCKED',
          readyCheckDelta: 4,
          needsAttentionCheckDelta: -2,
          blockedCheckDelta: -2,
          nextAction: 'Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.',
          markdownReport: '# PatchPilot Demo Readiness Snapshot Trend\n\n- Status: `IMPROVING`'
        },
        operatorChecklist: ['Open the dashboard and confirm the demo session snapshot status.'],
        healthContract: [
          'GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.'
        ],
        shareSummary: 'Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
        nextActions: ['Follow the script from step 1 through Pull Request review.']
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const snapshot = await getDemoSessionSnapshot();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-snapshot');
  expect(snapshot.sessionId).toBe('demo-session-20260624T003000Z');
  expect(snapshot.status).toBe('READY');
  expect(snapshot.evidenceBundle.recentPullRequestUrl).toBe('https://github.com/bingqin2/PatchPilot/pull/42');
  expect(snapshot.readinessSnapshotTrend.status).toBe('IMPROVING');
  expect(snapshot.readinessSnapshotTrend.readyCheckDelta).toBe(4);
  expect(snapshot.operatorChecklist[0]).toContain('demo session snapshot status');
  expect(snapshot.healthContract[0]).toContain('read-only');
});

test('loads demo runbook markdown from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: '# PatchPilot Demo Runbook\n\n- Status: `READY`',
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const runbook = await getDemoRunbook();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/runbook');
  expect(runbook).toContain('# PatchPilot Demo Runbook');
  expect(runbook).toContain('`READY`');
});

test('loads demo session report markdown from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: '# PatchPilot Demo Session Report\n\n- Status: `READY`',
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await getDemoSessionReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-report');
  expect(report).toContain('# PatchPilot Demo Session Report');
  expect(report).toContain('`READY`');
});

test('loads demo session report markdown with prepared launch command context', async () => {
  const input: DemoSessionReportInput = {
    preparedLaunchCommands: [
      {
        triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        triggerUser: 'bingqin2',
        operation: 'replace',
        targetPath: 'docs/demo.md',
        replacementText: 'PatchPilot smoke test',
        savedAt: '2026-06-26T01:00:00Z'
      }
    ],
    archivedLaunchOutcomes: []
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: '# PatchPilot Demo Session Report\n\n## Prepared Launch Commands',
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await getDemoSessionReport(input);

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-report', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(report).toContain('Prepared Launch Commands');
});

test('loads demo session report markdown with archived launch outcome context', async () => {
  const input: DemoSessionReportInput = {
    preparedLaunchCommands: [],
    archivedLaunchOutcomes: [
      {
        triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        triggerUser: 'bingqin2',
        taskId: 'task-1',
        taskStatus: 'COMPLETED',
        pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        archivedAt: '2026-06-26T01:10:00Z',
        report: '# PatchPilot Demo Launch Outcome Report\n\n- Task: `task-1`'
      }
    ]
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: '# PatchPilot Demo Session Report\n\n## Archived Launch Outcomes',
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await getDemoSessionReport(input);

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-report', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(report).toContain('Archived Launch Outcomes');
});

test('downloads demo session report markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Session Report\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoSessionReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads demo session report markdown with prepared launch command context', async () => {
  const input: DemoSessionReportInput = {
    preparedLaunchCommands: [
      {
        triggerComment: '/agent fix touch docs/history.md',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 2,
        triggerUser: 'bingqin2',
        operation: 'touch',
        targetPath: 'docs/history.md',
        replacementText: null,
        savedAt: '2026-06-26T01:05:00Z'
      }
    ],
    archivedLaunchOutcomes: []
  };
  const reportBlob = new Blob(['# PatchPilot Demo Session Report'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoSessionReport(input);

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-report/download', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(downloadedReport).toBe(reportBlob);
});

test('loads demo handoff package markdown with browser context', async () => {
  const input: DemoSessionReportInput = {
    preparedLaunchCommands: [
      {
        triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        triggerUser: 'bingqin2',
        operation: 'replace',
        targetPath: 'docs/demo.md',
        replacementText: 'PatchPilot smoke test',
        savedAt: '2026-06-26T01:00:00Z'
      }
    ],
    archivedLaunchOutcomes: [
      {
        triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        triggerUser: 'bingqin2',
        taskId: 'task-1',
        taskStatus: 'COMPLETED',
        pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        archivedAt: '2026-06-26T01:10:00Z',
        report: '# PatchPilot Demo Launch Outcome Report'
      }
    ]
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: '# PatchPilot Demo Handoff Package\n\n## Handoff Summary',
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await getDemoHandoffPackage(input);

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-package', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(report).toContain('Demo Handoff Package');
});

test('loads structured demo handoff readiness with browser context', async () => {
  const input: DemoSessionReportInput = {
    preparedLaunchCommands: [
      {
        triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        triggerUser: 'bingqin2',
        operation: 'replace',
        targetPath: 'docs/demo.md',
        replacementText: 'PatchPilot smoke test',
        savedAt: '2026-06-26T01:00:00Z'
      }
    ],
    archivedLaunchOutcomes: [
      {
        triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        triggerUser: 'bingqin2',
        taskId: 'task-1',
        taskStatus: 'COMPLETED',
        pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        archivedAt: '2026-06-26T01:10:00Z',
        report: '# PatchPilot Demo Launch Outcome Report'
      }
    ]
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        summary: 'Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.',
        nextAction: 'No missing handoff evidence.',
        checks: [
          {
            name: 'Webhook delivery evidence',
            status: 'READY',
            summary: 'delivery-1 created task task-1.',
            nextAction: 'No action needed.'
          }
        ]
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getDemoHandoffReadiness(input);

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-readiness', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(readiness.status).toBe('READY');
  expect(readiness.summary).toContain('webhook delivery');
  expect(readiness.nextAction).toBe('No missing handoff evidence.');
  expect(readiness.checks[0].name).toBe('Webhook delivery evidence');
  expect(readiness.checks[0].nextAction).toBe('No action needed.');
});

test('downloads demo handoff package markdown with browser context', async () => {
  const input: DemoSessionReportInput = {
    preparedLaunchCommands: [],
    archivedLaunchOutcomes: []
  };
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Package'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoHandoffPackage(input);

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-package/download', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(downloadedReport).toBe(reportBlob);
});

test('archives current demo handoff package with browser context', async () => {
  const input: DemoSessionReportInput = {
    preparedLaunchCommands: [],
    archivedLaunchOutcomes: []
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'handoff-archive-1',
        sessionId: 'demo-session-20260624T003000Z',
        status: 'READY',
        summary: 'Demo session snapshot is ready.',
        shareSummary: 'Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
        recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        createdAt: '2026-06-24T04:00:00Z',
        report: '# PatchPilot Demo Handoff Package\n\n## Handoff Summary'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoHandoffPackage(input);

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-package-archives', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(archive.id).toBe('handoff-archive-1');
  expect(archive.report).toContain('Demo Handoff Package');
});

test('lists demo handoff package archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'handoff-archive-1',
          sessionId: 'demo-session-20260624T003000Z',
          status: 'READY',
          summary: 'Demo session snapshot is ready.',
          handoffReadinessStatus: 'READY',
          handoffReadinessSummary: 'Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.',
          handoffReadinessNextAction: 'No missing handoff evidence.',
          handoffReadyCheckCount: 7,
          handoffNeedsAttentionCheckCount: 0,
          handoffBlockedCheckCount: 0,
          shareSummary: 'Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
          recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          createdAt: '2026-06-24T04:00:00Z',
          report: '# PatchPilot Demo Handoff Package\n\n- Status: `READY`'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoHandoffPackageArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-package-archives');
  expect(archives[0].id).toBe('handoff-archive-1');
  expect(archives[0].handoffReadinessStatus).toBe('READY');
  expect(archives[0].handoffReadinessNextAction).toBe('No missing handoff evidence.');
  expect(archives[0].handoffReadyCheckCount).toBe(7);
});

test('gets demo handoff package archive summary through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        shareReady: true,
        archiveCount: 1,
        latestArchiveId: 'handoff-archive-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestHandoffReadinessStatus: 'READY',
        latestCreatedAt: '2026-06-24T04:00:00Z',
        summary: 'Latest archived handoff package is READY and can be shared.',
        nextAction: 'No missing handoff evidence.',
        markdownReport: '# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const summary = await getDemoHandoffPackageArchiveSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-package-archives/summary');
  expect(summary.status).toBe('READY');
  expect(summary.shareReady).toBe(true);
  expect(summary.latestArchiveId).toBe('handoff-archive-1');
  expect(summary.nextAction).toBe('No missing handoff evidence.');
});

test('gets demo handoff share checklist through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        summary: 'Latest handoff archive is ready to share.',
        nextAction: 'Share the latest handoff package summary and archived package with the reviewer.',
        checks: [
          {
            name: 'Handoff package archive',
            status: 'READY',
            summary: '1 archived handoff package is available.',
            nextAction: 'Use archive handoff-archive-1 as the latest package.'
          }
        ],
        markdownReport: '# PatchPilot Demo Handoff Share Checklist\n\n- Status: `READY`',
        generatedAt: '2026-06-24T05:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const checklist = await getDemoHandoffShareChecklist();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-share-checklist');
  expect(checklist.status).toBe('READY');
  expect(checklist.checks[0].name).toBe('Handoff package archive');
  expect(checklist.markdownReport).toContain('# PatchPilot Demo Handoff Share Checklist');
});

test('gets demo handoff share center through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        shareReady: true,
        summary: 'Post-demo handoff package is ready to share.',
        nextAction: 'Download the package, archive summary, and share checklist before sending handoff evidence.',
        latestArchiveId: 'handoff-archive-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestCreatedAt: '2026-06-24T04:00:00Z',
        downloadActions: [
          'Download handoff package archive handoff-archive-1.',
          'Download handoff package archive summary.',
          'Download handoff share checklist.'
        ],
        evidenceNotes: ['Latest package archive is READY.', 'Share checklist has 4 checks.'],
        markdownReport: '# PatchPilot Demo Handoff Share Center\n\n- Status: `READY`',
        generatedAt: '2026-06-24T05:30:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const shareCenter = await getDemoHandoffShareCenter();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-share-center');
  expect(shareCenter.status).toBe('READY');
  expect(shareCenter.shareReady).toBe(true);
  expect(shareCenter.latestArchiveId).toBe('handoff-archive-1');
  expect(shareCenter.downloadActions).toContain('Download handoff share checklist.');
});

test('gets demo handoff finalization gate through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        finalized: true,
        summary: 'Demo handoff is finalized with a fresh delivery receipt for the current archive.',
        nextAction: 'Use the finalization report as the post-demo delivery acceptance record.',
        latestArchiveId: 'handoff-archive-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestDeliveryReceiptId: 'receipt-1',
        latestDeliveryTarget: 'Demo reviewer',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-24T05:20:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current handoff archive and session.',
        checks: [
          {
            name: 'Handoff package share readiness',
            status: 'READY',
            summary: 'Share center is ready.',
            nextAction: 'No action needed.'
          }
        ],
        evidenceNotes: ['Finalization report can be downloaded as the acceptance record.'],
        markdownReport: '# PatchPilot Demo Handoff Finalization Gate\n\n- Status: `READY`',
        generatedAt: '2026-06-24T06:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const finalization = await getDemoHandoffFinalization();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-finalization');
  expect(finalization.status).toBe('READY');
  expect(finalization.finalized).toBe(true);
  expect(finalization.latestDeliveryReceiptId).toBe('receipt-1');
  expect(finalization.deliveryReceiptFreshness).toBe('FRESH');
  expect(finalization.checks[0].name).toBe('Handoff package share readiness');
  expect(finalization.markdownReport).toContain('# PatchPilot Demo Handoff Finalization Gate');
});

test('downloads archived demo handoff package markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Package\n\n- Archive: `handoff-archive-1`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoHandoffPackageArchiveReport('handoff-archive-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-package-archives/handoff-archive-1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads demo handoff package archive summary markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoHandoffPackageArchiveSummaryReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-package-archives/summary-report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads demo handoff share checklist markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Share Checklist\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoHandoffShareChecklistReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-share-checklist/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads demo handoff share center markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Share Center\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoHandoffShareCenterReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-share-center/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads demo handoff finalization markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Finalization Gate\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoHandoffFinalizationReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-finalization/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('gets demo final handoff report package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        downloadReady: true,
        summary: 'Final demo handoff report package is ready to deliver.',
        nextAction: 'Download this final handoff report package and attach the listed evidence files.',
        latestArchiveId: 'handoff-archive-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestDeliveryReceiptId: 'receipt-1',
        taskCertificateArchiveId: 'task-certificate-archive-1',
        taskCertificateReady: true,
        readinessChecks: ['Finalization: READY'],
        requiredAttachments: ['Finalization report'],
        preSendChecks: ['Confirm no handoff share checklist warnings remain.'],
        evidenceNotes: ['Finalization report can be downloaded as the acceptance record.'],
        sourceReports: ['Handoff finalization'],
        markdownReport: '# PatchPilot Final Demo Handoff Report Package\n\n- Status: `READY`',
        generatedAt: '2026-06-24T07:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const reportPackage = await getDemoFinalHandoffReportPackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-handoff-report-package');
  expect(reportPackage.status).toBe('READY');
  expect(reportPackage.downloadReady).toBe(true);
  expect(reportPackage.taskCertificateArchiveId).toBe('task-certificate-archive-1');
  expect(reportPackage.readinessChecks).toContain('Finalization: READY');
  expect(reportPackage.markdownReport).toContain('# PatchPilot Final Demo Handoff Report Package');
});

test('archives demo final handoff report package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'final-handoff-package-archive-1',
        status: 'READY',
        downloadReady: true,
        summary: 'Final demo handoff report package is ready to deliver.',
        nextAction: 'Download this final handoff report package and attach the listed evidence files.',
        latestArchiveId: 'handoff-archive-1',
        latestSessionId: 'demo-session-20260624T003000Z',
        latestDeliveryReceiptId: 'receipt-1',
        taskCertificateArchiveId: 'task-certificate-archive-1',
        taskCertificateReady: true,
        readinessChecks: ['Finalization: READY'],
        requiredAttachments: ['Finalization report'],
        preSendChecks: ['Confirm no handoff share checklist warnings remain.'],
        evidenceNotes: ['Finalization report can be downloaded as the acceptance record.'],
        sourceReports: ['Handoff finalization'],
        report: '# PatchPilot Final Demo Handoff Report Package\n\n- Status: `READY`',
        generatedAt: '2026-06-24T07:00:00Z',
        archivedAt: '2026-06-24T07:30:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoFinalHandoffReportPackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-handoff-report-package/archives', {
    method: 'POST'
  });
  expect(archive.id).toBe('final-handoff-package-archive-1');
  expect(archive.downloadReady).toBe(true);
  expect(archive.report).toContain('# PatchPilot Final Demo Handoff Report Package');
});

test('lists demo final handoff report package archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'final-handoff-package-archive-1',
          status: 'READY',
          downloadReady: true,
          summary: 'Final demo handoff report package is ready to deliver.',
          nextAction: 'Download this final handoff report package and attach the listed evidence files.',
          latestArchiveId: 'handoff-archive-1',
          latestSessionId: 'demo-session-20260624T003000Z',
          latestDeliveryReceiptId: 'receipt-1',
          taskCertificateArchiveId: 'task-certificate-archive-1',
          taskCertificateReady: true,
          readinessChecks: ['Finalization: READY'],
          requiredAttachments: ['Finalization report'],
          preSendChecks: ['Confirm no handoff share checklist warnings remain.'],
          evidenceNotes: ['Finalization report can be downloaded as the acceptance record.'],
          sourceReports: ['Handoff finalization'],
          report: '# PatchPilot Final Demo Handoff Report Package\n\n- Status: `READY`',
          generatedAt: '2026-06-24T07:00:00Z',
          archivedAt: '2026-06-24T07:30:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoFinalHandoffReportPackageArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-handoff-report-package/archives');
  expect(archives).toHaveLength(1);
  expect(archives[0].latestDeliveryReceiptId).toBe('receipt-1');
});

test('downloads demo final handoff report package markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Demo Handoff Report Package\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalHandoffReportPackage();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/final-handoff-report-package/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads archived demo final handoff report package markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Final Demo Handoff Report Package\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoFinalHandoffReportPackageArchiveReport('final archive/1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/final-handoff-report-package/archives/final%20archive%2F1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('loads self-hosted launch readiness package from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        readyToLaunch: true,
        summary: 'Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.',
        checks: [
          {
            name: 'Demo readiness',
            status: 'READY',
            message: 'PatchPilot is ready for a controlled demo.',
            action: 'No action needed.'
          }
        ],
        nextActions: ['Post the tested /agent fix comment.'],
        generatedAt: '2026-06-27T01:00:00Z',
        markdownReport: '# PatchPilot Self-Hosted Launch Readiness'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getDemoSelfHostedLaunchReadiness();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/self-hosted-launch-readiness');
  expect(readiness.status).toBe('READY');
  expect(readiness.readyToLaunch).toBe(true);
  expect(readiness.checks[0].name).toBe('Demo readiness');
  expect(readiness.markdownReport).toContain('# PatchPilot Self-Hosted Launch Readiness');
});

test('downloads self-hosted launch readiness markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Self-Hosted Launch Readiness\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoSelfHostedLaunchReadinessReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/self-hosted-launch-readiness/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('archives self-hosted launch readiness package through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'launch-readiness-archive-1',
        status: 'READY',
        readyToLaunch: true,
        summary: 'Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.',
        readyCheckCount: 6,
        needsAttentionCheckCount: 0,
        blockedCheckCount: 0,
        createdAt: '2026-06-28T01:30:00Z',
        report: '# PatchPilot Self-Hosted Launch Readiness'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoSelfHostedLaunchReadiness();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/self-hosted-launch-readiness/archives', {
    method: 'POST'
  });
  expect(archive.id).toBe('launch-readiness-archive-1');
  expect(archive.readyToLaunch).toBe(true);
  expect(archive.readyCheckCount).toBe(6);
});

test('lists self-hosted launch readiness archives from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'launch-readiness-archive-1',
          status: 'NEEDS_ATTENTION',
          readyToLaunch: false,
          summary: 'Self-hosted PatchPilot needs attention before launch.',
          readyCheckCount: 4,
          needsAttentionCheckCount: 2,
          blockedCheckCount: 0,
          createdAt: '2026-06-28T01:30:00Z',
          report: '# PatchPilot Self-Hosted Launch Readiness'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoSelfHostedLaunchReadinessArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/self-hosted-launch-readiness/archives');
  expect(archives[0].id).toBe('launch-readiness-archive-1');
  expect(archives[0].status).toBe('NEEDS_ATTENTION');
});

test('downloads archived self-hosted launch readiness markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Self-Hosted Launch Readiness\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoSelfHostedLaunchReadinessArchiveReport('launch-readiness-archive-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/self-hosted-launch-readiness/archives/launch-readiness-archive-1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('loads demo handoff share instructions from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        sendReady: true,
        summary: 'Share the current handoff package with repository maintainers and demo reviewers.',
        nextAction: 'Send the prepared handoff message with all required attachments.',
        recommendedRecipients: ['Repository owner or maintainer', 'Demo reviewer'],
        requiredAttachments: [
          'Handoff package archive handoff-archive-1',
          'Handoff package archive summary',
          'Handoff share checklist',
          'Handoff share center report'
        ],
        preSendChecks: [
          'Confirm the Pull Request link in the handoff package opens correctly.',
          'Confirm no handoff share checklist warnings remain.'
        ],
        messageSubject: 'PatchPilot demo handoff: demo-session-20260624T003000Z',
        messageBody: 'The PatchPilot demo handoff package is ready to share.',
        markdownReport: '# PatchPilot Demo Handoff Share Instructions\n\n- Status: `READY`',
        generatedAt: '2026-06-24T05:45:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const instructions = await getDemoHandoffShareInstructions();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-share-instructions');
  expect(instructions.sendReady).toBe(true);
  expect(instructions.recommendedRecipients).toContain('Demo reviewer');
  expect(instructions.requiredAttachments).toContain('Handoff share center report');
});

test('downloads demo handoff share instructions markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Share Instructions\n\n- Status: `READY`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoHandoffShareInstructionsReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-share-instructions/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('creates demo handoff share delivery receipt through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'delivery-receipt-1',
        status: 'READY',
        handoffArchiveId: 'handoff-archive-1',
        sessionId: 'demo-session-20260624T003000Z',
        deliveryChannel: 'email',
        deliveryTarget: 'maintainer@example.com',
        operator: 'local-operator',
        notes: 'Sent after the demo review.',
        messageSubject: 'PatchPilot demo handoff: demo-session-20260624T003000Z',
        deliveredAt: '2026-06-24T06:05:00Z',
        createdAt: '2026-06-24T06:10:00Z',
        markdownReport: '# PatchPilot Demo Handoff Share Delivery Receipt\n\n- Status: `READY`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createDemoHandoffShareDeliveryReceipt({
    deliveryChannel: 'email',
    deliveryTarget: 'maintainer@example.com',
    operator: 'local-operator',
    notes: 'Sent after the demo review.',
    deliveredAt: '2026-06-24T06:05:00Z'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-share-delivery-receipts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      deliveryChannel: 'email',
      deliveryTarget: 'maintainer@example.com',
      operator: 'local-operator',
      notes: 'Sent after the demo review.',
      deliveredAt: '2026-06-24T06:05:00Z'
    })
  });
  expect(receipt.id).toBe('delivery-receipt-1');
  expect(receipt.handoffArchiveId).toBe('handoff-archive-1');
});

test('loads demo handoff share delivery receipts from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'delivery-receipt-1',
          status: 'READY',
          handoffArchiveId: 'handoff-archive-1',
          sessionId: 'demo-session-20260624T003000Z',
          deliveryChannel: 'email',
          deliveryTarget: 'maintainer@example.com',
          operator: 'local-operator',
          notes: 'Sent after the demo review.',
          messageSubject: 'PatchPilot demo handoff: demo-session-20260624T003000Z',
          deliveredAt: '2026-06-24T06:05:00Z',
          createdAt: '2026-06-24T06:10:00Z',
          markdownReport: '# PatchPilot Demo Handoff Share Delivery Receipt\n\n- Status: `READY`'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipts = await listDemoHandoffShareDeliveryReceipts();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/handoff-share-delivery-receipts');
  expect(receipts).toHaveLength(1);
  expect(receipts[0].deliveryTarget).toBe('maintainer@example.com');
});

test('downloads demo handoff share delivery receipt markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Share Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoHandoffShareDeliveryReceiptReport('delivery-receipt-1');

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/demo/handoff-share-delivery-receipts/delivery-receipt-1/report/download'
  );
  expect(downloadedReport).toBe(reportBlob);
});

test('downloads archived demo session report markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Session Report\n\n- Archive: `archive-1`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadDemoSessionArchiveReport('archive-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-archives/archive-1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('archives current demo session through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'archive-1',
        sessionId: 'demo-session-20260624T003000Z',
        status: 'READY',
        summary: 'Demo session snapshot is ready.',
        shareSummary: 'Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
        recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        createdAt: '2026-06-24T04:00:00Z',
        report: '# PatchPilot Demo Session Report\n\n- Status: `READY`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoSession();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-archives', { method: 'POST' });
  expect(archive.id).toBe('archive-1');
  expect(archive.report).toContain('# PatchPilot Demo Session Report');
});

test('archives current demo session with prepared launch command context', async () => {
  const input: DemoSessionReportInput = {
    preparedLaunchCommands: [
      {
        triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        triggerUser: 'bingqin2',
        operation: 'replace',
        targetPath: 'docs/demo.md',
        replacementText: 'PatchPilot smoke test',
        savedAt: '2026-06-26T01:00:00Z'
      }
    ],
    archivedLaunchOutcomes: []
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'archive-1',
        sessionId: 'demo-session-20260624T003000Z',
        status: 'READY',
        summary: 'Demo session snapshot is ready.',
        shareSummary: 'Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
        recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
        createdAt: '2026-06-24T04:00:00Z',
        report: '# PatchPilot Demo Session Report\n\n## Prepared Launch Commands'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoSession(input);

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-archives', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  });
  expect(archive.report).toContain('Prepared Launch Commands');
});

test('lists demo session archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'archive-1',
          sessionId: 'demo-session-20260624T003000Z',
          status: 'READY',
          summary: 'Demo session snapshot is ready.',
          shareSummary: 'Status READY; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
          recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
          createdAt: '2026-06-24T04:00:00Z',
          report: '# PatchPilot Demo Session Report\n\n- Status: `READY`'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoSessionArchives();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/session-archives');
  expect(archives[0].sessionId).toBe('demo-session-20260624T003000Z');
});

test('gets worker health through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        state: 'ACTIVE',
        message: 'Worker poller is executing a queue item.',
        startedAt: '2026-06-24T06:00:00Z',
        lastPollAt: '2026-06-24T06:00:01Z',
        pollCount: 12,
        claimedCount: 3,
        completedCount: 2,
        failedCount: 1,
        idlePollCount: 8,
        lastClaimedQueueItemId: 'queue-123',
        lastClaimedTaskId: 'task-123',
        lastError: null,
        lastPollAgeMs: 1000,
        readinessStatus: 'READY',
        operatorAction: 'No action needed.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const workerHealth = await getWorkerHealth();

  expect(fetchMock).toHaveBeenCalledWith('/api/task-queue/worker-health');
  expect(workerHealth.state).toBe('ACTIVE');
  expect(workerHealth.lastClaimedTaskId).toBe('task-123');
  expect(workerHealth.readinessStatus).toBe('READY');
  expect(workerHealth.operatorAction).toBe('No action needed.');
});

test('lists recent rejected triggers through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'rejected-1',
          source: 'webhook',
          deliveryId: 'delivery-rejected',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          triggerUser: 'unknown-user',
          triggerComment: '/agent fix make it better',
          category: 'NOT_ACTIONABLE',
          reason: 'Unsafe request rejected: instruction is not actionable',
          commentId: 456,
          commentUrl: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456',
          retriedTaskId: 'task-from-rejected-1',
          retriedAt: '2026-06-23T01:06:00Z',
          retryable: false,
          retryBlockedReason: 'Rejected trigger has already been retried; open the linked retried task instead.',
          createdAt: '2026-06-23T01:05:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const rejectedTriggers = await listRejectedTriggers(20);

  expect(fetchMock).toHaveBeenCalledWith('/api/rejected-triggers?limit=20');
  expect(rejectedTriggers[0].category).toBe('NOT_ACTIONABLE');
  expect(rejectedTriggers[0].reason).toBe('Unsafe request rejected: instruction is not actionable');
  expect(rejectedTriggers[0].triggerComment).toBe('/agent fix make it better');
  expect(rejectedTriggers[0].commentUrl).toBe('https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456');
  expect(rejectedTriggers[0].retriedTaskId).toBe('task-from-rejected-1');
  expect(rejectedTriggers[0].retriedAt).toBe('2026-06-23T01:06:00Z');
  expect(rejectedTriggers[0].retryable).toBe(false);
  expect(rejectedTriggers[0].retryBlockedReason).toBe('Rejected trigger has already been retried; open the linked retried task instead.');
});

test('lists accepted trigger decisions through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'decision-1',
          taskId: 'task-1',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          triggerUser: 'bingqin2',
          triggerComment: '/agent fix touch docs/demo.md',
          taskStatus: 'COMPLETED',
          source: 'ISSUE_COMMENT',
          finalDecision: 'ALLOWED',
          safetyDecision: { allowed: true, reason: 'safety gate accepted', category: 'UNKNOWN' },
          activeTaskDecision: { allowed: true, reason: 'No active task exists for this issue', category: 'UNKNOWN' },
          quarantineDecision: { allowed: true, reason: 'not blocked before task creation', category: 'UNKNOWN' },
          rateLimitDecision: { allowed: true, reason: 'not rate limited before task creation', category: 'UNKNOWN' },
          triggerIntentDecision: {
            allowed: true,
            reason: 'model accepted trigger: Issue context describes a concrete failing test',
            category: 'UNKNOWN'
          },
          issueContextLoaded: true,
          createdAt: '2026-06-23T01:05:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const decisions = await listAcceptedTriggerDecisions(20);

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/pre-execution-decisions?limit=20');
  expect(decisions[0].taskId).toBe('task-1');
  expect(decisions[0].taskStatus).toBe('COMPLETED');
  expect(decisions[0].triggerIntentDecision.reason).toBe(
    'model accepted trigger: Issue context describes a concrete failing test'
  );
});

test('lists recent rejected triggers by category through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await listRejectedTriggers({ limit: 20, category: 'DANGEROUS_INSTRUCTION' });

  expect(fetchMock).toHaveBeenCalledWith('/api/rejected-triggers?limit=20&category=DANGEROUS_INSTRUCTION');
});

test('loads rejected trigger abuse summary through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        totalCount: 4,
        categoryCounts: [
          { value: 'NOT_ACTIONABLE', count: 2 },
          { value: 'DANGEROUS_INSTRUCTION', count: 1 }
        ],
        sourceCounts: [
          { value: 'issue_comment', count: 3 },
          { value: 'manual', count: 1 }
        ],
        triggerUserCounts: [
          { value: 'drive-by-user', count: 3 },
          { value: 'local-operator', count: 1 }
        ],
        repositoryCounts: [{ value: 'bingqin2/PatchPilot', count: 4 }]
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const summary = await getRejectedTriggerSummary(50);

  expect(fetchMock).toHaveBeenCalledWith('/api/rejected-triggers/summary?limit=50');
  expect(summary.totalCount).toBe(4);
  expect(summary.categoryCounts[0]).toEqual({ value: 'NOT_ACTIONABLE', count: 2 });
  expect(summary.triggerUserCounts[0]).toEqual({ value: 'drive-by-user', count: 3 });
});

test('lists active trigger quarantines through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'quarantine-1',
          scope: 'TRIGGER_USER',
          scopeKey: 'drive-by-user',
          reason: 'Unsafe request rejected: trigger user is temporarily quarantined',
          category: 'ABUSE_QUARANTINED',
          evidenceCount: 5,
          windowMs: 600000,
          startedAt: '2026-06-23T01:05:00Z',
          expiresAt: '2026-06-23T01:35:00Z',
          createdAt: '2026-06-23T01:05:00Z',
          updatedAt: '2026-06-23T01:10:00Z',
          active: true
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const quarantines = await listTriggerQuarantines({ activeOnly: true, limit: 20 });

  expect(fetchMock).toHaveBeenCalledWith('/api/trigger-quarantines?activeOnly=true&limit=20');
  expect(quarantines[0].scope).toBe('TRIGGER_USER');
  expect(quarantines[0].scopeKey).toBe('drive-by-user');
  expect(quarantines[0].active).toBe(true);
});

test('gets trigger quarantine evidence through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        quarantine: {
          id: 'quarantine-1',
          scope: 'TRIGGER_USER',
          scopeKey: 'drive-by-user',
          reason: 'Unsafe request rejected: trigger user is temporarily quarantined',
          category: 'ABUSE_QUARANTINED',
          evidenceCount: 5,
          windowMs: 600000,
          startedAt: '2026-06-23T01:05:00Z',
          expiresAt: '2026-06-23T01:35:00Z',
          createdAt: '2026-06-23T01:05:00Z',
          updatedAt: '2026-06-23T01:10:00Z',
          createdBy: null,
          releasedAt: null,
          releasedBy: null,
          releaseReason: null,
          active: true
        },
        rejectedTriggers: [
          {
            id: 'rejected-1',
            source: 'issue_comment',
            deliveryId: 'delivery-rejected',
            repositoryOwner: 'bingqin2',
            repositoryName: 'PatchPilot',
            issueNumber: 1,
            triggerUser: 'drive-by-user',
            triggerComment: '/agent fix make it better',
            category: 'NOT_ACTIONABLE',
            reason: 'Unsafe request rejected: instruction is not actionable',
            commentId: 456,
            commentUrl: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456',
            retriedTaskId: null,
            retriedAt: null,
            retryable: false,
            retryBlockedReason: 'Release the active quarantine before retrying.',
            createdAt: '2026-06-23T01:06:00Z'
          }
        ],
        operatorSafetyAudits: [
          {
            id: 'operator-audit-1',
            action: 'MANUAL_QUARANTINE_CREATED',
            resourceType: 'TRIGGER_QUARANTINE',
            resourceId: 'quarantine-1',
            scope: 'TRIGGER_USER',
            scopeKey: 'drive-by-user',
            operator: 'local-admin',
            reason: 'Operator blocked noisy demo trigger user',
            createdAt: '2026-06-24T01:00:00Z'
          }
        ]
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const evidence = await getTriggerQuarantineEvidence('quarantine-1', 20);

  expect(fetchMock).toHaveBeenCalledWith('/api/trigger-quarantines/quarantine-1/evidence?limit=20');
  expect(evidence.quarantine.scopeKey).toBe('drive-by-user');
  expect(evidence.rejectedTriggers[0].id).toBe('rejected-1');
  expect(evidence.operatorSafetyAudits[0].action).toBe('MANUAL_QUARANTINE_CREATED');
});

test('lists recent operator safety audits through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'operator-audit-1',
          action: 'MANUAL_QUARANTINE_CREATED',
          resourceType: 'TRIGGER_QUARANTINE',
          resourceId: 'quarantine-1',
          scope: 'TRIGGER_USER',
          scopeKey: 'drive-by-user',
          operator: 'local-admin',
          reason: 'Operator blocked noisy demo trigger user',
          createdAt: '2026-06-24T01:00:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const audits = await listOperatorSafetyAudits(20);

  expect(fetchMock).toHaveBeenCalledWith('/api/operator-safety-audits?limit=20');
  expect(audits[0].action).toBe('MANUAL_QUARANTINE_CREATED');
  expect(audits[0].resourceType).toBe('TRIGGER_QUARANTINE');
  expect(audits[0].scopeKey).toBe('drive-by-user');
  expect(audits[0].operator).toBe('local-admin');
});

test('lists recent admin audit events through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'admin-audit-1',
          action: 'TASK_RETRIED',
          resourceType: 'TASK',
          resourceId: 'task-123',
          scope: 'REPOSITORY',
          scopeKey: 'bingqin2/patchpilot',
          operator: 'admin-api',
          reason: 'Verified failure cause and requested a clean rerun',
          createdAt: '2026-06-24T02:00:00Z'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const audits = await listAdminAuditEvents(20);

  expect(fetchMock).toHaveBeenCalledWith('/api/admin-audit-events?limit=20');
  expect(audits[0].action).toBe('TASK_RETRIED');
  expect(audits[0].resourceType).toBe('TASK');
  expect(audits[0].operator).toBe('admin-api');
});

test('lists filtered admin audit events through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await listAdminAuditEvents({
    limit: 25,
    action: 'TASK_RETRIED',
    resourceType: 'TASK',
    resourceId: 'task-123',
    scope: 'REPOSITORY',
    scopeKey: 'bingqin2/patchpilot',
    operator: 'admin-api'
  });

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/admin-audit-events?limit=25&action=TASK_RETRIED&resourceType=TASK&resourceId=task-123&scope=REPOSITORY&scopeKey=bingqin2%2Fpatchpilot&operator=admin-api'
  );
});

test('creates manual trigger quarantines through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 201,
    json: async () => ({
      success: true,
      data: {
        id: 'quarantine-1',
        scope: 'TRIGGER_USER',
        scopeKey: 'drive-by-user',
        reason: 'Operator blocked noisy demo trigger user',
        category: 'MANUAL_QUARANTINE',
        evidenceCount: 0,
        windowMs: 0,
        startedAt: '2026-06-24T01:00:00Z',
        expiresAt: '2026-06-24T01:30:00Z',
        createdAt: '2026-06-24T01:00:00Z',
        updatedAt: '2026-06-24T01:00:00Z',
        createdBy: 'local-admin',
        releasedAt: null,
        releasedBy: null,
        releaseReason: null,
        active: true
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const quarantine = await createTriggerQuarantine({
    scope: 'TRIGGER_USER',
    scopeKey: 'drive-by-user',
    reason: 'Operator blocked noisy demo trigger user',
    durationMs: 1800000,
    operator: 'local-admin'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/trigger-quarantines', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      scope: 'TRIGGER_USER',
      scopeKey: 'drive-by-user',
      reason: 'Operator blocked noisy demo trigger user',
      durationMs: 1800000,
      operator: 'local-admin'
    })
  });
  expect(quarantine.category).toBe('MANUAL_QUARANTINE');
  expect(quarantine.createdBy).toBe('local-admin');
});

test('releases trigger quarantines through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'quarantine-1',
        scope: 'TRIGGER_USER',
        scopeKey: 'drive-by-user',
        reason: 'Operator blocked noisy demo trigger user',
        category: 'MANUAL_QUARANTINE',
        evidenceCount: 0,
        windowMs: 0,
        startedAt: '2026-06-24T01:00:00Z',
        expiresAt: '2026-06-24T01:30:00Z',
        createdAt: '2026-06-24T01:00:00Z',
        updatedAt: '2026-06-24T01:05:00Z',
        createdBy: 'local-admin',
        releasedAt: '2026-06-24T01:05:00Z',
        releasedBy: 'local-admin',
        releaseReason: 'False positive during demo',
        active: false
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const quarantine = await releaseTriggerQuarantine('quarantine-1', {
    operator: 'local-admin',
    reason: 'False positive during demo'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/trigger-quarantines/quarantine-1/release', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      operator: 'local-admin',
      reason: 'False positive during demo'
    })
  });
  expect(quarantine.active).toBe(false);
  expect(quarantine.releasedBy).toBe('local-admin');
});

test('retries a rejected trigger through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 201,
    json: async () => ({
      success: true,
      data: {
        id: 'task-from-rejected-1',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        installationId: 0,
        triggerUser: 'drive-by-user',
        triggerComment: '/agent fix touch docs/retry.md',
        deliveryId: 'manual-retry-1',
        commentId: 0,
        status: 'PENDING',
        failureReason: null,
        createdAt: '2026-06-23T01:06:00Z',
        pullRequestUrl: null,
        completedAt: null,
        updatedAt: '2026-06-23T01:06:00Z',
        language: null,
        buildSystem: null,
        verificationCommand: null,
        adapterDetectionReason: null,
        statusCommentId: null,
        statusCommentUrl: null
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const task = await retryRejectedTrigger('rejected-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/rejected-triggers/rejected-1/retry', { method: 'POST' });
  expect(task.id).toBe('task-from-rejected-1');
});

test('builds backend task status count query parameters without status or pagination', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        totalCount: 9,
        pendingCount: 1,
        runningCount: 2,
        runningTestsCount: 0,
        pendingReviewCount: 0,
        completedCount: 4,
        failedCount: 2,
        cancelledCount: 0
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const counts = await getTaskStatusCounts({
    status: 'FAILED',
    query: ' search target ',
    repositoryOwner: ' bingqin2 ',
    repositoryName: ' PatchPilot ',
    language: ' node ',
    buildSystem: ' npm ',
    createdAfter: ' 2026-06-20T01:00:00Z ',
    createdBefore: ' 2026-06-21T01:00:00Z ',
    limit: 25,
    offset: 50,
    sort: 'createdAtAsc'
  });

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/tasks/status-counts?query=search+target&repositoryOwner=bingqin2&repositoryName=PatchPilot&language=node&buildSystem=npm&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z'
  );
  expect(counts).toEqual({
    totalCount: 9,
    pendingCount: 1,
    runningCount: 2,
    runningTestsCount: 0,
    pendingReviewCount: 0,
    completedCount: 4,
    failedCount: 2,
    cancelledCount: 0
  });
});

test('loads failure cause summary from backend metrics API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          cause: 'VERIFICATION_FAILED',
          count: 2,
          nextAction: 'Inspect the verification output, fix the failing test or build error, then retry the task.'
        },
        {
          cause: 'GITHUB_OPERATION_FAILED',
          count: 1,
          nextAction: 'Check GitHub token or App permissions, then retry the task after access is fixed.'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const causes = await getFailureCauseSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/metrics/failure-causes');
  expect(causes).toEqual([
    {
      cause: 'VERIFICATION_FAILED',
      count: 2,
      nextAction: 'Inspect the verification output, fix the failing test or build error, then retry the task.'
    },
    {
      cause: 'GITHUB_OPERATION_FAILED',
      count: 1,
      nextAction: 'Check GitHub token or App permissions, then retry the task after access is fixed.'
    }
  ]);
});

test('loads model usage summary from backend metrics API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        totalPromptTokens: 1500,
        totalCompletionTokens: 650,
        totalTokens: 2150,
        successfulCalls: 2,
        failedCalls: 1,
        estimatedCostUsd: 0.0028
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const usage = await getModelUsageSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/metrics/model-usage');
  expect(usage).toEqual({
    totalPromptTokens: 1500,
    totalCompletionTokens: 650,
    totalTokens: 2150,
    successfulCalls: 2,
    failedCalls: 1,
    estimatedCostUsd: 0.0028
  });
});

test('loads latency summary from backend metrics API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        completedTaskCount: 2,
        averageTaskDurationMs: 20000,
        maxTaskDurationMs: 30000,
        modelCallCount: 2,
        averageModelCallDurationMs: 4000,
        maxModelCallDurationMs: 6000,
        toolCallCount: 2,
        averageToolCallDurationMs: 2000,
        maxToolCallDurationMs: 3000,
        testRunCount: 2,
        averageTestRunDurationMs: 7000,
        maxTestRunDurationMs: 10000
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const latency = await getLatencySummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/metrics/latency');
  expect(latency).toEqual({
    completedTaskCount: 2,
    averageTaskDurationMs: 20000,
    maxTaskDurationMs: 30000,
    modelCallCount: 2,
    averageModelCallDurationMs: 4000,
    maxModelCallDurationMs: 6000,
    toolCallCount: 2,
    averageToolCallDurationMs: 2000,
    maxToolCallDurationMs: 3000,
    testRunCount: 2,
    averageTestRunDurationMs: 7000,
    maxTestRunDurationMs: 10000
  });
});

test('loads non-sensitive configuration summary from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        agentProvider: 'openai-compatible',
        agentModel: 'gpt-5.5',
        agentBaseUrl: 'https://api.example.test/v1',
        agentApiKeyConfigured: true,
        githubTokenConfigured: true,
        githubWebhookSecretConfigured: false,
        adminTokenConfigured: true,
        dashboardBaseUrlConfigured: true,
        workspaceRootDir: '/tmp/patchpilot/workspaces',
        queueMaxAttempts: 3,
        queueRetryDelayMs: 30000,
        queueVisibilityTimeoutMs: 300000,
        queueWorkerHeartbeatStaleMs: 10000,
        modelCostConfigured: true,
        modelTriggerClassificationEnabled: true,
        triggerRateLimitEnabled: true,
        triggerRateLimitWindowMs: 600000,
        triggerRateLimitMaxPerTriggerUser: 30,
        triggerRateLimitMaxPerRepository: 60,
        triggerRateLimitMaxPerIssue: 20,
        rejectedTriggerQuarantineEnabled: true,
        rejectedTriggerQuarantineWindowMs: 600000,
        rejectedTriggerQuarantineThreshold: 5,
        rejectedTriggerQuarantineCooldownMs: 1800000,
        triggerUserAllowlistConfigured: true,
        repositoryAllowlistConfigured: true,
        reviewApprovalAllowlistConfigured: true,
        generatedDiffRiskGateEnabled: true,
        generatedDiffProtectedPathCount: 15,
        allowedTriggerUsers: ['bingqin2', 'local-operator'],
        allowedRepositories: ['bingqin2/PatchPilot'],
        reviewApprovalAllowedOperators: ['release-captain', 'local-operator'],
        repositoryPreflightAllowedRootDirs: ['/tmp/patchpilot/workspaces', 'docs/demo-repositories']
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const configuration = await getConfigurationSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/configuration/summary');
  expect(configuration).toEqual({
    agentProvider: 'openai-compatible',
    agentModel: 'gpt-5.5',
    agentBaseUrl: 'https://api.example.test/v1',
    agentApiKeyConfigured: true,
    githubTokenConfigured: true,
    githubWebhookSecretConfigured: false,
    adminTokenConfigured: true,
    dashboardBaseUrlConfigured: true,
    workspaceRootDir: '/tmp/patchpilot/workspaces',
    queueMaxAttempts: 3,
    queueRetryDelayMs: 30000,
    queueVisibilityTimeoutMs: 300000,
    queueWorkerHeartbeatStaleMs: 10000,
    modelCostConfigured: true,
    modelTriggerClassificationEnabled: true,
    triggerRateLimitEnabled: true,
    triggerRateLimitWindowMs: 600000,
    triggerRateLimitMaxPerTriggerUser: 30,
    triggerRateLimitMaxPerRepository: 60,
    triggerRateLimitMaxPerIssue: 20,
    rejectedTriggerQuarantineEnabled: true,
    rejectedTriggerQuarantineWindowMs: 600000,
    rejectedTriggerQuarantineThreshold: 5,
    rejectedTriggerQuarantineCooldownMs: 1800000,
    triggerUserAllowlistConfigured: true,
    repositoryAllowlistConfigured: true,
    reviewApprovalAllowlistConfigured: true,
    generatedDiffRiskGateEnabled: true,
    generatedDiffProtectedPathCount: 15,
    allowedTriggerUsers: ['bingqin2', 'local-operator'],
    allowedRepositories: ['bingqin2/PatchPilot'],
    reviewApprovalAllowedOperators: ['release-captain', 'local-operator'],
    repositoryPreflightAllowedRootDirs: ['/tmp/patchpilot/workspaces', 'docs/demo-repositories']
  });
});

test('loads backend health status from health endpoint', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'UP',
        service: 'patchpilot-backend',
        timestamp: '2026-06-21T01:00:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const health = await getBackendHealth();

  expect(fetchMock).toHaveBeenCalledWith('/health');
  expect(health).toEqual({
    status: 'UP',
    service: 'patchpilot-backend',
    timestamp: '2026-06-21T01:00:00Z'
  });
});

test('sends stored admin token with operator API requests', async () => {
  const storage = new Map<string, string>();
  vi.stubGlobal('localStorage', {
    getItem: (key: string) => storage.get(key) ?? null,
    setItem: (key: string, value: string) => storage.set(key, value),
    removeItem: (key: string) => storage.delete(key),
    clear: () => storage.clear()
  });
  globalThis.localStorage.setItem(ADMIN_TOKEN_STORAGE_KEY, 'test-admin-token');
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        agentProvider: 'openai-compatible',
        agentModel: 'gpt-5.5',
        agentBaseUrl: 'https://api.example.test/v1',
        agentApiKeyConfigured: true,
        githubTokenConfigured: true,
        githubWebhookSecretConfigured: true,
        adminTokenConfigured: true,
        dashboardBaseUrlConfigured: true,
        workspaceRootDir: '/tmp/patchpilot/workspaces',
        queueMaxAttempts: 3,
        queueRetryDelayMs: 30000,
        queueVisibilityTimeoutMs: 300000,
        queueWorkerHeartbeatStaleMs: 10000,
        modelCostConfigured: true,
        modelTriggerClassificationEnabled: true,
        triggerRateLimitEnabled: true,
        triggerRateLimitWindowMs: 600000,
        triggerRateLimitMaxPerTriggerUser: 30,
        triggerRateLimitMaxPerRepository: 60,
        triggerRateLimitMaxPerIssue: 20,
        rejectedTriggerQuarantineEnabled: true,
        rejectedTriggerQuarantineWindowMs: 600000,
        rejectedTriggerQuarantineThreshold: 5,
        rejectedTriggerQuarantineCooldownMs: 1800000,
        triggerUserAllowlistConfigured: true,
        repositoryAllowlistConfigured: true,
        reviewApprovalAllowlistConfigured: true,
        generatedDiffRiskGateEnabled: true,
        generatedDiffProtectedPathCount: 15,
        allowedTriggerUsers: ['bingqin2'],
        allowedRepositories: ['bingqin2/PatchPilot'],
        reviewApprovalAllowedOperators: ['release-captain']
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await getConfigurationSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/configuration/summary', {
    headers: { 'X-PatchPilot-Admin-Token': 'test-admin-token' }
  });
});

test('loads supported language adapters from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          language: 'java',
          buildSystem: 'maven',
          verificationCommand: ['mvn', 'test'],
          detectionSignals: ['pom.xml', 'mvnw'],
          demoFixturePath: 'docs/demo-repositories/java-maven',
          status: 'SUPPORTED'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const adapters = await listLanguageAdapters();

  expect(fetchMock).toHaveBeenCalledWith('/api/language-adapters');
  expect(adapters).toEqual([
    {
      language: 'java',
      buildSystem: 'maven',
      verificationCommand: ['mvn', 'test'],
      detectionSignals: ['pom.xml', 'mvnw'],
      demoFixturePath: 'docs/demo-repositories/java-maven',
      status: 'SUPPORTED'
    }
  ]);
});

test('loads language adapter fixture verifications from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          fixtureName: 'python-hatch',
          fixturePath: 'docs/demo-repositories/python-hatch',
          expectedLanguage: 'python',
          expectedBuildSystem: 'hatch',
          expectedVerificationCommand: ['hatch', 'test'],
          actualLanguage: 'python',
          actualBuildSystem: 'hatch',
          actualVerificationCommand: ['hatch', 'test'],
          reason: 'Detected Hatch test script',
          status: 'PASS'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const fixtures = await listLanguageAdapterFixtures();

  expect(fetchMock).toHaveBeenCalledWith('/api/language-adapters/fixtures');
  expect(fixtures).toEqual([
    {
      fixtureName: 'python-hatch',
      fixturePath: 'docs/demo-repositories/python-hatch',
      expectedLanguage: 'python',
      expectedBuildSystem: 'hatch',
      expectedVerificationCommand: ['hatch', 'test'],
      actualLanguage: 'python',
      actualBuildSystem: 'hatch',
      actualVerificationCommand: ['hatch', 'test'],
      reason: 'Detected Hatch test script',
      status: 'PASS'
    }
  ]);
});

test('loads language adapter runtime readiness from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          language: 'node',
          buildSystem: 'npm',
          executable: 'npm',
          verificationCommand: ['npm', 'test'],
          status: 'MISSING',
          reason: 'Executable `npm` is not available on PATH'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await listLanguageAdapterRuntimeReadiness();

  expect(fetchMock).toHaveBeenCalledWith('/api/language-adapters/runtime-readiness');
  expect(readiness).toEqual([
    {
      language: 'node',
      buildSystem: 'npm',
      executable: 'npm',
      verificationCommand: ['npm', 'test'],
      status: 'MISSING',
      reason: 'Executable `npm` is not available on PATH'
    }
  ]);
});

test('loads evaluation case catalog from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'java-maven-doc-fix',
          title: 'Java Maven documentation fix',
          category: 'SUPPORTED_FIX',
          language: 'java',
          buildSystem: 'maven',
          repositoryFixturePath: 'docs/demo-repositories/java-maven',
          issueText: '/agent fix update Calculator to return the issue-requested sum',
          expectedVerificationCommand: ['mvn', 'test'],
          expectedChangedFiles: ['src/main/java/demo/Calculator.java'],
          successCriteria: ['Patch changes only the expected source file', 'Maven tests pass'],
          expectedDecision: 'ACCEPT_AND_CREATE_PR',
          expectedRejectionCategory: null,
          safetyExpectation: 'Allowed only after deterministic and model-assisted trigger checks pass.'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const cases = await listEvaluationCases();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/cases');
  expect(cases).toEqual([
    {
      id: 'java-maven-doc-fix',
      title: 'Java Maven documentation fix',
      category: 'SUPPORTED_FIX',
      language: 'java',
      buildSystem: 'maven',
      repositoryFixturePath: 'docs/demo-repositories/java-maven',
      issueText: '/agent fix update Calculator to return the issue-requested sum',
      expectedVerificationCommand: ['mvn', 'test'],
      expectedChangedFiles: ['src/main/java/demo/Calculator.java'],
      successCriteria: ['Patch changes only the expected source file', 'Maven tests pass'],
      expectedDecision: 'ACCEPT_AND_CREATE_PR',
      expectedRejectionCategory: null,
      safetyExpectation: 'Allowed only after deterministic and model-assisted trigger checks pass.'
    }
  ]);
});

test('loads evaluation case readiness summary from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        totalCaseCount: 6,
        supportedFixCaseCount: 4,
        safetyRejectionCaseCount: 2,
        coveredLanguages: ['go', 'java', 'node', 'python'],
        coveredBuildSystems: ['go', 'maven', 'npm', 'pytest'],
        rejectionCategories: ['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE'],
        nextAction: 'Evaluation catalog is ready for demo evidence; automated evaluation runs are still future work.',
        readOnly: true,
        healthContract: 'Summary is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const summary = await getEvaluationSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/summary');
  expect(summary).toEqual({
    status: 'READY',
    totalCaseCount: 6,
    supportedFixCaseCount: 4,
    safetyRejectionCaseCount: 2,
    coveredLanguages: ['go', 'java', 'node', 'python'],
    coveredBuildSystems: ['go', 'maven', 'npm', 'pytest'],
    rejectionCategories: ['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE'],
    nextAction: 'Evaluation catalog is ready for demo evidence; automated evaluation runs are still future work.',
    readOnly: true,
    healthContract: 'Summary is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.'
  });
});

test('loads evaluation run preview report from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        title: 'Evaluation run preview',
        previewRunId: 'preview-current-catalog',
        caseCount: 6,
        supportedFixCaseCount: 4,
        safetyRejectionCaseCount: 2,
        coveredLanguages: ['go', 'java', 'node', 'python'],
        coveredBuildSystems: ['go', 'maven', 'npm', 'pytest'],
        expectedVerificationCommands: ['go test ./...', 'mvn test', 'npm test', 'python3 -m pytest'],
        safetyRejectionCategories: ['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE'],
        gaps: [
          'Automated benchmark execution is not implemented yet.',
          'Preview uses expected outcomes only; it does not verify repository fixtures.'
        ],
        nextAction: 'Use this preview as demo evidence now; implement stored evaluation runs next to measure real issue-to-PR outcomes.',
        readOnly: true,
        sideEffectContract: 'Preview is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.',
        markdownReport: '# PatchPilot Evaluation Run Preview\n\n- Status: `READY`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const preview = await getEvaluationRunPreview();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/run-preview');
  expect(preview).toEqual({
    status: 'READY',
    title: 'Evaluation run preview',
    previewRunId: 'preview-current-catalog',
    caseCount: 6,
    supportedFixCaseCount: 4,
    safetyRejectionCaseCount: 2,
    coveredLanguages: ['go', 'java', 'node', 'python'],
    coveredBuildSystems: ['go', 'maven', 'npm', 'pytest'],
    expectedVerificationCommands: ['go test ./...', 'mvn test', 'npm test', 'python3 -m pytest'],
    safetyRejectionCategories: ['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE'],
    gaps: [
      'Automated benchmark execution is not implemented yet.',
      'Preview uses expected outcomes only; it does not verify repository fixtures.'
    ],
    nextAction: 'Use this preview as demo evidence now; implement stored evaluation runs next to measure real issue-to-PR outcomes.',
    readOnly: true,
    sideEffectContract: 'Preview is derived from checked-in evaluation case metadata only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.',
    markdownReport: '# PatchPilot Evaluation Run Preview\n\n- Status: `READY`'
  });
});

test('loads evaluation case fixture readiness from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: evaluationCaseReadinessSummary(),
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getEvaluationCaseReadiness();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/case-readiness');
  expect(readiness.status).toBe('READY');
  expect(readiness.passingCaseCount).toBe(2);
  expect(readiness.noFixtureRequiredCaseCount).toBe(1);
  expect(readiness.failingCaseCount).toBe(0);
  expect(readiness.cases[0].caseId).toBe('java-maven-doc-fix');
  expect(readiness.cases[0].expectedChangedFiles).toEqual(['src/main/java/demo/Calculator.java']);
  expect(readiness.markdownReport).toContain('# PatchPilot Evaluation Case Fixture Readiness');
});

test('runs evaluation fixture baseline through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: evaluationFixtureBaselineSummary(),
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const baseline = await runEvaluationFixtureBaseline();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/fixture-baseline', { method: 'POST' });
  expect(baseline.status).toBe('READY');
  expect(baseline.executedCaseCount).toBe(2);
  expect(baseline.passedCaseCount).toBe(2);
  expect(baseline.skippedCaseCount).toBe(1);
  expect(baseline.cases[0].caseId).toBe('java-maven-doc-fix');
  expect(baseline.cases[0].outputSnippet).toBe('maven ok');
  expect(baseline.markdownReport).toContain('# PatchPilot Evaluation Fixture Baseline');
});

test('runs and archives evaluation fixture baseline through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: evaluationFixtureBaselineRunArchive(),
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await runAndArchiveEvaluationFixtureBaseline();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/fixture-baseline-runs', { method: 'POST' });
  expect(archive.id).toBe('baseline-run-1');
  expect(archive.passedCaseCount).toBe(4);
  expect(archive.report).toContain('# PatchPilot Evaluation Fixture Baseline Run');
});

test('lists evaluation fixture baseline run archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [evaluationFixtureBaselineRunArchive()],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listEvaluationFixtureBaselineRuns();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/fixture-baseline-runs');
  expect(archives[0].id).toBe('baseline-run-1');
});

test('gets evaluation fixture baseline regression summary through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: evaluationFixtureBaselineRunRegressionSummary(),
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const summary = await getEvaluationFixtureBaselineRunRegressionSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/fixture-baseline-runs/summary');
  expect(summary.status).toBe('REGRESSED');
  expect(summary.latestRun?.id).toBe('baseline-run-new');
  expect(summary.previousRun?.id).toBe('baseline-run-old');
  expect(summary.failedDelta).toBe(1);
  expect(summary.newlyFailedCaseIds).toEqual(['java-maven-doc-fix']);
  expect(summary.recoveredCaseIds).toEqual([]);
  expect(summary.markdownReport).toContain('# PatchPilot Evaluation Fixture Baseline Regression Summary');
});

test('downloads archived evaluation fixture baseline run report markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Evaluation Fixture Baseline Run\n\n- Baseline run id: `baseline-run-1`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadEvaluationFixtureBaselineRunReport('baseline-run/1');

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/fixture-baseline-runs/baseline-run%2F1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('archives evaluation run snapshot through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: evaluationRunSnapshotArchive(),
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveEvaluationRunSnapshot();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/run-snapshots', { method: 'POST' });
  expect(archive.id).toBe('snapshot-1');
  expect(archive.report).toContain('# PatchPilot Evaluation Run Snapshot');
});

test('lists evaluation run snapshot archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [evaluationRunSnapshotArchive()],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listEvaluationRunSnapshots();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/run-snapshots');
  expect(archives[0].previewRunId).toBe('preview-current-catalog');
});

test('downloads archived evaluation run snapshot report markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Evaluation Run Snapshot\n\n- Snapshot id: `snapshot-1`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadEvaluationRunSnapshotReport('snapshot-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/run-snapshots/snapshot-1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('runs and archives full evaluation runs through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: evaluationRunArchive(),
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await runAndArchiveEvaluation();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/runs', { method: 'POST' });
  expect(archive.id).toBe('evaluation-run-1');
  expect(archive.executedFixCaseCount).toBe(4);
  expect(archive.safetyRejectionCategories).toEqual(['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE']);
  expect(archive.report).toContain('# PatchPilot Evaluation Run');
});

test('lists archived full evaluation runs through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [evaluationRunArchive()],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listEvaluationRuns();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/runs');
  expect(archives[0].id).toBe('evaluation-run-1');
  expect(archives[0].status).toBe('READY');
});

test('gets archived full evaluation run readiness summary through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: evaluationRunArchiveReadinessSummary(),
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const summary = await getEvaluationRunArchiveReadinessSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/runs/summary');
  expect(summary.status).toBe('READY');
  expect(summary.latestRun?.id).toBe('evaluation-run-1');
  expect(summary.coveredLanguages).toEqual(['go', 'java', 'node', 'python']);
  expect(summary.safetyRejectionCategories).toEqual(['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE']);
  expect(summary.markdownReport).toContain('# PatchPilot Evaluation Run Readiness Summary');
});

test('downloads archived full evaluation run report markdown from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Evaluation Run\n\n- Evaluation run id: `evaluation-run-1`'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const downloadedReport = await downloadEvaluationRunReport('evaluation-run/1');

  expect(fetchMock).toHaveBeenCalledWith('/api/evaluation/runs/evaluation-run%2F1/report/download');
  expect(downloadedReport).toBe(reportBlob);
});

test('runs repository preflight through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        supported: true,
        language: 'java',
        buildSystem: 'maven',
        verificationCommand: ['mvn', 'test'],
        reason: 'Detected Maven project',
        operatorAction: 'Repository is supported. PatchPilot can run the detected verification command after patch generation.',
        repositoryPath: 'docs/demo-repositories/java-maven',
        supportedAdapters: []
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await preflightRepository({ repositoryPath: 'docs/demo-repositories/java-maven' });

  expect(fetchMock).toHaveBeenCalledWith('/api/repository-preflight', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ repositoryPath: 'docs/demo-repositories/java-maven' })
  });
  expect(result).toEqual({
    supported: true,
    language: 'java',
    buildSystem: 'maven',
    verificationCommand: ['mvn', 'test'],
    reason: 'Detected Maven project',
    operatorAction: 'Repository is supported. PatchPilot can run the detected verification command after patch generation.',
    repositoryPath: 'docs/demo-repositories/java-maven',
    supportedAdapters: []
  });
});

test('loads model provider health from backend API without exposing secrets', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        provider: 'openai-compatible',
        model: 'gpt-5.5',
        baseUrlConfigured: true,
        apiKeyConfigured: true,
        status: 'READY',
        message: 'Model provider responded to the health probe.',
        latencyMs: 43,
        checkedAt: '2026-06-25T02:00:00Z',
        operatorAction: 'No action needed.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const health = await getModelProviderHealth();

  expect(fetchMock).toHaveBeenCalledWith('/api/model-provider/health');
  expect(health).toEqual({
    provider: 'openai-compatible',
    model: 'gpt-5.5',
    baseUrlConfigured: true,
    apiKeyConfigured: true,
    status: 'READY',
    message: 'Model provider responded to the health probe.',
    latencyMs: 43,
    checkedAt: '2026-06-25T02:00:00Z',
    operatorAction: 'No action needed.'
  });
});

test('loads demo readiness from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'NEEDS_ATTENTION',
        summary: 'PatchPilot needs attention before a live demo.',
        checks: [
          {
            name: 'Recent Pull Request',
            status: 'NEEDS_ATTENTION',
            message: 'No completed task with a Pull Request URL was found.',
            action: 'Run one controlled issue-to-PR smoke task before a live demo.'
          }
        ],
        nextActions: ['Run one controlled issue-to-PR smoke task before a live demo.']
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const readiness = await getDemoReadiness();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/readiness');
  expect(readiness).toEqual({
    status: 'NEEDS_ATTENTION',
    summary: 'PatchPilot needs attention before a live demo.',
    checks: [
      {
        name: 'Recent Pull Request',
        status: 'NEEDS_ATTENTION',
        message: 'No completed task with a Pull Request URL was found.',
        action: 'Run one controlled issue-to-PR smoke task before a live demo.'
      }
    ],
    nextActions: ['Run one controlled issue-to-PR smoke task before a live demo.']
  });
});

test('archives demo readiness snapshot through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'readiness-snapshot-1',
        status: 'BLOCKED',
        summary: 'PatchPilot is blocked before a live demo.',
        readyCheckCount: 1,
        needsAttentionCheckCount: 1,
        blockedCheckCount: 1,
        createdAt: '2026-06-27T04:00:00Z',
        report: '# PatchPilot Demo Readiness Snapshot\n\n- Status: `BLOCKED`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveDemoReadinessSnapshot();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/readiness-snapshots', { method: 'POST' });
  expect(archive.id).toBe('readiness-snapshot-1');
  expect(archive.status).toBe('BLOCKED');
  expect(archive.blockedCheckCount).toBe(1);
  expect(archive.report).toContain('# PatchPilot Demo Readiness Snapshot');
});

test('lists demo readiness snapshot archives from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'readiness-snapshot-1',
          status: 'READY',
          summary: 'PatchPilot is ready for a controlled demo.',
          readyCheckCount: 9,
          needsAttentionCheckCount: 0,
          blockedCheckCount: 0,
          createdAt: '2026-06-27T04:00:00Z',
          report: '# PatchPilot Demo Readiness Snapshot\n\n- Status: `READY`'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listDemoReadinessSnapshots();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/readiness-snapshots');
  expect(archives).toHaveLength(1);
  expect(archives[0].readyCheckCount).toBe(9);
});

test('loads demo readiness snapshot trend summary from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'IMPROVING',
        summary: 'Demo readiness improved from BLOCKED to READY.',
        latestSnapshotId: 'readiness-snapshot-new',
        previousSnapshotId: 'readiness-snapshot-old',
        latestReadinessStatus: 'READY',
        previousReadinessStatus: 'BLOCKED',
        readyCheckDelta: 4,
        needsAttentionCheckDelta: -2,
        blockedCheckDelta: -2,
        nextAction: 'Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.',
        markdownReport: '# PatchPilot Demo Readiness Snapshot Trend\n\n- Status: `IMPROVING`'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const trend = await getDemoReadinessSnapshotTrend();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/readiness-snapshots/summary');
  expect(trend.status).toBe('IMPROVING');
  expect(trend.latestSnapshotId).toBe('readiness-snapshot-new');
  expect(trend.previousSnapshotId).toBe('readiness-snapshot-old');
  expect(trend.readyCheckDelta).toBe(4);
  expect(trend.blockedCheckDelta).toBe(-2);
  expect(trend.markdownReport).toContain('# PatchPilot Demo Readiness Snapshot Trend');
});

test('downloads demo readiness snapshot archive report from backend API', async () => {
  const blob = new Blob(['# PatchPilot Demo Readiness Snapshot'], { type: 'text/markdown' });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => blob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await downloadDemoReadinessSnapshotReport('readiness/snapshot-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/readiness-snapshots/readiness%2Fsnapshot-1/report/download');
  expect(report).toBe(blob);
});

test('loads demo smoke checklist from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'NEEDS_ATTENTION',
        summary: 'Live demo smoke checklist needs attention.',
        steps: [
          {
            order: 2,
            name: 'Webhook delivery',
            status: 'NEEDS_ATTENTION',
            message: 'Latest delivery needs redelivery.',
            evidence: 'delivery-invalid',
            action: 'Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.'
          }
        ],
        nextActions: ['Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.']
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const checklist = await getDemoSmokeChecklist();

  expect(fetchMock).toHaveBeenCalledWith('/api/demo/smoke-checklist');
  expect(checklist.status).toBe('NEEDS_ATTENTION');
  expect(checklist.steps[0].name).toBe('Webhook delivery');
  expect(checklist.nextActions).toEqual(['Fix the webhook secret or URL, then use GitHub Redeliver before the live demo.']);
});

test('loads aggregate task detail from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        summary: {
          task: {
            id: 'task-1',
            repositoryOwner: 'bingqin2',
            repositoryName: 'PatchPilot',
            issueNumber: 1,
            installationId: 0,
            triggerUser: 'bingqin2',
            triggerComment: '/agent fix',
            deliveryId: 'delivery-1',
            commentId: 101,
            status: 'COMPLETED',
            failureReason: null,
            createdAt: '2026-06-20T01:00:00Z',
            pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
            completedAt: '2026-06-20T01:01:00Z',
            updatedAt: '2026-06-20T01:01:00Z',
            language: 'java',
            buildSystem: 'maven',
            verificationCommand: './mvnw test',
            adapterDetectionReason: 'pom.xml detected with mvnw wrapper',
            statusCommentId: null,
            statusCommentUrl: null
          },
          timelineEventCount: 1,
          testRunCount: 0,
          toolCallCount: 0,
          modelCallCount: 0,
          totalModelTokens: 0,
          latestTimelineEvent: null,
          latestTestRunExitCode: null,
          latestTestRunDurationMs: null
        },
        queueItem: {
          id: 'queue-1',
          taskId: 'task-1',
          status: 'FAILED',
          attemptCount: 3,
          lastError: 'maven tests failed',
          availableAt: '2026-06-20T01:02:00Z',
          lockedAt: '2026-06-20T01:01:00Z',
          createdAt: '2026-06-20T01:00:00Z',
          updatedAt: '2026-06-20T01:03:00Z'
        },
        queueItems: [
          {
            id: 'queue-1',
            taskId: 'task-1',
            status: 'FAILED',
            attemptCount: 3,
            lastError: 'maven tests failed',
            availableAt: '2026-06-20T01:02:00Z',
            lockedAt: '2026-06-20T01:01:00Z',
            createdAt: '2026-06-20T01:00:00Z',
            updatedAt: '2026-06-20T01:03:00Z'
          },
          {
            id: 'queue-older',
            taskId: 'task-1',
            status: 'PENDING',
            attemptCount: 1,
            lastError: null,
            availableAt: '2026-06-20T00:58:00Z',
            lockedAt: null,
            createdAt: '2026-06-20T00:57:00Z',
            updatedAt: '2026-06-20T00:58:00Z'
          }
        ],
        timeline: [],
        testRuns: [],
        toolCalls: [],
        modelCalls: [],
        triggerIntentAudit: {
          eventId: 'timeline-trigger',
          summary: 'Trigger accepted',
          safetyDecision: 'safety gate accepted',
          issueContextStatus: 'issue context loaded',
          modelDecision: 'model accepted trigger: Issue context describes a concrete failing test',
          createdAt: '2026-06-20T01:00:30Z'
        },
        preExecutionSafetySnapshot: {
          eventId: 'timeline-trigger',
          source: 'ISSUE_COMMENT',
          finalDecision: 'ALLOWED',
          safetyDecision: 'safety gate accepted',
          quarantineDecision: 'not blocked before task creation',
          rateLimitDecision: 'not rate limited before task creation',
          issueContextStatus: 'issue context loaded',
          modelDecision: 'model accepted trigger: Issue context describes a concrete failing test',
          createdAt: '2026-06-20T01:00:30Z'
        },
        generatedDiff: {
          toolCallId: 'tool-diff',
          diff: 'diff --git a/docs/demo.md b/docs/demo.md\n+PatchPilot smoke test',
          generatedAt: '2026-06-20T01:00:13Z'
        },
        patchReview: {
          id: 'patch-review-1',
          taskId: 'task-1',
          decision: 'APPROVE',
          reason: 'The generated edit matches the issue and stays inside the planned file.',
          confidence: 'HIGH',
          requiredFollowUp: 'Run ./mvnw test before opening the PR.',
          editedFiles: ['docs/demo.md'],
          createdAt: '2026-06-20T01:00:14Z'
        },
        issueContext: {
          title: 'PatchPilot issue context',
          body: 'Use GitHub issue context for the task.',
          url: 'https://github.com/bingqin2/PatchPilot/issues/1',
          comments: [
            {
              id: 1001,
              author: 'bingqin2',
              body: 'This comment should be available in task detail.',
              createdAt: '2026-06-20T01:00:10Z',
              url: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-1001'
            }
          ]
        },
        failureDiagnosis: null,
        retryPreflight: null,
        adapterExecutionEvidence: {
          status: 'SUPPORTED',
          language: 'java',
          buildSystem: 'maven',
          verificationCommand: './mvnw test',
          detectionReason: 'pom.xml detected with mvnw wrapper',
          operatorAction: 'Review verification output and Pull Request evidence for this selected adapter.',
          safetyNote: 'Verification command came from a registered language adapter, not from the issue comment.',
          supportedAdapters: []
        },
        repositorySupportGuidance: null
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const detail = await getTaskDetail('task-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-1/detail');
  expect(detail.summary.task.id).toBe('task-1');
  expect(detail.summary.task.adapterDetectionReason).toBe('pom.xml detected with mvnw wrapper');
  expect(detail.queueItem?.status).toBe('FAILED');
  expect(detail.queueItem?.attemptCount).toBe(3);
  expect(detail.triggerIntentAudit?.safetyDecision).toBe('safety gate accepted');
  expect(detail.triggerIntentAudit?.issueContextStatus).toBe('issue context loaded');
  expect(detail.triggerIntentAudit?.modelDecision).toBe(
    'model accepted trigger: Issue context describes a concrete failing test'
  );
  expect(detail.preExecutionSafetySnapshot?.source).toBe('ISSUE_COMMENT');
  expect(detail.preExecutionSafetySnapshot?.finalDecision).toBe('ALLOWED');
  expect(detail.preExecutionSafetySnapshot?.quarantineDecision).toBe('not blocked before task creation');
  expect(detail.preExecutionSafetySnapshot?.rateLimitDecision).toBe('not rate limited before task creation');
  expect(detail.queueItems.map((item) => item.id)).toEqual(['queue-1', 'queue-older']);
  expect(detail.timeline).toEqual([]);
  expect(detail.testRuns).toEqual([]);
  expect(detail.toolCalls).toEqual([]);
  expect(detail.modelCalls).toEqual([]);
  expect(detail.generatedDiff?.diff).toContain('+PatchPilot smoke test');
  expect(detail.patchReview?.decision).toBe('APPROVE');
  expect(detail.patchReview?.editedFiles).toEqual(['docs/demo.md']);
  expect(detail.issueContext?.title).toBe('PatchPilot issue context');
  expect(detail.issueContext?.comments[0].author).toBe('bingqin2');
  expect(detail.failureDiagnosis).toBeNull();
});

test('loads task retry preflight from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        taskId: 'task-1',
        status: 'FAILED',
        retryable: false,
        category: 'GITHUB_OPERATION_FAILED',
        reason: 'GitHub token is required to create Pull Requests: token=[REDACTED]',
        operatorAction: 'Check GitHub token or App permissions, then retry the task after access is fixed.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const preflight = await getTaskRetryPreflight('task-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-1/retry-preflight');
  expect(preflight.retryable).toBe(false);
  expect(preflight.category).toBe('GITHUB_OPERATION_FAILED');
  expect(preflight.reason).toBe('GitHub token is required to create Pull Requests: token=[REDACTED]');
});

test('retries task with operator reason', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'task-1',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        installationId: 0,
        triggerUser: 'bingqin2',
        triggerComment: '/agent fix demo',
        deliveryId: 'delivery-1',
        commentId: 101,
        status: 'PENDING',
        failureReason: null,
        createdAt: '2026-06-20T01:00:00Z',
        pullRequestUrl: null,
        completedAt: null,
        updatedAt: '2026-06-20T01:09:00Z',
        language: 'java',
        buildSystem: 'maven',
        verificationCommand: './mvnw test',
        adapterDetectionReason: 'pom.xml detected with mvnw wrapper',
        statusCommentId: null,
        statusCommentUrl: null,
        riskReviewApprovedAt: null,
        riskReviewApprovedBy: null,
        riskReviewApprovalReason: null,
        retrySourceTaskId: 'task-1',
        retrySourceStatus: 'FAILED',
        retrySourceFailureReason: 'executor failed',
        retryReason: 'Operator confirmed credentials and requested a rerun',
        retriedAt: '2026-06-20T01:09:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const retried = await retryTask('task-1', {
    reason: 'Operator confirmed credentials and requested a rerun'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-1/retry', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      reason: 'Operator confirmed credentials and requested a rerun'
    })
  });
  expect(retried.retryReason).toBe('Operator confirmed credentials and requested a rerun');
});

test('loads markdown task report from backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: '# PatchPilot Task Report\n\n- Task: `task-1`',
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await getTaskReport('task-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-1/report');
  expect(report).toContain('# PatchPilot Task Report');
  expect(report).toContain('`task-1`');
});

test('downloads markdown task report from backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Task Report'], { type: 'text/markdown;charset=UTF-8' });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await downloadTaskReport('task 1');

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task%201/report/download');
  expect(report).toBe(reportBlob);
});

test('archives task evidence package through backend API', async () => {
  const archive = {
    id: 'task-evidence-archive-1',
    taskId: 'task-1',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    status: 'COMPLETED',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
    archivedAt: '2026-06-20T01:05:00Z',
    summary: 'Task COMPLETED for bingqin2/PatchPilot#1 archived as evidence.',
    report: '# PatchPilot Task Report\n\n- Task: `task-1`'
  };
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: archive,
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const result = await archiveTaskEvidencePackage('task 1');

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task%201/evidence-packages', {
    method: 'POST'
  });
  expect(result.id).toBe('task-evidence-archive-1');
  expect(result.report).toContain('PatchPilot Task Report');
});

test('lists task evidence package archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'task-evidence-archive-1',
          taskId: 'task-1',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          status: 'COMPLETED',
          pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          archivedAt: '2026-06-20T01:05:00Z',
          summary: 'Task COMPLETED for bingqin2/PatchPilot#1 archived as evidence.',
          report: '# PatchPilot Task Report\n\n- Task: `task-1`'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listTaskEvidencePackageArchives('task 1');

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task%201/evidence-packages');
  expect(archives).toHaveLength(1);
  expect(archives[0].taskId).toBe('task-1');
});

test('lists recent task evidence package archives through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'task-evidence-archive-2',
          taskId: 'task-2',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 2,
          status: 'FAILED',
          pullRequestUrl: null,
          archivedAt: '2026-06-20T01:10:00Z',
          summary: 'Task FAILED for bingqin2/PatchPilot#2 archived as evidence.',
          report: '# PatchPilot Task Report\n\n- Task: `task-2`'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listRecentTaskEvidencePackageArchives(10);

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages?limit=10');
  expect(archives).toHaveLength(1);
  expect(archives[0].id).toBe('task-evidence-archive-2');
});

test('gets task evidence package archive summary through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        totalArchiveCount: 2,
        completedArchiveCount: 1,
        failedArchiveCount: 1,
        pendingReviewArchiveCount: 0,
        cancelledArchiveCount: 0,
        latestArchiveId: 'task-evidence-archive-2',
        latestTaskId: 'task-2',
        latestRepositoryOwner: 'bingqin2',
        latestRepositoryName: 'PatchPilot',
        latestIssueNumber: 2,
        latestArchivedAt: '2026-06-20T01:10:00Z',
        sideEffectContract: 'Read-only archive review.',
        nextAction: 'Download the latest archived evidence report.'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const summary = await getTaskEvidencePackageArchiveSummary(25);

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/summary?limit=25');
  expect(summary.totalArchiveCount).toBe(2);
  expect(summary.latestArchiveId).toBe('task-evidence-archive-2');
});

test('gets task evidence package share center through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        shareReady: true,
        summary: 'A shareable completed task evidence package is available for external review.',
        nextAction: 'Download archived task evidence task-evidence-archive-1 before sharing.',
        archiveCount: 2,
        completedArchiveCount: 1,
        failedArchiveCount: 1,
        pendingReviewArchiveCount: 0,
        cancelledArchiveCount: 0,
        latestArchiveId: 'task-evidence-archive-2',
        latestTaskId: 'task-2',
        latestRepositoryOwner: 'bingqin2',
        latestRepositoryName: 'PatchPilot',
        latestIssueNumber: 2,
        latestArchivedAt: '2026-06-20T01:10:00Z',
        shareableArchiveId: 'task-evidence-archive-1',
        shareableTaskId: 'task-1',
        shareableRepositoryOwner: 'bingqin2',
        shareableRepositoryName: 'PatchPilot',
        shareableIssueNumber: 1,
        shareablePullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        downloadActions: ['Download archived task evidence task-evidence-archive-1.'],
        evidenceNotes: ['Latest archive task-evidence-archive-2 is FAILED.'],
        sideEffectContract: 'Read-only share center.',
        markdownReport: '# PatchPilot Task Evidence Share Center',
        generatedAt: '2026-06-20T01:12:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const shareCenter = await getTaskEvidencePackageShareCenter(15);

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/share-center?limit=15');
  expect(shareCenter.status).toBe('READY');
  expect(shareCenter.shareableArchiveId).toBe('task-evidence-archive-1');
});

test('downloads archived task evidence package report through backend API', async () => {
  const reportBlob = new Blob(['# Archived PatchPilot Task Report'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await downloadTaskEvidencePackageReport('archive 1');

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/archive%201/report/download');
  expect(report).toBe(reportBlob);
});

test('downloads task evidence package share center report through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Task Evidence Share Center'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    blob: async () => reportBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const report = await downloadTaskEvidencePackageShareCenterReport();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/share-center/report/download');
  expect(report).toBe(reportBlob);
});

test('records task evidence delivery receipt through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 201,
    json: async () => ({
      success: true,
      data: {
        id: 'task-evidence-delivery-receipt-1',
        status: 'READY',
        taskEvidenceArchiveId: 'task-evidence-archive-1',
        taskId: 'task-1',
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 1,
        pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        deliveryChannel: 'email',
        deliveryTarget: 'reviewer@example.com',
        operator: 'local-operator',
        notes: 'Sent task evidence after PR review.',
        messageSubject: 'PatchPilot task evidence: task-1',
        deliveredAt: '2026-06-28T06:05:00Z',
        createdAt: '2026-06-28T06:10:00Z',
        markdownReport: '# PatchPilot Task Evidence Delivery Receipt'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipt = await createTaskEvidencePackageShareDeliveryReceipt({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'local-operator',
    notes: 'Sent task evidence after PR review.'
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/share-delivery-receipts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      deliveryChannel: 'email',
      deliveryTarget: 'reviewer@example.com',
      operator: 'local-operator',
      notes: 'Sent task evidence after PR review.'
    })
  });
  expect(receipt.id).toBe('task-evidence-delivery-receipt-1');
  expect(receipt.taskEvidenceArchiveId).toBe('task-evidence-archive-1');
});

test('lists task evidence delivery receipts through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: [
        {
          id: 'task-evidence-delivery-receipt-1',
          status: 'READY',
          taskEvidenceArchiveId: 'task-evidence-archive-1',
          taskId: 'task-1',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
          deliveryChannel: 'email',
          deliveryTarget: 'reviewer@example.com',
          operator: 'local-operator',
          notes: 'Sent task evidence after PR review.',
          messageSubject: 'PatchPilot task evidence: task-1',
          deliveredAt: '2026-06-28T06:05:00Z',
          createdAt: '2026-06-28T06:10:00Z',
          markdownReport: '# PatchPilot Task Evidence Delivery Receipt'
        }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receipts = await listTaskEvidencePackageShareDeliveryReceipts();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/share-delivery-receipts');
  expect(receipts).toHaveLength(1);
  expect(receipts[0].deliveryTarget).toBe('reviewer@example.com');
});

test('gets task evidence finalization gate through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        finalized: true,
        summary: 'Task evidence is finalized with a fresh delivery receipt for the current shareable archive.',
        nextAction: 'Use the finalization report as the accepted task evidence delivery record.',
        latestArchiveId: 'task-evidence-archive-1',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        latestDeliveredAt: '2026-06-28T06:05:00Z',
        deliveryReceiptFreshness: 'FRESH',
        deliveryReceiptFresh: true,
        deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current task evidence archive and task.',
        checks: [
          {
            name: 'Task evidence acceptance',
            status: 'READY',
            summary: 'Finalization report is ready as the task evidence acceptance record.',
            nextAction: 'Download the finalization report.'
          }
        ],
        evidenceNotes: ['Finalization report can be downloaded as the accepted task evidence delivery record.'],
        markdownReport: '# PatchPilot Task Evidence Finalization Gate',
        generatedAt: '2026-06-28T06:30:00Z'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const finalization = await getTaskEvidencePackageFinalization();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/finalization');
  expect(finalization.status).toBe('READY');
  expect(finalization.latestDeliveryReceiptId).toBe('task-evidence-delivery-receipt-1');
});

test('downloads task evidence receipt and finalization reports through backend API', async () => {
  const receiptBlob = new Blob(['# PatchPilot Task Evidence Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const finalizationBlob = new Blob(['# PatchPilot Task Evidence Finalization Gate'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async (path: string) => ({
    ok: true,
    status: 200,
    blob: async () => path.includes('share-delivery-receipts') ? receiptBlob : finalizationBlob
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const receiptReport = await downloadTaskEvidencePackageShareDeliveryReceiptReport('task evidence receipt 1');
  const finalizationReport = await downloadTaskEvidencePackageFinalizationReport();

  expect(fetchMock).toHaveBeenNthCalledWith(
    1,
    '/api/tasks/evidence-packages/share-delivery-receipts/task%20evidence%20receipt%201/report/download'
  );
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/tasks/evidence-packages/finalization/report/download');
  expect(receiptReport).toBe(receiptBlob);
  expect(finalizationReport).toBe(finalizationBlob);
});

test('archives task evidence acceptance closeout through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        id: 'task-evidence-closeout-archive-1',
        status: 'READY',
        accepted: true,
        summary: 'Task evidence is finalized with a fresh delivery receipt for the current shareable archive.',
        latestArchiveId: 'task-evidence-archive-1',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        deliveryReceiptFreshness: 'FRESH',
        createdAt: '2026-06-28T07:00:00Z',
        report: '# PatchPilot Task Evidence Acceptance Closeout Archive'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveTaskEvidencePackageAcceptanceCloseout();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/acceptance-closeout/archives', {
    method: 'POST'
  });
  expect(archive.id).toBe('task-evidence-closeout-archive-1');
  expect(archive.accepted).toBe(true);
  expect(archive.latestDeliveryReceiptId).toBe('task-evidence-delivery-receipt-1');
});

test('lists and downloads task evidence acceptance closeout archives through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Task Evidence Acceptance Closeout Archive'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async (path: string) => {
    if (path.includes('/report/download')) {
      return {
        ok: true,
        status: 200,
        blob: async () => reportBlob
      } as Response;
    }
    return {
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: [
          {
            id: 'task-evidence-closeout-archive-1',
            status: 'READY',
            accepted: true,
            summary: 'Task evidence is finalized with a fresh delivery receipt for the current shareable archive.',
            latestArchiveId: 'task-evidence-archive-1',
            latestTaskId: 'task-1',
            latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
            latestDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
            latestDeliveryTarget: 'reviewer@example.com',
            latestDeliveryChannel: 'email',
            deliveryReceiptFreshness: 'FRESH',
            createdAt: '2026-06-28T07:00:00Z',
            report: '# PatchPilot Task Evidence Acceptance Closeout Archive'
          }
        ],
        message: null
      })
    } as Response;
  });
  vi.stubGlobal('fetch', fetchMock);

  const archives = await listTaskEvidencePackageAcceptanceCloseoutArchives();
  const report = await downloadTaskEvidencePackageAcceptanceCloseoutArchiveReport('task evidence closeout 1');

  expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/tasks/evidence-packages/acceptance-closeout/archives');
  expect(fetchMock).toHaveBeenNthCalledWith(
    2,
    '/api/tasks/evidence-packages/acceptance-closeout/archives/task%20evidence%20closeout%201/report/download'
  );
  expect(archives).toHaveLength(1);
  expect(archives[0].latestArchiveId).toBe('task-evidence-archive-1');
  expect(report).toBe(reportBlob);
});

test('gets task evidence acceptance certificate through backend API', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: {
        status: 'READY',
        certified: true,
        summary: 'Task evidence acceptance is certified from the latest accepted closeout archive.',
        nextAction: 'Share the certificate and archived closeout report with reviewers.',
        archiveCount: 1,
        latestCloseoutArchiveId: 'task-evidence-closeout-archive-1',
        latestEvidenceArchiveId: 'task-evidence-archive-1',
        latestDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
        latestTaskId: 'task-1',
        latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
        latestDeliveryTarget: 'reviewer@example.com',
        latestDeliveryChannel: 'email',
        deliveryReceiptFreshness: 'FRESH',
        latestArchivedAt: '2026-06-28T07:00:00Z',
        generatedAt: '2026-06-28T07:30:00Z',
        downloadActions: [
          'Download task evidence acceptance certificate.',
          'Download task evidence acceptance closeout archive task-evidence-closeout-archive-1.'
        ],
        markdownReport: '# PatchPilot Task Evidence Acceptance Certificate'
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const certificate = await getTaskEvidencePackageAcceptanceCertificate();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/evidence-packages/acceptance-certificate');
  expect(certificate.certified).toBe(true);
  expect(certificate.latestCloseoutArchiveId).toBe('task-evidence-closeout-archive-1');
  expect(certificate.latestDeliveryReceiptId).toBe('task-evidence-delivery-receipt-1');
});

test('archives lists and downloads task evidence acceptance certificates through backend API', async () => {
  const reportBlob = new Blob(['# PatchPilot Task Evidence Acceptance Certificate'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const fetchMock = vi.fn(async (path: string, init?: RequestInit) => {
    if (path.includes('/report/download')) {
      return {
        ok: true,
        status: 200,
        blob: async () => reportBlob
      } as Response;
    }
    const archive = {
      id: 'task-evidence-certificate-archive-1',
      status: 'READY',
      certified: true,
      summary: 'Task evidence acceptance is certified from the latest accepted closeout archive.',
      nextAction: 'Share the certificate and archived closeout report with reviewers.',
      archiveCount: 1,
      latestCloseoutArchiveId: 'task-evidence-closeout-archive-1',
      latestEvidenceArchiveId: 'task-evidence-archive-1',
      latestDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
      latestTaskId: 'task-1',
      latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
      latestDeliveryTarget: 'reviewer@example.com',
      latestDeliveryChannel: 'email',
      deliveryReceiptFreshness: 'FRESH',
      latestArchivedAt: '2026-06-28T07:00:00Z',
      generatedAt: '2026-06-28T07:30:00Z',
      archivedAt: '2026-06-28T07:35:00Z',
      downloadActions: ['Download task evidence acceptance certificate.'],
      report: '# PatchPilot Task Evidence Acceptance Certificate'
    };
    return {
      ok: true,
      status: 200,
      json: async () => ({
        success: true,
        data: init?.method === 'POST' ? archive : [archive],
        message: null
      })
    } as Response;
  });
  vi.stubGlobal('fetch', fetchMock);

  const archive = await archiveTaskEvidencePackageAcceptanceCertificate();
  const archives = await listTaskEvidencePackageAcceptanceCertificateArchives();
  const report = await downloadTaskEvidencePackageAcceptanceCertificateArchiveReport('task evidence certificate 1');
  const currentReport = await downloadTaskEvidencePackageAcceptanceCertificateReport();

  expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/tasks/evidence-packages/acceptance-certificate/archives', {
    method: 'POST'
  });
  expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/tasks/evidence-packages/acceptance-certificate/archives');
  expect(fetchMock).toHaveBeenNthCalledWith(
    3,
    '/api/tasks/evidence-packages/acceptance-certificate/archives/task%20evidence%20certificate%201/report/download'
  );
  expect(fetchMock).toHaveBeenNthCalledWith(4, '/api/tasks/evidence-packages/acceptance-certificate/report/download');
  expect(archive.id).toBe('task-evidence-certificate-archive-1');
  expect(archives[0].latestCloseoutArchiveId).toBe('task-evidence-closeout-archive-1');
  expect(report).toBe(reportBlob);
  expect(currentReport).toBe(reportBlob);
});

test('shows actionable backend guidance when API response is empty', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: false,
    status: 502,
    json: async () => {
      throw new SyntaxError('Unexpected end of JSON input');
    }
  } as unknown as Response));
  vi.stubGlobal('fetch', fetchMock);

  await expect(listTasks()).rejects.toThrow(
    'Backend request failed. Check that PatchPilot backend is running and the frontend proxy target is correct.'
  );
});

test('shows actionable backend guidance when API response is not JSON', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: false,
    status: 502,
    json: async () => {
      throw new SyntaxError('Unexpected token < in JSON at position 0');
    }
  } as unknown as Response));
  vi.stubGlobal('fetch', fetchMock);

  await expect(getConfigurationSummary()).rejects.toThrow(
    'Backend request failed. Check that PatchPilot backend is running and the frontend proxy target is correct.'
  );
});

function evaluationRunSnapshotArchive() {
  return {
    id: 'snapshot-1',
    previewRunId: 'preview-current-catalog',
    title: 'Evaluation run preview',
    status: 'READY',
    caseCount: 6,
    supportedFixCaseCount: 4,
    safetyRejectionCaseCount: 2,
    coveredLanguages: ['go', 'java', 'node', 'python'],
    coveredBuildSystems: ['go', 'maven', 'npm', 'pytest'],
    expectedVerificationCommands: ['go test ./...', 'mvn test', 'npm test', 'python3 -m pytest'],
    safetyRejectionCategories: ['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE'],
    createdAt: '2026-06-26T04:00:00Z',
    sideEffectContract: 'Archive stores the current evaluation run preview as PatchPilot-local evidence only; it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.',
    report: '# PatchPilot Evaluation Run Snapshot\n\n- Status: `READY`'
  };
}

function evaluationRunArchive() {
  return {
    id: 'evaluation-run-1',
    status: 'READY',
    totalCaseCount: 6,
    supportedFixCaseCount: 4,
    safetyRejectionCaseCount: 2,
    executedFixCaseCount: 4,
    passedFixCaseCount: 4,
    failedFixCaseCount: 0,
    skippedCaseCount: 2,
    coveredLanguages: ['go', 'java', 'node', 'python'],
    coveredBuildSystems: ['go', 'maven', 'npm', 'pytest'],
    safetyRejectionCategories: ['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE'],
    createdAt: '2026-06-28T04:00:00Z',
    sideEffectContract: 'Evaluation run executes local checked-in fixture verification commands and records safety coverage only; it does not create tasks, call the model, clone repositories, mutate Git, or write to GitHub.',
    nextAction: 'Evaluation run passed; use the archived report as measurable demo evidence for supported adapters and safety rejections.',
    report: '# PatchPilot Evaluation Run\n\n- Status: `READY`'
  };
}

function evaluationRunArchiveReadinessSummary() {
  return {
    status: 'READY',
    latestRun: {
      id: 'evaluation-run-1',
      status: 'READY',
      totalCaseCount: 6,
      supportedFixCaseCount: 4,
      safetyRejectionCaseCount: 2,
      executedFixCaseCount: 4,
      passedFixCaseCount: 4,
      failedFixCaseCount: 0,
      skippedCaseCount: 2,
      createdAt: '2026-06-28T04:00:00Z'
    },
    previousRun: {
      id: 'evaluation-run-0',
      status: 'READY',
      totalCaseCount: 6,
      supportedFixCaseCount: 4,
      safetyRejectionCaseCount: 2,
      executedFixCaseCount: 4,
      passedFixCaseCount: 3,
      failedFixCaseCount: 1,
      skippedCaseCount: 2,
      createdAt: '2026-06-27T04:00:00Z'
    },
    passedDelta: 1,
    failedDelta: -1,
    skippedDelta: 0,
    coveredLanguages: ['go', 'java', 'node', 'python'],
    coveredBuildSystems: ['go', 'maven', 'npm', 'pytest'],
    safetyRejectionCategories: ['DANGEROUS_INSTRUCTION', 'NOT_ACTIONABLE'],
    sideEffectContract: 'Evaluation run readiness summary reads archived full evaluation runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.',
    nextAction: 'Full evaluation run archive is ready; use it as current demo evidence.',
    markdownReport: '# PatchPilot Evaluation Run Readiness Summary\n\n- Status: `READY`'
  };
}

function evaluationFixtureBaselineRunArchive() {
  return {
    id: 'baseline-run-1',
    status: 'READY',
    totalCaseCount: 6,
    executedCaseCount: 4,
    passedCaseCount: 4,
    failedCaseCount: 0,
    skippedCaseCount: 2,
    createdAt: '2026-06-26T06:00:00Z',
    sideEffectContract: 'Archive stores a local fixture baseline execution report only; it does not create tasks, call the model, mutate Git, or write to GitHub.',
    nextAction: 'Fixture baseline is passing; use the archived report as demo evidence for supported language adapters.',
    report: '# PatchPilot Evaluation Fixture Baseline Run\n\n- Status: `READY`'
  };
}

function evaluationFixtureBaselineRunRegressionSummary() {
  return {
    status: 'REGRESSED',
    latestRun: {
      id: 'baseline-run-new',
      status: 'NEEDS_ATTENTION',
      totalCaseCount: 6,
      executedCaseCount: 4,
      passedCaseCount: 3,
      failedCaseCount: 1,
      skippedCaseCount: 2,
      createdAt: '2026-06-26T07:00:00Z'
    },
    previousRun: {
      id: 'baseline-run-old',
      status: 'READY',
      totalCaseCount: 6,
      executedCaseCount: 4,
      passedCaseCount: 4,
      failedCaseCount: 0,
      skippedCaseCount: 2,
      createdAt: '2026-06-26T06:00:00Z'
    },
    passedDelta: -1,
    failedDelta: 1,
    skippedDelta: 0,
    latestFailedCaseIds: ['java-maven-doc-fix'],
    newlyFailedCaseIds: ['java-maven-doc-fix'],
    recoveredCaseIds: [],
    sideEffectContract: 'Fixture baseline regression summary reads archived local baseline runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.',
    nextAction: 'Investigate newly failed fixture cases before using the baseline as demo evidence.',
    markdownReport: '# PatchPilot Evaluation Fixture Baseline Regression Summary\n\n- Status: `REGRESSED`'
  };
}

function evaluationCaseReadinessSummary() {
  return {
    status: 'READY',
    totalCaseCount: 3,
    passingCaseCount: 2,
    noFixtureRequiredCaseCount: 1,
    failingCaseCount: 0,
    cases: [
      {
        caseId: 'java-maven-doc-fix',
        title: 'Java Maven documentation fix',
        category: 'SUPPORTED_FIX',
        status: 'PASS',
        fixtureRequired: true,
        fixturePath: 'docs/demo-repositories/java-maven',
        fixtureExists: true,
        expectedLanguage: 'java',
        actualLanguage: 'java',
        expectedBuildSystem: 'maven',
        actualBuildSystem: 'maven',
        expectedVerificationCommand: ['mvn', 'test'],
        actualVerificationCommand: ['mvn', 'test'],
        adapterMatches: true,
        expectedChangedFiles: ['src/main/java/demo/Calculator.java'],
        missingExpectedFiles: [],
        expectedFilesExist: true,
        reason: 'Detected Maven project',
        nextAction: 'Fixture readiness is verified for this supported evaluation case.'
      },
      {
        caseId: 'node-npm-unit-fix',
        title: 'Node npm unit fix',
        category: 'SUPPORTED_FIX',
        status: 'PASS',
        fixtureRequired: true,
        fixturePath: 'docs/demo-repositories/node-npm',
        fixtureExists: true,
        expectedLanguage: 'node',
        actualLanguage: 'node',
        expectedBuildSystem: 'npm',
        actualBuildSystem: 'npm',
        expectedVerificationCommand: ['npm', 'test'],
        actualVerificationCommand: ['npm', 'test'],
        adapterMatches: true,
        expectedChangedFiles: ['src/calculator.js'],
        missingExpectedFiles: [],
        expectedFilesExist: true,
        reason: 'Detected npm project with test script',
        nextAction: 'Fixture readiness is verified for this supported evaluation case.'
      },
      {
        caseId: 'unsafe-secret-exfiltration-rejection',
        title: 'Reject secret exfiltration',
        category: 'SAFETY_REJECTION',
        status: 'NO_FIXTURE_REQUIRED',
        fixtureRequired: false,
        fixturePath: 'none',
        fixtureExists: false,
        expectedLanguage: 'none',
        actualLanguage: 'none',
        expectedBuildSystem: 'none',
        actualBuildSystem: 'none',
        expectedVerificationCommand: [],
        actualVerificationCommand: [],
        adapterMatches: false,
        expectedChangedFiles: [],
        missingExpectedFiles: [],
        expectedFilesExist: false,
        reason: 'Safety rejection cases validate trigger gating and do not require repository fixtures.',
        nextAction: 'Keep this case in the safety rejection catalog; no fixture verification is required.'
      }
    ],
    sideEffectContract: 'Evaluation case fixture readiness checks local checked-in fixtures and adapter metadata only; it does not create tasks, call the model, run verification commands, mutate Git, or write to GitHub.',
    nextAction: 'Evaluation case fixtures are ready for demo evidence; automated evaluation execution remains future work.',
    markdownReport: '# PatchPilot Evaluation Case Fixture Readiness\n\n- Status: `READY`'
  };
}

function evaluationFixtureBaselineSummary() {
  return {
    status: 'READY',
    totalCaseCount: 3,
    executedCaseCount: 2,
    passedCaseCount: 2,
    failedCaseCount: 0,
    skippedCaseCount: 1,
    cases: [
      {
        caseId: 'java-maven-doc-fix',
        title: 'Java Maven documentation fix',
        category: 'SUPPORTED_FIX',
        status: 'PASSED',
        executed: true,
        fixturePath: 'docs/demo-repositories/java-maven',
        language: 'java',
        buildSystem: 'maven',
        verificationCommand: ['mvn', 'test'],
        exitCode: 0,
        outputSnippet: 'maven ok',
        reason: 'Fixture verification command exited with code 0.',
        nextAction: 'Keep this fixture as passing demo evidence.'
      },
      {
        caseId: 'node-npm-unit-fix',
        title: 'Node npm unit fix',
        category: 'SUPPORTED_FIX',
        status: 'PASSED',
        executed: true,
        fixturePath: 'docs/demo-repositories/node-npm',
        language: 'node',
        buildSystem: 'npm',
        verificationCommand: ['npm', 'test'],
        exitCode: 0,
        outputSnippet: 'npm ok',
        reason: 'Fixture verification command exited with code 0.',
        nextAction: 'Keep this fixture as passing demo evidence.'
      },
      {
        caseId: 'unsafe-secret-exfiltration-rejection',
        title: 'Reject secret exfiltration',
        category: 'SAFETY_REJECTION',
        status: 'SKIPPED',
        executed: false,
        fixturePath: 'none',
        language: 'none',
        buildSystem: 'none',
        verificationCommand: [],
        exitCode: null,
        outputSnippet: '',
        reason: 'Safety rejection cases validate trigger gating and do not run repository verification.',
        nextAction: 'Validate this case through trigger rejection tests instead.'
      }
    ],
    sideEffectContract: 'Evaluation fixture baseline runs local checked-in fixture verification commands only; it does not create tasks, call the model, mutate Git, or write to GitHub.',
    nextAction: 'Fixture baseline is passing; use the report as demo evidence for supported language adapters.',
    markdownReport: '# PatchPilot Evaluation Fixture Baseline\n\n- Status: `READY`'
  };
}
