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
  language: 'java',
  buildSystem: 'maven',
  verificationCommand: './mvnw test',
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
  language: 'node',
  buildSystem: 'npm',
  verificationCommand: 'npm test',
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
  language: null,
  buildSystem: null,
  verificationCommand: null,
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

const manuallyCreatedTask = {
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

const detail = {
  summary,
  queueItem: {
    id: 'queue-1',
    taskId: 'task-1',
    status: 'COMPLETED',
    attemptCount: 1,
    lastError: null,
    availableAt: '2026-06-20T01:00:00Z',
    lockedAt: '2026-06-20T01:00:20Z',
    createdAt: '2026-06-20T01:00:00Z',
    updatedAt: '2026-06-20T01:01:00Z'
  },
  queueItems: [
    {
      id: 'queue-1',
      taskId: 'task-1',
      status: 'COMPLETED',
      attemptCount: 1,
      lastError: null,
      availableAt: '2026-06-20T01:00:00Z',
      lockedAt: '2026-06-20T01:00:20Z',
      createdAt: '2026-06-20T01:00:00Z',
      updatedAt: '2026-06-20T01:01:00Z'
    }
  ],
  timeline,
  testRuns,
  toolCalls,
  modelCalls
};

const manualTaskDetail = {
  summary: {
    ...summary,
    task: manuallyCreatedTask,
    timelineEventCount: 1,
    testRunCount: 0,
    toolCallCount: 0,
    modelCallCount: 0,
    totalModelTokens: 0,
    latestTimelineEvent: {
      id: 'timeline-manual',
      taskId: 'manual-task-1',
      eventType: 'TASK_CREATED',
      message: 'Task accepted from dashboard manual creation',
      createdAt: '2026-06-21T10:00:00Z'
    },
    latestTestRunExitCode: null,
    latestTestRunDurationMs: null
  },
  queueItem: null,
  queueItems: [],
  timeline: [
    {
      id: 'timeline-manual',
      taskId: 'manual-task-1',
      eventType: 'TASK_CREATED',
      message: 'Task accepted from dashboard manual creation',
      createdAt: '2026-06-21T10:00:00Z'
    }
  ],
  testRuns: [],
  toolCalls: [],
  modelCalls: []
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

const supportedLanguageAdapters = [
  {
    language: 'java',
    buildSystem: 'maven',
    verificationCommand: ['mvn', 'test'],
    detectionSignals: ['pom.xml', 'mvnw'],
    demoFixturePath: 'docs/demo-repositories/java-maven',
    status: 'SUPPORTED'
  },
  {
    language: 'java',
    buildSystem: 'gradle',
    verificationCommand: ['gradle', 'test'],
    detectionSignals: ['build.gradle', 'build.gradle.kts', 'gradlew'],
    demoFixturePath: 'docs/demo-repositories/java-gradle',
    status: 'SUPPORTED'
  },
  {
    language: 'node',
    buildSystem: 'bun',
    verificationCommand: ['bun', 'test'],
    detectionSignals: ['package.json', 'bun.lockb', 'bun.lock', 'scripts.test'],
    demoFixturePath: 'docs/demo-repositories/node-bun',
    status: 'SUPPORTED'
  },
  {
    language: 'node',
    buildSystem: 'pnpm',
    verificationCommand: ['pnpm', 'test'],
    detectionSignals: ['package.json', 'pnpm-lock.yaml', 'scripts.test'],
    demoFixturePath: 'docs/demo-repositories/node-pnpm',
    status: 'SUPPORTED'
  },
  {
    language: 'node',
    buildSystem: 'yarn',
    verificationCommand: ['yarn', 'test'],
    detectionSignals: ['package.json', 'yarn.lock', 'scripts.test'],
    demoFixturePath: 'docs/demo-repositories/node-yarn',
    status: 'SUPPORTED'
  },
  {
    language: 'node',
    buildSystem: 'npm',
    verificationCommand: ['npm', 'test'],
    detectionSignals: ['package.json', 'scripts.test'],
    demoFixturePath: 'docs/demo-repositories/node-npm',
    status: 'SUPPORTED'
  },
  {
    language: 'python',
    buildSystem: 'tox',
    verificationCommand: ['tox'],
    detectionSignals: ['tox.ini', 'pyproject.toml', '[tool.tox]'],
    demoFixturePath: 'docs/demo-repositories/python-tox',
    status: 'SUPPORTED'
  },
  {
    language: 'python',
    buildSystem: 'nox',
    verificationCommand: ['nox'],
    detectionSignals: ['noxfile.py'],
    demoFixturePath: 'docs/demo-repositories/python-nox',
    status: 'SUPPORTED'
  },
  {
    language: 'python',
    buildSystem: 'hatch',
    verificationCommand: ['hatch', 'test'],
    detectionSignals: ['pyproject.toml', 'Hatch test script'],
    demoFixturePath: 'docs/demo-repositories/python-hatch',
    status: 'SUPPORTED'
  },
  {
    language: 'python',
    buildSystem: 'poetry',
    verificationCommand: ['poetry', 'run', 'pytest'],
    detectionSignals: ['pyproject.toml', '[tool.poetry]', 'pytest configuration or dependency'],
    demoFixturePath: 'docs/demo-repositories/python-poetry',
    status: 'SUPPORTED'
  },
  {
    language: 'python',
    buildSystem: 'uv',
    verificationCommand: ['uv', 'run', 'pytest'],
    detectionSignals: ['uv.lock', 'pyproject.toml', 'pytest configuration or dependency'],
    demoFixturePath: 'docs/demo-repositories/python-uv',
    status: 'SUPPORTED'
  },
  {
    language: 'python',
    buildSystem: 'pytest',
    verificationCommand: ['python3', '-m', 'pytest'],
    detectionSignals: ['pytest.ini', 'requirements.txt', 'pyproject.toml'],
    demoFixturePath: 'docs/demo-repositories/python-pytest',
    status: 'SUPPORTED'
  }
];

const adapterFixtureVerifications = supportedLanguageAdapters.map((adapter) => ({
  fixtureName: fixtureName(adapter.demoFixturePath),
  fixturePath: adapter.demoFixturePath,
  expectedLanguage: adapter.language,
  expectedBuildSystem: adapter.buildSystem,
  expectedVerificationCommand: adapter.verificationCommand,
  actualLanguage: adapter.language,
  actualBuildSystem: adapter.buildSystem,
  actualVerificationCommand: adapter.verificationCommand,
  reason: `Detected ${adapter.buildSystem} fixture`,
  status: 'PASS'
}));

function fixtureName(fixturePath: string) {
  const segments = fixturePath.split('/');
  return segments[segments.length - 1] ?? fixturePath;
}

const statusCounts = {
  totalCount: 2,
  pendingCount: 0,
  runningCount: 0,
  runningTestsCount: 0,
  completedCount: 1,
  failedCount: 1,
  cancelledCount: 0
};

const narrowedStatusCounts = {
  totalCount: 1,
  pendingCount: 0,
  runningCount: 0,
  runningTestsCount: 0,
  completedCount: 0,
  failedCount: 1,
  cancelledCount: 0
};

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
  modelCostConfigured: true,
  modelTriggerClassificationEnabled: true,
  triggerRateLimitEnabled: true,
  triggerRateLimitWindowMs: 600000,
  triggerRateLimitMaxPerTriggerUser: 30,
  triggerRateLimitMaxPerRepository: 60,
  triggerRateLimitMaxPerIssue: 20
};

beforeEach(() => {
  let manualTaskCreated = false;
  vi.stubGlobal('fetch', vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
    const url = input.toString();
    if (url === '/api/tasks/status-counts') {
      return jsonResponse(manualTaskCreated ? { ...statusCounts, totalCount: 3, pendingCount: 1 } : statusCounts);
    }
    if (url === '/api/tasks/status-counts?query=broken') {
      return jsonResponse(narrowedStatusCounts);
    }
    if (
      url === '/api/tasks/status-counts?repositoryOwner=bingqin2&repositoryName=PatchPilot' ||
      url === '/api/tasks/status-counts?createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z' ||
      url === '/api/tasks/status-counts?query=broken&repositoryOwner=bingqin2&repositoryName=PatchPilot' ||
      url === '/api/tasks/status-counts?query=broken&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z'
    ) {
      return jsonResponse(narrowedStatusCounts);
    }
    if (url.startsWith('/api/tasks/status-counts?')) {
      const searchParams = new URLSearchParams(url.slice('/api/tasks/status-counts?'.length));
      if (searchParams.has('language') || searchParams.has('buildSystem')) {
        return jsonResponse(narrowedStatusCounts);
      }
    }
    if (url === '/api/tasks?limit=50') {
      return jsonResponse(taskPage(manualTaskCreated ? [manuallyCreatedTask, completedTask, failedTask] : [completedTask, failedTask]));
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
    if (url === '/api/tasks?limit=50&sort=createdAtAsc') {
      return jsonResponse(taskPage([failedTask, completedTask]));
    }
    if (url === '/api/tasks?limit=50&query=broken&sort=createdAtAsc&status=FAILED') {
      return jsonResponse(taskPage([failedTask]));
    }
    if (url === '/api/tasks?limit=50&status=CANCELLED') {
      return jsonResponse(taskPage([]));
    }
    if (url.startsWith('/api/tasks?')) {
      const searchParams = new URLSearchParams(url.slice('/api/tasks?'.length));
      if (
        searchParams.has('repositoryOwner') ||
        searchParams.has('repositoryName') ||
        searchParams.has('language') ||
        searchParams.has('buildSystem') ||
        searchParams.has('createdAfter') ||
        searchParams.has('createdBefore')
      ) {
        const narrowedToFailed = searchParams.get('query') === 'broken' || searchParams.get('status') === 'FAILED';
        return jsonResponse(taskPage(narrowedToFailed ? [failedTask] : [completedTask, failedTask]));
      }
    }
    if (url === '/api/tasks' && init?.method === 'POST') {
      manualTaskCreated = true;
      return jsonResponse(manuallyCreatedTask, true, null, 201);
    }
    if (url === '/api/tasks/metrics/summary' || url.startsWith('/api/tasks/metrics/summary?')) {
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
    if (url === '/api/tasks/metrics/failure-causes' || url.startsWith('/api/tasks/metrics/failure-causes?')) {
      return jsonResponse([
        { cause: 'MAVEN_TESTS', count: 1 },
        { cause: 'GITHUB_AUTH', count: 1 }
      ]);
    }
    if (url === '/api/tasks/metrics/model-usage' || url.startsWith('/api/tasks/metrics/model-usage?')) {
      return jsonResponse(modelUsageSummary);
    }
    if (url === '/api/tasks/metrics/latency' || url.startsWith('/api/tasks/metrics/latency?')) {
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
    if (url === '/api/language-adapters') {
      return jsonResponse(supportedLanguageAdapters);
    }
    if (url === '/api/language-adapters/fixtures') {
      return jsonResponse(adapterFixtureVerifications);
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
    if (url === '/api/tasks/manual-task-1/detail') {
      return jsonResponse(manualTaskDetail);
    }
    if (url === '/api/tasks/task-1/report') {
      return jsonResponse('# PatchPilot Task Report\n\n- Task: `task-1`');
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
        queueItem: null,
        queueItems: [],
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
        queueItem: null,
        queueItems: [],
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
  expect(screen.getByRole('heading', { name: 'Supported adapters' })).toBeInTheDocument();
  expect(screen.getByText('12 supported adapters')).toBeInTheDocument();
  expect(screen.getByRole('heading', { name: 'Fixture verification' })).toBeInTheDocument();
  expect(screen.getByText('12/12 fixtures passing')).toBeInTheDocument();
  expect(screen.getByRole('row', { name: /java maven mvn test/i })).toBeInTheDocument();
  expect(screen.getByRole('row', { name: /node bun bun test/i })).toBeInTheDocument();
  expect(screen.getByRole('row', { name: /python tox tox/i })).toBeInTheDocument();
  expect(screen.getByRole('row', { name: /python-hatch python hatch python hatch pass/i })).toBeInTheDocument();
  expect(screen.getByRole('row', { name: /python-uv python uv python uv pass/i })).toBeInTheDocument();
  expect(screen.getByText('Queue')).toBeInTheDocument();
  expect(screen.getByText('Queue has failures')).toBeInTheDocument();
  expect(screen.getByText('1 failed item')).toBeInTheDocument();
  expect(screen.getByText('1 running item')).toBeInTheDocument();
  expect(screen.getByText('1 delayed')).toBeInTheDocument();
  expect(screen.getByText('maven test command timed out')).toBeInTheDocument();

  await waitFor(() => expect(screen.getByText('Task completed')).toBeInTheDocument());
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/language-adapters'));
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/language-adapters/fixtures'));
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

test('copies selected task report from backend API', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'Copy report' }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks/task-1/report'));
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Task Report\n\n- Task: `task-1`');
  expect(screen.getByText('Task report copied')).toBeInTheDocument();
});

test('creates a manual task from the dashboard and refreshes task data', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);

  render(<App />);

  await user.type(await screen.findByLabelText('Repository owner'), 'bingqin2');
  await user.type(screen.getByLabelText('Repository name'), 'PatchPilot');
  await user.type(screen.getByLabelText('Issue number'), '7');
  await user.clear(screen.getByLabelText('Trigger user'));
  await user.type(screen.getByLabelText('Trigger user'), 'local-operator');
  await user.type(screen.getByLabelText('Command'), '/agent fix touch docs/manual-task.md');
  await user.click(screen.getByRole('button', { name: 'Create task' }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 7,
      triggerUser: 'local-operator',
      triggerComment: '/agent fix touch docs/manual-task.md'
    })
  }));
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50'));
  expect(await screen.findByText('Manual task queued')).toBeInTheDocument();
  expect(screen.getByLabelText('Command')).toHaveValue('');
});

test('shows manual task creation failures without clearing the form', async () => {
  const user = userEvent.setup();
  vi.stubGlobal('fetch', vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
    const url = input.toString();
    if (url === '/api/tasks' && init?.method === 'POST') {
      return jsonResponse(null, false, 'An active task already exists for this issue', 409);
    }
    if (url === '/api/tasks?limit=50') {
      return jsonResponse(taskPage([completedTask, failedTask]));
    }
    if (url === '/api/tasks/metrics/summary' || url.startsWith('/api/tasks/metrics/summary?')) {
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
    if (url === '/api/tasks/metrics/failure-causes' || url.startsWith('/api/tasks/metrics/failure-causes?')) {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/metrics/model-usage' || url.startsWith('/api/tasks/metrics/model-usage?')) {
      return jsonResponse(modelUsageSummary);
    }
    if (url === '/api/tasks/metrics/latency' || url.startsWith('/api/tasks/metrics/latency?')) {
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
    if (url === '/api/language-adapters') {
      return jsonResponse(supportedLanguageAdapters);
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
    return jsonResponse(null, false, 'not found', 404);
  }));

  render(<App />);

  await user.type(await screen.findByLabelText('Repository owner'), 'bingqin2');
  await user.type(screen.getByLabelText('Repository name'), 'PatchPilot');
  await user.type(screen.getByLabelText('Issue number'), '7');
  await user.type(screen.getByLabelText('Command'), '/agent fix touch docs/manual-task.md');
  await user.click(screen.getByRole('button', { name: 'Create task' }));

  expect(await screen.findByRole('alert')).toHaveTextContent('An active task already exists for this issue');
  expect(screen.getByLabelText('Command')).toHaveValue('/agent fix touch docs/manual-task.md');
});

test('selects task detail from taskId URL parameter', async () => {
  window.history.replaceState(null, '', '/?taskId=task-2');

  render(<App />);

  await waitFor(() => expect(screen.getByText('Task failed')).toBeInTheDocument());
  expect(screen.getByRole('heading', { name: 'bingqin2/PatchPilot #2' })).toBeInTheDocument();
  expect(within(screen.getByRole('heading', { name: 'bingqin2/PatchPilot #2' }).closest('section')!).getByText('task-2')).toBeInTheDocument();
  expect(screen.getByText('Latest test PASS')).toBeInTheDocument();
});

test('selects task detail from task detail route', async () => {
  window.history.replaceState(null, '', '/tasks/task-2');

  render(<App />);

  await waitFor(() => expect(screen.getByText('Task failed')).toBeInTheDocument());
  expect(screen.getByRole('heading', { name: 'bingqin2/PatchPilot #2' })).toBeInTheDocument();
  expect(within(screen.getByRole('heading', { name: 'bingqin2/PatchPilot #2' }).closest('section')!).getByText('task-2')).toBeInTheDocument();
});

test('restores filter URL state on initial dashboard load', async () => {
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/?status=FAILED&query=broken');

  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md broken')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'FAILED' })).toHaveAttribute('aria-pressed', 'true');
  expect(screen.getByRole('searchbox', { name: 'Search tasks' })).toHaveValue('broken');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&query=broken&status=FAILED'));
});

test('restores repository filter URL state on initial dashboard load', async () => {
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/?repositoryOwner=bingqin2&repositoryName=PatchPilot');

  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();
  expect(screen.getByRole('textbox', { name: 'Filter repository owner' })).toHaveValue('bingqin2');
  expect(screen.getByRole('textbox', { name: 'Filter repository name' })).toHaveValue('PatchPilot');
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&repositoryOwner=bingqin2&repositoryName=PatchPilot')
  );
});

test('restores adapter filter URL state on initial dashboard load', async () => {
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/?language=node&buildSystem=npm');

  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md broken')).toBeInTheDocument();
  expect(screen.getByRole('textbox', { name: 'Filter language' })).toHaveValue('node');
  expect(screen.getByRole('textbox', { name: 'Filter build system' })).toHaveValue('npm');
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&language=node&buildSystem=npm')
  );
});

test('restores created time filter URL state on initial dashboard load', async () => {
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/?createdAfter=2026-06-20T01:00:00Z&createdBefore=2026-06-21T01:00:00Z');

  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();
  expect(screen.getByRole('textbox', { name: 'Filter created after' })).toHaveValue('2026-06-20T01:00:00Z');
  expect(screen.getByRole('textbox', { name: 'Filter created before' })).toHaveValue('2026-06-21T01:00:00Z');
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/tasks?limit=50&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z'
    )
  );
});

