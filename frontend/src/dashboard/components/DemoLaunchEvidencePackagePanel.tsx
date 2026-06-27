import { Archive, Copy, Download, Send } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import type {
  DemoLaunchEvidenceFinalization,
  DemoLaunchEvidencePackage,
  DemoLaunchEvidencePackageArchive,
  DemoLaunchEvidenceShareCenter,
  DemoLaunchEvidenceShareDeliveryReceipt,
  DemoLaunchEvidenceShareDeliveryReceiptInput,
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
  finalization: DemoLaunchEvidenceFinalization | null;
  finalizationError: string | null;
  deliveryReceipts: DemoLaunchEvidenceShareDeliveryReceipt[];
  deliveryReceiptError: string | null;
  onArchivePackage: () => Promise<DemoLaunchEvidencePackageArchive>;
  onDownloadReport: () => Promise<Blob>;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadShareCenterReport: () => Promise<Blob>;
  onDownloadFinalizationReport: () => Promise<Blob>;
  onCreateDeliveryReceipt: (input: DemoLaunchEvidenceShareDeliveryReceiptInput) => Promise<DemoLaunchEvidenceShareDeliveryReceipt>;
  onDownloadDeliveryReceiptReport: (receiptId: string) => Promise<Blob>;
}

export function DemoLaunchEvidencePackagePanel({
  evidencePackage,
  error,
  archives,
  archiveError,
  shareCenter,
  shareCenterError,
  finalization,
  finalizationError,
  deliveryReceipts,
  deliveryReceiptError,
  onArchivePackage,
  onDownloadReport,
  onDownloadArchiveReport,
  onDownloadShareCenterReport,
  onDownloadFinalizationReport,
  onCreateDeliveryReceipt,
  onDownloadDeliveryReceiptReport
}: DemoLaunchEvidencePackagePanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);
  const [receiptStatus, setReceiptStatus] = useState<string | null>(null);
  const [deliveryChannel, setDeliveryChannel] = useState('email');
  const [deliveryTarget, setDeliveryTarget] = useState('');
  const [deliveryOperator, setDeliveryOperator] = useState('');
  const [deliveryNotes, setDeliveryNotes] = useState('');

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

  async function downloadFinalizationReport() {
    try {
      const report = await onDownloadFinalizationReport();
      downloadMarkdown(report, 'patchpilot-demo-launch-evidence-finalization.md');
      setDownloadStatus('Launch evidence finalization downloaded');
    } catch {
      setDownloadStatus('Launch evidence finalization download failed');
    }
  }

  async function submitDeliveryReceipt(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      const receipt = await onCreateDeliveryReceipt({
        deliveryChannel,
        deliveryTarget,
        operator: deliveryOperator,
        notes: deliveryNotes,
        deliveredAt: new Date().toISOString()
      });
      setReceiptStatus(`Recorded launch evidence receipt ${receipt.id}`);
      setDeliveryTarget('');
      setDeliveryOperator('');
      setDeliveryNotes('');
    } catch {
      setReceiptStatus('Launch evidence receipt record failed');
    }
  }

  async function downloadDeliveryReceiptReport(receipt: DemoLaunchEvidenceShareDeliveryReceipt) {
    try {
      const report = await onDownloadDeliveryReceiptReport(receipt.id);
      downloadMarkdown(report, `patchpilot-demo-launch-evidence-delivery-receipt-${receipt.id}.md`);
      setReceiptStatus(`Launch delivery receipt ${receipt.id} downloaded`);
    } catch {
      setReceiptStatus('Launch delivery receipt download failed');
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

      {finalizationError ? (
        <div className="adapter-api-error">
          <strong>Launch evidence finalization unavailable</strong>
          <span>{finalizationError}</span>
        </div>
      ) : null}

      {deliveryReceiptError ? (
        <div className="adapter-api-error">
          <strong>Launch evidence delivery receipts unavailable</strong>
          <span>{deliveryReceiptError}</span>
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
          <LaunchEvidenceFinalizationPanel
            finalization={finalization}
            onDownloadFinalizationReport={() => void downloadFinalizationReport()}
          />
          <LaunchEvidenceDeliveryReceiptPanel
            deliveryReceipts={deliveryReceipts}
            deliveryChannel={deliveryChannel}
            deliveryTarget={deliveryTarget}
            deliveryOperator={deliveryOperator}
            deliveryNotes={deliveryNotes}
            receiptStatus={receiptStatus}
            onDeliveryChannelChange={setDeliveryChannel}
            onDeliveryTargetChange={setDeliveryTarget}
            onDeliveryOperatorChange={setDeliveryOperator}
            onDeliveryNotesChange={setDeliveryNotes}
            onSubmitDeliveryReceipt={(event) => void submitDeliveryReceipt(event)}
            onDownloadDeliveryReceipt={(receipt) => void downloadDeliveryReceiptReport(receipt)}
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
            <div>
              <span>Delivery receipt</span>
              <strong>{shareCenter.latestDeliveryReceiptId ?? 'Missing'}</strong>
              <small>{shareCenter.deliveryReceiptFreshness}: {shareCenter.deliveryReceiptFresh ? 'fresh' : 'not current'}</small>
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

function LaunchEvidenceFinalizationPanel({
  finalization,
  onDownloadFinalizationReport
}: {
  finalization: DemoLaunchEvidenceFinalization | null;
  onDownloadFinalizationReport: () => void;
}) {
  return (
    <div className="demo-evidence-actions">
      <div className="demo-evidence-list-header">
        <div>
          <h3>Launch evidence finalization</h3>
          <p>{finalization?.summary ?? 'Launch evidence finalization has not loaded yet.'}</p>
        </div>
        <button
          className="secondary-button"
          type="button"
          onClick={onDownloadFinalizationReport}
          aria-label="Download launch evidence finalization"
          disabled={!finalization}
        >
          <Download size={14} />
          Download finalization
        </button>
      </div>
      {finalization ? (
        <>
          <div className="demo-evidence-records">
            <div>
              <span>Status</span>
              <strong>{statusLabel(finalization.status)}</strong>
              <small>{finalization.finalized ? 'Finalized' : 'Not finalized'}</small>
            </div>
            <div>
              <span>Receipt</span>
              <strong>{finalization.latestDeliveryReceiptId ?? 'Missing'}</strong>
              <small>{finalization.deliveryReceiptFreshnessSummary}</small>
            </div>
            <div>
              <span>Archive</span>
              <strong>{finalization.latestArchiveId ?? 'Missing'}</strong>
              <small>{finalization.latestSessionId ?? 'No session'}</small>
            </div>
            <div>
              <span>Next action</span>
              <strong>{finalization.nextAction}</strong>
            </div>
          </div>
          <EvidenceList
            title="Finalization checks"
            items={finalization.checks.map((check) => `${check.name}: ${statusLabel(check.status)} - ${check.summary}`)}
          />
          <EvidenceList title="Finalization evidence" items={finalization.evidenceNotes} />
        </>
      ) : (
        <p>No launch evidence finalization loaded.</p>
      )}
    </div>
  );
}

function LaunchEvidenceDeliveryReceiptPanel({
  deliveryReceipts,
  deliveryChannel,
  deliveryTarget,
  deliveryOperator,
  deliveryNotes,
  receiptStatus,
  onDeliveryChannelChange,
  onDeliveryTargetChange,
  onDeliveryOperatorChange,
  onDeliveryNotesChange,
  onSubmitDeliveryReceipt,
  onDownloadDeliveryReceipt
}: {
  deliveryReceipts: DemoLaunchEvidenceShareDeliveryReceipt[];
  deliveryChannel: string;
  deliveryTarget: string;
  deliveryOperator: string;
  deliveryNotes: string;
  receiptStatus: string | null;
  onDeliveryChannelChange: (value: string) => void;
  onDeliveryTargetChange: (value: string) => void;
  onDeliveryOperatorChange: (value: string) => void;
  onDeliveryNotesChange: (value: string) => void;
  onSubmitDeliveryReceipt: (event: FormEvent<HTMLFormElement>) => void;
  onDownloadDeliveryReceipt: (receipt: DemoLaunchEvidenceShareDeliveryReceipt) => void;
}) {
  return (
    <div className="demo-evidence-actions">
      <div className="demo-evidence-list-header">
        <div>
          <h3>Launch evidence delivery receipts</h3>
          <p>{deliveryReceipts.length} recorded launch delivery receipts</p>
        </div>
        {receiptStatus ? <span className="copy-status">{receiptStatus}</span> : null}
      </div>
      <form className="demo-evidence-receipt-form" onSubmit={onSubmitDeliveryReceipt}>
        <label>
          <span>Launch delivery channel</span>
          <input
            value={deliveryChannel}
            onChange={(event) => onDeliveryChannelChange(event.target.value)}
            aria-label="Launch delivery channel"
            required
          />
        </label>
        <label>
          <span>Launch delivery target</span>
          <input
            value={deliveryTarget}
            onChange={(event) => onDeliveryTargetChange(event.target.value)}
            aria-label="Launch delivery target"
            required
          />
        </label>
        <label>
          <span>Launch delivery operator</span>
          <input
            value={deliveryOperator}
            onChange={(event) => onDeliveryOperatorChange(event.target.value)}
            aria-label="Launch delivery operator"
            required
          />
        </label>
        <label>
          <span>Launch delivery notes</span>
          <textarea
            value={deliveryNotes}
            onChange={(event) => onDeliveryNotesChange(event.target.value)}
            aria-label="Launch delivery notes"
          />
        </label>
        <button className="secondary-button" type="submit" aria-label="Record launch evidence delivery receipt">
          <Send size={14} />
          Record delivery receipt
        </button>
      </form>
      {deliveryReceipts.length === 0 ? (
        <p>No launch evidence delivery receipts recorded.</p>
      ) : (
        <ul>
          {deliveryReceipts.map((receipt) => (
            <li key={receipt.id}>
              <span>
                {receipt.id} · {receipt.deliveryTarget} · {receipt.deliveryChannel} · {compactDateTime(receipt.deliveredAt)}
              </span>
              <button
                className="secondary-button"
                type="button"
                onClick={() => onDownloadDeliveryReceipt(receipt)}
                aria-label={`Download launch delivery receipt ${receipt.id}`}
              >
                <Download size={14} />
                Download receipt
              </button>
            </li>
          ))}
        </ul>
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
