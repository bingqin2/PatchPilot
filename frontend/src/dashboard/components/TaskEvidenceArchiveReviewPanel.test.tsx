import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  FixTaskEvidencePackageArchive,
  FixTaskEvidencePackageAcceptanceCloseoutArchive,
  FixTaskEvidencePackageFinalization,
  FixTaskEvidencePackageArchiveShareCenter,
  FixTaskEvidencePackageArchiveSummary,
  FixTaskEvidencePackageShareDeliveryReceipt
} from '../../types';
import { TaskEvidenceArchiveReviewPanel } from './TaskEvidenceArchiveReviewPanel';

const summary: FixTaskEvidencePackageArchiveSummary = {
  totalArchiveCount: 2,
  completedArchiveCount: 1,
  failedArchiveCount: 1,
  pendingReviewArchiveCount: 0,
  cancelledArchiveCount: 0,
  latestArchiveId: 'task-evidence-archive-2',
  latestTaskId: 'task-2',
  latestRepositoryOwner: 'bingqin2',
  latestRepositoryName: 'PatchPilot',
  latestIssueNumber: 2,
  latestArchivedAt: '2026-06-20T01:10:00Z',
  sideEffectContract:
    'Task evidence archive review reads PatchPilot-local archived reports only; it does not create tasks, call the model, run verification commands, mutate Git, push, open Pull Requests, or write GitHub comments.',
  nextAction: 'Download the latest archived task evidence report or open the related task before sharing.'
};

const archives: FixTaskEvidencePackageArchive[] = [
  {
    id: 'task-evidence-archive-2',
    taskId: 'task-2',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 2,
    status: 'FAILED',
    pullRequestUrl: null,
    archivedAt: '2026-06-20T01:10:00Z',
    summary: 'Task FAILED for bingqin2/PatchPilot#2 archived as evidence.',
    report: '# PatchPilot Task Report\n\n- Task: `task-2`'
  },
  {
    id: 'task-evidence-archive-1',
    taskId: 'task-1',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    status: 'COMPLETED',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
    archivedAt: '2026-06-20T01:05:00Z',
    summary: 'Task COMPLETED for bingqin2/PatchPilot#1 archived as evidence.',
    report: '# PatchPilot Task Report\n\n- Task: `task-1`'
  }
];

const shareCenter: FixTaskEvidencePackageArchiveShareCenter = {
  status: 'READY',
  shareReady: true,
  summary: 'A shareable completed task evidence package is available for external review.',
  nextAction: 'Download archived task evidence task-evidence-archive-1 before sharing.',
  archiveCount: 2,
  completedArchiveCount: 1,
  failedArchiveCount: 1,
  pendingReviewArchiveCount: 0,
  cancelledArchiveCount: 0,
  latestArchiveId: 'task-evidence-archive-2',
  latestTaskId: 'task-2',
  latestRepositoryOwner: 'bingqin2',
  latestRepositoryName: 'PatchPilot',
  latestIssueNumber: 2,
  latestArchivedAt: '2026-06-20T01:10:00Z',
  shareableArchiveId: 'task-evidence-archive-1',
  shareableTaskId: 'task-1',
  shareableRepositoryOwner: 'bingqin2',
  shareableRepositoryName: 'PatchPilot',
  shareableIssueNumber: 1,
  shareablePullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  downloadActions: [
    'Download archived task evidence task-evidence-archive-1.',
    'Open Pull Request https://github.com/bingqin2/PatchPilot/pull/8.'
  ],
  evidenceNotes: [
    'Latest archive task-evidence-archive-2 is FAILED.',
    'Shareable archive task-evidence-archive-1 completed with a Pull Request.'
  ],
  sideEffectContract:
    'Task evidence share center is read-only; it does not create tasks, mutate Git, push, open Pull Requests, or write GitHub comments.',
  markdownReport: '# PatchPilot Task Evidence Share Center',
  generatedAt: '2026-06-20T01:12:00Z'
};

const finalization: FixTaskEvidencePackageFinalization = {
  status: 'READY',
  finalized: true,
  summary: 'Task evidence is finalized with a fresh delivery receipt for the current shareable archive.',
  nextAction: 'Use the finalization report as the accepted task evidence delivery record.',
  latestArchiveId: 'task-evidence-archive-1',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  latestDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  latestDeliveredAt: '2026-06-28T06:05:00Z',
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current task evidence archive and task.',
  checks: [
    {
      name: 'Task evidence share readiness',
      status: 'READY',
      summary: 'A shareable completed task evidence package is available for external review.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Delivery receipt freshness',
      status: 'READY',
      summary: 'Latest delivery receipt matches the current task evidence archive and task.',
      nextAction: 'No action needed.'
    }
  ],
  evidenceNotes: [
    'Latest delivery receipt task-evidence-delivery-receipt-1 is fresh for task-evidence-archive-1/task-1.',
    'Finalization report can be downloaded as the accepted task evidence delivery record.'
  ],
  markdownReport: '# PatchPilot Task Evidence Finalization Gate',
  generatedAt: '2026-06-28T06:30:00Z'
};

