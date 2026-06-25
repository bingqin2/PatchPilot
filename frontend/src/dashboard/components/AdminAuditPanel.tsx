import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import type { AdminAuditFilterOptions, OperatorSafetyAudit } from '../../types';
import { actionLabel } from '../actionLabels';
import { compactTime } from '../format';

interface AdminAuditPanelProps {
  audits: OperatorSafetyAudit[];
  error: string | null;
  filters: AdminAuditFilterOptions;
  onFiltersChange: (filters: AdminAuditFilterOptions) => void;
}

type AdminAuditTextFilterKey = 'action' | 'operator' | 'resourceType' | 'resourceId' | 'scopeKey';

export function AdminAuditPanel({ audits, error, filters, onFiltersChange }: AdminAuditPanelProps) {
  const [draftFilters, setDraftFilters] = useState<AdminAuditFilterOptions>(() => normalizedFilters(filters));

  useEffect(() => {
    setDraftFilters(normalizedFilters(filters));
  }, [filters]);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    onFiltersChange(normalizedFilters(draftFilters));
  }

  function clearFilters() {
    onFiltersChange({ limit: 20 });
  }

  function updateDraftFilter(key: AdminAuditTextFilterKey, value: string) {
    setDraftFilters((current) => ({
      ...current,
      [key]: value
    }));
  }

  async function copyReport() {
    await navigator.clipboard?.writeText(buildAdminAuditReport(audits, filters));
  }

  return (
    <section className="panel admin-audit-panel" aria-label="Admin audit trail">
      <div className="panel-header">
        <div>
          <h2>Admin audit trail</h2>
          <p>
            {audits.length} recent admin {audits.length === 1 ? 'event' : 'events'}
          </p>
        </div>
        <button type="button" className="secondary-button" onClick={copyReport}>
          Copy admin audit report
        </button>
      </div>
      <form className="admin-audit-filter-form" onSubmit={handleSubmit}>
        <label>
          <span>Admin audit action</span>
          <input
            value={draftFilters.action ?? ''}
            onChange={(event) => updateDraftFilter('action', event.target.value)}
            placeholder="TASK_RETRIED"
          />
        </label>
        <label>
          <span>Admin audit operator</span>
          <input
            value={draftFilters.operator ?? ''}
            onChange={(event) => updateDraftFilter('operator', event.target.value)}
            placeholder="admin-api"
          />
        </label>
        <label>
          <span>Admin audit resource type</span>
          <input
            value={draftFilters.resourceType ?? ''}
            onChange={(event) => updateDraftFilter('resourceType', event.target.value)}
            placeholder="TASK"
          />
        </label>
        <label>
          <span>Admin audit resource id</span>
          <input
            value={draftFilters.resourceId ?? ''}
            onChange={(event) => updateDraftFilter('resourceId', event.target.value)}
            placeholder="task id"
          />
        </label>
        <label>
          <span>Admin audit scope key</span>
          <input
            value={draftFilters.scopeKey ?? ''}
            onChange={(event) => updateDraftFilter('scopeKey', event.target.value)}
            placeholder="owner/repo"
          />
        </label>
        <div className="admin-audit-filter-actions">
          <button type="submit">Apply admin audit filters</button>
          <button type="button" className="secondary-button" onClick={clearFilters}>
            Clear admin audit filters
          </button>
        </div>
      </form>
      {error ? <p className="panel-error">{error}</p> : null}
      <div className="admin-audit-list" role="group" aria-label="Admin audit event rows">
        {audits.map((audit) => (
          <article className="admin-audit-row" key={audit.id}>
            <div>
              <span className="status-pill status-warning">{actionLabel(audit.action)}</span>
              <strong>{audit.resourceId}</strong>
              <span>{audit.resourceType}</span>
              <span>{audit.scopeKey}</span>
              <span>{audit.operator}</span>
              <time>{compactTime(audit.createdAt)}</time>
            </div>
            <p>{audit.reason}</p>
          </article>
        ))}
        {audits.length === 0 ? <p className="empty-state compact-empty-state">No admin actions recorded.</p> : null}
      </div>
    </section>
  );
}

function normalizedFilters(filters: AdminAuditFilterOptions): AdminAuditFilterOptions {
  return {
    limit: filters.limit ?? 20,
    action: trimmedOrUndefined(filters.action),
    resourceType: trimmedOrUndefined(filters.resourceType),
    resourceId: trimmedOrUndefined(filters.resourceId),
    scope: filters.scope,
    scopeKey: trimmedOrUndefined(filters.scopeKey),
    operator: trimmedOrUndefined(filters.operator)
  };
}

function trimmedOrUndefined(value: string | undefined) {
  const trimmed = value?.trim();
  return trimmed ? trimmed : undefined;
}

function buildAdminAuditReport(audits: OperatorSafetyAudit[], filters: AdminAuditFilterOptions) {
  const filterSummary = Object.entries(normalizedFilters(filters))
    .filter(([, value]) => value !== undefined && value !== '')
    .map(([key, value]) => `${key}=${value}`)
    .join(', ');
  const lines = audits.map((audit) => (
    `- \`${audit.action}\` ${audit.resourceType} ${audit.resourceId} | ${audit.scopeKey} | ${audit.operator} | ${audit.reason}`
  ));
  return [
    '# PatchPilot Admin Audit Report',
    '',
    `- Filters: \`${filterSummary || 'none'}\``,
    `- Events: ${audits.length}`,
    '',
    '## Events',
    '',
    ...(lines.length > 0 ? lines : ['No admin audit events matched the current filters.'])
  ].join('\n');
}
