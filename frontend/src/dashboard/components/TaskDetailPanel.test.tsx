import { render, screen } from '@testing-library/react';
import type { TaskDetailState } from '../types';
import { TaskDetailPanel } from './TaskDetailPanel';
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
    />
  );

  expect(screen.getByText('Execution evidence')).toBeInTheDocument();
  expect(screen.getByText('Timeline 4')).toBeInTheDocument();
  expect(screen.getByText('Tests 1')).toBeInTheDocument();
  expect(screen.getByText('Tools 3')).toBeInTheDocument();
  expect(screen.getByText('Model calls 2')).toBeInTheDocument();
  expect(screen.getByText('Latest test PASS')).toBeInTheDocument();
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
    />
  );

  expect(screen.getByText('Tests 0')).toBeInTheDocument();
  expect(screen.getByText('Latest test None')).toBeInTheDocument();
});
