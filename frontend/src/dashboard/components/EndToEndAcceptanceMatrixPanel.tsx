import { Copy, RefreshCw } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoEndToEndAcceptanceMatrix,
  DemoEndToEndAcceptanceMatrixItem,
  DemoReadinessStatus
} from '../../types';
import { compactDateTime } from '../format';

interface EndToEndAcceptanceMatrixPanelProps {
  matrix: DemoEndToEndAcceptanceMatrix | null;
  error: string | null;
  onRefresh: () => Promise<void> | void;
}

export function EndToEndAcceptanceMatrixPanel({
  matrix,
  error,
  onRefresh
}: EndToEndAcceptanceMatrixPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);

  async function copyReport() {
    if (!matrix) {
      return;
    }
    try {
      await navigator.clipboard?.writeText(matrix.markdownReport);
      setCopyStatus('Matrix report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  return (
    <section className="panel end-to-end-acceptance-panel" aria-label="End-to-end acceptance matrix">
      <div className="panel-header">
        <div>
          <h2>End-to-end acceptance</h2>
          <p>{matrix?.summary ?? 'Loading final demo acceptance matrix'}</p>
        </div>
        <div className="demo-readiness-header-meta">
          {matrix ? (
            <button className="secondary-button" type="button" onClick={() => void copyReport()}>
              <Copy size={16} />
              Copy matrix report
            </button>
          ) : null}
          <button className="secondary-button" type="button" onClick={() => void onRefresh()}>
            <RefreshCw size={16} />
            Refresh matrix
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>End-to-end acceptance unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {matrix ? (
        <EndToEndAcceptanceMatrixResult matrix={matrix} />
      ) : (
        <div className="empty-state">No end-to-end acceptance matrix loaded.</div>
      )}
    </section>
  );
}

function EndToEndAcceptanceMatrixResult({ matrix }: { matrix: DemoEndToEndAcceptanceMatrix }) {
  const tone = statusClass(matrix.status);

  return (
    <div className={`demo-launch-preflight-result demo-launch-preflight-result-${tone}`}>
      <div className="demo-launch-preflight-summary">
        <span className={`demo-readiness-status demo-readiness-status-${tone}`}>
          {matrix.status}
        </span>
        <strong>{matrix.readyForFinalDemo ? 'Ready for final demo' : 'Not ready for final demo'}</strong>
        <p>{matrix.summary}</p>
        <small>
          Generated {compactDateTime(matrix.generatedAt)} · {matrix.totalCount} checks
        </small>
      </div>

      <div className="demo-launch-preflight-grid">
        <div>
          <span>Readiness</span>
          <strong>{matrix.readinessPercent}%</strong>
        </div>
        <div>
          <span>Ready</span>
          <strong>{matrix.readyCount} ready</strong>
        </div>
        <div>
          <span>Needs attention</span>
          <strong>{matrix.needsAttentionCount} warning</strong>
        </div>
        <div>
          <span>Blocked</span>
          <strong>{matrix.blockedCount} blocked</strong>
        </div>
      </div>

      <p className="demo-launch-preflight-blocked">{matrix.sideEffectContract}</p>

      <div className="operator-setup-grid">
        {matrix.items.map((item) => (
          <AcceptanceMatrixItemCard item={item} key={`${item.category}:${item.name}`} />
        ))}
      </div>

      <div className="demo-launch-preflight-actions">
        <h3>Next actions</h3>
        <ul>
          {matrix.nextActions.map((action) => (
            <li key={action}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function AcceptanceMatrixItemCard({ item }: { item: DemoEndToEndAcceptanceMatrixItem }) {
  const tone = statusClass(item.status);

  return (
    <div className={`operator-setup-check operator-setup-check-${tone}`}>
      <span>{item.category}</span>
      <strong>{item.name}</strong>
      <p>{item.evidence}</p>
      <small>{item.gap}</small>
      <small>{item.nextAction}</small>
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
