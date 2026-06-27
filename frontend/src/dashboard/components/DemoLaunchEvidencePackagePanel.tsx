import { Archive, Copy, Download } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoLaunchEvidencePackage,
  DemoLaunchEvidencePackageArchive,
  DemoLaunchEvidenceShareCenter,
  DemoReadinessStatus
} from '../../types';
import { compactDateTime } from '../format';

interface DemoLaunchEvidencePackagePanelProps {
  evidencePackage: DemoLaunchEvidencePackage | null;
  error: string | null;
  archives: DemoLaunchEvidencePackageArchive[];
  archiveError: string | null;
  shareCenter: DemoLaunchEvidenceShareCenter | null;
  shareCenterError: string | null;
  onArchivePackage: () => Promise<DemoLaunchEvidencePackageArchive>;
  onDownloadReport: () => Promise<Blob>;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadShareCenterReport: () => Promise<Blob>;
}

export function DemoLaunchEvidencePackagePanel({
  evidencePackage,
  error,
  archives,
  archiveError,
  shareCenter,
  shareCenterError,
  onArchivePackage,
  onDownloadReport,
  onDownloadArchiveReport,
  onDownloadShareCenterReport
}: DemoLaunchEvidencePackagePanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);

  async function copyReport() {
    if (!evidencePackage) {
      return;
    }
    try {
      await navigator.clipboard.writeText(evidencePackage.markdownReport);
      setCopyStatus('Launch evidence report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadReport() {
    try {
      const report = await onDownloadReport();
      downloadMarkdown(report, 'patchpilot-demo-launch-evidence-package.md');
      setDownloadStatus('Launch evidence report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function archivePackage() {
    try {
      const archive = await onArchivePackage();
      setArchiveStatus(`Archived launch package ${archive.id}`);
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function downloadArchiveReport(archive: DemoLaunchEvidencePackageArchive) {
    try {
      const report = await onDownloadArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-demo-launch-evidence-package-${archive.id}.md`);
      setArchiveStatus(`Archived report ${archive.id} downloaded`);
    } catch {
      setArchiveStatus('Archived report download failed');
    }
  }

  async function downloadShareCenterReport() {
    try {
      const report = await onDownloadShareCenterReport();
      downloadMarkdown(report, 'patchpilot-demo-launch-evidence-share-center.md');
      setDownloadStatus('Launch evidence share center downloaded');
    } catch {
      setDownloadStatus('Launch evidence share center download failed');
    }
  }

  return (
    <section className="panel demo-launch-evidence-package-panel" aria-label="Demo launch evidence package">
      <div className="panel-header">
        <div>
          <h2>Demo launch evidence package</h2>
          <p>{evidencePackage?.summary ?? 'Loading demo launch evidence package'}</p>
        </div>
        <div className="demo-evidence-header-actions">
          {evidencePackage ? (
            <>
              <span className={`demo-readiness-status demo-readiness-status-${statusClass(evidencePackage.status)}`}>
                {statusLabel(evidencePackage.status)}
              </span>
              <span className={`demo-readiness-status demo-readiness-status-${evidencePackage.readyToShare ? 'ready' : 'needs-attention'}`}>
                {evidencePackage.readyToShare ? 'Ready to share' : 'Not share-ready'}
              </span>
            </>
          ) : null}
          <button
            className="secondary-button"
            type="button"
            onClick={() => void archivePackage()}
            aria-label="Archive launch evidence package"
            disabled={!evidencePackage}
          >
            <Archive size={14} />
            Archive package
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => void copyReport()}
            aria-label="Copy launch evidence report"
            disabled={!evidencePackage}
          >
            <Copy size={14} />
            Copy report
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => void downloadReport()}
            aria-label="Download launch evidence report"
          >
            <Download size={14} />
            Download report
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
          {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Demo launch evidence package unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {archiveError ? (
        <div className="adapter-api-error">
          <strong>Archived launch evidence unavailable</strong>
          <span>{archiveError}</span>
        </div>
      ) : null}

      {shareCenterError ? (
        <div className="adapter-api-error">
          <strong>Launch evidence share center unavailable</strong>
          <span>{shareCenterError}</span>
        </div>
      ) : null}

      {evidencePackage ? (
        <>
          <div className="demo-evidence-grid">
            <EvidenceStat label="Session" value={evidencePackage.sessionId} detail={compactDateTime(evidencePackage.generatedAt)} />
            <EvidenceStat label="Task" value={evidencePackage.latestTaskId ?? 'Missing'} detail="latest completed task evidence" />
            <EvidenceStat label="Webhook" value={evidencePackage.latestWebhookDeliveryId ?? 'Missing'} detail="latest delivery evidence" />
            <EvidenceStat label="Evaluation" value={evidencePackage.evaluationRunId ?? 'Missing'} detail={csv(evidencePackage.evaluationCoverage)} />
          </div>

          <div className="demo-evidence-records">
            <div>
              <span>Pre-launch checks</span>
              <strong>{statusLabel(evidencePackage.launchReadinessStatus)}</strong>
              {evidencePackage.preLaunchChecks.map((check) => (
                <small key={check.name}>
                  {check.name}: {statusLabel(check.status)}
                </small>
              ))}
            </div>
            <div>
              <span>Evidence bundle</span>
              <strong>{statusLabel(evidencePackage.evidenceBundleStatus)}</strong>
              <small>Session {evidencePackage.sessionId}</small>
            </div>
            <div>
              <span>Handoff finalization</span>
              <strong>{statusLabel(evidencePackage.handoffFinalizationStatus)}</strong>
              {evidencePackage.postDemoProof.map((proof) => (
                <small key={proof}>{proof}</small>
              ))}
            </div>
            <div>
              <span>Pull Request</span>
              {evidencePackage.latestPullRequestUrl ? (
                <a href={evidencePackage.latestPullRequestUrl} target="_blank" rel="noreferrer">
                  Open launch Pull Request
                </a>
              ) : (
                <strong>Missing</strong>
              )}
              <small>{evidencePackage.latestPullRequestUrl ?? 'No Pull Request evidence recorded.'}</small>
            </div>
          </div>

          <EvidenceList title="Live run proof" items={evidencePackage.liveRunProof} />
          <EvidenceList title="Post-demo proof" items={evidencePackage.postDemoProof} />
          <EvidenceList title="Launch next actions" items={evidencePackage.nextActions} />
          <EvidenceList title="Side effect contract" items={evidencePackage.healthContract} />
          <LaunchEvidenceArchiveList
            archives={archives}
            onDownloadArchiveReport={(archive) => void downloadArchiveReport(archive)}
          />
          <LaunchEvidenceShareCenterPanel
            shareCenter={shareCenter}
            onDownloadShareCenterReport={() => void downloadShareCenterReport()}
          />
        </>
      ) : (
        <div className="empty-state">Demo launch evidence package has not loaded yet.</div>
      )}
    </section>
  );
}

function LaunchEvidenceShareCenterPanel({
  shareCenter,
  onDownloadShareCenterReport
}: {
  shareCenter: DemoLaunchEvidenceShareCenter | null;
  onDownloadShareCenterReport: () => void;
}) {
  return (
    <div className="demo-evidence-actions">
      <div className="demo-evidence-list-header">
        <div>
          <h3>Launch evidence share center</h3>
          <p>{shareCenter?.summary ?? 'Launch evidence share center has not loaded yet.'}</p>
        </div>
        <button
          className="secondary-button"
          type="button"
          onClick={onDownloadShareCenterReport}
          aria-label="Download launch evidence share center"
          disabled={!shareCenter}
        >
          <Download size={14} />
          Download share center
        </button>
      </div>
      {shareCenter ? (
        <>
          <div className="demo-evidence-records">
            <div>
              <span>Status</span>
              <strong>{shareCenterStatusLabel(shareCenter.status)}</strong>
              <small>{shareCenter.shareReady ? 'Ready to share' : 'Not share-ready'}</small>
            </div>
            <div>
              <span>Latest archive</span>
              <strong>{shareCenter.latestArchiveId ?? 'Missing'}</strong>
              <small>{shareCenter.latestCreatedAt ? compactDateTime(shareCenter.latestCreatedAt) : 'No archive captured'}</small>
            </div>
            <div>
              <span>Session</span>
              <strong>{shareCenter.latestSessionId ?? 'Missing'}</strong>
              <small>{shareCenter.archiveCount} archived launch packages</small>
            </div>
            <div>
              <span>Next action</span>
              <strong>{shareCenter.nextAction}</strong>
            </div>
          </div>
          {shareCenter.latestPullRequestUrl ? (
            <a href={shareCenter.latestPullRequestUrl} target="_blank" rel="noreferrer">
              Open archived Pull Request
            </a>
          ) : null}
          <EvidenceList title="Share center downloads" items={shareCenter.downloadActions} />
          <EvidenceList title="Share center evidence" items={shareCenter.evidenceNotes} />
        </>
      ) : (
        <p>No launch evidence share center loaded.</p>
      )}
    </div>
  );
}

function LaunchEvidenceArchiveList({
  archives,
  onDownloadArchiveReport
}: {
  archives: DemoLaunchEvidencePackageArchive[];
  onDownloadArchiveReport: (archive: DemoLaunchEvidencePackageArchive) => void;
}) {
  return (
    <div className="demo-evidence-actions">
      <h3>Recent archived launch packages</h3>
      {archives.length === 0 ? (
        <p>No archived launch evidence packages yet.</p>
      ) : (
        <ul>
          {archives.map((archive) => (
            <li key={archive.id}>
              <span>
                {archive.id} · {statusLabel(archive.status)} · {archive.sessionId} · {compactDateTime(archive.createdAt)}
              </span>
              {archive.latestPullRequestUrl ? (
                <a href={archive.latestPullRequestUrl} target="_blank" rel="noreferrer">
                  PR
                </a>
              ) : null}
              <button
                className="secondary-button"
                type="button"
                onClick={() => onDownloadArchiveReport(archive)}
                aria-label={`Download archived launch evidence report ${archive.id}`}
              >
                <Download size={14} />
                Download archived report
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

function EvidenceStat({ label, value, detail }: { label: string; value: string; detail: string }) {
  return (
    <div className="metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{detail}</small>
    </div>
  );
}

function EvidenceList({ title, items }: { title: string; items: string[] }) {
  return (
    <div className="demo-evidence-actions">
      <h3>{title}</h3>
      <ul>
        {items.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </div>
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

function shareCenterStatusLabel(status: DemoLaunchEvidenceShareCenter['status']) {
  if (status === 'NO_ARCHIVE') {
    return 'No archive';
  }
  return statusLabel(status);
}

function statusClass(status: DemoReadinessStatus) {
  return status.toLowerCase().replace('_', '-');
}

function csv(values: string[]) {
  return values.length === 0 ? 'none' : values.join(', ');
}

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}
