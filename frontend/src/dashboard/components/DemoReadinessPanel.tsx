import type { DemoReadiness, DemoReadinessStatus } from '../../types';

interface DemoReadinessPanelProps {
  readiness: DemoReadiness | null;
  error: string | null;
}

export function DemoReadinessPanel({ readiness, error }: DemoReadinessPanelProps) {
  return (
    <section className="panel demo-readiness-panel" aria-label="Demo readiness">
      <div className="panel-header">
        <div>
          <h2>Demo readiness</h2>
          <p>{readiness?.summary ?? 'Loading demo readiness'}</p>
        </div>
        {readiness ? (
          <span className={`demo-readiness-status demo-readiness-status-${statusClass(readiness.status)}`}>
            {statusLabel(readiness.status)}
          </span>
        ) : null}
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Demo readiness unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {readiness ? (
        <>
          <ul className="demo-readiness-checks">
            {readiness.checks.map((check) => (
              <li
                className={`demo-readiness-check demo-readiness-check-${statusClass(check.status)}`}
                key={check.name}
                aria-label={`${check.name} ${statusLabel(check.status)}`}
              >
                <div>
                  <strong>{check.name}</strong>
                  <p>{check.message}</p>
                </div>
                <span className="demo-readiness-check-marker" aria-hidden="true" />
              </li>
            ))}
          </ul>

          <div className="demo-readiness-actions">
            <h3>Next actions</h3>
            <ul>
              {readiness.nextActions.map((action) => (
                <li key={action}>{action}</li>
              ))}
            </ul>
          </div>
        </>
      ) : (
        <div className="empty-state">Demo readiness has not loaded yet.</div>
      )}
    </section>
  );
}

function statusLabel(status: DemoReadinessStatus) {
  switch (status) {
    case 'READY':
      return 'Ready';
    case 'NEEDS_ATTENTION':
      return 'Needs attention';
    case 'BLOCKED':
      return 'Blocked';
  }
}

function statusClass(status: DemoReadinessStatus) {
  return status.toLowerCase().replace('_', '-');
}
