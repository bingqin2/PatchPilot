import type {
  CreateTriggerQuarantineInput,
  RejectedTriggerAudit,
  RejectedTriggerAuditSummary,
  RejectedTriggerCategoryFilter,
  RejectedTriggerCountSummary,
  ReleaseTriggerQuarantineInput,
  TriggerQuarantine,
  TriggerQuarantineScope
} from '../../types';
import { useState, type FormEvent } from 'react';
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
  onCreateTriggerQuarantine: (input: CreateTriggerQuarantineInput) => void;
  onReleaseTriggerQuarantine: (id: string, input: ReleaseTriggerQuarantineInput) => void;
  creatingTriggerQuarantine: boolean;
  releasingTriggerQuarantineId: string | null;
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
  onSelectTask,
  onCreateTriggerQuarantine,
  onReleaseTriggerQuarantine,
  creatingTriggerQuarantine,
  releasingTriggerQuarantineId
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
      <TriggerQuarantineControls
        quarantines={quarantines}
        onCreateTriggerQuarantine={onCreateTriggerQuarantine}
        onReleaseTriggerQuarantine={onReleaseTriggerQuarantine}
        creatingTriggerQuarantine={creatingTriggerQuarantine}
        releasingTriggerQuarantineId={releasingTriggerQuarantineId}
      />
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

function TriggerQuarantineControls({
  quarantines,
  onCreateTriggerQuarantine,
  onReleaseTriggerQuarantine,
  creatingTriggerQuarantine,
  releasingTriggerQuarantineId
}: {
  quarantines: TriggerQuarantine[];
  onCreateTriggerQuarantine: (input: CreateTriggerQuarantineInput) => void;
  onReleaseTriggerQuarantine: (id: string, input: ReleaseTriggerQuarantineInput) => void;
  creatingTriggerQuarantine: boolean;
  releasingTriggerQuarantineId: string | null;
}) {
  const [scope, setScope] = useState<TriggerQuarantineScope>('TRIGGER_USER');
  const [scopeKey, setScopeKey] = useState('');
  const [reason, setReason] = useState('');
  const [durationMinutes, setDurationMinutes] = useState('30');
  const [operator, setOperator] = useState('');

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    onCreateTriggerQuarantine({
      scope,
      scopeKey: scopeKey.trim(),
      reason: reason.trim(),
      durationMs: Math.max(1, Number(durationMinutes)) * 60_000,
      operator: operator.trim()
    });
  }

  return (
    <div className="trigger-quarantine-summary" role="group" aria-label="Trigger quarantine controls">
      <div className="rejected-trigger-summary-header">
        <div>
          <h3>Trigger quarantine controls</h3>
          <p>
            {quarantines.length} active {quarantines.length === 1 ? 'quarantine' : 'quarantines'}
          </p>
        </div>
      </div>
      <form className="trigger-quarantine-form" onSubmit={handleSubmit}>
        <label>
          <span>Scope</span>
          <select
            aria-label="Manual quarantine scope"
            value={scope}
            onChange={(event) => setScope(event.target.value as TriggerQuarantineScope)}
          >
            <option value="TRIGGER_USER">Trigger user</option>
            <option value="REPOSITORY">Repository</option>
          </select>
        </label>
        <label>
          <span>Target</span>
          <input
            aria-label="Manual quarantine target"
            value={scopeKey}
            onChange={(event) => setScopeKey(event.target.value)}
            placeholder={scope === 'TRIGGER_USER' ? 'github-user' : 'owner/repo'}
          />
        </label>
        <label>
          <span>Reason</span>
          <input
            aria-label="Manual quarantine reason"
            value={reason}
            onChange={(event) => setReason(event.target.value)}
            placeholder="Why this trigger should be blocked"
          />
        </label>
        <label>
          <span>Minutes</span>
          <input
            aria-label="Manual quarantine duration minutes"
            min="1"
            type="number"
            value={durationMinutes}
            onChange={(event) => setDurationMinutes(event.target.value)}
          />
        </label>
        <label>
          <span>Operator</span>
          <input
            aria-label="Manual quarantine operator"
            value={operator}
            onChange={(event) => setOperator(event.target.value)}
            placeholder="local-admin"
          />
        </label>
        <button
          type="submit"
          className="secondary-button"
          disabled={creatingTriggerQuarantine || !scopeKey.trim() || !reason.trim() || !operator.trim()}
        >
          {creatingTriggerQuarantine ? 'Creating quarantine' : 'Create quarantine'}
        </button>
      </form>
      <TriggerQuarantineSummary
        quarantines={quarantines}
        operator={operator.trim() || 'dashboard-operator'}
        releasingTriggerQuarantineId={releasingTriggerQuarantineId}
        onReleaseTriggerQuarantine={onReleaseTriggerQuarantine}
      />
    </div>
  );
}

function TriggerQuarantineSummary({
  quarantines,
  operator,
  releasingTriggerQuarantineId,
  onReleaseTriggerQuarantine
}: {
  quarantines: TriggerQuarantine[];
  operator: string;
  releasingTriggerQuarantineId: string | null;
  onReleaseTriggerQuarantine: (id: string, input: ReleaseTriggerQuarantineInput) => void;
}) {
  if (quarantines.length === 0) {
    return <p className="empty-state compact-empty-state">No active trigger quarantines.</p>;
  }
  return (
    <div className="trigger-quarantine-list" role="group" aria-label="Active trigger quarantines">
      <h4>Active trigger quarantines</h4>
      {quarantines.map((quarantine) => (
        <article className="trigger-quarantine-row" key={quarantine.id}>
          <div>
            <span className="status-pill status-warning">{scopeLabel(quarantine.scope)}</span>
            <strong>{quarantine.scopeKey}</strong>
            <span>{quarantine.evidenceCount} rejected triggers</span>
            {quarantine.createdBy ? <span>By {quarantine.createdBy}</span> : null}
            <time>Expires {compactTime(quarantine.expiresAt)}</time>
            <button
              type="button"
              className="inline-action"
              disabled={releasingTriggerQuarantineId === quarantine.id}
              onClick={() =>
                onReleaseTriggerQuarantine(quarantine.id, {
                  operator,
                  reason: 'Operator released active quarantine from dashboard'
                })
              }
            >
              {releasingTriggerQuarantineId === quarantine.id
                ? 'Releasing quarantine'
                : `Release ${quarantine.scopeKey} quarantine`}
            </button>
          </div>
          <p>{quarantine.reason}</p>
        </article>
      ))}
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
