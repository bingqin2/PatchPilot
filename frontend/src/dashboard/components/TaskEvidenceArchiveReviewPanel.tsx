import { Download, ExternalLink, FileText, Send } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import type {
  FixTaskEvidencePackageArchive,
  FixTaskEvidencePackageAcceptanceCertificate,
  FixTaskEvidencePackageAcceptanceCertificateArchive,
  FixTaskEvidencePackageAcceptanceCloseoutArchive,
  FixTaskEvidencePackageFinalization,
  FixTaskEvidencePackageArchiveShareCenter,
  FixTaskEvidencePackageArchiveSummary,
  FixTaskEvidencePackageShareDeliveryReceipt,
  FixTaskEvidencePackageShareDeliveryReceiptInput
} from '../../types';
import { compactTime } from '../format';

interface TaskEvidenceArchiveReviewPanelProps {
  summary: FixTaskEvidencePackageArchiveSummary | null;
  shareCenter: FixTaskEvidencePackageArchiveShareCenter | null;
  finalization: FixTaskEvidencePackageFinalization | null;
  deliveryReceipts: FixTaskEvidencePackageShareDeliveryReceipt[];
  closeoutArchives: FixTaskEvidencePackageAcceptanceCloseoutArchive[];
  certificate: FixTaskEvidencePackageAcceptanceCertificate | null;
  certificateArchives: FixTaskEvidencePackageAcceptanceCertificateArchive[];
  archives: FixTaskEvidencePackageArchive[];
  error: string | null;
  shareCenterError: string | null;
  finalizationError: string | null;
  deliveryReceiptError: string | null;
  closeoutArchiveError: string | null;
  certificateError: string | null;
  certificateArchiveError: string | null;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadShareCenterReport: () => Promise<Blob>;
  onDownloadFinalizationReport: () => Promise<Blob>;
  onCreateDeliveryReceipt: (input: FixTaskEvidencePackageShareDeliveryReceiptInput) => Promise<FixTaskEvidencePackageShareDeliveryReceipt>;
  onDownloadDeliveryReceiptReport: (receiptId: string) => Promise<Blob>;
  onArchiveAcceptanceCloseout: () => Promise<FixTaskEvidencePackageAcceptanceCloseoutArchive>;
  onDownloadAcceptanceCloseoutArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadAcceptanceCertificateReport: () => Promise<Blob>;
  onArchiveAcceptanceCertificate: () => Promise<FixTaskEvidencePackageAcceptanceCertificateArchive>;
  onDownloadAcceptanceCertificateArchiveReport: (archiveId: string) => Promise<Blob>;
  onSelectTask: (taskId: string) => void;
}

