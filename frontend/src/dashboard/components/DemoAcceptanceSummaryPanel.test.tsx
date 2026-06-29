import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  DemoAcceptanceSummary,
  DemoFinalAcceptanceShareDeliveryReceipt,
  DemoFinalAcceptanceShareFinalization,
  DemoFinalAcceptanceCompletionArchive,
  DemoFinalAcceptanceCompletionEvidenceBundle,
  DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt,
  DemoFinalAcceptanceSharePackage,
  DemoFinalAcceptanceSharePackageArchive
} from '../../types';
import { DemoAcceptanceSummaryPanel } from './DemoAcceptanceSummaryPanel';

const summary: DemoAcceptanceSummary = {
  status: 'READY',
  accepted: true,
  summary: 'PatchPilot final demo acceptance is ready for external review.',
  nextAction: 'Share the launch and task evidence certificates with reviewers.',
  launchCertificateStatus: 'READY',
  launchCertificateArchived: true,
  launchCertificateCertified: true,
  launchCertificateArchiveId: 'launch-certificate-archive-1',
  launchCloseoutArchiveId: 'launch-closeout-archive-1',
  launchEvidenceArchiveId: 'launch-evidence-archive-1',
  launchDeliveryReceiptId: 'launch-delivery-receipt-1',
  taskCertificateStatus: 'READY',
  taskCertificateArchived: true,
  taskCertificateCertified: true,
  taskCertificateArchiveId: 'task-evidence-certificate-archive-1',
  taskCloseoutArchiveId: 'task-evidence-closeout-archive-1',
  taskEvidenceArchiveId: 'task-evidence-archive-1',
  taskDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  generatedAt: '2026-06-28T09:20:00Z',
  checks: [
    {
      name: 'Launch acceptance certificate',
      status: 'READY',
      summary: 'Latest launch acceptance certificate archive is certified.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Task evidence acceptance certificate',
      status: 'READY',
      summary: 'Latest task evidence acceptance certificate archive is certified.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: [
    'Launch acceptance certificate archive launch-certificate-archive-1 is certified.',
    'Task evidence acceptance certificate archive task-evidence-certificate-archive-1 is certified.'
  ],
  downloadActions: [
    'Download final demo acceptance summary.',
    'Download launch acceptance certificate archive launch-certificate-archive-1.',
    'Download task evidence acceptance certificate archive task-evidence-certificate-archive-1.'
  ],
  sideEffectContract:
    'GET /api/demo/acceptance-summary is read-only: it does not create tasks, call the model, run tests, archive records, mutate Git, send messages, record receipts, or write to GitHub.',
  markdownReport: '# PatchPilot Final Demo Acceptance Summary'
};

const sharePackage: DemoFinalAcceptanceSharePackage = {
  status: 'READY',
  sendReady: true,
  summary: 'PatchPilot final demo acceptance package is ready to send.',
  nextAction: 'Send the prepared final acceptance message with all required attachments.',
  launchCertificateArchiveId: 'launch-certificate-archive-1',
  taskCertificateArchiveId: 'task-evidence-certificate-archive-1',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  recommendedRecipients: ['Repository owner or maintainer', 'Demo reviewer'],
  requiredAttachments: [
    'Final demo acceptance summary report',
    'Launch acceptance certificate archive launch-certificate-archive-1',
    'Task evidence acceptance certificate archive task-evidence-certificate-archive-1'
  ],
  preSendChecks: [
    'Confirm final demo acceptance status is READY and accepted.',
    'Confirm Pull Request https://github.com/bingqin2/PatchPilot/pull/42 opens correctly.'
  ],
  messageSubject: 'PatchPilot final demo acceptance: task-1',
  messageBody: 'PatchPilot final demo acceptance is ready for external review.',
  evidenceNotes: ['Final acceptance status is READY.'],
  sideEffectContract:
    'GET /api/demo/final-acceptance-share-package is read-only: it does not create tasks, call the model, run tests, mutate Git, archive records, record receipts, send messages, or write to GitHub.',
  markdownReport: '# PatchPilot Final Demo Acceptance Share Package',
  generatedAt: '2026-06-28T15:00:00Z'
};

const sharePackageArchive: DemoFinalAcceptanceSharePackageArchive = {
  id: 'final-acceptance-share-package-archive-1',
  status: 'READY',
  sendReady: true,
  summary: 'PatchPilot final demo acceptance package is ready to send.',
  nextAction: 'Send the prepared final acceptance message with all required attachments.',
  launchCertificateArchiveId: 'launch-certificate-archive-1',
  taskCertificateArchiveId: 'task-evidence-certificate-archive-1',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  recommendedRecipients: ['Repository owner or maintainer', 'Demo reviewer'],
  requiredAttachments: ['Final demo acceptance summary report'],
  preSendChecks: ['Confirm final demo acceptance status is READY and accepted.'],
  messageSubject: 'PatchPilot final demo acceptance: task-1',
  messageBody: 'PatchPilot final demo acceptance is ready for external review.',
  evidenceNotes: ['Final acceptance status is READY.'],
  sideEffectContract:
    'POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.',
  report: '# PatchPilot Final Demo Acceptance Share Package',
  generatedAt: '2026-06-29T01:30:00Z',
  archivedAt: '2026-06-29T02:00:00Z'
};

const shareDeliveryReceipt: DemoFinalAcceptanceShareDeliveryReceipt = {
  id: 'final-acceptance-delivery-receipt-1',
  status: 'READY',
  finalAcceptanceSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
  latestTaskId: 'task-1',
  deliveryChannel: 'email',
  deliveryTarget: 'reviewer@example.com',
  operator: 'local-operator',
  notes: 'Sent final acceptance share package to the reviewer.',
  messageSubject: 'PatchPilot final demo acceptance: task-1',
  deliveredAt: '2026-06-29T03:05:00Z',
  createdAt: '2026-06-29T03:10:00Z',
  markdownReport: '# PatchPilot Final Demo Acceptance Share Delivery Receipt'
};

const shareFinalization: DemoFinalAcceptanceShareFinalization = {
  status: 'READY',
  finalized: true,
  summary: 'Final demo acceptance share package is finalized with a fresh delivery receipt.',
  nextAction: 'Use the finalization report as the external-review acceptance delivery record.',
  latestArchiveId: 'final-acceptance-share-package-archive-1',
  latestTaskId: 'task-1',
  latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  latestDeliveredAt: '2026-06-29T03:05:00Z',
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current final acceptance share package archive.',
  checks: [
    {
      name: 'Final acceptance package archive',
      status: 'READY',
      summary: 'Latest final acceptance share package archive is send-ready.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Delivery receipt freshness',
      status: 'READY',
      summary: 'Latest delivery receipt matches the current final acceptance share package archive.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: [
    'Latest final acceptance archive final-acceptance-share-package-archive-1 is send-ready.',
    'Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1.'
  ],
  markdownReport: '# PatchPilot Final Demo Acceptance Share Finalization Gate',
  generatedAt: '2026-06-29T03:30:00Z'
};

const completionArchive: DemoFinalAcceptanceCompletionArchive = {
  id: 'final-acceptance-completion-archive-1',
  status: 'READY',
  finalized: true,
  summary: 'Final demo acceptance share package is finalized with a fresh delivery receipt.',
  nextAction: 'Use the finalization report as the external-review acceptance delivery record.',
  latestArchiveId: 'final-acceptance-share-package-archive-1',
  latestTaskId: 'task-1',
  latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  latestDeliveredAt: '2026-06-29T03:05:00Z',
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current final acceptance share package archive.',
  evidenceNotes: ['Latest final acceptance share package archive is send-ready.'],
  report: '# PatchPilot Final Demo Acceptance Share Finalization Gate',
  generatedAt: '2026-06-29T03:30:00Z',
  archivedAt: '2026-06-29T04:00:00Z'
};

const completionEvidenceBundle: DemoFinalAcceptanceCompletionEvidenceBundle = {
  status: 'READY',
  readyToShare: true,
  summary: 'PatchPilot final acceptance completion evidence bundle is ready to share.',
  nextAction: 'Share this final acceptance completion evidence bundle with reviewers.',
  latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
  latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
  latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  latestTaskId: 'task-1',
  completionArchiveCount: 1,
  latestArchivedAt: '2026-06-29T04:00:00Z',
  generatedAt: '2026-06-29T04:05:00Z',
  evidenceNotes: [
    'Latest completion archive final-acceptance-completion-archive-1 is finalized.',
    'Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1.'
  ],
  downloadActions: [
    'Download final acceptance completion evidence bundle.',
    'Download final acceptance completion archive final-acceptance-completion-archive-1.'
  ],
  sideEffectContract:
    'GET /api/demo/final-acceptance-completion-evidence-bundle is read-only: it does not create tasks, call the model, run tests, archive records, record receipts, mutate Git, send messages, or write to GitHub.',
  markdownReport: '# PatchPilot Final Acceptance Completion Evidence Bundle'
};

const completionEvidenceDeliveryReceipt: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt = {
  id: 'final-acceptance-completion-evidence-delivery-receipt-1',
  status: 'READY',
  readyToShare: true,
  completionEvidenceBundleStatus: 'READY',
  summary: 'PatchPilot final acceptance completion evidence bundle is ready to share.',
  nextAction: 'Share this final acceptance completion evidence bundle with reviewers.',
  latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
  latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
  latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
  latestTaskId: 'task-1',
  deliveryChannel: 'email',
  deliveryTarget: 'reviewer@example.com',
  operator: 'local-operator',
  notes: 'Sent final completion evidence bundle to the reviewer.',
  deliveredAt: '2026-06-29T04:25:00Z',
  createdAt: '2026-06-29T04:30:00Z',
  markdownReport: '# PatchPilot Final Acceptance Completion Evidence Delivery Receipt'
};

const completionEvidenceDeliveryFinalization: DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization = {
  status: 'READY',
  finalized: true,
  summary: 'Final acceptance completion evidence delivery is finalized with a fresh delivery receipt.',
  nextAction: 'Use the finalization report as the reviewer-facing completion delivery record.',
  latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
  latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
  latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
  latestTaskId: 'task-1',
  latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  latestDeliveredAt: '2026-06-29T04:25:00Z',
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary:
    'Latest completion evidence delivery receipt matches the current completion evidence bundle.',
  checks: [
    {
      name: 'Completion evidence bundle',
      status: 'READY',
      summary: 'Completion evidence bundle is ready to share.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Completion evidence delivery receipt',
      status: 'READY',
      summary: 'Latest completion evidence delivery receipt matches the current completion evidence bundle.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: [
    'Completion evidence bundle final-acceptance-completion-archive-1 is ready to share.',
    'Completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1 is fresh.'
  ],
  downloadActions: [
    'Download final acceptance completion evidence delivery finalization report.',
    'Download final acceptance completion evidence bundle.'
  ],
  sideEffectContract:
    'GET /api/demo/final-acceptance-completion-evidence-delivery-finalization is read-only: it does not create tasks, call the model, run tests, archive records, record receipts, mutate Git, send messages, or write to GitHub.',
  markdownReport: '# PatchPilot Final Acceptance Completion Evidence Delivery Finalization',
  generatedAt: '2026-06-29T05:00:00Z'
};

test('shows final demo acceptance status and certificate evidence', () => {
  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      completionEvidenceDeliveryFinalization={completionEvidenceDeliveryFinalization}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      completionEvidenceDeliveryFinalizationError={null}
      onDownloadReport={vi.fn()}
      onDownloadSharePackageReport={vi.fn()}
      onArchiveSharePackage={vi.fn()}
      onDownloadSharePackageArchiveReport={vi.fn()}
      onCreateShareDeliveryReceipt={vi.fn()}
      onDownloadShareDeliveryReceiptReport={vi.fn()}
      onDownloadShareFinalizationReport={vi.fn()}
      onDownloadCompletionEvidenceBundleReport={vi.fn()}
      onArchiveCompletion={vi.fn()}
      onDownloadCompletionArchiveReport={vi.fn()}
      onCreateCompletionEvidenceDeliveryReceipt={vi.fn()}
      onDownloadCompletionEvidenceDeliveryReceiptReport={vi.fn()}
      onDownloadCompletionEvidenceDeliveryFinalizationReport={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Final demo acceptance' });
  expect(within(panel).getByRole('heading', { name: 'Final demo acceptance' })).toBeInTheDocument();
  expect(within(panel).getByText('Accepted')).toBeInTheDocument();
  expect(within(panel).getAllByText('PatchPilot final demo acceptance is ready for external review.')).not.toHaveLength(0);
  expect(within(panel).getAllByText('launch-certificate-archive-1')).not.toHaveLength(0);
  expect(within(panel).getAllByText('task-evidence-certificate-archive-1')).not.toHaveLength(0);
  expect(within(panel).getByText('Latest task evidence acceptance certificate archive is certified.')).toBeInTheDocument();
  expect(within(panel).getAllByText(/does not create tasks/)).not.toHaveLength(0);
  expect(within(panel).getByRole('heading', { name: 'Final acceptance share package' })).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot final demo acceptance package is ready to send.')).toBeInTheDocument();
  expect(within(panel).getAllByText('PatchPilot final demo acceptance: task-1')).not.toHaveLength(0);
  expect(within(panel).getByText('Final demo acceptance summary report')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Archived final acceptance packages' })).toBeInTheDocument();
  expect(within(panel).getByText('final-acceptance-share-package-archive-1')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Final acceptance delivery finalization' })).toBeInTheDocument();
  expect(within(panel).getByText('Final demo acceptance share package is finalized with a fresh delivery receipt.')).toBeInTheDocument();
  expect(within(panel).getAllByText('final-acceptance-delivery-receipt-1')).not.toHaveLength(0);
  expect(within(panel).getByRole('heading', { name: 'Archived final acceptance completions' })).toBeInTheDocument();
  expect(within(panel).getAllByText('final-acceptance-completion-archive-1')).not.toHaveLength(0);
  expect(within(panel).getByRole('heading', {
    name: 'Final acceptance completion delivery finalization'
  })).toBeInTheDocument();
  expect(within(panel).getByText(
    'Final acceptance completion evidence delivery is finalized with a fresh delivery receipt.'
  )).toBeInTheDocument();
  expect(within(panel).getAllByText(
    'Latest completion evidence delivery receipt matches the current completion evidence bundle.'
  )).not.toHaveLength(0);
});

test('downloads the final demo acceptance markdown report', async () => {
  const user = userEvent.setup();
  const downloadReport = vi.fn(async () => new Blob(['# PatchPilot Final Demo Acceptance Summary'], {
    type: 'text/markdown'
  }));
  const createObjectUrl = vi.fn(() => 'blob:final-demo-acceptance');
  const revokeObjectUrl = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', {
    createObjectURL: createObjectUrl,
    revokeObjectURL: revokeObjectUrl
  });

  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      onDownloadReport={downloadReport}
      onDownloadSharePackageReport={vi.fn()}
      onArchiveSharePackage={vi.fn()}
      onDownloadSharePackageArchiveReport={vi.fn()}
      onCreateShareDeliveryReceipt={vi.fn()}
      onDownloadShareDeliveryReceiptReport={vi.fn()}
      onDownloadShareFinalizationReport={vi.fn()}
      onDownloadCompletionEvidenceBundleReport={vi.fn()}
      onArchiveCompletion={vi.fn()}
      onDownloadCompletionArchiveReport={vi.fn()}
      onCreateCompletionEvidenceDeliveryReceipt={vi.fn()}
      onDownloadCompletionEvidenceDeliveryReceiptReport={vi.fn()}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Download final acceptance report' }));

  expect(downloadReport).toHaveBeenCalled();
  expect(createObjectUrl).toHaveBeenCalled();
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-demo-acceptance');
  expect(screen.getByText('Final demo acceptance report downloaded')).toBeInTheDocument();
});

test('copies and downloads the final acceptance share package', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn();
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });
  const downloadSharePackageReport = vi.fn(async () => new Blob(['# PatchPilot Final Demo Acceptance Share Package'], {
    type: 'text/markdown'
  }));
  const createObjectUrl = vi.fn(() => 'blob:final-acceptance-share-package');
  const revokeObjectUrl = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', {
    createObjectURL: createObjectUrl,
    revokeObjectURL: revokeObjectUrl
  });

  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      onDownloadReport={vi.fn()}
      onDownloadSharePackageReport={downloadSharePackageReport}
      onArchiveSharePackage={vi.fn()}
      onDownloadSharePackageArchiveReport={vi.fn()}
      onCreateShareDeliveryReceipt={vi.fn()}
      onDownloadShareDeliveryReceiptReport={vi.fn()}
      onDownloadShareFinalizationReport={vi.fn()}
      onDownloadCompletionEvidenceBundleReport={vi.fn()}
      onArchiveCompletion={vi.fn()}
      onDownloadCompletionArchiveReport={vi.fn()}
      onCreateCompletionEvidenceDeliveryReceipt={vi.fn()}
      onDownloadCompletionEvidenceDeliveryReceiptReport={vi.fn()}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Copy final acceptance share package' }));
  await user.click(screen.getByRole('button', { name: 'Download final acceptance share package' }));

  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('Subject: PatchPilot final demo acceptance: task-1'));
  expect(downloadSharePackageReport).toHaveBeenCalled();
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-acceptance-share-package');
  expect(screen.getByText('Final acceptance share package copied')).toBeInTheDocument();
  expect(screen.getByText('Final acceptance share package downloaded')).toBeInTheDocument();
});

