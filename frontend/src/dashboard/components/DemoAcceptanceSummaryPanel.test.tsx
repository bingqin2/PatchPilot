import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoAcceptanceSummary } from '../../types';
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

test('shows final demo acceptance status and certificate evidence', () => {
  render(
    <DemoAcceptanceSummaryPanel
      summary={summary}
      error={null}
      onDownloadReport={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Final demo acceptance' });
  expect(within(panel).getByRole('heading', { name: 'Final demo acceptance' })).toBeInTheDocument();
  expect(within(panel).getByText('Accepted')).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot final demo acceptance is ready for external review.')).toBeInTheDocument();
  expect(within(panel).getAllByText('launch-certificate-archive-1')).not.toHaveLength(0);
  expect(within(panel).getAllByText('task-evidence-certificate-archive-1')).not.toHaveLength(0);
  expect(within(panel).getByText('Latest task evidence acceptance certificate archive is certified.')).toBeInTheDocument();
  expect(within(panel).getByText(/does not create tasks/)).toBeInTheDocument();
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
      error={null}
      onDownloadReport={downloadReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Download final acceptance report' }));

  expect(downloadReport).toHaveBeenCalled();
  expect(createObjectUrl).toHaveBeenCalled();
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectUrl).toHaveBeenCalledWith('blob:final-demo-acceptance');
  expect(screen.getByText('Final demo acceptance report downloaded')).toBeInTheDocument();
});
