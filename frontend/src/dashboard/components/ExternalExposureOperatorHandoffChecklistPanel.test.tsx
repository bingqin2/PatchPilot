import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { ExternalExposureOperatorHandoffChecklist } from '../../types';
import { ExternalExposureOperatorHandoffChecklistPanel } from './ExternalExposureOperatorHandoffChecklistPanel';

const readyChecklist: ExternalExposureOperatorHandoffChecklist = {
  status: 'READY',
  readyForNextLiveStep: true,
  summary: 'External exposure evidence is closed and ready for the next live step.',
  nextAction:
    'Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.',
  repository: 'bingqin2/PatchPilot',
  latestCloseoutArchiveId: 'closeout-archive-1',
  latestSessionId: 'exposure-session-1',
  latestSessionStatus: 'CLOSED',
  publicUrl: 'https://demo.trycloudflare.com',
  webhookUrl: 'https://demo.trycloudflare.com/api/github/webhook',
  handoffStatus: 'READY',
  archiveFreshness: 'CURRENT',
  livePublishStatus: 'READY',
  livePublishReady: true,
  activeSessionCount: 0,
  readyCount: 4,
  needsAttentionCount: 0,
  blockedCount: 0,
  totalCount: 4,
  nextActions: [
    'Post the prepared /agent fix comment only after confirming the GitHub webhook payload URL still points to the intended backend.'
  ],
  evidenceNotes: [
    'Latest closeout archive closeout-archive-1 is READY.',
    'Live GitHub publish preflight is READY.'
  ],
  downloadActions: ['GET /api/security/external-exposure-operator-handoff-checklist/report/download'],
  sideEffectContract:
    'GET /api/security/external-exposure-operator-handoff-checklist is read-only and does not mutate GitHub.',
  checks: [
    {
      name: 'Closeout archive',
      status: 'READY',
      summary: 'Latest closeout archive closeout-archive-1 is READY.',
      nextAction: 'Ready.'
    },
    {
      name: 'Active exposure sessions',
      status: 'READY',
      summary: 'No active external exposure sessions.',
      nextAction: 'Ready.'
    },
    {
      name: 'Live GitHub publish preflight',
      status: 'READY',
      summary: 'Live GitHub publish preflight is ready.',
      nextAction: 'Ready.'
    }
  ],
  generatedAt: '2026-07-01T19:00:00Z',
  markdownReport: '# PatchPilot External Exposure Operator Handoff Checklist'
};

afterEach(() => {
  vi.restoreAllMocks();
  vi.unstubAllGlobals();
});

test('renders ready external exposure operator handoff checklist', () => {
  render(
    <ExternalExposureOperatorHandoffChecklistPanel
      checklist={readyChecklist}
      error={null}
      onDownloadReport={vi.fn()}
      onRefresh={vi.fn()}
    />
  );

  const panel = screen.getByLabelText('External exposure operator handoff checklist');
  expect(within(panel).getByRole('heading', { name: 'External exposure handoff checklist' })).toBeInTheDocument();
  expect(within(panel).getByText('Ready for next live step')).toBeInTheDocument();
  expect(within(panel).getByText('bingqin2/PatchPilot')).toBeInTheDocument();
  expect(within(panel).getByText('closeout-archive-1')).toBeInTheDocument();
  expect(within(panel).getByText('exposure-session-1')).toBeInTheDocument();
  expect(within(panel).getByText('https://demo.trycloudflare.com/api/github/webhook')).toBeInTheDocument();
  expect(within(panel).getByText('0 active sessions')).toBeInTheDocument();
  expect(within(panel).getAllByText('Closeout archive').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('Live GitHub publish preflight')).toBeInTheDocument();
  expect(within(panel).getAllByText('Latest closeout archive closeout-archive-1 is READY.').length)
    .toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('GET /api/security/external-exposure-operator-handoff-checklist/report/download')).toBeInTheDocument();
});

test('downloads and refreshes external exposure operator handoff checklist', async () => {
  const user = userEvent.setup();
  const reportBlob = new Blob(['# PatchPilot External Exposure Operator Handoff Checklist'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadReport = vi.fn(async () => reportBlob);
  const onRefresh = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:operator-handoff-checklist');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);

  render(
    <ExternalExposureOperatorHandoffChecklistPanel
      checklist={readyChecklist}
      error={null}
      onDownloadReport={onDownloadReport}
      onRefresh={onRefresh}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Download exposure handoff checklist' }));
  expect(onDownloadReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:operator-handoff-checklist');
  expect(screen.getByText('Exposure handoff checklist downloaded')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Refresh exposure handoff checklist' }));
  expect(onRefresh).toHaveBeenCalledTimes(1);
});

test('shows blocked checklist and error state', () => {
  const blockedChecklist: ExternalExposureOperatorHandoffChecklist = {
    ...readyChecklist,
    status: 'BLOCKED',
    readyForNextLiveStep: false,
    summary: 'External exposure handoff is blocked before the next live step.',
    nextAction: 'Close active external exposure sessions before posting another live /agent fix.',
    activeSessionCount: 1,
    readyCount: 3,
    blockedCount: 1,
    checks: readyChecklist.checks.map((check) =>
      check.name === 'Active exposure sessions'
        ? {
            name: 'Active exposure sessions',
            status: 'BLOCKED' as const,
            summary: '1 active external exposure session remains open.',
            nextAction: 'Close active external exposure sessions before posting another live /agent fix.'
          }
        : check
    )
  };

  render(
    <ExternalExposureOperatorHandoffChecklistPanel
      checklist={blockedChecklist}
      error="checklist unavailable"
      onDownloadReport={vi.fn()}
      onRefresh={vi.fn()}
    />
  );

  expect(screen.getByText('External exposure handoff checklist unavailable')).toBeInTheDocument();
  expect(screen.getByText('checklist unavailable')).toBeInTheDocument();
  expect(screen.getByText('Blocked before next live step')).toBeInTheDocument();
  expect(screen.getByText('1 active sessions')).toBeInTheDocument();
  expect(screen.getAllByText('Close active external exposure sessions before posting another live /agent fix.').length)
    .toBeGreaterThanOrEqual(1);
});
