import { Download, ExternalLink } from 'lucide-react';
import { useState } from 'react';
import type { DemoAcceptanceSummary, DemoReadinessStatus } from '../../types';
import { compactDateTime } from '../format';

interface DemoAcceptanceSummaryPanelProps {
  summary: DemoAcceptanceSummary | null;
  error: string | null;
  onDownloadReport: () => Promise<Blob>;
}

export function DemoAcceptanceSummaryPanel({
  summary,
  error,
  onDownloadReport
}: DemoAcceptanceSummaryPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);

  async function downloadReport() {
    try {
      const report = await onDownloadReport();
      downloadMarkdown(report, 'patchpilot-final-demo-acceptance-summary.md');
      setDownloadStatus('Final demo acceptance report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  return (
    <section className="panel demo-acceptance-summary-panel" aria-label="Final demo acceptance">
      <div className="panel-header">
        <div>
          <h2>Final demo acceptance</h2>
          <p>{summary?.summary ?? 'Loading final demo acceptance summary'}</p>
        </div>
        <div className="demo-evidence-header-actions">
          {summary ? (
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(summary.status)}`}>
              {statusLabel(summary.status)}
            </span>
          ) : null}
          <button className="secondary-button" type="button" onClick={() => void downloadReport()}>
            <Download size={14} />
            Download final acceptance report
          </button>
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Final demo acceptance unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {summary ? (
        <>
          <div className="demo-evidence-grid">
            <AcceptanceStat
              label="Acceptance"
              value={summary.accepted ? 'Accepted' : 'Not accepted'}
              detail={summary.nextAction}
            />
            <AcceptanceStat
              label="Launch certificate"
              value={summary.launchCertificateCertified ? 'Certified' : statusLabel(summary.launchCertificateStatus)}
              detail={summary.launchCertificateArchiveId ?? 'No launch certificate archive'}
            />
            <AcceptanceStat
              label="Task evidence certificate"
              value={summary.taskCertificateCertified ? 'Certified' : statusLabel(summary.taskCertificateStatus)}
              detail={summary.taskCertificateArchiveId ?? 'No task certificate archive'}
            />
            <AcceptanceStat
              label="Generated"
              value={compactDateTime(summary.generatedAt)}
              detail="Read-only summary"
            />
          </div>

          <div className="demo-evidence-records">
            <CertificateEvidence
              title="Launch acceptance certificate"
              status={summary.launchCertificateStatus}
              archived={summary.launchCertificateArchived}
              certified={summary.launchCertificateCertified}
              archiveId={summary.launchCertificateArchiveId}
              closeoutArchiveId={summary.launchCloseoutArchiveId}
              evidenceArchiveId={summary.launchEvidenceArchiveId}
              deliveryReceiptId={summary.launchDeliveryReceiptId}
            />
            <CertificateEvidence
              title="Task evidence acceptance certificate"
              status={summary.taskCertificateStatus}
              archived={summary.taskCertificateArchived}
              certified={summary.taskCertificateCertified}
              archiveId={summary.taskCertificateArchiveId}
              closeoutArchiveId={summary.taskCloseoutArchiveId}
              evidenceArchiveId={summary.taskEvidenceArchiveId}
              deliveryReceiptId={summary.taskDeliveryReceiptId}
            />
            <div>
              <span>Latest task</span>
              <strong>{summary.latestTaskId ?? 'No certified task'}</strong>
              {summary.latestPullRequestUrl ? (
                <a href={summary.latestPullRequestUrl} target="_blank" rel="noreferrer">
                  <ExternalLink size={13} />
                  Open Pull Request
                </a>
              ) : (
                <small>No certified Pull Request</small>
              )}
            </div>
            <div>
              <span>Read-only contract</span>
              <strong>No side effects</strong>
              <small>{summary.sideEffectContract}</small>
            </div>
          </div>

          <div className="demo-webhook-delivery-trail">
            <div className="section-heading">
              <h3>Acceptance checks</h3>
              <span>{summary.checks.length} checks</span>
            </div>
            {summary.checks.length === 0 ? (
              <p>No acceptance checks recorded.</p>
            ) : (
              <ul>
                {summary.checks.map((check) => (
                  <li key={check.name}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{check.name}</strong>
                      <span>{statusLabel(check.status)}</span>
                    </div>
                    <p>{check.summary}</p>
                    <small>{check.nextAction}</small>
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div className="demo-evidence-actions">
            <h3>Evidence notes</h3>
            <ul>
              {summary.evidenceNotes.map((note) => (
                <li key={note}>{note}</li>
              ))}
            </ul>
          </div>

          <div className="demo-evidence-actions">
            <h3>Download actions</h3>
            <ul>
              {summary.downloadActions.map((action) => (
                <li key={action}>{action}</li>
              ))}
            </ul>
          </div>
        </>
      ) : (
        <div className="empty-state">Final demo acceptance summary has not loaded yet.</div>
      )}
    </section>
  );
}

interface AcceptanceStatProps {
  label: string;
  value: string;
  detail: string;
}

function AcceptanceStat({ label, value, detail }: AcceptanceStatProps) {
  return (
    <div className="demo-evidence-stat">
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{detail}</small>
    </div>
  );
}

interface CertificateEvidenceProps {
  title: string;
  status: DemoReadinessStatus;
  archived: boolean;
  certified: boolean;
  archiveId: string | null;
  closeoutArchiveId: string | null;
  evidenceArchiveId: string | null;
  deliveryReceiptId: string | null;
}

function CertificateEvidence({
  title,
  status,
  archived,
  certified,
  archiveId,
  closeoutArchiveId,
  evidenceArchiveId,
  deliveryReceiptId
}: CertificateEvidenceProps) {
  return (
    <div>
      <span>{title}</span>
      <strong>{certified ? 'Certified archive' : statusLabel(status)}</strong>
      <small>{archived ? 'Archive recorded' : 'Archive missing'}</small>
      <small>{archiveId ?? 'No certificate archive'}</small>
      <small>{closeoutArchiveId ?? 'No linked closeout archive'}</small>
      <small>{evidenceArchiveId ?? 'No linked evidence archive'}</small>
      <small>{deliveryReceiptId ?? 'No linked delivery receipt'}</small>
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

function downloadMarkdown(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(url);
}