const deliveryReceipt: FixTaskEvidencePackageShareDeliveryReceipt = {
  id: 'task-evidence-delivery-receipt-1',
  status: 'READY',
  taskEvidenceArchiveId: 'task-evidence-archive-1',
  taskId: 'task-1',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 1,
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  deliveryChannel: 'email',
  deliveryTarget: 'reviewer@example.com',
  operator: 'local-operator',
  notes: 'Sent task evidence after PR review.',
  messageSubject: 'PatchPilot task evidence: task-1',
  deliveredAt: '2026-06-28T06:05:00Z',
  createdAt: '2026-06-28T06:10:00Z',
  markdownReport: '# PatchPilot Task Evidence Delivery Receipt'
};

const closeoutArchive: FixTaskEvidencePackageAcceptanceCloseoutArchive = {
  id: 'task-evidence-closeout-archive-1',
  status: 'READY',
  accepted: true,
  summary: 'Task evidence is finalized with a fresh delivery receipt for the current shareable archive.',
  latestArchiveId: 'task-evidence-archive-1',
  latestTaskId: 'task-1',
  latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  latestDeliveryReceiptId: 'task-evidence-delivery-receipt-1',
  latestDeliveryTarget: 'reviewer@example.com',
  latestDeliveryChannel: 'email',
  deliveryReceiptFreshness: 'FRESH',
  createdAt: '2026-06-28T07:00:00Z',
  report: '# PatchPilot Task Evidence Acceptance Closeout Archive'
};

test('shows global task evidence archive summary and recent archives', () => {
  render(
    <TaskEvidenceArchiveReviewPanel
      summary={summary}
      shareCenter={shareCenter}
      finalization={finalization}
      deliveryReceipts={[deliveryReceipt]}
      closeoutArchives={[closeoutArchive]}
      archives={archives}
      error={null}
      shareCenterError={null}
      finalizationError={null}
      deliveryReceiptError={null}
      closeoutArchiveError={null}
      onDownloadArchiveReport={vi.fn()}
      onDownloadShareCenterReport={vi.fn()}
      onDownloadFinalizationReport={vi.fn()}
      onCreateDeliveryReceipt={vi.fn()}
      onDownloadDeliveryReceiptReport={vi.fn()}
      onArchiveAcceptanceCloseout={vi.fn()}
      onDownloadAcceptanceCloseoutArchiveReport={vi.fn()}
      onSelectTask={vi.fn()}
    />
  );

  const panel = screen.getByLabelText('Task evidence archive review');
  const sharePanel = screen.getByLabelText('Task evidence share center');
  expect(within(sharePanel).getByText('Task evidence share center')).toBeInTheDocument();
  expect(within(sharePanel).getByText('Ready')).toBeInTheDocument();
  expect(within(sharePanel).getByText(shareCenter.summary)).toBeInTheDocument();
  expect(within(sharePanel).getByText('task-evidence-archive-1')).toBeInTheDocument();
  expect(within(sharePanel).getByText('https://github.com/bingqin2/PatchPilot/pull/8')).toBeInTheDocument();
  const finalizationPanel = screen.getByLabelText('Task evidence finalization');
  expect(within(finalizationPanel).getByText('Task evidence finalization')).toBeInTheDocument();
  expect(within(finalizationPanel).getByText('Task evidence is finalized with a fresh delivery receipt for the current shareable archive.')).toBeInTheDocument();
  expect(within(finalizationPanel).getByText('task-evidence-delivery-receipt-1')).toBeInTheDocument();
  const receiptPanel = screen.getByLabelText('Task evidence delivery receipts');
  expect(within(receiptPanel).getByText('task-evidence-delivery-receipt-1')).toBeInTheDocument();
  expect(within(receiptPanel).getByText(/reviewer@example.com.*email.*delivered/)).toBeInTheDocument();
  const closeoutPanel = screen.getByLabelText('Task evidence acceptance closeout archives');
  expect(within(closeoutPanel).getByText('Task evidence acceptance closeout archives')).toBeInTheDocument();
  expect(within(closeoutPanel).getByText('task-evidence-closeout-archive-1')).toBeInTheDocument();
  expect(within(closeoutPanel).getByText(/accepted.*task-evidence-archive-1.*task-evidence-delivery-receipt-1/i)).toBeInTheDocument();
  expect(within(panel).getByText('Task evidence archive review')).toBeInTheDocument();
  expect(within(panel).getByText('2 archived reports')).toBeInTheDocument();
  expect(within(panel).getByText('Completed')).toBeInTheDocument();
  expect(within(panel).getByText('Failed')).toBeInTheDocument();
  expect(within(panel).getByText('completed')).toBeInTheDocument();
  expect(within(panel).getByText('failed')).toBeInTheDocument();
  expect(within(panel).getByText('Latest task')).toBeInTheDocument();
  expect(within(panel).getByText('bingqin2/PatchPilot #2')).toBeInTheDocument();
  expect(within(panel).getAllByText('task-evidence-archive-2').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Task COMPLETED for bingqin2/PatchPilot#1 archived as evidence.')).toBeInTheDocument();
  expect(within(panel).getByText(summary.sideEffectContract)).toBeInTheDocument();
});

test('downloads archived evidence and opens related task from review panel', async () => {
  const user = userEvent.setup();
  const download = vi.fn(async () => new Blob(['# Archived report'], { type: 'text/markdown;charset=UTF-8' }));
  const selectTask = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:task-evidence');
  const revokeObjectURL = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });

  render(
    <TaskEvidenceArchiveReviewPanel
      summary={summary}
      shareCenter={shareCenter}
      finalization={finalization}
      deliveryReceipts={[deliveryReceipt]}
      closeoutArchives={[closeoutArchive]}
      archives={archives}
      error={null}
      shareCenterError={null}
      finalizationError={null}
      deliveryReceiptError={null}
      closeoutArchiveError={null}
      onDownloadArchiveReport={download}
      onDownloadShareCenterReport={vi.fn()}
      onDownloadFinalizationReport={vi.fn()}
      onCreateDeliveryReceipt={vi.fn()}
      onDownloadDeliveryReceiptReport={vi.fn()}
      onArchiveAcceptanceCloseout={vi.fn()}
      onDownloadAcceptanceCloseoutArchiveReport={vi.fn()}
      onSelectTask={selectTask}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Open task task-2' }));
  expect(selectTask).toHaveBeenCalledWith('task-2');

  await user.click(screen.getByRole('button', { name: 'Download archived evidence task-evidence-archive-2' }));
  expect(download).toHaveBeenCalledWith('task-evidence-archive-2');
  expect(anchorClick).toHaveBeenCalled();
  expect(screen.getByText('Archived evidence task-evidence-archive-2 downloaded')).toBeInTheDocument();
});

