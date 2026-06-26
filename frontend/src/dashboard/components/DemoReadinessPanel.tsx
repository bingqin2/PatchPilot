import { Archive, Copy, Download } from 'lucide-react';
import { useState } from 'react';
import type { DemoReadiness, DemoReadinessSnapshotArchive, DemoReadinessStatus } from '../../types';
import { compactDateTime } from '../format';

interface DemoReadinessPanelProps {
  readiness: DemoReadiness | null;
  error: string | null;
  snapshots?: DemoReadinessSnapshotArchive[];
  snapshotError?: string | null;
  onArchiveReadiness?: () => Promise<DemoReadinessSnapshotArchive>;
  onDownloadSnapshotReport?: (snapshotId: string) => Promise<Blob>;
}

export function DemoReadinessPanel({
  readiness,
  error,
  snapshots = [],
  snapshotError = null,
  onArchiveReadiness,
  onDownloadSnapshotReport
}: DemoReadinessPanelProps) {
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);

  async function archiveReadiness() {
    if (!onArchiveReadiness) {
      return;
    }
    try {
      await onArchiveReadiness();
      setArchiveStatus('Demo readiness snapshot archived');
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function copySnapshotReport(snapshot: DemoReadinessSnapshotArchive) {
    try {
      await navigator.clipboard.writeText(snapshot.report);
      setCopyStatus('Readiness snapshot report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadSnapshotReport(snapshot: DemoReadinessSnapshotArchive) {
    if (!onDownloadSnapshotReport) {
      return;
    }
    try {
      const report = await onDownloadSnapshotReport(snapshot.id);
      downloadMarkdown(report, `patchpilot-demo-readiness-${snapshot.id}.md`);
      setDownloadStatus('Readiness snapshot report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  return (
    <section className="panel demo-readiness-panel" aria-label="Demo readiness">
      <div className="panel-header">
        <div>
          <h2>Demo readiness</h2>
          <p>{readiness?.summary ?? 'Loading demo readiness'}</p>
        </div>
        {readiness ? (
          <div className="demo-readiness-header-meta">
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(readiness.status)}`}>
              {statusLabel(readiness.status)}
            </span>
            {onArchiveReadiness ? (
              <button className="secondary-button" type="button" onClick={() => void archiveReadiness()}>
                <Archive size={14} />
                Archive readiness
              </button>
            ) : null}
            {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
            {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
            {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
          </div>
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
                  {check.action && check.action !== 'No action needed.' ? (
                    <p className="demo-readiness-check-action">{check.action}</p>
                  ) : null}
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

          <div className="demo-readiness-snapshots">
            <h3>Recent readiness snapshots</h3>
            {snapshotError ? (
              <div className="adapter-api-error">
                <strong>Demo readiness snapshots unavailable</strong>
                <span>{snapshotError}</span>
              </div>
            ) : null}
            {snapshots.length ? (
              <ul>
                {snapshots.map((snapshot) => (
                  <li key={snapshot.id}>
                    <div>
                      <strong>{snapshot.id}</strong>
                      <span>{snapshot.summary}</span>
                      <small>
                        {snapshot.readyCheckCount} ready / {snapshot.needsAttentionCheckCount} warning / {snapshot.blockedCheckCount} blocked
                      </small>
                    </div>
                    <div className="demo-readiness-snapshot-actions">
                      <span className={`demo-readiness-status demo-readiness-status-${statusClass(snapshot.status)}`}>
                        {statusLabel(snapshot.status)}
                      </span>
                      <time dateTime={snapshot.createdAt}>{compactDateTime(snapshot.createdAt)}</time>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void copySnapshotReport(snapshot)}
                        aria-label={`Copy readiness snapshot report ${snapshot.id}`}
                      >
                        <Copy size={14} />
                        Copy report
                      </button>
                      {onDownloadSnapshotReport ? (
                        <button
                          className="secondary-button"
                          type="button"
                          onClick={() => void downloadSnapshotReport(snapshot)}
                          aria-label={`Download readiness snapshot report ${snapshot.id}`}
                        >
                          <Download size={14} />
                          Download report
                        </button>
                      ) : null}
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No demo readiness snapshots recorded.</p>
            )}
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

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}