test('archives and downloads final acceptance share package archives', async () => {
  const user = userEvent.setup();
  const archiveSharePackage = vi.fn(async () => sharePackageArchive);
  const downloadArchiveReport = vi.fn(async () => new Blob(['# Archived Final Acceptance Package'], {
    type: 'text/markdown'
  }));
  const createObjectUrl = vi.fn(() => 'blob:final-acceptance-share-package-archive');
  const revokeObjectUrl = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', {
    createObjectURL: createObjectUrl,
    revokeObjectURL: revokeObjectUrl
  });

  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      onDownloadReport={vi.fn()}
      onDownloadSharePackageReport={vi.fn()}
      onArchiveSharePackage={archiveSharePackage}
      onDownloadSharePackageArchiveReport={downloadArchiveReport}
      onCreateShareDeliveryReceipt={vi.fn()}
      onDownloadShareDeliveryReceiptReport={vi.fn()}
      onDownloadShareFinalizationReport={vi.fn()}
      onDownloadCompletionEvidenceBundleReport={vi.fn()}
      onArchiveCompletion={vi.fn()}
      onDownloadCompletionArchiveReport={vi.fn()}
      onCreateCompletionEvidenceDeliveryReceipt={vi.fn()}
      onDownloadCompletionEvidenceDeliveryReceiptReport={vi.fn()}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Archive final acceptance share package' }));
  await user.click(screen.getByRole('button', { name: 'Download archived final acceptance package final-acceptance-share-package-archive-1' }));

  expect(archiveSharePackage).toHaveBeenCalled();
  expect(downloadArchiveReport).toHaveBeenCalledWith('final-acceptance-share-package-archive-1');
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-acceptance-share-package-archive');
  expect(screen.getByText('Final acceptance share package archived')).toBeInTheDocument();
  expect(screen.getByText('Archived final acceptance package downloaded')).toBeInTheDocument();
});