test('shows task status filter counts from backend status count API', async () => {
  const fetchMock = vi.mocked(fetch);

  render(<App />);

  expect(await screen.findByRole('button', { name: 'ALL' })).toHaveAttribute('aria-pressed', 'true');
  expect(within(screen.getByRole('button', { name: 'ALL' })).getByText('2')).toBeInTheDocument();
  expect(within(screen.getByRole('button', { name: 'COMPLETED' })).getByText('1')).toBeInTheDocument();
  expect(within(screen.getByRole('button', { name: 'FAILED' })).getByText('1')).toBeInTheDocument();
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks/status-counts'));
});

test('loads status filter counts for the current search repository and time scope without status', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-1?status=FAILED&repositoryOwner=bingqin2#timeline');

  render(<App />);

  await user.type(await screen.findByRole('searchbox', { name: 'Search tasks' }), 'broken');
  await user.type(screen.getByRole('textbox', { name: 'Filter repository name' }), 'PatchPilot');
  await user.type(screen.getByRole('textbox', { name: 'Filter created after' }), '2026-06-20T01:00:00Z');
  await user.type(screen.getByRole('textbox', { name: 'Filter created before' }), '2026-06-21T01:00:00Z');

  expect(within(await screen.findByRole('button', { name: 'ALL' })).getByText('1')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'FAILED' })).toHaveAttribute('aria-pressed', 'true');
  expect(within(screen.getByRole('button', { name: 'FAILED' })).getByText('1')).toBeInTheDocument();
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/tasks/status-counts?query=broken&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z'
    )
  );
  expect(fetchMock).not.toHaveBeenCalledWith(
    '/api/tasks/status-counts?query=broken&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z&status=FAILED'
  );
});

