import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  DemoLaunchAcceptanceCertificate,
  DemoLaunchAcceptanceCertificateArchive,
  DemoLaunchAcceptanceCloseout,
  DemoLaunchAcceptanceCloseoutArchive,
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
  finalHandoffReportPackageArchiveStatus: 'READY',
  finalHandoffReportPackageArchiveReady: true,
  finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
  finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
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
    'Latest delivery receipt delivery-receipt-1 is fresh.',
    'Final handoff report package archive final-handoff-report-package-archive-1 is download-ready.'
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
  finalHandoffReportPackageArchiveStatus: 'READY',
  finalHandoffReportPackageArchiveReady: true,
  finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
  finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
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
  finalHandoffReportPackageArchiveStatus: 'READY',
  finalHandoffReportPackageArchiveReady: true,
  finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
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
    'Download final handoff report package archive final-handoff-report-package-archive-1.',
    'Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review.',
    'Download launch evidence delivery receipt launch-delivery-receipt-1.'
  ],
  evidenceNotes: [
    'Latest launch evidence archive status is READY.',
    'Final handoff report package archive final-handoff-report-package-archive-1 is READY and download-ready.',
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
  finalHandoffReportPackageArchiveStatus: 'READY',
  finalHandoffReportPackageArchiveReady: true,
  finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
  finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
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
    'Download final handoff report package archive final-handoff-report-package-archive-1.',
    'Download launch acceptance closeout report.'
  ],
  markdownReport: '# PatchPilot Launch Acceptance Closeout'
};

const launchAcceptanceCloseoutArchive: DemoLaunchAcceptanceCloseoutArchive = {
  id: 'launch-closeout-archive-1',
  status: 'READY',
  accepted: true,
  summary: 'PatchPilot launch acceptance closeout is complete.',
  sessionId: 'demo-session-20260624T003000Z',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestWebhookDeliveryId: 'delivery-1',
  evaluationRunId: 'evaluation-run-2',
  latestArchiveId: 'launch-evidence-archive-1',
  finalHandoffReportPackageArchiveStatus: 'READY',
  finalHandoffReportPackageArchiveReady: true,
  finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
  finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
  latestDeliveryReceiptId: 'launch-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  deliveryReceiptFreshness: 'FRESH',
  createdAt: '2026-06-28T08:30:00Z',
  report: '# PatchPilot Launch Acceptance Closeout'
};

const launchAcceptanceCertificate: DemoLaunchAcceptanceCertificate = {
  status: 'READY',
  certified: true,
  summary: 'PatchPilot launch acceptance is certified from the latest accepted closeout archive.',
  nextAction: 'Share the certificate and archived closeout report with reviewers.',
  archiveCount: 1,
  latestCloseoutArchiveId: 'launch-closeout-archive-1',
  latestLaunchEvidenceArchiveId: 'launch-evidence-archive-1',
  finalHandoffReportPackageArchiveStatus: 'READY',
  finalHandoffReportPackageArchiveReady: true,
  finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
  finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
  latestDeliveryReceiptId: 'launch-delivery-receipt-1',
  latestSessionId: 'demo-session-20260624T003000Z',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestWebhookDeliveryId: 'delivery-1',
  evaluationRunId: 'evaluation-run-2',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  deliveryReceiptFreshness: 'FRESH',
  latestArchivedAt: '2026-06-28T08:30:00Z',
  generatedAt: '2026-06-28T09:00:00Z',
  downloadActions: [
    'Download launch acceptance certificate.',
    'Download launch acceptance closeout archive launch-closeout-archive-1.',
    'Download final handoff report package archive final-handoff-report-package-archive-1.',
    'Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review.'
  ],
  markdownReport: '# PatchPilot Launch Acceptance Certificate'
};

