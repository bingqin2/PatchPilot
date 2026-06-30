import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { SelfHostedLaunchReadinessPanel } from './SelfHostedLaunchReadinessPanel';
import type { DemoSelfHostedLaunchReadiness, DemoSelfHostedLaunchReadinessArchive } from '../../types';

const launchReadiness: DemoSelfHostedLaunchReadiness = {
  status: 'NEEDS_ATTENTION',
  readyToLaunch: false,
  summary: 'Self-hosted PatchPilot needs attention before launch.',
  checks: [
    {
      name: 'Demo readiness',
      status: 'READY',
      message: 'PatchPilot is ready for a controlled demo.',
      action: 'No action needed.'
    },
    {
      name: 'Handoff finalization',
      status: 'NEEDS_ATTENTION',
      message: 'Demo handoff package is send-ready but final delivery evidence is not current.',
      action: 'Send the current handoff package, record a delivery receipt, then download the finalization report.'
    },
    {
      name: 'GitHub publish path',
      status: 'READY',
      message: 'GitHub publish path is ready for PatchPilot push and Pull Request creation.',
      action: 'Continue with the live /agent fix demo.'
    },
    {
      name: 'GitHub publish permissions',
      status: 'NEEDS_ATTENTION',
      message: 'GitHub token can read the repository but does not expose write permissions required for publish.',
      action: 'Grant Contents: Read and write, Pull requests: Read and write, and Issues: Read and write on the demo repository; then restart PatchPilot if the token changed.'
    }
  ],
  nextActions: [
    'Send the current handoff package, record a delivery receipt, then download the finalization report.',
    'Grant Contents: Read and write, Pull requests: Read and write, and Issues: Read and write on the demo repository; then restart PatchPilot if the token changed.',
    'Resolve launch package warnings, then rerun this readiness package.'
  ],
  generatedAt: '2026-06-27T01:00:00Z',
  markdownReport: '# PatchPilot Self-Hosted Launch Readiness'
};

const archives: DemoSelfHostedLaunchReadinessArchive[] = [
  {
    id: 'launch-readiness-archive-1',
    status: 'READY',
    readyToLaunch: true,
    summary: 'Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.',
    readyCheckCount: 6,
    needsAttentionCheckCount: 0,
    blockedCheckCount: 0,
    createdAt: '2026-06-28T01:30:00Z',
    report: '# PatchPilot Self-Hosted Launch Readiness'
  }
];

test('renders self-hosted launch readiness checks and next actions', () => {
  render(
    <SelfHostedLaunchReadinessPanel
      readiness={launchReadiness}
      error={null}
      archives={archives}
      archiveError={null}
      onArchiveReadiness={async () => archives[0]}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
    />
  );

  const panel = screen.getByRole('region', { name: 'Self-hosted launch readiness' });
  expect(within(panel).getByText('Self-hosted launch readiness')).toBeInTheDocument();
  expect(within(panel).getAllByText('Needs attention').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('Not ready')).toBeInTheDocument();
  expect(within(panel).getByText('Handoff finalization')).toBeInTheDocument();
  expect(within(panel).getByText('Demo handoff package is send-ready but final delivery evidence is not current.')).toBeInTheDocument();
  expect(within(panel).getByText('GitHub publish path')).toBeInTheDocument();
  expect(within(panel).getByText('GitHub publish path is ready for PatchPilot push and Pull Request creation.')).toBeInTheDocument();
  expect(within(panel).getByText('GitHub publish permissions')).toBeInTheDocument();
  expect(within(panel).getByText('GitHub token can read the repository but does not expose write permissions required for publish.')).toBeInTheDocument();
  expect(within(panel).getAllByText('Grant Contents: Read and write, Pull requests: Read and write, and Issues: Read and write on the demo repository; then restart PatchPilot if the token changed.').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Resolve launch package warnings, then rerun this readiness package.')).toBeInTheDocument();
  expect(within(panel).getByText('Recent launch readiness archives')).toBeInTheDocument();
  expect(within(panel).getByText('launch-readiness-archive-1')).toBeInTheDocument();
});

test('downloads self-hosted launch readiness markdown report', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:self-hosted-launch-readiness');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onDownloadReport = vi.fn(async () => new Blob(['report'], { type: 'text/markdown' }));

  render(
    <SelfHostedLaunchReadinessPanel
      readiness={launchReadiness}
      error={null}
      archives={archives}
      archiveError={null}
      onArchiveReadiness={async () => archives[0]}
      onDownloadReport={onDownloadReport}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download launch readiness report' }));

  expect(onDownloadReport).toHaveBeenCalled();
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:self-hosted-launch-readiness');
  expect(screen.getByText('Launch readiness report downloaded')).toBeInTheDocument();

  vi.unstubAllGlobals();
  click.mockRestore();
});

test('archives self-hosted launch readiness and downloads archived reports', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:self-hosted-launch-readiness-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onArchiveReadiness = vi.fn(async () => archives[0]);
  const onDownloadArchiveReport = vi.fn(async () => new Blob(['archive report'], { type: 'text/markdown' }));

  render(
    <SelfHostedLaunchReadinessPanel
      readiness={launchReadiness}
      error={null}
      archives={archives}
      archiveError={null}
      onArchiveReadiness={onArchiveReadiness}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={onDownloadArchiveReport}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Archive launch readiness' }));
  expect(onArchiveReadiness).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Launch readiness archived')).toBeInTheDocument();

  await userEvent.click(screen.getByRole('button', { name: 'Download launch readiness archive launch-readiness-archive-1' }));
  expect(onDownloadArchiveReport).toHaveBeenCalledWith('launch-readiness-archive-1');
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:self-hosted-launch-readiness-archive');

  vi.unstubAllGlobals();
  click.mockRestore();
});
