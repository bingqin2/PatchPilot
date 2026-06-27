import { Download } from 'lucide-react';
import { useState } from 'react';
import type { DemoReadinessStatus, DemoSelfHostedLaunchReadiness } from '../../types';
import { compactDateTime } from '../format';

interface SelfHostedLaunchReadinessPanelProps {
  readiness: DemoSelfHostedLaunchReadiness | null;
  error: string | null;
  onDownloadReport: () => Promise<Blob>;
}

export function SelfHostedLaunchReadinessPanel({
  readiness,
  error,
  onDownloadReport
}: SelfHostedLaunchReadinessPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);

  async function downloadReport() {
    try {
      const report = await onDownloadReport();
      downloadMarkdown(report, 'patchpilot-self-hosted-launch-readiness.md');
      setDownloadStatus('Launch readiness report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  return (
    <section className="panel self-hosted-launch-panel" aria-label="Self-hosted launch readiness">
      <div className="panel-header">
        <div>
          <h2>Self-hosted launch readiness</h2>
          <p>{readiness?.summary ?? 'Loading self-hosted launch readiness'}</p>
        </div>
        <div className="demo-evidence-header-actions">
          {readiness ? (
            <>
              <span className={`demo-readiness-status demo-readiness-status-${statusClass(readiness.status)}`}>
                {statusLabel(readiness.status)}
              </span>
              <span className={`demo-readiness-status demo-readiness-status-${readiness.readyToLaunch ? 'ready' : 'needs-attention'}`}>
                {readiness.readyToLaunch ? 'Ready to launch' : 'Not ready'}
              </span>
            </>
          ) : null}
          <button
            className="secondary-button"
            type="button"
            onClick={() => void downloadReport()}
            aria-label="Download launch readiness report"
          >
            <Download size={14} />
            Download report
          </button>
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Self-hosted launch readiness unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {readiness ? (
        <>
          <div className="demo-evidence-records">
            {readiness.checks.map((check) => (
              <div key={check.name}>
                <span>{check.name}</span>
                <strong>{statusLabel(check.status)}</strong>
                <small>{check.message}</small>
                <small>{check.action}</small>
              </div>
            ))}
            <div>
              <span>Generated</span>
              <strong>{compactDateTime(readiness.generatedAt)}</strong>
              <small>Current-state launch package</small>
            </div>
          </div>

          <div className="demo-evidence-actions">
            <h3>Launch next actions</h3>
            <ul>
              {readiness.nextActions.map((action) => (
                <li key={action}>{action}</li>
              ))}
            </ul>
          </div>
        </>
      ) : (
        <div className="empty-state">Self-hosted launch readiness has not loaded yet.</div>
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

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}
