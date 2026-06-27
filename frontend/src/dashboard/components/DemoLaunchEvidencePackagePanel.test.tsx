import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  DemoLaunchEvidencePackage,
  DemoLaunchEvidencePackageArchive,
  DemoLaunchEvidenceShareCenter
} from '../../types';
import { DemoLaunchEvidencePackagePanel } from './DemoLaunchEvidencePackagePanel';

const launchEvidencePackage: DemoLaunchEvidencePackage = {
  status: 'READY',
  readyToShare: true,
  summary: 'PatchPilot launch evidence package is ready to share.',
  sessionId: 'demo-session-20260624T003000Z',
  launchReadinessStatus: 'READY',
  evidenceBundleStatus: 'READY',
  handoffFinalizationStatus: 'READY',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestWebhookDeliveryId: 'delivery-1',
  evaluationRunId: 'evaluation-run-2',
  evaluationCoverage: ['java', 'python', 'maven', 'pytest'],
  preLaunchChecks: [
    {
      name: 'Demo readiness',
      status: 'READY',
      message: 'PatchPilot is ready for a controlled demo.',
      action: 'No action needed.'
    },
    {
      name: 'Evidence bundle',
      status: 'READY',
      message: 'Demo evidence bundle is ready.',
      action: 'No action needed.'
    }
  ],
  liveRunProof: [
    'Recent task task-1 reached COMPLETED.',
    'Recent Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is available.',
    'Latest webhook delivery delivery-1 created task task-1.'
  ],
  postDemoProof: [
    'Handoff finalization is READY.',
    'Latest delivery receipt delivery-receipt-1 is fresh.'
  ],
  nextActions: [
    'Post the tested /agent fix comment, watch the task reach COMPLETED, then use the generated Pull Request for review.'
  ],
  healthContract: [
    'GET /api/demo/launch-evidence-package is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.'
  ],
  markdownReport: '# PatchPilot Demo Launch Evidence Package',
  generatedAt: '2026-06-28T02:00:00Z'
};

const launchEvidenceArchive: DemoLaunchEvidencePackageArchive = {
  id: 'launch-evidence-archive-1',
  status: 'READY',
  readyToShare: true,
  summary: 'PatchPilot launch evidence package is ready to share.',
  sessionId: 'demo-session-20260624T003000Z',
  launchReadinessStatus: 'READY',
  evidenceBundleStatus: 'READY',
  handoffFinalizationStatus: 'READY',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestWebhookDeliveryId: 'delivery-1',
  evaluationRunId: 'evaluation-run-2',
  createdAt: '2026-06-28T02:30:00Z',
  report: '# PatchPilot Demo Launch Evidence Package'
};

const launchEvidenceShareCenter: DemoLaunchEvidenceShareCenter = {
  status: 'READY',
  shareReady: true,
  summary: 'Latest archived launch evidence package is READY and can be shared.',
  nextAction: 'Download the archived launch evidence package and share it with reviewers.',
  archiveCount: 1,
  latestArchiveId: 'launch-evidence-archive-1',
  latestSessionId: 'demo-session-20260624T003000Z',
  latestCreatedAt: '2026-06-28T02:30:00Z',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestWebhookDeliveryId: 'delivery-1',
  evaluationRunId: 'evaluation-run-2',
  downloadActions: [
    'Download launch evidence package archive launch-evidence-archive-1.',
    'Download launch evidence share center report.',
    'Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review.'
  ],
  evidenceNotes: [
    'Latest launch evidence archive status is READY.',
    'Latest Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is ready for review.'
  ],
  markdownReport: '# PatchPilot Demo Launch Evidence Share Center',
  generatedAt: '2026-06-28T02:45:00Z'
};

