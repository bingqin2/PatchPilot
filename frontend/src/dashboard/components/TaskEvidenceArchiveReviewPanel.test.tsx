import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  FixTaskEvidencePackageArchive,
  FixTaskEvidencePackageArchiveShareCenter,
  FixTaskEvidencePackageArchiveSummary
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

test('shows global task evidence archive summary and recent archives', () => {
  render(
    <TaskEvidenceArchiveReviewPanel
      summary={summary}
      shareCenter={shareCenter}
      archives={archives}
      error={null}
      shareCenterError={null}
      onDownloadArchiveReport={vi.fn()}
      onDownloadShareCenterReport={vi.fn()}
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
  expect(within(panel).getByText('Task evidence archive review')).toBeInTheDocument();
  expect(within(panel).getByText('2 archived reports')).toBeInTheDocument();
  expect(within(panel).getByText('Completed')).toBeInTheDocument();
  expect(within(panel).getByText('Failed')).toBeInTheDocument();
  expect(within(panel).getAllByText('1')).toHaveLength(2);
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
      archives={archives}
      error={null}
      shareCenterError={null}
      onDownloadArchiveReport={download}
      onDownloadShareCenterReport={vi.fn()}
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
      archives={archives}
      error={null}
      shareCenterError={null}
      onDownloadArchiveReport={vi.fn()}
      onDownloadShareCenterReport={downloadShareCenterReport}
      onSelectTask={vi.fn()}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Download task evidence share center report' }));

  expect(downloadShareCenterReport).toHaveBeenCalled();
  expect(anchorClick).toHaveBeenCalled();
  expect(screen.getByText('Task evidence share center report downloaded')).toBeInTheDocument();
});
