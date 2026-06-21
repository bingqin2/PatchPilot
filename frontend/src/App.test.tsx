import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App';

const completedTask = {
  id: 'task-1',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 1,
  installationId: 0,
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
  deliveryId: 'delivery-1',
  commentId: 101,
  status: 'COMPLETED',
  failureReason: null,
  createdAt: '2026-06-20T01:00:00Z',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  completedAt: '2026-06-20T01:01:00Z',
  updatedAt: '2026-06-20T01:01:00Z',
  statusCommentId: null,
  statusCommentUrl: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-4756084894'
};

const failedTask = {
  id: 'task-2',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 2,
  installationId: 0,
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix replace docs/demo.md broken',
  deliveryId: 'delivery-2',
  commentId: 102,
  status: 'FAILED',
  failureReason: 'maven tests failed',
  createdAt: '2026-06-20T01:05:00Z',
  pullRequestUrl: null,
  completedAt: null,
  updatedAt: '2026-06-20T01:06:00Z',
  statusCommentId: null,
  statusCommentUrl: null
};

const runningTask = {
  id: 'task-3',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 3,
  installationId: 0,
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix running task',
  deliveryId: 'delivery-3',
  commentId: 103,
  status: 'RUNNING',
  failureReason: null,
  createdAt: '2026-06-20T01:10:00Z',
  pullRequestUrl: null,
  completedAt: null,
  updatedAt: '2026-06-20T01:10:30Z',
  statusCommentId: null,
  statusCommentUrl: null
};

const cancelledTask = {
  ...runningTask,
  status: 'CANCELLED',
  failureReason: 'Task cancelled by user request',
  updatedAt: '2026-06-20T01:11:00Z'
};

const retriedTask = {
  ...failedTask,
  status: 'PENDING',
  failureReason: null,
  updatedAt: '2026-06-20T01:07:00Z'
};

const summary = {
  task: completedTask,
  timelineEventCount: 4,
  testRunCount: 1,
  toolCallCount: 3,
  modelCallCount: 2,
  totalModelTokens: 1800,
  latestTimelineEvent: {
    id: 'timeline-1',
    taskId: 'task-1',
    eventType: 'COMPLETED',
    message: 'Task completed',
    createdAt: '2026-06-20T01:01:00Z'
  },
  latestTestRunExitCode: 0,
  latestTestRunDurationMs: 12769
};

const timeline = [
  {
    id: 'timeline-1',
    taskId: 'task-1',
    eventType: 'TASK_CREATED',
    message: 'Task accepted',
    createdAt: '2026-06-20T01:00:00Z'
  },
  {
    id: 'timeline-2',
    taskId: 'task-1',
    eventType: 'COMPLETED',
    message: 'Pull request opened',
    createdAt: '2026-06-20T01:01:00Z'
  }
];

const testRuns = [
  {
    id: 'test-run-1',
    taskId: 'task-1',
    command: './mvnw test',
    exitCode: 0,
    output: 'Tests run: 247, Failures: 0, Errors: 0',
    startedAt: '2026-06-20T01:00:30Z',
    finishedAt: '2026-06-20T01:00:43Z',
    durationMs: 12769
  }
];

const toolCalls = [
  {
    id: 'tool-call-1',
    taskId: 'task-1',
    toolName: 'replace',
    inputSummary: 'docs/demo.md',
    outputSummary: 'updated file',
    success: true,
    startedAt: '2026-06-20T01:00:20Z',
    finishedAt: '2026-06-20T01:00:21Z',
    durationMs: 1000
  }
];

const modelCalls = [
  {
    id: 'model-call-1',
    taskId: 'task-1',
    provider: 'openai-compatible',
    model: 'gpt-5.5',
    promptSummary: 'Fix issue',
    responseSummary: 'Plan generated',
    promptTokens: 1000,
    completionTokens: 800,
    totalTokens: 1800,
    success: true,
    errorMessage: null,
    startedAt: '2026-06-20T01:00:10Z',
    finishedAt: '2026-06-20T01:00:12Z',
    durationMs: 2000
  }
];

const detail = {
  summary,
  timeline,
  testRuns,
  toolCalls,
  modelCalls
};

