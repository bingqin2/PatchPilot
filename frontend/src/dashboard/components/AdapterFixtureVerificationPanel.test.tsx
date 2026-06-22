import { render, screen, within } from '@testing-library/react';
import type { LanguageAdapterFixtureVerification } from '../../types';
import { AdapterFixtureVerificationPanel } from './AdapterFixtureVerificationPanel';

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
    fixtureName: 'python-hatch',
    fixturePath: 'docs/demo-repositories/python-hatch',
    expectedLanguage: 'python',
    expectedBuildSystem: 'hatch',
    expectedVerificationCommand: ['hatch', 'test'],
    actualLanguage: 'python',
    actualBuildSystem: 'hatch',
    actualVerificationCommand: ['hatch', 'test'],
    reason: 'Detected Hatch test script',
    status: 'PASS'
  },
  {
    fixtureName: 'missing',
    fixturePath: 'docs/demo-repositories/missing',
    expectedLanguage: 'python',
    expectedBuildSystem: 'pytest',
    expectedVerificationCommand: ['python3', '-m', 'pytest'],
    actualLanguage: 'unknown',
    actualBuildSystem: 'unknown',
    actualVerificationCommand: [],
    reason: 'Unsupported repository: missing fixture path docs/demo-repositories/missing',
    status: 'FAIL'
  }
];

test('shows adapter fixture verification pass and fail rows', () => {
  render(<AdapterFixtureVerificationPanel verifications={verifications} error={null} />);

  expect(screen.getByRole('heading', { name: 'Fixture verification' })).toBeInTheDocument();
  expect(screen.getByText('2/3 fixtures passing')).toBeInTheDocument();

  const hatchRow = screen.getByRole('row', { name: /python-hatch python hatch python hatch pass/i });
  expect(within(hatchRow).getByText('docs/demo-repositories/python-hatch')).toBeInTheDocument();
  expect(within(hatchRow).getByText('expected: hatch test')).toBeInTheDocument();
  expect(within(hatchRow).getByText('actual: hatch test')).toBeInTheDocument();
  expect(within(hatchRow).getByText('Detected Hatch test script')).toBeInTheDocument();

  const missingRow = screen.getByRole('row', { name: /missing python pytest unknown unknown fail/i });
  expect(within(missingRow).getByText('actual: none')).toBeInTheDocument();
  expect(within(missingRow).getByText('Unsupported repository: missing fixture path docs/demo-repositories/missing')).toBeInTheDocument();
});

test('shows fixture verification API errors without hiding previous data', () => {
  render(<AdapterFixtureVerificationPanel verifications={verifications} error="Backend request failed" />);

  expect(screen.getByText('Fixture verification unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('docs/demo-repositories/python-hatch')).toBeInTheDocument();
});
