import {
  createTask,
  getBackendHealth,
  getConfigurationSummary,
  getFailureCauseSummary,
  getLatencySummary,
  getModelUsageSummary,
  getTaskReport,
  getTaskDetail,
  getTaskStatusCounts,
  listTasks
} from './api';

afterEach(() => {
  vi.unstubAllGlobals();
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
    createdAfter: ' 2026-06-20T01:00:00Z ',
    createdBefore: ' 2026-06-21T01:00:00Z ',
    limit: 25,
    offset: 50,
    sort: 'createdAtAsc'
  });

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/tasks?limit=25&offset=50&query=search+target&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z&sort=createdAtAsc&status=FAILED'
  );
  expect(page).toEqual({
    items: [],
    limit: 25,
    offset: 50,
    hasMore: true,
    total: 74
  });
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
    createdAfter: ' 2026-06-20T01:00:00Z ',
    createdBefore: ' 2026-06-21T01:00:00Z ',
    limit: 25,
    offset: 50,
    sort: 'createdAtAsc'
  });

  expect(fetchMock).toHaveBeenCalledWith(
    '/api/tasks/status-counts?query=search+target&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z'
  );
  expect(counts).toEqual({
    totalCount: 9,
    pendingCount: 1,
    runningCount: 2,
    runningTestsCount: 0,
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
        modelTriggerClassificationEnabled: true
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
    modelTriggerClassificationEnabled: true
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
        modelCalls: []
      },
      message: null
    })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  const detail = await getTaskDetail('task-1');

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-1/detail');
  expect(detail.summary.task.id).toBe('task-1');
  expect(detail.queueItem?.status).toBe('FAILED');
  expect(detail.queueItem?.attemptCount).toBe(3);
  expect(detail.queueItems.map((item) => item.id)).toEqual(['queue-1', 'queue-older']);
  expect(detail.timeline).toEqual([]);
  expect(detail.testRuns).toEqual([]);
  expect(detail.toolCalls).toEqual([]);
  expect(detail.modelCalls).toEqual([]);
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
