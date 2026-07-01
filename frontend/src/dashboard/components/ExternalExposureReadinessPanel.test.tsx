import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  ExternalExposureHandoffPackage,
  ExternalExposureReadiness,
  ExternalExposureReadinessArchive
} from '../../types';
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

const handoffPackage: ExternalExposureHandoffPackage = {
  status: 'READY',
  handoffReady: true,
  summary: 'External exposure handoff package is ready to share.',
  nextAction: 'Start the temporary tunnel, share the current payload URL, and monitor webhook deliveries.',
  readinessStatus: 'READY',
  readinessSafeToExpose: true,
  readinessReadyCount: 10,
  readinessNeedsAttentionCount: 0,
  readinessBlockedCount: 0,
  readinessTotalCount: 10,
  latestArchiveId: 'exposure-archive-1',
  latestArchiveStatus: 'READY',
  latestArchiveSafeToExpose: true,
  latestArchiveCreatedAt: '2026-07-01T13:30:00Z',
  archiveFreshness: 'CURRENT',
  nextActions: ['Start the temporary tunnel and keep monitoring.'],
  evidenceNotes: ['Latest archive exposure-archive-1 captures READY readiness evidence.'],
  downloadActions: ['GET /api/security/external-exposure-handoff-package/report/download'],
  sideEffectContract: 'GET /api/security/external-exposure-handoff-package is read-only.',
  generatedAt: '2026-07-01T14:00:00Z',
  markdownReport: '# PatchPilot External Exposure Handoff Package'
};

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
      handoffPackage={handoffPackage}
      handoffPackageError={null}
      onArchiveReadiness={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageReport={vi.fn()}
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
  expect(within(panel).getAllByText('exposure-archive-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('7 ready, 3 attention, 0 blocked')).toBeInTheDocument();
  expect(within(panel).getByText('External exposure handoff package')).toBeInTheDocument();
  expect(within(panel).getByText('External exposure handoff package is ready to share.')).toBeInTheDocument();
  expect(
    within(panel).getAllByText((_, element) =>
      element?.textContent?.includes('Archive freshness: CURRENT') ?? false
    ).length
  ).toBeGreaterThan(0);
  expect(within(panel).getByText('Latest archive exposure-archive-1 captures READY readiness evidence.')).toBeInTheDocument();
});

test('copies, archives, downloads archived markdown, and refreshes the panel', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn();
  const onRefresh = vi.fn();
  const onArchiveReadiness = vi.fn(async () => archives[0]);
  const reportBlob = new Blob(['# PatchPilot External Exposure Readiness Archive'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const handoffReportBlob = new Blob(['# PatchPilot External Exposure Handoff Package'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadArchiveReport = vi.fn(async () => reportBlob);
  const onDownloadHandoffPackageReport = vi.fn(async () => handoffReportBlob);
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
      handoffPackage={handoffPackage}
      handoffPackageError={null}
      onArchiveReadiness={onArchiveReadiness}
      onDownloadArchiveReport={onDownloadArchiveReport}
      onDownloadHandoffPackageReport={onDownloadHandoffPackageReport}
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

  await user.click(screen.getByRole('button', { name: 'Download exposure handoff package' }));
  expect(onDownloadHandoffPackageReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(handoffReportBlob);
  expect(screen.getByText('Exposure handoff package downloaded')).toBeInTheDocument();

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
      handoffPackage={null}
      handoffPackageError="handoff unavailable"
      onArchiveReadiness={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageReport={vi.fn()}
      onRefresh={vi.fn()}
    />
  );

  expect(screen.getByText('External exposure readiness unavailable')).toBeInTheDocument();
  expect(screen.getByText('backend unavailable')).toBeInTheDocument();
  expect(screen.getByText('No external exposure readiness loaded.')).toBeInTheDocument();
  expect(screen.getByText('Exposure readiness archive unavailable')).toBeInTheDocument();
  expect(screen.getByText('archive unavailable')).toBeInTheDocument();
  expect(screen.getByText('External exposure handoff package unavailable')).toBeInTheDocument();
  expect(screen.getByText('handoff unavailable')).toBeInTheDocument();
  expect(screen.getByText('No external exposure handoff package loaded.')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Archive exposure readiness' })).toBeDisabled();
  expect(screen.getByRole('button', { name: 'Refresh exposure gate' })).toBeEnabled();
});
