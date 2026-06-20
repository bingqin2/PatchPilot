import type { FixTaskQueueItem, FixTaskQueueSummary } from '../../types';
import { compactTime } from '../format';
import { SummaryItem } from './SummaryItem';

interface QueuePanelProps {
  summary: FixTaskQueueSummary | null;
  items: FixTaskQueueItem[];
}

export function QueuePanel({ summary, items }: QueuePanelProps) {
  const health = queueHealth(summary);

  return (
    <section className="panel queue-panel">
      <div className="panel-header">
        <div>
          <h2>Queue</h2>
          <p>{summary ? `${summary.totalCount} queue items` : 'Loading queue state'}</p>
        </div>
      </div>
      <div className={`queue-health queue-health-${health.level}`}>
        <strong>{health.title}</strong>
        {health.details.map((detail) => (
          <span key={detail}>{detail}</span>
        ))}
      </div>
      <div className="queue-summary">
        <SummaryItem label="Pending" value={`${summary?.pendingCount ?? 0} pending`} />
        <SummaryItem label="Available" value={`${summary?.availablePendingCount ?? 0} available`} />
        <SummaryItem label="Delayed" value={`${summary?.delayedPendingCount ?? 0} delayed`} />
        <SummaryItem label="Running" value={summary?.runningCount ?? 0} />
        <SummaryItem label="Failed" value={summary?.failedCount ?? 0} />
        <SummaryItem label="Cancelled" value={summary?.cancelledCount ?? 0} />
      </div>
      <div className="queue-list">
        {items.map((item) => (
          <div className="queue-row" key={item.id}>
            <span className={`status-pill status-${item.status.toLowerCase()}`}>{item.status}</span>
            <strong>{item.id}</strong>
            <span>{item.taskId}</span>
            <span>attempt {item.attemptCount}</span>
            <time>{compactTime(item.availableAt)}</time>
            {item.lastError ? <p>{item.lastError}</p> : null}
          </div>
        ))}
        {items.length === 0 ? <p className="empty-state">No queue items.</p> : null}
      </div>
    </section>
  );
}

function queueHealth(summary: FixTaskQueueSummary | null) {
  if (!summary) {
    return {
      level: 'loading',
      title: 'Loading queue state',
      details: [] as string[]
    };
  }

  const details = [
    countLabel(summary.failedCount, 'failed item'),
    countLabel(summary.delayedPendingCount, 'delayed item'),
    countLabel(summary.runningCount, 'running item')
  ].filter(Boolean);

  if (summary.failedCount > 0) {
    return {
      level: 'critical',
      title: 'Queue has failures',
      details
    };
  }

  if (summary.delayedPendingCount > 0) {
    return {
      level: 'advisory',
      title: 'Queue delayed',
      details
    };
  }

  if (summary.runningCount > 0) {
    return {
      level: 'active',
      title: 'Queue active',
      details
    };
  }

  return {
    level: 'idle',
    title: 'Queue idle',
    details: ['No queue items need attention.']
  };
}

function countLabel(count: number, label: string) {
  if (count <= 0) {
    return '';
  }
  return `${count} ${label}${count === 1 ? '' : 's'}`;
}
