import type { ConfigurationSummary } from '../../types';
import { duration, number } from '../format';

interface ConfigurationPanelProps {
  configuration: ConfigurationSummary | null;
}

export function ConfigurationPanel({ configuration }: ConfigurationPanelProps) {
  return (
    <section className="panel configuration-panel" aria-label="Configuration">
      <div className="panel-header">
        <div>
          <h2>Configuration</h2>
          <p>{configuration?.agentProvider || 'Loading configuration'}</p>
        </div>
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
        </div>
        <div>
          <span>Queue policy</span>
          <strong>Queue attempts {number(configuration?.queueMaxAttempts)}</strong>
          <p>{duration(configuration?.queueRetryDelayMs)} retry delay</p>
          <p>{duration(configuration?.queueVisibilityTimeoutMs)} visibility timeout</p>
        </div>
        <div>
          <span>Cost</span>
          <strong>Model cost {configured(configuration?.modelCostConfigured)}</strong>
        </div>
      </div>
    </section>
  );
}

function configured(value?: boolean) {
  return value ? 'Configured' : 'Missing';
}
