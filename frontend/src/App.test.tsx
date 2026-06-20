import { render, screen, waitFor, within } from '@testing-library/react';
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
  statusCommentUrl: null
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

beforeEach(() => {
  vi.stubGlobal('fetch', vi.fn(async (input: RequestInfo | URL) => {
    const url = input.toString();
    if (url === '/api/tasks?limit=50') {
      return jsonResponse([completedTask, failedTask]);
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
    return jsonResponse(null, false, 'not found', 404);
  }));
});

afterEach(() => {
  vi.unstubAllGlobals();
});

test('renders operational task dashboard from backend APIs', async () => {
  render(<App />);

  expect(await screen.findByRole('heading', { name: 'PatchPilot Operations' })).toBeInTheDocument();
  expect(screen.getByText('COMPLETED')).toBeInTheDocument();
  expect(screen.getByText('FAILED')).toBeInTheDocument();
  expect(screen.getByText('maven tests failed')).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'PR #8' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/8'
  );

  expect(screen.getByText('Completion')).toBeInTheDocument();
  expect(screen.getByText('50%')).toBeInTheDocument();
  expect(screen.getByText('Test pass')).toBeInTheDocument();
  expect(screen.getByText('100%')).toBeInTheDocument();

  await waitFor(() => expect(screen.getByText('Task completed')).toBeInTheDocument());
  expect(screen.getByText('Pull request opened')).toBeInTheDocument();
  expect(screen.getByText('Tests run: 247, Failures: 0, Errors: 0')).toBeInTheDocument();
  expect(screen.getByText('replace')).toBeInTheDocument();
  expect(screen.getByText('gpt-5.5')).toBeInTheDocument();
});

test('shows an actionable error when a backend request fails', async () => {
  vi.stubGlobal('fetch', vi.fn(async () => jsonResponse(null, false, 'backend unavailable', 500)));

  render(<App />);

  const alert = await screen.findByRole('alert');
  expect(within(alert).getByText('backend unavailable')).toBeInTheDocument();
});

function jsonResponse(data: unknown, success = true, message: string | null = null, status = 200) {
  return Promise.resolve({
    ok: status >= 200 && status < 300,
    status,
    json: async () => ({ success, data, message })
  } as Response);
}