test('loads status filter counts for the current adapter scope without status', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-1?status=FAILED&query=broken#timeline');

  render(<App />);

  await user.type(await screen.findByRole('textbox', { name: 'Filter language' }), 'node');
  await user.type(screen.getByRole('textbox', { name: 'Filter build system' }), 'npm');

  expect(within(await screen.findByRole('button', { name: 'ALL' })).getByText('1')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'FAILED' })).toHaveAttribute('aria-pressed', 'true');
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/tasks/status-counts?query=broken&language=node&buildSystem=npm'
    )
  );
  expect(fetchMock).not.toHaveBeenCalledWith(
    '/api/tasks/status-counts?query=broken&language=node&buildSystem=npm&status=FAILED'
  );
});

test('restores task detail route with filter URL state', async () => {
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-2?status=FAILED&query=broken');

  render(<App />);

  await waitFor(() => expect(screen.getByText('Task failed')).toBeInTheDocument());
  expect(screen.getByRole('heading', { name: 'bingqin2/PatchPilot #2' })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'FAILED' })).toHaveAttribute('aria-pressed', 'true');
  expect(screen.getByRole('searchbox', { name: 'Search tasks' })).toHaveValue('broken');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&query=broken&status=FAILED'));
});

test('updates selected task route when selecting a task', async () => {
  const user = userEvent.setup();
  window.history.replaceState(null, '', '/?status=FAILED&taskId=task-1');

  render(<App />);

  await user.click(await screen.findByRole('button', { name: /FAILED bingqin2\/PatchPilot #2/ }));

  expect(window.location.pathname).toBe('/tasks/task-2');
  expect(window.location.search).toBe('?status=FAILED');
});

test('syncs repository filter changes into the URL and backend request', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-1?status=FAILED&query=broken&sort=createdAtAsc#timeline');

  render(<App />);

  await user.type(await screen.findByRole('textbox', { name: 'Filter repository owner' }), 'bingqin2');
  await user.type(screen.getByRole('textbox', { name: 'Filter repository name' }), 'PatchPilot');

  expect(window.location.pathname).toBe('/tasks/task-1');
  expect(window.location.search).toBe(
    '?status=FAILED&query=broken&sort=createdAtAsc&repositoryOwner=bingqin2&repositoryName=PatchPilot'
  );
  expect(window.location.hash).toBe('#timeline');
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/tasks?limit=50&query=broken&repositoryOwner=bingqin2&repositoryName=PatchPilot&sort=createdAtAsc&status=FAILED'
    )
  );
});

