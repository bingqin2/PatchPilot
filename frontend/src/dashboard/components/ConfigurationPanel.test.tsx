import { render, screen } from '@testing-library/react';
import { ConfigurationPanel } from './ConfigurationPanel';

const healthyConfiguration = {
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

test('shows healthy configuration status when required and advisory settings are valid', () => {
  render(
    <ConfigurationPanel
      configuration={healthyConfiguration}
      backendHealth={{
        status: 'UP',
        service: 'patchpilot-backend',
        timestamp: '2026-06-21T01:00:00Z'
      }}
    />
  );

  expect(screen.getByText('Configuration healthy')).toBeInTheDocument();
  expect(screen.getByText('Backend UP')).toBeInTheDocument();
  expect(screen.getByText('patchpilot-backend')).toBeInTheDocument();
  expect(screen.queryByText('Agent API key is missing')).not.toBeInTheDocument();
  expect(screen.queryByText('Model cost is not configured')).not.toBeInTheDocument();
});

test('shows setup issues and advisories for weak configuration', () => {
  render(
    <ConfigurationPanel
      configuration={{
        ...healthyConfiguration,
        agentApiKeyConfigured: false,
        githubTokenConfigured: false,
        githubWebhookSecretConfigured: false,
        queueMaxAttempts: 0,
        queueRetryDelayMs: -1,
        queueVisibilityTimeoutMs: 500,
        modelCostConfigured: false
      }}
      backendHealth={null}
    />
  );

  expect(screen.getByText('3 setup issues')).toBeInTheDocument();
  expect(screen.getByText('Backend unavailable')).toBeInTheDocument();
  expect(screen.getByText('4 advisory items')).toBeInTheDocument();
  expect(screen.getByText('Agent API key is missing')).toBeInTheDocument();
  expect(screen.getByText('GitHub token is missing')).toBeInTheDocument();
  expect(screen.getByText('Webhook secret is missing')).toBeInTheDocument();
  expect(screen.getByText('Model cost is not configured')).toBeInTheDocument();
  expect(screen.getByText('Queue attempts must be at least 1')).toBeInTheDocument();
  expect(screen.getByText('Queue retry delay cannot be negative')).toBeInTheDocument();
  expect(screen.getByText('Queue visibility timeout is below 1.0s')).toBeInTheDocument();
});

test('shows advisory status when required secrets are configured but optional settings are missing', () => {
  render(
    <ConfigurationPanel
      configuration={{
        ...healthyConfiguration,
        modelCostConfigured: false
      }}
      backendHealth={{
        status: 'UP',
        service: 'patchpilot-backend',
        timestamp: '2026-06-21T01:00:00Z'
      }}
    />
  );

  expect(screen.queryByText('setup issues')).not.toBeInTheDocument();
  expect(screen.getByText('1 advisory item')).toBeInTheDocument();
  expect(screen.getByText('Model cost is not configured')).toBeInTheDocument();
});
