import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  DemoLaunchAcceptanceCloseout,
  DemoLaunchEvidenceFinalization,
  DemoLaunchEvidencePackage,
  DemoLaunchEvidencePackageArchive,
  DemoLaunchEvidenceShareCenter,
  DemoLaunchEvidenceShareDeliveryReceipt
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
  latestDeliveryReceiptId: 'launch-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  latestDeliveredAt: '2026-06-28T06:05:00Z',
  deliveryReceiptRecorded: true,
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current launch evidence archive and session.',
  downloadActions: [
    'Download launch evidence package archive launch-evidence-archive-1.',
    'Download launch evidence share center report.',
    'Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review.',
    'Download launch evidence delivery receipt launch-delivery-receipt-1.'
  ],
  evidenceNotes: [
    'Latest launch evidence archive status is READY.',
    'Latest delivery receipt launch-delivery-receipt-1 was recorded for reviewer@example.com via email.',
    'Latest Pull Request https://github.com/bingqin2/PatchPilot/pull/42 is ready for review.'
  ],
  markdownReport: '# PatchPilot Demo Launch Evidence Share Center',
  generatedAt: '2026-06-28T02:45:00Z'
};

const launchEvidenceFinalization: DemoLaunchEvidenceFinalization = {
  status: 'READY',
  finalized: true,
  summary: 'Demo launch evidence is finalized with a fresh delivery receipt for the current archive.',
  nextAction: 'Use the finalization report as the launch evidence delivery acceptance record.',
  latestArchiveId: 'launch-evidence-archive-1',
  latestSessionId: 'demo-session-20260624T003000Z',
  latestDeliveryReceiptId: 'launch-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  latestDeliveredAt: '2026-06-28T06:05:00Z',
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current launch evidence archive and session.',
  checks: [
    {
      name: 'Launch evidence share readiness',
      status: 'READY',
      summary: 'Share center is ready.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: ['Finalization report can be downloaded as the launch delivery acceptance record.'],
  markdownReport: '# PatchPilot Demo Launch Evidence Finalization Gate',
  generatedAt: '2026-06-28T06:30:00Z'
};

const launchAcceptanceCloseout: DemoLaunchAcceptanceCloseout = {
  status: 'READY',
  accepted: true,
  summary: 'PatchPilot launch acceptance closeout is complete.',
  nextAction: 'Use this closeout report as the final self-hosted launch acceptance record.',
  sessionId: 'demo-session-20260624T003000Z',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestWebhookDeliveryId: 'delivery-1',
  evaluationRunId: 'evaluation-run-2',
  latestArchiveId: 'launch-evidence-archive-1',
  latestDeliveryReceiptId: 'launch-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  latestDeliveredAt: '2026-06-28T06:05:00Z',
  deliveryReceiptFreshness: 'FRESH',
  generatedAt: '2026-06-28T07:15:00Z',
  checks: [
    {
      name: 'Self-hosted launch readiness',
      status: 'READY',
      summary: 'Self-hosted PatchPilot is ready for a controlled issue-to-PR launch.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: [
    'Launch readiness status is READY.',
    'Delivery receipt launch-delivery-receipt-1 is fresh for demo-session-20260624T003000Z.'
  ],
  downloadActions: [
    'Download self-hosted launch readiness report.',
    'Download launch evidence package report.',
    'Download launch evidence share center report.',
    'Download launch evidence finalization report.',
    'Download launch acceptance closeout report.'
  ],
  markdownReport: '# PatchPilot Launch Acceptance Closeout'
};

const launchEvidenceDeliveryReceipt: DemoLaunchEvidenceShareDeliveryReceipt = {
  id: 'launch-delivery-receipt-1',
  status: 'READY',
  launchEvidenceArchiveId: 'launch-evidence-archive-1',
  sessionId: 'demo-session-20260624T003000Z',
  deliveryChannel: 'email',
  deliveryTarget: 'reviewer@example.com',
  operator: 'local-operator',
  notes: 'Sent final launch evidence after the smoke demo.',
  messageSubject: 'PatchPilot demo launch evidence: demo-session-20260624T003000Z',
  deliveredAt: '2026-06-28T06:05:00Z',
  createdAt: '2026-06-28T06:10:00Z',
  markdownReport: '# PatchPilot Demo Launch Evidence Delivery Receipt'
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
      finalization={launchEvidenceFinalization}
      finalizationError={null}
      closeout={launchAcceptanceCloseout}
      closeoutError={null}
      deliveryReceipts={[launchEvidenceDeliveryReceipt]}
      deliveryReceiptError={null}
      onArchivePackage={async () => launchEvidenceArchive}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
      onDownloadShareCenterReport={async () => new Blob(['share report'], { type: 'text/markdown' })}
      onDownloadFinalizationReport={async () => new Blob(['finalization report'], { type: 'text/markdown' })}
      onDownloadCloseoutReport={async () => new Blob(['closeout report'], { type: 'text/markdown' })}
      onCreateDeliveryReceipt={async () => launchEvidenceDeliveryReceipt}
      onDownloadDeliveryReceiptReport={async () => new Blob(['receipt report'], { type: 'text/markdown' })}
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
  expect(within(panel).getByRole('heading', { name: 'Launch evidence finalization' })).toBeInTheDocument();
  expect(within(panel).getByText('Demo launch evidence is finalized with a fresh delivery receipt for the current archive.')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Launch acceptance closeout' })).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot launch acceptance closeout is complete.')).toBeInTheDocument();
  expect(within(panel).getByText('Accepted')).toBeInTheDocument();
  expect(within(panel).getByText('Use this closeout report as the final self-hosted launch acceptance record.')).toBeInTheDocument();
  expect(within(panel).getByText('Download launch acceptance closeout report.')).toBeInTheDocument();
  expect(within(panel).getAllByText('launch-delivery-receipt-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Latest delivery receipt matches the current launch evidence archive and session.')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Launch evidence delivery receipts' })).toBeInTheDocument();
  expect(within(panel).getAllByText(/reviewer@example.com/).length).toBeGreaterThanOrEqual(1);
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
      finalization={launchEvidenceFinalization}
      finalizationError={null}
      closeout={launchAcceptanceCloseout}
      closeoutError={null}
      deliveryReceipts={[launchEvidenceDeliveryReceipt]}
      deliveryReceiptError={null}
      onArchivePackage={async () => launchEvidenceArchive}
      onDownloadReport={onDownloadReport}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
      onDownloadShareCenterReport={async () => new Blob(['share report'], { type: 'text/markdown' })}
      onDownloadFinalizationReport={async () => new Blob(['finalization report'], { type: 'text/markdown' })}
      onDownloadCloseoutReport={async () => new Blob(['closeout report'], { type: 'text/markdown' })}
      onCreateDeliveryReceipt={async () => launchEvidenceDeliveryReceipt}
      onDownloadDeliveryReceiptReport={async () => new Blob(['receipt report'], { type: 'text/markdown' })}
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
      finalization={launchEvidenceFinalization}
      finalizationError={null}
      closeout={launchAcceptanceCloseout}
      closeoutError={null}
      deliveryReceipts={[launchEvidenceDeliveryReceipt]}
      deliveryReceiptError={null}
      onArchivePackage={onArchivePackage}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={onDownloadArchiveReport}
      onDownloadShareCenterReport={async () => new Blob(['share report'], { type: 'text/markdown' })}
      onDownloadFinalizationReport={async () => new Blob(['finalization report'], { type: 'text/markdown' })}
      onDownloadCloseoutReport={async () => new Blob(['closeout report'], { type: 'text/markdown' })}
      onCreateDeliveryReceipt={async () => launchEvidenceDeliveryReceipt}
      onDownloadDeliveryReceiptReport={async () => new Blob(['receipt report'], { type: 'text/markdown' })}
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
      finalization={launchEvidenceFinalization}
      finalizationError={null}
      closeout={launchAcceptanceCloseout}
      closeoutError={null}
      deliveryReceipts={[launchEvidenceDeliveryReceipt]}
      deliveryReceiptError={null}
      onArchivePackage={async () => launchEvidenceArchive}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
      onDownloadShareCenterReport={onDownloadShareCenterReport}
      onDownloadFinalizationReport={async () => new Blob(['finalization report'], { type: 'text/markdown' })}
      onDownloadCloseoutReport={async () => new Blob(['closeout report'], { type: 'text/markdown' })}
      onCreateDeliveryReceipt={async () => launchEvidenceDeliveryReceipt}
      onDownloadDeliveryReceiptReport={async () => new Blob(['receipt report'], { type: 'text/markdown' })}
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

test('records launch evidence delivery receipt and downloads finalization evidence', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:launch-finalization');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onCreateDeliveryReceipt = vi.fn(async () => launchEvidenceDeliveryReceipt);
  const onDownloadFinalizationReport = vi.fn(async () => new Blob(['finalization report'], { type: 'text/markdown' }));
  const onDownloadCloseoutReport = vi.fn(async () => new Blob(['closeout report'], { type: 'text/markdown' }));
  const onDownloadDeliveryReceiptReport = vi.fn(async () => new Blob(['receipt report'], { type: 'text/markdown' }));

  render(
    <DemoLaunchEvidencePackagePanel
      evidencePackage={launchEvidencePackage}
      error={null}
      archives={[launchEvidenceArchive]}
      archiveError={null}
      shareCenter={launchEvidenceShareCenter}
      shareCenterError={null}
      finalization={launchEvidenceFinalization}
      finalizationError={null}
      closeout={launchAcceptanceCloseout}
      closeoutError={null}
      deliveryReceipts={[launchEvidenceDeliveryReceipt]}
      deliveryReceiptError={null}
      onArchivePackage={async () => launchEvidenceArchive}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
      onDownloadShareCenterReport={async () => new Blob(['share report'], { type: 'text/markdown' })}
      onDownloadFinalizationReport={onDownloadFinalizationReport}
      onDownloadCloseoutReport={onDownloadCloseoutReport}
      onCreateDeliveryReceipt={onCreateDeliveryReceipt}
      onDownloadDeliveryReceiptReport={onDownloadDeliveryReceiptReport}
    />
  );

  await userEvent.clear(screen.getByLabelText('Launch delivery channel'));
  await userEvent.type(screen.getByLabelText('Launch delivery channel'), 'email');
  await userEvent.clear(screen.getByLabelText('Launch delivery target'));
  await userEvent.type(screen.getByLabelText('Launch delivery target'), 'reviewer@example.com');
  await userEvent.clear(screen.getByLabelText('Launch delivery operator'));
  await userEvent.type(screen.getByLabelText('Launch delivery operator'), 'local-operator');
  await userEvent.clear(screen.getByLabelText('Launch delivery notes'));
  await userEvent.type(screen.getByLabelText('Launch delivery notes'), 'Sent final launch evidence after the smoke demo.');
  await userEvent.click(screen.getByRole('button', { name: 'Record launch evidence delivery receipt' }));

  expect(onCreateDeliveryReceipt).toHaveBeenCalledWith({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'local-operator',
    notes: 'Sent final launch evidence after the smoke demo.',
    deliveredAt: expect.any(String)
  });
  expect(screen.getByText('Recorded launch evidence receipt launch-delivery-receipt-1')).toBeInTheDocument();

  await userEvent.click(screen.getByRole('button', { name: 'Download launch evidence finalization' }));
  expect(onDownloadFinalizationReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();

  await userEvent.click(screen.getByRole('button', { name: 'Download launch acceptance closeout' }));
  expect(onDownloadCloseoutReport).toHaveBeenCalledTimes(1);

  await userEvent.click(screen.getByRole('button', { name: 'Download launch delivery receipt launch-delivery-receipt-1' }));
  expect(onDownloadDeliveryReceiptReport).toHaveBeenCalledWith('launch-delivery-receipt-1');

  vi.unstubAllGlobals();
  click.mockRestore();
});
