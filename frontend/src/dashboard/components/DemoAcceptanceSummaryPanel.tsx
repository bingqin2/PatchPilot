import { Archive, Copy, Download, ExternalLink } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoAcceptanceSummary,
  DemoFinalAcceptanceCompletionArchive,
  DemoFinalAcceptanceCompletionCloseoutArchive,
  DemoFinalAcceptanceCompletionCloseout,
  DemoFinalAcceptanceCompletionEvidenceBundle,
  DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt,
  DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput,
  DemoFinalAcceptanceShareDeliveryReceipt,
  DemoFinalAcceptanceShareDeliveryReceiptInput,
  DemoFinalAcceptanceShareFinalization,
  DemoFinalAcceptanceSharePackage,
  DemoFinalAcceptanceSharePackageArchive,
  DemoReadinessStatus
} from '../../types';
import { compactDateTime } from '../format';

interface DemoAcceptanceSummaryPanelProps {
  summary: DemoAcceptanceSummary | null;
  sharePackage: DemoFinalAcceptanceSharePackage | null;
  sharePackageArchives: DemoFinalAcceptanceSharePackageArchive[];
  shareDeliveryReceipts: DemoFinalAcceptanceShareDeliveryReceipt[];
  shareFinalization: DemoFinalAcceptanceShareFinalization | null;
  completionEvidenceBundle: DemoFinalAcceptanceCompletionEvidenceBundle | null;
  completionArchives: DemoFinalAcceptanceCompletionArchive[];
  completionCloseoutArchives?: DemoFinalAcceptanceCompletionCloseoutArchive[];
  completionEvidenceDeliveryReceipts: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt[];
  completionEvidenceDeliveryFinalization?: DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization | null;
  completionCloseout?: DemoFinalAcceptanceCompletionCloseout | null;
  error: string | null;
  sharePackageError: string | null;
  sharePackageArchiveError: string | null;
  shareDeliveryReceiptError: string | null;
  shareFinalizationError: string | null;
  completionEvidenceBundleError: string | null;
  completionArchiveError: string | null;
  completionCloseoutArchiveError?: string | null;
  completionEvidenceDeliveryReceiptError: string | null;
  completionEvidenceDeliveryFinalizationError?: string | null;
  completionCloseoutError?: string | null;
  onDownloadReport: () => Promise<Blob>;
  onDownloadSharePackageReport: () => Promise<Blob>;
  onArchiveSharePackage: () => Promise<DemoFinalAcceptanceSharePackageArchive>;
  onDownloadSharePackageArchiveReport: (archiveId: string) => Promise<Blob>;
  onCreateShareDeliveryReceipt: (
    input: DemoFinalAcceptanceShareDeliveryReceiptInput
  ) => Promise<DemoFinalAcceptanceShareDeliveryReceipt>;
  onDownloadShareDeliveryReceiptReport: (receiptId: string) => Promise<Blob>;
  onDownloadShareFinalizationReport: () => Promise<Blob>;
  onDownloadCompletionEvidenceBundleReport: () => Promise<Blob>;
  onArchiveCompletion: () => Promise<DemoFinalAcceptanceCompletionArchive>;
  onDownloadCompletionArchiveReport: (archiveId: string) => Promise<Blob>;
  onArchiveCompletionCloseout?: () => Promise<DemoFinalAcceptanceCompletionCloseoutArchive>;
  onDownloadCompletionCloseoutArchiveReport?: (archiveId: string) => Promise<Blob>;
  onCreateCompletionEvidenceDeliveryReceipt: (
    input: DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptInput
  ) => Promise<DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt>;
  onDownloadCompletionEvidenceDeliveryReceiptReport: (receiptId: string) => Promise<Blob>;
  onDownloadCompletionEvidenceDeliveryFinalizationReport?: () => Promise<Blob>;
  onDownloadCompletionCloseoutReport?: () => Promise<Blob>;
}

