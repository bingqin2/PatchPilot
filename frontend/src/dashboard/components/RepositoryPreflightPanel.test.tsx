import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { RepositoryPreflightResult } from '../../types';
import { RepositoryPreflightPanel } from './RepositoryPreflightPanel';

const supportedResult: RepositoryPreflightResult = {
  supported: true,
  language: 'java',
  buildSystem: 'maven',
  verificationCommand: ['mvn', 'test'],
  reason: 'Detected Maven project',
  operatorAction: 'Repository is supported. PatchPilot can run the detected verification command after patch generation.',
  repositoryPath: 'docs/demo-repositories/java-maven',
  supportedAdapters: []
};

const unsupportedResult: RepositoryPreflightResult = {
  supported: false,
  language: 'unknown',
  buildSystem: 'unknown',
  verificationCommand: [],
  reason: 'Unsupported repository: no supported language adapter detected',
  operatorAction: 'Add a LanguageAdapter for this repository shape before running /agent fix.',
  repositoryPath: 'docs/demo-repositories/unknown',
  supportedAdapters: [
    {
      language: 'java',
      buildSystem: 'maven',
      verificationCommand: ['mvn', 'test'],
      detectionSignals: ['pom.xml', 'mvnw'],
      demoFixturePath: 'docs/demo-repositories/java-maven',
      status: 'SUPPORTED'
    }
  ]
};

test('runs repository preflight for a local path', async () => {
  const user = userEvent.setup();
  const onRunPreflight = vi.fn();

  render(
    <RepositoryPreflightPanel
      result={null}
      error={null}
      loading={false}
      allowedRootDirs={['/Users/demo/agent/docs/demo-repositories']}
      onRunPreflight={onRunPreflight}
    />
  );

  await user.clear(screen.getByLabelText('Repository path'));
  await user.type(screen.getByLabelText('Repository path'), 'docs/demo-repositories/java-maven');
  await user.click(screen.getByRole('button', { name: 'Run preflight' }));

  expect(onRunPreflight).toHaveBeenCalledWith({ repositoryPath: 'docs/demo-repositories/java-maven' });
});

test('shows supported repository detection details', () => {
  render(
    <RepositoryPreflightPanel
      result={supportedResult}
      error={null}
      loading={false}
      allowedRootDirs={['/Users/demo/agent/docs/demo-repositories']}
      onRunPreflight={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Repository preflight' });
  expect(within(panel).getByText('Allowed roots')).toBeInTheDocument();
  expect(within(panel).getByText('/Users/demo/agent/docs/demo-repositories')).toBeInTheDocument();
  expect(within(panel).getByText('SUPPORTED')).toBeInTheDocument();
  expect(within(panel).getByText('java')).toBeInTheDocument();
  expect(within(panel).getByText('maven')).toBeInTheDocument();
  expect(within(panel).getByText('mvn test')).toBeInTheDocument();
  expect(within(panel).getByText('Detected Maven project')).toBeInTheDocument();
});

test('shows unsupported guidance and adapter options', () => {
  render(
    <RepositoryPreflightPanel
      result={unsupportedResult}
      error="Backend request failed"
      loading={false}
      allowedRootDirs={[]}
      onRunPreflight={vi.fn()}
    />
  );

  expect(screen.getByText('No repository preflight allowed roots configured')).toBeInTheDocument();
  expect(screen.getByText('Preflight failed')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('UNSUPPORTED')).toBeInTheDocument();
  expect(screen.getByText('Add a LanguageAdapter for this repository shape before running /agent fix.')).toBeInTheDocument();
  expect(screen.getByText('java / maven')).toBeInTheDocument();
  expect(screen.getByText('pom.xml, mvnw')).toBeInTheDocument();
});
