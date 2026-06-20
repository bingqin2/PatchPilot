import { getFailureCauseSummary, listTasks } from './api';

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
