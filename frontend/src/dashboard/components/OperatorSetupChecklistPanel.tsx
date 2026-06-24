import type {
  BackendHealth,
  ConfigurationSummary,
  DemoReadiness,
  LanguageAdapterFixtureVerification,
  FixTask,
  FixTaskQueueSummary,
  FixTaskWorkerHealth
} from '../../types';

interface OperatorSetupChecklistPanelProps {
  backendHealth: BackendHealth | null;
  configuration: ConfigurationSummary | null;
  demoReadiness: DemoReadiness | null;
  adapterFixtureVerifications: LanguageAdapterFixtureVerification[];
  queueSummary: FixTaskQueueSummary | null;
  workerHealth: FixTaskWorkerHealth | null;
  tasks: FixTask[];
  hasStoredAdminToken: boolean;
}

interface SetupCheck {
  name: string;
  ready: boolean;
  message: string;
  action: string;
}

export function OperatorSetupChecklistPanel({
  backendHealth,
  configuration,
  demoReadiness,
  adapterFixtureVerifications,
  queueSummary,
  workerHealth,
  tasks,
  hasStoredAdminToken
}: OperatorSetupChecklistPanelProps) {
  const checks = setupChecks({
    backendHealth,
    configuration,
    demoReadiness,
    adapterFixtureVerifications,
    queueSummary,
    workerHealth,
    tasks,
    hasStoredAdminToken
  });
  const readyCount = checks.filter((check) => check.ready).length;
  const nextActions = checks.filter((check) => !check.ready).map((check) => check.action);

  return (
    <section className="panel operator-setup-panel" aria-label="Operator setup checklist">
      <div className="panel-header">
        <div>
          <h2>Operator setup checklist</h2>
          <p>{`${readyCount}/${checks.length} checks ready`}</p>
        </div>
      </div>

      <div className="operator-setup-grid">
        {checks.map((check) => (
          <div className={`operator-setup-check operator-setup-check-${check.ready ? 'ready' : 'attention'}`} key={check.name}>
            <span>{check.name}</span>
            <strong>{check.ready ? 'Ready' : 'Attention'} - {check.message}</strong>
          </div>
        ))}
      </div>

      {nextActions.length > 0 ? (
        <div className="operator-setup-actions">
          <h3>Next setup actions</h3>
          <ul>
            {nextActions.map((action) => (
              <li key={action}>{action}</li>
            ))}
          </ul>
        </div>
      ) : (
        <p className="operator-setup-ready">All setup checks are ready for a controlled issue-to-PR demo.</p>
      )}
    </section>
  );
}

function setupChecks({
  backendHealth,
  configuration,
  demoReadiness,
  adapterFixtureVerifications,
  queueSummary,
  workerHealth,
  tasks,
  hasStoredAdminToken
}: OperatorSetupChecklistPanelProps): SetupCheck[] {
  return [
    backendCheck(backendHealth),
    credentialCheck(configuration, hasStoredAdminToken),
    safetyPolicyCheck(configuration),
    repositoryPreflightScopeCheck(configuration, demoReadiness),
    adapterFixtureCheck(adapterFixtureVerifications),
    queueHealthCheck(queueSummary),
    workerHeartbeatCheck(demoReadiness, workerHealth),
    recentPullRequestCheck(demoReadiness, tasks)
  ];
}

function backendCheck(backendHealth: BackendHealth | null): SetupCheck {
  const ready = backendHealth?.status === 'UP';
  return {
    name: 'Backend connectivity',
    ready,
    message: ready ? '/health reports UP' : 'backend health has not loaded',
    action: 'Start the backend and confirm /health returns UP.'
  };
}

function credentialCheck(configuration: ConfigurationSummary | null, hasStoredAdminToken: boolean): SetupCheck {
  const ready = Boolean(
    configuration?.agentApiKeyConfigured &&
      configuration.githubTokenConfigured &&
      configuration.githubWebhookSecretConfigured &&
      (!configuration.adminTokenConfigured || hasStoredAdminToken)
  );
  return {
    name: 'Required credentials',
    ready,
    message: ready
      ? 'agent, GitHub, webhook, and browser admin token are configured'
      : 'missing a required server credential or browser admin token',
    action: 'Configure model, GitHub, webhook, and dashboard admin token values before a demo.'
  };
}

