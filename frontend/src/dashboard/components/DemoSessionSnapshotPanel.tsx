import { Archive, Copy, Download } from 'lucide-react';
import { useEffect, useState, type FormEvent } from 'react';
import type {
  DemoArchivedLaunchOutcome,
  DemoHandoffReadiness,
  DemoHandoffReadinessCheck,
  DemoFinalHandoffReportPackage,
  DemoHandoffFinalization,
  DemoHandoffPackageArchive,
  DemoHandoffPackageArchiveSummary,
  DemoHandoffPackageArchiveSummaryStatus,
  DemoHandoffShareCenter,
  DemoHandoffShareDeliveryReceipt,
  DemoHandoffShareDeliveryReceiptInput,
  DemoHandoffShareInstructions,
  DemoHandoffShareChecklist,
  DemoPreparedLaunchCommand,
  DemoReadinessSnapshotTrendStatus,
  DemoReadinessStatus,
  DemoSessionArchive,
  DemoSessionReportInput,
  DemoSessionSnapshot
} from '../../types';
import { compactDateTime } from '../format';

const missingTaskEvidenceAcceptanceCertificateEvidence: DemoSessionSnapshot['evidenceBundle']['taskEvidenceAcceptanceCertificateEvidence'] = {
  status: 'NEEDS_ATTENTION',
  archived: false,
  certified: false,
  summary: 'No task evidence acceptance certificate archive is available.',
  nextAction: 'Archive a certified task evidence acceptance certificate after final task evidence closeout.',
  archiveCount: 0,
  latestArchiveId: null,
  latestCloseoutArchiveId: null,
  latestEvidenceArchiveId: null,
  latestDeliveryReceiptId: null,
  latestTaskId: null,
  latestPullRequestUrl: null,
  latestArchivedAt: null,
  downloadActions: [
    'Archive a task evidence acceptance certificate before using the session handoff as task-level review proof.'
  ]
};

interface DemoSessionSnapshotPanelProps {
  snapshot: DemoSessionSnapshot | null;
  preparedLaunchCommands: DemoPreparedLaunchCommand[];
  archivedLaunchOutcomes: DemoArchivedLaunchOutcome[];
  handoffReadiness: DemoHandoffReadiness | null;
  archives: DemoSessionArchive[];
  handoffPackageArchives: DemoHandoffPackageArchive[];
  handoffPackageArchiveSummary?: DemoHandoffPackageArchiveSummary | null;
  handoffShareChecklist?: DemoHandoffShareChecklist | null;
  handoffShareCenter?: DemoHandoffShareCenter | null;
  handoffFinalization?: DemoHandoffFinalization | null;
  finalHandoffReportPackage?: DemoFinalHandoffReportPackage | null;
  handoffShareInstructions?: DemoHandoffShareInstructions | null;
  handoffShareDeliveryReceipts?: DemoHandoffShareDeliveryReceipt[];
  error: string | null;
  handoffReadinessError?: string | null;
  archiveError: string | null;
  handoffPackageArchiveError: string | null;
  handoffPackageArchiveSummaryError?: string | null;
  handoffShareChecklistError?: string | null;
  handoffShareCenterError?: string | null;
  handoffFinalizationError?: string | null;
  finalHandoffReportPackageError?: string | null;
  handoffShareInstructionsError?: string | null;
  handoffShareDeliveryReceiptError?: string | null;
  onCopyReport: (input: DemoSessionReportInput) => Promise<string>;
  onDownloadReport: (input: DemoSessionReportInput) => Promise<Blob>;
  onArchiveSession: (input: DemoSessionReportInput) => Promise<DemoSessionArchive>;
  onCopyHandoffPackage: (input: DemoSessionReportInput) => Promise<string>;
  onDownloadHandoffPackage: (input: DemoSessionReportInput) => Promise<Blob>;
  onArchiveHandoffPackage: (input: DemoSessionReportInput) => Promise<DemoHandoffPackageArchive>;
  onDownloadArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadHandoffPackageArchiveReport: (archiveId: string) => Promise<Blob>;
  onDownloadHandoffPackageArchiveSummaryReport: () => Promise<Blob>;
  onDownloadHandoffShareCenterReport: () => Promise<Blob>;
  onDownloadHandoffFinalizationReport?: () => Promise<Blob>;
  onDownloadFinalHandoffReportPackage?: () => Promise<Blob>;
  onDownloadHandoffShareInstructionsReport: () => Promise<Blob>;
  onCreateHandoffShareDeliveryReceipt?: (
    input: DemoHandoffShareDeliveryReceiptInput
  ) => Promise<DemoHandoffShareDeliveryReceipt>;
  onDownloadHandoffShareDeliveryReceiptReport?: (receiptId: string) => Promise<Blob>;
  onDownloadHandoffShareChecklistReport: () => Promise<Blob>;
}