const launchAcceptanceCertificateArchive: DemoLaunchAcceptanceCertificateArchive = {
  id: 'launch-certificate-archive-1',
  status: 'READY',
  certified: true,
  summary: 'PatchPilot launch acceptance is certified from the latest accepted closeout archive.',
  nextAction: 'Share the certificate and archived closeout report with reviewers.',
  archiveCount: 1,
  latestCloseoutArchiveId: 'launch-closeout-archive-1',
  latestLaunchEvidenceArchiveId: 'launch-evidence-archive-1',
  finalHandoffReportPackageArchiveStatus: 'READY',
  finalHandoffReportPackageArchiveReady: true,
  finalHandoffReportPackageArchiveId: 'final-handoff-report-package-archive-1',
  finalHandoffReportPackageArchiveSummary: 'Latest final handoff report package archive is download-ready and ready.',
  latestDeliveryReceiptId: 'launch-delivery-receipt-1',
  latestSessionId: 'demo-session-20260624T003000Z',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestWebhookDeliveryId: 'delivery-1',
  evaluationRunId: 'evaluation-run-2',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  deliveryReceiptFreshness: 'FRESH',
  latestArchivedAt: '2026-06-28T08:30:00Z',
  generatedAt: '2026-06-28T09:00:00Z',
  archivedAt: '2026-06-28T10:30:00Z',
  downloadActions: ['Download launch acceptance certificate.'],
  report: '# PatchPilot Launch Acceptance Certificate'
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

type PanelOverrides = Partial<Parameters<typeof DemoLaunchEvidencePackagePanel>[0]>;

function renderPanel(overrides: PanelOverrides = {}) {
  return render(
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
      closeoutArchives={[launchAcceptanceCloseoutArchive]}
      closeoutArchiveError={null}
      certificate={launchAcceptanceCertificate}
      certificateError={null}
      certificateArchives={[launchAcceptanceCertificateArchive]}
      certificateArchiveError={null}
      deliveryReceipts={[launchEvidenceDeliveryReceipt]}
      deliveryReceiptError={null}
      onArchivePackage={async () => launchEvidenceArchive}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
      onDownloadArchiveReport={async () => new Blob(['archive report'], { type: 'text/markdown' })}
      onDownloadShareCenterReport={async () => new Blob(['share report'], { type: 'text/markdown' })}
      onDownloadFinalizationReport={async () => new Blob(['finalization report'], { type: 'text/markdown' })}
      onDownloadCloseoutReport={async () => new Blob(['closeout report'], { type: 'text/markdown' })}
      onArchiveCloseout={async () => launchAcceptanceCloseoutArchive}
      onDownloadCloseoutArchiveReport={async () => new Blob(['closeout archive report'], { type: 'text/markdown' })}
      onDownloadCertificateReport={async () => new Blob(['certificate report'], { type: 'text/markdown' })}
      onArchiveCertificate={async () => launchAcceptanceCertificateArchive}
      onDownloadCertificateArchiveReport={async () => new Blob(['certificate archive report'], { type: 'text/markdown' })}
      onCreateDeliveryReceipt={async () => launchEvidenceDeliveryReceipt}
      onDownloadDeliveryReceiptReport={async () => new Blob(['receipt report'], { type: 'text/markdown' })}
      {...overrides}
    />
  );
}

test('renders demo launch evidence package proof and readiness status', () => {
  renderPanel();

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
  expect(within(panel).getAllByText('Final handoff package archive').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getAllByText('final-handoff-report-package-archive-1').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getAllByText('Latest final handoff report package archive is download-ready and ready.').length).toBeGreaterThanOrEqual(1);
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
  expect(within(panel).getAllByText('Final handoff archive').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('Final handoff proof')).toBeInTheDocument();
  expect(within(panel).getAllByText('Download-ready').length).toBeGreaterThanOrEqual(3);
  expect(within(panel).getAllByText('Latest final handoff report package archive is download-ready and ready.').length).toBeGreaterThanOrEqual(3);
  expect(
    within(panel).getAllByText('Download final handoff report package archive final-handoff-report-package-archive-1.').length
  ).toBeGreaterThanOrEqual(3);
  expect(within(panel).getByText('Download launch acceptance closeout report.')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Recent launch acceptance closeouts' })).toBeInTheDocument();
  expect(within(panel).getAllByText(/launch-closeout-archive-1/).length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText(/Accepted closeout/)).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Launch acceptance certificate' })).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot launch acceptance is certified from the latest accepted closeout archive.')).toBeInTheDocument();
  expect(within(panel).getByText('Certified')).toBeInTheDocument();
  expect(within(panel).getByText('Share the certificate and archived closeout report with reviewers.')).toBeInTheDocument();
  expect(within(panel).getByText('Download launch acceptance certificate.')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Recent launch acceptance certificates' })).toBeInTheDocument();
  expect(within(panel).getAllByText(/launch-certificate-archive-1/).length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getAllByText('launch-delivery-receipt-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Latest delivery receipt matches the current launch evidence archive and session.')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Launch evidence delivery receipts' })).toBeInTheDocument();
  expect(within(panel).getAllByText(/reviewer@example.com/).length).toBeGreaterThanOrEqual(1);
  expect(
    within(panel)
      .getAllByRole('link', { name: 'PR' })
      .some((link) => link.getAttribute('href') === 'https://github.com/bingqin2/PatchPilot/pull/42')
  ).toBe(true);
});

test('downloads launch acceptance certificate markdown report', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:launch-acceptance-certificate');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onDownloadCertificateReport = vi.fn(async () => new Blob(['certificate report'], { type: 'text/markdown' }));

  renderPanel({ onDownloadCertificateReport });

  await userEvent.click(screen.getByRole('button', { name: 'Download launch acceptance certificate' }));

  expect(onDownloadCertificateReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-acceptance-certificate');
  expect(screen.getByText('Launch acceptance certificate downloaded')).toBeInTheDocument();

  vi.unstubAllGlobals();
  click.mockRestore();
});

test('archives launch acceptance certificate and downloads archived report', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:launch-acceptance-certificate-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onArchiveCertificate = vi.fn(async () => launchAcceptanceCertificateArchive);
  const onDownloadCertificateArchiveReport = vi.fn(async () => new Blob(['certificate archive report'], { type: 'text/markdown' }));

  renderPanel({ onArchiveCertificate, onDownloadCertificateArchiveReport });

  await userEvent.click(screen.getByRole('button', { name: 'Archive launch acceptance certificate' }));
  expect(onArchiveCertificate).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Launch acceptance certificate archived launch-certificate-archive-1')).toBeInTheDocument();

  await userEvent.click(screen.getByRole('button', { name: 'Download launch acceptance certificate archive launch-certificate-archive-1' }));
  expect(onDownloadCertificateArchiveReport).toHaveBeenCalledWith('launch-certificate-archive-1');
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-acceptance-certificate-archive');
  expect(screen.getByText('Launch acceptance certificate archive downloaded')).toBeInTheDocument();

  vi.unstubAllGlobals();
  click.mockRestore();
});

test('copies and downloads demo launch evidence package markdown report', async () => {
  const writeText = vi.fn(async () => {});
  Object.assign(navigator, { clipboard: { writeText } });
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:launch-evidence-package');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onDownloadReport = vi.fn(async () => new Blob(['report'], { type: 'text/markdown' }));

  renderPanel({ onDownloadReport });

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

  renderPanel({ onArchivePackage, onDownloadArchiveReport });

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

  renderPanel({ onDownloadShareCenterReport });

  await userEvent.click(screen.getByRole('button', { name: 'Download launch evidence share center' }));

  expect(onDownloadShareCenterReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-evidence-share-center');
  expect(screen.getByText('Launch evidence share center downloaded')).toBeInTheDocument();

  vi.unstubAllGlobals();
  click.mockRestore();
});

test('archives launch acceptance closeout and downloads archived closeout report', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:launch-closeout-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onArchiveCloseout = vi.fn(async () => launchAcceptanceCloseoutArchive);
  const onDownloadCloseoutArchiveReport = vi.fn(async () => new Blob(['closeout archive report'], { type: 'text/markdown' }));

  renderPanel({ onArchiveCloseout, onDownloadCloseoutArchiveReport });

  await userEvent.click(screen.getByRole('button', { name: 'Archive launch acceptance closeout' }));
  expect(onArchiveCloseout).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Archived launch closeout launch-closeout-archive-1')).toBeInTheDocument();

  await userEvent.click(
    screen.getByRole('button', { name: 'Download archived launch acceptance closeout launch-closeout-archive-1' })
  );
  expect(onDownloadCloseoutArchiveReport).toHaveBeenCalledWith('launch-closeout-archive-1');
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-closeout-archive');
  expect(screen.getByText('Archived closeout launch-closeout-archive-1 downloaded')).toBeInTheDocument();

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

  renderPanel({
    onDownloadFinalizationReport,
    onDownloadCloseoutReport,
    onCreateDeliveryReceipt,
    onDownloadDeliveryReceiptReport
  });

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