test('syncs adapter filter changes into the URL and backend request', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-1?status=FAILED&query=broken&sort=createdAtAsc#timeline');

  render(<App />);

  await user.type(await screen.findByRole('textbox', { name: 'Filter language' }), 'node');
  await user.type(screen.getByRole('textbox', { name: 'Filter build system' }), 'npm');

  expect(window.location.pathname).toBe('/tasks/task-1');
  expect(window.location.search).toBe(
    '?status=FAILED&query=broken&sort=createdAtAsc&language=node&buildSystem=npm'
  );
  expect(window.location.hash).toBe('#timeline');
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/tasks?limit=50&query=broken&language=node&buildSystem=npm&sort=createdAtAsc&status=FAILED'
    )
  );
});

test('syncs created time filter changes into the URL and backend request', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-1?status=FAILED&query=broken&repositoryOwner=bingqin2&repositoryName=PatchPilot&sort=createdAtAsc#timeline');

  render(<App />);

  await user.type(await screen.findByRole('textbox', { name: 'Filter created after' }), '2026-06-20T01:00:00Z');
  await user.type(screen.getByRole('textbox', { name: 'Filter created before' }), '2026-06-21T01:00:00Z');

  expect(window.location.pathname).toBe('/tasks/task-1');
  expect(window.location.search).toBe(
    '?status=FAILED&query=broken&repositoryOwner=bingqin2&repositoryName=PatchPilot&sort=createdAtAsc&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z'
  );
  expect(window.location.hash).toBe('#timeline');
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/tasks?limit=50&query=broken&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z&sort=createdAtAsc&status=FAILED'
    )
  );
});

