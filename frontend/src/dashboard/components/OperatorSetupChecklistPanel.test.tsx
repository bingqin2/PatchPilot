import { render, screen, within } from '@testing-library/react';
import type {
  BackendHealth,
  ConfigurationSummary,
  DemoReadiness,
  ModelProviderHealth,
  FixTask,
  FixTaskWorkerHealth,
  FixTaskQueueSummary,
  LanguageAdapterFixtureVerification,
  LanguageAdapterRuntimeReadiness
} from '../../types';
import { OperatorSetupChecklistPanel } from './OperatorSetupChecklistPanel';

const backendHealth: BackendHealth = {
  status: 'UP',
  service: 'patchpilot-backend',
  timestamp: '2026-06-24T00:00:00Z'
};

const configuration: ConfigurationSummary = {
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
  reviewApprovalAllowedOperators: ['release-captain'],
  repositoryPreflightAllowedRootDirs: ['/tmp/patchpilot/workspaces', '/Users/demo/agent/docs/demo-repositories']
};

const modelProviderHealth: ModelProviderHealth = {
  provider: 'openai-compatible',
  model: 'gpt-5.5',
  baseUrlConfigured: true,
  apiKeyConfigured: true,
  status: 'READY',
  message: 'Model provider responded to the health probe.',
  latencyMs: 43,
  checkedAt: '2026-06-25T02:00:00Z',
  operatorAction: 'No action needed.'
};

const demoReadiness: DemoReadiness = {
  status: 'READY',
  summary: 'PatchPilot is ready for a controlled demo.',
  checks: [
    {
      name: 'Recent Pull Request',
      status: 'READY',
      message: 'Recent task history includes a completed Pull Request.',
      action: 'No action needed.'
    }
  ],
  nextActions: []
};

const fixtures: LanguageAdapterFixtureVerification[] = [
  {
    fixtureName: 'java-maven',
    fixturePath: 'docs/demo-repositories/java-maven',
    expectedLanguage: 'java',
    expectedBuildSystem: 'maven',
    expectedVerificationCommand: ['mvn', 'test'],
    actualLanguage: 'java',
    actualBuildSystem: 'maven',
    actualVerificationCommand: ['mvn', 'test'],
    reason: 'Detected Maven fixture',
    status: 'PASS'
  }
];

const runtimeReadiness: LanguageAdapterRuntimeReadiness[] = [
  {
    language: 'java',
    buildSystem: 'maven',
    executable: 'mvn',
    verificationCommand: ['mvn', 'test'],
    status: 'READY',
    reason: 'Executable `mvn` is available on PATH'
  }
];

const queueSummary: FixTaskQueueSummary = {
  totalCount: 1,
  pendingCount: 0,
  availablePendingCount: 0,
  delayedPendingCount: 0,
  runningCount: 0,
  completedCount: 1,
  failedCount: 0,
  cancelledCount: 0
};

const workerHealth: FixTaskWorkerHealth = {
  state: 'IDLE',
  message: 'Worker poller is active but no queue item was available.',
  startedAt: '2026-06-24T00:00:00Z',
  lastPollAt: '2026-06-24T00:00:01Z',
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
};

const tasks: FixTask[] = [];