const modelUsageSummary = {
  totalPromptTokens: 1500,
  totalCompletionTokens: 650,
  totalTokens: 2150,
  successfulCalls: 2,
  failedCalls: 1,
  estimatedCostUsd: 0.0028
};

const latencySummary = {
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
};

const queueSummary = {
  totalCount: 4,
  pendingCount: 2,
  availablePendingCount: 1,
  delayedPendingCount: 1,
  runningCount: 1,
  completedCount: 0,
  failedCount: 1,
  cancelledCount: 0
};

const queueItems = [
  {
    id: 'queue-1',
    taskId: 'task-3',
    status: 'RUNNING',
    attemptCount: 2,
    lastError: null,
    availableAt: '2026-06-20T01:10:00Z',
    lockedAt: '2026-06-20T01:10:30Z',
    createdAt: '2026-06-20T01:09:00Z',
    updatedAt: '2026-06-20T01:10:30Z'
  },
  {
    id: 'queue-2',
    taskId: 'task-2',
    status: 'FAILED',
    attemptCount: 3,
    lastError: 'maven test command timed out',
    availableAt: '2026-06-20T01:05:00Z',
    lockedAt: null,
    createdAt: '2026-06-20T01:04:00Z',
    updatedAt: '2026-06-20T01:06:00Z'
  }
];

const configurationSummary = {
  agentProvider: 'openai-compatible',
  agentModel: 'gpt-5.5',
  agentBaseUrl: 'https://api.example.test/v1',
  agentApiKeyConfigured: true,
  githubTokenConfigured: true,
  githubWebhookSecretConfigured: true,
  workspaceRootDir: '/tmp/patchpilot/workspaces',
  queueMaxAttempts: 3,
  queueRetryDelayMs: 30000,
  queueVisibilityTimeoutMs: 300000,
  modelCostConfigured: true
};

