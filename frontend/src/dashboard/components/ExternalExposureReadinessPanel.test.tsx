import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  ExternalExposureCloseout,
  ExternalExposureCloseoutArchive,
  ExternalExposureHandoffPackage,
  ExternalExposureReadiness,
  ExternalExposureReadinessArchive,
  ExternalExposureSession
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

const sessions: ExternalExposureSession[] = [
  {
    id: 'exposure-session-1',
    status: 'ACTIVE',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    purpose: 'Live GitHub webhook smoke test',
    operator: 'bingqin2',
    expectedShutdownAt: '2026-07-01T17:00:00Z',
    notes: 'Keep terminal visible during test.',
    linkedHandoffStatus: 'READY',
    linkedReadinessArchiveId: 'exposure-archive-1',
    startedAt: '2026-07-01T15:00:00Z',
    closedBy: null,
    closedAt: null,
    closeNotes: null,
    markdownReport: '# PatchPilot External Exposure Session'
  }
];

const closeout: ExternalExposureCloseout = {
  status: 'READY',
  closeoutReady: true,
  summary: 'External exposure session is closed with complete local evidence.',
  nextAction: 'Keep the closeout report with the demo evidence bundle.',
  latestSessionId: 'exposure-session-1',
  latestSessionStatus: 'CLOSED',
  publicUrl: 'https://demo.trycloudflare.com',
  webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
  purpose: 'Live GitHub webhook smoke test',
  operator: 'bingqin2',
  startedAt: '2026-07-01T15:00:00Z',
  closedBy: 'bingqin2',
  closedAt: '2026-07-01T16:30:00Z',
  closeNotes: 'Tunnel process stopped.',
  linkedReadinessArchiveId: 'exposure-archive-1',
  handoffStatus: 'READY',
  archiveFreshness: 'CURRENT',
  readyCount: 4,
  needsAttentionCount: 0,
  blockedCount: 0,
  totalCount: 4,
  nextActions: ['Keep the closeout report with the demo evidence bundle.'],
  evidenceNotes: ['Latest session exposure-session-1 is CLOSED.'],
  downloadActions: ['GET /api/security/external-exposure-closeout/report/download'],
  sideEffectContract: 'GET /api/security/external-exposure-closeout is read-only.',
  generatedAt: '2026-07-01T18:00:00Z',
  markdownReport: '# PatchPilot External Exposure Closeout'
};

