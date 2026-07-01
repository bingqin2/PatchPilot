import { Archive, Copy, Download, RefreshCw } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoReadinessStatus,
  ExternalExposureHandoffPackage,
  ExternalExposureReadiness,
  ExternalExposureReadinessArchive,
  ExternalExposureReadinessCheck
} from '../../types';
import { compactDateTime } from '../format';

interface ExternalExposureReadinessPanelProps {
  readiness: ExternalExposureReadiness | null;
  error: string | null;
  archives: ExternalExposureReadinessArchive[];
  archiveError: string | null;
  handoffPackage: ExternalExposureHandoffPackage | null;
  handoffPackageError: string | null;
  onArchiveReadiness: () => Promise<ExternalExposureReadinessArchive>;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadHandoffPackageReport: () => Promise<Blob>;
  onRefresh: () => Promise<void> | void;
}

export function ExternalExposureReadinessPanel({
  readiness,
  error,
  archives,
  archiveError,
  handoffPackage,
  handoffPackageError,
  onArchiveReadiness,
  onDownloadArchiveReport,
  onDownloadHandoffPackageReport,
  onRefresh
}: ExternalExposureReadinessPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);

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

  async function archiveReadiness() {
    try {
      await onArchiveReadiness();
      setArchiveStatus('Exposure readiness archived');
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function downloadArchiveReport(archiveId: string) {
    try {
      const report = await onDownloadArchiveReport(archiveId);
      downloadMarkdown(report, `patchpilot-external-exposure-readiness-${archiveId}.md`);
      setDownloadStatus('Exposure readiness archive downloaded');
    } catch {
      setDownloadStatus('Archive download failed');
    }
  }

  async function downloadHandoffPackageReport() {
    try {
      const report = await onDownloadHandoffPackageReport();
      downloadMarkdown(report, 'patchpilot-external-exposure-handoff-package.md');
      setDownloadStatus('Exposure handoff package downloaded');
    } catch {
      setDownloadStatus('Handoff package download failed');
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
          <button
            className="secondary-button"
            type="button"
            onClick={() => void archiveReadiness()}
            aria-label="Archive exposure readiness"
            disabled={!readiness}
          >
            <Archive size={16} />
            Archive
          </button>
          <button className="secondary-button" type="button" onClick={() => void onRefresh()}>
            <RefreshCw size={16} />
            Refresh exposure gate
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
          {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
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

      <ExternalExposureHandoffPackageResult
        handoffPackage={handoffPackage}
        error={handoffPackageError}
        onDownloadReport={downloadHandoffPackageReport}
      />

      <div className="demo-launch-preflight-actions">
        <h3>Recent exposure readiness archives</h3>
        {archiveError ? (
          <div className="adapter-api-error">
            <strong>Exposure readiness archive unavailable</strong>
            <span>{archiveError}</span>
          </div>
        ) : null}
        {archives.length === 0 ? (
          <p className="empty-state">No exposure readiness archives recorded.</p>
        ) : (
          <div className="demo-evidence-records">
            {archives.map((archive) => (
              <div key={archive.id}>
                <span>{archive.id}</span>
                <strong>{archive.status}</strong>
                <small>{archive.safeToExpose ? 'Safe to expose' : 'Not safe to expose'}</small>
                <small>{archive.summary}</small>
                <small>
                  {archive.readyCount} ready, {archive.needsAttentionCount} attention, {archive.blockedCount} blocked
                </small>
                <small>{compactDateTime(archive.createdAt)}</small>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => void downloadArchiveReport(archive.id)}
                  aria-label={`Download exposure readiness archive ${archive.id}`}
                >
                  <Download size={14} />
                  Download archive
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
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

function ExternalExposureHandoffPackageResult({
  handoffPackage,
  error,
  onDownloadReport
}: {
  handoffPackage: ExternalExposureHandoffPackage | null;
  error: string | null;
  onDownloadReport: () => Promise<void>;
}) {
  return (
    <div className="demo-launch-preflight-actions">
      <div className="panel-subheader">
        <div>
          <h3>External exposure handoff package</h3>
          <p>{handoffPackage?.summary ?? 'Loading shareable exposure handoff evidence'}</p>
        </div>
        <button
          className="secondary-button"
          type="button"
          onClick={() => void onDownloadReport()}
          aria-label="Download exposure handoff package"
          disabled={!handoffPackage}
        >
          <Download size={14} />
          Download handoff
        </button>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>External exposure handoff package unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {handoffPackage ? (
        <div className={`demo-launch-preflight-result demo-launch-preflight-result-${statusClass(handoffPackage.status)}`}>
          <div className="demo-launch-preflight-summary">
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(handoffPackage.status)}`}>
              {handoffPackage.status}
            </span>
            <strong>{handoffPackage.handoffReady ? 'Ready to share' : 'Not ready to share'}</strong>
            <p>{handoffPackage.nextAction}</p>
            <small>
              Generated {compactDateTime(handoffPackage.generatedAt)} · Archive freshness: {handoffPackage.archiveFreshness}
            </small>
          </div>

          <div className="demo-launch-preflight-grid">
            <div>
              <span>Current readiness</span>
              <strong>{handoffPackage.readinessStatus}</strong>
            </div>
            <div>
              <span>Current safety</span>
              <strong>{handoffPackage.readinessSafeToExpose ? 'Safe to expose' : 'Not safe to expose'}</strong>
            </div>
            <div>
              <span>Latest archive</span>
              <strong>{handoffPackage.latestArchiveId ?? 'Missing'}</strong>
            </div>
            <div>
              <span>Archived safety</span>
              <strong>
                {handoffPackage.latestArchiveSafeToExpose === null
                  ? 'No archive'
                  : handoffPackage.latestArchiveSafeToExpose
                    ? 'Safe to expose'
                    : 'Not safe to expose'}
              </strong>
            </div>
          </div>

          <p className="demo-launch-preflight-blocked">{handoffPackage.sideEffectContract}</p>

          <div className="demo-evidence-records">
            {handoffPackage.evidenceNotes.map((note) => (
              <div key={note}>
                <span>Evidence</span>
                <small>{note}</small>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p className="empty-state">No external exposure handoff package loaded.</p>
      )}
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

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