beforeEach(() => {
  vi.stubGlobal('fetch', vi.fn(async (input: RequestInfo | URL) => {
    const url = input.toString();
    if (url === '/api/tasks?limit=50') {
      return jsonResponse(taskPage([completedTask, failedTask]));
    }
    if (url === '/api/tasks?limit=50&status=RUNNING') {
      return jsonResponse(taskPage([runningTask]));
    }
    if (url === '/api/tasks?limit=50&status=FAILED') {
      return jsonResponse(taskPage([failedTask]));
    }
    if (url === '/api/tasks?limit=50&query=broken') {
      return jsonResponse(taskPage([failedTask]));
    }
    if (url === '/api/tasks?limit=50&query=broken&status=FAILED') {
      return jsonResponse(taskPage([failedTask]));
    }
    if (url === '/api/tasks?limit=50&status=CANCELLED') {
      return jsonResponse(taskPage([]));
    }
    if (url === '/api/tasks/metrics/summary') {
      return jsonResponse({
        totalCount: 2,
        pendingCount: 0,
        runningCount: 0,
        runningTestsCount: 0,
        completedCount: 1,
        failedCount: 1,
        cancelledCount: 0,
        completionRate: 0.5,
        failureRate: 0.5,
        averageCompletionDurationMs: 60000,
        totalModelTokens: 1800,
        averageModelTokensPerCompletedTask: 1800,
        testRunCount: 1,
        passedTestRunCount: 1,
        failedTestRunCount: 0,
        testPassRate: 1
      });
    }
    if (url === '/api/tasks/metrics/failure-causes') {
      return jsonResponse([
        { cause: 'MAVEN_TESTS', count: 1 },
        { cause: 'GITHUB_AUTH', count: 1 }
      ]);
    }
    if (url === '/api/tasks/metrics/model-usage') {
      return jsonResponse(modelUsageSummary);
    }
    if (url === '/api/tasks/metrics/latency') {
      return jsonResponse(latencySummary);
    }
    if (url === '/api/configuration/summary') {
      return jsonResponse(configurationSummary);
    }
    if (url === '/health') {
      return jsonResponse({
        status: 'UP',
        service: 'patchpilot-backend',
        timestamp: '2026-06-21T01:00:00Z'
      });
    }
    if (url === '/api/task-queue/summary') {
      return jsonResponse(queueSummary);
    }
    if (url === '/api/task-queue/items') {
      return jsonResponse(queueItems);
    }
    if (url === '/api/tasks/task-1/detail') {
      return jsonResponse(detail);
    }
    if (url === '/api/tasks/task-1/summary') {
      return jsonResponse(summary);
    }
    if (url === '/api/tasks/task-1/timeline') {
      return jsonResponse(timeline);
    }
    if (url === '/api/tasks/task-1/test-runs') {
      return jsonResponse(testRuns);
    }
    if (url === '/api/tasks/task-1/tool-calls') {
      return jsonResponse(toolCalls);
    }
    if (url === '/api/tasks/task-1/model-calls') {
      return jsonResponse(modelCalls);
    }
    if (url === '/api/tasks/task-2/summary') {
      return jsonResponse({
        ...summary,
        task: failedTask,
        latestTimelineEvent: {
          id: 'timeline-failed',
          taskId: 'task-2',
          eventType: 'FAILED',
          message: 'Task failed',
          createdAt: '2026-06-20T01:06:00Z'
        }
      });
    }
    if (url === '/api/tasks/task-2/detail') {
      return jsonResponse({
        summary: {
          ...summary,
          task: failedTask,
          latestTimelineEvent: {
            id: 'timeline-failed',
            taskId: 'task-2',
            eventType: 'FAILED',
            message: 'Task failed',
            createdAt: '2026-06-20T01:06:00Z'
          }
        },
        timeline: [],
        testRuns: [],
        toolCalls: [],
        modelCalls: []
      });
    }
    if (url === '/api/tasks/task-2/timeline') {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/task-2/test-runs') {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/task-2/tool-calls') {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/task-2/model-calls') {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/task-3/summary') {
      return jsonResponse({
        ...summary,
        task: runningTask,
        latestTimelineEvent: {
          id: 'timeline-running',
          taskId: 'task-3',
          eventType: 'RUNNING',
          message: 'Task is running',
          createdAt: '2026-06-20T01:10:30Z'
        }
      });
    }
    if (url === '/api/tasks/task-3/detail') {
      return jsonResponse({
        summary: {
          ...summary,
          task: runningTask,
          latestTimelineEvent: {
            id: 'timeline-running',
            taskId: 'task-3',
            eventType: 'RUNNING',
            message: 'Task is running',
            createdAt: '2026-06-20T01:10:30Z'
          }
        },
        timeline: [
          {
            id: 'timeline-running',
            taskId: 'task-3',
            eventType: 'RUNNING',
            message: 'Task is running',
            createdAt: '2026-06-20T01:10:30Z'
          }
        ],
        testRuns: [],
        toolCalls: [],
        modelCalls: []
      });
    }
    if (url === '/api/tasks/task-3/timeline') {
      return jsonResponse([
        {
          id: 'timeline-running',
          taskId: 'task-3',
          eventType: 'RUNNING',
          message: 'Task is running',
          createdAt: '2026-06-20T01:10:30Z'
        }
      ]);
    }
    if (url === '/api/tasks/task-3/test-runs') {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/task-3/tool-calls') {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/task-3/model-calls') {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/task-3/cancel') {
      return jsonResponse(cancelledTask);
    }
    if (url === '/api/tasks/task-2/retry') {
      return jsonResponse(retriedTask);
    }
    return jsonResponse(null, false, 'not found', 404);
  }));
});

afterEach(() => {
  vi.unstubAllGlobals();
  vi.useRealTimers();
  window.history.replaceState(null, '', '/');
});

