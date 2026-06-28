import { Archive, Copy, Download, ExternalLink } from 'lucide-react';
import { useState } from 'react';
import type {
  DemoAcceptanceSummary,
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
  error: string | null;
  sharePackageError: string | null;
  sharePackageArchiveError: string | null;
  shareDeliveryReceiptError: string | null;
  shareFinalizationError: string | null;
  onDownloadReport: () => Promise<Blob>;
  onDownloadSharePackageReport: () => Promise<Blob>;
  onArchiveSharePackage: () => Promise<DemoFinalAcceptanceSharePackageArchive>;
  onDownloadSharePackageArchiveReport: (archiveId: string) => Promise<Blob>;
  onCreateShareDeliveryReceipt: (
    input: DemoFinalAcceptanceShareDeliveryReceiptInput
  ) => Promise<DemoFinalAcceptanceShareDeliveryReceipt>;
  onDownloadShareDeliveryReceiptReport: (receiptId: string) => Promise<Blob>;
  onDownloadShareFinalizationReport: () => Promise<Blob>;
}

export function DemoAcceptanceSummaryPanel({
  summary,
  sharePackage,
  sharePackageArchives,
  shareDeliveryReceipts,
  shareFinalization,
  error,
  sharePackageError,
  sharePackageArchiveError,
  shareDeliveryReceiptError,
  shareFinalizationError,
  onDownloadReport,
  onDownloadSharePackageReport,
  onArchiveSharePackage,
  onDownloadSharePackageArchiveReport,
  onCreateShareDeliveryReceipt,
  onDownloadShareDeliveryReceiptReport,
  onDownloadShareFinalizationReport
}: DemoAcceptanceSummaryPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [sharePackageCopyStatus, setSharePackageCopyStatus] = useState<string | null>(null);
  const [sharePackageDownloadStatus, setSharePackageDownloadStatus] = useState<string | null>(null);
  const [sharePackageArchiveStatus, setSharePackageArchiveStatus] = useState<string | null>(null);
  const [sharePackageArchiveDownloadStatus, setSharePackageArchiveDownloadStatus] = useState<string | null>(null);
  const [shareDeliveryReceiptStatus, setShareDeliveryReceiptStatus] = useState<string | null>(null);
  const [shareFinalizationDownloadStatus, setShareFinalizationDownloadStatus] = useState<string | null>(null);
  const [deliveryChannel, setDeliveryChannel] = useState('email');
  const [deliveryTarget, setDeliveryTarget] = useState('');
  const [operator, setOperator] = useState('');
  const [deliveryNotes, setDeliveryNotes] = useState('');

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

  async function downloadShareDeliveryReceipt(receipt: DemoFinalAcceptanceShareDeliveryReceipt) {
    try {
      const report = await onDownloadShareDeliveryReceiptReport(receipt.id);
      downloadMarkdown(report, `patchpilot-final-demo-acceptance-share-delivery-receipt-${receipt.id}.md`);
      setShareDeliveryReceiptStatus(`Final acceptance delivery receipt ${receipt.id} downloaded`);
    } catch {
      setShareDeliveryReceiptStatus('Delivery receipt download failed');
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
            error={sharePackageError}
            archiveError={sharePackageArchiveError}
            deliveryReceiptError={shareDeliveryReceiptError}
            finalizationError={shareFinalizationError}
            copyStatus={sharePackageCopyStatus}
            downloadStatus={sharePackageDownloadStatus}
            archiveStatus={sharePackageArchiveStatus}
            archiveDownloadStatus={sharePackageArchiveDownloadStatus}
            deliveryReceiptStatus={shareDeliveryReceiptStatus}
            finalizationDownloadStatus={shareFinalizationDownloadStatus}
            deliveryChannel={deliveryChannel}
            deliveryTarget={deliveryTarget}
            operator={operator}
            deliveryNotes={deliveryNotes}
            onCopy={() => void copySharePackage()}
            onDownload={() => void downloadSharePackage()}
            onArchive={() => void archiveSharePackage()}
            onDownloadArchive={(archive) => void downloadSharePackageArchive(archive)}
            onDeliveryChannelChange={setDeliveryChannel}
            onDeliveryTargetChange={setDeliveryTarget}
            onOperatorChange={setOperator}
            onDeliveryNotesChange={setDeliveryNotes}
            onCreateDeliveryReceipt={() => void createShareDeliveryReceipt()}
            onDownloadDeliveryReceipt={(receipt) => void downloadShareDeliveryReceipt(receipt)}
            onDownloadFinalization={() => void downloadShareFinalization()}
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
  error: string | null;
  archiveError: string | null;
  deliveryReceiptError: string | null;
  finalizationError: string | null;
  copyStatus: string | null;
  downloadStatus: string | null;
  archiveStatus: string | null;
  archiveDownloadStatus: string | null;
  deliveryReceiptStatus: string | null;
  finalizationDownloadStatus: string | null;
  deliveryChannel: string;
  deliveryTarget: string;
  operator: string;
  deliveryNotes: string;
  onCopy: () => void;
  onDownload: () => void;
  onArchive: () => void;
  onDownloadArchive: (archive: DemoFinalAcceptanceSharePackageArchive) => void;
  onDeliveryChannelChange: (value: string) => void;
  onDeliveryTargetChange: (value: string) => void;
  onOperatorChange: (value: string) => void;
  onDeliveryNotesChange: (value: string) => void;
  onCreateDeliveryReceipt: () => void;
  onDownloadDeliveryReceipt: (receipt: DemoFinalAcceptanceShareDeliveryReceipt) => void;
  onDownloadFinalization: () => void;
}

function FinalAcceptanceSharePackage({
  sharePackage,
  archives,
  deliveryReceipts,
  finalization,
  error,
  archiveError,
  deliveryReceiptError,
  finalizationError,
  copyStatus,
  downloadStatus,
  archiveStatus,
  archiveDownloadStatus,
  deliveryReceiptStatus,
  finalizationDownloadStatus,
  deliveryChannel,
  deliveryTarget,
  operator,
  deliveryNotes,
  onCopy,
  onDownload,
  onArchive,
  onDownloadArchive,
  onDeliveryChannelChange,
  onDeliveryTargetChange,
  onOperatorChange,
  onDeliveryNotesChange,
  onCreateDeliveryReceipt,
  onDownloadDeliveryReceipt,
  onDownloadFinalization
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