test('renders demo launch evidence package proof and readiness status', () => {
  render(
    <DemoLaunchEvidencePackagePanel
      evidencePackage={launchEvidencePackage}
      error={null}
      archives={[launchEvidenceArchive]}
      archiveError={null}
      shareCenter={launchEvidenceShareCenter}
      shareCenterError={null}
      onArchivePackage={async () => launchEvidenceArchive}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
      onDownloadShareCenterReport={async () => new Blob(['share report'], { type: 'text/markdown' })}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch evidence package' });
  expect(within(panel).getByRole('heading', { name: 'Demo launch evidence package' })).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot launch evidence package is ready to share.')).toBeInTheDocument();
  expect(within(panel).getAllByText('Ready').length).toBeGreaterThanOrEqual(3);
  expect(within(panel).getAllByText('Ready to share').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getAllByText('demo-session-20260624T003000Z').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('task-1')).toBeInTheDocument();
  expect(within(panel).getByRole('link', { name: 'Open launch Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(within(panel).getByText('delivery-1')).toBeInTheDocument();
  expect(within(panel).getByText('evaluation-run-2')).toBeInTheDocument();
  expect(within(panel).getByText('java, python, maven, pytest')).toBeInTheDocument();
  expect(within(panel).getByText('Recent task task-1 reached COMPLETED.')).toBeInTheDocument();
  expect(within(panel).getAllByText('Latest delivery receipt delivery-receipt-1 is fresh.').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText(/does not create tasks, call the model, run tests, archive records/)).toBeInTheDocument();
  expect(within(panel).getAllByText(/launch-evidence-archive-1/).length).toBeGreaterThanOrEqual(3);
  expect(within(panel).getByRole('heading', { name: 'Launch evidence share center' })).toBeInTheDocument();
  expect(within(panel).getByText('Latest archived launch evidence package is READY and can be shared.')).toBeInTheDocument();
  expect(within(panel).getByText('Download launch evidence package archive launch-evidence-archive-1.')).toBeInTheDocument();
  expect(within(panel).getByText('Latest launch evidence archive status is READY.')).toBeInTheDocument();
  expect(within(panel).getByRole('link', { name: 'PR' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
});

test('copies and downloads demo launch evidence package markdown report', async () => {
  const writeText = vi.fn(async () => {});
  Object.assign(navigator, { clipboard: { writeText } });
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:launch-evidence-package');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onDownloadReport = vi.fn(async () => new Blob(['report'], { type: 'text/markdown' }));

  render(
    <DemoLaunchEvidencePackagePanel
      evidencePackage={launchEvidencePackage}
      error={null}
      archives={[launchEvidenceArchive]}
      archiveError={null}
      shareCenter={launchEvidenceShareCenter}
      shareCenterError={null}
      onArchivePackage={async () => launchEvidenceArchive}
      onDownloadReport={onDownloadReport}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
      onDownloadShareCenterReport={async () => new Blob(['share report'], { type: 'text/markdown' })}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Copy launch evidence report' }));
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Launch Evidence Package');
  expect(screen.getByText('Launch evidence report copied')).toBeInTheDocument();

  await userEvent.click(screen.getByRole('button', { name: 'Download launch evidence report' }));
  expect(onDownloadReport).toHaveBeenCalled();
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-evidence-package');
  expect(screen.getByText('Launch evidence report downloaded')).toBeInTheDocument();

  vi.unstubAllGlobals();
  click.mockRestore();
});

test('archives launch evidence package and downloads archived report', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:launch-evidence-package-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onArchivePackage = vi.fn(async () => launchEvidenceArchive);
  const onDownloadArchiveReport = vi.fn(async () => new Blob(['archive report'], { type: 'text/markdown' }));

  render(
    <DemoLaunchEvidencePackagePanel
      evidencePackage={launchEvidencePackage}
      error={null}
      archives={[launchEvidenceArchive]}
      archiveError={null}
      shareCenter={launchEvidenceShareCenter}
      shareCenterError={null}
      onArchivePackage={onArchivePackage}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={onDownloadArchiveReport}
      onDownloadShareCenterReport={async () => new Blob(['share report'], { type: 'text/markdown' })}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Archive launch evidence package' }));
  expect(onArchivePackage).toHaveBeenCalled();
  expect(screen.getByText('Archived launch package launch-evidence-archive-1')).toBeInTheDocument();

  await userEvent.click(screen.getByRole('button', { name: 'Download archived launch evidence report launch-evidence-archive-1' }));
  expect(onDownloadArchiveReport).toHaveBeenCalledWith('launch-evidence-archive-1');
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-evidence-package-archive');
  expect(screen.getByText('Archived report launch-evidence-archive-1 downloaded')).toBeInTheDocument();

  vi.unstubAllGlobals();
  click.mockRestore();
});

test('downloads launch evidence share center markdown report', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:launch-evidence-share-center');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onDownloadShareCenterReport = vi.fn(async () => new Blob(['share report'], { type: 'text/markdown' }));

  render(
    <DemoLaunchEvidencePackagePanel
      evidencePackage={launchEvidencePackage}
      error={null}
      archives={[launchEvidenceArchive]}
      archiveError={null}
      shareCenter={launchEvidenceShareCenter}
      shareCenterError={null}
      onArchivePackage={async () => launchEvidenceArchive}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
      onDownloadShareCenterReport={onDownloadShareCenterReport}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download launch evidence share center' }));

  expect(onDownloadShareCenterReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-evidence-share-center');
  expect(screen.getByText('Launch evidence share center downloaded')).toBeInTheDocument();

  vi.unstubAllGlobals();
  click.mockRestore();
});
