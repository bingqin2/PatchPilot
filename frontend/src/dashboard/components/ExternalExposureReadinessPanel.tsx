import { Copy, RefreshCw } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoReadinessStatus,
  ExternalExposureReadiness,
  ExternalExposureReadinessCheck
} from '../../types';
import { compactDateTime } from '../format';

interface ExternalExposureReadinessPanelProps {
  readiness: ExternalExposureReadiness | null;
  error: string | null;
  onRefresh: () => Promise<void> | void;
}

export function ExternalExposureReadinessPanel({
  readiness,
  error,
  onRefresh
}: ExternalExposureReadinessPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);

  async function copyReport() {
    if (!readiness) {
      return;
    }
    try {
      await navigator.clipboard?.writeText(readiness.markdownReport);
      setCopyStatus('Exposure report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  return (
    <section className="panel external-exposure-readiness-panel" aria-label="External exposure readiness">
      <div className="panel-header">
        <div>
          <h2>External exposure readiness</h2>
          <p>{readiness?.summary ?? 'Loading temporary public URL safety gate'}</p>
        </div>
        <div className="demo-readiness-header-meta">
          {readiness ? (
            <button className="secondary-button" type="button" onClick={() => void copyReport()}>
              <Copy size={16} />
              Copy exposure report
            </button>
          ) : null}
          <button className="secondary-button" type="button" onClick={() => void onRefresh()}>
            <RefreshCw size={16} />
            Refresh exposure gate
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>External exposure readiness unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {readiness ? (
        <ExternalExposureReadinessResult readiness={readiness} />
      ) : (
        <div className="empty-state">No external exposure readiness loaded.</div>
      )}
    </section>
  );
}

function ExternalExposureReadinessResult({ readiness }: { readiness: ExternalExposureReadiness }) {
  const tone = statusClass(readiness.status);

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {readiness.status}
        </span>
        <strong>{readiness.safeToExpose ? 'Safe to expose' : 'Not safe to expose'}</strong>
        <p>{readiness.summary}</p>
        <small>
          Generated {compactDateTime(readiness.generatedAt)} · {readiness.totalCount} checks
        </small>
      </div>

      <div className="demo-launch-preflight-grid">
        <div>
          <span>Ready</span>
          <strong>{readiness.readyCount} ready</strong>
        </div>
        <div>
          <span>Needs attention</span>
          <strong>{readiness.needsAttentionCount} warning</strong>
        </div>
        <div>
          <span>Blocked</span>
          <strong>{readiness.blockedCount} blocked</strong>
        </div>
        <div>
          <span>Total</span>
          <strong>{readiness.totalCount} checks</strong>
        </div>
      </div>

      <p className="demo-launch-preflight-blocked">{readiness.sideEffectContract}</p>

      <div className="operator-setup-grid">
        {readiness.checks.map((check) => (
          <ExternalExposureCheckCard check={check} key={check.name} />
        ))}
      </div>

      <div className="demo-launch-preflight-actions">
        <h3>Next actions</h3>
        <ul>
          {readiness.nextActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function ExternalExposureCheckCard({ check }: { check: ExternalExposureReadinessCheck }) {
  const tone = statusClass(check.status);

  return (
    <div className={`operator-setup-check operator-setup-check-${tone}`}>
      <span>{check.status}</span>
      <strong>{check.name}</strong>
      <p>{check.summary}</p>
      <small>{check.nextAction}</small>
    </div>
  );
}

function statusClass(status: DemoReadinessStatus) {
  if (status === 'READY') {
    return 'ready';
  }
  if (status === 'BLOCKED') {
    return 'blocked';
  }
  return 'attention';
}