test('renders operational task dashboard from backend APIs', async () => {
  vi.useFakeTimers({ shouldAdvanceTime: true });
  vi.setSystemTime(new Date('2026-06-21T08:15:30Z'));
  const fetchMock = vi.mocked(fetch);
  render(<App />);

  expect(await screen.findByRole('heading', { name: 'PatchPilot Operations' })).toBeInTheDocument();
  expect(screen.getByText(/Last refreshed/)).toBeInTheDocument();
  expect(screen.getByText(/Last refreshed/)).toHaveAttribute('datetime', '2026-06-21T08:15:30.000Z');
  expect(screen.getByText('2 of 2 tasks visible')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /COMPLETED bingqin2\/PatchPilot #1/ })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /FAILED bingqin2\/PatchPilot #2/ })).toBeInTheDocument();
  expect(screen.getByText('maven tests failed')).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'PR #8' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/8'
  );
  const issueLinks = screen.getAllByRole('link', { name: 'Open Issue' });
  expect(issueLinks).toHaveLength(3);
  expect(issueLinks[0]).toHaveAttribute('href', 'https://github.com/bingqin2/PatchPilot/issues/1');
  expect(issueLinks[1]).toHaveAttribute('href', 'https://github.com/bingqin2/PatchPilot/issues/2');
  expect(issueLinks[2]).toHaveAttribute('href', 'https://github.com/bingqin2/PatchPilot/issues/1');
  const statusCommentLinks = screen.getAllByRole('link', { name: 'Status Comment' });
  expect(statusCommentLinks).toHaveLength(2);
  expect(statusCommentLinks[0]).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-4756084894'
  );
  expect(statusCommentLinks[1]).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-4756084894'
  );

  expect(screen.getByText('Completion')).toBeInTheDocument();
  expect(screen.getByText('50%')).toBeInTheDocument();
  expect(screen.getByText('Test pass')).toBeInTheDocument();
  expect(screen.getByText('100%')).toBeInTheDocument();
  expect(screen.getByText('Failure causes')).toBeInTheDocument();
  expect(screen.getByText('Maven tests')).toBeInTheDocument();
  expect(screen.getByText('GitHub auth')).toBeInTheDocument();
  expect(screen.getByText('Model usage')).toBeInTheDocument();
  expect(screen.getByText('2,150')).toBeInTheDocument();
  expect(screen.getByText('2 successful')).toBeInTheDocument();
  expect(screen.getByText('1 failed')).toBeInTheDocument();
  expect(screen.getByText('$0.0028')).toBeInTheDocument();
  expect(screen.getByText('Latency')).toBeInTheDocument();
  expect(screen.getByText('20.0s avg task')).toBeInTheDocument();
  expect(screen.getByText('4.0s model avg')).toBeInTheDocument();
  expect(screen.getByText('2.0s tool avg')).toBeInTheDocument();
  expect(screen.getByText('7.0s test avg')).toBeInTheDocument();
  expect(screen.getByText('Configuration')).toBeInTheDocument();
  expect(screen.getByText('openai-compatible')).toBeInTheDocument();
  expect(screen.getByText('https://api.example.test/v1')).toBeInTheDocument();
  expect(screen.getByText('/tmp/patchpilot/workspaces')).toBeInTheDocument();
  expect(screen.getByText('Configuration healthy')).toBeInTheDocument();
  expect(screen.getByText('Backend UP')).toBeInTheDocument();
  expect(screen.getByText('patchpilot-backend')).toBeInTheDocument();
  expect(screen.getByText('Agent key Configured')).toBeInTheDocument();
  expect(screen.getByText('Webhook secret Configured')).toBeInTheDocument();
  expect(screen.getByText('Queue attempts 3')).toBeInTheDocument();
  expect(screen.getByText('Queue')).toBeInTheDocument();
  expect(screen.getByText('Queue has failures')).toBeInTheDocument();
  expect(screen.getByText('1 failed item')).toBeInTheDocument();
  expect(screen.getByText('1 running item')).toBeInTheDocument();
  expect(screen.getByText('1 delayed')).toBeInTheDocument();
  expect(screen.getByText('maven test command timed out')).toBeInTheDocument();

  await waitFor(() => expect(screen.getByText('Task completed')).toBeInTheDocument());
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-1/detail'));
  expect(screen.getByText('Pull request opened')).toBeInTheDocument();
  expect(screen.getByText('Tests run: 247, Failures: 0, Errors: 0')).toBeInTheDocument();
  expect(screen.getByText('replace')).toBeInTheDocument();
  expect(screen.getAllByText('gpt-5.5')).toHaveLength(2);
});