test('downloads task evidence share center report from review panel', async () => {
  const user = userEvent.setup();
  const downloadShareCenterReport = vi.fn(async () => new Blob(['# Share center'], {
    type: 'text/markdown;charset=UTF-8'
  }));
  const createObjectURL = vi.fn(() => 'blob:task-evidence-share-center');
  const revokeObjectURL = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });

  render(
    <TaskEvidenceArchiveReviewPanel
      summary={summary}
      shareCenter={shareCenter}
      finalization={finalization}
      deliveryReceipts={[deliveryReceipt]}
      closeoutArchives={[closeoutArchive]}
      archives={archives}
      error={null}
      shareCenterError={null}
      finalizationError={null}
      deliveryReceiptError={null}
      closeoutArchiveError={null}
      onDownloadArchiveReport={vi.fn()}
      onDownloadShareCenterReport={downloadShareCenterReport}
      onDownloadFinalizationReport={vi.fn()}
      onCreateDeliveryReceipt={vi.fn()}
      onDownloadDeliveryReceiptReport={vi.fn()}
      onArchiveAcceptanceCloseout={vi.fn()}
      onDownloadAcceptanceCloseoutArchiveReport={vi.fn()}
      onSelectTask={vi.fn()}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Download task evidence share center report' }));

  expect(downloadShareCenterReport).toHaveBeenCalled();
  expect(anchorClick).toHaveBeenCalled();
  expect(screen.getByText('Task evidence share center report downloaded')).toBeInTheDocument();
});

