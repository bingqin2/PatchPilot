import type { RejectedTriggerAudit } from '../../types';
import { compactTime } from '../format';

interface RejectedTriggerPanelProps {
  rejectedTriggers: RejectedTriggerAudit[];
  error: string | null;
}

export function RejectedTriggerPanel({ rejectedTriggers, error }: RejectedTriggerPanelProps) {
  const rejectionSummary =
    rejectedTriggers.length === 0
      ? 'No recent rejections'
      : rejectedTriggers.length === 1
        ? '1 recent rejection'
        : `${rejectedTriggers.length} recent rejections`;

  return (
    <section className="panel rejected-trigger-panel" aria-label="Rejected triggers">
      <div className="panel-header">
        <div>
          <h2>Rejected triggers</h2>
          <p>{rejectionSummary}</p>
        </div>
      </div>
      {error ? <p className="panel-error">{error}</p> : null}
      <div className="rejected-trigger-list">
        {rejectedTriggers.map((trigger) => (
          <article className="rejected-trigger-row" key={trigger.id}>
            <div className="rejected-trigger-main">
              <span className="status-pill status-failed">{trigger.source}</span>
              <div>
                <strong>{trigger.triggerComment ?? 'missing trigger comment'}</strong>
                <span>{repositoryLabel(trigger)}</span>
              </div>
              <time>{compactTime(trigger.createdAt)}</time>
            </div>
            <div className="rejected-trigger-meta">
              {trigger.triggerUser ? <span>{trigger.triggerUser}</span> : null}
              {trigger.deliveryId ? <span>{trigger.deliveryId}</span> : null}
              {trigger.commentUrl ? (
                <a href={trigger.commentUrl} target="_blank" rel="noreferrer">
                  Refusal comment
                </a>
              ) : null}
            </div>
            <p>{trigger.reason}</p>
          </article>
        ))}
        {rejectedTriggers.length === 0 && !error ? <p className="empty-state">No rejected /agent fix triggers recorded.</p> : null}
      </div>
    </section>
  );
}

function repositoryLabel(trigger: RejectedTriggerAudit) {
  if (!trigger.repositoryOwner || !trigger.repositoryName) {
    return 'repository unavailable';
  }
  const issue = trigger.issueNumber === null ? '' : ` #${trigger.issueNumber}`;
  return `${trigger.repositoryOwner}/${trigger.repositoryName}${issue}`;
}
