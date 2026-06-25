import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { LanguageAdapterFixtureVerification, LanguageAdapterRuntimeReadiness, SupportedLanguageAdapter } from '../../types';
import { AdapterReadinessReportPanel } from './AdapterReadinessReportPanel';

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
    language: 'node',
    buildSystem: 'npm',
    verificationCommand: ['npm', 'test'],
    detectionSignals: ['package.json', 'scripts.test'],
    demoFixturePath: 'docs/demo-repositories/node-npm',
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

const verifications: LanguageAdapterFixtureVerification[] = [
  {
    fixtureName: 'java-maven',
    fixturePath: 'docs/demo-repositories/java-maven',
    expectedLanguage: 'java',
    expectedBuildSystem: 'maven',
    expectedVerificationCommand: ['mvn', 'test'],
    actualLanguage: 'java',
    actualBuildSystem: 'maven',
    actualVerificationCommand: ['mvn', 'test'],
    reason: 'Detected Maven project',
    status: 'PASS'
  },
  {
    fixtureName: 'node-npm',
    fixturePath: 'docs/demo-repositories/node-npm',
    expectedLanguage: 'node',
    expectedBuildSystem: 'npm',
    expectedVerificationCommand: ['npm', 'test'],
    actualLanguage: 'node',
    actualBuildSystem: 'npm',
    actualVerificationCommand: ['npm', 'test'],
    reason: 'Detected npm test script',
    status: 'PASS'
  },
  {
    fixtureName: 'python-pytest',
    fixturePath: 'docs/demo-repositories/python-pytest',
    expectedLanguage: 'python',
    expectedBuildSystem: 'pytest',
    expectedVerificationCommand: ['python3', '-m', 'pytest'],
    actualLanguage: 'unknown',
    actualBuildSystem: 'unknown',
    actualVerificationCommand: [],
    reason: 'Unsupported repository: missing pytest fixture',
    status: 'FAIL'
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
  },
  {
    language: 'node',
    buildSystem: 'npm',
    executable: 'npm',
    verificationCommand: ['npm', 'test'],
    status: 'MISSING',
    reason: 'Executable `npm` is not available on PATH'
  },
  {
    language: 'python',
    buildSystem: 'pytest',
    executable: 'python3',
    verificationCommand: ['python3', '-m', 'pytest'],
    status: 'READY',
    reason: 'Executable `python3` is available on PATH'
  }
];

test('summarizes adapter readiness and failing fixtures', () => {
  render(
    <AdapterReadinessReportPanel
      adapters={adapters}
      verifications={verifications}
      runtimeReadiness={runtimeReadiness}
      error={null}
    />
  );

  const panel = screen.getByRole('region', { name: 'Adapter readiness report' });
  expect(within(panel).getByText('Attention - 2/3 fixtures passing, 2/3 runtimes ready')).toBeInTheDocument();
  expect(within(panel).getByText('3 adapters across 3 languages')).toBeInTheDocument();
  expect(within(panel).getByText('java, node, python')).toBeInTheDocument();
  expect(within(panel).getByText('2/3 runtimes ready')).toBeInTheDocument();
  expect(within(panel).getByText('1 runtime executable missing.')).toBeInTheDocument();
  expect(within(panel).getByText('Allowlisted verification commands')).toBeInTheDocument();
  expect(within(panel).getAllByText('java/maven')).toHaveLength(2);
  expect(within(panel).getByText('mvn test')).toBeInTheDocument();
  expect(within(panel).getByText('Runtime executable readiness')).toBeInTheDocument();
  expect(within(panel).getAllByText('node/npm')).toHaveLength(2);
  expect(within(panel).getByText('MISSING')).toBeInTheDocument();
  expect(within(panel).getByText('Executable `npm` is not available on PATH')).toBeInTheDocument();
  expect(within(panel).getByText('python-pytest')).toBeInTheDocument();
  expect(within(panel).getByText('Unsupported repository: missing pytest fixture')).toBeInTheDocument();
});

test('copies adapter readiness report markdown', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });
  render(
    <AdapterReadinessReportPanel
      adapters={adapters}
      verifications={verifications}
      runtimeReadiness={runtimeReadiness}
      error={null}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Copy adapter readiness report' }));

  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('# PatchPilot Adapter Readiness Report'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Status: `NEEDS_ATTENTION`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- Runtime readiness: 2/3 ready'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- `java/maven`: `mvn test`'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- `node/npm`: `npm` -> `MISSING`; Executable `npm` is not available on PATH'));
  expect(writeText).toHaveBeenCalledWith(
    expect.stringContaining('- `python-pytest`: expected `python/pytest`, actual `unknown/unknown`')
  );
});

test('shows adapter readiness API errors without hiding stale data', () => {
  render(
    <AdapterReadinessReportPanel
      adapters={adapters}
      verifications={verifications}
      runtimeReadiness={runtimeReadiness}
      error="Fixture verification unavailable"
    />
  );

  expect(screen.getByText('Adapter readiness incomplete')).toBeInTheDocument();
  expect(screen.getByText('Fixture verification unavailable')).toBeInTheDocument();
  expect(screen.getByText('3 adapters across 3 languages')).toBeInTheDocument();
});