test('records final acceptance delivery receipt and downloads finalization evidence', async () => {
  const user = userEvent.setup();
  const createReceipt = vi.fn(async () => shareDeliveryReceipt);
  const downloadReceiptReport = vi.fn(async () => new Blob(['# Receipt'], { type: 'text/markdown' }));
  const downloadFinalizationReport = vi.fn(async () => new Blob(['# Finalization'], { type: 'text/markdown' }));
  const createObjectUrl = vi.fn(() => 'blob:final-acceptance-delivery');
  const revokeObjectUrl = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', {
    createObjectURL: createObjectUrl,
    revokeObjectURL: revokeObjectUrl
  });

  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      onDownloadReport={vi.fn()}
      onDownloadSharePackageReport={vi.fn()}
      onArchiveSharePackage={vi.fn()}
      onDownloadSharePackageArchiveReport={vi.fn()}
      onCreateShareDeliveryReceipt={createReceipt}
      onDownloadShareDeliveryReceiptReport={downloadReceiptReport}
      onDownloadShareFinalizationReport={downloadFinalizationReport}
      onDownloadCompletionEvidenceBundleReport={vi.fn()}
      onArchiveCompletion={vi.fn()}
      onDownloadCompletionArchiveReport={vi.fn()}
      onCreateCompletionEvidenceDeliveryReceipt={vi.fn()}
      onDownloadCompletionEvidenceDeliveryReceiptReport={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Final demo acceptance' });
  await user.selectOptions(within(panel).getByLabelText('Delivery channel'), 'email');
  await user.type(within(panel).getByLabelText('Delivery target'), 'reviewer@example.com');
  await user.type(within(panel).getByLabelText('Operator'), 'local-operator');
  await user.type(within(panel).getByLabelText('Delivery notes'), 'Sent final acceptance share package to the reviewer.');
  await user.click(within(panel).getByRole('button', { name: 'Record final acceptance delivery receipt' }));

  expect(createReceipt).toHaveBeenCalledWith({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'local-operator',
    notes: 'Sent final acceptance share package to the reviewer.'
  });
  expect(await within(panel).findByText('Final acceptance delivery receipt recorded')).toBeInTheDocument();

  await user.click(within(panel).getByRole('button', { name: 'Download final acceptance finalization report' }));
  await user.click(within(panel).getByRole('button', {
    name: 'Download final acceptance delivery receipt final-acceptance-delivery-receipt-1'
  }));

  expect(downloadFinalizationReport).toHaveBeenCalledTimes(1);
  expect(downloadReceiptReport).toHaveBeenCalledWith('final-acceptance-delivery-receipt-1');
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-acceptance-delivery');
});