function safetyPolicyCheck(configuration: ConfigurationSummary | null): SetupCheck {
  const ready = Boolean(
    configuration?.triggerUserAllowlistConfigured &&
      configuration.repositoryAllowlistConfigured &&
      configuration.reviewApprovalAllowlistConfigured &&
      configuration.triggerRateLimitEnabled
  );
  return {
    name: 'Safety policy',
    ready,
    message: ready
      ? 'allowlists, review approvers, and trigger rate limits are configured'
      : 'allowlists, review approvers, or trigger rate limits need attention',
    action: 'Configure trigger allowlists, repository allowlists, review approvers, and trigger rate limits.'
  };
}

function repositoryPreflightScopeCheck(configuration: ConfigurationSummary | null, demoReadiness: DemoReadiness | null): SetupCheck {
  const demoReadinessCheck = demoReadiness?.checks.find((check) => check.name === 'Repository preflight scope');
  if (demoReadinessCheck) {
    return {
      name: 'Repository preflight scope',
      ready: demoReadinessCheck.status === 'READY',
      message: demoReadinessCheck.message,
      action: demoReadinessCheck.action
    };
  }

  const allowedRootDirs = configuration?.repositoryPreflightAllowedRootDirs ?? [];
  const ready = allowedRootDirs.some((rootDir) => rootDir.includes('docs/demo-repositories'));
  return {
    name: 'Repository preflight scope',
    ready,
    message: ready ? 'demo fixture preflight paths are allowed' : 'demo fixture preflight path is not allowed',
    action: 'Add docs/demo-repositories or the project root to PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS.'
  };
}

function adapterFixtureCheck(adapterFixtureVerifications: LanguageAdapterFixtureVerification[]): SetupCheck {
  const failedFixtures = adapterFixtureVerifications.filter((verification) => verification.status !== 'PASS').length;
  const ready = adapterFixtureVerifications.length > 0 && failedFixtures === 0;
  return {
    name: 'Adapter fixtures',
    ready,
    message: ready
      ? `${adapterFixtureVerifications.length}/${adapterFixtureVerifications.length} fixtures passing`
      : `${failedFixtures} adapter fixture${failedFixtures === 1 ? '' : 's'} failing`,
    action: 'Fix adapter fixture drift before running a multi-language demo.'
  };
}

function queueHealthCheck(queueSummary: FixTaskQueueSummary | null): SetupCheck {
  const failedCount = queueSummary?.failedCount ?? 0;
  return {
    name: 'Queue health',
    ready: failedCount === 0,
    message: failedCount === 0 ? 'no failed queue items' : `${failedCount} failed queue item${failedCount === 1 ? '' : 's'}`,
    action: 'Clear failed queue items before a live demo.'
  };
}

function workerHeartbeatCheck(demoReadiness: DemoReadiness | null, workerHealth: FixTaskWorkerHealth | null): SetupCheck {
  const demoReadinessCheck = demoReadiness?.checks.find((check) => check.name === 'Worker heartbeat');
  if (demoReadinessCheck) {
    return {
      name: 'Worker heartbeat',
      ready: demoReadinessCheck.status === 'READY',
      message: demoReadinessCheck.message,
      action: demoReadinessCheck.action
    };
  }

  const ready = workerHealth?.readinessStatus === 'READY';
  return {
    name: 'Worker heartbeat',
    ready,
    message: workerHealth?.message ?? 'worker heartbeat has not loaded',
    action: workerHealth?.operatorAction ?? 'Confirm the queue worker endpoint loads before a live demo.'
  };
}

function recentPullRequestCheck(demoReadiness: DemoReadiness | null, tasks: FixTask[]): SetupCheck {
  const demoReadinessCheck = demoReadiness?.checks.find((check) => check.name === 'Recent Pull Request');
  if (demoReadinessCheck) {
    const ready = demoReadinessCheck.status === 'READY';
    return {
      name: 'Recent PR evidence',
      ready,
      message: ready ? 'recent completed task has a Pull Request URL' : 'run one controlled issue-to-PR smoke task',
      action: demoReadinessCheck.action
    };
  }

  const hasCompletedPullRequest = tasks.some((task) => task.status === 'COMPLETED' && Boolean(task.pullRequestUrl));
  return {
    name: 'Recent PR evidence',
    ready: hasCompletedPullRequest,
    message: hasCompletedPullRequest ? 'recent completed task has a Pull Request URL' : 'run one controlled issue-to-PR smoke task',
    action: 'Run one controlled issue-to-PR smoke task before a live demo.'
  };
}