test('records delivery receipt and downloads finalization evidence from review panel', async () => {
  const user = userEvent.setup();
  const createReceipt = vi.fn(async () => ({
    ...deliveryReceipt,
    id: 'task-evidence-delivery-receipt-2',
    deliveryTarget: 'ops@example.com',
    notes: 'Shared after review.'
  }));
  const downloadFinalizationReport = vi.fn(async () => new Blob(['# Finalization'], {
    type: 'text/markdown;charset=UTF-8'
  }));
  const downloadReceiptReport = vi.fn(async () => new Blob(['# Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  }));
  const createObjectURL = vi.fn(() => 'blob:task-evidence-finalization');
  const revokeObjectURL = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });

  render(
    <TaskEvidenceArchiveReviewPanel
      summary={summary}
      shareCenter={shareCenter}
      finalization={finalization}
      deliveryReceipts={[deliveryReceipt]}
      closeoutArchives={[closeoutArchive]}
      archives={archives}
      error={null}
      shareCenterError={null}
      finalizationError={null}
      deliveryReceiptError={null}
      closeoutArchiveError={null}
      onDownloadArchiveReport={vi.fn()}
      onDownloadShareCenterReport={vi.fn()}
      onDownloadFinalizationReport={downloadFinalizationReport}
      onCreateDeliveryReceipt={createReceipt}
      onDownloadDeliveryReceiptReport={downloadReceiptReport}
      onArchiveAcceptanceCloseout={vi.fn()}
      onDownloadAcceptanceCloseoutArchiveReport={vi.fn()}
      onSelectTask={vi.fn()}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Download task evidence finalization report' }));
  expect(downloadFinalizationReport).toHaveBeenCalled();
  expect(anchorClick).toHaveBeenCalled();
  expect(screen.getByText('Task evidence finalization report downloaded')).toBeInTheDocument();

  await user.clear(screen.getByLabelText('Task evidence delivery target'));
  await user.type(screen.getByLabelText('Task evidence delivery target'), 'ops@example.com');
  await user.type(screen.getByLabelText('Task evidence delivery notes'), 'Shared after review.');
  await user.click(screen.getByRole('button', { name: 'Record task evidence delivery receipt' }));

  expect(createReceipt).toHaveBeenCalledWith({
    deliveryChannel: 'email',
    deliveryTarget: 'ops@example.com',
    operator: 'local-operator',
    notes: 'Shared after review.'
  });
  expect(screen.getByText('Task evidence delivery receipt task-evidence-delivery-receipt-2 recorded')).toBeInTheDocument();

  await user.click(screen.getByRole('button', {
    name: 'Download task evidence delivery receipt task-evidence-delivery-receipt-1'
  }));
  expect(downloadReceiptReport).toHaveBeenCalledWith('task-evidence-delivery-receipt-1');
});

test('archives and downloads task evidence acceptance closeout from review panel', async () => {
  const user = userEvent.setup();
  const archiveCloseout = vi.fn(async () => ({
    ...closeoutArchive,
    id: 'task-evidence-closeout-archive-2'
  }));
  const downloadCloseoutReport = vi.fn(async () => new Blob(['# Closeout archive'], {
    type: 'text/markdown;charset=UTF-8'
  }));
  const createObjectURL = vi.fn(() => 'blob:task-evidence-closeout');
  const revokeObjectURL = vi.fn();
  const anchorClick = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });

  render(
    <TaskEvidenceArchiveReviewPanel
      summary={summary}
      shareCenter={shareCenter}
      finalization={finalization}
      deliveryReceipts={[deliveryReceipt]}
      closeoutArchives={[closeoutArchive]}
      archives={archives}
      error={null}
      shareCenterError={null}
      finalizationError={null}
      deliveryReceiptError={null}
      closeoutArchiveError={null}
      onDownloadArchiveReport={vi.fn()}
      onDownloadShareCenterReport={vi.fn()}
      onDownloadFinalizationReport={vi.fn()}
      onCreateDeliveryReceipt={vi.fn()}
      onDownloadDeliveryReceiptReport={vi.fn()}
      onArchiveAcceptanceCloseout={archiveCloseout}
      onDownloadAcceptanceCloseoutArchiveReport={downloadCloseoutReport}
      onSelectTask={vi.fn()}
    />
  );

  const closeoutPanel = screen.getByLabelText('Task evidence acceptance closeout archives');
  await user.click(within(closeoutPanel).getByRole('button', { name: 'Archive task evidence acceptance closeout' }));
  expect(archiveCloseout).toHaveBeenCalled();
  expect(screen.getByText('Task evidence acceptance closeout task-evidence-closeout-archive-2 archived')).toBeInTheDocument();

  await user.click(within(closeoutPanel).getByRole('button', {
    name: 'Download task evidence acceptance closeout task-evidence-closeout-archive-1'
  }));
  expect(downloadCloseoutReport).toHaveBeenCalledWith('task-evidence-closeout-archive-1');
  expect(anchorClick).toHaveBeenCalled();
  expect(screen.getByText('Task evidence acceptance closeout task-evidence-closeout-archive-1 downloaded')).toBeInTheDocument();
});