test('shows tool and model call durations in task detail records', async () => {
  render(<App />);

  await waitFor(() => expect(screen.getByText('replace')).toBeInTheDocument());
  expect(screen.getByText('success · 1.0s')).toBeInTheDocument();
  expect(screen.getByText('1800 tokens · 2.0s')).toBeInTheDocument();
});

test('selects task detail from taskId URL parameter', async () => {
  window.history.replaceState(null, '', '/?taskId=task-2');

  render(<App />);

  await waitFor(() => expect(screen.getByText('Task failed')).toBeInTheDocument());
  expect(screen.getByRole('heading', { name: 'bingqin2/PatchPilot #2' })).toBeInTheDocument();
  expect(within(screen.getByRole('heading', { name: 'bingqin2/PatchPilot #2' }).closest('section')!).getByText('task-2')).toBeInTheDocument();
  expect(screen.getByText('Latest test PASS')).toBeInTheDocument();
});

test('updates taskId URL parameter when selecting a task', async () => {
  const user = userEvent.setup();

  render(<App />);

  await user.click(await screen.findByRole('button', { name: /FAILED bingqin2\/PatchPilot #2/ }));

  expect(window.location.search).toBe('?taskId=task-2');
});

test('loads queue summary and items from backend APIs', async () => {
  const fetchMock = vi.mocked(fetch);

  render(<App />);

  expect(await screen.findByText('Queue')).toBeInTheDocument();
  expect(screen.getByText('2 pending')).toBeInTheDocument();
  expect(screen.getByText('1 available')).toBeInTheDocument();
  expect(screen.getByText('queue-1')).toBeInTheDocument();
  expect(screen.getByText('task-3')).toBeInTheDocument();
  expect(screen.getByText('attempt 2')).toBeInTheDocument();

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/task-queue/summary'));
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/task-queue/items'));
});

test('shows task creation and update times in task rows', async () => {
  render(<App />);

  const completedTaskRow = await screen.findByRole('button', { name: /COMPLETED bingqin2\/PatchPilot #1/ });
  const completedTimes = within(completedTaskRow).getAllByText(/^(Created|Updated) /);
  expect(completedTimes[0]).toHaveTextContent(/^Created /);
  expect(completedTimes[0]).toHaveAttribute('datetime', completedTask.createdAt);
  expect(completedTimes[1]).toHaveTextContent(/^Updated /);
  expect(completedTimes[1]).toHaveAttribute('datetime', completedTask.updatedAt);

  const failedTaskRow = screen.getByRole('button', { name: /FAILED bingqin2\/PatchPilot #2/ });
  const failedTimes = within(failedTaskRow).getAllByText(/^(Created|Updated) /);
  expect(failedTimes[0]).toHaveTextContent(/^Created /);
  expect(failedTimes[0]).toHaveAttribute('datetime', failedTask.createdAt);
  expect(failedTimes[1]).toHaveTextContent(/^Updated /);
  expect(failedTimes[1]).toHaveAttribute('datetime', failedTask.updatedAt);
});

test('shows an actionable error when a backend request fails', async () => {
  vi.stubGlobal('fetch', vi.fn(async () => jsonResponse(null, false, 'backend unavailable', 500)));

  render(<App />);

  const alert = await screen.findByRole('alert');
  expect(within(alert).getByText('backend unavailable')).toBeInTheDocument();
});

test('shows dashboard refresh progress while top-level data is loading', async () => {
  let resolveTasks: ((value: Response) => void) | undefined;
  const fetchMock = vi.fn(async (input: RequestInfo | URL) => {
    const url = input.toString();
    if (url === '/api/tasks?limit=50') {
      return new Promise<Response>((resolve) => {
        resolveTasks = resolve;
      });
    }
    if (url === '/api/tasks/metrics/summary') {
      return jsonResponse({
        totalCount: 0,
        pendingCount: 0,
        runningCount: 0,
        runningTestsCount: 0,
        completedCount: 0,
        failedCount: 0,
        cancelledCount: 0,
        completionRate: 0,
        failureRate: 0,
        averageCompletionDurationMs: 0,
        totalModelTokens: 0,
        averageModelTokensPerCompletedTask: 0,
        testRunCount: 0,
        passedTestRunCount: 0,
        failedTestRunCount: 0,
        testPassRate: 0
      });
    }
    if (url === '/api/tasks/metrics/failure-causes') {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/metrics/model-usage') {
      return jsonResponse(modelUsageSummary);
    }
    if (url === '/api/tasks/metrics/latency') {
      return jsonResponse(latencySummary);
    }
    if (url === '/api/configuration/summary') {
      return jsonResponse(configurationSummary);
    }
    if (url === '/health') {
      return jsonResponse({
        status: 'UP',
        service: 'patchpilot-backend',
        timestamp: '2026-06-21T01:00:00Z'
      });
    }
    if (url === '/api/task-queue/summary') {
      return jsonResponse(queueSummary);
    }
    if (url === '/api/task-queue/items') {
      return jsonResponse([]);
    }
    return jsonResponse(null, false, 'not found', 404);
  });
  vi.stubGlobal('fetch', fetchMock);

  render(<App />);

  expect(await screen.findByRole('status')).toHaveTextContent('Dashboard refreshing');
  expect(screen.getByRole('button', { name: 'Refreshing dashboard' })).toBeDisabled();

  resolveTasks?.(await jsonResponse(taskPage([])));

  await waitFor(() => expect(screen.queryByRole('status')).not.toBeInTheDocument());
  expect(screen.getByRole('button', { name: 'Refresh dashboard' })).toBeEnabled();
});

test('filters tasks by status with backend query parameters', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);

  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'FAILED' }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&status=FAILED'));
  expect(screen.queryByText('/agent fix replace docs/demo.md PatchPilot smoke test')).not.toBeInTheDocument();
  expect(await screen.findByText('/agent fix replace docs/demo.md broken')).toBeInTheDocument();
  expect(await screen.findByText('Task failed')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'CANCELLED' }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&status=CANCELLED'));
  expect(screen.getByText('No CANCELLED tasks found.')).toBeInTheDocument();
  expect(screen.getByText('Select a task to inspect execution records.')).toBeInTheDocument();
});

test('searches tasks with backend query parameters', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);

  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();

  await user.type(screen.getByRole('searchbox', { name: 'Search tasks' }), 'broken');

  expect(screen.queryByText('/agent fix replace docs/demo.md PatchPilot smoke test')).not.toBeInTheDocument();
  expect(screen.getByText('/agent fix replace docs/demo.md broken')).toBeInTheDocument();
  expect(await screen.findByText('Task failed')).toBeInTheDocument();
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&query=broken'));
});

test('preserves status filter when searching backend task history', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);

  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'FAILED' }));
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&status=FAILED'));

  await user.type(screen.getByRole('searchbox', { name: 'Search tasks' }), 'broken');

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&query=broken&status=FAILED'));
  expect(screen.getByText('/agent fix replace docs/demo.md broken')).toBeInTheDocument();
});