export function TaskEvidenceArchiveReviewPanel({
  summary,
  shareCenter,
  finalization,
  deliveryReceipts,
  closeoutArchives,
  certificate,
  certificateArchives,
  archives,
  error,
  shareCenterError,
  finalizationError,
  deliveryReceiptError,
  closeoutArchiveError,
  certificateError,
  certificateArchiveError,
  onDownloadArchiveReport,
  onDownloadShareCenterReport,
  onDownloadFinalizationReport,
  onCreateDeliveryReceipt,
  onDownloadDeliveryReceiptReport,
  onArchiveAcceptanceCloseout,
  onDownloadAcceptanceCloseoutArchiveReport,
  onDownloadAcceptanceCertificateReport,
  onArchiveAcceptanceCertificate,
  onDownloadAcceptanceCertificateArchiveReport,
  onSelectTask
}: TaskEvidenceArchiveReviewPanelProps) {
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [deliveryChannel, setDeliveryChannel] = useState('email');
  const [deliveryTarget, setDeliveryTarget] = useState('');
  const [deliveryOperator, setDeliveryOperator] = useState('local-operator');
  const [deliveryNotes, setDeliveryNotes] = useState('');
  const [receiptStatus, setReceiptStatus] = useState<string | null>(null);
  const [closeoutStatus, setCloseoutStatus] = useState<string | null>(null);
  const [certificateStatus, setCertificateStatus] = useState<string | null>(null);

  async function downloadArchive(archive: FixTaskEvidencePackageArchive) {
    try {
      const report = await onDownloadArchiveReport(archive.id);
      downloadMarkdown(
        report,
        `patchpilot-task-${safeFilenamePart(archive.taskId)}-evidence-${safeFilenamePart(archive.id)}.md`
      );
      setDownloadStatus(`Archived evidence ${archive.id} downloaded`);
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function downloadShareCenterReport() {
    try {
      const report = await onDownloadShareCenterReport();
      downloadMarkdown(report, 'patchpilot-task-evidence-share-center.md');
      setDownloadStatus('Task evidence share center report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function downloadFinalizationReport() {
    try {
      const report = await onDownloadFinalizationReport();
      downloadMarkdown(report, 'patchpilot-task-evidence-finalization.md');
      setDownloadStatus('Task evidence finalization report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function submitDeliveryReceipt(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      const receipt = await onCreateDeliveryReceipt({
        deliveryChannel,
        deliveryTarget,
        operator: deliveryOperator,
        notes: deliveryNotes
      });
      setReceiptStatus(`Task evidence delivery receipt ${receipt.id} recorded`);
      setDeliveryNotes('');
    } catch {
      setReceiptStatus('Task evidence delivery receipt failed');
    }
  }

  async function downloadDeliveryReceipt(receipt: FixTaskEvidencePackageShareDeliveryReceipt) {
    try {
      const report = await onDownloadDeliveryReceiptReport(receipt.id);
      downloadMarkdown(report, `patchpilot-task-evidence-delivery-receipt-${safeFilenamePart(receipt.id)}.md`);
      setDownloadStatus(`Task evidence delivery receipt ${receipt.id} downloaded`);
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function archiveAcceptanceCloseout() {
    try {
      const archive = await onArchiveAcceptanceCloseout();
      setCloseoutStatus(`Task evidence acceptance closeout ${archive.id} archived`);
    } catch {
      setCloseoutStatus('Task evidence acceptance closeout archive failed');
    }
  }

  async function downloadAcceptanceCloseoutArchive(archive: FixTaskEvidencePackageAcceptanceCloseoutArchive) {
    try {
      const report = await onDownloadAcceptanceCloseoutArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-task-evidence-acceptance-closeout-${safeFilenamePart(archive.id)}.md`);
      setDownloadStatus(`Task evidence acceptance closeout ${archive.id} downloaded`);
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function downloadAcceptanceCertificateReport() {
    try {
      const report = await onDownloadAcceptanceCertificateReport();
      downloadMarkdown(report, 'patchpilot-task-evidence-acceptance-certificate.md');
      setDownloadStatus('Task evidence acceptance certificate downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function archiveAcceptanceCertificate() {
    try {
      const archive = await onArchiveAcceptanceCertificate();
      setCertificateStatus(`Task evidence acceptance certificate ${archive.id} archived`);
    } catch {
      setCertificateStatus('Task evidence acceptance certificate archive failed');
    }
  }

  async function downloadAcceptanceCertificateArchive(
    archive: FixTaskEvidencePackageAcceptanceCertificateArchive
  ) {
    try {
      const report = await onDownloadAcceptanceCertificateArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-task-evidence-acceptance-certificate-${safeFilenamePart(archive.id)}.md`);
      setDownloadStatus(`Task evidence acceptance certificate ${archive.id} downloaded`);
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  return (
    <section className="panel task-evidence-archive-review-panel" aria-label="Task evidence archive review">
      <div className="panel-header">
        <div>
          <h2>Task evidence archive review</h2>
          <p>{summary?.totalArchiveCount ?? archives.length} archived reports</p>
        </div>
        {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
      </div>

      {error ? <p className="panel-error">{error}</p> : null}
      {shareCenterError ? <p className="panel-error">{shareCenterError}</p> : null}
      {finalizationError ? <p className="panel-error">{finalizationError}</p> : null}
      {deliveryReceiptError ? <p className="panel-error">{deliveryReceiptError}</p> : null}
      {closeoutArchiveError ? <p className="panel-error">{closeoutArchiveError}</p> : null}
      {certificateError ? <p className="panel-error">{certificateError}</p> : null}
      {certificateArchiveError ? <p className="panel-error">{certificateArchiveError}</p> : null}

      {shareCenter ? (
        <section
          className={`task-evidence-share-center task-evidence-share-center-${shareCenter.status.toLowerCase().replace('_', '-')}`}
          aria-label="Task evidence share center"
        >
          <div className="task-evidence-share-center-heading">
            <div>
              <span>Task evidence share center</span>
              <strong>{formatShareCenterStatus(shareCenter.status)}</strong>
              <p>{shareCenter.summary}</p>
            </div>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadShareCenterReport()}
              aria-label="Download task evidence share center report"
            >
              <FileText size={14} />
              Download share center report
            </button>
          </div>
          <div className="task-evidence-share-center-grid">
            <div>
              <span>Shareable archive</span>
              <strong>{shareCenter.shareableArchiveId ?? 'None'}</strong>
              <p>{shareCenter.shareableTaskId ? `Task ${shareCenter.shareableTaskId}` : 'Archive a completed PR task'}</p>
            </div>
            <div>
              <span>Pull Request</span>
              <strong>{shareCenter.shareablePullRequestUrl ?? 'None'}</strong>
              <p>{shareCenter.shareReady ? 'Ready to share' : 'Missing completed PR evidence'}</p>
            </div>
            <div>
              <span>Latest archive</span>
              <strong>{shareCenter.latestArchiveId ?? 'None'}</strong>
              <p>{shareCenter.latestArchivedAt ? `archived ${compactTime(shareCenter.latestArchivedAt)}` : 'No archive yet'}</p>
            </div>
          </div>
          <div className="task-evidence-share-center-notes">
            <div>
              <span>Download actions</span>
              <ul>
                {shareCenter.downloadActions.map((action) => (
                  <li key={action}>{action}</li>
                ))}
              </ul>
            </div>
            <div>
              <span>Evidence notes</span>
              <ul>
                {shareCenter.evidenceNotes.map((note) => (
                  <li key={note}>{note}</li>
                ))}
              </ul>
            </div>
          </div>
          <p className="task-evidence-share-center-contract">{shareCenter.sideEffectContract}</p>
          <strong>{shareCenter.nextAction}</strong>
        </section>
      ) : null}

      {finalization ? (
        <section
          className={`task-evidence-share-center task-evidence-share-center-${finalization.status.toLowerCase().replace('_', '-')}`}
          aria-label="Task evidence finalization"
        >
          <div className="task-evidence-share-center-heading">
            <div>
              <span>Task evidence finalization</span>
              <strong>{formatFinalizationStatus(finalization.status)}</strong>
              <p>{finalization.summary}</p>
            </div>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadFinalizationReport()}
              aria-label="Download task evidence finalization report"
            >
              <FileText size={14} />
              Download finalization report
            </button>
          </div>
          <div className="task-evidence-share-center-grid">
            <div>
              <span>Finalized</span>
              <strong>{finalization.finalized ? 'Yes' : 'No'}</strong>
              <p>{finalization.nextAction}</p>
            </div>
            <div>
              <span>Delivery receipt</span>
              <strong>{finalization.latestDeliveryReceiptId ?? 'Missing'}</strong>
              <p>{finalization.deliveryReceiptFreshness}: {finalization.deliveryReceiptFreshnessSummary}</p>
            </div>
            <div>
              <span>Shareable archive</span>
              <strong>{finalization.latestArchiveId ?? 'None'}</strong>
              <p>{finalization.latestTaskId ? `Task ${finalization.latestTaskId}` : 'No current task evidence'}</p>
            </div>
          </div>
          <div className="task-evidence-share-center-notes">
            <div>
              <span>Finalization checks</span>
              <ul>
                {finalization.checks.map((check) => (
                  <li key={check.name}>{check.name}: {formatFinalizationStatus(check.status)} - {check.summary}</li>
                ))}
              </ul>
            </div>
            <div>
              <span>Evidence notes</span>
              <ul>
                {finalization.evidenceNotes.map((note) => (
                  <li key={note}>{note}</li>
                ))}
              </ul>
            </div>
          </div>
        </section>
      ) : null}

      <section className="task-evidence-share-center" aria-label="Task evidence acceptance closeout archives">
        <div className="task-evidence-share-center-heading">
          <div>
            <span>Task evidence acceptance closeout archives</span>
            <strong>{closeoutArchives.length}</strong>
            <p>local final acceptance records for delivered task evidence</p>
          </div>
          <button
            className="secondary-button"
            type="button"
            onClick={() => void archiveAcceptanceCloseout()}
            aria-label="Archive task evidence acceptance closeout"
            disabled={!finalization?.finalized}
          >
            <FileText size={14} />
            Archive closeout
          </button>
        </div>
        {closeoutStatus ? <span className="copy-status">{closeoutStatus}</span> : null}
        {closeoutArchives.length === 0 ? (
          <p className="empty-state compact-empty-state">No task evidence acceptance closeouts archived.</p>
        ) : (
          <div className="task-evidence-archive-list">
            {closeoutArchives.map((archive) => (
              <article className="task-evidence-archive-row" key={archive.id}>
                <div>
                  <strong>{archive.id}</strong>
                  <p>
                    {archive.accepted ? 'accepted' : formatFinalizationStatus(archive.status)}
                    {' '}· {archive.latestArchiveId ?? 'no archive'} · {archive.latestDeliveryReceiptId ?? 'no receipt'}
                  </p>
                  <span>
                    {archive.latestTaskId ?? 'No task'} · {archive.deliveryReceiptFreshness} · archived{' '}
                    {compactTime(archive.createdAt)}
                  </span>
                </div>
                <div className="task-evidence-archive-actions">
                  <button
                    className="secondary-button"
                    type="button"
                    onClick={() => void downloadAcceptanceCloseoutArchive(archive)}
                    aria-label={`Download task evidence acceptance closeout ${archive.id}`}
                  >
                    <Download size={14} />
                    Download closeout
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      <section
        className={`task-evidence-share-center task-evidence-share-center-${(certificate?.status ?? 'NEEDS_ATTENTION').toLowerCase().replace('_', '-')}`}
        aria-label="Task evidence acceptance certificate"
      >
        <div className="task-evidence-share-center-heading">
          <div>
            <span>Task evidence acceptance certificate</span>
            <strong>{certificate?.certified ? 'Certified' : formatFinalizationStatus(certificate?.status ?? 'NEEDS_ATTENTION')}</strong>
            <p>{certificate?.summary ?? 'No task evidence acceptance certificate is available yet.'}</p>
          </div>
          <div className="task-evidence-archive-actions">
            <button
              className="secondary-button"
              type="button"
              onClick={() => void downloadAcceptanceCertificateReport()}
              aria-label="Download task evidence acceptance certificate"
              disabled={!certificate}
            >
              <FileText size={14} />
              Download certificate
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => void archiveAcceptanceCertificate()}
              aria-label="Archive task evidence acceptance certificate"
              disabled={!certificate?.certified}
            >
              <FileText size={14} />
              Archive certificate
            </button>
          </div>
        </div>
        {certificateStatus ? <span className="copy-status">{certificateStatus}</span> : null}
        {certificate ? (
          <>
            <div className="task-evidence-share-center-grid">
              <div>
                <span>Certificate source</span>
                <strong>{certificate.latestCloseoutArchiveId ?? 'None'}</strong>
                <p>{certificate.nextAction}</p>
              </div>
              <div>
                <span>Task evidence</span>
                <strong>{certificate.latestEvidenceArchiveId ?? 'None'}</strong>
                <p>{certificate.latestTaskId ? `Task ${certificate.latestTaskId}` : 'No task evidence archive'}</p>
              </div>
              <div>
                <span>Delivery receipt</span>
                <strong>{certificate.latestDeliveryReceiptId ?? 'Missing'}</strong>
                <p>{certificate.deliveryReceiptFreshness}</p>
              </div>
            </div>
            <div className="task-evidence-share-center-notes">
              <div>
                <span>Download actions</span>
                <ul>
                  {certificate.downloadActions.map((action) => (
                    <li key={action}>{action}</li>
                  ))}
                </ul>
              </div>
            </div>
          </>
        ) : (
          <p className="empty-state compact-empty-state">Archive an accepted closeout before generating the certificate.</p>
        )}
        {certificateArchives.length === 0 ? (
          <p className="empty-state compact-empty-state">No task evidence acceptance certificates archived.</p>
        ) : (
          <div className="task-evidence-archive-list">
            {certificateArchives.map((archive) => (
              <article className="task-evidence-archive-row" key={archive.id}>
                <div>
                  <strong>{archive.id}</strong>
                  <p>
                    {archive.certified ? 'certified' : formatFinalizationStatus(archive.status)}
                    {' '}· {archive.latestCloseoutArchiveId ?? 'no closeout'} · {archive.latestDeliveryReceiptId ?? 'no receipt'}
                  </p>
                  <span>
                    {archive.latestTaskId ?? 'No task'} · {archive.deliveryReceiptFreshness} · archived{' '}
                    {compactTime(archive.archivedAt)}
                  </span>
                </div>
                <div className="task-evidence-archive-actions">
                  <button
                    className="secondary-button"
                    type="button"
                    onClick={() => void downloadAcceptanceCertificateArchive(archive)}
                    aria-label={`Download task evidence acceptance certificate archive ${archive.id}`}
                  >
                    <Download size={14} />
                    Download certificate
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="task-evidence-share-center" aria-label="Task evidence delivery receipts">
        <div className="task-evidence-share-center-heading">
          <div>
            <span>Task evidence delivery receipts</span>
            <strong>{deliveryReceipts.length}</strong>
            <p>local delivery records for the current task evidence share flow</p>
          </div>
          {receiptStatus ? <span className="copy-status">{receiptStatus}</span> : null}
        </div>
        <form className="demo-evidence-receipt-form" onSubmit={(event) => void submitDeliveryReceipt(event)}>
          <label>
            <span>Delivery channel</span>
            <input
              value={deliveryChannel}
              onChange={(event) => setDeliveryChannel(event.target.value)}
              aria-label="Task evidence delivery channel"
              required
            />
          </label>
          <label>
            <span>Delivery target</span>
            <input
              value={deliveryTarget}
              onChange={(event) => setDeliveryTarget(event.target.value)}
              aria-label="Task evidence delivery target"
              required
            />
          </label>
          <label>
            <span>Delivery operator</span>
            <input
              value={deliveryOperator}
              onChange={(event) => setDeliveryOperator(event.target.value)}
              aria-label="Task evidence delivery operator"
              required
            />
          </label>
          <label>
            <span>Delivery notes</span>
            <textarea
              value={deliveryNotes}
              onChange={(event) => setDeliveryNotes(event.target.value)}
              aria-label="Task evidence delivery notes"
            />
          </label>
          <button
            className="secondary-button"
            type="submit"
            aria-label="Record task evidence delivery receipt"
            disabled={!shareCenter?.shareReady}
          >
            <Send size={14} />
            Record delivery receipt
          </button>
        </form>
        {deliveryReceipts.length === 0 ? (
          <p className="empty-state compact-empty-state">No task evidence delivery receipts recorded.</p>
        ) : (
          <div className="task-evidence-archive-list">
            {deliveryReceipts.map((receipt) => (
              <article className="task-evidence-archive-row" key={receipt.id}>
                <div>
                  <strong>{receipt.id}</strong>
                  <p>{receipt.deliveryTarget} · {receipt.deliveryChannel} · delivered {compactTime(receipt.deliveredAt)}</p>
                  <span>
                    {receipt.repositoryOwner}/{receipt.repositoryName} #{receipt.issueNumber} · {receipt.taskEvidenceArchiveId}
                  </span>
                </div>
                <div className="task-evidence-archive-actions">
                  <button
                    className="secondary-button"
                    type="button"
                    onClick={() => void downloadDeliveryReceipt(receipt)}
                    aria-label={`Download task evidence delivery receipt ${receipt.id}`}
                  >
                    <Download size={14} />
                    Download receipt
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      <div className="task-evidence-archive-summary">
        <div>
          <span>Total</span>
          <strong>{summary?.totalArchiveCount ?? archives.length}</strong>
          <p>archived reports</p>
        </div>
        <div>
          <span>Completed</span>
          <strong>{summary?.completedArchiveCount ?? countStatus(archives, 'COMPLETED')}</strong>
          <p>completed</p>
        </div>
        <div>
          <span>Failed</span>
          <strong>{summary?.failedArchiveCount ?? countStatus(archives, 'FAILED')}</strong>
          <p>failed</p>
        </div>
        <div>
          <span>Latest task</span>
          <strong>{latestTaskLabel(summary)}</strong>
          <p>{summary?.latestArchivedAt ? `archived ${compactTime(summary.latestArchivedAt)}` : 'No archive yet'}</p>
        </div>
      </div>

      {summary ? (
        <div className="task-evidence-archive-contract">
          <p>{summary.sideEffectContract}</p>
          <strong>{summary.nextAction}</strong>
        </div>
      ) : null}

      {archives.length > 0 ? (
        <div className="task-evidence-archive-list">
          {archives.map((archive) => (
            <article className="task-evidence-archive-row" key={archive.id}>
              <div>
                <strong>{archive.id}</strong>
                <p>{archive.summary}</p>
                <span>
                  {archive.repositoryOwner}/{archive.repositoryName} #{archive.issueNumber} · {archive.status} · archived{' '}
                  {compactTime(archive.archivedAt)}
                </span>
              </div>
              <div className="task-evidence-archive-actions">
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => onSelectTask(archive.taskId)}
                  aria-label={`Open task ${archive.taskId}`}
                >
                  <ExternalLink size={14} />
                  Open task
                </button>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => void downloadArchive(archive)}
                  aria-label={`Download archived evidence ${archive.id}`}
                >
                  <Download size={14} />
                  Download
                </button>
              </div>
            </article>
          ))}
        </div>
      ) : (
        <p className="empty-state">No archived task evidence packages.</p>
      )}
    </section>
  );
}

function latestTaskLabel(summary: FixTaskEvidencePackageArchiveSummary | null) {
  if (!summary?.latestRepositoryOwner || !summary.latestRepositoryName || summary.latestIssueNumber == null) {
    return 'None';
  }
  return `${summary.latestRepositoryOwner}/${summary.latestRepositoryName} #${summary.latestIssueNumber}`;
}

function countStatus(archives: FixTaskEvidencePackageArchive[], status: string) {
  return archives.filter((archive) => archive.status === status).length;
}

function formatShareCenterStatus(status: FixTaskEvidencePackageArchiveShareCenter['status']) {
  return formatFinalizationStatus(status);
}

function formatFinalizationStatus(status: string) {
  return status
    .split('_')
    .map((part) => part.charAt(0) + part.slice(1).toLowerCase())
    .join(' ');
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

function safeFilenamePart(value: string) {
  return value.replace(/[^A-Za-z0-9._-]/g, '-');
}
