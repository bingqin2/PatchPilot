import type {
  RejectedTriggerAudit,
  RejectedTriggerAuditSummary,
  RejectedTriggerCategoryFilter,
  RejectedTriggerCountSummary,
  TriggerQuarantine,
  TriggerQuarantineScope
} from '../../types';
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
  'ABUSE_QUARANTINED',
  'MODEL_REJECTED',
  'MODEL_NEEDS_CLARIFICATION',
  'MODEL_CLASSIFICATION_FAILED'
];

interface RejectedTriggerPanelProps {
  rejectedTriggers: RejectedTriggerAudit[];
  summary: RejectedTriggerAuditSummary | null;
  quarantines: TriggerQuarantine[];
  categoryFilter: RejectedTriggerCategoryFilter;
  error: string | null;
  retryingRejectedTriggerId: string | null;
  onCategoryFilterChange: (category: RejectedTriggerCategoryFilter) => void;
  onRetryRejectedTrigger: (id: string) => void;
  onSelectTask: (taskId: string) => void;
}

export function RejectedTriggerPanel({
  rejectedTriggers,
  summary,
  quarantines,
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
      <RejectedTriggerSummary summary={summary} onCategoryFilterChange={onCategoryFilterChange} />
      <TriggerQuarantineSummary quarantines={quarantines} />
      <div className="rejected-trigger-list" role="group" aria-label="Rejected trigger audit rows">
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

function TriggerQuarantineSummary({ quarantines }: { quarantines: TriggerQuarantine[] }) {
  if (quarantines.length === 0) {
    return null;
  }
  return (
    <div className="trigger-quarantine-summary" role="group" aria-label="Active trigger quarantines">
      <div className="rejected-trigger-summary-header">
        <div>
          <h3>Active trigger quarantines</h3>
          <p>
            {quarantines.length} active {quarantines.length === 1 ? 'quarantine' : 'quarantines'}
          </p>
        </div>
      </div>
      <div className="trigger-quarantine-list">
        {quarantines.map((quarantine) => (
          <article className="trigger-quarantine-row" key={quarantine.id}>
            <div>
              <span className="status-pill status-warning">{scopeLabel(quarantine.scope)}</span>
              <strong>{quarantine.scopeKey}</strong>
              <span>{quarantine.evidenceCount} rejected triggers</span>
              <time>Expires {compactTime(quarantine.expiresAt)}</time>
            </div>
            <p>{quarantine.reason}</p>
          </article>
        ))}
      </div>
    </div>
  );
}

function RejectedTriggerSummary({
  summary,
  onCategoryFilterChange
}: {
  summary: RejectedTriggerAuditSummary | null;
  onCategoryFilterChange: (category: RejectedTriggerCategoryFilter) => void;
}) {
  if (!summary) {
    return null;
  }
  return (
    <div className="rejected-trigger-summary" role="group" aria-label="Rejected trigger summary">
      <div className="rejected-trigger-summary-header">
        <div>
          <h3>Rejected trigger summary</h3>
          <p>{summary.totalCount} rejected triggers analyzed</p>
        </div>
      </div>
      <div className="rejected-trigger-summary-grid">
        <CountList
          title="Categories"
          items={summary.categoryCounts}
          valueLabel={categoryLabel}
          onCategoryFilterChange={onCategoryFilterChange}
        />
        <CountList title="Sources" items={summary.sourceCounts} />
        <CountList title="Top users" items={summary.triggerUserCounts} />
        <CountList title="Top repositories" items={summary.repositoryCounts} />
      </div>
    </div>
  );
}

function CountList({
  title,
  items,
  valueLabel = rawValueLabel,
  onCategoryFilterChange
}: {
  title: string;
  items: RejectedTriggerCountSummary[];
  valueLabel?: (value: string) => string;
  onCategoryFilterChange?: (category: RejectedTriggerCategoryFilter) => void;
}) {
  return (
    <div className="rejected-trigger-summary-card">
      <span>{title}</span>
      <div className="rejected-trigger-count-list">
        {items.length === 0 ? <p>No data</p> : null}
        {items.map((item) => renderCountItem(item, valueLabel, onCategoryFilterChange))}
      </div>
    </div>
  );
}

function renderCountItem(
  item: RejectedTriggerCountSummary,
  valueLabel: (value: string) => string,
  onCategoryFilterChange?: (category: RejectedTriggerCategoryFilter) => void
) {
  const label = valueLabel(item.value);
  const accessibleName = `Filter by ${label}, ${item.count} rejected ${item.count === 1 ? 'trigger' : 'triggers'}`;
  if (onCategoryFilterChange && isRejectedTriggerCategory(item.value)) {
    const category = item.value;
    return (
      <button
        key={item.value}
        type="button"
        className="rejected-trigger-count-button"
        aria-label={accessibleName}
        onClick={() => onCategoryFilterChange(category)}
      >
        <span>{label}</span>
        <strong>{item.count}</strong>
      </button>
    );
  }
  return (
    <div className="rejected-trigger-count-row" key={item.value}>
      <span>{label}</span>
      <strong>{item.count}</strong>
    </div>
  );
}

function rawValueLabel(value: string) {
  return value;
}

function scopeLabel(scope: TriggerQuarantineScope) {
  return scope === 'TRIGGER_USER' ? 'Trigger user' : 'Repository';
}

function repositoryLabel(trigger: RejectedTriggerAudit) {
  if (!trigger.repositoryOwner || !trigger.repositoryName) {
    return 'repository unavailable';
  }
  const issue = trigger.issueNumber === null ? '' : ` #${trigger.issueNumber}`;
  return `${trigger.repositoryOwner}/${trigger.repositoryName}${issue}`;
}

function isRejectedTriggerCategory(value: string): value is RejectedTriggerCategoryFilter {
  return REJECTED_TRIGGER_CATEGORY_FILTERS.includes(value as RejectedTriggerCategoryFilter) && value !== 'ALL';
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
