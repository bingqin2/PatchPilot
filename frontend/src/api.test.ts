import { listTasks } from './api';

afterEach(() => {
  vi.unstubAllGlobals();
});

test('builds backend task search and pagination query parameters', async () => {
  const fetchMock = vi.fn(async () => ({
    ok: true,
    status: 200,
    json: async () => ({ success: true, data: [], message: null })
  } as Response));
  vi.stubGlobal('fetch', fetchMock);

  await listTasks({
    status: 'FAILED',
    query: ' search target ',
    limit: 25,
    offset: 50
  });

  expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=25&offset=50&query=search+target&status=FAILED');
});