test('archives and downloads final acceptance completion evidence', async () => {
  const user = userEvent.setup();
  const archiveCompletion = vi.fn(async () => completionArchive);
  const downloadCompletionArchiveReport = vi.fn(async () => new Blob(['# Completion'], { type: 'text/markdown' }));
  const createObjectUrl = vi.fn(() => 'blob:final-acceptance-completion');
  const revokeObjectUrl = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', {
    createObjectURL: createObjectUrl,
    revokeObjectURL: revokeObjectUrl
  });

  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      onDownloadReport={vi.fn()}
      onDownloadSharePackageReport={vi.fn()}
      onArchiveSharePackage={vi.fn()}
      onDownloadSharePackageArchiveReport={vi.fn()}
      onCreateShareDeliveryReceipt={vi.fn()}
      onDownloadShareDeliveryReceiptReport={vi.fn()}
      onDownloadShareFinalizationReport={vi.fn()}
      onDownloadCompletionEvidenceBundleReport={vi.fn()}
      onArchiveCompletion={archiveCompletion}
      onDownloadCompletionArchiveReport={downloadCompletionArchiveReport}
      onCreateCompletionEvidenceDeliveryReceipt={vi.fn()}
      onDownloadCompletionEvidenceDeliveryReceiptReport={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Final demo acceptance' });
  await user.click(within(panel).getByRole('button', { name: 'Archive final acceptance completion' }));
  await user.click(within(panel).getByRole('button', {
    name: 'Download final acceptance completion final-acceptance-completion-archive-1'
  }));

  expect(archiveCompletion).toHaveBeenCalledTimes(1);
  expect(downloadCompletionArchiveReport).toHaveBeenCalledWith('final-acceptance-completion-archive-1');
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-acceptance-completion');
  expect(await within(panel).findByText('Final acceptance completion archived')).toBeInTheDocument();
  expect(within(panel).getByText('Final acceptance completion archive downloaded')).toBeInTheDocument();
});