test('shows repository preflight scope as ready when demo fixtures are allowed', () => {
  renderChecklist(configuration);

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('10/10 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Model provider health')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - Model provider responded to the health probe.')).toBeInTheDocument();
  expect(within(panel).getByText('Repository preflight scope')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - demo fixture preflight paths are allowed')).toBeInTheDocument();
  expect(within(panel).getByText('Adapter runtimes')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - 1/1 runtime executables available')).toBeInTheDocument();
  expect(within(panel).getByText('Worker heartbeat')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - Worker poller is active but no queue item was available.')).toBeInTheDocument();
});

test('shows repository preflight scope setup action when demo fixtures are outside allowed roots', () => {
  renderChecklist({
    ...configuration,
    repositoryPreflightAllowedRootDirs: ['/tmp/patchpilot/workspaces']
  });

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('9/10 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Repository preflight scope')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - demo fixture preflight path is not allowed')).toBeInTheDocument();
  expect(within(panel).getByText('Add docs/demo-repositories or the project root to PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS.')).toBeInTheDocument();
});

test('uses demo readiness preflight scope result when backend reports a scope warning', () => {
  renderChecklist(configuration, {
    ...demoReadiness,
    status: 'NEEDS_ATTENTION',
    checks: [
      {
        name: 'Repository preflight scope',
        status: 'NEEDS_ATTENTION',
        message: 'Repository preflight allowed roots do not include docs/demo-repositories.',
        action: 'Configure PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS to include docs/demo-repositories or the project root before a live demo.'
      },
      ...demoReadiness.checks
    ],
    nextActions: [
      'Configure PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS to include docs/demo-repositories or the project root before a live demo.'
    ]
  });

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('9/10 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - Repository preflight allowed roots do not include docs/demo-repositories.')).toBeInTheDocument();
  expect(within(panel).getByText('Configure PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS to include docs/demo-repositories or the project root before a live demo.')).toBeInTheDocument();
});

test('uses demo readiness worker heartbeat result when backend reports a stale worker', () => {
  renderChecklist(configuration, {
    ...demoReadiness,
    status: 'NEEDS_ATTENTION',
    checks: [
      {
        name: 'Worker heartbeat',
        status: 'NEEDS_ATTENTION',
        message: 'Worker heartbeat is stale.',
        action: 'Check whether the queue worker scheduler is still running.'
      },
      ...demoReadiness.checks
    ],
    nextActions: ['Check whether the queue worker scheduler is still running.']
  });

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('9/10 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - Worker heartbeat is stale.')).toBeInTheDocument();
  expect(within(panel).getByText('Check whether the queue worker scheduler is still running.')).toBeInTheDocument();
});

test('shows adapter runtime setup action when an executable is missing', () => {
  renderChecklist(configuration, demoReadiness, [
    ...runtimeReadiness,
    {
      language: 'python',
      buildSystem: 'hatch',
      executable: 'python',
      verificationCommand: ['python', '-m', 'pytest'],
      status: 'MISSING',
      reason: 'Executable `python` is not available on PATH'
    }
  ]);

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('9/10 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Adapter runtimes')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - 1 runtime executable missing: python-hatch requires python')).toBeInTheDocument();
  expect(within(panel).getByText('Install missing adapter executables on the backend PATH before demonstrating affected languages.')).toBeInTheDocument();
});

test('shows model provider setup action when the health probe is not ready', () => {
  renderChecklist(configuration, demoReadiness, runtimeReadiness, {
    ...modelProviderHealth,
    status: 'NEEDS_ATTENTION',
    message: 'Model provider health probe failed: HTTP 401',
    operatorAction: 'Check PATCHPILOT_AGENT_API_KEY, PATCHPILOT_AGENT_BASE_URL, and PATCHPILOT_AGENT_MODEL.'
  });

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('9/10 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Model provider health')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - Model provider health probe failed: HTTP 401')).toBeInTheDocument();
  expect(within(panel).getByText('Check PATCHPILOT_AGENT_API_KEY, PATCHPILOT_AGENT_BASE_URL, and PATCHPILOT_AGENT_MODEL.')).toBeInTheDocument();
});

function renderChecklist(
  config: ConfigurationSummary,
  readiness: DemoReadiness = demoReadiness,
  runtimes: LanguageAdapterRuntimeReadiness[] = runtimeReadiness,
  providerHealth: ModelProviderHealth = modelProviderHealth
) {
  render(
    <OperatorSetupChecklistPanel
      backendHealth={backendHealth}
      configuration={config}
      modelProviderHealth={providerHealth}
      demoReadiness={readiness}
      adapterFixtureVerifications={fixtures}
      adapterRuntimeReadiness={runtimes}
      queueSummary={queueSummary}
      workerHealth={workerHealth}
      tasks={tasks}
      hasStoredAdminToken
    />
  );
}
