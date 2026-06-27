import { Archive, Download } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoReadinessStatus,
  DemoSelfHostedLaunchReadiness,
  DemoSelfHostedLaunchReadinessArchive
} from '../../types';
import { compactDateTime } from '../format';

interface SelfHostedLaunchReadinessPanelProps {
  readiness: DemoSelfHostedLaunchReadiness | null;
  error: string | null;
  archives: DemoSelfHostedLaunchReadinessArchive[];
  archiveError: string | null;
  onArchiveReadiness: () => Promise<DemoSelfHostedLaunchReadinessArchive>;
  onDownloadReport: () => Promise<Blob>;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
}

export function SelfHostedLaunchReadinessPanel({
  readiness,
  error,
  archives,
  archiveError,
  onArchiveReadiness,
  onDownloadReport,
  onDownloadArchiveReport
}: SelfHostedLaunchReadinessPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);

  async function downloadReport() {
    try {
      const report = await onDownloadReport();
      downloadMarkdown(report, 'patchpilot-self-hosted-launch-readiness.md');
      setDownloadStatus('Launch readiness report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function archiveReadiness() {
    try {
      await onArchiveReadiness();
      setArchiveStatus('Launch readiness archived');
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function downloadArchiveReport(archiveId: string) {
    try {
      const report = await onDownloadArchiveReport(archiveId);
      downloadMarkdown(report, `patchpilot-self-hosted-launch-readiness-${archiveId}.md`);
      setDownloadStatus('Launch readiness archive downloaded');
    } catch {
      setDownloadStatus('Archive download failed');
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
            onClick={() => void archiveReadiness()}
            aria-label="Archive launch readiness"
            disabled={!readiness}
          >
            <Archive size={14} />
            Archive
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => void downloadReport()}
            aria-label="Download launch readiness report"
          >
            <Download size={14} />
            Download report
          </button>
          {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
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

          <div className="demo-evidence-actions">
            <h3>Recent launch readiness archives</h3>
            {archiveError ? (
              <div className="adapter-api-error">
                <strong>Launch readiness archive unavailable</strong>
                <span>{archiveError}</span>
              </div>
            ) : null}
            {archives.length === 0 ? (
              <p className="empty-state">No launch readiness archives recorded.</p>
            ) : (
              <div className="demo-evidence-records">
                {archives.map((archive) => (
                  <div key={archive.id}>
                    <span>{archive.id}</span>
                    <strong>{statusLabel(archive.status)}</strong>
                    <small>{archive.summary}</small>
                    <small>
                      {archive.readyCheckCount} ready, {archive.needsAttentionCheckCount} attention, {archive.blockedCheckCount} blocked
                    </small>
                    <small>{compactDateTime(archive.createdAt)}</small>
                    <button
                      className="secondary-button"
                      type="button"
                      onClick={() => void downloadArchiveReport(archive.id)}
                      aria-label={`Download launch readiness archive ${archive.id}`}
                    >
                      <Download size={14} />
                      Download archive
                    </button>
                  </div>
                ))}
              </div>
            )}
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