test('shows and downloads final acceptance completion evidence bundle', async () => {
  const user = userEvent.setup();
  const downloadCompletionEvidenceBundleReport = vi.fn(async () => new Blob(['# Completion Evidence Bundle'], {
    type: 'text/markdown'
  }));
  const createObjectUrl = vi.fn(() => 'blob:final-acceptance-completion-evidence-bundle');
  const revokeObjectUrl = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', {
    createObjectURL: createObjectUrl,
    revokeObjectURL: revokeObjectUrl
  });

  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      onDownloadReport={vi.fn()}
      onDownloadSharePackageReport={vi.fn()}
      onArchiveSharePackage={vi.fn()}
      onDownloadSharePackageArchiveReport={vi.fn()}
      onCreateShareDeliveryReceipt={vi.fn()}
      onDownloadShareDeliveryReceiptReport={vi.fn()}
      onDownloadShareFinalizationReport={vi.fn()}
      onDownloadCompletionEvidenceBundleReport={downloadCompletionEvidenceBundleReport}
      onArchiveCompletion={vi.fn()}
      onDownloadCompletionArchiveReport={vi.fn()}
      onCreateCompletionEvidenceDeliveryReceipt={vi.fn()}
      onDownloadCompletionEvidenceDeliveryReceiptReport={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Final demo acceptance' });
  expect(within(panel).getByRole('heading', { name: 'Final acceptance completion evidence bundle' })).toBeInTheDocument();
  expect(
    within(panel).getAllByText('PatchPilot final acceptance completion evidence bundle is ready to share.')
  ).not.toHaveLength(0);
  expect(within(panel).getByText('Download final acceptance completion evidence bundle.')).toBeInTheDocument();

  await user.click(within(panel).getByRole('button', {
    name: 'Download final acceptance completion evidence bundle'
  }));

  expect(downloadCompletionEvidenceBundleReport).toHaveBeenCalledTimes(1);
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-acceptance-completion-evidence-bundle');
  expect(await within(panel).findByText('Final acceptance completion evidence bundle downloaded')).toBeInTheDocument();
});

