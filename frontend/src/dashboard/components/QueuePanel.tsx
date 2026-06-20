import type { FixTaskQueueItem, FixTaskQueueSummary } from '../../types';
import { compactTime } from '../format';
import { SummaryItem } from './SummaryItem';

interface QueuePanelProps {
  summary: FixTaskQueueSummary | null;
  items: FixTaskQueueItem[];
}

export function QueuePanel({ summary, items }: QueuePanelProps) {
  return (
    <section className="panel queue-panel">
      <div className="panel-header">
        <div>
          <h2>Queue</h2>
          <p>{summary ? `${summary.totalCount} queue items` : 'Loading queue state'}</p>
        </div>
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
