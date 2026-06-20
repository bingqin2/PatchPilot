import { getFailureCauseSummary, getLatencySummary, getModelUsageSummary, listTasks } from './api';

afterEach(() => {
  vi.unstubAllGlobals();
});

test('builds backend task search and pagination query parameters', async () => {
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
    limit: 25,
    offset: 50
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=25&offset=50&query=search+target&status=FAILED');
  expect(page).toEqual({
    items: [],
    limit: 25,
    offset: 50,
    hasMore: true,
    total: 74
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