test('loads the next backend task page with offset pagination', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.fn(async (input: RequestInfo | URL) => {
    const url = input.toString();
    const firstPage = Array.from({ length: 50 }, (_, index) => ({
      ...completedTask,
      id: `page-task-${index + 1}`,
      issueNumber: index + 10,
      triggerComment: `/agent fix page ${index + 1}`,
      pullRequestUrl: null,
      statusCommentUrl: null
    }));
    const nextPageTask = {
      ...failedTask,
      id: 'page-task-51',
      issueNumber: 60,
      triggerComment: '/agent fix page 51'
    };

    if (url === '/api/tasks?limit=50') {
      return jsonResponse(taskPage(firstPage, 50, 0, true, 51));
    }
    if (url === '/api/tasks?limit=50&offset=50') {
      return jsonResponse(taskPage([nextPageTask], 50, 50, false, 51));
    }
    if (url === '/api/tasks/metrics/summary') {
      return jsonResponse({
        totalCount: 51,
        pendingCount: 0,
        runningCount: 0,
        runningTestsCount: 0,
        completedCount: 50,
        failedCount: 1,
        cancelledCount: 0,
        completionRate: 0.98,
        failureRate: 0.02,
        averageCompletionDurationMs: 60000,
        totalModelTokens: 1800,
        averageModelTokensPerCompletedTask: 36,
        testRunCount: 1,
        passedTestRunCount: 1,
        failedTestRunCount: 0,
        testPassRate: 1
      });
    }
    if (url === '/api/tasks/metrics/failure-causes') {
      return jsonResponse([{ cause: 'MAVEN_TESTS', count: 1 }]);
    }
    if (url === '/api/tasks/metrics/model-usage') {
      return jsonResponse(modelUsageSummary);
    }
    if (url === '/api/tasks/metrics/latency') {
      return jsonResponse(latencySummary);
    }
    if (url === '/api/configuration/summary') {
      return jsonResponse(configurationSummary);
    }
    if (url === '/health') {
      return jsonResponse({
        status: 'UP',
        service: 'patchpilot-backend',
        timestamp: '2026-06-21T01:00:00Z'
      });
    }
    if (url === '/api/task-queue/summary') {
      return jsonResponse(queueSummary);
    }
    if (url === '/api/task-queue/items') {
      return jsonResponse(queueItems);
    }
    if (url === '/api/tasks/page-task-1/detail') {
      return jsonResponse({
        summary: {
          ...summary,
          task: firstPage[0],
          timelineEventCount: 0,
          testRunCount: 0,
          toolCallCount: 0,
          modelCallCount: 0,
          totalModelTokens: 0,
          latestTimelineEvent: null,
          latestTestRunExitCode: null,
          latestTestRunDurationMs: null
        },
        timeline: [],
        testRuns: [],
        toolCalls: [],
        modelCalls: []
      });
    }
    if (url === '/api/tasks/page-task-1/summary') {
      return jsonResponse({
        ...summary,
        task: firstPage[0],
        timelineEventCount: 0,
        testRunCount: 0,
        toolCallCount: 0,
        modelCallCount: 0,
        totalModelTokens: 0,
        latestTimelineEvent: null,
        latestTestRunExitCode: null,
        latestTestRunDurationMs: null
      });
    }
    if (
      url === '/api/tasks/page-task-1/timeline' ||
      url === '/api/tasks/page-task-1/test-runs' ||
      url === '/api/tasks/page-task-1/tool-calls' ||
      url === '/api/tasks/page-task-1/model-calls'
    ) {
      return jsonResponse([]);
    }
    return jsonResponse(null, false, 'not found', 404);
  });
  vi.stubGlobal('fetch', fetchMock);

  render(<App />);

  expect(await screen.findByText('/agent fix page 50')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Load more tasks' }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&offset=50'));
  expect(await screen.findByText('/agent fix page 51')).toBeInTheDocument();
});