export function DemoAcceptanceSummaryPanel({
  summary,
  sharePackage,
  sharePackageArchives,
  shareDeliveryReceipts,
  shareFinalization,
  completionEvidenceBundle,
  completionArchives,
  completionCloseoutArchives = [],
  completionEvidenceDeliveryReceipts,
  completionEvidenceDeliveryFinalization = null,
  completionCloseout = null,
  error,
  sharePackageError,
  sharePackageArchiveError,
  shareDeliveryReceiptError,
  shareFinalizationError,
  completionEvidenceBundleError,
  completionArchiveError,
  completionCloseoutArchiveError = null,
  completionEvidenceDeliveryReceiptError,
  completionEvidenceDeliveryFinalizationError = null,
  completionCloseoutError = null,
  onDownloadReport,
  onDownloadSharePackageReport,
  onArchiveSharePackage,
  onDownloadSharePackageArchiveReport,
  onCreateShareDeliveryReceipt,
  onDownloadShareDeliveryReceiptReport,
  onDownloadShareFinalizationReport,
  onDownloadCompletionEvidenceBundleReport,
  onArchiveCompletion,
  onDownloadCompletionArchiveReport,
  onArchiveCompletionCloseout,
  onDownloadCompletionCloseoutArchiveReport,
  onCreateCompletionEvidenceDeliveryReceipt,
  onDownloadCompletionEvidenceDeliveryReceiptReport,
  onDownloadCompletionEvidenceDeliveryFinalizationReport,
  onDownloadCompletionCloseoutReport
}: DemoAcceptanceSummaryPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [sharePackageCopyStatus, setSharePackageCopyStatus] = useState<string | null>(null);
  const [sharePackageDownloadStatus, setSharePackageDownloadStatus] = useState<string | null>(null);
  const [sharePackageArchiveStatus, setSharePackageArchiveStatus] = useState<string | null>(null);
  const [sharePackageArchiveDownloadStatus, setSharePackageArchiveDownloadStatus] = useState<string | null>(null);
  const [shareDeliveryReceiptStatus, setShareDeliveryReceiptStatus] = useState<string | null>(null);
  const [shareFinalizationDownloadStatus, setShareFinalizationDownloadStatus] = useState<string | null>(null);
  const [completionEvidenceBundleDownloadStatus, setCompletionEvidenceBundleDownloadStatus] = useState<string | null>(null);
  const [completionArchiveStatus, setCompletionArchiveStatus] = useState<string | null>(null);
  const [completionArchiveDownloadStatus, setCompletionArchiveDownloadStatus] = useState<string | null>(null);
  const [completionCloseoutArchiveStatus, setCompletionCloseoutArchiveStatus] = useState<string | null>(null);
  const [
    completionCloseoutArchiveDownloadStatus,
    setCompletionCloseoutArchiveDownloadStatus
  ] = useState<string | null>(null);
  const [completionEvidenceDeliveryReceiptStatus, setCompletionEvidenceDeliveryReceiptStatus] = useState<string | null>(null);
  const [
    completionEvidenceDeliveryFinalizationDownloadStatus,
    setCompletionEvidenceDeliveryFinalizationDownloadStatus
  ] = useState<string | null>(null);
  const [completionCloseoutDownloadStatus, setCompletionCloseoutDownloadStatus] = useState<string | null>(null);
  const [deliveryChannel, setDeliveryChannel] = useState('email');
  const [deliveryTarget, setDeliveryTarget] = useState('');
  const [operator, setOperator] = useState('');
  const [deliveryNotes, setDeliveryNotes] = useState('');
  const [completionEvidenceDeliveryChannel, setCompletionEvidenceDeliveryChannel] = useState('email');
  const [completionEvidenceDeliveryTarget, setCompletionEvidenceDeliveryTarget] = useState('');
  const [completionEvidenceOperator, setCompletionEvidenceOperator] = useState('');
  const [completionEvidenceDeliveryNotes, setCompletionEvidenceDeliveryNotes] = useState('');

  async function downloadReport() {
    try {
      const report = await onDownloadReport();
      downloadMarkdown(report, 'patchpilot-final-demo-acceptance-summary.md');
      setDownloadStatus('Final demo acceptance report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function copySharePackage() {
    if (!sharePackage) {
      return;
    }
    try {
      await navigator.clipboard.writeText(formatSharePackageClipboard(sharePackage));
      setSharePackageCopyStatus('Final acceptance share package copied');
    } catch {
      setSharePackageCopyStatus('Copy failed');
    }
  }

  async function downloadSharePackage() {
    try {
      const report = await onDownloadSharePackageReport();
      downloadMarkdown(report, 'patchpilot-final-demo-acceptance-share-package.md');
      setSharePackageDownloadStatus('Final acceptance share package downloaded');
    } catch {
      setSharePackageDownloadStatus('Download failed');
    }
  }

  async function archiveSharePackage() {
    try {
      await onArchiveSharePackage();
      setSharePackageArchiveStatus('Final acceptance share package archived');
    } catch {
      setSharePackageArchiveStatus('Archive failed');
    }
  }

  async function downloadSharePackageArchive(archive: DemoFinalAcceptanceSharePackageArchive) {
    try {
      const report = await onDownloadSharePackageArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-demo-acceptance-share-package-${archive.id}.md`);
      setSharePackageArchiveDownloadStatus('Archived final acceptance package downloaded');
    } catch {
      setSharePackageArchiveDownloadStatus('Archive download failed');
    }
  }

  async function createShareDeliveryReceipt() {
    try {
      await onCreateShareDeliveryReceipt({
        deliveryChannel,
        deliveryTarget: deliveryTarget.trim(),
        operator: operator.trim(),
        notes: deliveryNotes.trim()
      });
      setShareDeliveryReceiptStatus('Final acceptance delivery receipt recorded');
    } catch {
      setShareDeliveryReceiptStatus('Delivery receipt failed');
    }
  }

  async function downloadShareFinalization() {
    try {
      const report = await onDownloadShareFinalizationReport();
      downloadMarkdown(report, 'patchpilot-final-demo-acceptance-share-finalization.md');
      setShareFinalizationDownloadStatus('Final acceptance finalization report downloaded');
    } catch {
      setShareFinalizationDownloadStatus('Finalization download failed');
    }
  }

  async function downloadCompletionEvidenceBundle() {
    try {
      const report = await onDownloadCompletionEvidenceBundleReport();
      downloadMarkdown(report, 'patchpilot-final-acceptance-completion-evidence-bundle.md');
      setCompletionEvidenceBundleDownloadStatus('Final acceptance completion evidence bundle downloaded');
    } catch {
      setCompletionEvidenceBundleDownloadStatus('Final acceptance completion evidence bundle download failed');
    }
  }

  async function downloadCompletionEvidenceDeliveryFinalization() {
    if (!onDownloadCompletionEvidenceDeliveryFinalizationReport) {
      return;
    }
    try {
      const report = await onDownloadCompletionEvidenceDeliveryFinalizationReport();
      downloadMarkdown(report, 'patchpilot-final-acceptance-completion-evidence-delivery-finalization.md');
      setCompletionEvidenceDeliveryFinalizationDownloadStatus(
        'Final acceptance completion delivery finalization report downloaded'
      );
    } catch {
      setCompletionEvidenceDeliveryFinalizationDownloadStatus(
        'Final acceptance completion delivery finalization report download failed'
      );
    }
  }

  async function downloadCompletionCloseout() {
    if (!onDownloadCompletionCloseoutReport) {
      return;
    }
    try {
      const report = await onDownloadCompletionCloseoutReport();
      downloadMarkdown(report, 'patchpilot-final-acceptance-completion-closeout.md');
      setCompletionCloseoutDownloadStatus('Final acceptance completion closeout report downloaded');
    } catch {
      setCompletionCloseoutDownloadStatus('Final acceptance completion closeout report download failed');
    }
  }

  async function archiveCompletionCloseout() {
    if (!onArchiveCompletionCloseout) {
      return;
    }
    try {
      await onArchiveCompletionCloseout();
      setCompletionCloseoutArchiveStatus('Final acceptance completion closeout archived');
    } catch {
      setCompletionCloseoutArchiveStatus('Final acceptance completion closeout archive failed');
    }
  }

  async function downloadCompletionCloseoutArchive(archive: DemoFinalAcceptanceCompletionCloseoutArchive) {
    if (!onDownloadCompletionCloseoutArchiveReport) {
      return;
    }
    try {
      const report = await onDownloadCompletionCloseoutArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-acceptance-completion-closeout-${archive.id}.md`);
      setCompletionCloseoutArchiveDownloadStatus('Final acceptance completion closeout archive downloaded');
    } catch {
      setCompletionCloseoutArchiveDownloadStatus('Final acceptance completion closeout archive download failed');
    }
  }

  async function downloadShareDeliveryReceipt(receipt: DemoFinalAcceptanceShareDeliveryReceipt) {
    try {
      const report = await onDownloadShareDeliveryReceiptReport(receipt.id);
      downloadMarkdown(report, `patchpilot-final-demo-acceptance-share-delivery-receipt-${receipt.id}.md`);
      setShareDeliveryReceiptStatus(`Final acceptance delivery receipt ${receipt.id} downloaded`);
    } catch {
      setShareDeliveryReceiptStatus('Delivery receipt download failed');
    }
  }

  async function archiveCompletion() {
    try {
      await onArchiveCompletion();
      setCompletionArchiveStatus('Final acceptance completion archived');
    } catch {
      setCompletionArchiveStatus('Final acceptance completion archive failed');
    }
  }

  async function downloadCompletionArchive(archive: DemoFinalAcceptanceCompletionArchive) {
    try {
      const report = await onDownloadCompletionArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-final-acceptance-completion-${archive.id}.md`);
      setCompletionArchiveDownloadStatus('Final acceptance completion archive downloaded');
    } catch {
      setCompletionArchiveDownloadStatus('Final acceptance completion archive download failed');
    }
  }

  async function createCompletionEvidenceDeliveryReceipt() {
    try {
      await onCreateCompletionEvidenceDeliveryReceipt({
        deliveryChannel: completionEvidenceDeliveryChannel,
        deliveryTarget: completionEvidenceDeliveryTarget.trim(),
        operator: completionEvidenceOperator.trim(),
        notes: completionEvidenceDeliveryNotes.trim()
      });
      setCompletionEvidenceDeliveryReceiptStatus('Final acceptance completion evidence delivery receipt recorded');
    } catch {
      setCompletionEvidenceDeliveryReceiptStatus('Final acceptance completion evidence delivery receipt failed');
    }
  }

  async function downloadCompletionEvidenceDeliveryReceipt(
    receipt: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt
  ) {
    try {
      const report = await onDownloadCompletionEvidenceDeliveryReceiptReport(receipt.id);
      downloadMarkdown(
        report,
        `patchpilot-final-acceptance-completion-evidence-delivery-receipt-${receipt.id}.md`
      );
      setCompletionEvidenceDeliveryReceiptStatus(
        `Final acceptance completion evidence delivery receipt ${receipt.id} downloaded`
      );
    } catch {
      setCompletionEvidenceDeliveryReceiptStatus('Final acceptance completion evidence delivery receipt download failed');
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

          <FinalAcceptanceSharePackage
            sharePackage={sharePackage}
            archives={sharePackageArchives}
            deliveryReceipts={shareDeliveryReceipts}
            finalization={shareFinalization}
            completionEvidenceBundle={completionEvidenceBundle}
            completionEvidenceDeliveryFinalization={completionEvidenceDeliveryFinalization}
            completionCloseout={completionCloseout}
            completionEvidenceDeliveryReceipts={completionEvidenceDeliveryReceipts}
            error={sharePackageError}
            archiveError={sharePackageArchiveError}
            deliveryReceiptError={shareDeliveryReceiptError}
            finalizationError={shareFinalizationError}
            completionEvidenceBundleError={completionEvidenceBundleError}
            completionEvidenceDeliveryFinalizationError={completionEvidenceDeliveryFinalizationError}
            completionCloseoutError={completionCloseoutError}
            copyStatus={sharePackageCopyStatus}
            downloadStatus={sharePackageDownloadStatus}
            archiveStatus={sharePackageArchiveStatus}
            archiveDownloadStatus={sharePackageArchiveDownloadStatus}
            deliveryReceiptStatus={shareDeliveryReceiptStatus}
            finalizationDownloadStatus={shareFinalizationDownloadStatus}
            completionEvidenceBundleDownloadStatus={completionEvidenceBundleDownloadStatus}
            completionEvidenceDeliveryFinalizationDownloadStatus={
              completionEvidenceDeliveryFinalizationDownloadStatus
            }
            completionCloseoutDownloadStatus={completionCloseoutDownloadStatus}
            completionCloseoutArchives={completionCloseoutArchives}
            completionArchives={completionArchives}
            completionArchiveError={completionArchiveError}
            completionCloseoutArchiveError={completionCloseoutArchiveError}
            completionEvidenceDeliveryReceiptError={completionEvidenceDeliveryReceiptError}
            completionArchiveStatus={completionArchiveStatus}
            completionArchiveDownloadStatus={completionArchiveDownloadStatus}
            completionCloseoutArchiveStatus={completionCloseoutArchiveStatus}
            completionCloseoutArchiveDownloadStatus={completionCloseoutArchiveDownloadStatus}
            completionEvidenceDeliveryReceiptStatus={completionEvidenceDeliveryReceiptStatus}
            deliveryChannel={deliveryChannel}
            deliveryTarget={deliveryTarget}
            operator={operator}
            deliveryNotes={deliveryNotes}
            completionEvidenceDeliveryChannel={completionEvidenceDeliveryChannel}
            completionEvidenceDeliveryTarget={completionEvidenceDeliveryTarget}
            completionEvidenceOperator={completionEvidenceOperator}
            completionEvidenceDeliveryNotes={completionEvidenceDeliveryNotes}
            onCopy={() => void copySharePackage()}
            onDownload={() => void downloadSharePackage()}
            onArchive={() => void archiveSharePackage()}
            onDownloadArchive={(archive) => void downloadSharePackageArchive(archive)}
            onDeliveryChannelChange={setDeliveryChannel}
            onDeliveryTargetChange={setDeliveryTarget}
            onOperatorChange={setOperator}
            onDeliveryNotesChange={setDeliveryNotes}
            onCompletionEvidenceDeliveryChannelChange={setCompletionEvidenceDeliveryChannel}
            onCompletionEvidenceDeliveryTargetChange={setCompletionEvidenceDeliveryTarget}
            onCompletionEvidenceOperatorChange={setCompletionEvidenceOperator}
            onCompletionEvidenceDeliveryNotesChange={setCompletionEvidenceDeliveryNotes}
            onCreateDeliveryReceipt={() => void createShareDeliveryReceipt()}
            onDownloadDeliveryReceipt={(receipt) => void downloadShareDeliveryReceipt(receipt)}
            onDownloadFinalization={() => void downloadShareFinalization()}
            onDownloadCompletionEvidenceBundle={() => void downloadCompletionEvidenceBundle()}
            onDownloadCompletionEvidenceDeliveryFinalization={() => void downloadCompletionEvidenceDeliveryFinalization()}
            onDownloadCompletionCloseout={() => void downloadCompletionCloseout()}
            onArchiveCompletion={() => void archiveCompletion()}
            onDownloadCompletionArchive={(archive) => void downloadCompletionArchive(archive)}
            onArchiveCompletionCloseout={() => void archiveCompletionCloseout()}
            onDownloadCompletionCloseoutArchive={(archive) => void downloadCompletionCloseoutArchive(archive)}
            onCreateCompletionEvidenceDeliveryReceipt={() => void createCompletionEvidenceDeliveryReceipt()}
            onDownloadCompletionEvidenceDeliveryReceipt={(receipt) => void downloadCompletionEvidenceDeliveryReceipt(receipt)}
          />
        </>
      ) : (
        <div className="empty-state">Final demo acceptance summary has not loaded yet.</div>
      )}
    </section>
  );
}

interface FinalAcceptanceSharePackageProps {
  sharePackage: DemoFinalAcceptanceSharePackage | null;
  archives: DemoFinalAcceptanceSharePackageArchive[];
  deliveryReceipts: DemoFinalAcceptanceShareDeliveryReceipt[];
  finalization: DemoFinalAcceptanceShareFinalization | null;
  completionEvidenceBundle: DemoFinalAcceptanceCompletionEvidenceBundle | null;
  completionEvidenceDeliveryFinalization: DemoFinalAcceptanceCompletionEvidenceDeliveryFinalization | null;
  completionCloseout: DemoFinalAcceptanceCompletionCloseout | null;
  completionEvidenceDeliveryReceipts: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt[];
  completionArchives: DemoFinalAcceptanceCompletionArchive[];
  completionCloseoutArchives: DemoFinalAcceptanceCompletionCloseoutArchive[];
  error: string | null;
  archiveError: string | null;
  deliveryReceiptError: string | null;
  finalizationError: string | null;
  completionEvidenceBundleError: string | null;
  completionEvidenceDeliveryFinalizationError: string | null;
  completionCloseoutError: string | null;
  completionArchiveError: string | null;
  completionCloseoutArchiveError: string | null;
  completionEvidenceDeliveryReceiptError: string | null;
  copyStatus: string | null;
  downloadStatus: string | null;
  archiveStatus: string | null;
  archiveDownloadStatus: string | null;
  deliveryReceiptStatus: string | null;
  finalizationDownloadStatus: string | null;
  completionEvidenceBundleDownloadStatus: string | null;
  completionEvidenceDeliveryFinalizationDownloadStatus: string | null;
  completionCloseoutDownloadStatus: string | null;
  completionArchiveStatus: string | null;
  completionArchiveDownloadStatus: string | null;
  completionCloseoutArchiveStatus: string | null;
  completionCloseoutArchiveDownloadStatus: string | null;
  completionEvidenceDeliveryReceiptStatus: string | null;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  deliveryNotes: string;
  completionEvidenceDeliveryChannel: string;
  completionEvidenceDeliveryTarget: string;
  completionEvidenceOperator: string;
  completionEvidenceDeliveryNotes: string;
  onCopy: () => void;
  onDownload: () => void;
  onArchive: () => void;
  onDownloadArchive: (archive: DemoFinalAcceptanceSharePackageArchive) => void;
  onDeliveryChannelChange: (value: string) => void;
  onDeliveryTargetChange: (value: string) => void;
  onOperatorChange: (value: string) => void;
  onDeliveryNotesChange: (value: string) => void;
  onCompletionEvidenceDeliveryChannelChange: (value: string) => void;
  onCompletionEvidenceDeliveryTargetChange: (value: string) => void;
  onCompletionEvidenceOperatorChange: (value: string) => void;
  onCompletionEvidenceDeliveryNotesChange: (value: string) => void;
  onCreateDeliveryReceipt: () => void;
  onDownloadDeliveryReceipt: (receipt: DemoFinalAcceptanceShareDeliveryReceipt) => void;
  onDownloadFinalization: () => void;
  onDownloadCompletionEvidenceBundle: () => void;
  onDownloadCompletionEvidenceDeliveryFinalization: () => void;
  onDownloadCompletionCloseout: () => void;
  onArchiveCompletion: () => void;
  onDownloadCompletionArchive: (archive: DemoFinalAcceptanceCompletionArchive) => void;
  onArchiveCompletionCloseout: () => void;
  onDownloadCompletionCloseoutArchive: (archive: DemoFinalAcceptanceCompletionCloseoutArchive) => void;
  onCreateCompletionEvidenceDeliveryReceipt: () => void;
  onDownloadCompletionEvidenceDeliveryReceipt: (
    receipt: DemoFinalAcceptanceCompletionEvidenceDeliveryReceipt
  ) => void;
}

function FinalAcceptanceSharePackage({
  sharePackage,
  archives,
  deliveryReceipts,
  finalization,
  completionEvidenceBundle,
  completionEvidenceDeliveryFinalization,
  completionCloseout,
  completionEvidenceDeliveryReceipts,
  completionArchives,
  completionCloseoutArchives,
  error,
  archiveError,
  deliveryReceiptError,
  finalizationError,
  completionEvidenceBundleError,
  completionEvidenceDeliveryFinalizationError,
  completionCloseoutError,
  completionArchiveError,
  completionCloseoutArchiveError,
  completionEvidenceDeliveryReceiptError,
  copyStatus,
  downloadStatus,
  archiveStatus,
  archiveDownloadStatus,
  deliveryReceiptStatus,
  finalizationDownloadStatus,
  completionEvidenceBundleDownloadStatus,
  completionEvidenceDeliveryFinalizationDownloadStatus,
  completionCloseoutDownloadStatus,
  completionArchiveStatus,
  completionArchiveDownloadStatus,
  completionCloseoutArchiveStatus,
  completionCloseoutArchiveDownloadStatus,
  completionEvidenceDeliveryReceiptStatus,
  deliveryChannel,
  deliveryTarget,
  operator,
  deliveryNotes,
  completionEvidenceDeliveryChannel,
  completionEvidenceDeliveryTarget,
  completionEvidenceOperator,
  completionEvidenceDeliveryNotes,
  onCopy,
  onDownload,
  onArchive,
  onDownloadArchive,
  onDeliveryChannelChange,
  onDeliveryTargetChange,
  onOperatorChange,
  onDeliveryNotesChange,
  onCompletionEvidenceDeliveryChannelChange,
  onCompletionEvidenceDeliveryTargetChange,
  onCompletionEvidenceOperatorChange,
  onCompletionEvidenceDeliveryNotesChange,
  onCreateDeliveryReceipt,
  onDownloadDeliveryReceipt,
  onDownloadFinalization,
  onDownloadCompletionEvidenceBundle,
  onDownloadCompletionEvidenceDeliveryFinalization,
  onDownloadCompletionCloseout,
  onArchiveCompletion,
  onDownloadCompletionArchive,
  onArchiveCompletionCloseout,
  onDownloadCompletionCloseoutArchive,
  onCreateCompletionEvidenceDeliveryReceipt,
  onDownloadCompletionEvidenceDeliveryReceipt
}: FinalAcceptanceSharePackageProps) {
  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Final acceptance share package</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onCopy()}
            aria-label="Copy final acceptance share package"
            disabled={!sharePackage}
          >
            <Copy size={14} />
            Copy package
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownload()}
            aria-label="Download final acceptance share package"
          >
            <Download size={14} />
            Download package
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => onArchive()}
            aria-label="Archive final acceptance share package"
            disabled={!sharePackage}
          >
            <Archive size={14} />
            Archive package
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
          {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
          {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
          {archiveDownloadStatus ? <span className="copy-status">{archiveDownloadStatus}</span> : null}
        </div>
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Final acceptance share package unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {sharePackage ? (
        <>
          <div className="demo-session-summary">
            <div>
              <span>Send status</span>
              <strong>{sharePackage.sendReady ? 'Send-ready' : statusLabel(sharePackage.status)}</strong>
              <small>{sharePackage.summary}</small>
            </div>
            <div>
              <span>Message subject</span>
              <strong>{sharePackage.messageSubject}</strong>
              <small>Generated {compactDateTime(sharePackage.generatedAt)}</small>
            </div>
            <div>
              <span>Next action</span>
              <strong>{sharePackage.nextAction}</strong>
              <small>{sharePackage.latestPullRequestUrl ?? 'No Pull Request link'}</small>
            </div>
          </div>
          <div className="demo-session-lists compact-demo-session-lists">
            <CompactList
              title="Recommended recipients"
              items={sharePackage.recommendedRecipients}
              emptyText="No recommended recipients available."
            />
            <CompactList
              title="Required attachments"
              items={sharePackage.requiredAttachments}
              emptyText="No required attachments available."
            />
            <CompactList
              title="Pre-send checks"
              items={sharePackage.preSendChecks}
              emptyText="No pre-send checks available."
            />
          </div>
          <div className="demo-session-handoff-checks">
            <h3>Message template</h3>
            <p>{sharePackage.messageBody}</p>
            <small>{sharePackage.sideEffectContract}</small>
          </div>
          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Archived final acceptance packages</h3>
              <span>{archives.length} archives</span>
            </div>
            {archiveError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance package archives unavailable</strong>
                <span>{archiveError}</span>
              </div>
            ) : null}
            {archives.length > 0 ? (
              <ul>
                {archives.map((archive) => (
                  <li key={archive.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{archive.id}</strong>
                      <span>{archive.sendReady ? 'Send-ready' : statusLabel(archive.status)}</span>
                    </div>
                    <p>{archive.messageSubject}</p>
                    <small>Archived {compactDateTime(archive.archivedAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadArchive(archive)}
                        aria-label={`Download archived final acceptance package ${archive.id}`}
                      >
                        <Download size={14} />
                        Download archived package
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No archived final acceptance packages yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance delivery finalization</h3>
              <div className="demo-session-archive-actions">
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadFinalization()}
                  aria-label="Download final acceptance finalization report"
                >
                  <Download size={14} />
                  Download final acceptance finalization report
                </button>
                {finalizationDownloadStatus ? <span className="copy-status">{finalizationDownloadStatus}</span> : null}
              </div>
            </div>
            {finalizationError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance finalization unavailable</strong>
                <span>{finalizationError}</span>
              </div>
            ) : null}
            {finalization ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Finalization</span>
                    <strong>{finalization.finalized ? 'Finalized' : statusLabel(finalization.status)}</strong>
                    <small>{finalization.summary}</small>
                  </div>
                  <div>
                    <span>Freshness</span>
                    <strong>{finalization.deliveryReceiptFreshness}</strong>
                    <small>{finalization.deliveryReceiptFreshnessSummary}</small>
                  </div>
                  <div>
                    <span>Latest receipt</span>
                    <strong>{finalization.latestDeliveryReceiptId ?? 'No receipt'}</strong>
                    <small>{finalization.nextAction}</small>
                  </div>
                </div>
                <ul>
                  {finalization.checks.map((check) => (
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
              </>
            ) : (
              <p className="empty-state">Final acceptance delivery finalization has not loaded yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Record final acceptance delivery receipt</h3>
              {deliveryReceiptStatus ? <span className="copy-status">{deliveryReceiptStatus}</span> : null}
            </div>
            {deliveryReceiptError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance delivery receipts unavailable</strong>
                <span>{deliveryReceiptError}</span>
              </div>
            ) : null}
            <div className="manual-task-grid compact-manual-task-grid">
              <label>
                Delivery channel
                <select value={deliveryChannel} onChange={(event) => onDeliveryChannelChange(event.target.value)}>
                  <option value="email">email</option>
                  <option value="slack">slack</option>
                  <option value="github-comment">github-comment</option>
                  <option value="manual">manual</option>
                </select>
              </label>
              <label>
                Delivery target
                <input
                  value={deliveryTarget}
                  onChange={(event) => onDeliveryTargetChange(event.target.value)}
                  placeholder="reviewer@example.com"
                />
              </label>
              <label>
                Operator
                <input
                  value={operator}
                  onChange={(event) => onOperatorChange(event.target.value)}
                  placeholder="local-operator"
                />
              </label>
              <label>
                Delivery notes
                <textarea
                  value={deliveryNotes}
                  onChange={(event) => onDeliveryNotesChange(event.target.value)}
                  placeholder="Sent final acceptance share package to the reviewer."
                />
              </label>
            </div>
            <button className="primary-button" type="button" onClick={() => onCreateDeliveryReceipt()}>
              Record final acceptance delivery receipt
            </button>
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance delivery receipts</h3>
              <span>{deliveryReceipts.length} receipts</span>
            </div>
            {deliveryReceipts.length > 0 ? (
              <ul>
                {deliveryReceipts.map((receipt) => (
                  <li key={receipt.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{receipt.id}</strong>
                      <span>{receipt.deliveryChannel}</span>
                    </div>
                    <p>{receipt.messageSubject}</p>
                    <small>{receipt.deliveryTarget}</small>
                    <small>Delivered {compactDateTime(receipt.deliveredAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadDeliveryReceipt(receipt)}
                        aria-label={`Download final acceptance delivery receipt ${receipt.id}`}
                      >
                        <Download size={14} />
                        Download final acceptance delivery receipt {receipt.id}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No final acceptance delivery receipts recorded.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Archived final acceptance completions</h3>
              <div className="demo-session-archive-actions">
                <span>{completionArchives.length} archives</span>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onArchiveCompletion()}
                  aria-label="Archive final acceptance completion"
                  disabled={!finalization?.finalized}
                >
                  <Archive size={14} />
                  Archive final acceptance completion
                </button>
                {completionArchiveStatus ? <span className="copy-status">{completionArchiveStatus}</span> : null}
                {completionArchiveDownloadStatus ? (
                  <span className="copy-status">{completionArchiveDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {completionArchiveError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion archives unavailable</strong>
                <span>{completionArchiveError}</span>
              </div>
            ) : null}
            {completionArchives.length > 0 ? (
              <ul>
                {completionArchives.map((archive) => (
                  <li key={archive.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{archive.id}</strong>
                      <span>{archive.finalized ? 'Finalized' : statusLabel(archive.status)}</span>
                    </div>
                    <p>{archive.deliveryReceiptFreshnessSummary}</p>
                    <small>Receipt {archive.latestDeliveryReceiptId ?? 'missing'}</small>
                    <small>Target {archive.latestDeliveryTarget ?? 'missing'}</small>
                    <small>Freshness {archive.deliveryReceiptFreshness}</small>
                    <small>Archived {compactDateTime(archive.archivedAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadCompletionArchive(archive)}
                        aria-label={`Download final acceptance completion ${archive.id}`}
                      >
                        <Download size={14} />
                        Download final acceptance completion {archive.id}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No final acceptance completion archives yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance completion evidence bundle</h3>
              <div className="demo-session-archive-actions">
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadCompletionEvidenceBundle()}
                  aria-label="Download final acceptance completion evidence bundle"
                >
                  <Download size={14} />
                  Download completion bundle
                </button>
                {completionEvidenceBundleDownloadStatus ? (
                  <span className="copy-status">{completionEvidenceBundleDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {completionEvidenceBundleError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion evidence bundle unavailable</strong>
                <span>{completionEvidenceBundleError}</span>
              </div>
            ) : null}
            {completionEvidenceBundle ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Share status</span>
                    <strong>{completionEvidenceBundle.readyToShare ? 'Share-ready' : statusLabel(completionEvidenceBundle.status)}</strong>
                    <small>{completionEvidenceBundle.summary}</small>
                  </div>
                  <div>
                    <span>Latest completion archive</span>
                    <strong>{completionEvidenceBundle.latestCompletionArchiveId ?? 'No archive'}</strong>
                    <small>{completionEvidenceBundle.completionArchiveCount} completion archives</small>
                  </div>
                  <div>
                    <span>Latest delivery</span>
                    <strong>{completionEvidenceBundle.latestDeliveryReceiptId ?? 'No receipt'}</strong>
                    <small>{completionEvidenceBundle.latestDeliveryTarget ?? 'No delivery target'}</small>
                  </div>
                </div>
                <div className="demo-session-lists compact-demo-session-lists">
                  <CompactList
                    title="Completion evidence notes"
                    items={completionEvidenceBundle.evidenceNotes}
                    emptyText="No completion evidence notes available."
                  />
                  <CompactList
                    title="Completion download actions"
                    items={completionEvidenceBundle.downloadActions}
                    emptyText="No completion download actions available."
                  />
                </div>
                <small>{completionEvidenceBundle.sideEffectContract}</small>
              </>
            ) : (
              <p className="empty-state">Final acceptance completion evidence bundle has not loaded yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance completion delivery finalization</h3>
              <div className="demo-session-archive-actions">
                {completionEvidenceDeliveryFinalization ? (
                  <span className={`demo-readiness-status demo-readiness-status-${statusClass(completionEvidenceDeliveryFinalization.status)}`}>
                    {statusLabel(completionEvidenceDeliveryFinalization.status)}
                  </span>
                ) : null}
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadCompletionEvidenceDeliveryFinalization()}
                  aria-label="Download final acceptance completion delivery finalization report"
                >
                  <Download size={14} />
                  Download completion delivery finalization
                </button>
                {completionEvidenceDeliveryFinalizationDownloadStatus ? (
                  <span className="copy-status">{completionEvidenceDeliveryFinalizationDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {completionEvidenceDeliveryFinalizationError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion delivery finalization unavailable</strong>
                <span>{completionEvidenceDeliveryFinalizationError}</span>
              </div>
            ) : null}
            {completionEvidenceDeliveryFinalization ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Finalization</span>
                    <strong>
                      {completionEvidenceDeliveryFinalization.finalized ? 'Finalized' : statusLabel(completionEvidenceDeliveryFinalization.status)}
                    </strong>
                    <small>{completionEvidenceDeliveryFinalization.summary}</small>
                  </div>
                  <div>
                    <span>Receipt freshness</span>
                    <strong>{completionEvidenceDeliveryFinalization.deliveryReceiptFreshness}</strong>
                    <small>{completionEvidenceDeliveryFinalization.deliveryReceiptFreshnessSummary}</small>
                  </div>
                  <div>
                    <span>Completion receipt</span>
                    <strong>
                      {completionEvidenceDeliveryFinalization.latestCompletionEvidenceDeliveryReceiptId ?? 'No receipt'}
                    </strong>
                    <small>
                      {completionEvidenceDeliveryFinalization.latestDeliveryTarget ?? 'No delivery target'}
                    </small>
                  </div>
                </div>
                <div className="demo-readiness-check-list compact-readiness-list">
                  {completionEvidenceDeliveryFinalization.checks.map((check) => (
                    <div key={check.name} className="demo-readiness-check">
                      <div>
                        <strong>{check.name}</strong>
                        <p>{check.summary}</p>
                        <p className="demo-readiness-check-action">{check.nextAction}</p>
                      </div>
                      <span className={`demo-readiness-status demo-readiness-status-${statusClass(check.status)}`}>
                        {statusLabel(check.status)}
                      </span>
                    </div>
                  ))}
                </div>
                <div className="demo-session-lists compact-demo-session-lists">
                  <CompactList
                    title="Completion finalization evidence"
                    items={completionEvidenceDeliveryFinalization.evidenceNotes}
                    emptyText="No completion delivery finalization evidence available."
                  />
                  <CompactList
                    title="Completion finalization downloads"
                    items={completionEvidenceDeliveryFinalization.downloadActions}
                    emptyText="No completion delivery finalization downloads available."
                  />
                </div>
                <small>{completionEvidenceDeliveryFinalization.sideEffectContract}</small>
              </>
            ) : (
              <p className="empty-state">Final acceptance completion delivery finalization has not loaded yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance completion closeout</h3>
              <div className="demo-session-archive-actions">
                {completionCloseout ? (
                  <span className={`demo-readiness-status demo-readiness-status-${statusClass(completionCloseout.status)}`}>
                    {statusLabel(completionCloseout.status)}
                  </span>
                ) : null}
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onDownloadCompletionCloseout()}
                  aria-label="Download final acceptance completion closeout report"
                  disabled={!completionCloseout}
                >
                  <Download size={14} />
                  Download completion closeout
                </button>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onArchiveCompletionCloseout()}
                  aria-label="Archive final acceptance completion closeout"
                  disabled={!completionCloseout?.closed}
                >
                  <Archive size={14} />
                  Archive completion closeout
                </button>
                {completionCloseoutDownloadStatus ? (
                  <span className="copy-status">{completionCloseoutDownloadStatus}</span>
                ) : null}
                {completionCloseoutArchiveStatus ? (
                  <span className="copy-status">{completionCloseoutArchiveStatus}</span>
                ) : null}
                {completionCloseoutArchiveDownloadStatus ? (
                  <span className="copy-status">{completionCloseoutArchiveDownloadStatus}</span>
                ) : null}
              </div>
            </div>
            {completionCloseoutError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion closeout unavailable</strong>
                <span>{completionCloseoutError}</span>
              </div>
            ) : null}
            {completionCloseout ? (
              <>
                <div className="demo-session-summary">
                  <div>
                    <span>Closeout</span>
                    <strong>{completionCloseout.closed ? 'Closed' : statusLabel(completionCloseout.status)}</strong>
                    <small>{completionCloseout.summary}</small>
                  </div>
                  <div>
                    <span>Completion receipt</span>
                    <strong>{completionCloseout.latestCompletionEvidenceDeliveryReceiptId ?? 'No receipt'}</strong>
                    <small>{completionCloseout.deliveryReceiptFreshness}</small>
                  </div>
                  <div>
                    <span>Final review target</span>
                    <strong>{completionCloseout.latestDeliveryTarget ?? 'No delivery target'}</strong>
                    <small>{completionCloseout.nextAction}</small>
                  </div>
                </div>
                <div className="demo-readiness-check-list compact-readiness-list">
                  {completionCloseout.checks.map((check) => (
                    <div key={check.name} className="demo-readiness-check">
                      <div>
                        <strong>{check.name}</strong>
                        <p>{check.summary}</p>
                        <p className="demo-readiness-check-action">{check.nextAction}</p>
                      </div>
                      <span className={`demo-readiness-status demo-readiness-status-${statusClass(check.status)}`}>
                        {statusLabel(check.status)}
                      </span>
                    </div>
                  ))}
                </div>
                <div className="demo-session-lists compact-demo-session-lists">
                  <CompactList
                    title="Closeout evidence"
                    items={completionCloseout.evidenceNotes}
                    emptyText="No closeout evidence available."
                  />
                  <CompactList
                    title="Closeout downloads"
                    items={completionCloseout.downloadActions}
                    emptyText="No closeout downloads available."
                  />
                </div>
                <small>{completionCloseout.sideEffectContract}</small>
                <div className="demo-session-handoff-checks">
                  <div className="demo-session-archive-title-row">
                    <h3>Archived final acceptance completion closeouts</h3>
                    <span>{completionCloseoutArchives.length} archives</span>
                  </div>
                  {completionCloseoutArchiveError ? (
                    <div className="adapter-api-error">
                      <strong>Final acceptance completion closeout archives unavailable</strong>
                      <span>{completionCloseoutArchiveError}</span>
                    </div>
                  ) : null}
                  {completionCloseoutArchives.length > 0 ? (
                    <ul>
                      {completionCloseoutArchives.map((archive) => (
                        <li key={archive.id}>
                          <div className="demo-webhook-delivery-main">
                            <strong>{archive.id}</strong>
                            <span>{archive.closed ? 'Closed' : statusLabel(archive.status)}</span>
                          </div>
                          <p>{archive.summary}</p>
                          <small>Completion archive {archive.latestCompletionArchiveId ?? 'missing'}</small>
                          <small>
                            Completion receipt {archive.latestCompletionEvidenceDeliveryReceiptId ?? 'missing'}
                          </small>
                          <small>Archived {compactDateTime(archive.archivedAt)}</small>
                          <div className="demo-session-archive-actions">
                            <button
                              className="secondary-button"
                              type="button"
                              onClick={() => onDownloadCompletionCloseoutArchive(archive)}
                              aria-label={`Download final acceptance completion closeout ${archive.id}`}
                            >
                              <Download size={14} />
                              Download final acceptance completion closeout {archive.id}
                            </button>
                          </div>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="empty-state">No final acceptance completion closeout archives yet.</p>
                  )}
                </div>
              </>
            ) : (
              <p className="empty-state">Final acceptance completion closeout has not loaded yet.</p>
            )}
          </div>

          <div className="demo-session-handoff-checks">
            <div className="demo-session-archive-title-row">
              <h3>Final acceptance completion evidence delivery receipts</h3>
              <div className="demo-session-archive-actions">
                <span>{completionEvidenceDeliveryReceipts.length} receipts</span>
                {completionEvidenceDeliveryReceiptStatus ? (
                  <span className="copy-status">{completionEvidenceDeliveryReceiptStatus}</span>
                ) : null}
              </div>
            </div>
            {completionEvidenceDeliveryReceiptError ? (
              <div className="adapter-api-error">
                <strong>Final acceptance completion evidence delivery receipts unavailable</strong>
                <span>{completionEvidenceDeliveryReceiptError}</span>
              </div>
            ) : null}
            <div className="demo-evidence-receipt-form">
              <h3>Record final acceptance completion evidence delivery receipt</h3>
              <label>
                Completion evidence delivery channel
                <select
                  value={completionEvidenceDeliveryChannel}
                  onChange={(event) => onCompletionEvidenceDeliveryChannelChange(event.target.value)}
                >
                  <option value="email">email</option>
                  <option value="slack">slack</option>
                  <option value="github-comment">github-comment</option>
                  <option value="manual">manual</option>
                </select>
              </label>
              <label>
                Completion evidence delivery target
                <input
                  value={completionEvidenceDeliveryTarget}
                  onChange={(event) => onCompletionEvidenceDeliveryTargetChange(event.target.value)}
                  placeholder="reviewer@example.com"
                />
              </label>
              <label>
                Completion evidence operator
                <input
                  value={completionEvidenceOperator}
                  onChange={(event) => onCompletionEvidenceOperatorChange(event.target.value)}
                  placeholder="local-operator"
                />
              </label>
              <label>
                Completion evidence delivery notes
                <textarea
                  value={completionEvidenceDeliveryNotes}
                  onChange={(event) => onCompletionEvidenceDeliveryNotesChange(event.target.value)}
                  placeholder="Sent final completion evidence bundle to the reviewer."
                />
              </label>
              <button
                className="primary-button"
                type="button"
                onClick={() => onCreateCompletionEvidenceDeliveryReceipt()}
                disabled={!completionEvidenceBundle?.readyToShare}
              >
                Record final acceptance completion evidence delivery receipt
              </button>
            </div>
            {completionEvidenceDeliveryReceipts.length > 0 ? (
              <ul>
                {completionEvidenceDeliveryReceipts.map((receipt) => (
                  <li key={receipt.id}>
                    <div className="demo-webhook-delivery-main">
                      <strong>{receipt.id}</strong>
                      <span>{statusLabel(receipt.status)}</span>
                    </div>
                    <p>{receipt.summary}</p>
                    <small>Completion archive {receipt.latestCompletionArchiveId}</small>
                    <small>Target {receipt.deliveryTarget}</small>
                    <small>Channel {receipt.deliveryChannel}</small>
                    <small>Delivered {compactDateTime(receipt.deliveredAt)}</small>
                    <div className="demo-session-archive-actions">
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => onDownloadCompletionEvidenceDeliveryReceipt(receipt)}
                        aria-label={`Download final acceptance completion evidence delivery receipt ${receipt.id}`}
                      >
                        <Download size={14} />
                        Download final acceptance completion evidence delivery receipt {receipt.id}
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No final acceptance completion evidence delivery receipts recorded.</p>
            )}
          </div>
        </>
      ) : (
        <p className="empty-state">Final acceptance share package has not loaded yet.</p>
      )}
    </div>
  );
}

interface CompactListProps {
  title: string;
  items: string[];
  emptyText: string;
}

function CompactList({ title, items, emptyText }: CompactListProps) {
  return (
    <div>
      <h3>{title}</h3>
      {items.length > 0 ? (
        <ul>
          {items.map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      ) : (
        <p>{emptyText}</p>
      )}
    </div>
  );
}

function formatSharePackageClipboard(sharePackage: DemoFinalAcceptanceSharePackage) {
  return [
    `Subject: ${sharePackage.messageSubject}`,
    '',
    sharePackage.messageBody,
    '',
    'Required attachments:',
    ...sharePackage.requiredAttachments.map((attachment) => `- ${attachment}`),
    '',
    'Pre-send checks:',
    ...sharePackage.preSendChecks.map((check) => `- ${check}`)
  ].join('\n');
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