test('shows and downloads final acceptance completion delivery finalization', async () => {
  const user = userEvent.setup();
  const downloadFinalizationReport = vi.fn(async () => new Blob([
    '# PatchPilot Final Acceptance Completion Evidence Delivery Finalization'
  ], {
    type: 'text/markdown'
  }));
  const createObjectUrl = vi.fn(() => 'blob:final-acceptance-completion-delivery-finalization');
  const revokeObjectUrl = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', {
    createObjectURL: createObjectUrl,
    revokeObjectURL: revokeObjectUrl
  });

  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      completionEvidenceDeliveryFinalization={completionEvidenceDeliveryFinalization}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      completionEvidenceDeliveryFinalizationError={null}
      onDownloadReport={vi.fn()}
      onDownloadSharePackageReport={vi.fn()}
      onArchiveSharePackage={vi.fn()}
      onDownloadSharePackageArchiveReport={vi.fn()}
      onCreateShareDeliveryReceipt={vi.fn()}
      onDownloadShareDeliveryReceiptReport={vi.fn()}
      onDownloadShareFinalizationReport={vi.fn()}
      onDownloadCompletionEvidenceBundleReport={vi.fn()}
      onArchiveCompletion={vi.fn()}
      onDownloadCompletionArchiveReport={vi.fn()}
      onCreateCompletionEvidenceDeliveryReceipt={vi.fn()}
      onDownloadCompletionEvidenceDeliveryReceiptReport={vi.fn()}
      onDownloadCompletionEvidenceDeliveryFinalizationReport={downloadFinalizationReport}
    />
  );

  const panel = screen.getByRole('region', { name: 'Final demo acceptance' });
  expect(within(panel).getByRole('heading', {
    name: 'Final acceptance completion delivery finalization'
  })).toBeInTheDocument();
  expect(within(panel).getAllByText('FRESH')).not.toHaveLength(0);
  expect(within(panel).getByText(
    'Completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1 is fresh.'
  )).toBeInTheDocument();

  await user.click(within(panel).getByRole('button', {
    name: 'Download final acceptance completion delivery finalization report'
  }));

  expect(downloadFinalizationReport).toHaveBeenCalledTimes(1);
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-acceptance-completion-delivery-finalization');
  expect(await within(panel).findByText(
    'Final acceptance completion delivery finalization report downloaded'
  )).toBeInTheDocument();
});

