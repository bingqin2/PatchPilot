import type { BackendHealth, ConfigurationSummary } from '../../types';
import { duration, number } from '../format';

interface ConfigurationPanelProps {
  configuration: ConfigurationSummary | null;
  backendHealth: BackendHealth | null;
}

export function ConfigurationPanel({ configuration, backendHealth }: ConfigurationPanelProps) {
  const health = configurationHealth(configuration);

  return (
    <section className="panel configuration-panel" aria-label="Configuration">
      <div className="panel-header">
        <div>
          <h2>Configuration</h2>
          <p>{configuration?.agentProvider || 'Loading configuration'}</p>
        </div>
      </div>
      <div className={`configuration-health configuration-health-${health.level}`}>
        <strong>{health.title}</strong>
        {health.advisoryTitle ? <span>{health.advisoryTitle}</span> : null}
      </div>
      <div className={`backend-health backend-health-${backendHealth ? 'up' : 'unavailable'}`}>
        <strong>{backendHealth ? `Backend ${backendHealth.status}` : 'Backend unavailable'}</strong>
        <span>{backendHealth?.service ?? 'Check /health, backend process, and Vite proxy target'}</span>
        {backendHealth?.timestamp ? <time dateTime={backendHealth.timestamp}>{backendHealth.timestamp}</time> : null}
      </div>
      <div className="configuration-grid">
        <div>
          <span>Model</span>
          <strong>{configuration?.agentModel || '-'}</strong>
          <p>{configuration?.agentBaseUrl || '-'}</p>
        </div>
        <div>
          <span>Workspace</span>
          <strong>Workspace root</strong>
          <p>{configuration?.workspaceRootDir || '-'}</p>
        </div>
        <div>
          <span>Secrets</span>
          <strong>Agent key {configured(configuration?.agentApiKeyConfigured)}</strong>
          <p>GitHub token {configured(configuration?.githubTokenConfigured)}</p>
          <p>Webhook secret {configured(configuration?.githubWebhookSecretConfigured)}</p>
          <p>Admin API token {configured(configuration?.adminTokenConfigured)}</p>
        </div>
        <div>
          <span>Queue policy</span>
          <strong>Queue attempts {number(configuration?.queueMaxAttempts)}</strong>
          <p>{duration(configuration?.queueRetryDelayMs)} retry delay</p>
          <p>{duration(configuration?.queueVisibilityTimeoutMs)} visibility timeout</p>
          <p>{duration(configuration?.queueWorkerHeartbeatStaleMs)} worker heartbeat stale threshold</p>
        </div>
        <div>
          <span>Cost</span>
          <strong>Model cost {configured(configuration?.modelCostConfigured)}</strong>
          <p>Trigger classifier {enabled(configuration?.modelTriggerClassificationEnabled)}</p>
        </div>
        <div>
          <span>Trigger guard</span>
          <strong>Rate limit {enabled(configuration?.triggerRateLimitEnabled)}</strong>
          <p>{duration(configuration?.triggerRateLimitWindowMs)} window</p>
          <p>
            {number(configuration?.triggerRateLimitMaxPerTriggerUser)} user /{' '}
            {number(configuration?.triggerRateLimitMaxPerRepository)} repo /{' '}
            {number(configuration?.triggerRateLimitMaxPerIssue)} issue
          </p>
        </div>
        <div>
          <span>Rejected-trigger quarantine</span>
          <strong>Rejected-trigger quarantine {enabled(configuration?.rejectedTriggerQuarantineEnabled)}</strong>
          <p>
            {number(configuration?.rejectedTriggerQuarantineThreshold)} rejections /{' '}
            {duration(configuration?.rejectedTriggerQuarantineWindowMs)} window /{' '}
            {duration(configuration?.rejectedTriggerQuarantineCooldownMs)} cooldown
          </p>
        </div>
        <div>
          <span>Review approvers</span>
          <strong>{reviewApproverSummary(configuration)}</strong>
          <p>Risk review approval allowlist</p>
        </div>
        <div>
          <span>Trigger users</span>
          <strong>{allowlistSummary(configuration?.allowedTriggerUsers)}</strong>
          <p>Task creation user allowlist</p>
        </div>
        <div>
          <span>Repositories</span>
          <strong>{allowlistSummary(configuration?.allowedRepositories)}</strong>
          <p>Task creation repository allowlist</p>
        </div>
        <div>
          <span>Generated diff policy</span>
          <strong>Risk gate {enabled(configuration?.generatedDiffRiskGateEnabled)}</strong>
          <p>{number(configuration?.generatedDiffProtectedPathCount)} protected path patterns</p>
        </div>
        <div>
          <span>Repository preflight</span>
          <strong>{allowedRootSummary(configuration?.repositoryPreflightAllowedRootDirs)}</strong>
          <p>{allowlistSummary(configuration?.repositoryPreflightAllowedRootDirs)}</p>
        </div>
      </div>
      {health.items.length > 0 ? (
        <div className="configuration-issues" aria-label="Configuration issues">
          {health.items.map((item) => (
            <p className={`configuration-issue configuration-issue-${item.kind}`} key={item.message}>
              {item.message}
            </p>
          ))}
        </div>
      ) : null}
    </section>
  );
}

function configured(value?: boolean) {
  return value ? 'Configured' : 'Missing';
}

function enabled(value?: boolean) {
  return value ? 'Enabled' : 'Disabled';
}

interface ConfigurationIssue {
  kind: 'critical' | 'advisory';
  message: string;
}