export function DemoSessionSnapshotPanel({
  snapshot,
  preparedLaunchCommands,
  archivedLaunchOutcomes,
  handoffReadiness,
  archives,
  handoffPackageArchives,
  handoffPackageArchiveSummary = null,
  handoffShareChecklist = null,
  handoffShareCenter = null,
  handoffFinalization = null,
  finalHandoffReportPackage = null,
  handoffShareInstructions = null,
  handoffShareDeliveryReceipts = [],
  error,
  handoffReadinessError = null,
  archiveError,
  handoffPackageArchiveError,
  handoffPackageArchiveSummaryError = null,
  handoffShareChecklistError = null,
  handoffShareCenterError = null,
  handoffFinalizationError = null,
  finalHandoffReportPackageError = null,
  handoffShareInstructionsError = null,
  handoffShareDeliveryReceiptError = null,
  onCopyReport,
  onDownloadReport,
  onArchiveSession,
  onCopyHandoffPackage,
  onDownloadHandoffPackage,
  onArchiveHandoffPackage,
  onDownloadArchiveReport,
  onDownloadHandoffPackageArchiveReport,
  onDownloadHandoffPackageArchiveSummaryReport,
  onDownloadHandoffShareCenterReport,
  onDownloadHandoffFinalizationReport = async () => {
    throw new Error('Handoff finalization download is unavailable.');
  },
  onDownloadFinalHandoffReportPackage = async () => {
    throw new Error('Final handoff report package download is unavailable.');
  },
  onDownloadHandoffShareInstructionsReport,
  onCreateHandoffShareDeliveryReceipt = async () => {
    throw new Error('Handoff share delivery receipt creation is unavailable.');
  },
  onDownloadHandoffShareDeliveryReceiptReport = async () => {
    throw new Error('Handoff share delivery receipt download is unavailable.');
  },
  onDownloadHandoffShareChecklistReport
}: DemoSessionSnapshotPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [downloadStatus, setDownloadStatus] = useState<string | null>(null);
  const [archiveStatus, setArchiveStatus] = useState<string | null>(null);
  const [receiptStatus, setReceiptStatus] = useState<string | null>(null);
  const scriptStepCount = snapshot?.script.steps.length ?? 0;
  const reportInput = { preparedLaunchCommands, archivedLaunchOutcomes };

  async function copySessionReport() {
    try {
      const report = await onCopyReport(reportInput);
      await navigator.clipboard.writeText(report);
      setCopyStatus('Demo session report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadSessionReport() {
    if (!snapshot) {
      return;
    }
    try {
      const report = await onDownloadReport(reportInput);
      downloadMarkdown(report, `patchpilot-demo-session-${snapshot.sessionId}.md`);
      setDownloadStatus('Demo session report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function copyHandoffPackage() {
    try {
      const report = await onCopyHandoffPackage(reportInput);
      await navigator.clipboard.writeText(report);
      setCopyStatus('Demo handoff package copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadHandoffPackage() {
    if (!snapshot) {
      return;
    }
    try {
      const report = await onDownloadHandoffPackage(reportInput);
      downloadMarkdown(report, `patchpilot-demo-handoff-${snapshot.sessionId}.md`);
      setDownloadStatus('Demo handoff package downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function archiveSession() {
    try {
      await onArchiveSession(reportInput);
      setArchiveStatus('Demo session archived');
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function archiveHandoffPackage() {
    try {
      await onArchiveHandoffPackage(reportInput);
      setArchiveStatus('Demo handoff package archived');
    } catch {
      setArchiveStatus('Archive failed');
    }
  }

  async function copyArchivedReport(archive: DemoSessionArchive) {
    try {
      await navigator.clipboard.writeText(archive.report);
      setCopyStatus('Archived session report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadArchivedReport(archive: DemoSessionArchive) {
    try {
      const report = await onDownloadArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-demo-session-${archive.id}.md`);
      setDownloadStatus('Archived session report downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function copyArchivedHandoffPackage(archive: DemoHandoffPackageArchive) {
    try {
      await navigator.clipboard.writeText(archive.report);
      setCopyStatus('Archived handoff package copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadArchivedHandoffPackage(archive: DemoHandoffPackageArchive) {
    try {
      const report = await onDownloadHandoffPackageArchiveReport(archive.id);
      downloadMarkdown(report, `patchpilot-demo-handoff-package-${archive.id}.md`);
      setDownloadStatus('Archived handoff package downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function copyHandoffPackageArchiveSummary() {
    if (!handoffPackageArchiveSummary) {
      return;
    }
    try {
      await navigator.clipboard.writeText(handoffPackageArchiveSummary.markdownReport);
      setCopyStatus('Handoff archive summary copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function copyHandoffShareChecklist() {
    if (!handoffShareChecklist) {
      return;
    }
    try {
      await navigator.clipboard.writeText(handoffShareChecklist.markdownReport);
      setCopyStatus('Handoff share checklist copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadHandoffShareChecklist() {
    try {
      const report = await onDownloadHandoffShareChecklistReport();
      downloadMarkdown(report, 'patchpilot-demo-handoff-share-checklist.md');
      setDownloadStatus('Handoff share checklist downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function downloadHandoffShareCenter() {
    try {
      const report = await onDownloadHandoffShareCenterReport();
      downloadMarkdown(report, 'patchpilot-demo-handoff-share-center.md');
      setDownloadStatus('Handoff share center downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function downloadHandoffFinalization() {
    try {
      const report = await onDownloadHandoffFinalizationReport();
      downloadMarkdown(report, 'patchpilot-demo-handoff-finalization.md');
      setDownloadStatus('Handoff finalization downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function downloadFinalHandoffReportPackage() {
    try {
      const report = await onDownloadFinalHandoffReportPackage();
      downloadMarkdown(report, 'patchpilot-demo-final-handoff-report-package.md');
      setDownloadStatus('Final handoff report package downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function copyHandoffShareInstructions() {
    if (!handoffShareInstructions) {
      return;
    }
    try {
      await navigator.clipboard.writeText(handoffShareInstructions.markdownReport);
      setCopyStatus('Handoff share instructions copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function downloadHandoffShareInstructions() {
    try {
      const report = await onDownloadHandoffShareInstructionsReport();
      downloadMarkdown(report, 'patchpilot-demo-handoff-share-instructions.md');
      setDownloadStatus('Handoff share instructions downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function createHandoffShareDeliveryReceipt(input: DemoHandoffShareDeliveryReceiptInput) {
    try {
      await onCreateHandoffShareDeliveryReceipt(input);
      setReceiptStatus('Handoff share delivery receipt recorded');
    } catch {
      setReceiptStatus('Receipt recording failed');
    }
  }

  async function downloadHandoffShareDeliveryReceipt(receiptId: string) {
    try {
      const report = await onDownloadHandoffShareDeliveryReceiptReport(receiptId);
      downloadMarkdown(report, `patchpilot-demo-handoff-share-delivery-receipt-${receiptId}.md`);
      setDownloadStatus('Handoff share delivery receipt downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  async function downloadHandoffPackageArchiveSummary() {
    try {
      const report = await onDownloadHandoffPackageArchiveSummaryReport();
      downloadMarkdown(report, 'patchpilot-demo-handoff-package-archive-summary.md');
      setDownloadStatus('Handoff archive summary downloaded');
    } catch {
      setDownloadStatus('Download failed');
    }
  }

  return (
    <section className="panel demo-session-panel" aria-label="Demo session snapshot">
      <div className="panel-header">
        <div>
          <h2>Demo session snapshot</h2>
          <p>{snapshot?.summary ?? 'Loading demo session snapshot'}</p>
        </div>
        {snapshot ? (
          <div className="demo-session-header-meta">
            <span className={`demo-readiness-status demo-readiness-status-${statusClass(snapshot.status)}`}>
              {statusLabel(snapshot.status)}
            </span>
            <time dateTime={snapshot.generatedAt}>{compactDateTime(snapshot.generatedAt)}</time>
            <button className="secondary-button" type="button" onClick={() => void copySessionReport()}>
              <Copy size={14} />
              Copy session report
            </button>
            <button className="secondary-button" type="button" onClick={() => void downloadSessionReport()}>
              <Download size={14} />
              Download session report
            </button>
            <button className="secondary-button" type="button" onClick={() => void copyHandoffPackage()}>
              <Copy size={14} />
              Copy handoff package
            </button>
            <button className="secondary-button" type="button" onClick={() => void downloadHandoffPackage()}>
              <Download size={14} />
              Download handoff package
            </button>
            <button className="secondary-button" type="button" onClick={() => void archiveSession()}>
              <Archive size={14} />
              Archive session
            </button>
            <button className="secondary-button" type="button" onClick={() => void archiveHandoffPackage()}>
              <Archive size={14} />
              Archive handoff package
            </button>
            {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
            {downloadStatus ? <span className="copy-status">{downloadStatus}</span> : null}
            {archiveStatus ? <span className="copy-status">{archiveStatus}</span> : null}
          </div>
        ) : null}
      </div>

      {error ? (
        <div className="adapter-api-error">
          <strong>Demo session snapshot unavailable</strong>
          <span>{error}</span>
        </div>
      ) : null}

      {archiveError ? (
        <div className="adapter-api-error">
          <strong>Demo session archive unavailable</strong>
          <span>{archiveError}</span>
        </div>
      ) : null}

      {handoffReadinessError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff readiness unavailable</strong>
          <span>{handoffReadinessError}</span>
        </div>
      ) : null}

      {handoffPackageArchiveError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff package archive unavailable</strong>
          <span>{handoffPackageArchiveError}</span>
        </div>
      ) : null}

      {handoffPackageArchiveSummaryError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff package archive summary unavailable</strong>
          <span>{handoffPackageArchiveSummaryError}</span>
        </div>
      ) : null}

      {handoffShareChecklistError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff share checklist unavailable</strong>
          <span>{handoffShareChecklistError}</span>
        </div>
      ) : null}

      {handoffShareCenterError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff share center unavailable</strong>
          <span>{handoffShareCenterError}</span>
        </div>
      ) : null}

      {handoffFinalizationError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff finalization unavailable</strong>
          <span>{handoffFinalizationError}</span>
        </div>
      ) : null}

      {finalHandoffReportPackageError ? (
        <div className="adapter-api-error">
          <strong>Demo final handoff report package unavailable</strong>
          <span>{finalHandoffReportPackageError}</span>
        </div>
      ) : null}

      {handoffShareInstructionsError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff share instructions unavailable</strong>
          <span>{handoffShareInstructionsError}</span>
        </div>
      ) : null}

      {handoffShareDeliveryReceiptError ? (
        <div className="adapter-api-error">
          <strong>Demo handoff share delivery receipts unavailable</strong>
          <span>{handoffShareDeliveryReceiptError}</span>
        </div>
      ) : null}

      {snapshot ? (
        <>
          <div className="demo-session-summary">
            <div>
              <span>Session</span>
              <strong>{snapshot.sessionId}</strong>
            </div>
            <div>
              <span>Share summary</span>
              <strong>{snapshot.shareSummary}</strong>
            </div>
          </div>

          <div className="demo-session-grid">
            <SnapshotFact
              label="Evidence status"
              value={statusLabel(snapshot.evidenceBundle.status)}
              detail={snapshot.evidenceBundle.summary}
            />
            <SnapshotFact
              label="Recent task"
              value={snapshot.evidenceBundle.recentTask?.id ?? 'None'}
              detail={snapshot.evidenceBundle.recentTask?.status ?? 'No task evidence'}
            />
            <SnapshotFact
              label="Recent Pull Request"
              value={snapshot.evidenceBundle.recentPullRequestUrl ?? 'Missing'}
              detail={snapshot.evidenceBundle.recentPullRequestUrl ? 'PR evidence available' : 'Run smoke task'}
            />
            <SnapshotFact
              label="Task evidence certificate"
              value={
                taskEvidenceCertificateEvidence(snapshot).certified
                  ? 'Certified task evidence archive'
                  : statusLabel(taskEvidenceCertificateEvidence(snapshot).status)
              }
              detail={taskEvidenceCertificateEvidence(snapshot).nextAction}
            />
            <SnapshotFact
              label="Task certificate archive"
              value={taskEvidenceCertificateEvidence(snapshot).latestArchiveId ?? 'Missing'}
              detail={taskEvidenceCertificateLinkDetail(snapshot)}
            />
            <SnapshotFact
              label="Task certificate target"
              value={taskEvidenceCertificateTaskLabel(snapshot)}
              detail={taskEvidenceCertificateEvidence(snapshot).latestPullRequestUrl ?? 'No Pull Request linked'}
            />
            <SnapshotFact
              label="Script"
              value={`${scriptStepCount} ${scriptStepCount === 1 ? 'step' : 'steps'}`}
              detail={snapshot.script.summary}
            />
            <SnapshotFact
              label="Readiness trend"
              value={trendStatusLabel(snapshot.readinessSnapshotTrend.status)}
              detail={snapshot.readinessSnapshotTrend.summary}
            />
            <SnapshotFact
              label="Trend delta"
              value={trendDelta(snapshot.readinessSnapshotTrend)}
              detail={snapshot.readinessSnapshotTrend.nextAction}
            />
            {handoffReadiness ? (
              <>
                <SnapshotFact
                  label="Handoff readiness"
                  value={statusLabel(handoffReadiness.status)}
                  detail={handoffReadiness.summary}
                />
                <SnapshotFact
                  label="Handoff evidence"
                  value={handoffEvidenceLabel(preparedLaunchCommands.length, archivedLaunchOutcomes.length)}
                  detail={handoffReadiness.nextAction}
                />
              </>
            ) : null}
          </div>

          {handoffReadiness ? <HandoffReadinessCheckList checks={handoffReadiness.checks} /> : null}

          <div className="demo-session-lists">
            <SnapshotList title="Operator checklist" items={snapshot.operatorChecklist} />
            <SnapshotList title="Health contract" items={snapshot.healthContract} />
            <SnapshotList title="Next actions" items={snapshot.nextActions} emptyText="No next actions recorded." />
          </div>

          <PreparedLaunchCommandList commands={preparedLaunchCommands} />
          <ArchivedLaunchOutcomeList outcomes={archivedLaunchOutcomes} />

          <div className="demo-session-archives">
            <h3>Recent session archives</h3>
            {archives.length ? (
              <ul>
                {archives.map((archive) => (
                  <li key={archive.id}>
                    <div>
                      <strong>{archive.id}</strong>
                      <span>{archive.sessionId}</span>
                      <small>{archive.shareSummary}</small>
                    </div>
                    <div className="demo-session-archive-actions">
                      <time dateTime={archive.createdAt}>{compactDateTime(archive.createdAt)}</time>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void copyArchivedReport(archive)}
                        aria-label={`Copy archived session report ${archive.id}`}
                      >
                        <Copy size={14} />
                        Copy report
                      </button>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void downloadArchivedReport(archive)}
                        aria-label={`Download archived session report ${archive.id}`}
                      >
                        <Download size={14} />
                        Download report
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No demo session archives recorded.</p>
            )}
          </div>

          <HandoffPackageArchiveSummaryPanel
            summary={handoffPackageArchiveSummary}
            onCopySummary={copyHandoffPackageArchiveSummary}
            onDownloadSummary={downloadHandoffPackageArchiveSummary}
          />

          <HandoffShareCenterPanel
            center={handoffShareCenter}
            onDownloadCenter={downloadHandoffShareCenter}
          />

          <HandoffFinalizationPanel
            finalization={handoffFinalization}
            onDownloadFinalization={downloadHandoffFinalization}
          />

          <FinalHandoffReportPackagePanel
            reportPackage={finalHandoffReportPackage}
            onDownloadPackage={downloadFinalHandoffReportPackage}
          />

          <HandoffShareInstructionsPanel
            instructions={handoffShareInstructions}
            onCopyInstructions={copyHandoffShareInstructions}
            onDownloadInstructions={downloadHandoffShareInstructions}
          />

          <HandoffShareDeliveryReceiptPanel
            receipts={handoffShareDeliveryReceipts}
            instructions={handoffShareInstructions}
            receiptStatus={receiptStatus}
            onCreateReceipt={createHandoffShareDeliveryReceipt}
            onDownloadReceipt={downloadHandoffShareDeliveryReceipt}
          />

          <HandoffShareChecklistPanel
            checklist={handoffShareChecklist}
            onCopyChecklist={copyHandoffShareChecklist}
            onDownloadChecklist={downloadHandoffShareChecklist}
          />

          <div className="demo-session-archives">
            <h3>Recent handoff package archives</h3>
            {handoffPackageArchives.length ? (
              <ul>
                {handoffPackageArchives.map((archive) => (
                  <li key={archive.id}>
                    <div>
                      <strong>{archive.id}</strong>
                      <span>{archive.sessionId}</span>
                      <small>{archive.shareSummary}</small>
                      <small>Handoff readiness: {statusLabel(archive.handoffReadinessStatus)}</small>
                      <small>
                        {archive.handoffReadyCheckCount} ready / {archive.handoffNeedsAttentionCheckCount} warning /{' '}
                        {archive.handoffBlockedCheckCount} blocked
                      </small>
                      <small>{archive.handoffReadinessNextAction}</small>
                    </div>
                    <div className="demo-session-archive-actions">
                      <time dateTime={archive.createdAt}>{compactDateTime(archive.createdAt)}</time>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void copyArchivedHandoffPackage(archive)}
                        aria-label={`Copy archived handoff package ${archive.id}`}
                      >
                        <Copy size={14} />
                        Copy package
                      </button>
                      <button
                        className="secondary-button"
                        type="button"
                        onClick={() => void downloadArchivedHandoffPackage(archive)}
                        aria-label={`Download archived handoff package ${archive.id}`}
                      >
                        <Download size={14} />
                        Download package
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No demo handoff package archives recorded.</p>
            )}
          </div>
        </>
      ) : (
        <div className="empty-state">Demo session snapshot has not loaded yet.</div>
      )}
    </section>
  );
}

function HandoffReadinessCheckList({ checks }: { checks: DemoHandoffReadinessCheck[] }) {
  return (
    <div className="demo-session-handoff-checks">
      <h3>Handoff readiness checks</h3>
      {checks.length ? (
        <ul>
          {checks.map((check) => (
            <li key={check.name}>
              <div>
                <strong>{check.name}</strong>
                <small>{check.summary}</small>
                <small>Next action: {check.nextAction}</small>
              </div>
              <span className={`demo-readiness-status demo-readiness-status-${statusClass(check.status)}`}>
                {statusLabel(check.status)}
              </span>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No handoff readiness checks recorded.</p>
      )}
    </div>
  );
}

function HandoffShareCenterPanel({
  center,
  onDownloadCenter
}: {
  center: DemoHandoffShareCenter | null;
  onDownloadCenter: () => void;
}) {
  if (!center) {
    return null;
  }

  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Handoff share center</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownloadCenter()}
            aria-label="Download handoff share center"
          >
            <Download size={14} />
            Download center
          </button>
        </div>
      </div>
      <div className="demo-session-summary">
        <div>
          <span>Share status</span>
          <strong>{center.shareReady ? 'Share-ready' : statusLabel(center.status)}</strong>
          <small>{center.summary}</small>
        </div>
        <div>
          <span>Latest package</span>
          <strong>{center.latestArchiveId ?? 'No archive'}</strong>
          <small>{center.latestSessionId ?? 'Archive a handoff package first'}</small>
        </div>
        <div>
          <span>Task certificate gate</span>
          <strong>{center.taskCertificateReady ? 'Certificate-ready' : statusLabel(center.taskCertificateStatus ?? 'NEEDS_ATTENTION')}</strong>
          <small>{center.taskCertificateSummary ?? 'Task evidence acceptance certificate evidence is unavailable.'}</small>
          <small>{center.taskCertificateArchiveId ?? 'No certificate archive'}</small>
          <small>{center.taskCertificateTaskId ? `Task ${center.taskCertificateTaskId}` : 'No task linked'}</small>
          {center.taskCertificatePullRequestUrl ? <small>{center.taskCertificatePullRequestUrl}</small> : null}
        </div>
        <div>
          <span>Latest delivery</span>
          <strong>{deliveryFreshnessLabel(center.deliveryReceiptFreshness)}</strong>
          <small>{center.deliveryReceiptFreshnessSummary}</small>
          <small>{center.deliveryReceiptRecorded ? center.latestDeliveryReceiptId : 'No receipt'}</small>
          <small>
            {center.deliveryReceiptRecorded
              ? `${center.latestDeliveryChannel} - ${center.latestDeliveryTarget}`
              : 'Record a delivery receipt after sending the handoff package.'}
          </small>
          {center.latestDeliveredAt ? <small>Delivered {compactDateTime(center.latestDeliveredAt)}</small> : null}
        </div>
        <div>
          <span>Next action</span>
          <strong>{center.nextAction}</strong>
          <small>Generated {compactDateTime(center.generatedAt)}</small>
        </div>
      </div>
      <div className="demo-session-lists compact-demo-session-lists">
        <SnapshotList
          title="Share downloads"
          items={center.downloadActions}
          emptyText="No share downloads available."
        />
        <SnapshotList
          title="Share evidence"
          items={center.evidenceNotes}
          emptyText="No share evidence available."
        />
      </div>
    </div>
  );
}

function HandoffFinalizationPanel({
  finalization,
  onDownloadFinalization
}: {
  finalization: DemoHandoffFinalization | null;
  onDownloadFinalization: () => void;
}) {
  if (!finalization) {
    return null;
  }

  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Handoff finalization gate</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownloadFinalization()}
            aria-label="Download handoff finalization"
          >
            <Download size={14} />
            Download finalization
          </button>
        </div>
      </div>
      <div className="demo-session-summary">
        <div>
          <span>Finalization status</span>
          <strong>{finalization.finalized ? 'Finalized' : statusLabel(finalization.status)}</strong>
          <small>{finalization.summary}</small>
        </div>
        <div>
          <span>Accepted package</span>
          <strong>{finalization.latestArchiveId ?? 'No archive'}</strong>
          <small>{finalization.latestSessionId ?? 'Archive and share a handoff package first'}</small>
        </div>
        <div>
          <span>Delivery receipt</span>
          <strong>{deliveryFreshnessLabel(finalization.deliveryReceiptFreshness)}</strong>
          <small>{finalization.deliveryReceiptFreshnessSummary}</small>
          <small>{finalization.latestDeliveryReceiptId ?? 'No receipt'}</small>
          {finalization.latestDeliveredAt ? (
            <small>Delivered {compactDateTime(finalization.latestDeliveredAt)}</small>
          ) : null}
        </div>
        <div>
          <span>Next action</span>
          <strong>{finalization.nextAction}</strong>
          <small>Generated {compactDateTime(finalization.generatedAt)}</small>
        </div>
      </div>
      <div className="demo-session-handoff-checks">
        <ul>
          {finalization.checks.map((check) => (
            <li key={check.name}>
              <div>
                <strong>{check.name}</strong>
                <small>{check.summary}</small>
                <small>Next action: {check.nextAction}</small>
              </div>
              <span className={`demo-readiness-status demo-readiness-status-${statusClass(check.status)}`}>
                {statusLabel(check.status)}
              </span>
            </li>
          ))}
        </ul>
      </div>
      <div className="demo-session-lists compact-demo-session-lists">
        <SnapshotList
          title="Finalization evidence"
          items={finalization.evidenceNotes}
          emptyText="No finalization evidence available."
        />
      </div>
    </div>
  );
}

function FinalHandoffReportPackagePanel({
  reportPackage,
  onDownloadPackage
}: {
  reportPackage: DemoFinalHandoffReportPackage | null;
  onDownloadPackage: () => void;
}) {
  if (!reportPackage) {
    return null;
  }

  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Final handoff report package</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownloadPackage()}
            aria-label="Download final handoff report package"
          >
            <Download size={14} />
            Download package
          </button>
        </div>
      </div>
      <div className="demo-session-summary">
        <div>
          <span>Package status</span>
          <strong>{reportPackage.downloadReady ? 'Download-ready' : statusLabel(reportPackage.status)}</strong>
          <small>{reportPackage.summary}</small>
        </div>
        <div>
          <span>Handoff archive</span>
          <strong>{reportPackage.latestArchiveId ?? 'No archive'}</strong>
          <small>{reportPackage.latestSessionId ?? 'Archive a handoff package first'}</small>
        </div>
        <div>
          <span>Task certificate</span>
          <strong>{reportPackage.taskCertificateReady ? 'Certificate-ready' : 'Certificate missing'}</strong>
          <small>{reportPackage.taskCertificateArchiveId ?? 'No certificate archive'}</small>
        </div>
        <div>
          <span>Delivery receipt</span>
          <strong>{reportPackage.latestDeliveryReceiptId ?? 'No receipt'}</strong>
          <small>Generated {compactDateTime(reportPackage.generatedAt)}</small>
        </div>
        <div>
          <span>Next action</span>
          <strong>{reportPackage.nextAction}</strong>
          <small>Use this as the final operator-facing handoff bundle.</small>
        </div>
      </div>
      <div className="demo-session-lists compact-demo-session-lists">
        <SnapshotList
          title="Final package checks"
          items={reportPackage.readinessChecks}
          emptyText="No final package checks available."
        />
        <SnapshotList
          title="Final package attachments"
          items={reportPackage.requiredAttachments}
          emptyText="No final package attachments available."
        />
        <SnapshotList
          title="Final package pre-send checks"
          items={reportPackage.preSendChecks}
          emptyText="No final package pre-send checks available."
        />
        <SnapshotList
          title="Final package evidence"
          items={reportPackage.evidenceNotes}
          emptyText="No final package evidence available."
        />
        <SnapshotList
          title="Final package source reports"
          items={reportPackage.sourceReports}
          emptyText="No source reports available."
        />
      </div>
    </div>
  );
}

function HandoffShareInstructionsPanel({
  instructions,
  onCopyInstructions,
  onDownloadInstructions
}: {
  instructions: DemoHandoffShareInstructions | null;
  onCopyInstructions: () => void;
  onDownloadInstructions: () => void;
}) {
  if (!instructions) {
    return null;
  }

  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Handoff share instructions</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onCopyInstructions()}
            aria-label="Copy handoff share instructions"
          >
            <Copy size={14} />
            Copy instructions
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownloadInstructions()}
            aria-label="Download handoff share instructions"
          >
            <Download size={14} />
            Download instructions
          </button>
        </div>
      </div>
      <div className="demo-session-summary">
        <div>
          <span>Send status</span>
          <strong>{instructions.sendReady ? 'Send-ready' : statusLabel(instructions.status)}</strong>
          <small>{instructions.summary}</small>
        </div>
        <div>
          <span>Message subject</span>
          <strong>{instructions.messageSubject}</strong>
          <small>Generated {compactDateTime(instructions.generatedAt)}</small>
        </div>
        <div>
          <span>Next action</span>
          <strong>{instructions.nextAction}</strong>
          <small>Copy or download these instructions before sharing.</small>
        </div>
      </div>
      <div className="demo-session-lists compact-demo-session-lists">
        <SnapshotList
          title="Recommended recipients"
          items={instructions.recommendedRecipients}
          emptyText="No recommended recipients available."
        />
        <SnapshotList
          title="Required attachments"
          items={instructions.requiredAttachments}
          emptyText="No required attachments available."
        />
        <SnapshotList
          title="Pre-send checks"
          items={instructions.preSendChecks}
          emptyText="No pre-send checks available."
        />
      </div>
      <div className="demo-session-handoff-checks">
        <h3>Message template</h3>
        <p>{instructions.messageBody}</p>
      </div>
    </div>
  );
}

function HandoffShareDeliveryReceiptPanel({
  receipts,
  instructions,
  receiptStatus,
  onCreateReceipt,
  onDownloadReceipt
}: {
  receipts: DemoHandoffShareDeliveryReceipt[];
  instructions: DemoHandoffShareInstructions | null;
  receiptStatus: string | null;
  onCreateReceipt: (input: DemoHandoffShareDeliveryReceiptInput) => void | Promise<void>;
  onDownloadReceipt: (receiptId: string) => void | Promise<void>;
}) {
  const [deliveryChannel, setDeliveryChannel] = useState('email');
  const [deliveryTarget, setDeliveryTarget] = useState(instructions?.recommendedRecipients[0] ?? '');
  const [deliveryTargetTouched, setDeliveryTargetTouched] = useState(false);
  const [operator, setOperator] = useState('local-operator');
  const [notes, setNotes] = useState('Sent after the demo review.');

  useEffect(() => {
    if (!deliveryTargetTouched && !deliveryTarget && instructions?.recommendedRecipients[0]) {
      setDeliveryTarget(instructions.recommendedRecipients[0]);
    }
  }, [deliveryTarget, deliveryTargetTouched, instructions]);

  async function submitReceipt(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onCreateReceipt({
      deliveryChannel: deliveryChannel.trim(),
      deliveryTarget: deliveryTarget.trim(),
      operator: operator.trim(),
      notes: notes.trim(),
      deliveredAt: new Date().toISOString()
    });
  }

  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Handoff share delivery receipts</h3>
        {receiptStatus ? <span className="manual-task-status">{receiptStatus}</span> : null}
      </div>
      <form className="manual-task-form demo-receipt-form" onSubmit={(event) => void submitReceipt(event)}>
        <label htmlFor="handoff-share-delivery-channel">
          Delivery channel
          <input
            id="handoff-share-delivery-channel"
            value={deliveryChannel}
            onChange={(event) => setDeliveryChannel(event.target.value)}
            required
          />
        </label>
        <label htmlFor="handoff-share-delivery-target">
          Delivery target
          <input
            id="handoff-share-delivery-target"
            value={deliveryTarget}
            onChange={(event) => {
              setDeliveryTargetTouched(true);
              setDeliveryTarget(event.target.value);
            }}
            required
          />
        </label>
        <label htmlFor="handoff-share-delivery-operator">
          Operator
          <input
            id="handoff-share-delivery-operator"
            value={operator}
            onChange={(event) => setOperator(event.target.value)}
            required
          />
        </label>
        <label htmlFor="handoff-share-delivery-notes">
          Notes
          <input
            id="handoff-share-delivery-notes"
            value={notes}
            onChange={(event) => setNotes(event.target.value)}
            required
          />
        </label>
        <button
          className="secondary-button"
          type="submit"
          disabled={!instructions?.sendReady}
          aria-label="Record handoff share delivery receipt"
        >
          <Archive size={14} />
          Record receipt
        </button>
      </form>
      {receipts.length ? (
        <ul className="demo-session-archive-list">
          {receipts.map((receipt) => (
            <li key={receipt.id}>
              <div>
                <strong>{receipt.id}</strong>
                <span>
                  {receipt.deliveryChannel} - {receipt.deliveryTarget}
                </span>
                <small>{receipt.messageSubject}</small>
                <time dateTime={receipt.deliveredAt}>{compactDateTime(receipt.deliveredAt)}</time>
              </div>
              <button
                className="secondary-button"
                type="button"
                onClick={() => void onDownloadReceipt(receipt.id)}
                aria-label={`Download handoff share delivery receipt ${receipt.id}`}
              >
                <Download size={14} />
                Download receipt
              </button>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No handoff share delivery receipts recorded.</p>
      )}
    </div>
  );
}

function HandoffShareChecklistPanel({
  checklist,
  onCopyChecklist,
  onDownloadChecklist
}: {
  checklist: DemoHandoffShareChecklist | null;
  onCopyChecklist: () => void;
  onDownloadChecklist: () => void;
}) {
  if (!checklist) {
    return null;
  }

  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Handoff share checklist</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onCopyChecklist()}
            aria-label="Copy handoff share checklist"
          >
            <Copy size={14} />
            Copy checklist
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownloadChecklist()}
            aria-label="Download handoff share checklist"
          >
            <Download size={14} />
            Download checklist
          </button>
        </div>
      </div>
      <div className="demo-session-summary">
        <div>
          <span>Share status</span>
          <strong>{statusLabel(checklist.status)}</strong>
          <small>{checklist.summary}</small>
        </div>
        <div>
          <span>Checklist evidence</span>
          <strong>
            {checklist.checks.length} {plural(checklist.checks.length, 'check')}
          </strong>
          <small>Generated {compactDateTime(checklist.generatedAt)}</small>
        </div>
        <div>
          <span>Next action</span>
          <strong>{checklist.nextAction}</strong>
          <small>Copy this checklist before sharing the handoff package.</small>
        </div>
      </div>
      <div className="demo-session-handoff-checks">
        <ul>
          {checklist.checks.map((check) => (
            <li key={check.name}>
              <div>
                <strong>{check.name}</strong>
                <small>{check.summary}</small>
                <small>Next action: {check.nextAction}</small>
              </div>
              <span className={`demo-readiness-status demo-readiness-status-${statusClass(check.status)}`}>
                {statusLabel(check.status)}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function HandoffPackageArchiveSummaryPanel({
  summary,
  onCopySummary,
  onDownloadSummary
}: {
  summary: DemoHandoffPackageArchiveSummary | null;
  onCopySummary: () => void;
  onDownloadSummary: () => void;
}) {
  if (!summary) {
    return null;
  }

  return (
    <div className="demo-session-archives">
      <div className="demo-session-archive-title-row">
        <h3>Handoff package archive summary</h3>
        <div className="demo-session-archive-actions">
          <button
            className="secondary-button"
            type="button"
            onClick={() => onCopySummary()}
            aria-label="Copy handoff archive summary"
          >
            <Copy size={14} />
            Copy summary
          </button>
          <button
            className="secondary-button"
            type="button"
            onClick={() => onDownloadSummary()}
            aria-label="Download handoff archive summary"
          >
            <Download size={14} />
            Download summary
          </button>
        </div>
      </div>
      <div className="demo-session-summary">
        <div>
          <span>Archive status</span>
          <strong>{summary.shareReady ? 'Share-ready' : archiveSummaryStatusLabel(summary.status)}</strong>
          <small>{summary.summary}</small>
        </div>
        <div>
          <span>Archive evidence</span>
          <strong>
            {summary.archiveCount} {plural(summary.archiveCount, 'archived package')}
          </strong>
          <small>
            {summary.latestArchiveId ? `Latest archive: ${summary.latestArchiveId}` : 'Latest archive: none'}
          </small>
        </div>
        <div>
          <span>Next action</span>
          <strong>{summary.nextAction}</strong>
          <small>
            {summary.latestCreatedAt ? `Captured ${compactDateTime(summary.latestCreatedAt)}` : 'Capture a handoff package first'}
          </small>
        </div>
      </div>
    </div>
  );
}

function ArchivedLaunchOutcomeList({ outcomes }: { outcomes: DemoArchivedLaunchOutcome[] }) {
  return (
    <div className="demo-session-prepared-commands">
      <h3>Archived launch outcomes</h3>
      {outcomes.length ? (
        <ul>
          {outcomes.map((outcome) => (
            <li key={`${outcome.triggerComment}-${outcome.archivedAt}`}>
              <code>{outcome.triggerComment}</code>
              <span>
                {outcome.repositoryOwner}/{outcome.repositoryName} #{outcome.issueNumber} - {outcome.taskStatus}
              </span>
              {outcome.pullRequestUrl ? <small>{outcome.pullRequestUrl}</small> : null}
              <time dateTime={outcome.archivedAt}>{compactDateTime(outcome.archivedAt)}</time>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No archived launch outcomes recorded in this browser.</p>
      )}
    </div>
  );
}

interface SnapshotFactProps {
  label: string;
  value: string;
  detail: string;
}

function SnapshotFact({ label, value, detail }: SnapshotFactProps) {
  return (
    <div className="demo-session-fact">
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{detail}</small>
    </div>
  );
}

interface SnapshotListProps {
  title: string;
  items: string[];
  emptyText?: string;
}

function SnapshotList({ title, items, emptyText = 'No entries recorded.' }: SnapshotListProps) {
  return (
    <div>
      <h3>{title}</h3>
      {items.length ? (
        <ul>
          {items.map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      ) : (
        <p className="empty-state">{emptyText}</p>
      )}
    </div>
  );
}

function PreparedLaunchCommandList({ commands }: { commands: DemoPreparedLaunchCommand[] }) {
  return (
    <div className="demo-session-prepared-commands">
      <h3>Prepared launch commands</h3>
      {commands.length ? (
        <ul>
          {commands.map((command) => (
            <li key={`${command.triggerComment}-${command.savedAt}`}>
              <code>{command.triggerComment}</code>
              <span>
                {command.repositoryOwner}/{command.repositoryName} #{command.issueNumber} - {command.operation} -{' '}
                {command.targetPath}
              </span>
              {command.replacementText ? <small>{command.replacementText}</small> : null}
              <time dateTime={command.savedAt}>{compactDateTime(command.savedAt)}</time>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No prepared launch commands recorded in this browser.</p>
      )}
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

function deliveryFreshnessLabel(freshness: string) {
  switch (freshness) {
    case 'FRESH':
      return 'Fresh';
    case 'STALE':
      return 'Stale';
    case 'MISSING':
      return 'Missing';
    default:
      return freshness;
  }
}

function statusClass(status: DemoReadinessStatus) {
  return status.toLowerCase().replace('_', '-');
}

function archiveSummaryStatusLabel(status: DemoHandoffPackageArchiveSummaryStatus) {
  if (status === 'NO_ARCHIVE') {
    return 'No archive';
  }
  return statusLabel(status);
}

function trendStatusLabel(status: DemoReadinessSnapshotTrendStatus) {
  switch (status) {
    case 'NO_BASELINE':
      return 'No baseline';
    case 'IMPROVING':
      return 'Improving';
    case 'STABLE':
      return 'Stable';
    case 'REGRESSING':
      return 'Regressing';
  }
}

function trendDelta(snapshotTrend: DemoSessionSnapshot['readinessSnapshotTrend']) {
  return `${signed(snapshotTrend.readyCheckDelta)} ready / ${signed(snapshotTrend.needsAttentionCheckDelta)} warning / ${signed(
    snapshotTrend.blockedCheckDelta
  )} blocked`;
}

function handoffEvidenceLabel(commandCount: number, outcomeCount: number) {
  return `${commandCount} ${plural(commandCount, 'command')} / ${outcomeCount} ${plural(outcomeCount, 'outcome')}`;
}

function taskEvidenceCertificateEvidence(snapshot: DemoSessionSnapshot) {
  return (
    snapshot.evidenceBundle.taskEvidenceAcceptanceCertificateEvidence ??
    missingTaskEvidenceAcceptanceCertificateEvidence
  );
}

function taskEvidenceCertificateTaskLabel(snapshot: DemoSessionSnapshot) {
  const evidence = taskEvidenceCertificateEvidence(snapshot);
  return evidence.latestTaskId ? `Task ${evidence.latestTaskId}` : 'No task linked';
}

function taskEvidenceCertificateLinkDetail(snapshot: DemoSessionSnapshot) {
  const evidence = taskEvidenceCertificateEvidence(snapshot);
  return evidence.latestCloseoutArchiveId ?? evidence.summary;
}

function plural(count: number, noun: string) {
  return count === 1 ? noun : `${noun}s`;
}

function signed(value: number) {
  return value > 0 ? `+${value}` : `${value}`;
}

function downloadMarkdown(blob: Blob, filename: string) {
  const objectUrl = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = objectUrl;
  anchor.download = safeFilename(filename);
  document.body.append(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(objectUrl);
}

function safeFilename(filename: string) {
  return filename.replace(/[^a-z0-9._-]+/gi, '-');
}
