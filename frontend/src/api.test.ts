import {
  approveTaskReview,
  createTask,
  createTriggerQuarantine,
  ADMIN_TOKEN_STORAGE_KEY,
  getBackendHealth,
  getConfigurationSummary,
  getFailureCauseSummary,
  getLatencySummary,
  getModelUsageSummary,
  getDemoScript,
  archiveDemoSession,
  listDemoSessionArchives,
  getDemoSessionSnapshot,
  getDemoSessionReport,
  downloadDemoSessionReport,
  downloadDemoSessionArchiveReport,
  getDemoSmokeChecklist,
  getDemoEvidenceBundle,
  getDemoRunbook,
  getRejectedTriggerSummary,
  getTriggerQuarantineEvidence,
  getWorkerHealth,
  listLanguageAdapterFixtures,
  listLanguageAdapters,
  listOperatorSafetyAudits,
  listRejectedTriggers,
  listTriggerQuarantines,
  preflightRepository,
  releaseTriggerQuarantine,
  retryRejectedTrigger,
  getDemoReadiness,
  getTaskReport,
  getTaskDetail,
  getTaskRetryPreflight,
  getTaskStatusCounts,
  listWebhookDeliveries,
  listTasks,
  retryTask
} from './api';

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
        latestWebhookDelivery: null,
        rejectedTriggerSummary: {
          totalCount: 0,
          categoryCounts: [],
          sourceCounts: [],
          triggerUserCounts: [],
          repositoryCounts: []
        },
        activeQuarantineCount: 0,
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
          latestWebhookDelivery: null,
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