test('records and downloads final acceptance completion evidence delivery receipts', async () => {
  const user = userEvent.setup();
  const createCompletionEvidenceDeliveryReceipt = vi.fn(async () => completionEvidenceDeliveryReceipt);
  const downloadCompletionEvidenceDeliveryReceiptReport = vi.fn(async () => new Blob(['# Completion Evidence Receipt'], {
    type: 'text/markdown'
  }));
  const createObjectUrl = vi.fn(() => 'blob:final-acceptance-completion-evidence-delivery');
  const revokeObjectUrl = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', {
    createObjectURL: createObjectUrl,
    revokeObjectURL: revokeObjectUrl
  });

  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      sharePackage={sharePackage}
      sharePackageArchives={[sharePackageArchive]}
      shareDeliveryReceipts={[shareDeliveryReceipt]}
      shareFinalization={shareFinalization}
      completionEvidenceBundle={completionEvidenceBundle}
      completionArchives={[completionArchive]}
      completionEvidenceDeliveryReceipts={[completionEvidenceDeliveryReceipt]}
      error={null}
      sharePackageError={null}
      sharePackageArchiveError={null}
      shareDeliveryReceiptError={null}
      shareFinalizationError={null}
      completionEvidenceBundleError={null}
      completionArchiveError={null}
      completionEvidenceDeliveryReceiptError={null}
      onDownloadReport={vi.fn()}
      onDownloadSharePackageReport={vi.fn()}
      onArchiveSharePackage={vi.fn()}
      onDownloadSharePackageArchiveReport={vi.fn()}
      onCreateShareDeliveryReceipt={vi.fn()}
      onDownloadShareDeliveryReceiptReport={vi.fn()}
      onDownloadShareFinalizationReport={vi.fn()}
      onDownloadCompletionEvidenceBundleReport={vi.fn()}
      onArchiveCompletion={vi.fn()}
      onDownloadCompletionArchiveReport={vi.fn()}
      onCreateCompletionEvidenceDeliveryReceipt={createCompletionEvidenceDeliveryReceipt}
      onDownloadCompletionEvidenceDeliveryReceiptReport={downloadCompletionEvidenceDeliveryReceiptReport}
    />
  );

  const panel = screen.getByRole('region', { name: 'Final demo acceptance' });
  expect(within(panel).getByRole('heading', {
    name: 'Final acceptance completion evidence delivery receipts'
  })).toBeInTheDocument();
  expect(within(panel).getAllByText('final-acceptance-completion-evidence-delivery-receipt-1')).not.toHaveLength(0);

  await user.selectOptions(within(panel).getByLabelText('Completion evidence delivery channel'), 'email');
  await user.type(within(panel).getByLabelText('Completion evidence delivery target'), 'reviewer@example.com');
  await user.type(within(panel).getByLabelText('Completion evidence operator'), 'local-operator');
  await user.type(
    within(panel).getByLabelText('Completion evidence delivery notes'),
    'Sent final completion evidence bundle to the reviewer.'
  );
  await user.click(within(panel).getByRole('button', {
    name: 'Record final acceptance completion evidence delivery receipt'
  }));

  expect(createCompletionEvidenceDeliveryReceipt).toHaveBeenCalledWith({
    deliveryChannel: 'email',
    deliveryTarget: 'reviewer@example.com',
    operator: 'local-operator',
    notes: 'Sent final completion evidence bundle to the reviewer.'
  });
  expect(await within(panel).findByText('Final acceptance completion evidence delivery receipt recorded')).toBeInTheDocument();

  await user.click(within(panel).getByRole('button', {
    name: 'Download final acceptance completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1'
  }));

  expect(downloadCompletionEvidenceDeliveryReceiptReport).toHaveBeenCalledWith(
    'final-acceptance-completion-evidence-delivery-receipt-1'
  );
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-acceptance-completion-evidence-delivery');
  expect(within(panel).getByText(
    'Final acceptance completion evidence delivery receipt final-acceptance-completion-evidence-delivery-receipt-1 downloaded'
  )).toBeInTheDocument();
});
