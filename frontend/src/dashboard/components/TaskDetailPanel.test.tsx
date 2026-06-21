import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { TaskDetailState } from '../types';
import { TaskDetailPanel, taskLinkFor } from './TaskDetailPanel';
import type { FixTask, FixTaskAuditSummary } from '../../types';

const task: FixTask = {
  id: 'task-1',
  repositoryOwner: 'bingqin2',
  repositoryName: 'PatchPilot',
  issueNumber: 1,
  installationId: 0,
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix demo',
  deliveryId: 'delivery-1',
  commentId: 101,
  status: 'COMPLETED',
  failureReason: null,
  createdAt: '2026-06-20T01:00:00Z',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/8',
  completedAt: '2026-06-20T01:01:00Z',
  updatedAt: '2026-06-20T01:01:00Z',
  statusCommentId: null,
  statusCommentUrl: null
};

const baseSummary: FixTaskAuditSummary = {
  task,
  timelineEventCount: 4,
  testRunCount: 1,
  toolCallCount: 3,
  modelCallCount: 2,
  totalModelTokens: 1800,
  latestTimelineEvent: {
    id: 'timeline-1',
    taskId: 'task-1',
    eventType: 'COMPLETED',
    message: 'Task completed',
    createdAt: '2026-06-20T01:01:00Z'
  },
  latestTestRunExitCode: 0,
  latestTestRunDurationMs: 12769
};

const baseDetail: TaskDetailState = {
  summary: baseSummary,
  queueItem: {
    id: 'queue-1',
    taskId: 'task-1',
    status: 'FAILED',
    attemptCount: 3,
    lastError: 'maven tests failed',
    availableAt: '2026-06-20T01:02:00Z',
    lockedAt: '2026-06-20T01:01:00Z',
    createdAt: '2026-06-20T01:00:00Z',
    updatedAt: '2026-06-20T01:03:00Z'
  },
  queueItems: [
    {
      id: 'queue-1',
      taskId: 'task-1',
      status: 'FAILED',
      attemptCount: 3,
      lastError: 'maven tests failed',
      availableAt: '2026-06-20T01:02:00Z',
      lockedAt: '2026-06-20T01:01:00Z',
      createdAt: '2026-06-20T01:00:00Z',
      updatedAt: '2026-06-20T01:03:00Z'
    },
    {
      id: 'queue-older',
      taskId: 'task-1',
      status: 'PENDING',
      attemptCount: 1,
      lastError: null,
      availableAt: '2026-06-20T00:58:00Z',
      lockedAt: null,
      createdAt: '2026-06-20T00:57:00Z',
      updatedAt: '2026-06-20T00:58:00Z'
    }
  ],
  timeline: [],
  testRuns: [],
  toolCalls: [],
  modelCalls: []
};

test('shows execution evidence summary for selected task', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Execution evidence')).toBeInTheDocument();
  expect(screen.getByText('Timeline 4')).toBeInTheDocument();
  expect(screen.getByText('Tests 1')).toBeInTheDocument();
  expect(screen.getByText('Tools 3')).toBeInTheDocument();
  expect(screen.getByText('Model calls 2')).toBeInTheDocument();
  expect(screen.getByText('Latest test PASS')).toBeInTheDocument();
});

test('shows selected task queue state in task detail', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Queue FAILED')).toBeInTheDocument();
  expect(screen.getByText('attempt 3')).toBeInTheDocument();
  expect(screen.getByText('maven tests failed')).toBeInTheDocument();
  const queueDetail = screen.getByText('Queue FAILED').closest('.queue-detail');
  expect(queueDetail).not.toBeNull();
  expect(within(queueDetail as HTMLElement).getByText(/Available /)).toBeInTheDocument();
  expect(within(queueDetail as HTMLElement).getByText(/Locked /)).toBeInTheDocument();
});

test('shows selected task queue history in task detail', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Queue History')).toBeInTheDocument();
  expect(screen.getByText('queue-1')).toBeInTheDocument();
  expect(screen.getByText('queue-older')).toBeInTheDocument();
  expect(screen.getByText('FAILED · attempt 3')).toBeInTheDocument();
  expect(screen.getByText('PENDING · attempt 1')).toBeInTheDocument();
  expect(screen.getAllByText(/maven tests failed/)).toHaveLength(2);
});

test('shows missing latest test evidence when no test result is recorded', () => {
  render(
    <TaskDetailPanel
      task={task}
      detail={{
        ...baseDetail,
        summary: {
          ...baseSummary,
          testRunCount: 0,
          latestTestRunExitCode: null,
          latestTestRunDurationMs: null
        }
      }}
      loading={false}
      actionInFlight={false}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  expect(screen.getByText('Tests 0')).toBeInTheDocument();
  expect(screen.getByText('Latest test None')).toBeInTheDocument();
});

test('builds a shareable task link from the current dashboard URL', () => {
  expect(taskLinkFor('task-1', 'http://127.0.0.1:5173/?status=FAILED')).toBe(
    'http://127.0.0.1:5173/?status=FAILED&taskId=task-1'
  );
});

test('copies the selected task deep link', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });
  window.history.replaceState(null, '', '/?status=FAILED');

  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onCopyReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: /copy link/i }));

  expect(writeText).toHaveBeenCalledWith('http://localhost:3000/?status=FAILED&taskId=task-1');
  expect(screen.getByText('Task link copied')).toBeInTheDocument();
});

test('copies the selected task report', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  const onCopyReport = vi.fn().mockResolvedValue('# PatchPilot Task Report\n\n- Task: `task-1`');
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <TaskDetailPanel
      task={task}
      detail={baseDetail}
      loading={false}
      actionInFlight={false}
      onCancelTask={vi.fn()}
      onRetryTask={vi.fn()}
      onCopyReport={onCopyReport}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: /copy report/i }));

  expect(onCopyReport).toHaveBeenCalledWith('task-1');
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Task Report\n\n- Task: `task-1`');
  expect(screen.getByText('Task report copied')).toBeInTheDocument();
});