test('syncs status filter changes into the URL', async () => {
  const user = userEvent.setup();

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'FAILED' }));

  expect(window.location.search).toBe('?status=FAILED');

  await user.click(screen.getByRole('button', { name: 'ALL' }));

  expect(window.location.search).toBe('');
});

test('syncs search query changes into the URL and removes cleared search', async () => {
  const user = userEvent.setup();
  window.history.replaceState(null, '', '/tasks/task-1?status=FAILED');

  render(<App />);

  await user.type(await screen.findByRole('searchbox', { name: 'Search tasks' }), 'broken');

  expect(window.location.pathname).toBe('/tasks/task-1');
  expect(window.location.search).toBe('?status=FAILED&query=broken');

  await user.clear(screen.getByRole('searchbox', { name: 'Search tasks' }));

  expect(window.location.search).toBe('?status=FAILED');
});

test('hides clear filters when no task filters are active', async () => {
  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();
  expect(screen.queryByRole('button', { name: 'Clear filters' })).not.toBeInTheDocument();
});

test('clear filters resets active task filters and preserves the selected task route', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-2?status=FAILED&query=broken&panel=detail#timeline');

  render(<App />);

  expect(await screen.findByText('/agent fix replace docs/demo.md broken')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Clear filters' }));

  expect(screen.getByRole('button', { name: 'ALL' })).toHaveAttribute('aria-pressed', 'true');
  expect(screen.getByRole('searchbox', { name: 'Search tasks' })).toHaveValue('');
  expect(window.location.pathname).toBe('/tasks/task-2');
  expect(window.location.search).toBe('?panel=detail');
  expect(window.location.hash).toBe('#timeline');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50'));
});