test('shows empty states for missing task detail records', async () => {
  const user = userEvent.setup();

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'FAILED' }));

  expect(await screen.findByText('No timeline events recorded.')).toBeInTheDocument();
  expect(screen.getByText('No Maven test runs recorded.')).toBeInTheDocument();
  expect(screen.getByText('No tool calls recorded.')).toBeInTheDocument();
  expect(screen.getByText('No model calls recorded.')).toBeInTheDocument();
});

test('cancels active tasks and refreshes dashboard data', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'RUNNING' }));
  expect(await screen.findByText('/agent fix running task')).toBeInTheDocument();

  await user.click(await screen.findByRole('button', { name: 'Cancel task' }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-3/cancel', { method: 'POST' }));
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&status=RUNNING'));
  expect(screen.queryByRole('button', { name: 'Retry task' })).not.toBeInTheDocument();
});

test('retries failed tasks and refreshes dashboard data', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'FAILED' }));
  expect(await screen.findByText('/agent fix replace docs/demo.md broken')).toBeInTheDocument();

  await user.click(await screen.findByRole('button', { name: 'Retry task' }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-2/retry', { method: 'POST' }));
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&status=FAILED'));
  expect(screen.queryByRole('button', { name: 'Cancel task' })).not.toBeInTheDocument();
});

function jsonResponse(data: unknown, success = true, message: string | null = null, status = 200) {
  return Promise.resolve({
    ok: status >= 200 && status < 300,
    status,
    json: async () => ({ success, data, message })
  } as Response);
}

function taskPage(items: unknown[], limit = 50, offset = 0, hasMore = false, total = items.length) {
  return { items, limit, offset, hasMore, total };
}
