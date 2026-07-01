import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { ExternalExposureReadiness, ExternalExposureReadinessArchive } from '../../types';
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

const archives: ExternalExposureReadinessArchive[] = [
  {
    id: 'exposure-archive-1',
    status: 'NEEDS_ATTENTION',
    safeToExpose: false,
    summary: 'PatchPilot needs more safeguards before public exposure.',
    readyCount: 7,
    needsAttentionCount: 3,
    blockedCount: 0,
    totalCount: 10,
    createdAt: '2026-07-01T13:30:00Z',
    report: '# PatchPilot External Exposure Readiness Archive'
  }
];

afterEach(() => {
  vi.restoreAllMocks();
  vi.unstubAllGlobals();
});

test('renders exposure readiness status, counts, checks, and next actions', () => {
  render(
    <ExternalExposureReadinessPanel
      readiness={readiness}
      error={null}
      archives={archives}
      archiveError={null}
      onArchiveReadiness={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onRefresh={vi.fn()}
    />
  );

  const panel = screen.getByLabelText('External exposure readiness');
  expect(within(panel).getByRole('heading', { name: 'External exposure readiness' })).toBeInTheDocument();
  expect(within(panel).getAllByText('Not safe to expose').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('6 ready')).toBeInTheDocument();
  expect(within(panel).getByText('2 warning')).toBeInTheDocument();
  expect(within(panel).getByText('2 blocked')).toBeInTheDocument();
  expect(within(panel).getByText('Admin API token')).toBeInTheDocument();
  expect(within(panel).getByText('Trigger rate limit')).toBeInTheDocument();
  expect(within(panel).getAllByText('Configure PATCHPILOT_ADMIN_TOKEN before exposing PatchPilot outside localhost.'))
    .toHaveLength(2);
  expect(within(panel).getByText('Recent exposure readiness archives')).toBeInTheDocument();
  expect(within(panel).getByText('exposure-archive-1')).toBeInTheDocument();
  expect(within(panel).getByText('7 ready, 3 attention, 0 blocked')).toBeInTheDocument();
});

test('copies, archives, downloads archived markdown, and refreshes the panel', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn();
  const onRefresh = vi.fn();
  const onArchiveReadiness = vi.fn(async () => archives[0]);
  const reportBlob = new Blob(['# PatchPilot External Exposure Readiness Archive'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadArchiveReport = vi.fn(async () => reportBlob);
  vi.stubGlobal('navigator', {
    clipboard: { writeText }
  });
  const objectUrl = 'blob:external-exposure-readiness';
  const createObjectURL = vi.fn(() => objectUrl);
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);

  render(
    <ExternalExposureReadinessPanel
      readiness={readiness}
      error={null}
      archives={archives}
      archiveError={null}
      onArchiveReadiness={onArchiveReadiness}
      onDownloadArchiveReport={onDownloadArchiveReport}
      onRefresh={onRefresh}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Copy exposure report' }));
  expect(writeText).toHaveBeenCalledWith('# PatchPilot External Exposure Readiness');
  expect(screen.getByText('Exposure report copied')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Archive exposure readiness' }));
  expect(onArchiveReadiness).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Exposure readiness archived')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Download exposure readiness archive exposure-archive-1' }));
  expect(onDownloadArchiveReport).toHaveBeenCalledWith('exposure-archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(revokeObjectURL).toHaveBeenCalledWith(objectUrl);
  expect(screen.getByText('Exposure readiness archive downloaded')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Refresh exposure gate' }));
  expect(onRefresh).toHaveBeenCalledTimes(1);
});

test('shows empty and error states without hiding refresh', () => {
  render(
    <ExternalExposureReadinessPanel
      readiness={null}
      error="backend unavailable"
      archives={[]}
      archiveError="archive unavailable"
      onArchiveReadiness={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onRefresh={vi.fn()}
    />
  );

  expect(screen.getByText('External exposure readiness unavailable')).toBeInTheDocument();
  expect(screen.getByText('backend unavailable')).toBeInTheDocument();
  expect(screen.getByText('No external exposure readiness loaded.')).toBeInTheDocument();
  expect(screen.getByText('Exposure readiness archive unavailable')).toBeInTheDocument();
  expect(screen.getByText('archive unavailable')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Archive exposure readiness' })).toBeDisabled();
  expect(screen.getByRole('button', { name: 'Refresh exposure gate' })).toBeEnabled();
});
