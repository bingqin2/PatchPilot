import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { ExternalExposureReadiness } from '../../types';
import { ExternalExposureReadinessPanel } from './ExternalExposureReadinessPanel';

const readiness: ExternalExposureReadiness = {
  status: 'BLOCKED',
  safeToExpose: false,
  readyCount: 6,
  needsAttentionCount: 2,
  blockedCount: 2,
  totalCount: 10,
  summary: 'PatchPilot is blocked from safe public exposure.',
  nextActions: [
    'Configure PATCHPILOT_ADMIN_TOKEN before exposing PatchPilot outside localhost.',
    'Set PATCHPILOT_DASHBOARD_ADMIN_TOKEN_BOOTSTRAP_ENABLED=false before using a public tunnel.'
  ],
  sideEffectContract:
    'GET /api/security/external-exposure-readiness is read-only: it does not create tasks, call the model, mutate GitHub, or expose secrets.',
  checks: [
    {
      name: 'Admin API token',
      status: 'BLOCKED',
      summary: 'Admin API token is missing.',
      nextAction: 'Configure PATCHPILOT_ADMIN_TOKEN before exposing PatchPilot outside localhost.'
    },
    {
      name: 'Trigger rate limit',
      status: 'READY',
      summary: 'Trigger rate limiting is enabled.',
      nextAction: 'No action needed.'
    }
  ],
  generatedAt: '2026-07-01T12:00:00Z',
  markdownReport: '# PatchPilot External Exposure Readiness'
};

afterEach(() => {
  vi.unstubAllGlobals();
});

test('renders exposure readiness status, counts, checks, and next actions', () => {
  render(<ExternalExposureReadinessPanel readiness={readiness} error={null} onRefresh={vi.fn()} />);

  const panel = screen.getByLabelText('External exposure readiness');
  expect(within(panel).getByRole('heading', { name: 'External exposure readiness' })).toBeInTheDocument();
  expect(within(panel).getByText('Not safe to expose')).toBeInTheDocument();
  expect(within(panel).getByText('6 ready')).toBeInTheDocument();
  expect(within(panel).getByText('2 warning')).toBeInTheDocument();
  expect(within(panel).getByText('2 blocked')).toBeInTheDocument();
  expect(within(panel).getByText('Admin API token')).toBeInTheDocument();
  expect(within(panel).getByText('Trigger rate limit')).toBeInTheDocument();
  expect(within(panel).getAllByText('Configure PATCHPILOT_ADMIN_TOKEN before exposing PatchPilot outside localhost.'))
    .toHaveLength(2);
});

test('copies the markdown readiness report and refreshes the panel', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn();
  const onRefresh = vi.fn();
  vi.stubGlobal('navigator', {
    clipboard: { writeText }
  });

  render(<ExternalExposureReadinessPanel readiness={readiness} error={null} onRefresh={onRefresh} />);

  await user.click(screen.getByRole('button', { name: 'Copy exposure report' }));
  expect(writeText).toHaveBeenCalledWith('# PatchPilot External Exposure Readiness');
  expect(screen.getByText('Exposure report copied')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Refresh exposure gate' }));
  expect(onRefresh).toHaveBeenCalledTimes(1);
});

test('shows empty and error states without hiding refresh', () => {
  render(
    <ExternalExposureReadinessPanel
      readiness={null}
      error="backend unavailable"
      onRefresh={vi.fn()}
    />
  );

  expect(screen.getByText('External exposure readiness unavailable')).toBeInTheDocument();
  expect(screen.getByText('backend unavailable')).toBeInTheDocument();
  expect(screen.getByText('No external exposure readiness loaded.')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Refresh exposure gate' })).toBeEnabled();
});
