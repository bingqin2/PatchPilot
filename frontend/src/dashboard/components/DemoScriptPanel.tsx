import type { DemoReadinessStatus, DemoScript } from '../../types';
import { compactDateTime } from '../format';

interface DemoScriptPanelProps {
  script: DemoScript | null;
  error: string | null;
}

export function DemoScriptPanel({ script, error }: DemoScriptPanelProps) {
  return (
    <section className="panel demo-script-panel" aria-label="Demo script">
      <div className="panel-header">
        <div>
          <h2>Demo script</h2>
          <p>{script?.summary ?? 'Loading demo script'}</p>
        </div>
        {script ? (
          <div className="demo-script-header-meta">
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(script.status)}`}>
              {statusLabel(script.status)}
            </span>
            <time dateTime={script.generatedAt}>{compactDateTime(script.generatedAt)}</time>
          </div>
        ) : null}
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Demo script unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {script ? (
        <>
          <ol className="demo-script-steps">
            {[...script.steps].sort((left, right) => left.order - right.order).map((step) => (
              <li className={`demo-script-step demo-script-step-${statusClass(step.status)}`} key={step.name}>
                <div className="demo-script-step-index">{step.order}</div>
                <div className="demo-script-step-body">
                  <div className="demo-script-step-title">
                    <strong>{step.name}</strong>
                    <span>{statusLabel(step.status)}</span>
                  </div>
                  <p>{step.operatorAction}</p>
                  <code>{step.verificationCommand}</code>
                  <dl>
                    <div>
                      <dt>Success</dt>
                      <dd>{step.successCriteria}</dd>
                    </div>
                    <div>
                      <dt>Troubleshoot</dt>
                      <dd>{step.troubleshootingPanel}</dd>
                    </div>
                    <div>
                      <dt>Evidence</dt>
                      <dd>{step.evidence}</dd>
                    </div>
                  </dl>
                </div>
              </li>
            ))}
          </ol>

          <div className="demo-script-contract">
            <div>
              <h3>Health contract</h3>
              <ul>
                {script.healthContract.map((item) => (
                  <li key={item}>{item}</li>
                ))}
              </ul>
            </div>
            <div>
              <h3>Script next actions</h3>
              <ul>
                {script.nextActions.map((action) => (
                  <li key={action}>{action}</li>
                ))}
              </ul>
            </div>
          </div>
        </>
      ) : (
        <div className="empty-state">Demo script has not loaded yet.</div>
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
