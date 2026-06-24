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

const activeWorkerHealth = {
  state: 'ACTIVE',
  message: 'Worker poller is executing a queue item.',
  startedAt: '2026-06-24T06:00:00Z',
  lastPollAt: '2026-06-24T06:00:01Z',
  pollCount: 12,
  claimedCount: 3,
  completedCount: 2,
  failedCount: 1,
  idlePollCount: 8,
  lastClaimedQueueItemId: 'queue-123',
  lastClaimedTaskId: 'task-123',
  lastError: null,
  lastPollAgeMs: 1000,
  readinessStatus: 'READY',
  operatorAction: 'No action needed.'
} as const;

test('shows idle queue health when no work is active', () => {
  render(<QueuePanel summary={emptySummary} items={[]} workerHealth={null} />);

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
      workerHealth={null}
    />
  );

  expect(screen.getByText('Queue active')).toBeInTheDocument();
  expect(screen.getByText('1 running item')).toBeInTheDocument();
});

test('shows delayed queue advisory when pending work is not yet available', () => {
  render(<QueuePanel summary={{ ...emptySummary, totalCount: 2, delayedPendingCount: 2 }} items={[]} workerHealth={null} />);

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
      workerHealth={null}
    />
  );

  expect(screen.getByText('Queue has failures')).toBeInTheDocument();
  expect(screen.getByText('1 failed item')).toBeInTheDocument();
  expect(screen.getByText('1 delayed item')).toBeInTheDocument();
  expect(screen.getByText('1 running item')).toBeInTheDocument();
});

test('shows worker heartbeat and last claimed task', () => {
  render(<QueuePanel summary={emptySummary} items={[]} workerHealth={activeWorkerHealth} />);

  expect(screen.getByText('Worker active')).toBeInTheDocument();
  expect(screen.getByText('Worker poller is executing a queue item.')).toBeInTheDocument();
  expect(screen.getByText('READY readiness')).toBeInTheDocument();
  expect(screen.getByText('1.0s last poll age')).toBeInTheDocument();
  expect(screen.getByText('No action needed.')).toBeInTheDocument();
  expect(screen.getByText('12 polls')).toBeInTheDocument();
  expect(screen.getByText('3 claimed')).toBeInTheDocument();
  expect(screen.getByText('Last task task-123')).toBeInTheDocument();
});

test('shows worker error with last failure reason', () => {
  render(
    <QueuePanel
      summary={emptySummary}
      items={[]}
      workerHealth={{
        ...activeWorkerHealth,
        state: 'ERROR',
        message: 'Worker poller recorded a task failure: worker failed',
        lastError: 'worker failed',
        readinessStatus: 'NEEDS_ATTENTION',
        operatorAction: 'Inspect worker logs and the latest failed queue item before starting a demo.'
      }}
    />
  );

  expect(screen.getByText('Worker error')).toBeInTheDocument();
  expect(screen.getByText('Worker poller recorded a task failure: worker failed')).toBeInTheDocument();
  expect(screen.getByText('NEEDS_ATTENTION readiness')).toBeInTheDocument();
  expect(screen.getByText('Inspect worker logs and the latest failed queue item before starting a demo.')).toBeInTheDocument();
  expect(screen.getByText('Last error worker failed')).toBeInTheDocument();
});
