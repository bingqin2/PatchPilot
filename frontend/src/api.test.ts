import {
  approveTaskReview,
  createTask,
  ADMIN_TOKEN_STORAGE_KEY,
  getBackendHealth,
  getConfigurationSummary,
  getFailureCauseSummary,
  getLatencySummary,
  getModelUsageSummary,
  getDemoSmokeChecklist,
  getRejectedTriggerSummary,
  listLanguageAdapterFixtures,
  listLanguageAdapters,
  listRejectedTriggers,
  retryRejectedTrigger,
  getDemoReadiness,
  getTaskReport,
  getTaskDetail,
  getTaskStatusCounts,
  listWebhookDeliveries,
  listTasks
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
        { cause: 'MAVEN_TESTS', count: 2 },
        { cause: 'GITHUB_AUTH', count: 1 }
      ],
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const causes = await getFailureCauseSummary();

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/metrics/failure-causes');
  expect(causes).toEqual([
    { cause: 'MAVEN_TESTS', count: 2 },
    { cause: 'GITHUB_AUTH', count: 1 }
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
        workspaceRootDir: '/tmp/patchpilot/workspaces',
        queueMaxAttempts: 3,
        queueRetryDelayMs: 30000,
        queueVisibilityTimeoutMs: 300000,
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
        reviewApprovalAllowedOperators: ['release-captain', 'local-operator']
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
    workspaceRootDir: '/tmp/patchpilot/workspaces',
    queueMaxAttempts: 3,
    queueRetryDelayMs: 30000,
    queueVisibilityTimeoutMs: 300000,
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
    reviewApprovalAllowedOperators: ['release-captain', 'local-operator']
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