const closeoutArchives: ExternalExposureCloseoutArchive[] = [
  {
    id: 'closeout-archive-1',
    status: 'READY',
    closeoutReady: true,
    summary: 'External exposure closeout archive is ready.',
    nextAction: 'Keep archived closeout evidence with the demo bundle.',
    latestSessionId: 'exposure-session-1',
    latestSessionStatus: 'CLOSED',
    publicUrl: 'https://demo.trycloudflare.com',
    webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    purpose: 'Live GitHub webhook smoke test',
    operator: 'bingqin2',
    startedAt: '2026-07-01T15:00:00Z',
    closedBy: 'bingqin2',
    closedAt: '2026-07-01T16:30:00Z',
    closeNotes: 'Tunnel process stopped.',
    linkedReadinessArchiveId: 'exposure-archive-1',
    handoffStatus: 'READY',
    archiveFreshness: 'CURRENT',
    readyCount: 4,
    needsAttentionCount: 0,
    blockedCount: 0,
    totalCount: 4,
    nextActions: ['Keep archived closeout evidence with the demo bundle.'],
    evidenceNotes: ['Latest session exposure-session-1 is CLOSED.'],
    downloadActions: ['GET /api/security/external-exposure-closeout/report/download'],
    sideEffectContract: 'GET /api/security/external-exposure-closeout is read-only.',
    generatedAt: '2026-07-01T18:00:00Z',
    archivedAt: '2026-07-01T18:05:00Z',
    report: '# PatchPilot External Exposure Closeout'
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
      handoffPackage={handoffPackage}
      handoffPackageError={null}
      sessions={sessions}
      sessionError={null}
      closeout={closeout}
      closeoutError={null}
      closeoutArchives={closeoutArchives}
      closeoutArchiveError={null}
      onArchiveReadiness={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageReport={vi.fn()}
      onArchiveCloseout={vi.fn()}
      onDownloadCloseoutArchiveReport={vi.fn()}
      onStartSession={vi.fn()}
      onCloseSession={vi.fn()}
      onDownloadSessionReport={vi.fn()}
      onDownloadCloseoutReport={vi.fn()}
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
  expect(within(panel).getByText('External exposure sessions')).toBeInTheDocument();
  expect(within(panel).getByText('https://demo.trycloudflare.com')).toBeInTheDocument();
  expect(within(panel).getByText('Live GitHub webhook smoke test')).toBeInTheDocument();
  expect(within(panel).getAllByText('exposure-session-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('External exposure closeout')).toBeInTheDocument();
  expect(within(panel).getByText('External exposure session is closed with complete local evidence.')).toBeInTheDocument();
  expect(within(panel).getByText('Closeout complete')).toBeInTheDocument();
  expect(within(panel).getByText('Latest session exposure-session-1 is CLOSED.')).toBeInTheDocument();
  expect(within(panel).getByText('Recent exposure closeout archives')).toBeInTheDocument();
  expect(within(panel).getByText('closeout-archive-1')).toBeInTheDocument();
  expect(within(panel).getByText('External exposure closeout archive is ready.')).toBeInTheDocument();
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
  const closeoutReportBlob = new Blob(['# PatchPilot External Exposure Closeout'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadCloseoutReport = vi.fn(async () => closeoutReportBlob);
  const onArchiveCloseout = vi.fn(async () => closeoutArchives[0]);
  const closeoutArchiveBlob = new Blob(['# PatchPilot External Exposure Closeout Archive'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadCloseoutArchiveReport = vi.fn(async () => closeoutArchiveBlob);
  const onStartSession = vi.fn(async () => sessions[0]);
  const onCloseSession = vi.fn(async () => ({ ...sessions[0], status: 'CLOSED' as const }));
  const sessionReportBlob = new Blob(['# PatchPilot External Exposure Session'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadSessionReport = vi.fn(async () => sessionReportBlob);
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
      sessions={sessions}
      sessionError={null}
      closeout={closeout}
      closeoutError={null}
      closeoutArchives={closeoutArchives}
      closeoutArchiveError={null}
      onArchiveReadiness={onArchiveReadiness}
      onDownloadArchiveReport={onDownloadArchiveReport}
      onDownloadHandoffPackageReport={onDownloadHandoffPackageReport}
      onArchiveCloseout={onArchiveCloseout}
      onDownloadCloseoutArchiveReport={onDownloadCloseoutArchiveReport}
      onStartSession={onStartSession}
      onCloseSession={onCloseSession}
      onDownloadSessionReport={onDownloadSessionReport}
      onDownloadCloseoutReport={onDownloadCloseoutReport}
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

  await user.click(screen.getByRole('button', { name: 'Download exposure closeout report' }));
  expect(onDownloadCloseoutReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(closeoutReportBlob);
  expect(screen.getByText('Exposure closeout report downloaded')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Archive exposure closeout' }));
  expect(onArchiveCloseout).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Exposure closeout archived')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Download exposure closeout archive closeout-archive-1' }));
  expect(onDownloadCloseoutArchiveReport).toHaveBeenCalledWith('closeout-archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(closeoutArchiveBlob);
  expect(screen.getByText('Exposure closeout archive downloaded')).toBeInTheDocument();

  await user.clear(screen.getByLabelText('Temporary public URL'));
  await user.type(screen.getByLabelText('Temporary public URL'), 'https://new-demo.trycloudflare.com');
  await user.clear(screen.getByLabelText('GitHub webhook URL'));
  await user.type(screen.getByLabelText('GitHub webhook URL'), 'https://new-demo.trycloudflare.com/api/github/webhook');
  await user.clear(screen.getByLabelText('Exposure purpose'));
  await user.type(screen.getByLabelText('Exposure purpose'), 'Reviewer live smoke test');
  await user.clear(screen.getByLabelText('Exposure operator'));
  await user.type(screen.getByLabelText('Exposure operator'), 'bingqin2');
  await user.click(screen.getByRole('button', { name: 'Start exposure session' }));
  expect(onStartSession).toHaveBeenCalledWith({
    publicUrl: 'https://new-demo.trycloudflare.com',
    webhookUrl: 'https://new-demo.trycloudflare.com/api/github/webhook',
    purpose: 'Reviewer live smoke test',
    operator: 'bingqin2',
    expectedShutdownAt: undefined,
    notes: ''
  });
  expect(screen.getByText('Exposure session started')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Close exposure session exposure-session-1' }));
  expect(onCloseSession).toHaveBeenCalledWith('exposure-session-1', {
    closedBy: 'bingqin2',
    closedAt: undefined,
    closeNotes: 'Closed from dashboard.'
  });
  expect(screen.getByText('Exposure session closed')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Download exposure session exposure-session-1' }));
  expect(onDownloadSessionReport).toHaveBeenCalledWith('exposure-session-1');
  expect(createObjectURL).toHaveBeenCalledWith(sessionReportBlob);
  expect(screen.getByText('Exposure session report downloaded')).toBeInTheDocument();

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
      sessions={[]}
      sessionError="sessions unavailable"
      closeout={null}
      closeoutError="closeout unavailable"
      closeoutArchives={[]}
      closeoutArchiveError="closeout archive unavailable"
      onArchiveReadiness={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageReport={vi.fn()}
      onArchiveCloseout={vi.fn()}
      onDownloadCloseoutArchiveReport={vi.fn()}
      onStartSession={vi.fn()}
      onCloseSession={vi.fn()}
      onDownloadSessionReport={vi.fn()}
      onDownloadCloseoutReport={vi.fn()}
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
  expect(screen.getByText('External exposure sessions unavailable')).toBeInTheDocument();
  expect(screen.getByText('sessions unavailable')).toBeInTheDocument();
  expect(screen.getByText('No external exposure sessions recorded.')).toBeInTheDocument();
  expect(screen.getByText('External exposure closeout unavailable')).toBeInTheDocument();
  expect(screen.getByText('closeout unavailable')).toBeInTheDocument();
  expect(screen.getByText('No external exposure closeout loaded.')).toBeInTheDocument();
  expect(screen.getByText('Exposure closeout archive unavailable')).toBeInTheDocument();
  expect(screen.getByText('closeout archive unavailable')).toBeInTheDocument();
  expect(screen.getByText('No external exposure closeout archives recorded.')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Archive exposure readiness' })).toBeDisabled();
  expect(screen.getByRole('button', { name: 'Archive exposure closeout' })).toBeDisabled();
  expect(screen.getByRole('button', { name: 'Refresh exposure gate' })).toBeEnabled();
});
