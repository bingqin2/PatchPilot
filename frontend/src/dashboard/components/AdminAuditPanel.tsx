import type { OperatorSafetyAudit } from '../../types';
import { actionLabel } from '../actionLabels';
import { compactTime } from '../format';

interface AdminAuditPanelProps {
  audits: OperatorSafetyAudit[];
  error: string | null;
}

export function AdminAuditPanel({ audits, error }: AdminAuditPanelProps) {
  return (
    <section className="panel admin-audit-panel" aria-label="Admin audit trail">
      <div className="panel-header">
        <div>
          <h2>Admin audit trail</h2>
          <p>
            {audits.length} recent admin {audits.length === 1 ? 'event' : 'events'}
          </p>
        </div>
      </div>
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
