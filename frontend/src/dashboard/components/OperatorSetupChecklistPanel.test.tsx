import { render, screen, within } from '@testing-library/react';
import type {
  BackendHealth,
  ConfigurationSummary,
  DemoReadiness,
  GitHubCredentialReadiness,
  GitHubLivePublishPreflight,
  GitHubPublishPermissionReadiness,
  GitHubPublishReadiness,
  GitHubRepositoryAccessReadiness,
  GitHubWebhookUrlReadiness,
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
  githubWebhookPublicBaseUrlConfigured: true,
  githubWebhookPublicBaseUrl: 'https://demo.trycloudflare.com',
  githubWebhookPayloadUrl: 'https://demo.trycloudflare.com/api/github/webhook',
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

const githubCredentialReadiness: GitHubCredentialReadiness = {
  tokenConfigured: true,
  status: 'READY',
  message: 'GitHub API accepted the configured token.',
  latencyMs: 31,
  checkedAt: '2026-06-25T03:00:00Z',
  operatorAction: 'No action needed.'
};

const githubRepositoryAccessReadiness: GitHubRepositoryAccessReadiness = {
  tokenConfigured: true,
  repositoryConfigured: true,
  repository: 'bingqin2/PatchPilot',
  status: 'READY',
  message: 'GitHub token can read repository bingqin2/PatchPilot.',
  defaultBranch: 'main',
  latencyMs: 42,
  checkedAt: '2026-06-25T04:00:00Z',
  operatorAction: 'No action needed.'
};

const githubPublishReadiness: GitHubPublishReadiness = {
  status: 'READY',
  publishReady: true,
  tokenConfigured: true,
  repositoryConfigured: true,
  repository: 'bingqin2/PatchPilot',
  defaultBranch: 'main',
  summary: 'GitHub publish path is ready for PatchPilot push and Pull Request creation.',
  nextAction: 'Continue with the live /agent fix demo.',
  safePublishCommand: 'git push origin HEAD:<patchpilot-branch>',
  sideEffectContract: 'Read-only readiness probe: this endpoint does not run git push, does not create branches, does not create Pull Requests, does not write issue comments, and does not expose tokens.',
  checks: [
    {
      name: 'GitHub token',
      status: 'READY',
      summary: 'GitHub API accepted the configured token.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Repository access',
      status: 'READY',
      summary: 'GitHub token can read repository bingqin2/PatchPilot.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Publish command safety',
      status: 'READY',
      summary: 'PatchPilot uses a bounded push shape: git push origin HEAD:<patchpilot-branch>.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: ['Token configured: true', 'Default branch: main'],
  checkedAt: '2026-06-30T01:00:00Z'
};

const githubPublishPermissionReadiness: GitHubPublishPermissionReadiness = {
  status: 'READY',
  publishPermissionReady: true,
  tokenConfigured: true,
  repositoryConfigured: true,
  repository: 'bingqin2/PatchPilot',
  defaultBranch: 'main',
  canReadRepository: true,
  canPushBranches: true,
  canCreatePullRequests: true,
  issueFeedbackPermissionLikely: true,
  summary: 'GitHub token has repository publish permissions for PatchPilot push and Pull Request creation.',
  nextAction: 'Continue with the live /agent fix demo.',
  sideEffectContract: 'Read-only permission probe: this endpoint does not run git push, does not create branches, does not create Pull Requests, does not write issue comments, and does not expose tokens.',
  permissionChecks: [
    {
      name: 'Repository read',
      status: 'READY',
      summary: 'Token can read repository metadata.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Branch push',
      status: 'READY',
      summary: 'Token can publish PatchPilot branches.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Pull Request creation',
      status: 'READY',
      summary: 'Token can create Pull Requests for pushed branches.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Issue feedback',
      status: 'READY',
      summary: 'Token is likely able to write task status feedback on issues.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: ['Repository: bingqin2/PatchPilot'],
  latencyMs: 35,
  checkedAt: '2026-06-30T06:00:00Z'
};

const githubLivePublishPreflight: GitHubLivePublishPreflight = {
  status: 'READY',
  livePublishReady: true,
  tokenConfigured: true,
  repositoryConfigured: true,
  repository: 'bingqin2/PatchPilot',
  defaultBranch: 'main',
  patchpilotBranches: [],
  openPatchpilotPullRequests: [],
  summary: 'Live GitHub publish preflight is ready for a clean PatchPilot branch and Pull Request.',
  nextAction: 'Post the live /agent fix comment when the rest of launch readiness is READY.',
  sideEffectContract: 'Read-only live publish preflight: this endpoint does not run git push, does not create branches, does not open Pull Requests, does not write issue comments, and does not expose tokens.',
  checks: [
    {
      name: 'PatchPilot branch inventory',
      status: 'READY',
      summary: 'No existing patchpilot/* branches were found.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Open PatchPilot Pull Requests',
      status: 'READY',
      summary: 'No open PatchPilot Pull Requests were found.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: ['Repository: bingqin2/PatchPilot', 'PatchPilot branch count: 0'],
  latencyMs: 42,
  checkedAt: '2026-06-30T09:00:00Z'
};

const githubWebhookUrlReadiness: GitHubWebhookUrlReadiness = {
  publicBaseUrlConfigured: true,
  status: 'READY',
  publicBaseUrl: 'https://demo.trycloudflare.com',
  payloadUrl: 'https://demo.trycloudflare.com/api/github/webhook',
  healthUrl: 'https://demo.trycloudflare.com/health',
  message: 'Configured public webhook URL reaches PatchPilot health.',
  latencyMs: 44,
  checkedAt: '2026-06-27T01:00:00Z',
  operatorAction: 'Use the payload URL in the GitHub webhook settings.'
};

const demoReadiness: DemoReadiness = {
  status: 'READY',
  summary: 'PatchPilot is ready for a controlled demo.',
  checks: [
    {
      name: 'Demo target policy',
      status: 'READY',
      message: 'Demo repository and recent trigger user align with configured safety allowlists.',
      action: 'No action needed.'
    },
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
  expect(within(panel).getByText('17/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('GitHub credentials')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - GitHub API accepted the configured token.')).toBeInTheDocument();
  expect(within(panel).getByText('Repository access')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - GitHub token can read repository bingqin2/PatchPilot.')).toBeInTheDocument();
  expect(within(panel).getAllByText('GitHub publish readiness')[0]).toBeInTheDocument();
  expect(within(panel).getByText('READY - GitHub publish path is ready for PatchPilot push and Pull Request creation.')).toBeInTheDocument();
  expect(within(panel).getByText('git push origin HEAD:<patchpilot-branch>')).toBeInTheDocument();
  expect(within(panel).getAllByText(/does not run git push/)).toHaveLength(3);
  expect(within(panel).getAllByText('GitHub publish permissions')[0]).toBeInTheDocument();
  expect(within(panel).getByText('Ready - GitHub token has repository publish permissions for PatchPilot push and Pull Request creation.')).toBeInTheDocument();
  expect(within(panel).getByText('READY - GitHub token has repository publish permissions for PatchPilot push and Pull Request creation.')).toBeInTheDocument();
  expect(within(panel).getByText('Push: yes')).toBeInTheDocument();
  expect(within(panel).getByText('PR: yes')).toBeInTheDocument();
  expect(within(panel).getByText('Issue feedback: yes')).toBeInTheDocument();
  expect(within(panel).getByText('Branch push: READY')).toBeInTheDocument();
  expect(within(panel).getByText('Webhook setup')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - Configured public webhook URL reaches PatchPilot health.')).toBeInTheDocument();
  expect(within(panel).getByText('https://demo.trycloudflare.com/api/github/webhook')).toBeInTheDocument();
  expect(within(panel).getByText('Demo target policy')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - Demo repository and recent trigger user align with configured safety allowlists.')).toBeInTheDocument();
  expect(within(panel).getByText('Model provider health')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - Model provider responded to the health probe.')).toBeInTheDocument();
  expect(within(panel).getByText('Repository preflight scope')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - demo fixture preflight paths are allowed')).toBeInTheDocument();
  expect(within(panel).getByText('Adapter runtimes')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - 1/1 runtime executables available')).toBeInTheDocument();
  expect(within(panel).getByText('Worker heartbeat')).toBeInTheDocument();
  expect(within(panel).getByText('Ready - Worker poller is active but no queue item was available.')).toBeInTheDocument();
});

test('uses demo readiness webhook setup result before URL-only readiness', () => {
  renderChecklist(configuration, {
    ...demoReadiness,
    checks: [
      ...demoReadiness.checks,
      {
        name: 'GitHub webhook setup',
        status: 'NEEDS_ATTENTION',
        message: 'Webhook setup needs attention before redelivery.',
        action: 'Fix the latest webhook delivery issue, then use GitHub Redeliver.'
      }
    ]
  });

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('Webhook setup')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - Webhook setup needs attention before redelivery.')).toBeInTheDocument();
  expect(within(panel).getByText('Fix the latest webhook delivery issue, then use GitHub Redeliver.')).toBeInTheDocument();
});

test('shows repository preflight scope setup action when demo fixtures are outside allowed roots', () => {
  renderChecklist({
    ...configuration,
    repositoryPreflightAllowedRootDirs: ['/tmp/patchpilot/workspaces']
  });

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
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
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
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
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
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
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
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
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Model provider health')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - Model provider health probe failed: HTTP 401')).toBeInTheDocument();
  expect(within(panel).getByText('Check PATCHPILOT_AGENT_API_KEY, PATCHPILOT_AGENT_BASE_URL, and PATCHPILOT_AGENT_MODEL.')).toBeInTheDocument();
});

test('shows GitHub credential setup action when token probe is not ready', () => {
  renderChecklist(configuration, demoReadiness, runtimeReadiness, modelProviderHealth, {
    ...githubCredentialReadiness,
    status: 'NEEDS_ATTENTION',
    message: 'GitHub credential probe failed: HTTP 401',
    operatorAction: 'Check PATCHPILOT_GITHUB_TOKEN permissions before running a live task.'
  });

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('GitHub credentials')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - GitHub credential probe failed: HTTP 401')).toBeInTheDocument();
  expect(within(panel).getByText('Check PATCHPILOT_GITHUB_TOKEN permissions before running a live task.')).toBeInTheDocument();
});

test('shows repository access setup action when the selected repository cannot be read', () => {
  renderChecklist(
    configuration,
    demoReadiness,
    runtimeReadiness,
    modelProviderHealth,
    githubCredentialReadiness,
    {
      ...githubRepositoryAccessReadiness,
      status: 'NEEDS_ATTENTION',
      message: 'GitHub repository access probe failed: HTTP 404',
      operatorAction: 'Check PATCHPILOT_GITHUB_TOKEN permissions and repository allowlist for bingqin2/PatchPilot.'
    }
  );

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Repository access')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - GitHub repository access probe failed: HTTP 404')).toBeInTheDocument();
  expect(within(panel).getByText('Check PATCHPILOT_GITHUB_TOKEN permissions and repository allowlist for bingqin2/PatchPilot.')).toBeInTheDocument();
});

test('uses demo readiness repository access result when backend reports the demo target is not readable', () => {
  renderChecklist(
    configuration,
    {
      ...demoReadiness,
      status: 'BLOCKED',
      checks: [
        {
          name: 'GitHub repository access',
          status: 'BLOCKED',
          message: 'GitHub repository access probe failed: HTTP 404',
          action: 'Check PATCHPILOT_GITHUB_TOKEN permissions, GitHub App installation access, and repository allowlist for bingqin2/PatchPilot; then retry the readiness check.'
        },
        ...demoReadiness.checks
      ],
      nextActions: [
        'Check PATCHPILOT_GITHUB_TOKEN permissions, GitHub App installation access, and repository allowlist for bingqin2/PatchPilot; then retry the readiness check.'
      ]
    },
    runtimeReadiness,
    modelProviderHealth,
    githubCredentialReadiness,
    githubRepositoryAccessReadiness
  );

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Repository access')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - GitHub repository access probe failed: HTTP 404')).toBeInTheDocument();
  expect(within(panel).getByText('Check PATCHPILOT_GITHUB_TOKEN permissions, GitHub App installation access, and repository allowlist for bingqin2/PatchPilot; then retry the readiness check.')).toBeInTheDocument();
});

test('uses demo readiness target policy result when allowlists do not match the live demo target', () => {
  renderChecklist(configuration, {
    ...demoReadiness,
    status: 'NEEDS_ATTENTION',
    checks: [
      {
        name: 'Demo target policy',
        status: 'NEEDS_ATTENTION',
        message: 'Demo repository bingqin2/PatchPilot is not in PATCHPILOT_ALLOWED_REPOSITORIES.',
        action: 'Update demo safety allowlists before a live demo: add bingqin2/PatchPilot to PATCHPILOT_ALLOWED_REPOSITORIES.'
      },
      ...demoReadiness.checks
    ],
    nextActions: [
      'Update demo safety allowlists before a live demo: add bingqin2/PatchPilot to PATCHPILOT_ALLOWED_REPOSITORIES.'
    ]
  });

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Demo target policy')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - Demo repository bingqin2/PatchPilot is not in PATCHPILOT_ALLOWED_REPOSITORIES.')).toBeInTheDocument();
  expect(within(panel).getByText('Update demo safety allowlists before a live demo: add bingqin2/PatchPilot to PATCHPILOT_ALLOWED_REPOSITORIES.')).toBeInTheDocument();
});

test('shows webhook URL setup action when the configured public URL is stale', () => {
  renderChecklist(
    configuration,
    demoReadiness,
    runtimeReadiness,
    modelProviderHealth,
    githubCredentialReadiness,
    githubRepositoryAccessReadiness,
    {
      ...githubWebhookUrlReadiness,
      status: 'NEEDS_ATTENTION',
      message: 'HTTP 502 from public URL.',
      operatorAction: 'Restart cloudflared, update PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL, and set the GitHub webhook Payload URL again.'
    }
  );

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getByText('Webhook setup')).toBeInTheDocument();
  expect(within(panel).getByText('Attention - HTTP 502 from public URL.')).toBeInTheDocument();
  expect(within(panel).getByText('Restart cloudflared, update PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL, and set the GitHub webhook Payload URL again.')).toBeInTheDocument();
});

test('shows GitHub publish setup action when push and PR publishing are blocked', () => {
  renderChecklist(
    configuration,
    demoReadiness,
    runtimeReadiness,
    modelProviderHealth,
    githubCredentialReadiness,
    githubRepositoryAccessReadiness,
    githubWebhookUrlReadiness,
    {
      ...githubPublishReadiness,
      status: 'BLOCKED',
      publishReady: false,
      summary: 'GitHub publish path is blocked before PatchPilot can push branches or create Pull Requests.',
      nextAction: 'Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.',
      checks: [
        {
          name: 'GitHub token',
          status: 'BLOCKED',
          summary: 'GitHub token is not configured.',
          nextAction: 'Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.'
        },
        ...githubPublishReadiness.checks.slice(1)
      ]
    }
  );

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getAllByText('GitHub publish readiness')[0]).toBeInTheDocument();
  expect(within(panel).getByText('Attention - GitHub publish path is blocked before PatchPilot can push branches or create Pull Requests.')).toBeInTheDocument();
  expect(within(panel).getByText('BLOCKED - GitHub publish path is blocked before PatchPilot can push branches or create Pull Requests.')).toBeInTheDocument();
  expect(within(panel).getByText('GitHub token: BLOCKED')).toBeInTheDocument();
  expect(within(panel).getAllByText('Configure PATCHPILOT_GITHUB_TOKEN and restart the backend.')).toHaveLength(2);
});

test('shows GitHub publish permission setup action when repository token is read only', () => {
  renderChecklist(
    configuration,
    demoReadiness,
    runtimeReadiness,
    modelProviderHealth,
    githubCredentialReadiness,
    githubRepositoryAccessReadiness,
    githubWebhookUrlReadiness,
    githubPublishReadiness,
    {
      ...githubPublishPermissionReadiness,
      status: 'NEEDS_ATTENTION',
      publishPermissionReady: false,
      canPushBranches: false,
      canCreatePullRequests: false,
      issueFeedbackPermissionLikely: false,
      summary: 'GitHub token can read the repository but does not expose write permissions required for publish.',
      nextAction: 'Grant Contents: Read and write, Pull requests: Read and write, and Issues: Read and write on the demo repository; then restart PatchPilot if the token changed.',
      permissionChecks: [
        githubPublishPermissionReadiness.permissionChecks[0],
        {
          name: 'Branch push',
          status: 'NEEDS_ATTENTION',
          summary: 'Token does not expose branch write permission.',
          nextAction: 'Grant Contents: Read and write for the demo repository.'
        },
        {
          name: 'Pull Request creation',
          status: 'NEEDS_ATTENTION',
          summary: 'Pull Request creation permission was not confirmed.',
          nextAction: 'Grant Pull requests: Read and write for the demo repository.'
        },
        {
          name: 'Issue feedback',
          status: 'NEEDS_ATTENTION',
          summary: 'Issue feedback permission was not confirmed.',
          nextAction: 'Grant Issues: Read and write if PatchPilot should comment on issues.'
        }
      ]
    }
  );

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getByText('16/17 checks ready')).toBeInTheDocument();
  expect(within(panel).getAllByText('GitHub publish permissions')[0]).toBeInTheDocument();
  expect(within(panel).getByText('Attention - GitHub token can read the repository but does not expose write permissions required for publish.')).toBeInTheDocument();
  expect(within(panel).getByText('NEEDS_ATTENTION - GitHub token can read the repository but does not expose write permissions required for publish.')).toBeInTheDocument();
  expect(within(panel).getByText('Push: no')).toBeInTheDocument();
  expect(within(panel).getByText('PR: no')).toBeInTheDocument();
  expect(within(panel).getByText('Issue feedback: no')).toBeInTheDocument();
  expect(within(panel).getByText('Branch push: NEEDS_ATTENTION')).toBeInTheDocument();
  expect(within(panel).getAllByText('Grant Contents: Read and write, Pull requests: Read and write, and Issues: Read and write on the demo repository; then restart PatchPilot if the token changed.')).toHaveLength(2);
});

test('shows live publish preflight stale branch and pull request guidance', () => {
  renderChecklist(
    configuration,
    demoReadiness,
    runtimeReadiness,
    modelProviderHealth,
    githubCredentialReadiness,
    githubRepositoryAccessReadiness,
    githubWebhookUrlReadiness,
    githubPublishReadiness,
    githubPublishPermissionReadiness,
    {
      ...githubLivePublishPreflight,
      status: 'NEEDS_ATTENTION',
      livePublishReady: false,
      patchpilotBranches: ['patchpilot/old-task'],
      openPatchpilotPullRequests: ['https://github.com/bingqin2/PatchPilot/pull/4'],
      summary: 'Live GitHub publish preflight found existing PatchPilot publish artifacts.',
      nextAction: 'Review, close or merge stale PatchPilot Pull Requests, and delete old patchpilot/* branches before the live demo.',
      checks: [
        {
          name: 'PatchPilot branch inventory',
          status: 'NEEDS_ATTENTION',
          summary: 'Found 1 existing patchpilot/* branch(es).',
          nextAction: 'Delete stale patchpilot/* branches or confirm they are intentionally kept before demo launch.'
        },
        {
          name: 'Open PatchPilot Pull Requests',
          status: 'NEEDS_ATTENTION',
          summary: 'Found 1 open PatchPilot Pull Request.',
          nextAction: 'Close, merge, or intentionally keep the existing PatchPilot Pull Request before demo launch.'
        }
      ]
    }
  );

  const panel = screen.getByRole('region', { name: 'Operator setup checklist' });
  expect(within(panel).getAllByText('GitHub live publish preflight')[0]).toBeInTheDocument();
  expect(within(panel).getByText('Attention - Live GitHub publish preflight found existing PatchPilot publish artifacts.')).toBeInTheDocument();
  expect(within(panel).getByText('NEEDS_ATTENTION - Live GitHub publish preflight found existing PatchPilot publish artifacts.')).toBeInTheDocument();
  expect(within(panel).getByText('patchpilot/old-task')).toBeInTheDocument();
  expect(within(panel).getByText('https://github.com/bingqin2/PatchPilot/pull/4')).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot branch inventory: NEEDS_ATTENTION')).toBeInTheDocument();
  expect(within(panel).getByText('Open PatchPilot Pull Requests: NEEDS_ATTENTION')).toBeInTheDocument();
  expect(within(panel).getByText(/does not open Pull Requests/)).toBeInTheDocument();
});

function renderChecklist(
  config: ConfigurationSummary,
  readiness: DemoReadiness = demoReadiness,
  runtimes: LanguageAdapterRuntimeReadiness[] = runtimeReadiness,
  providerHealth: ModelProviderHealth = modelProviderHealth,
  credentialReadiness: GitHubCredentialReadiness = githubCredentialReadiness,
  repositoryAccessReadiness: GitHubRepositoryAccessReadiness = githubRepositoryAccessReadiness,
  webhookUrlReadiness: GitHubWebhookUrlReadiness = githubWebhookUrlReadiness,
  publishReadiness: GitHubPublishReadiness = githubPublishReadiness,
  publishPermissionReadiness: GitHubPublishPermissionReadiness = githubPublishPermissionReadiness,
  livePublishPreflight: GitHubLivePublishPreflight = githubLivePublishPreflight
) {
  render(
    <OperatorSetupChecklistPanel
      backendHealth={backendHealth}
      configuration={config}
      githubCredentialReadiness={credentialReadiness}
      githubLivePublishPreflight={livePublishPreflight}
      githubPublishPermissionReadiness={publishPermissionReadiness}
      githubPublishReadiness={publishReadiness}
      githubRepositoryAccessReadiness={repositoryAccessReadiness}
      githubWebhookUrlReadiness={webhookUrlReadiness}
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