test('restores task sort URL state on initial dashboard load', async () => {
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/?sort=createdAtAsc');

  render(<App />);

  expect(await screen.findByRole('combobox', { name: 'Sort tasks' })).toHaveValue('createdAtAsc');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&sort=createdAtAsc'));
});

test('ignores invalid task sort URL state on initial dashboard load', async () => {
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/?sort=updatedAtDesc');

  render(<App />);

  expect(await screen.findByRole('combobox', { name: 'Sort tasks' })).toHaveValue('createdAtDesc');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50'));
});

test('syncs task sort changes into the URL and backend request', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-1?status=FAILED&query=broken#timeline');

  render(<App />);

  await user.selectOptions(await screen.findByRole('combobox', { name: 'Sort tasks' }), 'createdAtAsc');

  expect(window.location.pathname).toBe('/tasks/task-1');
  expect(window.location.search).toBe('?status=FAILED&query=broken&sort=createdAtAsc');
  expect(window.location.hash).toBe('#timeline');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&query=broken&sort=createdAtAsc&status=FAILED'));

  await user.selectOptions(screen.getByRole('combobox', { name: 'Sort tasks' }), 'createdAtDesc');

  expect(window.location.search).toBe('?status=FAILED&query=broken');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&query=broken&status=FAILED'));
});

