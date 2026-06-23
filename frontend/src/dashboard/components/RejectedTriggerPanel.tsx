import type { RejectedTriggerAudit, RejectedTriggerCategoryFilter } from '../../types';
import { compactTime } from '../format';

const REJECTED_TRIGGER_CATEGORY_FILTERS: RejectedTriggerCategoryFilter[] = [
  'ALL',
  'UNKNOWN',
  'EMPTY_COMMAND',
  'UNSUPPORTED_COMMAND',
  'NOT_ACTIONABLE',
  'DANGEROUS_INSTRUCTION',
  'TRIGGER_USER_NOT_ALLOWED',
  'REPOSITORY_NOT_ALLOWED',
  'RATE_LIMITED',
  'MODEL_REJECTED',
  'MODEL_NEEDS_CLARIFICATION',
  'MODEL_CLASSIFICATION_FAILED'
];

interface RejectedTriggerPanelProps {
  rejectedTriggers: RejectedTriggerAudit[];
  categoryFilter: RejectedTriggerCategoryFilter;
  error: string | null;
  retryingRejectedTriggerId: string | null;
  onCategoryFilterChange: (category: RejectedTriggerCategoryFilter) => void;
  onRetryRejectedTrigger: (id: string) => void;
  onSelectTask: (taskId: string) => void;
}

export function RejectedTriggerPanel({
  rejectedTriggers,
  categoryFilter,
  error,
  retryingRejectedTriggerId,
  onCategoryFilterChange,
  onRetryRejectedTrigger,
  onSelectTask
}: RejectedTriggerPanelProps) {
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
        <label className="compact-filter-control">
          <span>Category</span>
          <select
            aria-label="Filter rejected triggers by category"
            value={categoryFilter}
            onChange={(event) => onCategoryFilterChange(event.target.value as RejectedTriggerCategoryFilter)}
          >
            {REJECTED_TRIGGER_CATEGORY_FILTERS.map((category) => (
              <option key={category} value={category}>
                {category === 'ALL' ? 'All categories' : categoryLabel(category)}
              </option>
            ))}
          </select>
        </label>
      </div>
      {error ? <p className="panel-error">{error}</p> : null}
      <div className="rejected-trigger-list">
        {rejectedTriggers.map((trigger) => (
          <article className="rejected-trigger-row" key={trigger.id}>
            <div className="rejected-trigger-main">
              <span className="status-pill status-failed">{trigger.source}</span>
              <span className="status-pill status-warning">{categoryLabel(trigger.category)}</span>
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
              {renderRetriedTaskLink(trigger.retriedTaskId, onSelectTask)}
              <button
                type="button"
                className="inline-action"
                disabled={retryingRejectedTriggerId === trigger.id}
                onClick={() => onRetryRejectedTrigger(trigger.id)}
              >
                {retryingRejectedTriggerId === trigger.id ? 'Retrying trigger' : 'Retry trigger'}
              </button>
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

function taskLink(taskId: string) {
  return `/tasks/${encodeURIComponent(taskId)}`;
}

function renderRetriedTaskLink(retriedTaskId: string | null, onSelectTask: (taskId: string) => void) {
  if (!retriedTaskId) {
    return null;
  }
  return (
    <a
      href={taskLink(retriedTaskId)}
      onClick={(event) => {
        event.preventDefault();
        onSelectTask(retriedTaskId);
      }}
    >
      Retried task
    </a>
  );
}
