import { render, screen, within } from '@testing-library/react';
import type { SupportedLanguageAdapter } from '../../types';
import { SupportedAdaptersPanel } from './SupportedAdaptersPanel';

const adapters: SupportedLanguageAdapter[] = [
  {
    language: 'java',
    buildSystem: 'maven',
    verificationCommand: ['mvn', 'test'],
    detectionSignals: ['pom.xml', 'mvnw'],
    demoFixturePath: 'docs/demo-repositories/java-maven',
    status: 'SUPPORTED'
  },
  {
    language: 'python',
    buildSystem: 'uv',
    verificationCommand: ['uv', 'run', 'pytest'],
    detectionSignals: ['uv.lock', 'pyproject.toml', 'pytest configuration or dependency'],
    demoFixturePath: 'docs/demo-repositories/python-uv',
    status: 'SUPPORTED'
  }
];

test('shows supported adapters with commands and demo fixtures', () => {
  render(<SupportedAdaptersPanel adapters={adapters} error={null} />);

  expect(screen.getByRole('heading', { name: 'Supported adapters' })).toBeInTheDocument();
  expect(screen.getByText('2 supported adapters')).toBeInTheDocument();

  const mavenRow = screen.getByRole('row', { name: /java maven mvn test/i });
  expect(within(mavenRow).getByText('docs/demo-repositories/java-maven')).toBeInTheDocument();
  expect(within(mavenRow).getByText('pom.xml, mvnw')).toBeInTheDocument();

  const uvRow = screen.getByRole('row', { name: /python uv uv run pytest/i });
  expect(within(uvRow).getByText('SUPPORTED')).toBeInTheDocument();
});

test('shows adapter API error guidance without hiding existing data', () => {
  render(<SupportedAdaptersPanel adapters={adapters} error="Backend request failed" />);

  expect(screen.getByText('Adapter API unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('docs/demo-repositories/python-uv')).toBeInTheDocument();
});
