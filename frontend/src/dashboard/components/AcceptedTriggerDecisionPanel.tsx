import type { AcceptedTriggerDecision, TriggerEvaluationDecision } from '../../types';
import { compactTime } from '../format';

interface AcceptedTriggerDecisionPanelProps {
  decisions: AcceptedTriggerDecision[];
  error: string | null;
  onSelectTask: (taskId: string) => void;
}

export function AcceptedTriggerDecisionPanel({
  decisions,
  error,
  onSelectTask
}: AcceptedTriggerDecisionPanelProps) {
  return (
    <section className="panel accepted-trigger-panel" aria-label="Accepted trigger audit">
      <div className="panel-header">
        <div>
          <h2>Accepted trigger audit</h2>
          <p>{decisions.length === 0 ? 'No accepted triggers recorded' : `${decisions.length} recent accepted triggers`}</p>
        </div>
      </div>

      {error ? <p className="error-banner compact-error">{error}</p> : null}

      <div className="accepted-trigger-list" role="group" aria-label="Accepted trigger decision rows">
        {decisions.map((decision) => (
          <article className="accepted-trigger-row" key={decision.id}>
            <div className="accepted-trigger-row-header">
              <div>
                <strong>
                  {decision.repositoryOwner}/{decision.repositoryName} #{decision.issueNumber}
                </strong>
                <span>{decision.triggerUser}</span>
              </div>
              <div className="accepted-trigger-row-meta">
                <span className="status-pill status-completed">{decision.finalDecision}</span>
                <span className={`status-pill status-${decision.taskStatus.toLowerCase().replaceAll('_', '-')}`}>
                  {statusLabel(decision.taskStatus)}
                </span>
                <time dateTime={decision.createdAt}>{compactTime(decision.createdAt)}</time>
              </div>
            </div>

            <code>{decision.triggerComment}</code>

            <dl className="accepted-trigger-decision-grid">
              <DecisionTerm label="Safety" decision={decision.safetyDecision} />
              <DecisionTerm label="Active task" decision={decision.activeTaskDecision} />
              <DecisionTerm label="Quarantine" decision={decision.quarantineDecision} />
              <DecisionTerm label="Rate limit" decision={decision.rateLimitDecision} />
              <DecisionTerm label="Intent" decision={decision.triggerIntentDecision} />
              <div>
                <dt>Issue context</dt>
                <dd>{decision.issueContextLoaded ? 'loaded' : 'unavailable'}</dd>
              </div>
            </dl>

            <button type="button" className="text-button" onClick={() => onSelectTask(decision.taskId)}>
              Open task
            </button>
          </article>
        ))}
        {decisions.length === 0 ? (
          <p className="empty-state compact-empty-state">No accepted `/agent fix` trigger decisions recorded yet.</p>
        ) : null}
      </div>
    </section>
  );
}

function DecisionTerm({ label, decision }: { label: string; decision: TriggerEvaluationDecision }) {
  return (
    <div>
      <dt>{label}</dt>
      <dd>{decision.reason}</dd>
    </div>
  );
}

function statusLabel(status: string) {
  return status.toLowerCase().split('_').join(' ');
}