test('clear filters preserves active task sort state', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(null, '', '/tasks/task-2?status=FAILED&query=broken&sort=createdAtAsc');

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'Clear filters' }));

  expect(screen.getByRole('combobox', { name: 'Sort tasks' })).toHaveValue('createdAtAsc');
  expect(window.location.search).toBe('?sort=createdAtAsc');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&sort=createdAtAsc'));
});

test('clear filters resets repository filters and preserves active sort state', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(
    null,
    '',
    '/tasks/task-2?status=FAILED&query=broken&sort=createdAtAsc&repositoryOwner=bingqin2&repositoryName=PatchPilot'
  );

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'Clear filters' }));

  expect(screen.getByRole('textbox', { name: 'Filter repository owner' })).toHaveValue('');
  expect(screen.getByRole('textbox', { name: 'Filter repository name' })).toHaveValue('');
  expect(screen.getByRole('combobox', { name: 'Sort tasks' })).toHaveValue('createdAtAsc');
  expect(window.location.search).toBe('?sort=createdAtAsc');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&sort=createdAtAsc'));
});

test('clear filters resets adapter filters and preserves active sort state', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(
    null,
    '',
    '/tasks/task-2?status=FAILED&query=broken&sort=createdAtAsc&language=node&buildSystem=npm'
  );

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'Clear filters' }));

  expect(screen.getByRole('textbox', { name: 'Filter language' })).toHaveValue('');
  expect(screen.getByRole('textbox', { name: 'Filter build system' })).toHaveValue('');
  expect(screen.getByRole('combobox', { name: 'Sort tasks' })).toHaveValue('createdAtAsc');
  expect(window.location.search).toBe('?sort=createdAtAsc');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&sort=createdAtAsc'));
});

test('clear filters resets created time filters and preserves active sort state', async () => {
  const user = userEvent.setup();
  const fetchMock = vi.mocked(fetch);
  window.history.replaceState(
    null,
    '',
    '/tasks/task-2?status=FAILED&query=broken&sort=createdAtAsc&createdAfter=2026-06-20T01:00:00Z&createdBefore=2026-06-21T01:00:00Z'
  );

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'Clear filters' }));

  expect(screen.getByRole('textbox', { name: 'Filter created after' })).toHaveValue('');
  expect(screen.getByRole('textbox', { name: 'Filter created before' })).toHaveValue('');
  expect(screen.getByRole('combobox', { name: 'Sort tasks' })).toHaveValue('createdAtAsc');
  expect(window.location.search).toBe('?sort=createdAtAsc');
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith('/api/tasks?limit=50&sort=createdAtAsc'));
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

