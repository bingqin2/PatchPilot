import { render, screen } from '@testing-library/react';
import { QueuePanel } from './QueuePanel';

const emptySummary = {
  totalCount: 0,
  pendingCount: 0,
  availablePendingCount: 0,
  delayedPendingCount: 0,
  runningCount: 0,
  completedCount: 0,
  failedCount: 0,
  cancelledCount: 0
};

test('shows idle queue health when no work is active', () => {
  render(<QueuePanel summary={emptySummary} items={[]} />);

  expect(screen.getByText('Queue idle')).toBeInTheDocument();
  expect(screen.getByText('No queue items need attention.')).toBeInTheDocument();
});

test('shows active queue health when work is running', () => {
  render(
    <QueuePanel
      summary={{ ...emptySummary, totalCount: 1, runningCount: 1 }}
      items={[
        {
          id: 'queue-1',
          taskId: 'task-1',
          status: 'RUNNING',
          attemptCount: 1,
          lastError: null,
          availableAt: '2026-06-20T01:10:00Z',
          lockedAt: '2026-06-20T01:10:30Z',
          createdAt: '2026-06-20T01:09:00Z',
          updatedAt: '2026-06-20T01:10:30Z'
        }
      ]}
    />
  );

  expect(screen.getByText('Queue active')).toBeInTheDocument();
  expect(screen.getByText('1 running item')).toBeInTheDocument();
});

test('shows delayed queue advisory when pending work is not yet available', () => {
  render(<QueuePanel summary={{ ...emptySummary, totalCount: 2, delayedPendingCount: 2 }} items={[]} />);

  expect(screen.getByText('Queue delayed')).toBeInTheDocument();
  expect(screen.getByText('2 delayed items')).toBeInTheDocument();
});

test('shows failed queue health before other queue states', () => {
  render(
    <QueuePanel
      summary={{
        ...emptySummary,
        totalCount: 3,
        delayedPendingCount: 1,
        runningCount: 1,
        failedCount: 1
      }}
      items={[]}
    />
  );

  expect(screen.getByText('Queue has failures')).toBeInTheDocument();
  expect(screen.getByText('1 failed item')).toBeInTheDocument();
  expect(screen.getByText('1 delayed item')).toBeInTheDocument();
  expect(screen.getByText('1 running item')).toBeInTheDocument();
});