function configurationHealth(configuration: ConfigurationSummary | null) {
  if (!configuration) {
    return { level: 'loading', title: 'Loading configuration', advisoryTitle: '', items: [] };
  }

  const criticalIssues: ConfigurationIssue[] = [];
  const advisoryIssues: ConfigurationIssue[] = [];

  if (!configuration.agentApiKeyConfigured) {
    criticalIssues.push({ kind: 'critical', message: 'Agent API key is missing' });
  }
  if (!configuration.githubTokenConfigured) {
    criticalIssues.push({ kind: 'critical', message: 'GitHub token is missing' });
  }
  if (!configuration.githubWebhookSecretConfigured) {
    criticalIssues.push({ kind: 'critical', message: 'Webhook secret is missing' });
  }
  if (!configuration.adminTokenConfigured) {
    advisoryIssues.push({ kind: 'advisory', message: 'Admin API token is missing' });
  }
  if (!configuration.modelCostConfigured) {
    advisoryIssues.push({ kind: 'advisory', message: 'Model cost is not configured' });
  }
  if (!configuration.triggerRateLimitEnabled) {
    advisoryIssues.push({ kind: 'advisory', message: 'Trigger rate limit is disabled' });
  }
  if (configuration.queueMaxAttempts < 1) {
    advisoryIssues.push({ kind: 'advisory', message: 'Queue attempts must be at least 1' });
  }
  if (configuration.queueRetryDelayMs < 0) {
    advisoryIssues.push({ kind: 'advisory', message: 'Queue retry delay cannot be negative' });
  }
  if (configuration.queueVisibilityTimeoutMs < 1000) {
    advisoryIssues.push({ kind: 'advisory', message: 'Queue visibility timeout is below 1.0s' });
  }
  if (configuration.queueWorkerHeartbeatStaleMs < 1000) {
    advisoryIssues.push({ kind: 'advisory', message: 'Queue worker heartbeat stale threshold is below 1.0s' });
  }
  if (configuration.triggerRateLimitWindowMs < 1000) {
    advisoryIssues.push({ kind: 'advisory', message: 'Trigger rate limit window is below 1.0s' });
  }
  if (
    configuration.triggerRateLimitMaxPerTriggerUser < 1 ||
    configuration.triggerRateLimitMaxPerRepository < 1 ||
    configuration.triggerRateLimitMaxPerIssue < 1
  ) {
    advisoryIssues.push({ kind: 'advisory', message: 'Trigger rate limit thresholds must be at least 1' });
  }
  if (!configuration.rejectedTriggerQuarantineEnabled) {
    advisoryIssues.push({ kind: 'advisory', message: 'Rejected-trigger quarantine is disabled' });
  }
  if (configuration.rejectedTriggerQuarantineWindowMs < 1000) {
    advisoryIssues.push({ kind: 'advisory', message: 'Rejected-trigger quarantine window is below 1.0s' });
  }
  if (configuration.rejectedTriggerQuarantineThreshold < 1 || configuration.rejectedTriggerQuarantineCooldownMs < 1) {
    advisoryIssues.push({ kind: 'advisory', message: 'Rejected-trigger quarantine thresholds must be at least 1' });
  }
  if (!configuration.triggerUserAllowlistConfigured) {
    advisoryIssues.push({ kind: 'advisory', message: 'Trigger user allowlist is open' });
  }
  if (!configuration.repositoryAllowlistConfigured) {
    advisoryIssues.push({ kind: 'advisory', message: 'Repository allowlist is open' });
  }
  if (configuration.reviewApprovalAllowedOperators.length === 0) {
    advisoryIssues.push({ kind: 'advisory', message: 'Review approval operators are not configured' });
  }
  if (!configuration.generatedDiffRiskGateEnabled) {
    advisoryIssues.push({ kind: 'advisory', message: 'Generated diff risk gate is disabled' });
  }
  if (configuration.generatedDiffProtectedPathCount < 1) {
    advisoryIssues.push({ kind: 'advisory', message: 'Generated diff protected path policy is empty' });
  }
  if (configuration.repositoryPreflightAllowedRootDirs.length === 0) {
    advisoryIssues.push({ kind: 'advisory', message: 'Repository preflight allowed roots are not configured' });
  }

  if (criticalIssues.length > 0) {
    return {
      level: 'critical',
      title: issueCount(criticalIssues.length, 'setup issue'),
      advisoryTitle: advisoryIssues.length > 0 ? issueCount(advisoryIssues.length, 'advisory item') : '',
      items: [...criticalIssues, ...advisoryIssues]
    };
  }

  if (advisoryIssues.length > 0) {
    return {
      level: 'advisory',
      title: issueCount(advisoryIssues.length, 'advisory item'),
      advisoryTitle: '',
      items: advisoryIssues
    };
  }

  return { level: 'healthy', title: 'Configuration healthy', advisoryTitle: '', items: [] };
}

function issueCount(count: number, label: string) {
  return `${count} ${label}${count === 1 ? '' : 's'}`;
}

function reviewApproverSummary(configuration: ConfigurationSummary | null) {
  if (!configuration) {
    return '-';
  }
  if (configuration.reviewApprovalAllowedOperators.length === 0) {
    return 'Not configured';
  }
  return configuration.reviewApprovalAllowedOperators.join(', ');
}

function allowlistSummary(values?: string[]) {
  if (!values) {
    return '-';
  }
  if (values.length === 0) {
    return 'Open';
  }
  return values.join(', ');
}

function allowedRootSummary(values?: string[]) {
  if (!values) {
    return '-';
  }
  return `${values.length} allowed root${values.length === 1 ? '' : 's'}`;
}
