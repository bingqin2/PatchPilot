import type {
  FixTask,
  FixTaskTimelineEvent,
  RejectedTriggerAudit,
  RejectedTriggerAuditSummary
} from '../../types';
import { compactTime } from '../format';

interface TriggerDecisionPanelProps {
  task: FixTask | null;
  timeline: FixTaskTimelineEvent[];
  rejectedTriggers: RejectedTriggerAudit[];
  summary: RejectedTriggerAuditSummary | null;
}

export function TriggerDecisionPanel({
  task,
  timeline,
  rejectedTriggers,
  summary
}: TriggerDecisionPanelProps) {
  const acceptedTrigger = timeline.find((event) => event.eventType === 'TRIGGER_ACCEPTED') ?? null;
  const recentRejectedTriggers = rejectedTriggers.slice(0, 3);

  return (
    <section className="panel trigger-decision-panel" aria-label="Trigger decisions">
      <div className="panel-header">
        <div>
          <h2>Trigger decisions</h2>
          <p>{decisionSummary(acceptedTrigger, summary)}</p>
        </div>
      </div>

      <div className="trigger-decision-grid">
        <section className="trigger-decision-card" aria-label="Accepted trigger evidence">
          <div className="trigger-decision-card-header">
            <div>
              <h3>Accepted trigger evidence</h3>
              <p>Why the selected `/agent` comment became an executable task.</p>
            </div>
            {task ? <span className="status-pill status-completed">Selected task</span> : null}
          </div>

          {!task ? (
            <p className="empty-state compact-empty-state">Select a task to inspect accepted trigger evidence.</p>
          ) : acceptedTrigger ? (
            <article className="trigger-decision-accepted-row">
              <div className="trigger-decision-meta">
                <span className="status-pill status-completed">Accepted</span>
                <time dateTime={acceptedTrigger.createdAt}>{compactTime(acceptedTrigger.createdAt)}</time>
              </div>
              <strong>
                {task.repositoryOwner}/{task.repositoryName} #{task.issueNumber}
              </strong>
              <code>{task.triggerComment}</code>
              <p>{acceptedTrigger.message}</p>
            </article>
          ) : (
            <p className="empty-state compact-empty-state">No accepted trigger evidence recorded for this task.</p>
          )}
        </section>

        <section className="trigger-decision-card" aria-label="Rejected trigger decisions">
          <div className="trigger-decision-card-header">
            <div>
              <h3>Rejected trigger decisions</h3>
              <p>Recent comments refused before task creation.</p>
            </div>
            <span>{summary ? `${summary.totalCount} analyzed` : 'No summary'}</span>
          </div>

          {summary && summary.categoryCounts.length > 0 ? (
            <div className="trigger-decision-summary-pills" aria-label="Rejected trigger category summary">
              {summary.categoryCounts.slice(0, 4).map((item) => (
                <span key={item.value}>
                  {categoryLabel(item.value)} <strong>{item.count}</strong>
                </span>
              ))}
            </div>
          ) : null}

          <div className="trigger-decision-rejected-list">
            {recentRejectedTriggers.map((trigger) => (
              <article className="trigger-decision-rejected-row" key={trigger.id}>
                <div className="trigger-decision-meta">
                  <span className="status-pill status-warning">{categoryLabel(trigger.category)}</span>
                  <span>{repositoryLabel(trigger)}</span>
                  <time dateTime={trigger.createdAt}>{compactTime(trigger.createdAt)}</time>
                  {trigger.commentUrl ? (
                    <a href={trigger.commentUrl} target="_blank" rel="noreferrer">
                      Refusal comment
                    </a>
                  ) : null}
                </div>
                <strong>{trigger.triggerComment ?? 'missing trigger comment'}</strong>
                <p>{trigger.reason}</p>
              </article>
            ))}
            {recentRejectedTriggers.length === 0 ? (
              <p className="empty-state compact-empty-state">No rejected trigger decisions recorded.</p>
            ) : null}
          </div>
        </section>
      </div>
    </section>
  );
}

function decisionSummary(acceptedTrigger: FixTaskTimelineEvent | null, summary: RejectedTriggerAuditSummary | null) {
  if (acceptedTrigger && summary) {
    return `1 selected accepted trigger, ${summary.totalCount} rejected triggers analyzed`;
  }
  if (acceptedTrigger) {
    return 'Selected task has accepted trigger evidence';
  }
  if (summary) {
    return `${summary.totalCount} rejected triggers analyzed`;
  }
  return 'Trigger decision evidence is not available yet';
}

function repositoryLabel(trigger: RejectedTriggerAudit) {
  if (!trigger.repositoryOwner || !trigger.repositoryName) {
    return 'repository unavailable';
  }
  const issue = trigger.issueNumber === null ? '' : ` #${trigger.issueNumber}`;
  return `${trigger.repositoryOwner}/${trigger.repositoryName}${issue}`;
}

function categoryLabel(category: string | null) {
  if (!category) {
    return 'Unknown';
  }
  return category
    .toLowerCase()
    .split('_')
    .filter(Boolean)
    .join(' ')
    .replace(/^./, (character) => character.toUpperCase());
}
