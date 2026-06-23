import type { DemoSmokeChecklist, DemoSmokeChecklistStatus } from '../../types';

interface DemoSmokeChecklistPanelProps {
  checklist: DemoSmokeChecklist | null;
  error: string | null;
}

export function DemoSmokeChecklistPanel({ checklist, error }: DemoSmokeChecklistPanelProps) {
  return (
    <section className="panel demo-smoke-panel" aria-label="Live demo smoke checklist">
      <div className="panel-header">
        <div>
          <h2>Live demo smoke checklist</h2>
          <p>{checklist?.summary ?? 'Loading live demo smoke checklist'}</p>
        </div>
        {checklist ? (
          <span className={`demo-readiness-status demo-readiness-status-${statusClass(checklist.status)}`}>
            {statusLabel(checklist.status)}
          </span>
        ) : null}
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Smoke checklist unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {checklist ? (
        <>
          <ol className="demo-smoke-steps">
            {[...checklist.steps].sort((left, right) => left.order - right.order).map((step) => (
              <li className={`demo-smoke-step demo-smoke-step-${statusClass(step.status)}`} key={step.name}>
                <div className="demo-smoke-step-index">{step.order}</div>
                <div>
                  <div className="demo-smoke-step-title">
                    <strong>{step.name}</strong>
                    <span>{statusLabel(step.status)}</span>
                  </div>
                  <p>{step.message}</p>
                  <code>{step.evidence}</code>
                  <small>{step.action}</small>
                </div>
              </li>
            ))}
          </ol>

          <div className="demo-smoke-actions">
            <h3>Smoke next actions</h3>
            <ul>
              {checklist.nextActions.map((action) => (
                <li key={action}>{action}</li>
              ))}
            </ul>
          </div>
        </>
      ) : (
        <div className="empty-state">Live demo smoke checklist has not loaded yet.</div>
      )}
    </section>
  );
}

function statusLabel(status: DemoSmokeChecklistStatus) {
  switch (status) {
    case 'READY':
      return 'Ready';
    case 'NEEDS_ATTENTION':
      return 'Needs attention';
    case 'BLOCKED':
      return 'Blocked';
  }
}

function statusClass(status: DemoSmokeChecklistStatus) {
  return status.toLowerCase().replace('_', '-');
}