test('shows selected language adapter metadata in task rows and detail', async () => {
  render(<App />);

  const completedTaskRow = await screen.findByRole('button', { name: /COMPLETED bingqin2\/PatchPilot #1/ });
  expect(within(completedTaskRow).getByText('java / maven')).toBeInTheDocument();
  expect(within(completedTaskRow).getByText('./mvnw test')).toBeInTheDocument();

  await userEvent.click(completedTaskRow);

  expect(screen.getByText('Adapter java / maven')).toBeInTheDocument();
  expect(screen.getByText('Verify ./mvnw test')).toBeInTheDocument();
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
    if (url === '/api/tasks/status-counts') {
      return jsonResponse({
        totalCount: 0,
        pendingCount: 0,
        runningCount: 0,
        runningTestsCount: 0,
        completedCount: 0,
        failedCount: 0,
        cancelledCount: 0
      });
    }
    if (url === '/api/tasks/metrics/summary' || url.startsWith('/api/tasks/metrics/summary?')) {
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
    if (url === '/api/tasks/metrics/failure-causes' || url.startsWith('/api/tasks/metrics/failure-causes?')) {
      return jsonResponse([]);
    }
    if (url === '/api/tasks/metrics/model-usage' || url.startsWith('/api/tasks/metrics/model-usage?')) {
      return jsonResponse(modelUsageSummary);
    }
    if (url === '/api/tasks/metrics/latency' || url.startsWith('/api/tasks/metrics/latency?')) {
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
    if (url === '/api/language-adapters') {
      return jsonResponse(supportedLanguageAdapters);
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
  window.history.replaceState(
    null,
    '',
    '/?repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01:00:00Z&createdBefore=2026-06-21T01:00:00Z&sort=createdAtAsc'
  );
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

    if (
      url ===
      '/api/tasks?limit=50&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z&sort=createdAtAsc'
    ) {
      return jsonResponse(taskPage(firstPage, 50, 0, true, 51));
    }
    if (
      url ===
      '/api/tasks/status-counts?repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z'
    ) {
      return jsonResponse({
        totalCount: 51,
        pendingCount: 0,
        runningCount: 0,
        runningTestsCount: 0,
        completedCount: 50,
        failedCount: 1,
        cancelledCount: 0
      });
    }
    if (
      url ===
      '/api/tasks?limit=50&offset=50&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z&sort=createdAtAsc'
    ) {
      return jsonResponse(taskPage([nextPageTask], 50, 50, false, 51));
    }
    if (url === '/api/tasks/metrics/summary' || url.startsWith('/api/tasks/metrics/summary?')) {
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
    if (url === '/api/tasks/metrics/failure-causes' || url.startsWith('/api/tasks/metrics/failure-causes?')) {
      return jsonResponse([{ cause: 'MAVEN_TESTS', count: 1 }]);
    }
    if (url === '/api/tasks/metrics/model-usage' || url.startsWith('/api/tasks/metrics/model-usage?')) {
      return jsonResponse(modelUsageSummary);
    }
    if (url === '/api/tasks/metrics/latency' || url.startsWith('/api/tasks/metrics/latency?')) {
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
        queueItem: null,
        queueItems: [],
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

  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/tasks?limit=50&offset=50&repositoryOwner=bingqin2&repositoryName=PatchPilot&createdAfter=2026-06-20T01%3A00%3A00Z&createdBefore=2026-06-21T01%3A00%3A00Z&sort=createdAtAsc'
    )
  );
  expect(await screen.findByText('/agent fix page 51')).toBeInTheDocument();
});

test('shows empty states for missing task detail records', async () => {
  const user = userEvent.setup();

  render(<App />);

  await user.click(await screen.findByRole('button', { name: 'FAILED' }));

  expect(await screen.findByText('No timeline events recorded.')).toBeInTheDocument();
  expect(screen.getByText('No verification runs recorded.')).toBeInTheDocument();
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
