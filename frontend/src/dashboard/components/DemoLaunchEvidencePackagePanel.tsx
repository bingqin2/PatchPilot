import { Copy, Download } from 'lucide-react';
import { useState } from 'react';
import type { DemoLaunchEvidencePackage, DemoReadinessStatus } from '../../types';
import { compactDateTime } from '../format';

interface DemoLaunchEvidencePackagePanelProps {
  evidencePackage: DemoLaunchEvidencePackage | null;
  error: string | null;
  onDownloadReport: () => Promise<Blob>;
}

export function DemoLaunchEvidencePackagePanel({
  evidencePackage,
  error,
  onDownloadReport
}: DemoLaunchEvidencePackagePanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);

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
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Demo launch evidence package unavailable</strong>
          <span>{error}</span>
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
        </>
      ) : (
        <div className="empty-state">Demo launch evidence package has not loaded yet.</div>
      )}
    </section>
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
